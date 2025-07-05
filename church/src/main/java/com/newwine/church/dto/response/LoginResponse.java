package com.newwine.church.dto.response;

import com.newwine.church.entity.User;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private UserInfo user;

    public LoginResponse() {}

    public LoginResponse(boolean success, String message, String token, UserInfo user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
    }

    public static class UserInfo {
        private Long id;
        private String username;
        private String role;
        private String fullName;
        private String email;

        public UserInfo() {}

        public UserInfo(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.role = user.getRole().name();
            this.fullName = user.getFullName();
            this.email = user.getEmail();
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }
}
