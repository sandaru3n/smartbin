package com.sliit.smartbin.smartbin.controller;

import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.RecyclingTransaction;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.model.WasteDisposal;
import com.sliit.smartbin.smartbin.service.BinService;
import com.sliit.smartbin.smartbin.service.RecyclingService;
import com.sliit.smartbin.smartbin.service.WasteDisposalService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ResidentController
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

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ResidentController residentController;

    private User testUser;
    private Bin testBin;
    private WasteDisposal testDisposal;
    private RecyclingTransaction testTransaction;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setRole(User.UserRole.RESIDENT);
        testUser.setRecyclingPoints(100.0);

        testBin = new Bin();
        testBin.setId(1L);
        testBin.setQrCode("QR123");
        testBin.setLocation("Test Location");
        testBin.setLatitude(6.9271);
        testBin.setLongitude(79.8612);
        testBin.setFillLevel(30);
        testBin.setStatus(Bin.BinStatus.EMPTY);

        testDisposal = new WasteDisposal();
        testDisposal.setId(1L);
        testDisposal.setUser(testUser);
        testDisposal.setBin(testBin);
        testDisposal.setReportedFillLevel(80);
        testDisposal.setStatus(WasteDisposal.DisposalStatus.CONFIRMED);

        testTransaction = new RecyclingTransaction();
        testTransaction.setId(1L);
        testTransaction.setUser(testUser);
        testTransaction.setItemType("plastic");
        testTransaction.setWeight(2.5);
        testTransaction.setQuantity(10);
        testTransaction.setPointsEarned(25.0);
        testTransaction.setStatus(RecyclingTransaction.TransactionStatus.CONFIRMED);
    }

    // ========== DASHBOARD TESTS ==========

    @Test
    @DisplayName("Should display dashboard successfully with valid user")
    void dashboard_withValidUser_shouldReturnDashboardView() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(binService.findNearbyBins(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Arrays.asList(testBin));
        when(binService.findAlertedBins()).thenReturn(Arrays.asList(testBin));
        when(recyclingService.getUserTransactions(testUser))
                .thenReturn(Arrays.asList(testTransaction));
        when(wasteDisposalService.getUserDisposals(testUser))
                .thenReturn(Arrays.asList(testDisposal));

        // When
        String viewName = residentController.dashboard(session, model);

        // Then
        assertEquals("resident/dashboard", viewName);
        verify(model).addAttribute("user", testUser);
        verify(model).addAttribute(eq("nearbyBins"), anyList());
        verify(model).addAttribute(eq("recyclingPoints"), any());
    }

    @Test
    @DisplayName("Should redirect to login when user is null")
    void dashboard_withNullUser_shouldRedirectToLogin() {
        // Given
        when(session.getAttribute("user")).thenReturn(null);

        // When
        String viewName = residentController.dashboard(session, model);

        // Then
        assertEquals("redirect:/resident/login", viewName);
        verify(binService, never()).findNearbyBins(anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("Should redirect to login when user is not a resident")
    void dashboard_withNonResidentUser_shouldRedirectToLogin() {
        // Given
        testUser.setRole(User.UserRole.AUTHORITY);
        when(session.getAttribute("user")).thenReturn(testUser);

        // When
        String viewName = residentController.dashboard(session, model);

        // Then
        assertEquals("redirect:/resident/login", viewName);
    }

    // ========== VIEW BINS TESTS ==========

    @Test
    @DisplayName("Should display all bins successfully")
    void viewBins_withValidUser_shouldReturnBinsView() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(binService.findAllBins()).thenReturn(Arrays.asList(testBin));

        // When
        String viewName = residentController.viewBins(session, model);

        // Then
        assertEquals("resident/bins", viewName);
        verify(model).addAttribute("bins", Arrays.asList(testBin));
        verify(model).addAttribute("user", testUser);
    }

    // ========== BIN DETAILS TESTS ==========

    @Test
    @DisplayName("Should display bin details successfully")
    void viewBinDetails_withValidBin_shouldReturnDetailsView() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(binService.findById(1L)).thenReturn(Optional.of(testBin));

        // When
        String viewName = residentController.viewBinDetails(1L, session, model);

        // Then
        assertEquals("resident/bin-details", viewName);
        verify(model).addAttribute("bin", testBin);
        verify(model).addAttribute("user", testUser);
    }

    @Test
    @DisplayName("Should redirect when bin not found")
    void viewBinDetails_withInvalidBin_shouldRedirectToBins() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(binService.findById(999L)).thenReturn(Optional.empty());

        // When
        String viewName = residentController.viewBinDetails(999L, session, model);

        // Then
        assertEquals("redirect:/resident/bins", viewName);
    }

    // ========== UPDATE BIN STATUS TESTS ==========

    @Test
    @DisplayName("Should update bin status successfully")
    void updateBinStatus_withValidInput_shouldRedirectWithSuccess() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        // updateBinFillLevel returns void, so we don't need to mock its behavior

        // When
        String viewName = residentController.updateBinStatus(1L, 75, session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/bin/1", viewName);
        verify(binService).updateBinFillLevel(1L, 75);
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    @DisplayName("Should handle exception when updating bin status")
    void updateBinStatus_withException_shouldRedirectWithError() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        doThrow(new RuntimeException("Update failed")).when(binService).updateBinFillLevel(1L, 75);

        // When
        String viewName = residentController.updateBinStatus(1L, 75, session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/bin/1", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    // ========== SEARCH BINS TESTS ==========

    @Test
    @DisplayName("Should search bins with valid coordinates")
    void searchBins_withValidCoordinates_shouldReturnResults() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(binService.findNearbyBins(6.9271, 79.8612, 5.0))
                .thenReturn(Arrays.asList(testBin));

        // When
        String viewName = residentController.searchBins(6.9271, 79.8612, 5.0, session, model);

        // Then
        assertEquals("resident/search-results", viewName);
        verify(model).addAttribute("bins", Arrays.asList(testBin));
    }

    // ========== SUBMIT DISPOSAL TESTS ==========

    @Test
    @DisplayName("Should submit disposal successfully")
    void submitDisposal_withValidInput_shouldRedirectWithSuccess() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(wasteDisposalService.submitDisposal(testUser, "QR123", 80, "Test notes"))
                .thenReturn(testDisposal);

        // When
        String viewName = residentController.submitDisposal("QR123", 80, "Test notes", session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    @DisplayName("Should redirect to login when user is null in disposal")
    void submitDisposal_withNullUser_shouldRedirectToLogin() {
        // Given
        when(session.getAttribute("user")).thenReturn(null);

        // When
        String viewName = residentController.submitDisposal("QR123", 80, "Test", session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/login", viewName);
        verify(wasteDisposalService, never()).submitDisposal(any(), any(), any(), any());
    }

    // ========== RECYCLING TESTS ==========

    @Test
    @DisplayName("Should display recycling units page")
    void findRecyclingUnits_withValidUser_shouldReturnView() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(recyclingService.getNearbyRecyclingUnits(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());

        // When
        String viewName = residentController.findRecyclingUnits(null, null, 10.0, session, model);

        // Then
        assertEquals("resident/recycling-units", viewName);
        verify(model).addAttribute("user", testUser);
    }

    @Test
    @DisplayName("Should display recycle page")
    void recyclePage_withValidUser_shouldReturnView() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);

        // When
        String viewName = residentController.recyclePage("RU001", session, model);

        // Then
        assertEquals("resident/recycle", viewName);
        verify(model).addAttribute("user", testUser);
        verify(model).addAttribute("unitQrCode", "RU001");
    }

    @Test
    @DisplayName("Should submit recycling successfully")
    void submitRecycling_withValidInput_shouldRedirectWithSuccess() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(recyclingService.processRecyclingTransaction(testUser, "RU001", "plastic", 2.5, 10))
                .thenReturn(testTransaction);

        // When
        String viewName = residentController.submitRecycling("RU001", "plastic", 2.5, 10, session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    @DisplayName("Should handle failed recycling transaction")
    void submitRecycling_withFailedTransaction_shouldShowError() {
        // Given
        testTransaction.setStatus(RecyclingTransaction.TransactionStatus.FAILED);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(recyclingService.processRecyclingTransaction(testUser, "RU001", "plastic", 2.5, 10))
                .thenReturn(testTransaction);

        // When
        String viewName = residentController.submitRecycling("RU001", "plastic", 2.5, 10, session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    @DisplayName("Should display my recycling page")
    void myRecycling_withValidUser_shouldReturnView() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(recyclingService.getUserTransactions(testUser))
                .thenReturn(Arrays.asList(testTransaction));

        // When
        String viewName = residentController.myRecycling(session, model);

        // Then
        assertEquals("resident/my-recycling", viewName);
        verify(model).addAttribute("user", testUser);
        verify(model).addAttribute("transactions", Arrays.asList(testTransaction));
        verify(model).addAttribute("totalPoints", testUser.getRecyclingPoints());
    }

    // ========== REPORTS TESTS ==========

    @Test
    @DisplayName("Should display reports page")
    void viewReports_withValidUser_shouldReturnView() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(binService.findAllBins()).thenReturn(Arrays.asList(testBin));

        // When
        String viewName = residentController.viewReports(session, model);

        // Then
        assertEquals("resident/reports", viewName);
        verify(model).addAttribute("user", testUser);
        verify(model).addAttribute(eq("totalBins"), anyLong());
    }

    // ========== SCAN BIN TESTS ==========

    @Test
    @DisplayName("Should display scan bin page")
    void scanBinPage_withValidUser_shouldReturnView() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);

        // When
        String viewName = residentController.scanBinPage(session, model);

        // Then
        assertEquals("resident/scan-bin", viewName);
        verify(model).addAttribute("user", testUser);
    }

    // ========== EXCEPTION HANDLING TESTS ==========

    @Test
    @DisplayName("Should handle exception when submitting disposal")
    void submitDisposal_withException_shouldRedirectWithError() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(wasteDisposalService.submitDisposal(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Service error"));

        // When
        String viewName = residentController.submitDisposal("QR123", 80, "Test", session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    @DisplayName("Should handle exception when submitting recycling")
    void submitRecycling_withException_shouldRedirectWithError() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(recyclingService.processRecyclingTransaction(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Service error"));

        // When
        String viewName = residentController.submitRecycling("RU001", "plastic", 2.5, 10, session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }
}
