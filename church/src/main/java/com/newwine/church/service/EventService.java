package com.newwine.church.service;

import com.newwine.church.dto.response.EventResponse;
import com.newwine.church.entity.Event;
import com.newwine.church.exception.ResourceNotFoundException;
import com.newwine.church.exception.ValidationException;
import com.newwine.church.respository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        logger.info("Fetching all events");
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getActiveEvents() {
        logger.info("Fetching active events");
        List<Event> events = eventRepository.findByIsActiveTrueOrderByEventDateAsc();
        return events.stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getUpcomingEvents() {
        logger.info("Fetching upcoming events");
        List<Event> events = eventRepository.findUpcomingEvents(LocalDateTime.now());
        return events.stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getPastEvents() {
        logger.info("Fetching past events");
        List<Event> events = eventRepository.findPastEvents(LocalDateTime.now());
        return events.stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        logger.info("Fetching event with ID: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));
        return EventResponse.fromEntity(event);
    }

    @Transactional(readOnly = true)
    public EventResponse getEventByName(String name) {
        logger.info("Fetching event with name: {}", name);
        Event event = eventRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with name: " + name));
        return EventResponse.fromEntity(event);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByType(String eventType) {
        logger.info("Fetching events with type: {}", eventType);
        List<Event> events = eventRepository.findByEventType(eventType);
        return events.stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByLocation(String location) {
        logger.info("Fetching events at location: {}", location);
        List<Event> events = eventRepository.findByLocation(location);
        return events.stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEventsWithAvailableCapacity() {
        logger.info("Fetching events with available capacity");
        List<Event> events = eventRepository.findEventsWithAvailableCapacity();
        return events.stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getFullEvents() {
        logger.info("Fetching full events");
        List<Event> events = eventRepository.findFullEvents();
        return events.stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponse> searchEvents(String searchTerm) {
        logger.info("Searching events with term: {}", searchTerm);
        List<Event> events = eventRepository.findByNameContainingIgnoreCase(searchTerm);
        return events.stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getEventCount() {
        return eventRepository.count();
    }

    @Transactional(readOnly = true)
    public Long getActiveEventCount() {
        return eventRepository.countActiveEvents();
    }

    @Transactional(readOnly = true)
    public Long getUpcomingEventCount() {
        return eventRepository.countUpcomingEvents(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public boolean eventExists(String name) {
        return eventRepository.existsByNameIgnoreCase(name);
    }

    @Transactional
    public EventResponse createEvent(String name, String description, LocalDateTime eventDate, 
                                   String location, String eventType, Integer maxCapacity, 
                                   String additionalInfo) {
        logger.info("Creating new event: {}", name);

        // Validate input
        validateEventData(name, description, eventDate, location);

        // Check if event name already exists
        if (eventRepository.existsByNameIgnoreCase(name)) {
            throw new ValidationException("Event with this name already exists");
        }

        Event event = new Event();
        event.setName(sanitizeInput(name));
        event.setDescription(sanitizeInput(description));
        event.setEventDate(eventDate);
        event.setLocation(sanitizeInput(location));
        event.setEventType(sanitizeInput(eventType));
        event.setMaxCapacity(maxCapacity);
        event.setAdditionalInfo(sanitizeInput(additionalInfo));
        event.setIsActive(true);
        event.setCurrentRegistrations(0);

        Event savedEvent = eventRepository.save(event);
        logger.info("Event created successfully with ID: {}", savedEvent.getId());
        
        return EventResponse.fromEntity(savedEvent);
    }

    @Transactional
    public EventResponse updateEvent(Long id, String name, String description, LocalDateTime eventDate, 
                                   String location, String eventType, Integer maxCapacity, 
                                   String additionalInfo, Boolean isActive) {
        logger.info("Updating event with ID: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));

        // Validate input if provided
        if (StringUtils.hasText(name) || StringUtils.hasText(description) || 
            eventDate != null || StringUtils.hasText(location)) {
            validateEventData(name != null ? name : event.getName(), 
                            description != null ? description : event.getDescription(),
                            eventDate != null ? eventDate : event.getEventDate(),
                            location != null ? location : event.getLocation());
        }

        // Check if new name conflicts with existing events
        if (name != null && !name.equalsIgnoreCase(event.getName()) && 
            eventRepository.existsByNameIgnoreCase(name)) {
            throw new ValidationException("Event with this name already exists");
        }

        // Update fields if provided
        if (StringUtils.hasText(name)) {
            event.setName(sanitizeInput(name));
        }
        if (StringUtils.hasText(description)) {
            event.setDescription(sanitizeInput(description));
        }
        if (eventDate != null) {
            event.setEventDate(eventDate);
        }
        if (StringUtils.hasText(location)) {
            event.setLocation(sanitizeInput(location));
        }
        if (StringUtils.hasText(eventType)) {
            event.setEventType(sanitizeInput(eventType));
        }
        if (maxCapacity != null) {
            event.setMaxCapacity(maxCapacity);
        }
        if (StringUtils.hasText(additionalInfo)) {
            event.setAdditionalInfo(sanitizeInput(additionalInfo));
        }
        if (isActive != null) {
            event.setIsActive(isActive);
        }

        Event updatedEvent = eventRepository.save(event);
        logger.info("Event updated successfully with ID: {}", updatedEvent.getId());
        
        return EventResponse.fromEntity(updatedEvent);
    }

    @Transactional
    public void deleteEvent(Long id) {
        logger.info("Deleting event with ID: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));
        
        eventRepository.delete(event);
        logger.info("Event deleted successfully with ID: {}", id);
    }

    @Transactional
    public void activateEvent(Long id) {
        logger.info("Activating event with ID: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));
        
        event.setIsActive(true);
        eventRepository.save(event);
        logger.info("Event activated successfully with ID: {}", id);
    }

    @Transactional
    public void deactivateEvent(Long id) {
        logger.info("Deactivating event with ID: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));
        
        event.setIsActive(false);
        eventRepository.save(event);
        logger.info("Event deactivated successfully with ID: {}", id);
    }

    @Transactional
    public void updateEventRegistrationCount(String eventName, int newCount) {
        logger.info("Updating registration count for event: {} to {}", eventName, newCount);
        Event event = eventRepository.findByNameIgnoreCase(eventName)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with name: " + eventName));
        
        event.setCurrentRegistrations(newCount);
        eventRepository.save(event);
        logger.info("Registration count updated successfully for event: {}", eventName);
    }

    private void validateEventData(String name, String description, LocalDateTime eventDate, String location) {
        if (!StringUtils.hasText(name)) {
            throw new ValidationException("Event name is required");
        }
        if (name.length() > 100) {
            throw new ValidationException("Event name must not exceed 100 characters");
        }
        if (description != null && description.length() > 1000) {
            throw new ValidationException("Description must not exceed 1000 characters");
        }
        if (location != null && location.length() > 200) {
            throw new ValidationException("Location must not exceed 200 characters");
        }
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().minusDays(1))) {
            throw new ValidationException("Event date cannot be in the past");
        }
    }

    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().replaceAll("<[^>]*>", "");
    }
}
