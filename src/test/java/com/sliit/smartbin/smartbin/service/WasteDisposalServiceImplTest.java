package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.model.WasteDisposal;
import com.sliit.smartbin.smartbin.repository.BinRepository;
import com.sliit.smartbin.smartbin.repository.WasteDisposalRepository;
import com.sliit.smartbin.smartbin.service.impl.WasteDisposalServiceImpl;
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
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for WasteDisposalServiceImpl
 * 
 * Test Coverage Areas:
 * - Positive test cases (happy path)
 * - Negative test cases (sad path)
 * - Boundary/edge cases
 * - Error handling and retry logic
 * - QR code validation
 * - Fill level thresholds
 * - Equivalence classes
 * - Determinism
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WasteDisposalServiceImpl Unit Tests")
class WasteDisposalServiceImplTest {

    @Mock
    private WasteDisposalRepository wasteDisposalRepository;

    @Mock
    private BinRepository binRepository;

    @Mock
    private BinService binService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private WasteDisposalServiceImpl wasteDisposalService;

    private User testUser;
    private Bin testBin;
    private WasteDisposal testDisposal;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");

        testBin = new Bin();
        testBin.setId(1L);
        testBin.setQrCode("QR123");
        testBin.setLocation("Test Location");
        testBin.setLatitude(6.9271);
        testBin.setLongitude(79.8612);
        testBin.setFillLevel(30);
        testBin.setStatus(Bin.BinStatus.PARTIAL);

        testDisposal = new WasteDisposal();
        testDisposal.setId(1L);
        testDisposal.setUser(testUser);
        testDisposal.setBin(testBin);
        testDisposal.setReportedFillLevel(80);
        testDisposal.setNotes("Test disposal");
        testDisposal.setStatus(WasteDisposal.DisposalStatus.CONFIRMED);
    }

    // ========== POSITIVE TEST CASES (Happy Path) ==========

    @Test
    @DisplayName("Should successfully submit disposal with valid inputs")
    void submitDisposal_withValidInputs_shouldSucceed() {
        // Given
        when(binRepository.findByQrCode("QR123")).thenReturn(Optional.of(testBin));
        when(wasteDisposalRepository.save(any(WasteDisposal.class))).thenReturn(testDisposal);
        doNothing().when(binService).updateBinFillLevel(anyLong(), anyInt());
        doNothing().when(notificationService).sendBinAlertNotification(any(Bin.class));

        // When
        WasteDisposal result = wasteDisposalService.submitDisposal(testUser, "QR123", 80, "Test notes");

        // Then
        assertNotNull(result);
        assertEquals(WasteDisposal.DisposalStatus.CONFIRMED, result.getStatus());
        assertEquals(80, result.getReportedFillLevel());
        assertEquals("Test notes", result.getNotes());

        verify(binRepository).findByQrCode("QR123");
        verify(wasteDisposalRepository, times(2)).save(any(WasteDisposal.class));
        verify(binService).updateBinFillLevel(1L, 80);
        verify(notificationService).sendBinAlertNotification(testBin);
    }

    @Test
    @DisplayName("Should get user disposals successfully")
    void getUserDisposals_withValidUser_shouldReturnDisposals() {
        // Given
        List<WasteDisposal> expectedDisposals = Arrays.asList(testDisposal);
        when(wasteDisposalRepository.findByUserOrderByCreatedAtDesc(testUser))
                .thenReturn(expectedDisposals);

        // When
        List<WasteDisposal> result = wasteDisposalService.getUserDisposals(testUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDisposal.getId(), result.get(0).getId());
        verify(wasteDisposalRepository).findByUserOrderByCreatedAtDesc(testUser);
    }

    @Test
    @DisplayName("Should validate correct QR code format")
    void validateBinQrCode_withValidFormat_shouldReturnTrue() {
        // Valid QR codes
        assertTrue(wasteDisposalService.validateBinQrCode("QR123"));
        assertTrue(wasteDisposalService.validateBinQrCode("QR1234"));
        assertTrue(wasteDisposalService.validateBinQrCode("QR999"));
        assertTrue(wasteDisposalService.validateBinQrCode("QR0001"));
    }

    @Test
    @DisplayName("Should check if bin needs collection when fill level is above threshold")
    void checkIfBinNeedsCollection_withHighFillLevel_shouldReturnTrue() {
        // Given
        testBin.setFillLevel(85); // Above 80% threshold
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));

        // When
        boolean result = wasteDisposalService.checkIfBinNeedsCollection(1L);

        // Then
        assertTrue(result);
        verify(binRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return false when bin fill level is below threshold")
    void checkIfBinNeedsCollection_withLowFillLevel_shouldReturnFalse() {
        // Given
        testBin.setFillLevel(50); // Below 80% threshold
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));

        // When
        boolean result = wasteDisposalService.checkIfBinNeedsCollection(1L);

        // Then
        assertFalse(result);
        verify(binRepository).findById(1L);
    }

    // ========== NEGATIVE TEST CASES (Sad Path) ==========

    @Test
    @DisplayName("Should throw exception for invalid QR code")
    void submitDisposal_withInvalidQrCode_shouldThrowException() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                wasteDisposalService.submitDisposal(testUser, "INVALID", 80, "Test"));

        assertEquals("Invalid bin QR code", exception.getMessage());
        verify(binRepository, never()).findByQrCode(any());
        verify(wasteDisposalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when bin not found")
    void submitDisposal_withNonExistentBin_shouldThrowException() {
        // Given
        when(binRepository.findByQrCode("QR999")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                wasteDisposalService.submitDisposal(testUser, "QR999", 80, "Test"));

        assertTrue(exception.getMessage().contains("Bin not found"));
        verify(binRepository).findByQrCode("QR999");
        verify(wasteDisposalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle database errors with retry logic")
    void submitDisposal_withDatabaseError_shouldRetryAndFail() {
        // Given
        when(binRepository.findByQrCode("QR123")).thenReturn(Optional.of(testBin));
        when(wasteDisposalRepository.save(any(WasteDisposal.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                wasteDisposalService.submitDisposal(testUser, "QR123", 80, "Test"));

        assertEquals("Unable to submit disposal", exception.getMessage());
        
        // Verify retry attempts (3 times)
        verify(wasteDisposalRepository, times(3)).save(any(WasteDisposal.class));
    }

    @Test
    @DisplayName("Should return false when bin not found for collection check")
    void checkIfBinNeedsCollection_withNonExistentBin_shouldReturnFalse() {
        // Given
        when(binRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        boolean result = wasteDisposalService.checkIfBinNeedsCollection(999L);

        // Then
        assertFalse(result);
        verify(binRepository).findById(999L);
    }

    // ========== BOUNDARY/EDGE CASES ==========

    @Test
    @DisplayName("Should handle fill level at exact threshold")
    void checkIfBinNeedsCollection_withExactThreshold_shouldReturnTrue() {
        // Given
        testBin.setFillLevel(80); // Exactly at threshold
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));

        // When
        boolean result = wasteDisposalService.checkIfBinNeedsCollection(1L);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should handle fill level just below threshold")
    void checkIfBinNeedsCollection_withJustBelowThreshold_shouldReturnFalse() {
        // Given
        testBin.setFillLevel(79); // Just below threshold
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));

        // When
        boolean result = wasteDisposalService.checkIfBinNeedsCollection(1L);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle maximum fill level")
    void checkIfBinNeedsCollection_withMaximumFillLevel_shouldReturnTrue() {
        // Given
        testBin.setFillLevel(100); // Maximum fill level
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));

        // When
        boolean result = wasteDisposalService.checkIfBinNeedsCollection(1L);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should handle zero fill level")
    void checkIfBinNeedsCollection_withZeroFillLevel_shouldReturnFalse() {
        // Given
        testBin.setFillLevel(0); // Empty bin
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));

        // When
        boolean result = wasteDisposalService.checkIfBinNeedsCollection(1L);

        // Then
        assertFalse(result);
    }

    // ========== QR CODE VALIDATION EDGE CASES ==========

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  ", "QR", "QR12", "QR12345", "qr123", "QR12a", "QR-123"})
    @DisplayName("Should reject invalid QR code formats")
    void validateBinQrCode_withInvalidFormats_shouldReturnFalse(String qrCode) {
        assertFalse(wasteDisposalService.validateBinQrCode(qrCode));
    }

    @ParameterizedTest
    @ValueSource(strings = {"QR123", "QR1234", "QR000", "QR999"})
    @DisplayName("Should accept valid QR code formats")
    void validateBinQrCode_withValidFormats_shouldReturnTrue(String qrCode) {
        assertTrue(wasteDisposalService.validateBinQrCode(qrCode));
    }

    // ========== EQUIVALENCE CLASSES ==========

    @ParameterizedTest
    @CsvSource({
            "0, false",
            "25, false",
            "50, false",
            "79, false",
            "80, true",
            "85, true",
            "90, true",
            "95, true",
            "100, true"
    })
    @DisplayName("Should correctly classify fill levels for collection need")
    void checkIfBinNeedsCollection_equivalenceClasses_shouldClassifyCorrectly(int fillLevel, boolean needsCollection) {
        // Given
        testBin.setFillLevel(fillLevel);
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));

        // When
        boolean result = wasteDisposalService.checkIfBinNeedsCollection(1L);

        // Then
        assertEquals(needsCollection, result);
    }

    // ========== FILL LEVEL BOUNDARY TESTING ==========

    @Test
    @DisplayName("Should handle negative fill level")
    void checkIfBinNeedsCollection_withNegativeFillLevel_shouldReturnFalse() {
        // Given
        testBin.setFillLevel(-10);
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));

        // When
        boolean result = wasteDisposalService.checkIfBinNeedsCollection(1L);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle fill level above 100")
    void checkIfBinNeedsCollection_withFillLevelAbove100_shouldReturnTrue() {
        // Given
        testBin.setFillLevel(150); // Invalid but above threshold
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));

        // When
        boolean result = wasteDisposalService.checkIfBinNeedsCollection(1L);

        // Then
        assertTrue(result);
    }

    // ========== RETRY LOGIC TESTING ==========

    @Test
    @DisplayName("Should succeed on second retry attempt")
    void submitDisposal_withRetrySuccess_shouldSucceedOnSecondAttempt() {
        // Given
        when(binRepository.findByQrCode("QR123")).thenReturn(Optional.of(testBin));
        when(wasteDisposalRepository.save(any(WasteDisposal.class)))
                .thenThrow(new RuntimeException("Temporary error"))
                .thenReturn(testDisposal);
        doNothing().when(binService).updateBinFillLevel(anyLong(), anyInt());

        // When
        WasteDisposal result = wasteDisposalService.submitDisposal(testUser, "QR123", 80, "Test");

        // Then
        assertNotNull(result);
        assertEquals(WasteDisposal.DisposalStatus.CONFIRMED, result.getStatus());
        verify(wasteDisposalRepository, times(2)).save(any(WasteDisposal.class));
    }

    @Test
    @DisplayName("Should fail after maximum retry attempts")
    void submitDisposal_withMaxRetryAttempts_shouldFailAfterThreeAttempts() {
        // Given
        when(binRepository.findByQrCode("QR123")).thenReturn(Optional.of(testBin));
        when(wasteDisposalRepository.save(any(WasteDisposal.class)))
                .thenThrow(new RuntimeException("Persistent database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                wasteDisposalService.submitDisposal(testUser, "QR123", 80, "Test"));

        assertEquals("Unable to submit disposal", exception.getMessage());
        verify(wasteDisposalRepository, times(3)).save(any(WasteDisposal.class));
    }

    // ========== NOTIFICATION ERROR HANDLING ==========

    @Test
    @DisplayName("Should handle notification service failure gracefully")
    void submitDisposal_withNotificationFailure_shouldStillSucceed() {
        // Given
        when(binRepository.findByQrCode("QR123")).thenReturn(Optional.of(testBin));
        when(wasteDisposalRepository.save(any(WasteDisposal.class))).thenReturn(testDisposal);
        doNothing().when(binService).updateBinFillLevel(anyLong(), anyInt());
        doThrow(new RuntimeException("Notification service down"))
                .when(notificationService).sendBinAlertNotification(any(Bin.class));

        // When
        WasteDisposal result = wasteDisposalService.submitDisposal(testUser, "QR123", 85, "Test");

        // Then
        assertNotNull(result);
        assertEquals(WasteDisposal.DisposalStatus.CONFIRMED, result.getStatus());
        verify(notificationService).sendBinAlertNotification(testBin);
    }

    // ========== DETERMINISM TESTS ==========

    @Test
    @DisplayName("Should produce consistent results for same inputs")
    void validateBinQrCode_determinism_shouldProduceConsistentResults() {
        boolean result1 = wasteDisposalService.validateBinQrCode("QR123");
        boolean result2 = wasteDisposalService.validateBinQrCode("QR123");
        boolean result3 = wasteDisposalService.validateBinQrCode("QR123");

        assertEquals(result1, result2);
        assertEquals(result2, result3);
        assertTrue(result1); // All should be true for valid QR code
    }

    @Test
    @DisplayName("Should produce consistent collection check results")
    void checkIfBinNeedsCollection_determinism_shouldProduceConsistentResults() {
        // Given
        testBin.setFillLevel(85);
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));

        // When
        boolean result1 = wasteDisposalService.checkIfBinNeedsCollection(1L);
        boolean result2 = wasteDisposalService.checkIfBinNeedsCollection(1L);
        boolean result3 = wasteDisposalService.checkIfBinNeedsCollection(1L);

        // Then
        assertEquals(result1, result2);
        assertEquals(result2, result3);
        assertTrue(result1); // All should be true for fill level > 80
    }

    // ========== THREAD SAFETY AND INTERRUPTION HANDLING ==========

    @Test
    @DisplayName("Should handle thread interruption during retry sleep")
    void submitDisposal_withThreadInterruption_shouldHandleGracefully() {
        // Given
        when(binRepository.findByQrCode("QR123")).thenReturn(Optional.of(testBin));
        when(wasteDisposalRepository.save(any(WasteDisposal.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Simulate thread interruption
        Thread.currentThread().interrupt();

        // When & Then
        assertThrows(RuntimeException.class, () ->
                wasteDisposalService.submitDisposal(testUser, "QR123", 80, "Test"));

        // Verify thread interruption status is preserved
        assertTrue(Thread.interrupted());
    }

    // ========== INTEGRATION WITH BIN SERVICE ==========

    @Test
    @DisplayName("Should call bin service to update fill level")
    void submitDisposal_shouldUpdateBinFillLevel() {
        // Given
        when(binRepository.findByQrCode("QR123")).thenReturn(Optional.of(testBin));
        when(wasteDisposalRepository.save(any(WasteDisposal.class))).thenReturn(testDisposal);
        doNothing().when(binService).updateBinFillLevel(anyLong(), anyInt());

        // When
        wasteDisposalService.submitDisposal(testUser, "QR123", 75, "Test");

        // Then
        verify(binService).updateBinFillLevel(1L, 75);
    }

    @Test
    @DisplayName("Should not send notification when bin doesn't need collection")
    void submitDisposal_withLowFillLevel_shouldNotSendNotification() {
        // Given
        when(binRepository.findByQrCode("QR123")).thenReturn(Optional.of(testBin));
        when(wasteDisposalRepository.save(any(WasteDisposal.class))).thenReturn(testDisposal);
        doNothing().when(binService).updateBinFillLevel(anyLong(), anyInt());

        // When
        wasteDisposalService.submitDisposal(testUser, "QR123", 50, "Test");

        // Then
        verify(notificationService, never()).sendBinAlertNotification(any(Bin.class));
    }
}

