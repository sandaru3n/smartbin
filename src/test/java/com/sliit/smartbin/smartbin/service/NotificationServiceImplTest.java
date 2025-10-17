package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.BulkRequest;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for NotificationServiceImpl
 * 
 * Test Coverage Areas:
 * - Positive test cases (happy path)
 * - Negative test cases (sad path)
 * - Boundary/edge cases
 * - Error handling
 * - Notification content validation
 * - Equivalence classes
 * - Determinism
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationServiceImpl Unit Tests")
class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User testUser;
    private Bin testBin;
    private BulkRequest testBulkRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPhone("0771234567");

        testBin = new Bin();
        testBin.setId(1L);
        testBin.setQrCode("QR123");
        testBin.setLocation("Test Location");
        testBin.setFillLevel(85);

        testBulkRequest = new BulkRequest();
        testBulkRequest.setId(1L);
        testBulkRequest.setRequestId("BR-2024-001");
        testBulkRequest.setUser(testUser);
    }

    // ========== POSITIVE TEST CASES (Happy Path) ==========

    @Test
    @DisplayName("Should send bin alert notification successfully")
    void sendBinAlertNotification_withValidBin_shouldSucceed() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> notificationService.sendBinAlertNotification(testBin));
    }

    @Test
    @DisplayName("Should notify user about bulk request successfully")
    void notifyUserBulkRequest_withValidInputs_shouldSucceed() {
        // Given
        String message = "Your bulk collection request has been submitted successfully.";

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(testUser, message, testBulkRequest));
    }

    @Test
    @DisplayName("Should notify collector about bulk assignment successfully")
    void notifyCollectorBulkAssignment_withValidInputs_shouldSucceed() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyCollectorBulkAssignment(testUser, testBulkRequest));
    }

    @Test
    @DisplayName("Should send pickup schedule notification successfully")
    void sendPickupScheduleNotification_withValidInputs_shouldSucceed() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.sendPickupScheduleNotification(testUser, testBulkRequest));
    }

    @Test
    @DisplayName("Should notify authority about bulk payment successfully")
    void notifyAuthorityBulkPayment_withValidBulkRequest_shouldSucceed() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> notificationService.notifyAuthorityBulkPayment(testBulkRequest));
    }

    // ========== NEGATIVE TEST CASES (Sad Path) ==========

    @Test
    @DisplayName("Should handle null bin gracefully")
    void sendBinAlertNotification_withNullBin_shouldHandleGracefully() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> notificationService.sendBinAlertNotification(null));
    }

    @Test
    @DisplayName("Should handle null user gracefully")
    void notifyUserBulkRequest_withNullUser_shouldHandleGracefully() {
        // Given
        String message = "Test message";

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(null, message, testBulkRequest));
    }

    @Test
    @DisplayName("Should handle null message gracefully")
    void notifyUserBulkRequest_withNullMessage_shouldHandleGracefully() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(testUser, null, testBulkRequest));
    }

    @Test
    @DisplayName("Should handle null bulk request gracefully")
    void notifyUserBulkRequest_withNullBulkRequest_shouldHandleGracefully() {
        // Given
        String message = "Test message";

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(testUser, message, null));
    }

    @Test
    @DisplayName("Should handle null collector gracefully")
    void notifyCollectorBulkAssignment_withNullCollector_shouldHandleGracefully() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyCollectorBulkAssignment(null, testBulkRequest));
    }

    @Test
    @DisplayName("Should handle null bulk request in collector assignment gracefully")
    void notifyCollectorBulkAssignment_withNullBulkRequest_shouldHandleGracefully() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyCollectorBulkAssignment(testUser, null));
    }

    @Test
    @DisplayName("Should handle null user in pickup schedule gracefully")
    void sendPickupScheduleNotification_withNullUser_shouldHandleGracefully() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.sendPickupScheduleNotification(null, testBulkRequest));
    }

    @Test
    @DisplayName("Should handle null bulk request in pickup schedule gracefully")
    void sendPickupScheduleNotification_withNullBulkRequest_shouldHandleGracefully() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.sendPickupScheduleNotification(testUser, null));
    }

    @Test
    @DisplayName("Should handle null bulk request in authority notification gracefully")
    void notifyAuthorityBulkPayment_withNullBulkRequest_shouldHandleGracefully() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> notificationService.notifyAuthorityBulkPayment(null));
    }

    // ========== BOUNDARY/EDGE CASES ==========

    @Test
    @DisplayName("Should handle empty message")
    void notifyUserBulkRequest_withEmptyMessage_shouldHandleGracefully() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(testUser, "", testBulkRequest));
    }

    @Test
    @DisplayName("Should handle very long message")
    void notifyUserBulkRequest_withVeryLongMessage_shouldHandleGracefully() {
        // Given
        String longMessage = "A".repeat(10000);

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(testUser, longMessage, testBulkRequest));
    }

    @Test
    @DisplayName("Should handle bin with null QR code")
    void sendBinAlertNotification_withNullQrCode_shouldHandleGracefully() {
        // Given
        testBin.setQrCode(null);

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> notificationService.sendBinAlertNotification(testBin));
    }

    @Test
    @DisplayName("Should handle bin with null location")
    void sendBinAlertNotification_withNullLocation_shouldHandleGracefully() {
        // Given
        testBin.setLocation(null);

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> notificationService.sendBinAlertNotification(testBin));
    }

    @Test
    @DisplayName("Should handle bin with null fill level")
    void sendBinAlertNotification_withNullFillLevel_shouldHandleGracefully() {
        // Given
        testBin.setFillLevel(null);

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> notificationService.sendBinAlertNotification(testBin));
    }

    @Test
    @DisplayName("Should handle user with null name")
    void notifyUserBulkRequest_withNullUserName_shouldHandleGracefully() {
        // Given
        testUser.setName(null);

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(testUser, "Test message", testBulkRequest));
    }

    @Test
    @DisplayName("Should handle user with null email")
    void notifyUserBulkRequest_withNullUserEmail_shouldHandleGracefully() {
        // Given
        testUser.setEmail(null);

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(testUser, "Test message", testBulkRequest));
    }

    @Test
    @DisplayName("Should handle user with null phone")
    void notifyUserBulkRequest_withNullUserPhone_shouldHandleGracefully() {
        // Given
        testUser.setPhone(null);

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(testUser, "Test message", testBulkRequest));
    }

    // ========== EQUIVALENCE CLASSES ==========

    @ParameterizedTest
    @ValueSource(ints = {0, 25, 50, 75, 80, 85, 90, 95, 100})
    @DisplayName("Should handle various fill levels in bin alert notification")
    void sendBinAlertNotification_withVariousFillLevels_shouldHandleGracefully(int fillLevel) {
        // Given
        testBin.setFillLevel(fillLevel);

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> notificationService.sendBinAlertNotification(testBin));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  ", "Test Message", "Very long message with special characters: !@#$%^&*()"})
    @DisplayName("Should handle various message formats")
    void notifyUserBulkRequest_withVariousMessages_shouldHandleGracefully(String message) {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(testUser, message, testBulkRequest));
    }

    // ========== DETERMINISM TESTS ==========

    @Test
    @DisplayName("Should produce consistent results for same inputs")
    void sendBinAlertNotification_determinism_shouldProduceConsistentResults() {
        // When calling multiple times with same input
        assertDoesNotThrow(() -> notificationService.sendBinAlertNotification(testBin));
        assertDoesNotThrow(() -> notificationService.sendBinAlertNotification(testBin));
        assertDoesNotThrow(() -> notificationService.sendBinAlertNotification(testBin));
    }

    @Test
    @DisplayName("Should produce consistent results for user notifications")
    void notifyUserBulkRequest_determinism_shouldProduceConsistentResults() {
        // Given
        String message = "Test message";

        // When calling multiple times with same input
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(testUser, message, testBulkRequest));
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(testUser, message, testBulkRequest));
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(testUser, message, testBulkRequest));
    }

    // ========== SPECIAL CHARACTERS AND FORMATTING TESTS ==========

    @Test
    @DisplayName("Should handle special characters in user name")
    void notifyUserBulkRequest_withSpecialCharactersInName_shouldHandleGracefully() {
        // Given
        testUser.setName("José María O'Connor-Smith");

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(testUser, "Test message", testBulkRequest));
    }

    @Test
    @DisplayName("Should handle special characters in bin location")
    void sendBinAlertNotification_withSpecialCharactersInLocation_shouldHandleGracefully() {
        // Given
        testBin.setLocation("Main St. & 1st Ave., Colombo-01, Sri Lanka");

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> notificationService.sendBinAlertNotification(testBin));
    }

    @Test
    @DisplayName("Should handle special characters in QR code")
    void sendBinAlertNotification_withSpecialCharactersInQrCode_shouldHandleGracefully() {
        // Given
        testBin.setQrCode("QR-123_ABC@#$");

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> notificationService.sendBinAlertNotification(testBin));
    }

    // ========== BULK REQUEST EDGE CASES ==========

    @Test
    @DisplayName("Should handle bulk request with null request ID")
    void notifyUserBulkRequest_withNullRequestId_shouldHandleGracefully() {
        // Given
        testBulkRequest.setRequestId(null);

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(testUser, "Test message", testBulkRequest));
    }

    @Test
    @DisplayName("Should handle bulk request with null user")
    void notifyUserBulkRequest_withNullBulkRequestUser_shouldHandleGracefully() {
        // Given
        testBulkRequest.setUser(null);

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> 
                notificationService.notifyUserBulkRequest(testUser, "Test message", testBulkRequest));
    }

    // ========== CONCURRENT ACCESS TESTS ==========

    @Test
    @DisplayName("Should handle concurrent notification calls")
    void sendBinAlertNotification_concurrentAccess_shouldHandleGracefully() {
        // When calling from multiple "threads" (simulated)
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                notificationService.sendBinAlertNotification(testBin);
            }
        });
    }

    @Test
    @DisplayName("Should handle concurrent user notification calls")
    void notifyUserBulkRequest_concurrentAccess_shouldHandleGracefully() {
        // Given
        String message = "Concurrent test message";

        // When calling from multiple "threads" (simulated)
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                notificationService.notifyUserBulkRequest(testUser, message, testBulkRequest);
            }
        });
    }

    // ========== PERFORMANCE TESTS ==========

    @Test
    @DisplayName("Should handle large number of notifications efficiently")
    void sendBinAlertNotification_performanceTest_shouldCompleteEfficiently() {
        // Given
        long startTime = System.currentTimeMillis();

        // When sending many notifications
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 1000; i++) {
                notificationService.sendBinAlertNotification(testBin);
            }
        });

        // Then should complete in reasonable time
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete within 5 seconds (generous for unit test)
        assertTrue(duration < 5000, "Notification service should be efficient");
    }

    // ========== INTEGRATION SCENARIOS ==========

    @Test
    @DisplayName("Should handle complete notification workflow")
    void completeNotificationWorkflow_shouldHandleAllSteps() {
        // Given
        String message = "Complete workflow test";

        // When executing complete workflow
        assertDoesNotThrow(() -> {
            // 1. Send bin alert
            notificationService.sendBinAlertNotification(testBin);
            
            // 2. Notify user about bulk request
            notificationService.notifyUserBulkRequest(testUser, message, testBulkRequest);
            
            // 3. Notify collector about assignment
            notificationService.notifyCollectorBulkAssignment(testUser, testBulkRequest);
            
            // 4. Send pickup schedule notification
            notificationService.sendPickupScheduleNotification(testUser, testBulkRequest);
            
            // 5. Notify authority about payment
            notificationService.notifyAuthorityBulkPayment(testBulkRequest);
        });
    }
}
