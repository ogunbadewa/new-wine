package com.newwine.church.respository;

import com.newwine.church.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    // Find event by name
    Optional<Event> findByName(String name);
    
    // Find event by name (case insensitive)
    Optional<Event> findByNameIgnoreCase(String name);
    
    // Find all active events
    List<Event> findByIsActiveTrue();
    
    // Find all active events ordered by event date
    List<Event> findByIsActiveTrueOrderByEventDateAsc();
    
    // Find events by type
    List<Event> findByEventType(String eventType);
    
    // Find events by location
    List<Event> findByLocation(String location);
    
    // Find upcoming events (event date is in the future)
    @Query("SELECT e FROM Event e WHERE e.eventDate > :now AND e.isActive = true ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEvents(@Param("now") LocalDateTime now);
    
    // Find past events (event date is in the past)
    @Query("SELECT e FROM Event e WHERE e.eventDate < :now ORDER BY e.eventDate DESC")
    List<Event> findPastEvents(@Param("now") LocalDateTime now);
    
    // Find events between dates
    List<Event> findByEventDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find events with available capacity
    @Query("SELECT e FROM Event e WHERE e.maxCapacity IS NULL OR e.currentRegistrations < e.maxCapacity")
    List<Event> findEventsWithAvailableCapacity();
    
    // Find events by name containing (case insensitive)
    List<Event> findByNameContainingIgnoreCase(String name);
    
    // Count active events
    @Query("SELECT COUNT(e) FROM Event e WHERE e.isActive = true")
    Long countActiveEvents();
    
    // Count upcoming events
    @Query("SELECT COUNT(e) FROM Event e WHERE e.eventDate > :now AND e.isActive = true")
    Long countUpcomingEvents(@Param("now") LocalDateTime now);
    
    // Find events that need registration updates
    @Query("SELECT e FROM Event e WHERE e.maxCapacity IS NOT NULL AND e.currentRegistrations >= e.maxCapacity")
    List<Event> findFullEvents();
    
    // Check if event name exists
    @Query("SELECT COUNT(e) > 0 FROM Event e WHERE LOWER(e.name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);
}