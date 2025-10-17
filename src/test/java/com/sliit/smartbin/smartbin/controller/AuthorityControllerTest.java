package com.sliit.smartbin.smartbin.controller;

import com.sliit.smartbin.smartbin.dto.BulkRequestDTO;
import com.sliit.smartbin.smartbin.model.*;
import com.sliit.smartbin.smartbin.service.*;
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


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthorityController
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthorityController Unit Tests")
class AuthorityControllerTest {

    @Mock
    private BinService binService;

    @Mock
    private UserService userService;

    @Mock
    private RouteService routeService;

    @Mock
    private CollectionService collectionService;

    @Mock
    private BulkRequestService bulkRequestService;

    @Mock
    private ReportService reportService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private AuthorityController authorityController;

    private User testAuthority;
    private Bin testBin;
    private BulkRequestDTO testBulkRequest;

    @BeforeEach
    void setUp() {
        testAuthority = new User();
        testAuthority.setId(1L);
        testAuthority.setName("Authority User");
        testAuthority.setEmail("authority@example.com");
        testAuthority.setRole(User.UserRole.AUTHORITY);

        testBin = new Bin();
        testBin.setId(1L);
        testBin.setQrCode("QR123");
        testBin.setLocation("Test Location");
        testBin.setFillLevel(80);
        testBin.setStatus(Bin.BinStatus.FULL);

        testBulkRequest = new BulkRequestDTO();
        testBulkRequest.setId(1L);
        testBulkRequest.setRequestId("BULK-001");
        testBulkRequest.setTotalAmount(3500.0);
    }

    // ========== DASHBOARD TESTS ==========

    @Test
    @DisplayName("Should display authority dashboard successfully")
    void dashboard_withValidAuthority_shouldReturnDashboardView() {
        // Given
        Map<String, Object> systemOverview = new HashMap<>();
        systemOverview.put("totalBins", 10);
        Map<String, Object> binStatusReport = new HashMap<>();
        binStatusReport.put("overdueBins", 2);
        
        when(session.getAttribute("user")).thenReturn(testAuthority);
        when(reportService.generateSystemOverviewReport()).thenReturn(systemOverview);
        when(reportService.generateBinStatusReport(any())).thenReturn(binStatusReport);

        // When
        String viewName = authorityController.dashboard(session, model);

        // Then
        assertEquals("authority/dashboard", viewName);
        verify(model).addAttribute("user", testAuthority);
        verify(reportService).generateSystemOverviewReport();
        verify(reportService).generateBinStatusReport(any());
    }

    @Test
    @DisplayName("Should redirect to login when user is null")
    void dashboard_withNullUser_shouldRedirectToLogin() {
        // Given
        when(session.getAttribute("user")).thenReturn(null);

        // When
        String viewName = authorityController.dashboard(session, model);

        // Then
        assertEquals("redirect:/authority/login", viewName);
        verify(binService, never()).findAllBins();
    }

    @Test
    @DisplayName("Should redirect to login when user is not authority")
    void dashboard_withNonAuthorityUser_shouldRedirectToLogin() {
        // Given
        testAuthority.setRole(User.UserRole.RESIDENT);
        when(session.getAttribute("user")).thenReturn(testAuthority);

        // When
        String viewName = authorityController.dashboard(session, model);

        // Then
        assertEquals("redirect:/authority/login", viewName);
    }

    // ========== ASSIGN COLLECTOR TO BULK REQUEST TESTS ==========

    @Test
    @DisplayName("Should assign collector to bulk request successfully")
    void assignCollectorToBulkRequest_withValidData_shouldRedirectWithSuccess() {
        // Given
        when(session.getAttribute("user")).thenReturn(testAuthority);
        when(bulkRequestService.assignCollector(1L, 2L)).thenReturn(testBulkRequest);

        // When
        String viewName = authorityController.assignCollectorToBulkRequest(
                1L, 2L, session, redirectAttributes);

        // Then
        assertEquals("redirect:/authority/bulk-requests", viewName);
        verify(bulkRequestService).assignCollector(1L, 2L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should handle exception when assigning collector")
    void assignCollectorToBulkRequest_withException_shouldRedirectWithError() {
        // Given
        when(session.getAttribute("user")).thenReturn(testAuthority);
        when(bulkRequestService.assignCollector(anyLong(), anyLong()))
                .thenThrow(new RuntimeException("Assignment failed"));

        // When
        String viewName = authorityController.assignCollectorToBulkRequest(
                1L, 2L, session, redirectAttributes);

        // Then
        assertEquals("redirect:/authority/bulk-requests", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}
