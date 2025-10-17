package com.sliit.smartbin.smartbin.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HomeController
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HomeController Unit Tests")
class HomeControllerTest {

    @InjectMocks
    private HomeController homeController;

    @Test
    @DisplayName("Should return home view")
    void home_shouldReturnHomeView() {
        // When
        String viewName = homeController.home();

        // Then
        assertEquals("home", viewName);
    }

    @Test
    @DisplayName("Should return consistent view for multiple calls")
    void home_determinism_shouldReturnConsistentView() {
        // When
        String result1 = homeController.home();
        String result2 = homeController.home();
        String result3 = homeController.home();

        // Then
        assertEquals(result1, result2);
        assertEquals(result2, result3);
        assertEquals("home", result1);
    }
}

