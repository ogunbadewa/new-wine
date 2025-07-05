package com.newwine.church.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @Size(max = 200, message = "Emergency contact must not exceed 200 characters")
    private String emergencyContact;

    @NotBlank(message = "Event name is required")
    @Size(max = 100, message = "Event name must not exceed 100 characters")
    private String eventName;

    @Size(max = 1000, message = "Special requests must not exceed 1000 characters")
    private String specialRequests;

    private String registrationDate;

    // Constructors
    public RegistrationRequest() {}

    public RegistrationRequest(String fullName, String email, String phone, String emergencyContact, 
                              String eventName, String specialRequests) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.emergencyContact = emergencyContact;
        this.eventName = eventName;
        this.specialRequests = specialRequests;
    }

    // Getters and Setters
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

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", emergencyContact='" + emergencyContact + '\'' +
                ", eventName='" + eventName + '\'' +
                ", specialRequests='" + specialRequests + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                '}';
    }
}
