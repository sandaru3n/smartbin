package com.sliit.smartbin.smartbin.controller;

import com.sliit.smartbin.smartbin.dto.LoginDTO;
import com.sliit.smartbin.smartbin.dto.UserDTO;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthController
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private AuthController authController;

    private User testUser;
    private UserDTO testUserDTO;
    private LoginDTO testLoginDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("password123");
        testUser.setRole(User.UserRole.RESIDENT);

        testUserDTO = new UserDTO();
        testUserDTO.setName("John Doe");
        testUserDTO.setEmail("john@example.com");
        testUserDTO.setPassword("password123");

        testLoginDTO = new LoginDTO();
        testLoginDTO.setEmail("john@example.com");
        testLoginDTO.setPassword("password123");
    }

    // ========== SIGNUP TESTS ==========

    @Test
    @DisplayName("Should display signup page successfully")
    void showSignupPage_shouldReturnSignupView() {
        // When
        String viewName = authController.showSignupPage(model);

        // Then
        assertEquals("signup", viewName);
        verify(model).addAttribute(eq("userDTO"), any(UserDTO.class));
    }

    @Test
    @DisplayName("Should register user successfully")
    void registerUser_withValidData_shouldRedirectToLogin() {
        // Given
        when(userService.emailExists(testUserDTO.getEmail())).thenReturn(false);
        when(userService.registerUser(testUserDTO)).thenReturn(testUser);

        // When
        String viewName = authController.registerUser(testUserDTO, redirectAttributes);

        // Then
        assertEquals("redirect:/login", viewName);
        verify(userService).registerUser(testUserDTO);
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    @DisplayName("Should reject registration with existing email")
    void registerUser_withExistingEmail_shouldRedirectToSignup() {
        // Given
        when(userService.emailExists(testUserDTO.getEmail())).thenReturn(true);

        // When
        String viewName = authController.registerUser(testUserDTO, redirectAttributes);

        // Then
        assertEquals("redirect:/signup", viewName);
        verify(userService, never()).registerUser(any());
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    @DisplayName("Should handle registration exception")
    void registerUser_withException_shouldRedirectToSignup() {
        // Given
        when(userService.emailExists(testUserDTO.getEmail())).thenReturn(false);
        when(userService.registerUser(testUserDTO)).thenThrow(new RuntimeException("Registration failed"));

        // When
        String viewName = authController.registerUser(testUserDTO, redirectAttributes);

        // Then
        assertEquals("redirect:/signup", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    // ========== LOGIN PAGE TESTS ==========

    @Test
    @DisplayName("Should display unified login page")
    void showLoginPage_shouldReturnLoginView() {
        // When
        String viewName = authController.showLoginPage();

        // Then
        assertEquals("login", viewName);
    }

    @Test
    @DisplayName("Should display resident login page")
    void showResidentLogin_shouldReturnResidentLoginView() {
        // When
        String viewName = authController.showResidentLogin(model);

        // Then
        assertEquals("resident/login", viewName);
        verify(model).addAttribute(eq("loginDTO"), any(LoginDTO.class));
        verify(model).addAttribute("userType", "Resident");
    }

    @Test
    @DisplayName("Should display collector login page")
    void showCollectorLogin_shouldReturnCollectorLoginView() {
        // When
        String viewName = authController.showCollectorLogin(model);

        // Then
        assertEquals("collector/login", viewName);
        verify(model).addAttribute(eq("loginDTO"), any(LoginDTO.class));
        verify(model).addAttribute("userType", "Waste Collector");
    }

    @Test
    @DisplayName("Should display authority login page")
    void showAuthorityLogin_shouldReturnAuthorityLoginView() {
        // When
        String viewName = authController.showAuthorityLogin(model);

        // Then
        assertEquals("authority/login", viewName);
        verify(model).addAttribute(eq("loginDTO"), any(LoginDTO.class));
        verify(model).addAttribute("userType", "Waste Management Authority");
    }

    // ========== RESIDENT LOGIN TESTS ==========

    @Test
    @DisplayName("Should login resident successfully")
    void residentLogin_withValidCredentials_shouldRedirectToDashboard() {
        // Given
        when(userService.loginUser(any(LoginDTO.class), eq(User.UserRole.RESIDENT)))
                .thenReturn(Optional.of(testUser));

        // When
        String viewName = authController.residentLogin(testLoginDTO, session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(session).setAttribute("user", testUser);
        verify(session).setAttribute("userRole", "RESIDENT");
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    @DisplayName("Should reject resident login with invalid credentials")
    void residentLogin_withInvalidCredentials_shouldRedirectToLogin() {
        // Given
        when(userService.loginUser(any(LoginDTO.class), eq(User.UserRole.RESIDENT)))
                .thenReturn(Optional.empty());

        // When
        String viewName = authController.residentLogin(testLoginDTO, session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/login", viewName);
        verify(session, never()).setAttribute(anyString(), any());
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    // ========== COLLECTOR LOGIN TESTS ==========

    @Test
    @DisplayName("Should login collector successfully")
    void collectorLogin_withValidCredentials_shouldRedirectToDashboard() {
        // Given
        testUser.setRole(User.UserRole.COLLECTOR);
        when(userService.loginUser(any(LoginDTO.class), eq(User.UserRole.COLLECTOR)))
                .thenReturn(Optional.of(testUser));

        // When
        String viewName = authController.collectorLogin(testLoginDTO, session, redirectAttributes);

        // Then
        assertEquals("redirect:/collector/dashboard", viewName);
        verify(session).setAttribute("user", testUser);
        verify(session).setAttribute("userRole", "COLLECTOR");
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    @DisplayName("Should reject collector login with invalid credentials")
    void collectorLogin_withInvalidCredentials_shouldRedirectToLogin() {
        // Given
        when(userService.loginUser(any(LoginDTO.class), eq(User.UserRole.COLLECTOR)))
                .thenReturn(Optional.empty());

        // When
        String viewName = authController.collectorLogin(testLoginDTO, session, redirectAttributes);

        // Then
        assertEquals("redirect:/collector/login", viewName);
        verify(session, never()).setAttribute(anyString(), any());
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    // ========== AUTHORITY LOGIN TESTS ==========

    @Test
    @DisplayName("Should login authority successfully")
    void authorityLogin_withValidCredentials_shouldRedirectToDashboard() {
        // Given
        testUser.setRole(User.UserRole.AUTHORITY);
        when(userService.loginUser(any(LoginDTO.class), eq(User.UserRole.AUTHORITY)))
                .thenReturn(Optional.of(testUser));

        // When
        String viewName = authController.authorityLogin(testLoginDTO, session, redirectAttributes);

        // Then
        assertEquals("redirect:/authority/dashboard", viewName);
        verify(session).setAttribute("user", testUser);
        verify(session).setAttribute("userRole", "AUTHORITY");
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    @DisplayName("Should reject authority login with invalid credentials")
    void authorityLogin_withInvalidCredentials_shouldRedirectToLogin() {
        // Given
        when(userService.loginUser(any(LoginDTO.class), eq(User.UserRole.AUTHORITY)))
                .thenReturn(Optional.empty());

        // When
        String viewName = authController.authorityLogin(testLoginDTO, session, redirectAttributes);

        // Then
        assertEquals("redirect:/authority/login", viewName);
        verify(session, never()).setAttribute(anyString(), any());
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    // ========== LOGOUT TESTS ==========

    @Test
    @DisplayName("Should logout successfully")
    void logout_shouldInvalidateSessionAndRedirectToHome() {
        // When
        String viewName = authController.logout(session, redirectAttributes);

        // Then
        assertEquals("redirect:/home", viewName);
        verify(session).invalidate();
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    // ========== DETERMINISM TESTS ==========

    @Test
    @DisplayName("Should produce consistent results for same inputs")
    void login_determinism_shouldProduceConsistentResults() {
        // Given
        when(userService.loginUser(any(LoginDTO.class), eq(User.UserRole.RESIDENT)))
                .thenReturn(Optional.of(testUser));

        // When
        String result1 = authController.residentLogin(testLoginDTO, session, redirectAttributes);
        String result2 = authController.residentLogin(testLoginDTO, session, redirectAttributes);
        String result3 = authController.residentLogin(testLoginDTO, session, redirectAttributes);

        // Then
        assertEquals(result1, result2);
        assertEquals(result2, result3);
        assertEquals("redirect:/resident/dashboard", result1);
    }
}

