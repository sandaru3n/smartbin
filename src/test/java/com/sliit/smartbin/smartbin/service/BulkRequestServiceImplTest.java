package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.dto.BulkRequestDTO;
import com.sliit.smartbin.smartbin.model.*;
import com.sliit.smartbin.smartbin.repository.BulkRequestRepository;
import com.sliit.smartbin.smartbin.repository.UserRepository;
import com.sliit.smartbin.smartbin.service.impl.BulkRequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for BulkRequestServiceImpl
 * 
 * Test Coverage Areas:
 * - Positive test cases (happy path)
 * - Negative test cases (sad path)
 * - Boundary/edge cases
 * - Error handling
 * - Fee calculations
 * - Status transitions
 * - Payment processing
 * - Equivalence classes
 * - Determinism
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BulkRequestServiceImpl Unit Tests")
class BulkRequestServiceImplTest {

    @Mock
    private BulkRequestRepository bulkRequestRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BulkRequestServiceImpl bulkRequestService;

    private User testUser;
    private User testCollector;
    private BulkRequest testBulkRequest;
    private BulkRequestDTO testBulkRequestDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setRole(User.UserRole.RESIDENT);

        testCollector = new User();
        testCollector.setId(2L);
        testCollector.setName("Jane Collector");
        testCollector.setEmail("jane@collector.com");
        testCollector.setRole(User.UserRole.COLLECTOR);

        testBulkRequest = new BulkRequest();
        testBulkRequest.setId(1L);
        testBulkRequest.setRequestId("BR-2024-001");
        testBulkRequest.setUser(testUser);
        testBulkRequest.setCategory(BulkCategory.FURNITURE);
        testBulkRequest.setDescription("Old wooden table");
        testBulkRequest.setStreetAddress("123 Main St");
        testBulkRequest.setCity("Colombo");
        testBulkRequest.setZipCode("00100");
        testBulkRequest.setBasePrice(1000.0);
        testBulkRequest.setProcessingFee(500.0);
        testBulkRequest.setTaxAmount(75.0);
        testBulkRequest.setTotalAmount(1575.0);
        testBulkRequest.setStatus(BulkRequestStatus.PENDING);
        testBulkRequest.setPaymentStatus(PaymentStatus.PENDING);

        testBulkRequestDTO = new BulkRequestDTO();
        testBulkRequestDTO.setId(1L);
        testBulkRequestDTO.setRequestId("BR-2024-001");
        testBulkRequestDTO.setUserId(1L);
        testBulkRequestDTO.setCategory(BulkCategory.FURNITURE);
        testBulkRequestDTO.setDescription("Old wooden table");
        testBulkRequestDTO.setStreetAddress("123 Main St");
        testBulkRequestDTO.setCity("Colombo");
        testBulkRequestDTO.setZipCode("00100");
        testBulkRequestDTO.setBasePrice(1000.0);
        testBulkRequestDTO.setProcessingFee(500.0);
        testBulkRequestDTO.setTaxAmount(75.0);
        testBulkRequestDTO.setTotalAmount(1575.0);
        testBulkRequestDTO.setStatus(BulkRequestStatus.PENDING);
        testBulkRequestDTO.setPaymentStatus(PaymentStatus.PENDING);
    }

    // ========== POSITIVE TEST CASES (Happy Path) ==========

    @Test
    @DisplayName("Should successfully create bulk request with valid inputs")
    void createBulkRequest_withValidInputs_shouldSucceed() {
        // Given
        when(bulkRequestRepository.save(any(BulkRequest.class))).thenReturn(testBulkRequest);
        doNothing().when(notificationService).notifyUserBulkRequest(any(User.class), anyString(), any(BulkRequest.class));

        // When
        BulkRequestDTO result = bulkRequestService.createBulkRequest(testBulkRequestDTO, testUser);

        // Then
        assertNotNull(result);
        assertEquals(BulkRequestStatus.PENDING, result.getStatus());
        assertEquals(PaymentStatus.PENDING, result.getPaymentStatus());
        assertEquals(1575.0, result.getTotalAmount());

        verify(bulkRequestRepository).save(any(BulkRequest.class));
        verify(notificationService).notifyUserBulkRequest(eq(testUser), anyString(), any(BulkRequest.class));
    }

    @Test
    @DisplayName("Should get bulk request by ID successfully")
    void getBulkRequestById_withValidId_shouldReturnRequest() {
        // Given
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));

        // When
        Optional<BulkRequestDTO> result = bulkRequestService.getBulkRequestById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testBulkRequestDTO.getId(), result.get().getId());
        verify(bulkRequestRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get bulk requests by user successfully")
    void getBulkRequestsByUser_withValidUser_shouldReturnRequests() {
        // Given
        List<BulkRequest> expectedRequests = Arrays.asList(testBulkRequest);
        when(bulkRequestRepository.findByUserOrderByCreatedAtDesc(testUser))
                .thenReturn(expectedRequests);

        // When
        List<BulkRequestDTO> result = bulkRequestService.getBulkRequestsByUser(testUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBulkRequest.getId(), result.get(0).getId());
        verify(bulkRequestRepository).findByUserOrderByCreatedAtDesc(testUser);
    }

    @Test
    @DisplayName("Should get bulk requests by status successfully")
    void getBulkRequestsByStatus_withValidStatus_shouldReturnRequests() {
        // Given
        List<BulkRequest> expectedRequests = Arrays.asList(testBulkRequest);
        when(bulkRequestRepository.findByStatusOrderByCreatedAtDesc(BulkRequestStatus.PENDING))
                .thenReturn(expectedRequests);

        // When
        List<BulkRequestDTO> result = bulkRequestService.getBulkRequestsByStatus(BulkRequestStatus.PENDING);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBulkRequest.getId(), result.get(0).getId());
        verify(bulkRequestRepository).findByStatusOrderByCreatedAtDesc(BulkRequestStatus.PENDING);
    }

    @Test
    @DisplayName("Should assign collector successfully")
    void assignCollector_withValidIds_shouldAssignCollector() {
        // Given
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testCollector));
        when(bulkRequestRepository.save(any(BulkRequest.class))).thenReturn(testBulkRequest);
        doNothing().when(notificationService).notifyCollectorBulkAssignment(any(User.class), any(BulkRequest.class));
        doNothing().when(notificationService).notifyUserBulkRequest(any(User.class), anyString(), any(BulkRequest.class));

        // When
        BulkRequestDTO result = bulkRequestService.assignCollector(1L, 2L);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getCollectorAssigned());
        assertEquals(BulkRequestStatus.COLLECTOR_ASSIGNED, result.getStatus());

        verify(bulkRequestRepository).save(any(BulkRequest.class));
        verify(notificationService).notifyCollectorBulkAssignment(eq(testCollector), any(BulkRequest.class));
        verify(notificationService).notifyUserBulkRequest(eq(testUser), anyString(), any(BulkRequest.class));
    }

    @Test
    @DisplayName("Should schedule collection successfully")
    void scheduleCollection_withValidDate_shouldScheduleCollection() {
        // Given
        LocalDateTime scheduledDate = LocalDateTime.now().plusDays(1);
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));
        when(bulkRequestRepository.save(any(BulkRequest.class))).thenReturn(testBulkRequest);
        doNothing().when(notificationService).sendPickupScheduleNotification(any(User.class), any(BulkRequest.class));

        // When
        BulkRequestDTO result = bulkRequestService.scheduleCollection(1L, scheduledDate);

        // Then
        assertNotNull(result);
        assertEquals(BulkRequestStatus.SCHEDULED, result.getStatus());
        assertEquals(scheduledDate, result.getScheduledDate());

        verify(bulkRequestRepository).save(any(BulkRequest.class));
        verify(notificationService).sendPickupScheduleNotification(eq(testUser), any(BulkRequest.class));
    }

    @Test
    @DisplayName("Should complete collection successfully")
    void completeCollection_withValidRequest_shouldCompleteCollection() {
        // Given
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));
        when(bulkRequestRepository.save(any(BulkRequest.class))).thenReturn(testBulkRequest);
        doNothing().when(notificationService).notifyUserBulkRequest(any(User.class), anyString(), any(BulkRequest.class));

        // When
        BulkRequestDTO result = bulkRequestService.completeCollection(1L, "Collection completed successfully");

        // Then
        assertNotNull(result);
        assertEquals(BulkRequestStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getCompletedDate());
        assertEquals("Collection completed successfully", result.getNotes());

        verify(bulkRequestRepository).save(any(BulkRequest.class));
        verify(notificationService).notifyUserBulkRequest(eq(testUser), anyString(), any(BulkRequest.class));
    }

    @Test
    @DisplayName("Should cancel request successfully")
    void cancelRequest_withValidRequest_shouldCancelRequest() {
        // Given
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));
        when(bulkRequestRepository.save(any(BulkRequest.class))).thenReturn(testBulkRequest);

        // When
        BulkRequestDTO result = bulkRequestService.cancelRequest(1L, "User requested cancellation");

        // Then
        assertNotNull(result);
        assertEquals(BulkRequestStatus.CANCELLED, result.getStatus());
        assertEquals("User requested cancellation", result.getNotes());

        verify(bulkRequestRepository).save(any(BulkRequest.class));
    }

    // ========== NEGATIVE TEST CASES (Sad Path) ==========

    @Test
    @DisplayName("Should throw exception when bulk request not found")
    void updateRequestStatus_withNonExistentRequest_shouldThrowException() {
        // Given
        when(bulkRequestRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bulkRequestService.updateRequestStatus(999L, BulkRequestStatus.APPROVED));

        assertEquals("Bulk request not found with ID: 999", exception.getMessage());
        verify(bulkRequestRepository).findById(999L);
    }

    @Test
    @DisplayName("Should throw exception when collector not found")
    void assignCollector_withNonExistentCollector_shouldThrowException() {
        // Given
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bulkRequestService.assignCollector(1L, 999L));

        assertEquals("Collector not found with ID: 999", exception.getMessage());
        verify(bulkRequestRepository).findById(1L);
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("Should throw exception when confirming assignment without collector")
    void confirmCollectorAssignment_withoutCollector_shouldThrowException() {
        // Given
        testBulkRequest.setCollectorAssigned(null);
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bulkRequestService.confirmCollectorAssignment(1L));

        assertEquals("No collector assigned to this request", exception.getMessage());
        verify(bulkRequestRepository).findById(1L);
    }

    // ========== VALIDATION TESTS ==========

    @Test
    @DisplayName("Should throw exception for missing category")
    void validateBulkRequest_withNullCategory_shouldThrowException() {
        // Given
        testBulkRequestDTO.setCategory(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bulkRequestService.validateBulkRequest(testBulkRequestDTO));

        assertEquals("Category is required", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should throw exception for empty description")
    void validateBulkRequest_withEmptyDescription_shouldThrowException(String description) {
        // Given
        testBulkRequestDTO.setDescription(description);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bulkRequestService.validateBulkRequest(testBulkRequestDTO));

        assertEquals("Description is required", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should throw exception for empty street address")
    void validateBulkRequest_withEmptyStreetAddress_shouldThrowException(String streetAddress) {
        // Given
        testBulkRequestDTO.setStreetAddress(streetAddress);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bulkRequestService.validateBulkRequest(testBulkRequestDTO));

        assertEquals("Street address is required", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should throw exception for empty city")
    void validateBulkRequest_withEmptyCity_shouldThrowException(String city) {
        // Given
        testBulkRequestDTO.setCity(city);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bulkRequestService.validateBulkRequest(testBulkRequestDTO));

        assertEquals("City is required", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should throw exception for empty ZIP code")
    void validateBulkRequest_withEmptyZipCode_shouldThrowException(String zipCode) {
        // Given
        testBulkRequestDTO.setZipCode(zipCode);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bulkRequestService.validateBulkRequest(testBulkRequestDTO));

        assertEquals("ZIP code is required", exception.getMessage());
    }

    // ========== FEE CALCULATION TESTS ==========

    @Test
    @DisplayName("Should calculate fee correctly for furniture category")
    void calculateFee_withFurnitureCategory_shouldCalculateCorrectly() {
        // Given
        testBulkRequestDTO.setCategory(BulkCategory.FURNITURE);

        // When
        BulkRequestDTO result = bulkRequestService.calculateFee(testBulkRequestDTO);

        // Then
        assertEquals(1000.0, result.getBasePrice());
        assertEquals(500.0, result.getProcessingFee());
        assertEquals(75.0, result.getTaxAmount()); // 5% of (1000 + 500)
        assertEquals(1575.0, result.getTotalAmount());
    }

    @Test
    @DisplayName("Should throw exception when calculating fee without category")
    void calculateFee_withoutCategory_shouldThrowException() {
        // Given
        testBulkRequestDTO.setCategory(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bulkRequestService.calculateFee(testBulkRequestDTO));

        assertEquals("Category is required for fee calculation", exception.getMessage());
    }

    // ========== EQUIVALENCE CLASSES FOR FEE CALCULATION ==========

    @ParameterizedTest
    @CsvSource({
            "FURNITURE, 1000.0, 500.0, 75.0, 1575.0",
            "ELECTRONICS, 1500.0, 500.0, 100.0, 2100.0",
            "APPLIANCES, 800.0, 500.0, 65.0, 1365.0",
            "CLOTHING, 300.0, 500.0, 40.0, 840.0",
            "BOOKS, 200.0, 500.0, 35.0, 735.0"
    })
    @DisplayName("Should calculate fees correctly for all categories")
    void calculateFee_equivalenceClasses_shouldCalculateCorrectly(
            BulkCategory category, double expectedBasePrice, double expectedProcessingFee,
            double expectedTax, double expectedTotal) {
        // Given
        testBulkRequestDTO.setCategory(category);

        // When
        BulkRequestDTO result = bulkRequestService.calculateFee(testBulkRequestDTO);

        // Then
        assertEquals(expectedBasePrice, result.getBasePrice());
        assertEquals(expectedProcessingFee, result.getProcessingFee());
        assertEquals(expectedTax, result.getTaxAmount());
        assertEquals(expectedTotal, result.getTotalAmount());
    }

    // ========== PAYMENT PROCESSING TESTS ==========

    @Test
    @DisplayName("Should process successful payment")
    void processPayment_withSuccessfulPayment_shouldUpdateStatus() {
        // Given
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));
        when(bulkRequestRepository.save(any(BulkRequest.class))).thenReturn(testBulkRequest);
        doNothing().when(notificationService).notifyUserBulkRequest(any(User.class), anyString(), any(BulkRequest.class));
        doNothing().when(notificationService).notifyAuthorityBulkPayment(any(BulkRequest.class));

        // When
        BulkRequestDTO result = bulkRequestService.processPayment(1L, "CREDIT_CARD", "PAY-123");

        // Then
        assertNotNull(result);
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
        assertEquals(BulkRequestStatus.PAYMENT_COMPLETED, result.getStatus());
        assertEquals("CREDIT_CARD", result.getPaymentMethod());
        assertEquals("PAY-123", result.getPaymentReference());

        verify(bulkRequestRepository).save(any(BulkRequest.class));
        verify(notificationService).notifyUserBulkRequest(eq(testUser), anyString(), any(BulkRequest.class));
        verify(notificationService).notifyAuthorityBulkPayment(any(BulkRequest.class));
    }

    @Test
    @DisplayName("Should handle failed payment")
    void processPayment_withFailedPayment_shouldUpdateStatus() {
        // Given
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));
        when(bulkRequestRepository.save(any(BulkRequest.class))).thenReturn(testBulkRequest);
        doNothing().when(notificationService).notifyUserBulkRequest(any(User.class), anyString(), any(BulkRequest.class));

        // When
        BulkRequestDTO result = bulkRequestService.processPayment(1L, "INVALID_METHOD", "PAY-123");

        // Then
        assertNotNull(result);
        assertEquals(PaymentStatus.FAILED, result.getPaymentStatus());
        assertEquals(BulkRequestStatus.PAYMENT_PENDING, result.getStatus());
        assertEquals("INVALID_METHOD", result.getPaymentMethod());

        verify(bulkRequestRepository).save(any(BulkRequest.class));
        verify(notificationService).notifyUserBulkRequest(eq(testUser), anyString(), any(BulkRequest.class));
        verify(notificationService, never()).notifyAuthorityBulkPayment(any(BulkRequest.class));
    }

    // ========== STATUS TRANSITION TESTS ==========

    @Test
    @DisplayName("Should update payment status correctly")
    void updatePaymentStatus_withCompletedPayment_shouldUpdateRequestStatus() {
        // Given
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));
        when(bulkRequestRepository.save(any(BulkRequest.class))).thenReturn(testBulkRequest);

        // When
        BulkRequestDTO result = bulkRequestService.updatePaymentStatus(1L, PaymentStatus.COMPLETED, "PAY-123");

        // Then
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
        assertEquals(BulkRequestStatus.PAYMENT_COMPLETED, result.getStatus());
        assertEquals("PAY-123", result.getPaymentReference());

        verify(bulkRequestRepository).save(any(BulkRequest.class));
    }

    @Test
    @DisplayName("Should update payment status for failed payment")
    void updatePaymentStatus_withFailedPayment_shouldUpdateRequestStatus() {
        // Given
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));
        when(bulkRequestRepository.save(any(BulkRequest.class))).thenReturn(testBulkRequest);

        // When
        BulkRequestDTO result = bulkRequestService.updatePaymentStatus(1L, PaymentStatus.FAILED, null);

        // Then
        assertEquals(PaymentStatus.FAILED, result.getPaymentStatus());
        assertEquals(BulkRequestStatus.PAYMENT_PENDING, result.getStatus());

        verify(bulkRequestRepository).save(any(BulkRequest.class));
    }

    // ========== BOUNDARY/EDGE CASES ==========

    @Test
    @DisplayName("Should handle null notes in completion")
    void completeCollection_withNullNotes_shouldCompleteWithoutNotes() {
        // Given
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));
        when(bulkRequestRepository.save(any(BulkRequest.class))).thenReturn(testBulkRequest);
        doNothing().when(notificationService).notifyUserBulkRequest(any(User.class), anyString(), any(BulkRequest.class));

        // When
        BulkRequestDTO result = bulkRequestService.completeCollection(1L, null);

        // Then
        assertNotNull(result);
        assertEquals(BulkRequestStatus.COMPLETED, result.getStatus());
        assertNull(result.getNotes());

        verify(bulkRequestRepository).save(any(BulkRequest.class));
    }

    @Test
    @DisplayName("Should handle null reason in cancellation")
    void cancelRequest_withNullReason_shouldCancelWithoutReason() {
        // Given
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));
        when(bulkRequestRepository.save(any(BulkRequest.class))).thenReturn(testBulkRequest);

        // When
        BulkRequestDTO result = bulkRequestService.cancelRequest(1L, null);

        // Then
        assertNotNull(result);
        assertEquals(BulkRequestStatus.CANCELLED, result.getStatus());
        assertNull(result.getNotes());

        verify(bulkRequestRepository).save(any(BulkRequest.class));
    }

    @Test
    @DisplayName("Should handle null payment reference")
    void updatePaymentStatus_withNullPaymentReference_shouldNotSetReference() {
        // Given
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));
        when(bulkRequestRepository.save(any(BulkRequest.class))).thenReturn(testBulkRequest);

        // When
        BulkRequestDTO result = bulkRequestService.updatePaymentStatus(1L, PaymentStatus.PENDING, null);

        // Then
        assertEquals(PaymentStatus.PENDING, result.getPaymentStatus());
        assertNull(result.getPaymentReference());

        verify(bulkRequestRepository).save(any(BulkRequest.class));
    }

    // ========== COUNT QUERY TESTS ==========

    @Test
    @DisplayName("Should get request count by status")
    void getRequestCountByStatus_withValidStatus_shouldReturnCount() {
        // Given
        when(bulkRequestRepository.countByStatus(BulkRequestStatus.PENDING)).thenReturn(5L);

        // When
        long result = bulkRequestService.getRequestCountByStatus(BulkRequestStatus.PENDING);

        // Then
        assertEquals(5L, result);
        verify(bulkRequestRepository).countByStatus(BulkRequestStatus.PENDING);
    }

    @Test
    @DisplayName("Should get request count by user")
    void getRequestCountByUser_withValidUser_shouldReturnCount() {
        // Given
        when(bulkRequestRepository.countByUser(testUser)).thenReturn(3L);

        // When
        long result = bulkRequestService.getRequestCountByUser(testUser);

        // Then
        assertEquals(3L, result);
        verify(bulkRequestRepository).countByUser(testUser);
    }

    @Test
    @DisplayName("Should get request count by payment status")
    void getRequestCountByPaymentStatus_withValidStatus_shouldReturnCount() {
        // Given
        when(bulkRequestRepository.countByPaymentStatus(PaymentStatus.COMPLETED)).thenReturn(10L);

        // When
        long result = bulkRequestService.getRequestCountByPaymentStatus(PaymentStatus.COMPLETED);

        // Then
        assertEquals(10L, result);
        verify(bulkRequestRepository).countByPaymentStatus(PaymentStatus.COMPLETED);
    }

    // ========== RECENT REQUESTS TESTS ==========

    @Test
    @DisplayName("Should get recent requests for specified days")
    void getRecentRequests_withValidDays_shouldReturnRequests() {
        // Given
        List<BulkRequest> expectedRequests = Arrays.asList(testBulkRequest);
        when(bulkRequestRepository.findRecentRequests(any(LocalDateTime.class)))
                .thenReturn(expectedRequests);

        // When
        List<BulkRequestDTO> result = bulkRequestService.getRecentRequests(7);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBulkRequest.getId(), result.get(0).getId());
        verify(bulkRequestRepository).findRecentRequests(any(LocalDateTime.class));
    }

    // ========== DETERMINISM TESTS ==========

    @Test
    @DisplayName("Should produce consistent fee calculations for same inputs")
    void calculateFee_determinism_shouldProduceConsistentResults() {
        // Given
        testBulkRequestDTO.setCategory(BulkCategory.FURNITURE);

        // When
        BulkRequestDTO result1 = bulkRequestService.calculateFee(testBulkRequestDTO);
        BulkRequestDTO result2 = bulkRequestService.calculateFee(testBulkRequestDTO);
        BulkRequestDTO result3 = bulkRequestService.calculateFee(testBulkRequestDTO);

        // Then
        assertEquals(result1.getTotalAmount(), result2.getTotalAmount());
        assertEquals(result2.getTotalAmount(), result3.getTotalAmount());
        assertEquals(1575.0, result1.getTotalAmount());
    }

    // ========== SCHEDULE AND ASSIGNMENT TESTS ==========

    @Test
    @DisplayName("Should schedule and notify pickup with collector assignment")
    void scheduleAndNotifyPickup_withCollectorAssignment_shouldScheduleAndAssign() {
        // Given
        LocalDateTime scheduledDate = LocalDateTime.now().plusDays(1);
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testCollector));
        when(bulkRequestRepository.save(any(BulkRequest.class))).thenReturn(testBulkRequest);
        doNothing().when(notificationService).notifyCollectorBulkAssignment(any(User.class), any(BulkRequest.class));
        doNothing().when(notificationService).sendPickupScheduleNotification(any(User.class), any(BulkRequest.class));

        // When
        BulkRequestDTO result = bulkRequestService.scheduleAndNotifyPickup(1L, scheduledDate, 2L);

        // Then
        assertNotNull(result);
        assertEquals(BulkRequestStatus.SCHEDULED, result.getStatus());
        assertEquals(scheduledDate, result.getScheduledDate());
        assertEquals(2L, result.getCollectorAssigned());

        verify(bulkRequestRepository).save(any(BulkRequest.class));
        verify(notificationService).notifyCollectorBulkAssignment(eq(testCollector), any(BulkRequest.class));
        verify(notificationService).sendPickupScheduleNotification(eq(testUser), any(BulkRequest.class));
    }

    @Test
    @DisplayName("Should schedule pickup without assigning new collector if already assigned")
    void scheduleAndNotifyPickup_withExistingCollector_shouldNotReassign() {
        // Given
        testBulkRequest.setCollectorAssigned(2L);
        LocalDateTime scheduledDate = LocalDateTime.now().plusDays(1);
        when(bulkRequestRepository.findById(1L)).thenReturn(Optional.of(testBulkRequest));
        when(bulkRequestRepository.save(any(BulkRequest.class))).thenReturn(testBulkRequest);
        doNothing().when(notificationService).sendPickupScheduleNotification(any(User.class), any(BulkRequest.class));

        // When
        BulkRequestDTO result = bulkRequestService.scheduleAndNotifyPickup(1L, scheduledDate, 3L);

        // Then
        assertNotNull(result);
        assertEquals(BulkRequestStatus.SCHEDULED, result.getStatus());
        assertEquals(2L, result.getCollectorAssigned()); // Should keep existing collector

        verify(userRepository, never()).findById(anyLong());
        verify(notificationService, never()).notifyCollectorBulkAssignment(any(User.class), any(BulkRequest.class));
        verify(notificationService).sendPickupScheduleNotification(eq(testUser), any(BulkRequest.class));
    }
}

