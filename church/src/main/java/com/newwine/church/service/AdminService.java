package com.newwine.church.service;

import com.newwine.church.dto.response.ContactResponse;
import com.newwine.church.dto.response.RegistrationResponse;
import com.newwine.church.dto.response.StatsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public Map<String, Object> getFilteredData(String searchTerm, String type, String eventName, 
                                              LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Admin: Filtering data with criteria - searchTerm: {}, type: {}, eventName: {}, startDate: {}, endDate: {}", 
                   searchTerm, type, eventName, startDate, endDate);

        Map<String, Object> result = new HashMap<>();
        
        // Get all data
        List<ContactResponse> allContacts = getAllContacts();
        List<RegistrationResponse> allRegistrations = getAllRegistrations();
        
        // Filter contacts
        List<ContactResponse> filteredContacts = allContacts.stream()
                .filter(contact -> {
                    // Type filter
                    if (type != null && !"all".equals(type) && "contact".equals(type)) {
                        // Keep contacts when type is "contact"
                    } else if (type != null && !"all".equals(type) && !"contact".equals(type)) {
                        return false; // Exclude contacts when type is not "contact"
                    }
                    
                    // Search term filter
                    if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                        String lowerSearchTerm = searchTerm.toLowerCase();
                        boolean matchesName = contact.getName() != null && contact.getName().toLowerCase().contains(lowerSearchTerm);
                        boolean matchesEmail = contact.getEmail() != null && contact.getEmail().toLowerCase().contains(lowerSearchTerm);
                        if (!matchesName && !matchesEmail) {
                            return false;
                        }
                    }
                    
                    // Event/Subject filter
                    if (eventName != null && !"all".equals(eventName)) {
                        if (contact.getSubject() == null || !contact.getSubject().equals(eventName)) {
                            return false;
                        }
                    }
                    
                    // Date range filter
                    if (startDate != null || endDate != null) {
                        LocalDateTime contactDate = contact.getSubmissionDate();
                        if (contactDate == null) {
                            return false;
                        }
                        
                        if (startDate != null && contactDate.isBefore(startDate)) {
                            return false;
                        }
                        
                        if (endDate != null && contactDate.isAfter(endDate)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
        
        // Filter registrations
        List<RegistrationResponse> filteredRegistrations = allRegistrations.stream()
                .filter(registration -> {
                    // Type filter
                    if (type != null && !"all".equals(type) && "registration".equals(type)) {
                        // Keep registrations when type is "registration"
                    } else if (type != null && !"all".equals(type) && !"registration".equals(type)) {
                        return false; // Exclude registrations when type is not "registration"
                    }
                    
                    // Search term filter
                    if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                        String lowerSearchTerm = searchTerm.toLowerCase();
                        boolean matchesName = registration.getFullName() != null && registration.getFullName().toLowerCase().contains(lowerSearchTerm);
                        boolean matchesEmail = registration.getEmail() != null && registration.getEmail().toLowerCase().contains(lowerSearchTerm);
                        if (!matchesName && !matchesEmail) {
                            return false;
                        }
                    }
                    
                    // Event filter
                    if (eventName != null && !"all".equals(eventName)) {
                        if (registration.getEventName() == null || !registration.getEventName().equals(eventName)) {
                            return false;
                        }
                    }
                    
                    // Date range filter
                    if (startDate != null || endDate != null) {
                        LocalDateTime registrationDate = registration.getRegistrationDate();
                        if (registrationDate == null) {
                            return false;
                        }
                        
                        if (startDate != null && registrationDate.isBefore(startDate)) {
                            return false;
                        }
                        
                        if (endDate != null && registrationDate.isAfter(endDate)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
        
        // Combine results
        result.put("contacts", filteredContacts);
        result.put("registrations", filteredRegistrations);
        result.put("totalContacts", filteredContacts.size());
        result.put("totalRegistrations", filteredRegistrations.size());
        result.put("totalResults", filteredContacts.size() + filteredRegistrations.size());
        
        // Get unique emails for the copy feature
        Set<String> uniqueEmails = new HashSet<>();
        filteredContacts.forEach(c -> {
            if (c.getEmail() != null && !c.getEmail().trim().isEmpty()) {
                uniqueEmails.add(c.getEmail());
            }
        });
        filteredRegistrations.forEach(r -> {
            if (r.getEmail() != null && !r.getEmail().trim().isEmpty()) {
                uniqueEmails.add(r.getEmail());
            }
        });
        result.put("uniqueEmails", uniqueEmails.size());
        result.put("emailList", new ArrayList<>(uniqueEmails));
        
        logger.info("Admin: Filtered data - {} contacts, {} registrations, {} unique emails", 
                   filteredContacts.size(), filteredRegistrations.size(), uniqueEmails.size());
        
        return result;
    }
}