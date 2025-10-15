package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.dto.LoginDTO;
import com.sliit.smartbin.smartbin.dto.UserDTO;
import com.sliit.smartbin.smartbin.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for User operations following SOLID principles
 * Single Responsibility: Handles only user-related operations
 * Open/Closed: Can be extended without modification
 * Liskov Substitution: Implementations can be substituted
 * Interface Segregation: Focused interface for user operations
 * Dependency Inversion: Depends on abstractions
 */
public interface UserService {
    
    /**
     * Find user by ID
     * @param id User ID
     * @return Optional containing user if found
     */
    Optional<User> findById(Long id);
    
    /**
     * Find users by role
     * @param role User role
     * @return List of users with specified role
     */
    List<User> findByRole(User.UserRole role);
    
    /**
     * Find user by email
     * @param email User email
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Save or update user
     * @param user User entity
     * @return Saved user entity
     */
    User saveUser(User user);
    
    /**
     * Update user
     * @param user User entity
     * @return Updated user entity
     */
    User updateUser(User user);
    
    /**
     * Delete user by ID
     * @param id User ID
     */
    void deleteUser(Long id);
    
    /**
     * Find all users
     * @return List of all users
     */
    List<User> findAllUsers();
    
    /**
     * Check if user exists by email
     * @param email User email
     * @return true if user exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find users by region
     * @param region Region name
     * @return List of users in specified region
     */
    List<User> findByRegion(String region);
    
    /**
     * Login user with email and password
     * @param loginDTO Login credentials
     * @param role Expected user role
     * @return Optional containing user if login successful
     */
    Optional<User> loginUser(LoginDTO loginDTO, User.UserRole role);
    
    /**
     * Check if email exists in the system
     * @param email Email to check
     * @return true if email exists
     */
    boolean emailExists(String email);
    
    /**
     * Register a new user
     * @param userDTO User data transfer object
     * @return Registered user entity
     */
    User registerUser(UserDTO userDTO);
}