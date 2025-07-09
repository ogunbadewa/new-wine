package com.newwine.church.controller;

import com.newwine.church.dto.request.LoginRequest;
import com.newwine.church.dto.response.ApiResponse;
import com.newwine.church.dto.response.LoginResponse;
import com.newwine.church.entity.User;
import com.newwine.church.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Admin login
     * PUBLIC ENDPOINT - No authentication required
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getUsername());
        
        try {
            LoginResponse response = authService.authenticateUser(loginRequest);
            
            if (response.isSuccess()) {
                logger.info("Login successful for user: {}", loginRequest.getUsername());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Login failed for user: {}", loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Login error for user: {}", loginRequest.getUsername(), e);
            
            LoginResponse response = new LoginResponse(
                false, 
                "Login failed. Please check your credentials and try again.", 
                null, 
                null
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Verify JWT token
     * PROTECTED ENDPOINT - Requires authentication
     */
    @GetMapping("/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> verifyToken() {
        logger.info("Token verification request");
        
        try {
            User currentUser = authService.getCurrentUser();
            
            if (currentUser == null) {
                ApiResponse<Object> response = ApiResponse.error("User not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Object userInfo = new Object() {
                public final Long id = currentUser.getId();
                public final String username = currentUser.getUsername();
                public final String role = currentUser.getRole().name();
                public final String fullName = currentUser.getFullName();
                public final String email = currentUser.getEmail();
                public final Boolean isActive = currentUser.getIsActive();
            };
            
            ApiResponse<Object> response = ApiResponse.success("Token is valid", userInfo);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Token verification error", e);
            
            ApiResponse<Object> response = ApiResponse.error(
                "Token verification failed", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Get current user info
     * PROTECTED ENDPOINT - Requires authentication
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getCurrentUser() {
        logger.info("Get current user request");
        
        try {
            User currentUser = authService.getCurrentUser();
            
            if (currentUser == null) {
                ApiResponse<Object> response = ApiResponse.error("User not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Object userInfo = new Object() {
                public final Long id = currentUser.getId();
                public final String username = currentUser.getUsername();
                public final String role = currentUser.getRole().name();
                public final String fullName = currentUser.getFullName();
                public final String email = currentUser.getEmail();
                public final Boolean isActive = currentUser.getIsActive();
                public final String lastLogin = currentUser.getLastLogin() != null ? 
                    currentUser.getLastLogin().toString() : null;
            };
            
            ApiResponse<Object> response = ApiResponse.success("User information retrieved", userInfo);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Get current user error", e);
            
            ApiResponse<Object> response = ApiResponse.error(
                "Error retrieving user information", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Check authentication status
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Object>> getAuthStatus() {
        logger.info("Auth status check");
        
        try {
            User currentUser = authService.getCurrentUser();
            
            Object status = new Object() {
                public final boolean isAuthenticated = currentUser != null;
                public final String username = currentUser != null ? currentUser.getUsername() : null;
                public final String role = currentUser != null ? currentUser.getRole().name() : null;
                public final String message = currentUser != null ? 
                    "User is authenticated" : "User is not authenticated";
            };
            
            ApiResponse<Object> response = ApiResponse.success("Authentication status", status);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Auth status check error", e);
            
            Object status = new Object() {
                public final boolean isAuthenticated = false;
                public final String username = null;
                public final String role = null;
                public final String message = "Authentication check failed";
            };
            
            ApiResponse<Object> response = ApiResponse.success("Authentication status", status);
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Validate credentials without logging in
     * PUBLIC ENDPOINT - No authentication required
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateCredentials(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Validating credentials for user: {}", loginRequest.getUsername());
        
        try {
            // Just validate the request format without actually authenticating
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                ApiResponse<String> response = ApiResponse.error("Username is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                ApiResponse<String> response = ApiResponse.error("Password is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (loginRequest.getUsername().length() < 3 || loginRequest.getUsername().length() > 50) {
                ApiResponse<String> response = ApiResponse.error("Username must be between 3 and 50 characters");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (loginRequest.getPassword().length() < 6) {
                ApiResponse<String> response = ApiResponse.error("Password must be at least 6 characters");
                return ResponseEntity.badRequest().body(response);
            }
            
            ApiResponse<String> response = ApiResponse.success("Credentials format is valid", "Valid");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Credentials validation error", e);
            
            ApiResponse<String> response = ApiResponse.error(
                "Error validating credentials", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Health check endpoint for auth service
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        logger.info("Auth service health check");
        
        try {
            ApiResponse<String> response = ApiResponse.success("Auth service is running", "OK");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Auth service health check failed", e);
            
            ApiResponse<String> response = ApiResponse.error(
                "Auth service health check failed", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}