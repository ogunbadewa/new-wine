package com.newwine.church.service;

import com.newwine.church.dto.request.ContactRequest;
import com.newwine.church.dto.response.ContactResponse;
import com.newwine.church.entity.Contact;
import com.newwine.church.exception.ResourceNotFoundException;
import com.newwine.church.exception.ValidationException;
import com.newwine.church.respository.ContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    @Autowired
    private ContactRepository contactRepository;

    @Transactional
    public ContactResponse submitContact(ContactRequest contactRequest) {
        logger.info("Submitting contact form for: {}", contactRequest.getEmail());

        // Validate input
        validateContactRequest(contactRequest);

        // Create contact entity
        Contact contact = new Contact();
        contact.setName(sanitizeInput(contactRequest.getName()));
        contact.setEmail(sanitizeInput(contactRequest.getEmail().toLowerCase()));
        contact.setPhone(sanitizeInput(contactRequest.getPhone()));
        contact.setSubject(StringUtils.hasText(contactRequest.getSubject()) ? 
                          sanitizeInput(contactRequest.getSubject()) : "General Inquiry");
        contact.setMessage(sanitizeInput(contactRequest.getMessage()));
        contact.setSource(StringUtils.hasText(contactRequest.getSource()) ? 
                         sanitizeInput(contactRequest.getSource()) : "Website");
        contact.setRequestType(StringUtils.hasText(contactRequest.getRequestType()) ? 
                              sanitizeInput(contactRequest.getRequestType()) : "Contact Form");

        // Save contact
        Contact savedContact = contactRepository.save(contact);
        logger.info("Contact form submitted successfully with ID: {}", savedContact.getId());

        return ContactResponse.fromEntity(savedContact);
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> getAllContacts() {
        logger.info("Fetching all contacts");
        List<Contact> contacts = contactRepository.findAllByOrderBySubmissionDateDesc();
        return contacts.stream()
                .map(ContactResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContactResponse getContactById(Long id) {
        logger.info("Fetching contact with ID: {}", id);
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with ID: " + id));
        return ContactResponse.fromEntity(contact);
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> getContactsByEmail(String email) {
        logger.info("Fetching contacts for email: {}", email);
        List<Contact> contacts = contactRepository.findByEmail(email.toLowerCase());
        return contacts.stream()
                .map(ContactResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> getContactsBySubject(String subject) {
        logger.info("Fetching contacts with subject: {}", subject);
        List<Contact> contacts = contactRepository.findBySubject(subject);
        return contacts.stream()
                .map(ContactResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> getContactsByRequestType(String requestType) {
        logger.info("Fetching contacts with request type: {}", requestType);
        List<Contact> contacts = contactRepository.findByRequestType(requestType);
        return contacts.stream()
                .map(ContactResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> getRecentContacts(int days) {
        logger.info("Fetching contacts from last {} days", days);
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        List<Contact> contacts = contactRepository.findRecentContacts(cutoffDate);
        return contacts.stream()
                .map(ContactResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> searchContacts(String searchTerm) {
        logger.info("Searching contacts with term: {}", searchTerm);
        List<Contact> contacts = contactRepository.searchByNameOrEmail(searchTerm);
        return contacts.stream()
                .map(ContactResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getContactCount() {
        return contactRepository.count();
    }

    @Transactional(readOnly = true)
    public Long getContactCountBySubject(String subject) {
        return contactRepository.countBySubject(subject);
    }

    @Transactional(readOnly = true)
    public Long getContactCountByRequestType(String requestType) {
        return contactRepository.countByRequestType(requestType);
    }

    @Transactional(readOnly = true)
    public Long getRecentContactCount(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return contactRepository.countSubmittedAfter(cutoffDate);
    }

    @Transactional
    public void deleteContact(Long id) {
        logger.info("Deleting contact with ID: {}", id);
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with ID: " + id));
        
        contactRepository.delete(contact);
        logger.info("Contact deleted successfully with ID: {}", id);
    }

    @Transactional
    public void deleteContactsByEmail(String email) {
        logger.info("Deleting contacts for email: {}", email);
        List<Contact> contacts = contactRepository.findByEmail(email.toLowerCase());
        if (!contacts.isEmpty()) {
            contactRepository.deleteAll(contacts);
            logger.info("Deleted {} contacts for email: {}", contacts.size(), email);
        }
    }

    private void validateContactRequest(ContactRequest request) {
        if (!StringUtils.hasText(request.getName())) {
            throw new ValidationException("Name is required");
        }
        if (!StringUtils.hasText(request.getEmail())) {
            throw new ValidationException("Email is required");
        }
        if (!isValidEmail(request.getEmail())) {
            throw new ValidationException("Please provide a valid email address");
        }
        if (!StringUtils.hasText(request.getMessage())) {
            throw new ValidationException("Message is required");
        }
        if (request.getName().length() > 100) {
            throw new ValidationException("Name must not exceed 100 characters");
        }
        if (request.getEmail().length() > 255) {
            throw new ValidationException("Email must not exceed 255 characters");
        }
        if (request.getMessage().length() > 2000) {
            throw new ValidationException("Message must not exceed 2000 characters");
        }
        if (request.getPhone() != null && request.getPhone().length() > 20) {
            throw new ValidationException("Phone must not exceed 20 characters");
        }
        if (request.getSubject() != null && request.getSubject().length() > 100) {
            throw new ValidationException("Subject must not exceed 100 characters");
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
