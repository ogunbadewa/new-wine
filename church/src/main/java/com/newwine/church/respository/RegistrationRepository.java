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

    /**
     * Find all registrations ordered by registration date (newest first)
     */
    List<Registration> findAllByOrderByRegistrationDateDesc();

    /**
     * Find registrations by event name ordered by registration date (newest first)
     */
    List<Registration> findByEventNameOrderByRegistrationDateDesc(String eventName);

    /**
     * Find registrations by email ordered by registration date (newest first)
     */
    List<Registration> findByEmailOrderByRegistrationDateDesc(String email);

    /**
     * Find registrations by email and event name
     */
    List<Registration> findByEmailAndEventName(String email, String eventName);

    /**
     * Check if a user is already registered for a specific event
     */
    boolean existsByEmailAndEventName(String email, String eventName);

    /**
     * Count registrations by event name
     */
    Long countByEventName(String eventName);

    /**
     * Count registrations after a specific date
     */
    Long countByRegistrationDateAfter(LocalDateTime date);

    /**
     * Find registrations after a specific date ordered by registration date (newest first)
     */
    List<Registration> findByRegistrationDateAfterOrderByRegistrationDateDesc(LocalDateTime date);

    /**
     * Search registrations by full name or email (case-insensitive)
     */
    List<Registration> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByRegistrationDateDesc(
            String fullName, String email);

    /**
     * Find registrations between two dates
     */
    List<Registration> findByRegistrationDateBetweenOrderByRegistrationDateDesc(
            LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get event statistics (event name and count)
     */
    @Query("SELECT r.eventName, COUNT(r) FROM Registration r GROUP BY r.eventName ORDER BY COUNT(r) DESC")
    List<Object[]> findEventStatistics();

    /**
     * Find registrations by event name and date range
     */
    @Query("SELECT r FROM Registration r WHERE r.eventName = :eventName AND r.registrationDate BETWEEN :startDate AND :endDate ORDER BY r.registrationDate DESC")
    List<Registration> findByEventNameAndDateRange(
            @Param("eventName") String eventName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Count registrations by event name and date range
     */
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.eventName = :eventName AND r.registrationDate BETWEEN :startDate AND :endDate")
    Long countByEventNameAndDateRange(
            @Param("eventName") String eventName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find all unique emails
     */
    @Query("SELECT DISTINCT r.email FROM Registration r ORDER BY r.email")
    List<String> findAllUniqueEmails();

    /**
     * Find all unique event names
     */
    @Query("SELECT DISTINCT r.eventName FROM Registration r ORDER BY r.eventName")
    List<String> findAllUniqueEventNames();

    /**
     * Find registrations with special requests
     */
    @Query("SELECT r FROM Registration r WHERE r.specialRequests IS NOT NULL AND r.specialRequests != '' ORDER BY r.registrationDate DESC")
    List<Registration> findRegistrationsWithSpecialRequests();

    /**
     * Count registrations with special requests by event
     */
    @Query("SELECT r.eventName, COUNT(r) FROM Registration r WHERE r.specialRequests IS NOT NULL AND r.specialRequests != '' GROUP BY r.eventName")
    List<Object[]> countRegistrationsWithSpecialRequestsByEvent();

    /**
     * Find recent registrations for a specific event
     */
    @Query("SELECT r FROM Registration r WHERE r.eventName = :eventName AND r.registrationDate >= :since ORDER BY r.registrationDate DESC")
    List<Registration> findRecentRegistrationsByEvent(
            @Param("eventName") String eventName,
            @Param("since") LocalDateTime since);

    /**
     * Get monthly registration statistics
     */
    @Query("SELECT YEAR(r.registrationDate), MONTH(r.registrationDate), COUNT(r) " +
           "FROM Registration r " +
           "GROUP BY YEAR(r.registrationDate), MONTH(r.registrationDate) " +
           "ORDER BY YEAR(r.registrationDate) DESC, MONTH(r.registrationDate) DESC")
    List<Object[]> findMonthlyRegistrationStatistics();

    /**
     * Find top events by registration count
     */
    @Query("SELECT r.eventName, COUNT(r) FROM Registration r GROUP BY r.eventName ORDER BY COUNT(r) DESC")
    List<Object[]> findTopEventsByRegistrationCount();

    /**
     * Search registrations by multiple criteria
     */
    @Query("SELECT r FROM Registration r WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(r.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.eventName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:eventName IS NULL OR r.eventName = :eventName) AND " +
           "(:startDate IS NULL OR r.registrationDate >= :startDate) AND " +
           "(:endDate IS NULL OR r.registrationDate <= :endDate) " +
           "ORDER BY r.registrationDate DESC")
    List<Registration> searchRegistrations(
            @Param("searchTerm") String searchTerm,
            @Param("eventName") String eventName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Delete registrations by email
     */
    void deleteByEmail(String email);

    /**
     * Delete registrations by event name
     */
    void deleteByEventName(String eventName);
}