package com.newwine.church.controller;

import com.newwine.church.dto.request.ContactRequest;
import com.newwine.church.dto.response.ApiResponse;
import com.newwine.church.dto.response.ContactResponse;
import com.newwine.church.service.ContactService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    /**
     * Submit a new contact form
     * PUBLIC ENDPOINT - No authentication required
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ContactResponse>> submitContact(@Valid @RequestBody ContactRequest contactRequest) {
        logger.info("Received contact form submission from: {}", contactRequest.getEmail());
        
        try {
            ContactResponse contactResponse = contactService.submitContact(contactRequest);
            
            ApiResponse<ContactResponse> response = ApiResponse.success(
                "Contact form submitted successfully. We'll get back to you soon!", 
                contactResponse
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error submitting contact form", e);
            
            ApiResponse<ContactResponse> response = ApiResponse.error(
                "Sorry, there was an error submitting your contact form. Please try again.",
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Health check endpoint for contact service
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        logger.info("Contact service health check");
        
        try {
            Long contactCount = contactService.getContactCount();
            String message = String.format("Contact service is running. Total contacts: %d", contactCount);
            
            ApiResponse<String> response = ApiResponse.success(message, "OK");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Contact service health check failed", e);
            
            ApiResponse<String> response = ApiResponse.error(
                "Contact service health check failed", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get contact statistics (public summary)
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getContactStats() {
        logger.info("Fetching public contact statistics");
        
        try {
            Long totalContact = contactService.getContactCount();
            Long recentContact = contactService.getRecentContactCount(30); // Last 30 days
            
            Object stats = new Object() {
                public final Long totalContacts = totalContact;
                public final Long recentContacts = recentContact;
                public final String message = "Thank you for your interest in New Wine Church!";
            };
            
            ApiResponse<Object> response = ApiResponse.success("Contact statistics", stats);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching contact statistics", e);
            
            ApiResponse<Object> response = ApiResponse.error(
                "Error fetching contact statistics", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Test endpoint for contact validation
     * PUBLIC ENDPOINT - No authentication required
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateContact(@Valid @RequestBody ContactRequest contactRequest) {
        logger.info("Validating contact form data");
        
        try {
            // Just validate the request without saving
            if (contactRequest.getName() == null || contactRequest.getName().trim().isEmpty()) {
                ApiResponse<String> response = ApiResponse.error("Name is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (contactRequest.getEmail() == null || contactRequest.getEmail().trim().isEmpty()) {
                ApiResponse<String> response = ApiResponse.error("Email is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (contactRequest.getMessage() == null || contactRequest.getMessage().trim().isEmpty()) {
                ApiResponse<String> response = ApiResponse.error("Message is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Email format validation
            if (!contactRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                ApiResponse<String> response = ApiResponse.error("Please provide a valid email address");
                return ResponseEntity.badRequest().body(response);
            }
            
            ApiResponse<String> response = ApiResponse.success("Contact form data is valid", "Valid");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error validating contact form", e);
            
            ApiResponse<String> response = ApiResponse.error(
                "Error validating contact form", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}