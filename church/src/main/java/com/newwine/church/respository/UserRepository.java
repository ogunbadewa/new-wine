package com.newwine.church.respository;

import com.newwine.church.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by username
    Optional<User> findByUsername(String username);
    
    // Find user by username (case insensitive)
    Optional<User> findByUsernameIgnoreCase(String username);
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Find all active users
    List<User> findByIsActiveTrue();
    
    // Find users by role
    List<User> findByRole(User.Role role);
    
    // Find active users by role
    List<User> findByRoleAndIsActiveTrue(User.Role role);
    
    // Check if username exists
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    boolean existsByUsernameIgnoreCase(@Param("username") String username);
    
    // Check if email exists
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);
    
    // Find users who logged in after a certain date
    List<User> findByLastLoginAfter(LocalDateTime date);
    
    // Count active users
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    Long countActiveUsers();
    
    // Count users by role
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") User.Role role);
    
    // Find users created after a certain date
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    // Find users who haven't logged in for a certain period
    @Query("SELECT u FROM User u WHERE u.lastLogin < :date OR u.lastLogin IS NULL")
    List<User> findInactiveUsers(@Param("date") LocalDateTime date);
}
