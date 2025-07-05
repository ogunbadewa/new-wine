package com.newwine.church.service;

import com.newwine.church.dto.response.ContactResponse;
import com.newwine.church.dto.response.RegistrationResponse;
import com.newwine.church.dto.response.StatsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private EventService eventService;

    @Transactional(readOnly = true)
    public List<ContactResponse> getAllContacts() {
        logger.info("Admin: Fetching all contacts");
        return contactService.getAllContacts();
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getAllRegistrations() {
        logger.info("Admin: Fetching all registrations");
        return registrationService.getAllRegistrations();
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getRegistrationsByEvent(String eventName) {
        logger.info("Admin: Fetching registrations for event: {}", eventName);
        return registrationService.getRegistrationsByEvent(eventName);
    }

    @Transactional(readOnly = true)
    public StatsResponse getAdminStatistics() {
        logger.info("Admin: Fetching admin statistics");

        // Get total counts
        Long totalRegistrations = registrationService.getRegistrationCount();
        Long totalContacts = contactService.getContactCount();

        // Get recent counts (last 7 days)
        Long recentRegistrations = registrationService.getRecentRegistrationCount(7);
        Long recentContacts = contactService.getRecentContactCount(7);
        Long recentCount = recentRegistrations + recentContacts;

        // Get event statistics
        Map<String, Long> eventStats = registrationService.getEventStatistics();

        // Add some additional stats
        Map<String, Long> enhancedEventStats = new HashMap<>(eventStats);
        enhancedEventStats.put("Total Active Events", eventService.getActiveEventCount());
        enhancedEventStats.put("Upcoming Events", eventService.getUpcomingEventCount());

        StatsResponse stats = new StatsResponse(totalRegistrations, totalContacts, enhancedEventStats, recentCount);
        
        logger.info("Admin statistics compiled - Registrations: {}, Contacts: {}, Recent: {}", 
                   totalRegistrations, totalContacts, recentCount);
        
        return stats;
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> searchContacts(String searchTerm) {
        logger.info("Admin: Searching contacts with term: {}", searchTerm);
        return contactService.searchContacts(searchTerm);
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> searchRegistrations(String searchTerm) {
        logger.info("Admin: Searching registrations with term: {}", searchTerm);
        return registrationService.searchRegistrations(searchTerm);
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> getContactsBySubject(String subject) {
        logger.info("Admin: Fetching contacts by subject: {}", subject);
        return contactService.getContactsBySubject(subject);
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> getContactsByRequestType(String requestType) {
        logger.info("Admin: Fetching contacts by request type: {}", requestType);
        return contactService.getContactsByRequestType(requestType);
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> getRecentContacts(int days) {
        logger.info("Admin: Fetching recent contacts from last {} days", days);
        return contactService.getRecentContacts(days);
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getRecentRegistrations(int days) {
        logger.info("Admin: Fetching recent registrations from last {} days", days);
        return registrationService.getRecentRegistrations(days);
    }

    @Transactional(readOnly = true)
    public ContactResponse getContactById(Long id) {
        logger.info("Admin: Fetching contact with ID: {}", id);
        return contactService.getContactById(id);
    }

    @Transactional(readOnly = true)
    public RegistrationResponse getRegistrationById(Long id) {
        logger.info("Admin: Fetching registration with ID: {}", id);
        return registrationService.getRegistrationById(id);
    }

    @Transactional
    public void deleteContact(Long id) {
        logger.info("Admin: Deleting contact with ID: {}", id);
        contactService.deleteContact(id);
    }

    @Transactional
    public void deleteRegistration(Long id) {
        logger.info("Admin: Deleting registration with ID: {}", id);
        registrationService.deleteRegistration(id);
    }

    @Transactional
    public void deleteContactsByEmail(String email) {
        logger.info("Admin: Deleting contacts for email: {}", email);
        contactService.deleteContactsByEmail(email);
    }

    @Transactional
    public void deleteRegistrationsByEmail(String email) {
        logger.info("Admin: Deleting registrations for email: {}", email);
        registrationService.deleteRegistrationsByEmail(email);
    }

    @Transactional
    public void deleteRegistrationsByEvent(String eventName) {
        logger.info("Admin: Deleting registrations for event: {}", eventName);
        registrationService.deleteRegistrationsByEvent(eventName);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardData() {
        logger.info("Admin: Fetching dashboard data");

        Map<String, Object> dashboardData = new HashMap<>();
        
        // Get statistics
        StatsResponse stats = getAdminStatistics();
        dashboardData.put("stats", stats);
        
        // Get recent activity
        List<ContactResponse> recentContacts = getRecentContacts(7);
        List<RegistrationResponse> recentRegistrations = getRecentRegistrations(7);
        
        dashboardData.put("recentContacts", recentContacts);
        dashboardData.put("recentRegistrations", recentRegistrations);
        
        // Get event breakdown
        Map<String, Long> eventStats = registrationService.getEventStatistics();
        dashboardData.put("eventBreakdown", eventStats);
        
        // Get contact type breakdown
        Map<String, Long> contactTypeStats = new HashMap<>();
        contactTypeStats.put("General Inquiry", contactService.getContactCountBySubject("General Inquiry"));
        contactTypeStats.put("Prayer Request", contactService.getContactCountBySubject("Prayer Request"));
        contactTypeStats.put("Small Groups", contactService.getContactCountBySubject("Small Groups"));
        contactTypeStats.put("Youth Ministry", contactService.getContactCountBySubject("Youth Ministry"));
        contactTypeStats.put("Volunteering", contactService.getContactCountBySubject("Volunteering"));
        contactTypeStats.put("Events", contactService.getContactCountBySubject("Events"));
        dashboardData.put("contactTypeBreakdown", contactTypeStats);
        
        return dashboardData;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getEventAnalytics(String eventName) {
        logger.info("Admin: Fetching analytics for event: {}", eventName);

        Map<String, Object> analytics = new HashMap<>();
        
        // Get registrations for the event
        List<RegistrationResponse> registrations = getRegistrationsByEvent(eventName);
        analytics.put("totalRegistrations", registrations.size());
        
        // Count registrations with special requests
        long specialRequestsCount = registrations.stream()
                .filter(r -> r.getSpecialRequests() != null && !r.getSpecialRequests().trim().isEmpty())
                .count();
        analytics.put("specialRequestsCount", specialRequestsCount);
        
        // Count registrations with emergency contacts
        long emergencyContactsCount = registrations.stream()
                .filter(r -> r.getEmergencyContact() != null && !r.getEmergencyContact().trim().isEmpty())
                .count();
        analytics.put("emergencyContactsCount", emergencyContactsCount);
        
        // Count registrations with phone numbers
        long phoneNumbersCount = registrations.stream()
                .filter(r -> r.getPhone() != null && !r.getPhone().trim().isEmpty())
                .count();
        analytics.put("phoneNumbersCount", phoneNumbersCount);
        
        // Registration timeline (last 30 days)
        List<RegistrationResponse> recentRegistrations = registrations.stream()
                .filter(r -> r.getRegistrationDate().isAfter(java.time.LocalDateTime.now().minusDays(30)))
                .toList();
        analytics.put("recentRegistrations", recentRegistrations.size());
        
        return analytics;
    }
}
