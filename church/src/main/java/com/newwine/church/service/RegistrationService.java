package com.newwine.church.service;

import com.newwine.church.dto.request.RegistrationRequest;
import com.newwine.church.dto.response.RegistrationResponse;
import com.newwine.church.entity.Registration;
import com.newwine.church.exception.ResourceNotFoundException;
import com.newwine.church.exception.ValidationException;
import com.newwine.church.respository.RegistrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventService eventService;

    @Transactional
    public RegistrationResponse registerForEvent(RegistrationRequest request) {
        logger.info("Processing registration for event: {} by email: {}", request.getEventName(), request.getEmail());

        // Validate input
        validateRegistrationRequest(request);

        // Check if event exists and is active
        if (!eventService.eventExists(request.getEventName())) {
            throw new ValidationException("Event '" + request.getEventName() + "' does not exist or is not available for registration");
        }

        // Check if user is already registered for this event
        if (isUserRegisteredForEvent(request.getEmail(), request.getEventName())) {
            throw new ValidationException("You are already registered for this event");
        }

        // Create registration entity
        Registration registration = new Registration();
        registration.setFullName(sanitizeInput(request.getFullName()));
        registration.setEmail(sanitizeInput(request.getEmail().toLowerCase()));
        registration.setPhone(sanitizeInput(request.getPhone()));
        registration.setEventName(sanitizeInput(request.getEventName()));
        registration.setSpecialRequests(sanitizeInput(request.getSpecialRequests()));
        registration.setRegistrationDate(request.getRegistrationDate() != null ? request.getRegistrationDate() : LocalDateTime.now());

        Registration savedRegistration = registrationRepository.save(registration);
        
        logger.info("Registration completed successfully with ID: {} for event: {} by email: {}", 
                   savedRegistration.getId(), request.getEventName(), request.getEmail());

        return mapToResponse(savedRegistration);
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getAllRegistrations() {
        logger.info("Fetching all registrations");
        List<Registration> registrations = registrationRepository.findAllByOrderByRegistrationDateDesc();
        return registrations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getRegistrationsByEvent(String eventName) {
        logger.info("Fetching registrations for event: {}", eventName);
        List<Registration> registrations = registrationRepository.findByEventNameOrderByRegistrationDateDesc(eventName);
        return registrations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getRegistrationsByEmail(String email) {
        logger.info("Fetching registrations for email: {}", email);
        List<Registration> registrations = registrationRepository.findByEmailOrderByRegistrationDateDesc(email.toLowerCase());
        return registrations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RegistrationResponse getRegistrationById(Long id) {
        logger.info("Fetching registration with ID: {}", id);
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + id));
        return mapToResponse(registration);
    }

    @Transactional(readOnly = true)
    public boolean isUserRegisteredForEvent(String email, String eventName) {
        return registrationRepository.existsByEmailAndEventName(email.toLowerCase(), eventName);
    }

    @Transactional(readOnly = true)
    public Long getRegistrationCount() {
        return registrationRepository.count();
    }

    @Transactional(readOnly = true)
    public Long getRegistrationCountByEvent(String eventName) {
        return registrationRepository.countByEventName(eventName);
    }

    @Transactional(readOnly = true)
    public Long getRecentRegistrationCount(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return registrationRepository.countByRegistrationDateAfter(since);
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getRecentRegistrations(int days) {
        logger.info("Fetching recent registrations from last {} days", days);
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Registration> registrations = registrationRepository.findByRegistrationDateAfterOrderByRegistrationDateDesc(since);
        return registrations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> searchRegistrations(String searchTerm) {
        logger.info("Searching registrations with term: {}", searchTerm);
        List<Registration> registrations = registrationRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByRegistrationDateDesc(
                searchTerm, searchTerm);
        return registrations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getEventStatistics() {
        logger.info("Fetching event registration statistics");
        List<Object[]> stats = registrationRepository.findEventStatistics();
        return stats.stream()
                .collect(Collectors.toMap(
                    row -> (String) row[0],
                    row -> (Long) row[1]
                ));
    }

    @Transactional
    public void deleteRegistration(Long id) {
        logger.info("Deleting registration with ID: {}", id);
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + id));
        
        registrationRepository.delete(registration);
        logger.info("Registration deleted successfully with ID: {}", id);
    }

    @Transactional
    public void deleteRegistrationsByEmail(String email) {
        logger.info("Deleting registrations for email: {}", email);
        List<Registration> registrations = registrationRepository.findByEmailOrderByRegistrationDateDesc(email.toLowerCase());
        if (!registrations.isEmpty()) {
            registrationRepository.deleteAll(registrations);
            logger.info("Deleted {} registrations for email: {}", registrations.size(), email);
        }
    }

    @Transactional
    public void deleteRegistrationsByEvent(String eventName) {
        logger.info("Deleting registrations for event: {}", eventName);
        List<Registration> registrations = registrationRepository.findByEventNameOrderByRegistrationDateDesc(eventName);
        if (!registrations.isEmpty()) {
            registrationRepository.deleteAll(registrations);
            logger.info("Deleted {} registrations for event: {}", registrations.size(), eventName);
        }
    }

    @Transactional
    public boolean cancelRegistration(String email, String eventName) {
        logger.info("Cancelling registration for email: {} and event: {}", email, eventName);
        
        List<Registration> registrations = registrationRepository.findByEmailAndEventName(email.toLowerCase(), eventName);
        if (!registrations.isEmpty()) {
            registrationRepository.deleteAll(registrations);
            logger.info("Cancelled {} registrations for email: {} and event: {}", registrations.size(), email, eventName);
            return true;
        }
        
        return false;
    }

    private void validateRegistrationRequest(RegistrationRequest request) {
        if (!StringUtils.hasText(request.getFullName())) {
            throw new ValidationException("Full name is required");
        }
        if (!StringUtils.hasText(request.getEmail())) {
            throw new ValidationException("Email is required");
        }
        if (!StringUtils.hasText(request.getEventName())) {
            throw new ValidationException("Event name is required");
        }
        if (!isValidEmail(request.getEmail())) {
            throw new ValidationException("Please provide a valid email address");
        }
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty() && !isValidPhone(request.getPhone())) {
            throw new ValidationException("Please provide a valid phone number");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    private boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true;
        }
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        return cleanPhone.matches("^[\\+]?[1-9][\\d]{0,15}$");
    }

    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().replaceAll("<[^>]*>", "");
    }

    private RegistrationResponse mapToResponse(Registration registration) {
        RegistrationResponse response = new RegistrationResponse();
        response.setId(registration.getId());
        response.setFullName(registration.getFullName());
        response.setEmail(registration.getEmail());
        response.setPhone(registration.getPhone());
        response.setEventName(registration.getEventName());
        response.setSpecialRequests(registration.getSpecialRequests());
        response.setRegistrationDate(registration.getRegistrationDate());
        response.setCreatedAt(registration.getCreatedAt());
        response.setUpdatedAt(registration.getUpdatedAt());
        return response;
    }
}