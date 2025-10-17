package com.sliit.smartbin.smartbin.controller;

import com.sliit.smartbin.smartbin.dto.LoginDTO;
import com.sliit.smartbin.smartbin.dto.UserDTO;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Signup Page
    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@ModelAttribute UserDTO userDTO, RedirectAttributes redirectAttributes) {
        try {
            if (userService.emailExists(userDTO.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Email already exists!");
                return "redirect:/signup";
            }
            
            userService.registerUser(userDTO);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            
            // Redirect to unified login page
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/signup";
        }
    }

    // Unified Login Page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // Resident Login
    @GetMapping("/resident/login")
    public String showResidentLogin(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        model.addAttribute("userType", "Resident");
        return "resident/login";
    }

    @PostMapping("/resident/login")
    public String residentLogin(@ModelAttribute LoginDTO loginDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.loginUser(loginDTO, User.UserRole.RESIDENT);
        
        if (user.isPresent()) {
            session.setAttribute("user", user.get());
            session.setAttribute("userRole", "RESIDENT");
            redirectAttributes.addFlashAttribute("success", "Welcome " + user.get().getName());
            return "redirect:/resident/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password!");
            return "redirect:/resident/login";
        }
    }

    // Collector Login
    @GetMapping("/collector/login")
    public String showCollectorLogin(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        model.addAttribute("userType", "Waste Collector");
        return "collector/login";
    }

    @PostMapping("/collector/login")
    public String collectorLogin(@ModelAttribute LoginDTO loginDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.loginUser(loginDTO, User.UserRole.COLLECTOR);
        
        if (user.isPresent()) {
            session.setAttribute("user", user.get());
            session.setAttribute("userRole", "COLLECTOR");
            redirectAttributes.addFlashAttribute("success", "Welcome " + user.get().getName());
            return "redirect:/collector/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password!");
            return "redirect:/collector/login";
        }
    }

    // Authority Login
    @GetMapping("/authority/login")
    public String showAuthorityLogin(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        model.addAttribute("userType", "Waste Management Authority");
        return "authority/login";
    }

    @PostMapping("/authority/login")
    public String authorityLogin(@ModelAttribute LoginDTO loginDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.loginUser(loginDTO, User.UserRole.AUTHORITY);
        
        if (user.isPresent()) {
            session.setAttribute("user", user.get());
            session.setAttribute("userRole", "AUTHORITY");
            redirectAttributes.addFlashAttribute("success", "Welcome " + user.get().getName());
            return "redirect:/authority/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password!");
            return "redirect:/authority/login";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Logged out successfully!");
        return "redirect:/home";
    }

}

