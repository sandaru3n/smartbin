package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.dto.LoginDTO;
import com.sliit.smartbin.smartbin.dto.UserDTO;
import com.sliit.smartbin.smartbin.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(UserDTO userDTO);
    Optional<User> loginUser(LoginDTO loginDTO, User.UserRole role);
    Optional<User> findByEmail(String email);
    List<User> getAllUsers();
    boolean emailExists(String email);
}

