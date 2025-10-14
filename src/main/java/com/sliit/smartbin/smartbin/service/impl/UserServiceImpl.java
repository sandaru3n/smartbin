package com.sliit.smartbin.smartbin.service.impl;

import com.sliit.smartbin.smartbin.dto.LoginDTO;
import com.sliit.smartbin.smartbin.dto.UserDTO;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.repository.UserRepository;
import com.sliit.smartbin.smartbin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User registerUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        user.setRole(userDTO.getRole());
        
        return userRepository.save(user);
    }

    @Override
    public Optional<User> loginUser(LoginDTO loginDTO, User.UserRole role) {
        Optional<User> userOptional = userRepository.findByEmailAndRole(loginDTO.getEmail(), role);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}

