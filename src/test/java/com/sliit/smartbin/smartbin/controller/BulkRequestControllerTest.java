package com.sliit.smartbin.smartbin.controller;

import com.sliit.smartbin.smartbin.dto.BulkRequestDTO;
import com.sliit.smartbin.smartbin.model.BulkCategory;
import com.sliit.smartbin.smartbin.model.PaymentStatus;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.service.BulkRequestService;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BulkRequestController
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BulkRequestController Unit Tests")
class BulkRequestControllerTest {

    @Mock
    private BulkRequestService bulkRequestService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private BulkRequestController bulkRequestController;

    private User testUser;
    private BulkRequestDTO testBulkRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setRole(User.UserRole.RESIDENT);

        testBulkRequest = new BulkRequestDTO();
        testBulkRequest.setId(1L);
        testBulkRequest.setRequestId("BULK-001");
        testBulkRequest.setCategory(BulkCategory.FURNITURE);
        testBulkRequest.setDescription("Old sofa");
        testBulkRequest.setStreetAddress("123 Main St");
        testBulkRequest.setCity("Colombo");
        testBulkRequest.setZipCode("00100");
        testBulkRequest.setTotalAmount(3500.0);
        testBulkRequest.setPaymentStatus(PaymentStatus.PENDING);
    }

    // ========== SHOW BULK REQUEST FORM TESTS ==========

    @Test
    @DisplayName("Should display bulk request form for logged-in user")
    void showBulkRequestForm_withLoggedInUser_shouldReturnFormView() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);

        // When
        String viewName = bulkRequestController.showBulkRequestForm(model, session);

        // Then
        assertEquals("resident/bulk-request", viewName);
        verify(model).addAttribute(eq("bulkRequest"), any(BulkRequestDTO.class));
        verify(model).addAttribute("user", testUser);
    }

    @Test
    @DisplayName("Should redirect to login when user not logged in")
    void showBulkRequestForm_withoutUser_shouldRedirectToLogin() {
        // Given
        when(session.getAttribute("user")).thenReturn(null);

        // When
        String viewName = bulkRequestController.showBulkRequestForm(model, session);

        // Then
        assertEquals("redirect:/resident/login", viewName);
        verify(model, never()).addAttribute(anyString(), any());
    }

    // ========== SUBMIT BULK REQUEST TESTS ==========

    @Test
    @DisplayName("Should submit bulk request successfully")
    void submitBulkRequest_withValidData_shouldRedirectToPayment() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(bulkRequestService.calculateFee(any(BulkRequestDTO.class))).thenReturn(testBulkRequest);
        when(bulkRequestService.createBulkRequest(any(BulkRequestDTO.class), eq(testUser)))
                .thenReturn(testBulkRequest);

        // When
        String viewName = bulkRequestController.submitBulkRequest(testBulkRequest, model, session, redirectAttributes);

        // Then
        assertTrue(viewName.contains("redirect:/resident/bulk-request/"));
        assertTrue(viewName.contains("/payment-page"));
        verify(bulkRequestService).calculateFee(any(BulkRequestDTO.class));
        verify(bulkRequestService).createBulkRequest(any(BulkRequestDTO.class), eq(testUser));
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should redirect to login when user not logged in during submission")
    void submitBulkRequest_withoutUser_shouldRedirectToLogin() {
        // Given
        when(session.getAttribute("user")).thenReturn(null);

        // When
        String viewName = bulkRequestController.submitBulkRequest(testBulkRequest, model, session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/login", viewName);
        verify(bulkRequestService, never()).createBulkRequest(any(), any());
    }

    @Test
    @DisplayName("Should handle missing category")
    void submitBulkRequest_withoutCategory_shouldReturnFormWithError() {
        // Given
        testBulkRequest.setCategory(null);
        when(session.getAttribute("user")).thenReturn(testUser);

        // When
        String viewName = bulkRequestController.submitBulkRequest(testBulkRequest, model, session, redirectAttributes);

        // Then
        assertEquals("resident/bulk-request", viewName);
        verify(model).addAttribute(eq("errorMessage"), anyString());
        verify(bulkRequestService, never()).createBulkRequest(any(), any());
    }

    @Test
    @DisplayName("Should handle exception during submission")
    void submitBulkRequest_withException_shouldReturnFormWithError() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(bulkRequestService.calculateFee(any(BulkRequestDTO.class)))
                .thenThrow(new RuntimeException("Service error"));

        // When
        String viewName = bulkRequestController.submitBulkRequest(testBulkRequest, model, session, redirectAttributes);

        // Then
        assertEquals("resident/bulk-request", viewName);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }

    // ========== SUCCESS PAGE TESTS ==========

    @Test
    @DisplayName("Should display success page with valid request ID")
    void showSuccessPage_withValidRequestId_shouldReturnSuccessView() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(bulkRequestService.getBulkRequestByRequestId("BULK-001"))
                .thenReturn(Optional.of(testBulkRequest));

        // When
        String viewName = bulkRequestController.showSuccessPage("BULK-001", model, session);

        // Then
        assertEquals("resident/bulk-request-success", viewName);
        verify(model).addAttribute("bulkRequest", testBulkRequest);
        verify(model).addAttribute("user", testUser);
    }

    @Test
    @DisplayName("Should redirect when request not found")
    void showSuccessPage_withInvalidRequestId_shouldRedirectToDashboard() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(bulkRequestService.getBulkRequestByRequestId("INVALID"))
                .thenReturn(Optional.empty());

        // When
        String viewName = bulkRequestController.showSuccessPage("INVALID", model, session);

        // Then
        assertEquals("redirect:/resident/dashboard", viewName);
    }

    // ========== MY BULK REQUESTS TESTS ==========

    @Test
    @DisplayName("Should display user's bulk requests")
    void showMyBulkRequests_withLoggedInUser_shouldReturnListView() {
        // Given
        List<BulkRequestDTO> requests = Arrays.asList(testBulkRequest);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(bulkRequestService.getBulkRequestsByUser(testUser)).thenReturn(requests);

        // When
        String viewName = bulkRequestController.showMyBulkRequests(model, session);

        // Then
        assertEquals("resident/my-bulk-requests", viewName);
        verify(model).addAttribute("bulkRequests", requests);
        verify(model).addAttribute("user", testUser);
    }

    // ========== PAYMENT TESTS ==========

    @Test
    @DisplayName("Should process payment successfully")
    void processPayment_withValidData_shouldRedirectWithSuccess() {
        // Given
        testBulkRequest.setPaymentStatus(PaymentStatus.COMPLETED);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(bulkRequestService.processPayment(1L, "credit_card", "REF123"))
                .thenReturn(testBulkRequest);

        // When
        String viewName = bulkRequestController.processPayment(1L, "credit_card", "REF123", session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/my-bulk-requests", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should handle failed payment")
    void processPayment_withFailedPayment_shouldRedirectWithError() {
        // Given
        testBulkRequest.setPaymentStatus(PaymentStatus.FAILED);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(bulkRequestService.processPayment(1L, "credit_card", "REF123"))
                .thenReturn(testBulkRequest);

        // When
        String viewName = bulkRequestController.processPayment(1L, "credit_card", "REF123", session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/my-bulk-requests", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should handle payment exception")
    void processPayment_withException_shouldRedirectWithError() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(bulkRequestService.processPayment(anyLong(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Payment error"));

        // When
        String viewName = bulkRequestController.processPayment(1L, "credit_card", "REF123", session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/my-bulk-requests", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    // ========== CANCEL REQUEST TESTS ==========

    @Test
    @DisplayName("Should cancel request successfully")
    void cancelRequest_withValidData_shouldRedirectWithSuccess() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        // cancelRequest returns void, no need to mock its behavior

        // When
        String viewName = bulkRequestController.cancelRequest(1L, "Changed mind", session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/my-bulk-requests", viewName);
        verify(bulkRequestService).cancelRequest(1L, "Changed mind");
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should handle cancel exception")
    void cancelRequest_withException_shouldRedirectWithError() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        doThrow(new RuntimeException("Cancel error")).when(bulkRequestService).cancelRequest(anyLong(), anyString());

        // When
        String viewName = bulkRequestController.cancelRequest(1L, "reason", session, redirectAttributes);

        // Then
        assertEquals("redirect:/resident/my-bulk-requests", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    // ========== AJAX ENDPOINTS TESTS ==========

    @Test
    @DisplayName("Should calculate fee via AJAX")
    void calculateFee_shouldReturnCalculatedDTO() {
        // Given
        when(bulkRequestService.calculateFee(any(BulkRequestDTO.class))).thenReturn(testBulkRequest);

        // When
        BulkRequestDTO result = bulkRequestController.calculateFee(testBulkRequest);

        // Then
        assertNotNull(result);
        assertEquals(testBulkRequest.getTotalAmount(), result.getTotalAmount());
        verify(bulkRequestService).calculateFee(any(BulkRequestDTO.class));
    }

    @Test
    @DisplayName("Should get request details by string ID")
    void getRequestDetails_withValidId_shouldReturnDTO() {
        // Given
        when(bulkRequestService.getBulkRequestByRequestId("BULK-001"))
                .thenReturn(Optional.of(testBulkRequest));

        // When
        BulkRequestDTO result = bulkRequestController.getRequestDetails("BULK-001");

        // Then
        assertNotNull(result);
        assertEquals("BULK-001", result.getRequestId());
    }

    @Test
    @DisplayName("Should return null when request not found")
    void getRequestDetails_withInvalidId_shouldReturnNull() {
        // Given
        when(bulkRequestService.getBulkRequestByRequestId("INVALID"))
                .thenReturn(Optional.empty());

        // When
        BulkRequestDTO result = bulkRequestController.getRequestDetails("INVALID");

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should get request details by numeric ID")
    void getRequestDetailsById_withValidId_shouldReturnDTO() {
        // Given
        when(bulkRequestService.getBulkRequestById(1L))
                .thenReturn(Optional.of(testBulkRequest));

        // When
        BulkRequestDTO result = bulkRequestController.getRequestDetailsById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // ========== PAYMENT PAGE TESTS ==========

    @Test
    @DisplayName("Should display payment page")
    void showPaymentPage_withValidId_shouldReturnPaymentView() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(bulkRequestService.getBulkRequestById(1L))
                .thenReturn(Optional.of(testBulkRequest));

        // When
        String viewName = bulkRequestController.showPaymentPage(1L, model, session);

        // Then
        assertEquals("resident/bulk-request-payment", viewName);
        verify(model).addAttribute("bulkRequest", testBulkRequest);
        verify(model).addAttribute("user", testUser);
    }

    @Test
    @DisplayName("Should redirect when payment page request not found")
    void showPaymentPage_withInvalidId_shouldRedirectToMyRequests() {
        // Given
        when(session.getAttribute("user")).thenReturn(testUser);
        when(bulkRequestService.getBulkRequestById(999L))
                .thenReturn(Optional.empty());

        // When
        String viewName = bulkRequestController.showPaymentPage(999L, model, session);

        // Then
        assertEquals("redirect:/resident/my-bulk-requests", viewName);
    }
}

