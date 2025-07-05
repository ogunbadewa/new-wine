package com.newwine.church.dto.response;

import com.newwine.church.entity.Registration;

import java.time.LocalDateTime;

public class RegistrationResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String emergencyContact;
    private String eventName;
    private String specialRequests;
    private LocalDateTime registrationDate;
    private LocalDateTime updatedAt;

    // Constructors
    public RegistrationResponse() {}

    public RegistrationResponse(Registration registration) {
        this.id = registration.getId();
        this.fullName = registration.getFullName();
        this.email = registration.getEmail();
        this.phone = registration.getPhone();
        this.emergencyContact = registration.getEmergencyContact();
        this.eventName = registration.getEventName();
        this.specialRequests = registration.getSpecialRequests();
        this.registrationDate = registration.getRegistrationDate();
        this.updatedAt = registration.getUpdatedAt();
    }

    // Static factory method
    public static RegistrationResponse fromEntity(Registration registration) {
        return new RegistrationResponse(registration);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "RegistrationResponse{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", emergencyContact='" + emergencyContact + '\'' +
                ", eventName='" + eventName + '\'' +
                ", specialRequests='" + specialRequests + '\'' +
                ", registrationDate=" + registrationDate +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
