// EventResponse.java
package com.newwine.church.dto.response;

import com.newwine.church.entity.Event;

import java.time.LocalDateTime;

public class EventResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private Boolean active;
    private Integer maxCapacity;
    private Integer currentRegistrations;
    private String eventType;
    private String additionalInfo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EventResponse() {}

    public EventResponse(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.eventDate = event.getEventDate();
        this.location = event.getLocation();
        this.active = event.getIsActive();
        this.maxCapacity = event.getMaxCapacity();
        this.currentRegistrations = event.getCurrentRegistrations();
        this.eventType = event.getEventType();
        this.additionalInfo = event.getAdditionalInfo();
        this.createdAt = event.getCreatedAt();
        this.updatedAt = event.getUpdatedAt();
    }

    public static EventResponse fromEntity(Event event) {
        return new EventResponse(event);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }
    public Integer getCurrentRegistrations() { return currentRegistrations; }
    public void setCurrentRegistrations(Integer currentRegistrations) { this.currentRegistrations = currentRegistrations; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

// LoginResponse.java


// StatsResponse.java
