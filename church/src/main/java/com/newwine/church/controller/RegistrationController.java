package com.newwine.church.controller;

import com.newwine.church.dto.request.RegistrationRequest;
import com.newwine.church.dto.response.ApiResponse;
import com.newwine.church.dto.response.RegistrationResponse;
import com.newwine.church.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

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
    public ResponseEntity<ApiResponse<RegistrationResponse>> registerForEvent(@Valid @RequestBody RegistrationRequest request) {
        logger.info("Registration request received for event: {} by email: {}", request.getEventName(), request.getEmail());
        
        try {
            // Set registration date if not provided
            if (request.getRegistrationDate() == null) {
                request.setRegistrationDate(LocalDateTime.now());
            }
            
            RegistrationResponse registration = registrationService.registerForEvent(request);
            
            ApiResponse<RegistrationResponse> response = ApiResponse.success(
                "Registration completed successfully! You will receive a confirmation email shortly.", 
                registration
            );
            
            logger.info("Registration completed successfully for event: {} by email: {}", 
                       request.getEventName(), request.getEmail());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing registration for event: {} by email: {}", 
                        request.getEventName(), request.getEmail(), e);
            
            String errorMessage = e.getMessage();
            if (errorMessage.contains("already registered")) {
                ApiResponse<RegistrationResponse> response = ApiResponse.error(
                    "You are already registered for this event", 
                    errorMessage
                );
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            } else if (errorMessage.contains("not found") || errorMessage.contains("does not exist")) {
                ApiResponse<RegistrationResponse> response = ApiResponse.error(
                    "Event not found or is no longer available", 
                    errorMessage
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else if (errorMessage.contains("capacity") || errorMessage.contains("full")) {
                ApiResponse<RegistrationResponse> response = ApiResponse.error(
                    "Event is at full capacity", 
                    errorMessage
                );
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            
            ApiResponse<RegistrationResponse> response = ApiResponse.error(
                "Registration failed. Please try again or contact us directly.", 
                errorMessage
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get registrations by email
     * PUBLIC ENDPOINT - No authentication required (limited info)
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<List<RegistrationResponse>>> getRegistrationsByEmail(@PathVariable String email) {
        logger.info("Fetching registrations for email: {}", email);
        
        try {
            List<RegistrationResponse> registrations = registrationService.getRegistrationsByEmail(email);
            
            // For public endpoint, only return basic info
            registrations.forEach(reg -> {
                // Keep only essential fields for privacy
                reg.setPhone(null);
                reg.setSpecialRequests(null);
            });
            
            ApiResponse<List<RegistrationResponse>> response = ApiResponse.success(
                "Registrations retrieved successfully", 
                registrations
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching registrations for email: {}", email, e);
            
            ApiResponse<List<RegistrationResponse>> response = ApiResponse.error(
                "Error fetching registrations", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Check if user is registered for an event
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Object>> checkRegistration(@RequestParam String email, @RequestParam String eventName) {
        logger.info("Checking registration for email: {} and event: {}", email, eventName);
        
        try {
            boolean isRegistered = registrationService.isUserRegisteredForEvent(email, eventName);
            
            Object result = new Object() {
                public final boolean registered = isRegistered;
                public final String userEmail = email;
                public final String NameOfEvent = eventName;
                public final String message = isRegistered ? 
                    "You are already registered for this event." : 
                    "You are not registered for this event.";
            };
            
            ApiResponse<Object> response = ApiResponse.success("Registration check completed", result);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error checking registration for email: {} and event: {}", email, eventName, e);
            
            ApiResponse<Object> response = ApiResponse.error(
                "Error checking registration", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get registration count for an event
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/count/{eventName}")
    public ResponseEntity<ApiResponse<Object>> getEventRegistrationCount(@PathVariable String eventName) {
        logger.info("Fetching registration count for event: {}", eventName);
        
        try {
            Long count = registrationService.getRegistrationCountByEvent(eventName);
            
            Object result = new Object() {
                public final Long registrationCount = count;
                public final String NameOfEvent = eventName;
                public final String message = String.format("Event has %d registrations", count);
            };
            
            ApiResponse<Object> response = ApiResponse.success("Registration count retrieved", result);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching registration count for event: {}", eventName, e);
            
            ApiResponse<Object> response = ApiResponse.error(
                "Error fetching registration count", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Cancel registration
     * PUBLIC ENDPOINT - No authentication required (with email verification)
     */
    @DeleteMapping("/cancel")
    public ResponseEntity<ApiResponse<String>> cancelRegistration(@RequestParam String email, @RequestParam String eventName) {
        logger.info("Registration cancellation request for email: {} and event: {}", email, eventName);
        
        try {
            boolean cancelled = registrationService.cancelRegistration(email, eventName);
            
            if (cancelled) {
                ApiResponse<String> response = ApiResponse.success(
                    "Registration cancelled successfully", 
                    "Cancelled"
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<String> response = ApiResponse.error(
                    "No registration found for this email and event", 
                    "Not found"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error cancelling registration for email: {} and event: {}", email, eventName, e);
            
            ApiResponse<String> response = ApiResponse.error(
                "Error cancelling registration", 
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
            Long totalRegistrations = registrationService.getRegistrationCount();
            String message = String.format("Registration service is running. Total registrations: %d", totalRegistrations);
            
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