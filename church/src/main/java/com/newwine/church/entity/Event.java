package com.newwine.church.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@EntityListeners(AuditingEntityListener.class)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Event name is required")
    @Size(max = 100, message = "Event name must not exceed 100 characters")
    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(length = 1000)
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Size(max = 200, message = "Location must not exceed 200 characters")
    @Column(length = 200)
    private String location;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "current_registrations")
    private Integer currentRegistrations = 0;

    @Size(max = 50, message = "Event type must not exceed 50 characters")
    @Column(name = "event_type", length = 50)
    private String eventType;

    @Size(max = 500, message = "Additional info must not exceed 500 characters")
    @Column(name = "additional_info", length = 500)
    private String additionalInfo;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Event() {}

    public Event(String name, String description, LocalDateTime eventDate, String location) {
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getCurrentRegistrations() {
        return currentRegistrations;
    }

    public void setCurrentRegistrations(Integer currentRegistrations) {
        this.currentRegistrations = currentRegistrations;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", eventDate=" + eventDate +
                ", location='" + location + '\'' +
                ", isActive=" + isActive +
                ", maxCapacity=" + maxCapacity +
                ", currentRegistrations=" + currentRegistrations +
                ", eventType='" + eventType + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
