package com.newwine.church.controller;

import com.newwine.church.dto.response.ApiResponse;
import com.newwine.church.dto.response.ContactResponse;
import com.newwine.church.dto.response.RegistrationResponse;
import com.newwine.church.dto.response.StatsResponse;
import com.newwine.church.service.AdminService;
import com.newwine.church.service.ExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:5500"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private ExportService exportService;

    /**
     * Get all contacts
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @GetMapping("/contacts")
    public ResponseEntity<ApiResponse<List<ContactResponse>>> getAllContacts() {
        logger.info("Admin: Fetching all contacts");
        
        try {
            List<ContactResponse> contacts = adminService.getAllContacts();
            
            ApiResponse<List<ContactResponse>> response = ApiResponse.success(
                "Contacts retrieved successfully", 
                contacts
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Admin: Error fetching contacts", e);
            
            ApiResponse<List<ContactResponse>> response = ApiResponse.error(
                "Error fetching contacts", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all registrations
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @GetMapping("/registrations")
    public ResponseEntity<ApiResponse<List<RegistrationResponse>>> getAllRegistrations() {
        logger.info("Admin: Fetching all registrations");
        
        try {
            List<RegistrationResponse> registrations = adminService.getAllRegistrations();
            
            ApiResponse<List<RegistrationResponse>> response = ApiResponse.success(
                "Registrations retrieved successfully", 
                registrations
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Admin: Error fetching registrations", e);
            
            ApiResponse<List<RegistrationResponse>> response = ApiResponse.error(
                "Error fetching registrations", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get registrations by event
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @GetMapping("/registrations/event/{eventName}")
    public ResponseEntity<ApiResponse<List<RegistrationResponse>>> getRegistrationsByEvent(@PathVariable String eventName) {
        logger.info("Admin: Fetching registrations for event: {}", eventName);
        
        try {
            List<RegistrationResponse> registrations = adminService.getRegistrationsByEvent(eventName);
            
            ApiResponse<List<RegistrationResponse>> response = ApiResponse.success(
                "Registrations retrieved successfully", 
                registrations
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Admin: Error fetching registrations for event: {}", eventName, e);
            
            ApiResponse<List<RegistrationResponse>> response = ApiResponse.error(
                "Error fetching registrations for event", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get filtered data with multiple criteria
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @GetMapping("/data/search")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFilteredData(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String eventName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        logger.info("Admin: Searching data with criteria - searchTerm: {}, type: {}, eventName: {}, startDate: {}, endDate: {}", 
                   searchTerm, type, eventName, startDate, endDate);
        
        try {
            Map<String, Object> result = adminService.getFilteredData(searchTerm, type, eventName, startDate, endDate);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "Filtered data retrieved successfully", 
                result
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Admin: Error fetching filtered data", e);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Error fetching filtered data", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get contact by ID
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @GetMapping("/contacts/{id}")
    public ResponseEntity<ApiResponse<ContactResponse>> getContactById(@PathVariable Long id) {
        logger.info("Admin: Fetching contact with ID: {}", id);
        
        try {
            ContactResponse contact = adminService.getContactById(id);
            
            ApiResponse<ContactResponse> response = ApiResponse.success(
                "Contact retrieved successfully", 
                contact
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Admin: Error fetching contact with ID: {}", id, e);
            
            if (e.getMessage().contains("not found")) {
                ApiResponse<ContactResponse> response = ApiResponse.error(
                    "Contact not found", 
                    e.getMessage()
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            ApiResponse<ContactResponse> response = ApiResponse.error(
                "Error fetching contact", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get registration by ID
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @GetMapping("/registrations/{id}")
    public ResponseEntity<ApiResponse<RegistrationResponse>> getRegistrationById(@PathVariable Long id) {
        logger.info("Admin: Fetching registration with ID: {}", id);
        
        try {
            RegistrationResponse registration = adminService.getRegistrationById(id);
            
            ApiResponse<RegistrationResponse> response = ApiResponse.success(
                "Registration retrieved successfully", 
                registration
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Admin: Error fetching registration with ID: {}", id, e);
            
            if (e.getMessage().contains("not found")) {
                ApiResponse<RegistrationResponse> response = ApiResponse.error(
                    "Registration not found", 
                    e.getMessage()
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            ApiResponse<RegistrationResponse> response = ApiResponse.error(
                "Error fetching registration", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete contact by ID
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @DeleteMapping("/contacts/{id}")
    public ResponseEntity<ApiResponse<String>> deleteContact(@PathVariable Long id) {
        logger.info("Admin: Deleting contact with ID: {}", id);
        
        try {
            adminService.deleteContact(id);
            
            ApiResponse<String> response = ApiResponse.success(
                "Contact deleted successfully", 
                "Deleted"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Admin: Error deleting contact with ID: {}", id, e);
            
            if (e.getMessage().contains("not found")) {
                ApiResponse<String> response = ApiResponse.error(
                    "Contact not found", 
                    e.getMessage()
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            ApiResponse<String> response = ApiResponse.error(
                "Error deleting contact", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete registration by ID
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @DeleteMapping("/registrations/{id}")
    public ResponseEntity<ApiResponse<String>> deleteRegistration(@PathVariable Long id) {
        logger.info("Admin: Deleting registration with ID: {}", id);
        
        try {
            adminService.deleteRegistration(id);
            
            ApiResponse<String> response = ApiResponse.success(
                "Registration deleted successfully", 
                "Deleted"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Admin: Error deleting registration with ID: {}", id, e);
            
            if (e.getMessage().contains("not found")) {
                ApiResponse<String> response = ApiResponse.error(
                    "Registration not found", 
                    e.getMessage()
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            ApiResponse<String> response = ApiResponse.error(
                "Error deleting registration", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get admin statistics
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<StatsResponse>> getAdminStats() {
        logger.info("Admin: Fetching admin statistics");
        
        try {
            StatsResponse stats = adminService.getAdminStatistics();
            
            ApiResponse<StatsResponse> response = ApiResponse.success(
                "Statistics retrieved successfully", 
                stats
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Admin: Error fetching statistics", e);
            
            ApiResponse<StatsResponse> response = ApiResponse.error(
                "Error fetching statistics", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Export all data to CSV
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportData(@RequestParam(required = false) String event) {
        logger.info("Admin: Exporting data to CSV. Event filter: {}", event);
        
        try {
            byte[] csvData;
            String filename;
            
            if (event != null && !event.trim().isEmpty()) {
                csvData = exportService.exportRegistrationsByEventToCsv(event);
                filename = String.format("new-wine-%s-registrations-%s.csv", 
                    event.toLowerCase().replace(" ", "-"), 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } else {
                csvData = exportService.exportAllDataToCsv();
                filename = String.format("new-wine-all-data-%s.csv", 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(csvData.length);
            
            logger.info("Admin: Successfully exported data to CSV. File size: {} bytes", csvData.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
            
        } catch (IOException e) {
            logger.error("Admin: Error exporting data to CSV", e);
            
            String errorMessage = "Error exporting data: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_PLAIN)
                .body(errorMessage.getBytes());
        }
    }

    /**
     * Export contacts to CSV
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @GetMapping("/export/contacts")
    public ResponseEntity<byte[]> exportContacts() {
        logger.info("Admin: Exporting contacts to CSV");
        
        try {
            byte[] csvData = exportService.exportContactsToCsv();
            String filename = String.format("new-wine-contacts-%s.csv", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(csvData.length);
            
            logger.info("Admin: Successfully exported contacts to CSV. File size: {} bytes", csvData.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
            
        } catch (IOException e) {
            logger.error("Admin: Error exporting contacts to CSV", e);
            
            String errorMessage = "Error exporting contacts: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_PLAIN)
                .body(errorMessage.getBytes());
        }
    }

    /**
     * Export registrations to CSV
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @GetMapping("/export/registrations")
    public ResponseEntity<byte[]> exportRegistrations() {
        logger.info("Admin: Exporting registrations to CSV");
        
        try {
            byte[] csvData = exportService.exportRegistrationsToCsv();
            String filename = String.format("new-wine-registrations-%s.csv", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(csvData.length);
            
            logger.info("Admin: Successfully exported registrations to CSV. File size: {} bytes", csvData.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
            
        } catch (IOException e) {
            logger.error("Admin: Error exporting registrations to CSV", e);
            
            String errorMessage = "Error exporting registrations: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_PLAIN)
                .body(errorMessage.getBytes());
        }
    }

    /**
     * Export email list to CSV
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @GetMapping("/export/emails")
    public ResponseEntity<byte[]> exportEmails() {
        logger.info("Admin: Exporting email list to CSV");
        
        try {
            byte[] csvData = exportService.exportEmailListToCsv();
            String filename = String.format("new-wine-emails-%s.csv", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(csvData.length);
            
            logger.info("Admin: Successfully exported email list to CSV. File size: {} bytes", csvData.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
            
        } catch (IOException e) {
            logger.error("Admin: Error exporting email list to CSV", e);
            
            String errorMessage = "Error exporting email list: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_PLAIN)
                .body(errorMessage.getBytes());
        }
    }

    /**
     * Get dashboard data
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardData() {
        logger.info("Admin: Fetching dashboard data");
        
        try {
            Map<String, Object> dashboardData = adminService.getDashboardData();
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "Dashboard data retrieved successfully", 
                dashboardData
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Admin: Error fetching dashboard data", e);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Error fetching dashboard data", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get event analytics
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @GetMapping("/analytics/event/{eventName}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEventAnalytics(@PathVariable String eventName) {
        logger.info("Admin: Fetching event analytics for: {}", eventName);
        
        try {
            Map<String, Object> analytics = adminService.getEventAnalytics(eventName);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "Event analytics retrieved successfully", 
                analytics
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Admin: Error fetching event analytics for: {}", eventName, e);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Error fetching event analytics", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get recent activity
     * PROTECTED ENDPOINT - Requires admin authentication
     */
    @GetMapping("/activity")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRecentActivity(@RequestParam(defaultValue = "7") int days) {
        logger.info("Admin: Fetching recent activity for last {} days", days);
        
        try {
            List<ContactResponse> recentContacts = adminService.getRecentContacts(days);
            List<RegistrationResponse> recentRegistrations = adminService.getRecentRegistrations(days);
            
            Map<String, Object> activity = Map.of(
                "contacts", recentContacts,
                "registrations", recentRegistrations,
                "totalActivity", recentContacts.size() + recentRegistrations.size(),
                "days", days
            );
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "Recent activity retrieved successfully", 
                activity
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Admin: Error fetching recent activity", e);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Error fetching recent activity", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}