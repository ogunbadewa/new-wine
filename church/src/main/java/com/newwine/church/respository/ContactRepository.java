package com.newwine.church.respository;

import com.newwine.church.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    // Find contacts by email
    List<Contact> findByEmail(String email);
    
    // Find contacts by name containing (case insensitive)
    List<Contact> findByNameContainingIgnoreCase(String name);
    
    // Find contacts by email containing (case insensitive)
    List<Contact> findByEmailContainingIgnoreCase(String email);
    
    // Find contacts by subject
    List<Contact> findBySubject(String subject);
    
    // Find contacts by request type
    List<Contact> findByRequestType(String requestType);
    
    // Find contacts by source
    List<Contact> findBySource(String source);
    
    // Find contacts submitted after a certain date
    List<Contact> findBySubmissionDateAfter(LocalDateTime date);
    
    // Find contacts submitted between dates
    List<Contact> findBySubmissionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Count contacts by subject
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.subject = :subject")
    Long countBySubject(@Param("subject") String subject);
    
    // Count contacts by request type
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.requestType = :requestType")
    Long countByRequestType(@Param("requestType") String requestType);
    
    // Count contacts submitted after a certain date
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.submissionDate >= :date")
    Long countSubmittedAfter(@Param("date") LocalDateTime date);
    
    // Find recent contacts (last 7 days)
    @Query("SELECT c FROM Contact c WHERE c.submissionDate >= :date ORDER BY c.submissionDate DESC")
    List<Contact> findRecentContacts(@Param("date") LocalDateTime date);
    
    // Search contacts by name or email
    @Query("SELECT c FROM Contact c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Contact> searchByNameOrEmail(@Param("searchTerm") String searchTerm);
    
    // Find all contacts ordered by submission date desc
    List<Contact> findAllByOrderBySubmissionDateDesc();
}