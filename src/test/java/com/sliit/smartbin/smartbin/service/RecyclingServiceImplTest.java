package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.model.RecyclingTransaction;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.repository.RecyclingTransactionRepository;
import com.sliit.smartbin.smartbin.repository.UserRepository;
import com.sliit.smartbin.smartbin.service.impl.RecyclingServiceImpl;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for RecyclingServiceImpl
 * 
 * Test Coverage Areas:
 * - Positive test cases (happy path)
 * - Negative test cases (sad path)
 * - Boundary/edge cases
 * - Error handling
 * - Equivalence classes
 * - Determinism
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RecyclingServiceImpl Unit Tests")
class RecyclingServiceImplTest {

    @Mock
    private RecyclingTransactionRepository recyclingTransactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecyclingServiceImpl recyclingService;

    private User testUser;
    private RecyclingTransaction testTransaction;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setRecyclingPoints(0.0);

        testTransaction = new RecyclingTransaction();
        testTransaction.setId(1L);
        testTransaction.setUser(testUser);
        testTransaction.setRecyclingUnitQrCode("RU001");
        testTransaction.setItemType("plastic");
        testTransaction.setWeight(2.5);
        testTransaction.setQuantity(10);
        testTransaction.setPointsEarned(25.0);
        testTransaction.setPriceValue(125.0);
        testTransaction.setStatus(RecyclingTransaction.TransactionStatus.PENDING);
    }

    // ========== POSITIVE TEST CASES (Happy Path) ==========

    @Test
    @DisplayName("Should successfully process recycling transaction with valid inputs")
    void processRecyclingTransaction_withValidInputs_shouldSucceed() {
        // Given
        when(recyclingTransactionRepository.save(any(RecyclingTransaction.class)))
                .thenReturn(testTransaction);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        RecyclingTransaction result = recyclingService.processRecyclingTransaction(
                testUser, "RU001", "plastic", 2.5, 10);

        // Then
        assertNotNull(result);
        assertEquals(RecyclingTransaction.TransactionStatus.CONFIRMED, result.getStatus());
        assertEquals(25.0, result.getPointsEarned());
        assertEquals(125.0, result.getPriceValue());
        
        verify(recyclingTransactionRepository, times(2)).save(any(RecyclingTransaction.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should calculate points correctly for different item types")
    void calculatePoints_withValidItemTypes_shouldCalculateCorrectly() {
        // Test plastic (10 points per kg)
        assertEquals(25.0, recyclingService.calculatePoints("plastic", 2.5, 10));
        
        // Test paper (5 points per kg)
        assertEquals(12.5, recyclingService.calculatePoints("paper", 2.5, 10));
        
        // Test metal (20 points per kg)
        assertEquals(50.0, recyclingService.calculatePoints("metal", 2.5, 10));
        
        // Test glass (15 points per kg)
        assertEquals(37.5, recyclingService.calculatePoints("glass", 2.5, 10));
    }

    @Test
    @DisplayName("Should calculate price correctly for different item types")
    void calculatePrice_withValidItemTypes_shouldCalculateCorrectly() {
        // Test plastic (50 LKR per kg)
        assertEquals(125.0, recyclingService.calculatePrice("plastic", 2.5));
        
        // Test paper (30 LKR per kg)
        assertEquals(75.0, recyclingService.calculatePrice("paper", 2.5));
        
        // Test metal (100 LKR per kg)
        assertEquals(250.0, recyclingService.calculatePrice("metal", 2.5));
        
        // Test glass (40 LKR per kg)
        assertEquals(100.0, recyclingService.calculatePrice("glass", 2.5));
    }

    @Test
    @DisplayName("Should get user transactions successfully")
    void getUserTransactions_withValidUser_shouldReturnTransactions() {
        // Given
        List<RecyclingTransaction> expectedTransactions = Arrays.asList(testTransaction);
        when(recyclingTransactionRepository.findByUserOrderByCreatedAtDesc(testUser))
                .thenReturn(expectedTransactions);

        // When
        List<RecyclingTransaction> result = recyclingService.getUserTransactions(testUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTransaction.getId(), result.get(0).getId());
        verify(recyclingTransactionRepository).findByUserOrderByCreatedAtDesc(testUser);
    }

    @Test
    @DisplayName("Should confirm transaction successfully")
    void confirmTransaction_withValidTransactionId_shouldConfirm() {
        // Given
        when(recyclingTransactionRepository.findById(1L))
                .thenReturn(Optional.of(testTransaction));
        when(recyclingTransactionRepository.save(any(RecyclingTransaction.class)))
                .thenReturn(testTransaction);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        RecyclingTransaction result = recyclingService.confirmTransaction(1L);

        // Then
        assertNotNull(result);
        assertEquals(RecyclingTransaction.TransactionStatus.CONFIRMED, result.getStatus());
        verify(recyclingTransactionRepository).save(testTransaction);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should get nearby recycling units successfully")
    void getNearbyRecyclingUnits_withValidCoordinates_shouldReturnUnits() {
        // When
        var result = recyclingService.getNearbyRecyclingUnits(6.9271, 79.8612, 5.0);

        // Then
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals("RU001", result.get(0).getQrCode());
        assertEquals("Colombo Central Recycling Hub", result.get(0).getName());
    }

    // ========== NEGATIVE TEST CASES (Sad Path) ==========

    @Test
    @DisplayName("Should handle transaction processing failure gracefully")
    void processRecyclingTransaction_whenExceptionThrown_shouldCreateFailedTransaction() {
        // Given
        when(recyclingTransactionRepository.save(any(RecyclingTransaction.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        RecyclingTransaction result = recyclingService.processRecyclingTransaction(
                testUser, "RU001", "plastic", 2.5, 10);

        assertNotNull(result);
        assertEquals(RecyclingTransaction.TransactionStatus.FAILED, result.getStatus());
        assertEquals(0.0, result.getPointsEarned());
        assertEquals(0.0, result.getPriceValue());
        verify(recyclingTransactionRepository).save(any(RecyclingTransaction.class));
    }

    @Test
    @DisplayName("Should throw exception when confirming non-existent transaction")
    void confirmTransaction_withNonExistentId_shouldThrowException() {
        // Given
        when(recyclingTransactionRepository.findById(999L))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
                recyclingService.confirmTransaction(999L));
        
        assertEquals("Transaction not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle already confirmed transaction")
    void confirmTransaction_withAlreadyConfirmedTransaction_shouldReturnSameTransaction() {
        // Given
        testTransaction.setStatus(RecyclingTransaction.TransactionStatus.CONFIRMED);
        when(recyclingTransactionRepository.findById(1L))
                .thenReturn(Optional.of(testTransaction));

        // When
        RecyclingTransaction result = recyclingService.confirmTransaction(1L);

        // Then
        assertEquals(RecyclingTransaction.TransactionStatus.CONFIRMED, result.getStatus());
        verify(userRepository, never()).save(any(User.class));
    }

    // ========== BOUNDARY/EDGE CASES ==========

    @Test
    @DisplayName("Should handle zero weight correctly")
    void calculatePoints_withZeroWeight_shouldReturnZero() {
        assertEquals(0.0, recyclingService.calculatePoints("plastic", 0.0, 1));
    }

    @Test
    @DisplayName("Should handle very small weight correctly")
    void calculatePoints_withVerySmallWeight_shouldCalculateCorrectly() {
        assertEquals(0.01, recyclingService.calculatePoints("plastic", 0.001, 1), 0.01);
    }

    @Test
    @DisplayName("Should handle null quantity correctly")
    void calculatePoints_withNullQuantity_shouldCalculateWithoutBonus() {
        assertEquals(25.0, recyclingService.calculatePoints("plastic", 2.5, null));
    }

    @Test
    @DisplayName("Should handle negative weight")
    void calculatePoints_withNegativeWeight_shouldCalculateNegativePoints() {
        assertEquals(-25.0, recyclingService.calculatePoints("plastic", -2.5, 10));
    }

    @Test
    @DisplayName("Should handle maximum integer quantity")
    void calculatePoints_withMaxQuantity_shouldCalculateWithBonus() {
        assertEquals(275.0, recyclingService.calculatePoints("plastic", 2.5, Integer.MAX_VALUE));
    }

    // ========== EQUIVALENCE CLASSES ==========

    @ParameterizedTest
    @CsvSource({
            "plastic, 2.5, 10, 25.0",
            "paper, 2.5, 10, 12.5",
            "metal, 2.5, 10, 50.0",
            "glass, 2.5, 10, 37.5",
            "cardboard, 2.5, 10, 17.5",
            "electronics, 2.5, 10, 125.0"
    })
    @DisplayName("Should calculate points correctly for all supported item types")
    void calculatePoints_equivalenceClasses_shouldCalculateCorrectly(
            String itemType, Double weight, Integer quantity, Double expectedPoints) {
        assertEquals(expectedPoints, recyclingService.calculatePoints(itemType, weight, quantity));
    }

    @ParameterizedTest
    @CsvSource({
            "plastic, 2.5, 125.0",
            "paper, 2.5, 75.0",
            "metal, 2.5, 250.0",
            "glass, 2.5, 100.0",
            "cardboard, 2.5, 87.5",
            "electronics, 2.5, 500.0"
    })
    @DisplayName("Should calculate price correctly for all supported item types")
    void calculatePrice_equivalenceClasses_shouldCalculateCorrectly(
            String itemType, Double weight, Double expectedPrice) {
        assertEquals(expectedPrice, recyclingService.calculatePrice(itemType, weight));
    }

    @Test
    @DisplayName("Should handle unknown item type with default rates")
    void calculatePoints_withUnknownItemType_shouldUseDefaultRates() {
        assertEquals(12.5, recyclingService.calculatePoints("unknown", 2.5, 10)); // 5.0 * 2.5
        assertEquals(62.5, recyclingService.calculatePrice("unknown", 2.5)); // 25.0 * 2.5
    }

    // ========== BULK QUANTITY BONUS TESTING ==========

    @Test
    @DisplayName("Should apply bonus for quantity > 10")
    void calculatePoints_withQuantityOver10_shouldApplyBonus() {
        assertEquals(27.5, recyclingService.calculatePoints("plastic", 2.5, 15)); // 25.0 * 1.1
    }

    @Test
    @DisplayName("Should not apply bonus for quantity <= 10")
    void calculatePoints_withQuantity10OrLess_shouldNotApplyBonus() {
        assertEquals(25.0, recyclingService.calculatePoints("plastic", 2.5, 10));
        assertEquals(25.0, recyclingService.calculatePoints("plastic", 2.5, 5));
    }

    // ========== DETERMINISM TESTS ==========

    @Test
    @DisplayName("Should produce same results for same inputs consistently")
    void calculatePoints_determinism_shouldProduceSameResults() {
        Double result1 = recyclingService.calculatePoints("plastic", 2.5, 10);
        Double result2 = recyclingService.calculatePoints("plastic", 2.5, 10);
        Double result3 = recyclingService.calculatePoints("plastic", 2.5, 10);

        assertEquals(result1, result2);
        assertEquals(result2, result3);
    }

    @Test
    @DisplayName("Should produce same price results for same inputs consistently")
    void calculatePrice_determinism_shouldProduceSameResults() {
        Double result1 = recyclingService.calculatePrice("metal", 1.0);
        Double result2 = recyclingService.calculatePrice("metal", 1.0);
        Double result3 = recyclingService.calculatePrice("metal", 1.0);

        assertEquals(result1, result2);
        assertEquals(result2, result3);
    }

    // ========== INPUT VALIDATION TESTS ==========

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should handle null and empty item types")
    void calculatePoints_withNullOrEmptyItemType_shouldUseDefaultRates(String itemType) {
        assertEquals(12.5, recyclingService.calculatePoints(itemType, 2.5, 10));
        assertEquals(62.5, recyclingService.calculatePrice(itemType, 2.5));
    }

    @Test
    @DisplayName("Should handle case insensitive item types")
    void calculatePoints_withCaseInsensitiveItemTypes_shouldCalculateCorrectly() {
        assertEquals(25.0, recyclingService.calculatePoints("PLASTIC", 2.5, 10));
        assertEquals(25.0, recyclingService.calculatePoints("Plastic", 2.5, 10));
        assertEquals(25.0, recyclingService.calculatePoints("PlAsTiC", 2.5, 10));
    }

    // ========== PRECISION AND ROUNDING TESTS ==========

    @Test
    @DisplayName("Should round points to 2 decimal places")
    void calculatePoints_shouldRoundToTwoDecimalPlaces() {
        // Test with weight that produces more than 2 decimal places
        assertEquals(33.33, recyclingService.calculatePoints("plastic", 3.333, 10), 0.01);
    }

    @Test
    @DisplayName("Should round price to 2 decimal places")
    void calculatePrice_shouldRoundToTwoDecimalPlaces() {
        // Test with weight that produces more than 2 decimal places
        assertEquals(166.67, recyclingService.calculatePrice("plastic", 3.333), 0.01);
    }

    // ========== USER POINTS INTEGRATION TESTS ==========

    @Test
    @DisplayName("Should correctly update user points when confirming transaction")
    void confirmTransaction_shouldUpdateUserPoints() {
        // Given
        testUser.setRecyclingPoints(50.0);
        when(recyclingTransactionRepository.findById(1L))
                .thenReturn(Optional.of(testTransaction));
        when(recyclingTransactionRepository.save(any(RecyclingTransaction.class)))
                .thenReturn(testTransaction);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        recyclingService.confirmTransaction(1L);

        // Then
        verify(userRepository).save(argThat(user -> 
                user.getRecyclingPoints().equals(75.0) // 50.0 + 25.0
        ));
    }

    @Test
    @DisplayName("Should handle null user points correctly")
    void confirmTransaction_withNullUserPoints_shouldInitializeToZero() {
        // Given
        testUser.setRecyclingPoints(null);
        when(recyclingTransactionRepository.findById(1L))
                .thenReturn(Optional.of(testTransaction));
        when(recyclingTransactionRepository.save(any(RecyclingTransaction.class)))
                .thenReturn(testTransaction);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        recyclingService.confirmTransaction(1L);

        // Then
        verify(userRepository).save(argThat(user -> 
                user.getRecyclingPoints().equals(25.0) // 0.0 + 25.0
        ));
    }
}
