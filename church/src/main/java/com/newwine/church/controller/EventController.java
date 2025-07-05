package com.newwine.church.controller;

import com.newwine.church.dto.response.ApiResponse;
import com.newwine.church.dto.response.EventResponse;
import com.newwine.church.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:5500"})
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService eventService;

    /**
     * Get all active events
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getActiveEvents() {
        logger.info("Fetching active events");
        
        try {
            List<EventResponse> events = eventService.getActiveEvents();
            
            ApiResponse<List<EventResponse>> response = ApiResponse.success(
                "Active events retrieved successfully", 
                events
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching active events", e);
            
            ApiResponse<List<EventResponse>> response = ApiResponse.error(
                "Error fetching active events", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get upcoming events
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getUpcomingEvents() {
        logger.info("Fetching upcoming events");
        
        try {
            List<EventResponse> events = eventService.getUpcomingEvents();
            
            ApiResponse<List<EventResponse>> response = ApiResponse.success(
                "Upcoming events retrieved successfully", 
                events
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching upcoming events", e);
            
            ApiResponse<List<EventResponse>> response = ApiResponse.error(
                "Error fetching upcoming events", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get event by name
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/name/{eventName}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventByName(@PathVariable String eventName) {
        logger.info("Fetching event by name: {}", eventName);
        
        try {
            EventResponse event = eventService.getEventByName(eventName);
            
            ApiResponse<EventResponse> response = ApiResponse.success(
                "Event retrieved successfully", 
                event
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching event by name: {}", eventName, e);
            
            if (e.getMessage().contains("not found")) {
                ApiResponse<EventResponse> response = ApiResponse.error(
                    "Event not found with name: " + eventName, 
                    e.getMessage()
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            ApiResponse<EventResponse> response = ApiResponse.error(
                "Error fetching event", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get events by type
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/type/{eventType}")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEventsByType(@PathVariable String eventType) {
        logger.info("Fetching events by type: {}", eventType);
        
        try {
            List<EventResponse> events = eventService.getEventsByType(eventType);
            
            ApiResponse<List<EventResponse>> response = ApiResponse.success(
                "Events retrieved successfully", 
                events
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching events by type: {}", eventType, e);
            
            ApiResponse<List<EventResponse>> response = ApiResponse.error(
                "Error fetching events by type", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Search events
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EventResponse>>> searchEvents(@RequestParam String query) {
        logger.info("Searching events with query: {}", query);
        
        try {
            List<EventResponse> events = eventService.searchEvents(query);
            
            ApiResponse<List<EventResponse>> response = ApiResponse.success(
                "Search results retrieved successfully", 
                events
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error searching events with query: {}", query, e);
            
            ApiResponse<List<EventResponse>> response = ApiResponse.error(
                "Error searching events", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get events with available capacity
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEventsWithAvailableCapacity() {
        logger.info("Fetching events with available capacity");
        
        try {
            List<EventResponse> events = eventService.getEventsWithAvailableCapacity();
            
            ApiResponse<List<EventResponse>> response = ApiResponse.success(
                "Events with available capacity retrieved successfully", 
                events
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching events with available capacity", e);
            
            ApiResponse<List<EventResponse>> response = ApiResponse.error(
                "Error fetching events with available capacity", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get public event statistics
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getEventStats() {
        logger.info("Fetching public event statistics");
        
        try {
            Long totalEvent = eventService.getEventCount();
            Long activeEvent = eventService.getActiveEventCount();
            Long upcomingEvent = eventService.getUpcomingEventCount();
            
            Object stats = new Object() {
                public final Long totalEvents = totalEvent;
                public final Long activeEvents = activeEvent;
                public final Long upcomingEvents = upcomingEvent;
                public final String message = "Join us for exciting upcoming events!";
            };
            
            ApiResponse<Object> response = ApiResponse.success("Event statistics", stats);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching event statistics", e);
            
            ApiResponse<Object> response = ApiResponse.error(
                "Error fetching event statistics", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Health check endpoint for event service
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        logger.info("Event service health check");
        
        try {
            Long eventCount = eventService.getEventCount();
            String message = String.format("Event service is running. Total events: %d", eventCount);
            
            ApiResponse<String> response = ApiResponse.success(message, "OK");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Event service health check failed", e);
            
            ApiResponse<String> response = ApiResponse.error(
                "Event service health check failed", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Check if event exists
     * PUBLIC ENDPOINT - No authentication required
     */
    @GetMapping("/exists/{NameOfEvent}")
    public ResponseEntity<ApiResponse<Object>> checkEventExists(@PathVariable String NameOfEvent) {
        logger.info("Checking if event exists: {}", NameOfEvent);
        
        try {
            boolean exist = eventService.eventExists(NameOfEvent);
            
            Object result = new Object() {
                public final boolean exists = exist;
                public final String eventName = NameOfEvent;
                public final String message = exists ? 
                    "Event exists and you can register for it." : 
                    "Event not found.";
            };
            
            ApiResponse<Object> response = ApiResponse.success("Event existence check completed", result);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error checking event existence", e);
            
            ApiResponse<Object> response = ApiResponse.error(
                "Error checking event existence", 
                e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}