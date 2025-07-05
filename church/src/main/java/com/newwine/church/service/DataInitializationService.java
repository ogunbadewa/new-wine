package com.newwine.church.service;

import com.newwine.church.entity.Event;
import com.newwine.church.entity.User;
import com.newwine.church.respository.EventRepository;
import com.newwine.church.respository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DataInitializationService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Starting data initialization...");
        
        try {
            initializeDefaultAdmin();
            initializeDefaultEvents();
            logger.info("Data initialization completed successfully");
        } catch (Exception e) {
            logger.error("Error during data initialization", e);
            throw e;
        }
    }

    private void initializeDefaultAdmin() {
        logger.info("Checking for default admin user...");
        
        Optional<User> existingAdmin = userRepository.findByUsername("admin");
        if (existingAdmin.isEmpty()) {
            logger.info("Creating default admin user...");
            
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // Change this in production!
            admin.setFullName("New Wine Administrator");
            admin.setEmail("admin@newwinechurch.com");
            admin.setRole(User.Role.ADMIN);
            admin.setIsActive(true);
            
            userRepository.save(admin);
            logger.info("Default admin user created successfully");
            logger.warn("⚠️  DEFAULT ADMIN CREDENTIALS - Username: admin, Password: admin123");
            logger.warn("⚠️  PLEASE CHANGE THE DEFAULT PASSWORD IN PRODUCTION!");
        } else {
            logger.info("Default admin user already exists");
        }

        // Create super admin if it doesn't exist
        Optional<User> existingSuperAdmin = userRepository.findByUsername("superadmin");
        if (existingSuperAdmin.isEmpty()) {
            logger.info("Creating super admin user...");
            
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(passwordEncoder.encode("superadmin123")); // Change this in production!
            superAdmin.setFullName("New Wine Super Administrator");
            superAdmin.setEmail("superadmin@newwinechurch.com");
            superAdmin.setRole(User.Role.SUPER_ADMIN);
            superAdmin.setIsActive(true);
            
            userRepository.save(superAdmin);
            logger.info("Super admin user created successfully");
            logger.warn("⚠️  SUPER ADMIN CREDENTIALS - Username: superadmin, Password: superadmin123");
            logger.warn("⚠️  PLEASE CHANGE THE DEFAULT PASSWORD IN PRODUCTION!");
        } else {
            logger.info("Super admin user already exists");
        }
    }

    private void initializeDefaultEvents() {
        logger.info("Checking for default events...");
        
        // Create IMMERSE NIGHT 2025 event
        if (!eventRepository.existsByNameIgnoreCase("IMMERSE NIGHT 2025")) {
            logger.info("Creating IMMERSE NIGHT 2025 event...");
            
            Event immerseNight = new Event();
            immerseNight.setName("IMMERSE NIGHT 2025");
            immerseNight.setDescription("An evening of intense prayer, intense worship, miracles, and praise. Come expecting God to move in powerful ways.");
            immerseNight.setEventDate(LocalDateTime.of(2025, 8, 10, 17, 0)); // August 10, 2025 at 5:00 PM
            immerseNight.setLocation("New Wine Church - Main Sanctuary");
            immerseNight.setEventType("Special Event");
            immerseNight.setMaxCapacity(500);
            immerseNight.setCurrentRegistrations(0);
            immerseNight.setIsActive(true);
            immerseNight.setAdditionalInfo("Free dinner and refreshments provided. Come expecting an encounter with God.");
            
            eventRepository.save(immerseNight);
            logger.info("IMMERSE NIGHT 2025 event created successfully");
        }

        // Create Summer Retreat 2025 event
        if (!eventRepository.existsByNameIgnoreCase("Summer Retreat 2025")) {
            logger.info("Creating Summer Retreat 2025 event...");
            
            Event summerRetreat = new Event();
            summerRetreat.setName("Summer Retreat 2025");
            summerRetreat.setDescription("A transformative week away from distractions to focus on God, build relationships, and find renewal in beautiful Colorado mountains.");
            summerRetreat.setEventDate(LocalDateTime.of(2025, 7, 15, 9, 0)); // July 15, 2025 at 9:00 AM
            summerRetreat.setLocation("Rocky Mountain Retreat Center, Colorado");
            summerRetreat.setEventType("Retreat");
            summerRetreat.setMaxCapacity(150);
            summerRetreat.setCurrentRegistrations(0);
            summerRetreat.setIsActive(true);
            summerRetreat.setAdditionalInfo("5-day retreat including accommodation, meals, and activities. Registration fee applies.");
            
            eventRepository.save(summerRetreat);
            logger.info("Summer Retreat 2025 event created successfully");
        }

        // Create Worship Night event
        if (!eventRepository.existsByNameIgnoreCase("Worship Night")) {
            logger.info("Creating Worship Night event...");
            
            Event worshipNight = new Event();
            worshipNight.setName("Worship Night");
            worshipNight.setDescription("Intimate evenings of worship and prayer where we come together to encounter God's presence and connect as a community.");
            worshipNight.setEventDate(LocalDateTime.of(2025, 7, 4, 19, 0)); // July 4, 2025 at 7:00 PM (First Friday)
            worshipNight.setLocation("New Wine Church - Main Sanctuary");
            worshipNight.setEventType("Monthly Event");
            worshipNight.setMaxCapacity(300);
            worshipNight.setCurrentRegistrations(0);
            worshipNight.setIsActive(true);
            worshipNight.setAdditionalInfo("First Friday of every month. Light refreshments provided.");
            
            eventRepository.save(worshipNight);
            logger.info("Worship Night event created successfully");
        }

        // Create Small Groups Launch event
        if (!eventRepository.existsByNameIgnoreCase("Small Groups Launch")) {
            logger.info("Creating Small Groups Launch event...");
            
            Event smallGroupsLaunch = new Event();
            smallGroupsLaunch.setName("Small Groups Launch");
            smallGroupsLaunch.setDescription("Join a small group and build lasting friendships while growing in faith. Groups for all ages and life stages.");
            smallGroupsLaunch.setEventDate(LocalDateTime.of(2025, 9, 5, 19, 0)); // September 5, 2025 at 7:00 PM
            smallGroupsLaunch.setLocation("New Wine Church - Various Rooms");
            smallGroupsLaunch.setEventType("Ministry Launch");
            smallGroupsLaunch.setMaxCapacity(200);
            smallGroupsLaunch.setCurrentRegistrations(0);
            smallGroupsLaunch.setIsActive(true);
            smallGroupsLaunch.setAdditionalInfo("Meet group leaders and sign up for small groups starting this fall.");
            
            eventRepository.save(smallGroupsLaunch);
            logger.info("Small Groups Launch event created successfully");
        }

        logger.info("Default events initialization completed");
    }

    @Transactional
    public void createTestData() {
        logger.info("Creating test data for development...");
        
        // This method can be called separately to create test data
        // You can add sample contacts and registrations here for testing
        
        logger.info("Test data creation completed");
    }

    @Transactional
    public void resetAdminPassword(String username, String newPassword) {
        logger.info("Resetting password for admin user: {}", username);
        
        Optional<User> adminUser = userRepository.findByUsername(username);
        if (adminUser.isPresent()) {
            User user = adminUser.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            logger.info("Password reset successfully for user: {}", username);
        } else {
            logger.error("Admin user not found: {}", username);
            throw new IllegalArgumentException("Admin user not found: " + username);
        }
    }

    @Transactional
    public void createAdminUser(String username, String password, String fullName, String email) {
        logger.info("Creating new admin user: {}", username);
        
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        
        if (email != null && userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        
        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setFullName(fullName);
        admin.setEmail(email);
        admin.setRole(User.Role.ADMIN);
        admin.setIsActive(true);
        
        userRepository.save(admin);
        logger.info("Admin user created successfully: {}", username);
    }
}