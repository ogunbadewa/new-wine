package com.newwine.church.service;

import com.newwine.church.dto.response.ContactResponse;
import com.newwine.church.dto.response.RegistrationResponse;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ContactService contactService;

    @Autowired
    private RegistrationService registrationService;

    public byte[] exportAllDataToCsv() throws IOException {
        logger.info("Exporting all data to CSV");
        
        // Get data before the try block
        List<ContactResponse> contacts = contactService.getAllContacts();
        List<RegistrationResponse> registrations = registrationService.getAllRegistrations();
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamWriter osw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {
            
            // Write headers
            String[] headers = {
                "Type", "ID", "Name/Full Name", "Email", "Phone", "Subject/Event Name", 
                "Message/Emergency Contact", "Special Requests", "Source", "Request Type", 
                "Submission/Registration Date", "Updated Date"
            };
            writer.writeNext(headers);
            
            // Write contact data
            for (ContactResponse contact : contacts) {
                String[] row = {
                    "Contact Form",
                    contact.getId().toString(),
                    contact.getName(),
                    contact.getEmail(),
                    contact.getPhone() != null ? contact.getPhone() : "",
                    contact.getSubject() != null ? contact.getSubject() : "",
                    contact.getMessage() != null ? contact.getMessage() : "",
                    "", // Special requests (not applicable for contacts)
                    contact.getSource() != null ? contact.getSource() : "",
                    contact.getRequestType() != null ? contact.getRequestType() : "",
                    contact.getSubmissionDate() != null ? contact.getSubmissionDate().format(DATE_FORMATTER) : "",
                    contact.getUpdatedAt() != null ? contact.getUpdatedAt().format(DATE_FORMATTER) : ""
                };
                writer.writeNext(row);
            }
            
            // Write registration data
            for (RegistrationResponse registration : registrations) {
                String[] row = {
                    "Event Registration",
                    registration.getId().toString(),
                    registration.getFullName(),
                    registration.getEmail(),
                    registration.getPhone() != null ? registration.getPhone() : "",
                    registration.getEventName() != null ? registration.getEventName() : "",
                    registration.getEmergencyContact() != null ? registration.getEmergencyContact() : "",
                    registration.getSpecialRequests() != null ? registration.getSpecialRequests() : "",
                    "Website", // Default source for registrations
                    "Event Registration", // Default request type for registrations
                    registration.getRegistrationDate() != null ? registration.getRegistrationDate().format(DATE_FORMATTER) : "",
                    registration.getUpdatedAt() != null ? registration.getUpdatedAt().format(DATE_FORMATTER) : ""
                };
                writer.writeNext(row);
            }
            
            writer.flush();
        }
        
        byte[] csvData = outputStream.toByteArray();
        logger.info("Successfully exported {} contacts and {} registrations to CSV", 
                   contacts.size(), registrations.size());
        
        return csvData;
    }

    public byte[] exportContactsToCsv() throws IOException {
        logger.info("Exporting contacts to CSV");
        
        // Get data before the try block
        List<ContactResponse> contacts = contactService.getAllContacts();
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamWriter osw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {
            
            // Write headers
            String[] headers = {
                "ID", "Name", "Email", "Phone", "Subject", "Message", 
                "Source", "Request Type", "Submission Date", "Updated Date"
            };
            writer.writeNext(headers);
            
            // Write contact data
            for (ContactResponse contact : contacts) {
                String[] row = {
                    contact.getId().toString(),
                    contact.getName(),
                    contact.getEmail(),
                    contact.getPhone() != null ? contact.getPhone() : "",
                    contact.getSubject() != null ? contact.getSubject() : "",
                    contact.getMessage() != null ? contact.getMessage() : "",
                    contact.getSource() != null ? contact.getSource() : "",
                    contact.getRequestType() != null ? contact.getRequestType() : "",
                    contact.getSubmissionDate() != null ? contact.getSubmissionDate().format(DATE_FORMATTER) : "",
                    contact.getUpdatedAt() != null ? contact.getUpdatedAt().format(DATE_FORMATTER) : ""
                };
                writer.writeNext(row);
            }
            
            writer.flush();
        }
        
        byte[] csvData = outputStream.toByteArray();
        logger.info("Successfully exported {} contacts to CSV", contacts.size());
        
        return csvData;
    }

    public byte[] exportRegistrationsToCsv() throws IOException {
        logger.info("Exporting registrations to CSV");
        
        // Get data before the try block
        List<RegistrationResponse> registrations = registrationService.getAllRegistrations();
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamWriter osw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {
            
            // Write headers
            String[] headers = {
                "ID", "Full Name", "Email", "Phone", "Event Name", "Emergency Contact", 
                "Special Requests", "Registration Date", "Updated Date"
            };
            writer.writeNext(headers);
            
            // Write registration data
            for (RegistrationResponse registration : registrations) {
                String[] row = {
                    registration.getId().toString(),
                    registration.getFullName(),
                    registration.getEmail(),
                    registration.getPhone() != null ? registration.getPhone() : "",
                    registration.getEventName() != null ? registration.getEventName() : "",
                    registration.getEmergencyContact() != null ? registration.getEmergencyContact() : "",
                    registration.getSpecialRequests() != null ? registration.getSpecialRequests() : "",
                    registration.getRegistrationDate() != null ? registration.getRegistrationDate().format(DATE_FORMATTER) : "",
                    registration.getUpdatedAt() != null ? registration.getUpdatedAt().format(DATE_FORMATTER) : ""
                };
                writer.writeNext(row);
            }
            
            writer.flush();
        }
        
        byte[] csvData = outputStream.toByteArray();
        logger.info("Successfully exported {} registrations to CSV", registrations.size());
        
        return csvData;
    }

    public byte[] exportRegistrationsByEventToCsv(String eventName) throws IOException {
        logger.info("Exporting registrations for event '{}' to CSV", eventName);
        
        // Get data before the try block
        List<RegistrationResponse> registrations = registrationService.getRegistrationsByEvent(eventName);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamWriter osw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {
            
            // Write headers
            String[] headers = {
                "ID", "Full Name", "Email", "Phone", "Event Name", "Emergency Contact", 
                "Special Requests", "Registration Date", "Updated Date"
            };
            writer.writeNext(headers);
            
            // Write registration data for specific event
            for (RegistrationResponse registration : registrations) {
                String[] row = {
                    registration.getId().toString(),
                    registration.getFullName(),
                    registration.getEmail(),
                    registration.getPhone() != null ? registration.getPhone() : "",
                    registration.getEventName() != null ? registration.getEventName() : "",
                    registration.getEmergencyContact() != null ? registration.getEmergencyContact() : "",
                    registration.getSpecialRequests() != null ? registration.getSpecialRequests() : "",
                    registration.getRegistrationDate() != null ? registration.getRegistrationDate().format(DATE_FORMATTER) : "",
                    registration.getUpdatedAt() != null ? registration.getUpdatedAt().format(DATE_FORMATTER) : ""
                };
                writer.writeNext(row);
            }
            
            writer.flush();
        }
        
        byte[] csvData = outputStream.toByteArray();
        logger.info("Successfully exported {} registrations for event '{}' to CSV", 
                   registrations.size(), eventName);
        
        return csvData;
    }

    public byte[] exportContactsBySubjectToCsv(String subject) throws IOException {
        logger.info("Exporting contacts with subject '{}' to CSV", subject);
        
        // Get data before the try block
        List<ContactResponse> contacts = contactService.getContactsBySubject(subject);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamWriter osw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {
            
            // Write headers
            String[] headers = {
                "ID", "Name", "Email", "Phone", "Subject", "Message", 
                "Source", "Request Type", "Submission Date", "Updated Date"
            };
            writer.writeNext(headers);
            
            // Write contact data for specific subject
            for (ContactResponse contact : contacts) {
                String[] row = {
                    contact.getId().toString(),
                    contact.getName(),
                    contact.getEmail(),
                    contact.getPhone() != null ? contact.getPhone() : "",
                    contact.getSubject() != null ? contact.getSubject() : "",
                    contact.getMessage() != null ? contact.getMessage() : "",
                    contact.getSource() != null ? contact.getSource() : "",
                    contact.getRequestType() != null ? contact.getRequestType() : "",
                    contact.getSubmissionDate() != null ? contact.getSubmissionDate().format(DATE_FORMATTER) : "",
                    contact.getUpdatedAt() != null ? contact.getUpdatedAt().format(DATE_FORMATTER) : ""
                };
                writer.writeNext(row);
            }
            
            writer.flush();
        }
        
        byte[] csvData = outputStream.toByteArray();
        logger.info("Successfully exported {} contacts with subject '{}' to CSV", 
                   contacts.size(), subject);
        
        return csvData;
    }

    public byte[] exportEmailListToCsv() throws IOException {
        logger.info("Exporting email list to CSV");
        
        // Collect unique emails from both contacts and registrations BEFORE the try block
        List<String[]> emailList = new ArrayList<>();
        
        // Add contact emails
        List<ContactResponse> contacts = contactService.getAllContacts();
        for (ContactResponse contact : contacts) {
            String[] row = {
                contact.getEmail(),
                contact.getName(),
                "Contact Form",
                contact.getSubmissionDate() != null ? contact.getSubmissionDate().format(DATE_FORMATTER) : ""
            };
            emailList.add(row);
        }
        
        // Add registration emails
        List<RegistrationResponse> registrations = registrationService.getAllRegistrations();
        for (RegistrationResponse registration : registrations) {
            String[] row = {
                registration.getEmail(),
                registration.getFullName(),
                "Event Registration",
                registration.getRegistrationDate() != null ? registration.getRegistrationDate().format(DATE_FORMATTER) : ""
            };
            emailList.add(row);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamWriter osw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {
            
            // Write headers
            String[] headers = {"Email", "Name", "Source", "Date Added"};
            writer.writeNext(headers);
            
            // Write all email data
            for (String[] row : emailList) {
                writer.writeNext(row);
            }
            
            writer.flush();
        }
        
        byte[] csvData = outputStream.toByteArray();
        logger.info("Successfully exported email list to CSV with {} entries", emailList.size());
        
        return csvData;
    }
}