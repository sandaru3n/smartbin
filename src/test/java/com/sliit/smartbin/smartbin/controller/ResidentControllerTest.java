package com.sliit.smartbin.smartbin.controller;

import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.model.WasteDisposal;
import com.sliit.smartbin.smartbin.service.BinService;
import com.sliit.smartbin.smartbin.service.RecyclingService;
import com.sliit.smartbin.smartbin.service.WasteDisposalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for ResidentController
 * 
 * Test Coverage Areas:
 * - Positive test cases (happy path)
 * - Negative test cases (sad path)
 * - Boundary/edge cases
 * - Error handling
 * - Model attribute validation
 * - View name validation
 * - Equivalence classes
 * - Determinism
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ResidentController Unit Tests")
class ResidentControllerTest {

    @Mock
    private BinService binService;

    @Mock
    private WasteDisposalService wasteDisposalService;

    @Mock
    private RecyclingService recyclingService;

    @Mock
    private Model model;

    @InjectMocks
    private ResidentController residentController;

    private User testUser;
    private Bin testBin;
    private WasteDisposal testDisposal;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setRole(User.UserRole.RESIDENT);

        testBin = new Bin();
        testBin.setId(1L);
        testBin.setQrCode("QR123");
        testBin.setLocation("Test Location");
        testBin.setLatitude(6.9271);
        testBin.setLongitude(79.8612);
        testBin.setFillLevel(30);

        testDisposal = new WasteDisposal();
        testDisposal.setId(1L);
        testDisposal.setUser(testUser);
        testDisposal.setBin(testBin);
        testDisposal.setReportedFillLevel(80);
    }

    // ========== POSITIVE TEST CASES (Happy Path) ==========

    @Test
    @DisplayName("Should display dashboard successfully")
    void dashboard_withValidUser_shouldReturnDashboardView() {
        // Given
        List<Bin> nearbyBins = Arrays.asList(testBin);
        List<WasteDisposal> userDisposals = Arrays.asList(testDisposal);
        
        when(binService.findNearbyBins(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(nearbyBins);
        when(wasteDisposalService.getUserDisposals(testUser))
                .thenReturn(userDisposals);

        // When
        String viewName = residentController.dashboard(testUser, model);

        // Then
        assertEquals("resident/dashboard", viewName);
        verify(model).addAttribute("nearbyBins", nearbyBins);
        verify(model).addAttribute("userDisposals", userDisposals);
        verify(binService).findNearbyBins(anyDouble(), anyDouble(), anyDouble());
        verify(wasteDisposalService).getUserDisposals(testUser);
    }

    @Test
    @DisplayName("Should display waste disposal form successfully")
    void wasteDisposalForm_shouldReturnWasteDisposalView() {
        // When
        String viewName = residentController.wasteDisposalForm();

        // Then
        assertEquals("resident/waste-disposal", viewName);
    }

    @Test
    @DisplayName("Should submit waste disposal successfully")
    void submitWasteDisposal_withValidInputs_shouldRedirectToDashboard() {
        // Given
        String binQrCode = "QR123";
        Integer fillLevel = 80;
        String notes = "Test disposal";

        when(wasteDisposalService.submitDisposal(eq(testUser), eq(binQrCode), eq(fillLevel), eq(notes)))
                .thenReturn(testDisposal);

        // When
        String viewName = residentController.submitWasteDisposal(testUser, binQrCode, fillLevel, notes);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(wasteDisposalService).submitDisposal(testUser, binQrCode, fillLevel, notes);
    }

    @Test
    @DisplayName("Should display disposal history successfully")
    void disposalHistory_withValidUser_shouldReturnHistoryView() {
        // Given
        List<WasteDisposal> userDisposals = Arrays.asList(testDisposal);
        when(wasteDisposalService.getUserDisposals(testUser))
                .thenReturn(userDisposals);

        // When
        String viewName = residentController.disposalHistory(testUser, model);

        // Then
        assertEquals("resident/disposal-history", viewName);
        verify(model).addAttribute("disposals", userDisposals);
        verify(wasteDisposalService).getUserDisposals(testUser);
    }

    @Test
    @DisplayName("Should display recycling form successfully")
    void recyclingForm_shouldReturnRecyclingView() {
        // When
        String viewName = residentController.recyclingForm();

        // Then
        assertEquals("resident/recycling", viewName);
    }

    @Test
    @DisplayName("Should process recycling transaction successfully")
    void processRecycling_withValidInputs_shouldRedirectToDashboard() {
        // Given
        String recyclingUnitQrCode = "RU001";
        String itemType = "plastic";
        Double weight = 2.5;
        Integer quantity = 10;

        when(recyclingService.processRecyclingTransaction(eq(testUser), eq(recyclingUnitQrCode), 
                eq(itemType), eq(weight), eq(quantity)))
                .thenReturn(null); // Mock return

        // When
        String viewName = residentController.processRecycling(testUser, recyclingUnitQrCode, 
                itemType, weight, quantity);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(recyclingService).processRecyclingTransaction(testUser, recyclingUnitQrCode, 
                itemType, weight, quantity);
    }

    @Test
    @DisplayName("Should display recycling history successfully")
    void recyclingHistory_withValidUser_shouldReturnHistoryView() {
        // Given
        List<Object> userTransactions = Arrays.asList(new Object());
        when(recyclingService.getUserTransactions(testUser))
                .thenReturn(userTransactions);

        // When
        String viewName = residentController.recyclingHistory(testUser, model);

        // Then
        assertEquals("resident/recycling-history", viewName);
        verify(model).addAttribute("transactions", userTransactions);
        verify(recyclingService).getUserTransactions(testUser);
    }

    // ========== NEGATIVE TEST CASES (Sad Path) ==========

    @Test
    @DisplayName("Should handle null user in dashboard")
    void dashboard_withNullUser_shouldHandleGracefully() {
        // When
        String viewName = residentController.dashboard(null, model);

        // Then
        assertEquals("resident/dashboard", viewName);
        verify(binService, never()).findNearbyBins(anyDouble(), anyDouble(), anyDouble());
        verify(wasteDisposalService, never()).getUserDisposals(any());
    }

    @Test
    @DisplayName("Should handle null user in disposal history")
    void disposalHistory_withNullUser_shouldHandleGracefully() {
        // When
        String viewName = residentController.disposalHistory(null, model);

        // Then
        assertEquals("resident/disposal-history", viewName);
        verify(wasteDisposalService, never()).getUserDisposals(any());
    }

    @Test
    @DisplayName("Should handle null user in recycling history")
    void recyclingHistory_withNullUser_shouldHandleGracefully() {
        // When
        String viewName = residentController.recyclingHistory(null, model);

        // Then
        assertEquals("resident/recycling-history", viewName);
        verify(recyclingService, never()).getUserTransactions(any());
    }

    @Test
    @DisplayName("Should handle null user in waste disposal submission")
    void submitWasteDisposal_withNullUser_shouldHandleGracefully() {
        // When
        String viewName = residentController.submitWasteDisposal(null, "QR123", 80, "Test");

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(wasteDisposalService, never()).submitDisposal(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should handle null user in recycling processing")
    void processRecycling_withNullUser_shouldHandleGracefully() {
        // When
        String viewName = residentController.processRecycling(null, "RU001", "plastic", 2.5, 10);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(recyclingService, never()).processRecyclingTransaction(any(), any(), any(), any(), any());
    }

    // ========== BOUNDARY/EDGE CASES ==========

    @Test
    @DisplayName("Should handle empty nearby bins list")
    void dashboard_withEmptyNearbyBins_shouldHandleGracefully() {
        // Given
        when(binService.findNearbyBins(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Arrays.asList());
        when(wasteDisposalService.getUserDisposals(testUser))
                .thenReturn(Arrays.asList());

        // When
        String viewName = residentController.dashboard(testUser, model);

        // Then
        assertEquals("resident/dashboard", viewName);
        verify(model).addAttribute("nearbyBins", Arrays.asList());
        verify(model).addAttribute("userDisposals", Arrays.asList());
    }

    @Test
    @DisplayName("Should handle empty disposal history")
    void disposalHistory_withEmptyHistory_shouldHandleGracefully() {
        // Given
        when(wasteDisposalService.getUserDisposals(testUser))
                .thenReturn(Arrays.asList());

        // When
        String viewName = residentController.disposalHistory(testUser, model);

        // Then
        assertEquals("resident/disposal-history", viewName);
        verify(model).addAttribute("disposals", Arrays.asList());
    }

    @Test
    @DisplayName("Should handle empty recycling history")
    void recyclingHistory_withEmptyHistory_shouldHandleGracefully() {
        // Given
        when(recyclingService.getUserTransactions(testUser))
                .thenReturn(Arrays.asList());

        // When
        String viewName = residentController.recyclingHistory(testUser, model);

        // Then
        assertEquals("resident/recycling-history", viewName);
        verify(model).addAttribute("transactions", Arrays.asList());
    }

    // ========== INPUT VALIDATION TESTS ==========

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should handle null and empty QR codes in waste disposal")
    void submitWasteDisposal_withNullOrEmptyQrCode_shouldHandleGracefully(String qrCode) {
        // When
        String viewName = residentController.submitWasteDisposal(testUser, qrCode, 80, "Test");

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(wasteDisposalService).submitDisposal(testUser, qrCode, 80, "Test");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should handle null and empty notes in waste disposal")
    void submitWasteDisposal_withNullOrEmptyNotes_shouldHandleGracefully(String notes) {
        // When
        String viewName = residentController.submitWasteDisposal(testUser, "QR123", 80, notes);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(wasteDisposalService).submitDisposal(testUser, "QR123", 80, notes);
    }

    @Test
    @DisplayName("Should handle null fill level in waste disposal")
    void submitWasteDisposal_withNullFillLevel_shouldHandleGracefully() {
        // When
        String viewName = residentController.submitWasteDisposal(testUser, "QR123", null, "Test");

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(wasteDisposalService).submitDisposal(testUser, "QR123", null, "Test");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should handle null and empty recycling unit QR codes")
    void processRecycling_withNullOrEmptyQrCode_shouldHandleGracefully(String qrCode) {
        // When
        String viewName = residentController.processRecycling(testUser, qrCode, "plastic", 2.5, 10);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(recyclingService).processRecyclingTransaction(testUser, qrCode, "plastic", 2.5, 10);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should handle null and empty item types")
    void processRecycling_withNullOrEmptyItemType_shouldHandleGracefully(String itemType) {
        // When
        String viewName = residentController.processRecycling(testUser, "RU001", itemType, 2.5, 10);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(recyclingService).processRecyclingTransaction(testUser, "RU001", itemType, 2.5, 10);
    }

    @Test
    @DisplayName("Should handle null weight in recycling")
    void processRecycling_withNullWeight_shouldHandleGracefully() {
        // When
        String viewName = residentController.processRecycling(testUser, "RU001", "plastic", null, 10);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(recyclingService).processRecyclingTransaction(testUser, "RU001", "plastic", null, 10);
    }

    @Test
    @DisplayName("Should handle null quantity in recycling")
    void processRecycling_withNullQuantity_shouldHandleGracefully() {
        // When
        String viewName = residentController.processRecycling(testUser, "RU001", "plastic", 2.5, null);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(recyclingService).processRecyclingTransaction(testUser, "RU001", "plastic", 2.5, null);
    }

    // ========== EQUIVALENCE CLASSES ==========

    @ParameterizedTest
    @ValueSource(ints = {0, 25, 50, 75, 80, 85, 90, 95, 100})
    @DisplayName("Should handle various fill levels in waste disposal")
    void submitWasteDisposal_withVariousFillLevels_shouldHandleCorrectly(int fillLevel) {
        // When
        String viewName = residentController.submitWasteDisposal(testUser, "QR123", fillLevel, "Test");

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(wasteDisposalService).submitDisposal(testUser, "QR123", fillLevel, "Test");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.1, 1.0, 2.5, 10.0, 100.0, 1000.0})
    @DisplayName("Should handle various weights in recycling")
    void processRecycling_withVariousWeights_shouldHandleCorrectly(double weight) {
        // When
        String viewName = residentController.processRecycling(testUser, "RU001", "plastic", weight, 10);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(recyclingService).processRecyclingTransaction(testUser, "RU001", "plastic", weight, 10);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 25, 50, 100})
    @DisplayName("Should handle various quantities in recycling")
    void processRecycling_withVariousQuantities_shouldHandleCorrectly(int quantity) {
        // When
        String viewName = residentController.processRecycling(testUser, "RU001", "plastic", 2.5, quantity);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(recyclingService).processRecyclingTransaction(testUser, "RU001", "plastic", 2.5, quantity);
    }

    // ========== DETERMINISM TESTS ==========

    @Test
    @DisplayName("Should produce consistent results for same inputs")
    void dashboard_determinism_shouldProduceConsistentResults() {
        // Given
        List<Bin> nearbyBins = Arrays.asList(testBin);
        List<WasteDisposal> userDisposals = Arrays.asList(testDisposal);
        
        when(binService.findNearbyBins(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(nearbyBins);
        when(wasteDisposalService.getUserDisposals(testUser))
                .thenReturn(userDisposals);

        // When calling multiple times
        String result1 = residentController.dashboard(testUser, model);
        String result2 = residentController.dashboard(testUser, model);
        String result3 = residentController.dashboard(testUser, model);

        // Then
        assertEquals(result1, result2);
        assertEquals(result2, result3);
        assertEquals("resident/dashboard", result1);
    }

    @Test
    @DisplayName("Should produce consistent results for form submissions")
    void submitWasteDisposal_determinism_shouldProduceConsistentResults() {
        // When calling multiple times with same inputs
        String result1 = residentController.submitWasteDisposal(testUser, "QR123", 80, "Test");
        String result2 = residentController.submitWasteDisposal(testUser, "QR123", 80, "Test");
        String result3 = residentController.submitWasteDisposal(testUser, "QR123", 80, "Test");

        // Then
        assertEquals(result1, result2);
        assertEquals(result2, result3);
        assertEquals("redirect:/resident/dashboard", result1);
    }

    // ========== ERROR HANDLING TESTS ==========

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    void submitWasteDisposal_withServiceException_shouldHandleGracefully() {
        // Given
        when(wasteDisposalService.submitDisposal(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Service error"));

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                residentController.submitWasteDisposal(testUser, "QR123", 80, "Test"));
    }

    @Test
    @DisplayName("Should handle recycling service exceptions gracefully")
    void processRecycling_withServiceException_shouldHandleGracefully() {
        // Given
        when(recyclingService.processRecyclingTransaction(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Service error"));

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                residentController.processRecycling(testUser, "RU001", "plastic", 2.5, 10));
    }

    // ========== MODEL ATTRIBUTE TESTS ==========

    @Test
    @DisplayName("Should add correct attributes to model in dashboard")
    void dashboard_shouldAddCorrectAttributesToModel() {
        // Given
        List<Bin> nearbyBins = Arrays.asList(testBin);
        List<WasteDisposal> userDisposals = Arrays.asList(testDisposal);
        
        when(binService.findNearbyBins(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(nearbyBins);
        when(wasteDisposalService.getUserDisposals(testUser))
                .thenReturn(userDisposals);

        // When
        residentController.dashboard(testUser, model);

        // Then
        verify(model).addAttribute("nearbyBins", nearbyBins);
        verify(model).addAttribute("userDisposals", userDisposals);
    }

    @Test
    @DisplayName("Should add correct attributes to model in disposal history")
    void disposalHistory_shouldAddCorrectAttributesToModel() {
        // Given
        List<WasteDisposal> userDisposals = Arrays.asList(testDisposal);
        when(wasteDisposalService.getUserDisposals(testUser))
                .thenReturn(userDisposals);

        // When
        residentController.disposalHistory(testUser, model);

        // Then
        verify(model).addAttribute("disposals", userDisposals);
    }

    @Test
    @DisplayName("Should add correct attributes to model in recycling history")
    void recyclingHistory_shouldAddCorrectAttributesToModel() {
        // Given
        List<Object> userTransactions = Arrays.asList(new Object());
        when(recyclingService.getUserTransactions(testUser))
                .thenReturn(userTransactions);

        // When
        residentController.recyclingHistory(testUser, model);

        // Then
        verify(model).addAttribute("transactions", userTransactions);
    }
}
