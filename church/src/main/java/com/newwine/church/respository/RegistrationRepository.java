package com.newwine.church.respository;

import com.newwine.church.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    // Find registrations by event name
    List<Registration> findByEventName(String eventName);
    
    // Find registrations by event name (case insensitive)
    List<Registration> findByEventNameIgnoreCase(String eventName);
    
    // Find registrations by email
    List<Registration> findByEmail(String email);
    
    // Find registrations by full name containing (case insensitive)
    List<Registration> findByFullNameContainingIgnoreCase(String fullName);
    
    // Find registrations by email containing (case insensitive)
    List<Registration> findByEmailContainingIgnoreCase(String email);
    
    // Find registrations after a certain date
    List<Registration> findByRegistrationDateAfter(LocalDateTime date);
    
    // Find registrations between dates
    List<Registration> findByRegistrationDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Count registrations by event name
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.eventName = :eventName")
    Long countByEventName(@Param("eventName") String eventName);
    
    // Count registrations by event name (case insensitive)
    @Query("SELECT COUNT(r) FROM Registration r WHERE LOWER(r.eventName) = LOWER(:eventName)")
    Long countByEventNameIgnoreCase(@Param("eventName") String eventName);
    
    // Count registrations after a certain date
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.registrationDate >= :date")
    Long countRegisteredAfter(@Param("date") LocalDateTime date);
    
    // Find recent registrations (last 7 days)
    @Query("SELECT r FROM Registration r WHERE r.registrationDate >= :date ORDER BY r.registrationDate DESC")
    List<Registration> findRecentRegistrations(@Param("date") LocalDateTime date);
    
    // Search registrations by name or email
    @Query("SELECT r FROM Registration r WHERE LOWER(r.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(r.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Registration> searchByNameOrEmail(@Param("searchTerm") String searchTerm);
    
    // Find all registrations ordered by registration date desc
    List<Registration> findAllByOrderByRegistrationDateDesc();
    
    // Get event statistics
    @Query("SELECT r.eventName, COUNT(r) FROM Registration r GROUP BY r.eventName")
    List<Object[]> getEventStatistics();
    
    // Check if email is already registered for an event
    @Query("SELECT COUNT(r) > 0 FROM Registration r WHERE r.email = :email AND r.eventName = :eventName")
    boolean isEmailRegisteredForEvent(@Param("email") String email, @Param("eventName") String eventName);
}
