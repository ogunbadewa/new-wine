package com.newwine.church.dto.response;

import com.newwine.church.entity.Contact;

import java.time.LocalDateTime;

public class ContactResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String subject;
    private String message;
    private String source;
    private String requestType;
    private LocalDateTime submissionDate;
    private LocalDateTime updatedAt;

    // Constructors
    public ContactResponse() {}

    public ContactResponse(Contact contact) {
        this.id = contact.getId();
        this.name = contact.getName();
        this.email = contact.getEmail();
        this.phone = contact.getPhone();
        this.subject = contact.getSubject();
        this.message = contact.getMessage();
        this.source = contact.getSource();
        this.requestType = contact.getRequestType();
        this.submissionDate = contact.getSubmissionDate();
        this.updatedAt = contact.getUpdatedAt();
    }

    // Static factory method
    public static ContactResponse fromEntity(Contact contact) {
        return new ContactResponse(contact);
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ContactResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                ", source='" + source + '\'' +
                ", requestType='" + requestType + '\'' +
                ", submissionDate=" + submissionDate +
                ", updatedAt=" + updatedAt +
                '}';
    }
}