package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.dto.BinDTO;
import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.repository.BinRepository;
import com.sliit.smartbin.smartbin.service.impl.BinServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for BinServiceImpl
 * 
 * Test Coverage Areas:
 * - Positive test cases (happy path)
 * - Negative test cases (sad path)
 * - Boundary/edge cases
 * - Error handling
 * - Status transitions
 * - Fill level calculations
 * - Geographic queries
 * - Equivalence classes
 * - Determinism
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BinServiceImpl Unit Tests")
class BinServiceImplTest {

    @Mock
    private BinRepository binRepository;

    @InjectMocks
    private BinServiceImpl binService;

    private Bin testBin;
    private BinDTO testBinDTO;

    @BeforeEach
    void setUp() {
        testBin = new Bin();
        testBin.setId(1L);
        testBin.setQrCode("QR123");
        testBin.setLocation("Test Location");
        testBin.setLatitude(6.9271);
        testBin.setLongitude(79.8612);
        testBin.setBinType(Bin.BinType.STANDARD);
        testBin.setStatus(Bin.BinStatus.EMPTY);
        testBin.setFillLevel(0);
        testBin.setAlertFlag(false);
        testBin.setLastEmptied(LocalDateTime.now().minusHours(24));

        testBinDTO = new BinDTO();
        testBinDTO.setQrCode("QR123");
        testBinDTO.setLocation("Test Location");
        testBinDTO.setLatitude(6.9271);
        testBinDTO.setLongitude(79.8612);
        testBinDTO.setBinType(Bin.BinType.STANDARD);
        testBinDTO.setStatus(Bin.BinStatus.EMPTY);
        testBinDTO.setFillLevel(0);
    }

    // ========== POSITIVE TEST CASES (Happy Path) ==========

    @Test
    @DisplayName("Should successfully create bin with valid inputs")
    void createBin_withValidInputs_shouldSucceed() {
        // Given
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        Bin result = binService.createBin(testBinDTO);

        // Then
        assertNotNull(result);
        assertEquals("QR123", result.getQrCode());
        assertEquals("Test Location", result.getLocation());
        assertEquals(Bin.BinType.STANDARD, result.getBinType());
        assertEquals(Bin.BinStatus.EMPTY, result.getStatus());
        assertEquals(0, result.getFillLevel());
        assertFalse(result.getAlertFlag());

        verify(binRepository).save(any(Bin.class));
    }

    @Test
    @DisplayName("Should create bin with default values when not specified")
    void createBin_withDefaultValues_shouldSetDefaults() {
        // Given
        testBinDTO.setStatus(null);
        testBinDTO.setFillLevel(null);
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        Bin result = binService.createBin(testBinDTO);

        // Then
        assertNotNull(result);
        assertEquals(Bin.BinStatus.EMPTY, result.getStatus());
        assertEquals(0, result.getFillLevel());
        assertFalse(result.getAlertFlag());

        verify(binRepository).save(any(Bin.class));
    }

    @Test
    @DisplayName("Should find bin by ID successfully")
    void findById_withValidId_shouldReturnBin() {
        // Given
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));

        // When
        Optional<Bin> result = binService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testBin.getId(), result.get().getId());
        verify(binRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find bin by QR code successfully")
    void findByQrCode_withValidQrCode_shouldReturnBin() {
        // Given
        when(binRepository.findByQrCode("QR123")).thenReturn(Optional.of(testBin));

        // When
        Optional<Bin> result = binService.findByQrCode("QR123");

        // Then
        assertTrue(result.isPresent());
        assertEquals("QR123", result.get().getQrCode());
        verify(binRepository).findByQrCode("QR123");
    }

    @Test
    @DisplayName("Should find all bins successfully")
    void findAllBins_shouldReturnAllBins() {
        // Given
        List<Bin> expectedBins = Arrays.asList(testBin);
        when(binRepository.findAll()).thenReturn(expectedBins);

        // When
        List<Bin> result = binService.findAllBins();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBin.getId(), result.get(0).getId());
        verify(binRepository).findAll();
    }

    @Test
    @DisplayName("Should find bins by status successfully")
    void findBinsByStatus_withValidStatus_shouldReturnBins() {
        // Given
        List<Bin> expectedBins = Arrays.asList(testBin);
        when(binRepository.findByStatus(Bin.BinStatus.EMPTY)).thenReturn(expectedBins);

        // When
        List<Bin> result = binService.findBinsByStatus(Bin.BinStatus.EMPTY);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Bin.BinStatus.EMPTY, result.get(0).getStatus());
        verify(binRepository).findByStatus(Bin.BinStatus.EMPTY);
    }

    @Test
    @DisplayName("Should find bins by type successfully")
    void findBinsByType_withValidType_shouldReturnBins() {
        // Given
        List<Bin> expectedBins = Arrays.asList(testBin);
        when(binRepository.findByBinType(Bin.BinType.STANDARD)).thenReturn(expectedBins);

        // When
        List<Bin> result = binService.findBinsByType(Bin.BinType.STANDARD);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Bin.BinType.STANDARD, result.get(0).getBinType());
        verify(binRepository).findByBinType(Bin.BinType.STANDARD);
    }

    @Test
    @DisplayName("Should find alerted bins successfully")
    void findAlertedBins_shouldReturnAlertedBins() {
        // Given
        testBin.setAlertFlag(true);
        List<Bin> expectedBins = Arrays.asList(testBin);
        when(binRepository.findByAlertFlagTrue()).thenReturn(expectedBins);

        // When
        List<Bin> result = binService.findAlertedBins();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAlertFlag());
        verify(binRepository).findByAlertFlagTrue();
    }

    @Test
    @DisplayName("Should find overdue bins successfully")
    void findOverdueBins_shouldReturnOverdueBins() {
        // Given
        List<Bin> expectedBins = Arrays.asList(testBin);
        when(binRepository.findOverdueBins(any(LocalDateTime.class))).thenReturn(expectedBins);

        // When
        List<Bin> result = binService.findOverdueBins();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(binRepository).findOverdueBins(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should find nearby bins successfully")
    void findNearbyBins_withValidCoordinates_shouldReturnNearbyBins() {
        // Given
        List<Bin> expectedBins = Arrays.asList(testBin);
        when(binRepository.findBinsInArea(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(expectedBins);

        // When
        List<Bin> result = binService.findNearbyBins(6.9271, 79.8612, 1.0);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(binRepository).findBinsInArea(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    // ========== NEGATIVE TEST CASES (Sad Path) ==========

    @Test
    @DisplayName("Should return empty when bin not found by ID")
    void findById_withNonExistentId_shouldReturnEmpty() {
        // Given
        when(binRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Bin> result = binService.findById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(binRepository).findById(999L);
    }

    @Test
    @DisplayName("Should return empty when bin not found by QR code")
    void findByQrCode_withNonExistentQrCode_shouldReturnEmpty() {
        // Given
        when(binRepository.findByQrCode("QR999")).thenReturn(Optional.empty());

        // When
        Optional<Bin> result = binService.findByQrCode("QR999");

        // Then
        assertFalse(result.isPresent());
        verify(binRepository).findByQrCode("QR999");
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent bin")
    void updateBinStatus_withNonExistentBin_shouldThrowException() {
        // Given
        when(binRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                binService.updateBinStatus(999L, Bin.BinStatus.FULL, 90));

        assertEquals("Bin not found with id: 999", exception.getMessage());
        verify(binRepository).findById(999L);
    }

    @Test
    @DisplayName("Should throw exception when updating fill level of non-existent bin")
    void updateBinFillLevel_withNonExistentBin_shouldThrowException() {
        // Given
        when(binRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                binService.updateBinFillLevel(999L, 75));

        assertEquals("Bin not found with id: 999", exception.getMessage());
        verify(binRepository).findById(999L);
    }

    // ========== BOUNDARY/EDGE CASES ==========

    @Test
    @DisplayName("Should handle minimum fill level (0)")
    void updateBinFillLevel_withMinimumFillLevel_shouldSetEmptyStatus() {
        // Given
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        Bin result = binService.updateBinFillLevel(1L, 0);

        // Then
        assertEquals(0, result.getFillLevel());
        assertEquals(Bin.BinStatus.EMPTY, result.getStatus());
        verify(binRepository).save(testBin);
    }

    @Test
    @DisplayName("Should handle maximum fill level (100)")
    void updateBinFillLevel_withMaximumFillLevel_shouldSetFullStatus() {
        // Given
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        Bin result = binService.updateBinFillLevel(1L, 100);

        // Then
        assertEquals(100, result.getFillLevel());
        assertEquals(Bin.BinStatus.FULL, result.getStatus());
        verify(binRepository).save(testBin);
    }

    @Test
    @DisplayName("Should handle fill level at boundary (50)")
    void updateBinFillLevel_withBoundaryFillLevel_shouldSetPartialStatus() {
        // Given
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        Bin result = binService.updateBinFillLevel(1L, 50);

        // Then
        assertEquals(50, result.getFillLevel());
        assertEquals(Bin.BinStatus.PARTIAL, result.getStatus());
        verify(binRepository).save(testBin);
    }

    @Test
    @DisplayName("Should handle fill level just below full threshold (89)")
    void updateBinFillLevel_withJustBelowFullThreshold_shouldSetPartialStatus() {
        // Given
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        Bin result = binService.updateBinFillLevel(1L, 89);

        // Then
        assertEquals(89, result.getFillLevel());
        assertEquals(Bin.BinStatus.PARTIAL, result.getStatus());
        verify(binRepository).save(testBin);
    }

    @Test
    @DisplayName("Should handle fill level at full threshold (90)")
    void updateBinFillLevel_withFullThreshold_shouldSetFullStatus() {
        // Given
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        Bin result = binService.updateBinFillLevel(1L, 90);

        // Then
        assertEquals(90, result.getFillLevel());
        assertEquals(Bin.BinStatus.FULL, result.getStatus());
        verify(binRepository).save(testBin);
    }

    // ========== STATUS TRANSITION TESTS ==========

    @Test
    @DisplayName("Should update bin status to empty and reset alert flag")
    void updateBinStatus_withEmptyStatus_shouldResetAlertFlag() {
        // Given
        testBin.setAlertFlag(true);
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        Bin result = binService.updateBinStatus(1L, Bin.BinStatus.EMPTY, 0);

        // Then
        assertEquals(Bin.BinStatus.EMPTY, result.getStatus());
        assertFalse(result.getAlertFlag());
        assertNotNull(result.getLastEmptied());
        verify(binRepository).save(testBin);
    }

    @Test
    @DisplayName("Should update bin status without changing fill level when null")
    void updateBinStatus_withNullFillLevel_shouldNotChangeFillLevel() {
        // Given
        testBin.setFillLevel(50);
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        Bin result = binService.updateBinStatus(1L, Bin.BinStatus.PARTIAL, null);

        // Then
        assertEquals(Bin.BinStatus.PARTIAL, result.getStatus());
        assertEquals(50, result.getFillLevel()); // Should remain unchanged
        verify(binRepository).save(testBin);
    }

    // ========== EQUIVALENCE CLASSES FOR FILL LEVEL ==========

    @ParameterizedTest
    @CsvSource({
            "0, EMPTY",
            "25, EMPTY",
            "49, EMPTY",
            "50, PARTIAL",
            "75, PARTIAL",
            "89, PARTIAL",
            "90, FULL",
            "95, FULL",
            "100, FULL"
    })
    @DisplayName("Should set correct status based on fill level")
    void updateBinFillLevel_equivalenceClasses_shouldSetCorrectStatus(int fillLevel, Bin.BinStatus expectedStatus) {
        // Given
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        Bin result = binService.updateBinFillLevel(1L, fillLevel);

        // Then
        assertEquals(fillLevel, result.getFillLevel());
        assertEquals(expectedStatus, result.getStatus());
        verify(binRepository).save(testBin);
    }

    // ========== OVERDUE BIN TESTS ==========

    @Test
    @DisplayName("Should return true for overdue bin")
    void isBinOverdue_withOverdueBin_shouldReturnTrue() {
        // Given
        testBin.setStatus(Bin.BinStatus.FULL);
        testBin.setLastEmptied(LocalDateTime.now().minusHours(50)); // More than 48 hours ago

        // When
        boolean result = binService.isBinOverdue(testBin);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false for recently emptied bin")
    void isBinOverdue_withRecentlyEmptiedBin_shouldReturnFalse() {
        // Given
        testBin.setStatus(Bin.BinStatus.FULL);
        testBin.setLastEmptied(LocalDateTime.now().minusHours(24)); // Less than 48 hours ago

        // When
        boolean result = binService.isBinOverdue(testBin);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false for non-full bin")
    void isBinOverdue_withNonFullBin_shouldReturnFalse() {
        // Given
        testBin.setStatus(Bin.BinStatus.PARTIAL);
        testBin.setLastEmptied(LocalDateTime.now().minusHours(50));

        // When
        boolean result = binService.isBinOverdue(testBin);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false for bin with null last emptied date")
    void isBinOverdue_withNullLastEmptied_shouldReturnFalse() {
        // Given
        testBin.setStatus(Bin.BinStatus.FULL);
        testBin.setLastEmptied(null);

        // When
        boolean result = binService.isBinOverdue(testBin);

        // Then
        assertFalse(result);
    }

    // ========== ALERT FLAG MANAGEMENT TESTS ==========

    @Test
    @DisplayName("Should check and set alert flags for overdue bins")
    void checkAndSetAlertFlags_withOverdueBins_shouldSetAlertFlags() {
        // Given
        List<Bin> overdueBins = Arrays.asList(testBin);
        when(binRepository.findOverdueBins(any(LocalDateTime.class))).thenReturn(overdueBins);
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        binService.checkAndSetAlertFlags();

        // Then
        verify(binRepository).findOverdueBins(any(LocalDateTime.class));
        verify(binRepository).save(testBin);
    }

    // ========== GEOGRAPHIC QUERY TESTS ==========

    @Test
    @DisplayName("Should calculate correct bounding box for nearby bins query")
    void findNearbyBins_shouldCalculateCorrectBoundingBox() {
        // Given
        double latitude = 6.9271;
        double longitude = 79.8612;
        double radiusKm = 1.0;
        List<Bin> expectedBins = Arrays.asList(testBin);
        when(binRepository.findBinsInArea(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(expectedBins);

        // When
        binService.findNearbyBins(latitude, longitude, radiusKm);

        // Then
        verify(binRepository).findBinsInArea(
                eq(6.9181), // latitude - latRange
                eq(6.9361), // latitude + latRange
                eq(79.8522), // longitude - lngRange
                eq(79.8702)  // longitude + lngRange
        );
    }

    @Test
    @DisplayName("Should handle zero radius for nearby bins query")
    void findNearbyBins_withZeroRadius_shouldUseMinimalBoundingBox() {
        // Given
        double latitude = 6.9271;
        double longitude = 79.8612;
        double radiusKm = 0.0;
        List<Bin> expectedBins = Arrays.asList(testBin);
        when(binRepository.findBinsInArea(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(expectedBins);

        // When
        binService.findNearbyBins(latitude, longitude, radiusKm);

        // Then
        verify(binRepository).findBinsInArea(
                eq(latitude), // Should use exact latitude
                eq(latitude), // Should use exact latitude
                eq(longitude), // Should use exact longitude
                eq(longitude)  // Should use exact longitude
        );
    }

    // ========== DELETE OPERATION TESTS ==========

    @Test
    @DisplayName("Should delete bin successfully")
    void deleteBin_withValidId_shouldDeleteBin() {
        // When
        binService.deleteBin(1L);

        // Then
        verify(binRepository).deleteById(1L);
    }

    // ========== DETERMINISM TESTS ==========

    @Test
    @DisplayName("Should produce consistent results for same inputs")
    void updateBinFillLevel_determinism_shouldProduceConsistentResults() {
        // Given
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        Bin result1 = binService.updateBinFillLevel(1L, 75);
        Bin result2 = binService.updateBinFillLevel(1L, 75);
        Bin result3 = binService.updateBinFillLevel(1L, 75);

        // Then
        assertEquals(result1.getFillLevel(), result2.getFillLevel());
        assertEquals(result2.getFillLevel(), result3.getFillLevel());
        assertEquals(75, result1.getFillLevel());
        assertEquals(Bin.BinStatus.PARTIAL, result1.getStatus());
    }

    @Test
    @DisplayName("Should produce consistent overdue check results")
    void isBinOverdue_determinism_shouldProduceConsistentResults() {
        // Given
        testBin.setStatus(Bin.BinStatus.FULL);
        testBin.setLastEmptied(LocalDateTime.now().minusHours(50));

        // When
        boolean result1 = binService.isBinOverdue(testBin);
        boolean result2 = binService.isBinOverdue(testBin);
        boolean result3 = binService.isBinOverdue(testBin);

        // Then
        assertEquals(result1, result2);
        assertEquals(result2, result3);
        assertTrue(result1); // All should be true for overdue bin
    }

    // ========== EDGE CASES FOR FILL LEVEL ==========

    @Test
    @DisplayName("Should handle negative fill level")
    void updateBinFillLevel_withNegativeFillLevel_shouldSetEmptyStatus() {
        // Given
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        Bin result = binService.updateBinFillLevel(1L, -10);

        // Then
        assertEquals(-10, result.getFillLevel());
        assertEquals(Bin.BinStatus.EMPTY, result.getStatus()); // Negative should be treated as empty
        verify(binRepository).save(testBin);
    }

    @Test
    @DisplayName("Should handle fill level above 100")
    void updateBinFillLevel_withFillLevelAbove100_shouldSetFullStatus() {
        // Given
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        Bin result = binService.updateBinFillLevel(1L, 150);

        // Then
        assertEquals(150, result.getFillLevel());
        assertEquals(Bin.BinStatus.FULL, result.getStatus()); // Above 90 should be full
        verify(binRepository).save(testBin);
    }

    // ========== ALL BIN TYPES TESTS ==========

    @ParameterizedTest
    @ValueSource(strings = {"STANDARD", "RECYCLING", "BULK"})
    @DisplayName("Should handle all bin types correctly")
    void createBin_withAllBinTypes_shouldCreateCorrectly(Bin.BinType binType) {
        // Given
        testBinDTO.setBinType(binType);
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        Bin result = binService.createBin(testBinDTO);

        // Then
        assertNotNull(result);
        verify(binRepository).save(any(Bin.class));
    }

    // ========== ALL BIN STATUSES TESTS ==========

    @ParameterizedTest
    @ValueSource(strings = {"EMPTY", "PARTIAL", "FULL", "OVERDUE"})
    @DisplayName("Should handle all bin statuses correctly")
    void updateBinStatus_withAllStatuses_shouldUpdateCorrectly(Bin.BinStatus status) {
        // Given
        when(binRepository.findById(1L)).thenReturn(Optional.of(testBin));
        when(binRepository.save(any(Bin.class))).thenReturn(testBin);

        // When
        Bin result = binService.updateBinStatus(1L, status, 50);

        // Then
        assertEquals(status, result.getStatus());
        verify(binRepository).save(testBin);
    }
}

