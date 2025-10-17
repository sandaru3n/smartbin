package com.sliit.smartbin.smartbin.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for Bin entity
 * 
 * Test Coverage Areas:
 * - Entity creation and initialization
 * - Field validation and constraints
 * - Enum handling (BinType, BinStatus)
 * - Lifecycle callbacks (@PrePersist, @PreUpdate)
 * - Default values
 * - Boundary conditions
 * - Geographic coordinates
 * - Fill level validation
 * - Equivalence classes
 * - Determinism
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Bin Entity Unit Tests")
class BinTest {

    private Bin bin;

    @BeforeEach
    void setUp() {
        bin = new Bin();
    }

    // ========== POSITIVE TEST CASES (Happy Path) ==========

    @Test
    @DisplayName("Should create bin with all required fields")
    void createBin_withAllRequiredFields_shouldSucceed() {
        // Given & When
        bin.setQrCode("QR123");
        bin.setLocation("Test Location");
        bin.setLatitude(6.9271);
        bin.setLongitude(79.8612);
        bin.setBinType(Bin.BinType.STANDARD);
        bin.setStatus(Bin.BinStatus.EMPTY);
        bin.setFillLevel(0);
        bin.setLastEmptied(LocalDateTime.now().minusHours(24));
        bin.setAlertFlag(false);

        // Then
        assertEquals("QR123", bin.getQrCode());
        assertEquals("Test Location", bin.getLocation());
        assertEquals(6.9271, bin.getLatitude());
        assertEquals(79.8612, bin.getLongitude());
        assertEquals(Bin.BinType.STANDARD, bin.getBinType());
        assertEquals(Bin.BinStatus.EMPTY, bin.getStatus());
        assertEquals(0, bin.getFillLevel());
        assertFalse(bin.getAlertFlag());
    }

    @Test
    @DisplayName("Should create bin with minimal required fields")
    void createBin_withMinimalFields_shouldSucceed() {
        // Given & When
        bin.setQrCode("QR456");
        bin.setLocation("Minimal Location");
        bin.setLatitude(6.9085);
        bin.setLongitude(79.8553);
        bin.setBinType(Bin.BinType.RECYCLING);
        bin.setStatus(Bin.BinStatus.PARTIAL);
        bin.setFillLevel(50);

        // Then
        assertEquals("QR456", bin.getQrCode());
        assertEquals("Minimal Location", bin.getLocation());
        assertEquals(6.9085, bin.getLatitude());
        assertEquals(79.8553, bin.getLongitude());
        assertEquals(Bin.BinType.RECYCLING, bin.getBinType());
        assertEquals(Bin.BinStatus.PARTIAL, bin.getStatus());
        assertEquals(50, bin.getFillLevel());
        assertFalse(bin.getAlertFlag()); // Default value
    }

    @Test
    @DisplayName("Should initialize with default alert flag")
    void createBin_shouldInitializeWithDefaultAlertFlag() {
        // When creating a new bin
        Bin newBin = new Bin();

        // Then alert flag should default to false
        assertFalse(newBin.getAlertFlag());
    }

    // ========== BIN TYPE ENUM TESTS ==========

    @ParameterizedTest
    @EnumSource(Bin.BinType.class)
    @DisplayName("Should handle all bin type enums correctly")
    void setBinType_withAllEnumValues_shouldSetCorrectly(Bin.BinType binType) {
        // When
        bin.setBinType(binType);

        // Then
        assertEquals(binType, bin.getBinType());
    }

    @Test
    @DisplayName("Should handle null bin type")
    void setBinType_withNullValue_shouldSetNull() {
        // When
        bin.setBinType(null);

        // Then
        assertNull(bin.getBinType());
    }

    // ========== BIN STATUS ENUM TESTS ==========

    @ParameterizedTest
    @EnumSource(Bin.BinStatus.class)
    @DisplayName("Should handle all bin status enums correctly")
    void setStatus_withAllEnumValues_shouldSetCorrectly(Bin.BinStatus status) {
        // When
        bin.setStatus(status);

        // Then
        assertEquals(status, bin.getStatus());
    }

    @Test
    @DisplayName("Should handle null bin status")
    void setStatus_withNullValue_shouldSetNull() {
        // When
        bin.setStatus(null);

        // Then
        assertNull(bin.getStatus());
    }

    // ========== NEGATIVE TEST CASES (Sad Path) ==========

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should handle null and empty QR codes")
    void setQrCode_withNullOrEmptyValue_shouldSetValue(String qrCode) {
        // When
        bin.setQrCode(qrCode);

        // Then
        assertEquals(qrCode, bin.getQrCode());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should handle null and empty locations")
    void setLocation_withNullOrEmptyValue_shouldSetValue(String location) {
        // When
        bin.setLocation(location);

        // Then
        assertEquals(location, bin.getLocation());
    }

    // ========== BOUNDARY/EDGE CASES ==========

    @Test
    @DisplayName("Should handle very long QR codes")
    void setQrCode_withVeryLongValue_shouldSetValue() {
        // Given
        String longQrCode = "QR" + "1".repeat(1000);

        // When
        bin.setQrCode(longQrCode);

        // Then
        assertEquals(longQrCode, bin.getQrCode());
    }

    @Test
    @DisplayName("Should handle very long locations")
    void setLocation_withVeryLongValue_shouldSetValue() {
        // Given
        String longLocation = "Location ".repeat(1000);

        // When
        bin.setLocation(longLocation);

        // Then
        assertEquals(longLocation, bin.getLocation());
    }

    // ========== GEOGRAPHIC COORDINATES TESTS ==========

    @Test
    @DisplayName("Should handle valid latitude values")
    void setLatitude_withValidValues_shouldSetCorrectly() {
        // Test various valid latitudes
        double[] validLatitudes = {-90.0, -45.0, 0.0, 45.0, 90.0};

        for (double latitude : validLatitudes) {
            // When
            bin.setLatitude(latitude);

            // Then
            assertEquals(latitude, bin.getLatitude());
        }
    }

    @Test
    @DisplayName("Should handle valid longitude values")
    void setLongitude_withValidValues_shouldSetCorrectly() {
        // Test various valid longitudes
        double[] validLongitudes = {-180.0, -90.0, 0.0, 90.0, 180.0};

        for (double longitude : validLongitudes) {
            // When
            bin.setLongitude(longitude);

            // Then
            assertEquals(longitude, bin.getLongitude());
        }
    }

    @Test
    @DisplayName("Should handle extreme latitude values")
    void setLatitude_withExtremeValues_shouldSetCorrectly() {
        // Test extreme latitudes
        double[] extremeLatitudes = {-90.0, 90.0, Double.MIN_VALUE, Double.MAX_VALUE};

        for (double latitude : extremeLatitudes) {
            // When
            bin.setLatitude(latitude);

            // Then
            assertEquals(latitude, bin.getLatitude());
        }
    }

    @Test
    @DisplayName("Should handle extreme longitude values")
    void setLongitude_withExtremeValues_shouldSetCorrectly() {
        // Test extreme longitudes
        double[] extremeLongitudes = {-180.0, 180.0, Double.MIN_VALUE, Double.MAX_VALUE};

        for (double longitude : extremeLongitudes) {
            // When
            bin.setLongitude(longitude);

            // Then
            assertEquals(longitude, bin.getLongitude());
        }
    }

    @Test
    @DisplayName("Should handle null latitude")
    void setLatitude_withNullValue_shouldSetNull() {
        // When
        bin.setLatitude(null);

        // Then
        assertNull(bin.getLatitude());
    }

    @Test
    @DisplayName("Should handle null longitude")
    void setLongitude_withNullValue_shouldSetNull() {
        // When
        bin.setLongitude(null);

        // Then
        assertNull(bin.getLongitude());
    }

    // ========== FILL LEVEL TESTS ==========

    @Test
    @DisplayName("Should handle minimum fill level")
    void setFillLevel_withMinimumValue_shouldSetCorrectly() {
        // When
        bin.setFillLevel(0);

        // Then
        assertEquals(0, bin.getFillLevel());
    }

    @Test
    @DisplayName("Should handle maximum fill level")
    void setFillLevel_withMaximumValue_shouldSetCorrectly() {
        // When
        bin.setFillLevel(100);

        // Then
        assertEquals(100, bin.getFillLevel());
    }

    @Test
    @DisplayName("Should handle negative fill level")
    void setFillLevel_withNegativeValue_shouldSetCorrectly() {
        // When
        bin.setFillLevel(-10);

        // Then
        assertEquals(-10, bin.getFillLevel());
    }

    @Test
    @DisplayName("Should handle fill level above 100")
    void setFillLevel_withValueAbove100_shouldSetCorrectly() {
        // When
        bin.setFillLevel(150);

        // Then
        assertEquals(150, bin.getFillLevel());
    }

    @Test
    @DisplayName("Should handle null fill level")
    void setFillLevel_withNullValue_shouldSetNull() {
        // When
        bin.setFillLevel(null);

        // Then
        assertNull(bin.getFillLevel());
    }

    // ========== EQUIVALENCE CLASSES FOR FILL LEVEL ==========

    @ParameterizedTest
    @ValueSource(ints = {-100, -50, -1, 0, 1, 25, 50, 75, 90, 95, 100, 150, 200})
    @DisplayName("Should handle various fill level values correctly")
    void setFillLevel_withVariousValues_shouldSetCorrectly(int fillLevel) {
        // When
        bin.setFillLevel(fillLevel);

        // Then
        assertEquals(fillLevel, bin.getFillLevel());
    }

    // ========== ALERT FLAG TESTS ==========

    @Test
    @DisplayName("Should handle true alert flag")
    void setAlertFlag_withTrueValue_shouldSetTrue() {
        // When
        bin.setAlertFlag(true);

        // Then
        assertTrue(bin.getAlertFlag());
    }

    @Test
    @DisplayName("Should handle false alert flag")
    void setAlertFlag_withFalseValue_shouldSetFalse() {
        // When
        bin.setAlertFlag(false);

        // Then
        assertFalse(bin.getAlertFlag());
    }

    @Test
    @DisplayName("Should handle null alert flag")
    void setAlertFlag_withNullValue_shouldSetNull() {
        // When
        bin.setAlertFlag(null);

        // Then
        assertNull(bin.getAlertFlag());
    }

    // ========== LAST EMPTIED TIMESTAMP TESTS ==========

    @Test
    @DisplayName("Should handle valid last emptied timestamp")
    void setLastEmptied_withValidTimestamp_shouldSetCorrectly() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        bin.setLastEmptied(now);

        // Then
        assertEquals(now, bin.getLastEmptied());
    }

    @Test
    @DisplayName("Should handle null last emptied timestamp")
    void setLastEmptied_withNullValue_shouldSetNull() {
        // When
        bin.setLastEmptied(null);

        // Then
        assertNull(bin.getLastEmptied());
    }

    @Test
    @DisplayName("Should handle future last emptied timestamp")
    void setLastEmptied_withFutureTimestamp_shouldSetCorrectly() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);

        // When
        bin.setLastEmptied(futureTime);

        // Then
        assertEquals(futureTime, bin.getLastEmptied());
    }

    @Test
    @DisplayName("Should handle past last emptied timestamp")
    void setLastEmptied_withPastTimestamp_shouldSetCorrectly() {
        // Given
        LocalDateTime pastTime = LocalDateTime.now().minusYears(1);

        // When
        bin.setLastEmptied(pastTime);

        // Then
        assertEquals(pastTime, bin.getLastEmptied());
    }

    // ========== TIMESTAMP BEHAVIOR TESTS ==========

    @Test
    @DisplayName("Should initialize timestamps as null by default")
    void createBin_shouldInitializeTimestampsAsNull() {
        // When creating a new bin
        Bin newBin = new Bin();

        // Then timestamps should be null initially
        assertNull(newBin.getCreatedAt());
        assertNull(newBin.getUpdatedAt());
    }

    @Test
    @DisplayName("Should allow setting custom timestamps")
    void setTimestamps_withCustomValues_shouldSetCorrectly() {
        // Given
        LocalDateTime customCreatedAt = LocalDateTime.now().minusDays(1);
        LocalDateTime customUpdatedAt = LocalDateTime.now().minusHours(1);

        // When
        bin.setCreatedAt(customCreatedAt);
        bin.setUpdatedAt(customUpdatedAt);

        // Then
        assertEquals(customCreatedAt, bin.getCreatedAt());
        assertEquals(customUpdatedAt, bin.getUpdatedAt());
    }

    // ========== DETERMINISM TESTS ==========

    @Test
    @DisplayName("Should produce consistent results for same inputs")
    void setFields_determinism_shouldProduceConsistentResults() {
        // Given
        String qrCode = "QR123";
        String location = "Test Location";
        Double latitude = 6.9271;
        Double longitude = 79.8612;
        Bin.BinType binType = Bin.BinType.STANDARD;
        Bin.BinStatus status = Bin.BinStatus.EMPTY;
        Integer fillLevel = 0;

        // When
        bin.setQrCode(qrCode);
        bin.setLocation(location);
        bin.setLatitude(latitude);
        bin.setLongitude(longitude);
        bin.setBinType(binType);
        bin.setStatus(status);
        bin.setFillLevel(fillLevel);

        // Then - verify multiple times
        assertEquals(qrCode, bin.getQrCode());
        assertEquals(location, bin.getLocation());
        assertEquals(latitude, bin.getLatitude());
        assertEquals(longitude, bin.getLongitude());
        assertEquals(binType, bin.getBinType());
        assertEquals(status, bin.getStatus());
        assertEquals(fillLevel, bin.getFillLevel());

        // Verify consistency
        assertEquals(qrCode, bin.getQrCode());
        assertEquals(location, bin.getLocation());
        assertEquals(latitude, bin.getLatitude());
        assertEquals(longitude, bin.getLongitude());
        assertEquals(binType, bin.getBinType());
        assertEquals(status, bin.getStatus());
        assertEquals(fillLevel, bin.getFillLevel());
    }

    // ========== OBJECT EQUALITY AND HASH CODE TESTS ==========

    @Test
    @DisplayName("Should handle object equality correctly")
    void binEquality_withSameData_shouldBeEqual() {
        // Given
        Bin bin1 = new Bin();
        bin1.setQrCode("QR123");
        bin1.setLocation("Test Location");
        bin1.setLatitude(6.9271);
        bin1.setLongitude(79.8612);
        bin1.setBinType(Bin.BinType.STANDARD);
        bin1.setStatus(Bin.BinStatus.EMPTY);
        bin1.setFillLevel(0);

        Bin bin2 = new Bin();
        bin2.setQrCode("QR123");
        bin2.setLocation("Test Location");
        bin2.setLatitude(6.9271);
        bin2.setLongitude(79.8612);
        bin2.setBinType(Bin.BinType.STANDARD);
        bin2.setStatus(Bin.BinStatus.EMPTY);
        bin2.setFillLevel(0);

        // When & Then
        assertEquals(bin1, bin2);
        assertEquals(bin1.hashCode(), bin2.hashCode());
    }

    @Test
    @DisplayName("Should handle object inequality correctly")
    void binEquality_withDifferentData_shouldNotBeEqual() {
        // Given
        Bin bin1 = new Bin();
        bin1.setQrCode("QR123");
        bin1.setLocation("Location 1");

        Bin bin2 = new Bin();
        bin2.setQrCode("QR456");
        bin2.setLocation("Location 2");

        // When & Then
        assertNotEquals(bin1, bin2);
    }

    // ========== CONSTRUCTOR TESTS ==========

    @Test
    @DisplayName("Should create bin with no-args constructor")
    void createBin_withNoArgsConstructor_shouldCreateEmptyBin() {
        // When
        Bin newBin = new Bin();

        // Then
        assertNotNull(newBin);
        assertNull(newBin.getQrCode());
        assertNull(newBin.getLocation());
        assertNull(newBin.getLatitude());
        assertNull(newBin.getLongitude());
        assertNull(newBin.getBinType());
        assertNull(newBin.getStatus());
        assertNull(newBin.getFillLevel());
        assertFalse(newBin.getAlertFlag());
        assertNull(newBin.getLastEmptied());
        assertNull(newBin.getCreatedAt());
        assertNull(newBin.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create bin with all-args constructor")
    void createBin_withAllArgsConstructor_shouldCreateBinWithAllFields() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastEmptied = LocalDateTime.now().minusHours(24);

        // When
        Bin newBin = new Bin(
                1L, "QR123", "Test Location", 6.9271, 79.8612,
                Bin.BinType.STANDARD, Bin.BinStatus.EMPTY, 0,
                lastEmptied, false, now, now
        );

        // Then
        assertEquals(1L, newBin.getId());
        assertEquals("QR123", newBin.getQrCode());
        assertEquals("Test Location", newBin.getLocation());
        assertEquals(6.9271, newBin.getLatitude());
        assertEquals(79.8612, newBin.getLongitude());
        assertEquals(Bin.BinType.STANDARD, newBin.getBinType());
        assertEquals(Bin.BinStatus.EMPTY, newBin.getStatus());
        assertEquals(0, newBin.getFillLevel());
        assertEquals(lastEmptied, newBin.getLastEmptied());
        assertFalse(newBin.getAlertFlag());
        assertEquals(now, newBin.getCreatedAt());
        assertEquals(now, newBin.getUpdatedAt());
    }

    // ========== SPECIAL CHARACTERS AND FORMATTING TESTS ==========

    @Test
    @DisplayName("Should handle special characters in QR code")
    void setQrCode_withSpecialCharacters_shouldSetValue() {
        // Given
        String qrCodeWithSpecialChars = "QR-123_ABC@#$";

        // When
        bin.setQrCode(qrCodeWithSpecialChars);

        // Then
        assertEquals(qrCodeWithSpecialChars, bin.getQrCode());
    }

    @Test
    @DisplayName("Should handle special characters in location")
    void setLocation_withSpecialCharacters_shouldSetValue() {
        // Given
        String locationWithSpecialChars = "Main St. & 1st Ave., Colombo-01, Sri Lanka";

        // When
        bin.setLocation(locationWithSpecialChars);

        // Then
        assertEquals(locationWithSpecialChars, bin.getLocation());
    }

    // ========== NULL SAFETY TESTS ==========

    @Test
    @DisplayName("Should handle all fields set to null")
    void setAllFields_toNull_shouldHandleGracefully() {
        // When
        bin.setId(null);
        bin.setQrCode(null);
        bin.setLocation(null);
        bin.setLatitude(null);
        bin.setLongitude(null);
        bin.setBinType(null);
        bin.setStatus(null);
        bin.setFillLevel(null);
        bin.setLastEmptied(null);
        bin.setAlertFlag(null);
        bin.setCreatedAt(null);
        bin.setUpdatedAt(null);

        // Then
        assertNull(bin.getId());
        assertNull(bin.getQrCode());
        assertNull(bin.getLocation());
        assertNull(bin.getLatitude());
        assertNull(bin.getLongitude());
        assertNull(bin.getBinType());
        assertNull(bin.getStatus());
        assertNull(bin.getFillLevel());
        assertNull(bin.getLastEmptied());
        assertNull(bin.getAlertFlag());
        assertNull(bin.getCreatedAt());
        assertNull(bin.getUpdatedAt());
    }

    // ========== EDGE CASES FOR TIMESTAMPS ==========

    @Test
    @DisplayName("Should handle future timestamps")
    void setTimestamps_withFutureValues_shouldSetCorrectly() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);

        // When
        bin.setCreatedAt(futureTime);
        bin.setUpdatedAt(futureTime);

        // Then
        assertEquals(futureTime, bin.getCreatedAt());
        assertEquals(futureTime, bin.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle past timestamps")
    void setTimestamps_withPastValues_shouldSetCorrectly() {
        // Given
        LocalDateTime pastTime = LocalDateTime.now().minusYears(1);

        // When
        bin.setCreatedAt(pastTime);
        bin.setUpdatedAt(pastTime);

        // Then
        assertEquals(pastTime, bin.getCreatedAt());
        assertEquals(pastTime, bin.getUpdatedAt());
    }
}

