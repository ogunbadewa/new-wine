package com.newwine.church.controller;

import com.newwine.church.dto.request.RegistrationRequest;
import com.newwine.church.dto.response.ApiResponse;
import com.newwine.church.dto.response.RegistrationResponse;
import com.newwine.church.service.RegistrationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:5500"})
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private RegistrationService registrationService;

    /**
     * Register for an event
     * PUBLIC ENDPOINT - No authentication required
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RegistrationResponse>> registerForEvent(@Valid @RequestBody RegistrationRequest registrationRequest) {
        logger.info("Received event registration from: {} for event: {}", 
                   registrationRequest.getEmail(), registrationRequest.getEventName());
        
        try {
            RegistrationResponse registrationResponse = registrationService.registerForEvent(registrationRequest);
            
            ApiResponse<RegistrationResponse> response = ApiResponse.success(
                "Registration successful! You'll receive a confirmation email shortly.", 
                registrationResponse
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing event registration", e);
            
            String errorMessage = e.getMessage();
            
            // Handle specific error cases
            if (errorMessage.contains("already registered")) {
                ApiResponse<RegistrationResponse> response = ApiResponse.error(
                    "You are already registered for this event. Check your email for confirmation details.",
                    errorMessage
                );
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            
            if (errorMessage.contains("full") || errorMessage.contains("capacity")) {
                ApiResponse<RegistrationResponse> response = ApiResponse.error(
                    "Sorry, this event is full. Please contact us to be added to the waiting list.",
                    errorMessage
                );
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            
            if (errorMessage.contains("required") || errorMessage.contains("valid")) {
                ApiResponse<RegistrationResponse> response = ApiResponse.error(
                    "Please check your information and try again.",
                    errorMessage
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // Generic error response
            ApiResponse<RegistrationResponse> response = ApiResponse.error(
                "Sorry, there was an error processing your registration. Please try again or contact us directly.",
                errorMessage
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Check if email is already registered for an event
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Object>> checkRegistration(
            @RequestParam String email, 
            @RequestParam String eventName) {
        logger.info("Checking registration status for email: {} and event: {}", email, eventName);
        
        try {
            boolean isRegister = registrationService.isEmailRegisteredForEvent(email, eventName);
            
            Object result = new Object() {
                public final boolean isRegistered = isRegister;
                public final String emailSent = email;
                public final String eventNameSent = eventName;
                public final String message = isRegistered ? 
                    "This email is already registered for this event." : 
                    "This email is not registered for this event.";
            };
            
            ApiResponse<Object> response = ApiResponse.success("Registration check completed", result);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error checking registration status", e);
            
            ApiResponse<Object> response = ApiResponse.error(
                "Error checking registration status", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get public registration statistics
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getRegistrationStats() {
        logger.info("Fetching public registration statistics");
        
        try {
            Long totalRegistration = registrationService.getRegistrationCount();
            Long recentRegistration = registrationService.getRecentRegistrationCount(30); // Last 30 days
            
            Object stats = new Object() {
                public final Long totalRegistrations = totalRegistration;
                public final Long recentRegistrations = recentRegistration;
                public final String message = "Join hundreds of others in our upcoming events!";
            };
            
            ApiResponse<Object> response = ApiResponse.success("Registration statistics", stats);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching registration statistics", e);
            
            ApiResponse<Object> response = ApiResponse.error(
                "Error fetching registration statistics", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get event capacity information
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/capacity")
    public ResponseEntity<ApiResponse<Object>> getEventCapacity(@RequestParam String eventName) {
        logger.info("Fetching capacity information for event: {}", eventName);
        
        try {
            Long registrationCount = registrationService.getRegistrationCountByEvent(eventName);
            
            Object capacity = new Object() {
                public final String NameOfEvent = eventName;
                public final Long currentRegistrations = registrationCount;
                public final String status = "Open for registration";
                public final String message = String.format("Join %d others already registered for %s!", 
                                                           registrationCount, eventName);
            };
            
            ApiResponse<Object> response = ApiResponse.success("Event capacity information", capacity);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching event capacity", e);
            
            ApiResponse<Object> response = ApiResponse.error(
                "Error fetching event capacity", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Validate registration data
     * PUBLIC ENDPOINT - No authentication required
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateRegistration(@Valid @RequestBody RegistrationRequest registrationRequest) {
        logger.info("Validating registration form data");
        
        try {
            // Just validate the request without saving
            if (registrationRequest.getFullName() == null || registrationRequest.getFullName().trim().isEmpty()) {
                ApiResponse<String> response = ApiResponse.error("Full name is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (registrationRequest.getEmail() == null || registrationRequest.getEmail().trim().isEmpty()) {
                ApiResponse<String> response = ApiResponse.error("Email is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (registrationRequest.getEventName() == null || registrationRequest.getEventName().trim().isEmpty()) {
                ApiResponse<String> response = ApiResponse.error("Event name is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Email format validation
            if (!registrationRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                ApiResponse<String> response = ApiResponse.error("Please provide a valid email address");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check for duplicate registration
            if (registrationService.isEmailRegisteredForEvent(registrationRequest.getEmail(), registrationRequest.getEventName())) {
                ApiResponse<String> response = ApiResponse.error("This email is already registered for this event");
                return ResponseEntity.badRequest().body(response);
            }
            
            ApiResponse<String> response = ApiResponse.success("Registration form data is valid", "Valid");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error validating registration form", e);
            
            ApiResponse<String> response = ApiResponse.error(
                "Error validating registration form", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Health check endpoint for registration service
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        logger.info("Registration service health check");
        
        try {
            Long registrationCount = registrationService.getRegistrationCount();
            String message = String.format("Registration service is running. Total registrations: %d", registrationCount);
            
            ApiResponse<String> response = ApiResponse.success(message, "OK");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Registration service health check failed", e);
            
            ApiResponse<String> response = ApiResponse.error(
                "Registration service health check failed", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
