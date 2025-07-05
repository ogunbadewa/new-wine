package com.newwine.church.service;

import com.newwine.church.dto.request.LoginRequest;
import com.newwine.church.dto.response.LoginResponse;
import com.newwine.church.entity.User;
import com.newwine.church.respository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class AuthService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private final AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    public AuthService(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User account is deactivated: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    @Transactional
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        logger.info("Attempting to authenticate user: {}", loginRequest.getUsername());

        try {
            // Lazy load AuthenticationManager at runtime
            AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Set authentication context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Load user to get role and userId for token generation
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (user.getRole() == null) {
                throw new IllegalStateException("Cannot generate token: user has no role assigned!");
            }

            // Generate JWT token with claims
            String jwt = jwtService.generateToken(
                    user.getUsername(),
                    user.getRole().name(),
                    user.getId()
            );

            // Update last login timestamp
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            logger.info("User authenticated successfully: {}", loginRequest.getUsername());

            return new LoginResponse(
                    true,
                    "Login successful",
                    jwt,
                    new LoginResponse.UserInfo(user)
            );

        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", loginRequest.getUsername(), e);
            return new LoginResponse(false, "Invalid username or password", null, null);
        }
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));
        }
        return null;
    }

    @Transactional
    public User createUser(String username, String password, String fullName, String email, User.Role role) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (email != null && userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);
        logger.info("User created successfully: {}", username);
        return savedUser;
    }

    @Transactional
    public User updateUser(Long userId, String fullName, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (fullName != null) {
            user.setFullName(fullName);
        }

        if (email != null) {
            userRepository.findByEmail(email).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(userId)) {
                    throw new IllegalArgumentException("Email already exists");
                }
            });
            user.setEmail(email);
        }

        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        logger.info("Password changed successfully for user: {}", user.getUsername());
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setIsActive(false);
        userRepository.save(user);
        logger.info("User deactivated: {}", user.getUsername());
    }

    @Transactional
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setIsActive(true);
        userRepository.save(user);
        logger.info("User activated: {}", user.getUsername());
    }

    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }

    public String getUsernameFromToken(String token) {
        return jwtService.getUsernameFromToken(token);
    }
}
