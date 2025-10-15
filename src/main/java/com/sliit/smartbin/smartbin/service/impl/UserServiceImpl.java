package com.sliit.smartbin.smartbin.service.impl;

import com.sliit.smartbin.smartbin.dto.LoginDTO;
import com.sliit.smartbin.smartbin.dto.UserDTO;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.repository.UserRepository;
import com.sliit.smartbin.smartbin.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserService following SOLID principles
 * Single Responsibility: Handles only user-related operations
 * Open/Closed: Can be extended without modification
 * Liskov Substitution: Implements UserService interface correctly
 * Interface Segregation: Implements focused interface
 * Dependency Inversion: Depends on UserRepository abstraction
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByRole(User.UserRole role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == role)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByRegion(String region) {
        return userRepository.findAll().stream()
                .filter(user -> region.equals(user.getRegion()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> loginUser(LoginDTO loginDTO, User.UserRole role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(loginDTO.getEmail()))
                .filter(user -> user.getRole() == role)
                .filter(user -> passwordEncoder.matches(loginDTO.getPassword(), user.getPassword()))
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public User registerUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        user.setRole(userDTO.getRole());
        user.setRegion(userDTO.getRegion());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
}