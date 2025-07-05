package com.newwine.church.service;

import com.newwine.church.dto.request.RegistrationRequest;
import com.newwine.church.dto.response.RegistrationResponse;
import com.newwine.church.entity.Event;
import com.newwine.church.entity.Registration;
import com.newwine.church.exception.ResourceNotFoundException;
import com.newwine.church.exception.ValidationException;
import com.newwine.church.respository.EventRepository;
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
    private EventRepository eventRepository;

    @Transactional
    public RegistrationResponse registerForEvent(RegistrationRequest registrationRequest) {
        logger.info("Processing event registration for: {} - Event: {}", 
                   registrationRequest.getEmail(), registrationRequest.getEventName());

        // Validate input
        validateRegistrationRequest(registrationRequest);

        // Check if email is already registered for this event
        if (registrationRepository.isEmailRegisteredForEvent(
                registrationRequest.getEmail().toLowerCase(), registrationRequest.getEventName())) {
            throw new ValidationException("Email is already registered for this event");
        }

        // Check if event exists and has capacity
        Event event = eventRepository.findByNameIgnoreCase(registrationRequest.getEventName())
                .orElse(null);
        
        if (event != null && event.getMaxCapacity() != null && 
            event.getCurrentRegistrations() >= event.getMaxCapacity()) {
            throw new ValidationException("Event is full - maximum capacity reached");
        }

        // Create registration entity
        Registration registration = new Registration();
        registration.setFullName(sanitizeInput(registrationRequest.getFullName()));
        registration.setEmail(sanitizeInput(registrationRequest.getEmail().toLowerCase()));
        registration.setPhone(sanitizeInput(registrationRequest.getPhone()));
        registration.setEmergencyContact(sanitizeInput(registrationRequest.getEmergencyContact()));
        registration.setEventName(sanitizeInput(registrationRequest.getEventName()));
        registration.setSpecialRequests(sanitizeInput(registrationRequest.getSpecialRequests()));

        // Save registration
        Registration savedRegistration = registrationRepository.save(registration);

        // Update event registration count if event exists
        if (event != null) {
            event.setCurrentRegistrations(event.getCurrentRegistrations() + 1);
            eventRepository.save(event);
        }

        logger.info("Registration completed successfully with ID: {}", savedRegistration.getId());
        return RegistrationResponse.fromEntity(savedRegistration);
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getAllRegistrations() {
        logger.info("Fetching all registrations");
        List<Registration> registrations = registrationRepository.findAllByOrderByRegistrationDateDesc();
        return registrations.stream()
                .map(RegistrationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RegistrationResponse getRegistrationById(Long id) {
        logger.info("Fetching registration with ID: {}", id);
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + id));
        return RegistrationResponse.fromEntity(registration);
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getRegistrationsByEvent(String eventName) {
        logger.info("Fetching registrations for event: {}", eventName);
        List<Registration> registrations = registrationRepository.findByEventNameIgnoreCase(eventName);
        return registrations.stream()
                .map(RegistrationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getRegistrationsByEmail(String email) {
        logger.info("Fetching registrations for email: {}", email);
        List<Registration> registrations = registrationRepository.findByEmail(email.toLowerCase());
        return registrations.stream()
                .map(RegistrationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getRecentRegistrations(int days) {
        logger.info("Fetching registrations from last {} days", days);
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        List<Registration> registrations = registrationRepository.findRecentRegistrations(cutoffDate);
        return registrations.stream()
                .map(RegistrationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> searchRegistrations(String searchTerm) {
        logger.info("Searching registrations with term: {}", searchTerm);
        List<Registration> registrations = registrationRepository.searchByNameOrEmail(searchTerm);
        return registrations.stream()
                .map(RegistrationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getRegistrationCount() {
        return registrationRepository.count();
    }

    @Transactional(readOnly = true)
    public Long getRegistrationCountByEvent(String eventName) {
        return registrationRepository.countByEventNameIgnoreCase(eventName);
    }

    @Transactional(readOnly = true)
    public Long getRecentRegistrationCount(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return registrationRepository.countRegisteredAfter(cutoffDate);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getEventStatistics() {
        logger.info("Fetching event statistics");
        List<Object[]> results = registrationRepository.getEventStatistics();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));
    }

    @Transactional(readOnly = true)
    public boolean isEmailRegisteredForEvent(String email, String eventName) {
        return registrationRepository.isEmailRegisteredForEvent(email.toLowerCase(), eventName);
    }

    @Transactional
    public void deleteRegistration(Long id) {
        logger.info("Deleting registration with ID: {}", id);
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + id));
        
        // Update event registration count if event exists
        Event event = eventRepository.findByNameIgnoreCase(registration.getEventName())
                .orElse(null);
        if (event != null && event.getCurrentRegistrations() > 0) {
            event.setCurrentRegistrations(event.getCurrentRegistrations() - 1);
            eventRepository.save(event);
        }

        registrationRepository.delete(registration);
        logger.info("Registration deleted successfully with ID: {}", id);
    }

    @Transactional
    public void deleteRegistrationsByEmail(String email) {
        logger.info("Deleting registrations for email: {}", email);
        List<Registration> registrations = registrationRepository.findByEmail(email.toLowerCase());
        if (!registrations.isEmpty()) {
            // Update event registration counts
            for (Registration registration : registrations) {
                Event event = eventRepository.findByNameIgnoreCase(registration.getEventName())
                        .orElse(null);
                if (event != null && event.getCurrentRegistrations() > 0) {
                    event.setCurrentRegistrations(event.getCurrentRegistrations() - 1);
                    eventRepository.save(event);
                }
            }
            
            registrationRepository.deleteAll(registrations);
            logger.info("Deleted {} registrations for email: {}", registrations.size(), email);
        }
    }

    @Transactional
    public void deleteRegistrationsByEvent(String eventName) {
        logger.info("Deleting registrations for event: {}", eventName);
        List<Registration> registrations = registrationRepository.findByEventNameIgnoreCase(eventName);
        if (!registrations.isEmpty()) {
            // Reset event registration count
            Event event = eventRepository.findByNameIgnoreCase(eventName).orElse(null);
            if (event != null) {
                event.setCurrentRegistrations(0);
                eventRepository.save(event);
            }
            
            registrationRepository.deleteAll(registrations);
            logger.info("Deleted {} registrations for event: {}", registrations.size(), eventName);
        }
    }

    private void validateRegistrationRequest(RegistrationRequest request) {
        if (!StringUtils.hasText(request.getFullName())) {
            throw new ValidationException("Full name is required");
        }
        if (!StringUtils.hasText(request.getEmail())) {
            throw new ValidationException("Email is required");
        }
        if (!isValidEmail(request.getEmail())) {
            throw new ValidationException("Please provide a valid email address");
        }
        if (!StringUtils.hasText(request.getEventName())) {
            throw new ValidationException("Event name is required");
        }
        if (request.getFullName().length() > 100) {
            throw new ValidationException("Full name must not exceed 100 characters");
        }
        if (request.getEmail().length() > 255) {
            throw new ValidationException("Email must not exceed 255 characters");
        }
        if (request.getPhone() != null && request.getPhone().length() > 20) {
            throw new ValidationException("Phone must not exceed 20 characters");
        }
        if (request.getEmergencyContact() != null && request.getEmergencyContact().length() > 200) {
            throw new ValidationException("Emergency contact must not exceed 200 characters");
        }
        if (request.getEventName().length() > 100) {
            throw new ValidationException("Event name must not exceed 100 characters");
        }
        if (request.getSpecialRequests() != null && request.getSpecialRequests().length() > 1000) {
            throw new ValidationException("Special requests must not exceed 1000 characters");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().replaceAll("<[^>]*>", "");
    }
}