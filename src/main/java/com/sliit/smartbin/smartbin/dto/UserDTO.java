package com.sliit.smartbin.smartbin.dto;

import com.sliit.smartbin.smartbin.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private User.UserRole role;
}

