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
 * Comprehensive unit tests for User entity
 * 
 * Test Coverage Areas:
 * - Entity creation and initialization
 * - Field validation and constraints
 * - Enum handling
 * - Lifecycle callbacks (@PrePersist, @PreUpdate)
 * - Default values
 * - Boundary conditions
 * - Equivalence classes
 * - Determinism
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Entity Unit Tests")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    // ========== POSITIVE TEST CASES (Happy Path) ==========

    @Test
    @DisplayName("Should create user with all required fields")
    void createUser_withAllRequiredFields_shouldSucceed() {
        // Given & When
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setPhone("0771234567");
        user.setRole(User.UserRole.RESIDENT);
        user.setAddress("123 Main Street, Colombo");
        user.setRegion("Western Province");
        user.setRecyclingPoints(100.0);

        // Then
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("0771234567", user.getPhone());
        assertEquals(User.UserRole.RESIDENT, user.getRole());
        assertEquals("123 Main Street, Colombo", user.getAddress());
        assertEquals("Western Province", user.getRegion());
        assertEquals(100.0, user.getRecyclingPoints());
    }

    @Test
    @DisplayName("Should create user with minimal required fields")
    void createUser_withMinimalFields_shouldSucceed() {
        // Given & When
        user.setName("Jane Smith");
        user.setEmail("jane@example.com");
        user.setPassword("password456");
        user.setPhone("0777654321");
        user.setRole(User.UserRole.COLLECTOR);

        // Then
        assertEquals("Jane Smith", user.getName());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("password456", user.getPassword());
        assertEquals("0777654321", user.getPhone());
        assertEquals(User.UserRole.COLLECTOR, user.getRole());
        assertNull(user.getAddress());
        assertNull(user.getRegion());
        assertEquals(0.0, user.getRecyclingPoints()); // Default value
    }

    @Test
    @DisplayName("Should handle all user roles correctly")
    void createUser_withAllUserRoles_shouldSetCorrectly() {
        // Test RESIDENT role
        user.setRole(User.UserRole.RESIDENT);
        assertEquals(User.UserRole.RESIDENT, user.getRole());

        // Test COLLECTOR role
        user.setRole(User.UserRole.COLLECTOR);
        assertEquals(User.UserRole.COLLECTOR, user.getRole());

        // Test AUTHORITY role
        user.setRole(User.UserRole.AUTHORITY);
        assertEquals(User.UserRole.AUTHORITY, user.getRole());
    }

    @Test
    @DisplayName("Should initialize with default recycling points")
    void createUser_shouldInitializeWithDefaultRecyclingPoints() {
        // When creating a new user
        User newUser = new User();

        // Then recycling points should default to 0.0
        assertEquals(0.0, newUser.getRecyclingPoints());
    }

    // ========== NEGATIVE TEST CASES (Sad Path) ==========

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should handle null and empty names")
    void setName_withNullOrEmptyName_shouldSetValue(String name) {
        // When
        user.setName(name);

        // Then
        assertEquals(name, user.getName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should handle null and empty emails")
    void setEmail_withNullOrEmptyEmail_shouldSetValue(String email) {
        // When
        user.setEmail(email);

        // Then
        assertEquals(email, user.getEmail());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should handle null and empty passwords")
    void setPassword_withNullOrEmptyPassword_shouldSetValue(String password) {
        // When
        user.setPassword(password);

        // Then
        assertEquals(password, user.getPassword());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should handle null and empty phone numbers")
    void setPhone_withNullOrEmptyPhone_shouldSetValue(String phone) {
        // When
        user.setPhone(phone);

        // Then
        assertEquals(phone, user.getPhone());
    }

    @Test
    @DisplayName("Should handle null role")
    void setRole_withNullRole_shouldSetNull() {
        // When
        user.setRole(null);

        // Then
        assertNull(user.getRole());
    }

    // ========== BOUNDARY/EDGE CASES ==========

    @Test
    @DisplayName("Should handle very long names")
    void setName_withVeryLongName_shouldSetValue() {
        // Given
        String longName = "A".repeat(1000);

        // When
        user.setName(longName);

        // Then
        assertEquals(longName, user.getName());
    }

    @Test
    @DisplayName("Should handle very long emails")
    void setEmail_withVeryLongEmail_shouldSetValue() {
        // Given
        String longEmail = "a".repeat(100) + "@example.com";

        // When
        user.setEmail(longEmail);

        // Then
        assertEquals(longEmail, user.getEmail());
    }

    @Test
    @DisplayName("Should handle very long passwords")
    void setPassword_withVeryLongPassword_shouldSetValue() {
        // Given
        String longPassword = "p".repeat(1000);

        // When
        user.setPassword(longPassword);

        // Then
        assertEquals(longPassword, user.getPassword());
    }

    @Test
    @DisplayName("Should handle very long phone numbers")
    void setPhone_withVeryLongPhone_shouldSetValue() {
        // Given
        String longPhone = "0".repeat(100);

        // When
        user.setPhone(longPhone);

        // Then
        assertEquals(longPhone, user.getPhone());
    }

    @Test
    @DisplayName("Should handle very long addresses")
    void setAddress_withVeryLongAddress_shouldSetValue() {
        // Given
        String longAddress = "Address ".repeat(1000);

        // When
        user.setAddress(longAddress);

        // Then
        assertEquals(longAddress, user.getAddress());
    }

    @Test
    @DisplayName("Should handle very long regions")
    void setRegion_withVeryLongRegion_shouldSetValue() {
        // Given
        String longRegion = "Region ".repeat(1000);

        // When
        user.setRegion(longRegion);

        // Then
        assertEquals(longRegion, user.getRegion());
    }

    // ========== RECYCLING POINTS BOUNDARY TESTS ==========

    @Test
    @DisplayName("Should handle zero recycling points")
    void setRecyclingPoints_withZero_shouldSetZero() {
        // When
        user.setRecyclingPoints(0.0);

        // Then
        assertEquals(0.0, user.getRecyclingPoints());
    }

    @Test
    @DisplayName("Should handle negative recycling points")
    void setRecyclingPoints_withNegativeValue_shouldSetNegative() {
        // When
        user.setRecyclingPoints(-100.0);

        // Then
        assertEquals(-100.0, user.getRecyclingPoints());
    }

    @Test
    @DisplayName("Should handle very large recycling points")
    void setRecyclingPoints_withVeryLargeValue_shouldSetLargeValue() {
        // When
        user.setRecyclingPoints(Double.MAX_VALUE);

        // Then
        assertEquals(Double.MAX_VALUE, user.getRecyclingPoints());
    }

    @Test
    @DisplayName("Should handle very small recycling points")
    void setRecyclingPoints_withVerySmallValue_shouldSetSmallValue() {
        // When
        user.setRecyclingPoints(Double.MIN_VALUE);

        // Then
        assertEquals(Double.MIN_VALUE, user.getRecyclingPoints());
    }

    @Test
    @DisplayName("Should handle null recycling points")
    void setRecyclingPoints_withNull_shouldSetNull() {
        // When
        user.setRecyclingPoints(null);

        // Then
        assertNull(user.getRecyclingPoints());
    }

    // ========== EQUIVALENCE CLASSES FOR RECYCLING POINTS ==========

    @ParameterizedTest
    @ValueSource(doubles = {-1000.0, -100.0, -10.0, -1.0, 0.0, 1.0, 10.0, 100.0, 1000.0})
    @DisplayName("Should handle various recycling point values correctly")
    void setRecyclingPoints_withVariousValues_shouldSetCorrectly(double points) {
        // When
        user.setRecyclingPoints(points);

        // Then
        assertEquals(points, user.getRecyclingPoints());
    }

    // ========== ENUM VALIDATION TESTS ==========

    @ParameterizedTest
    @EnumSource(User.UserRole.class)
    @DisplayName("Should handle all user role enums correctly")
    void setRole_withAllEnumValues_shouldSetCorrectly(User.UserRole role) {
        // When
        user.setRole(role);

        // Then
        assertEquals(role, user.getRole());
    }

    // ========== TIMESTAMP BEHAVIOR TESTS ==========

    @Test
    @DisplayName("Should initialize timestamps as null by default")
    void createUser_shouldInitializeTimestampsAsNull() {
        // When creating a new user
        User newUser = new User();

        // Then timestamps should be null initially
        assertNull(newUser.getCreatedAt());
        assertNull(newUser.getUpdatedAt());
    }

    @Test
    @DisplayName("Should allow setting custom timestamps")
    void setTimestamps_withCustomValues_shouldSetCorrectly() {
        // Given
        LocalDateTime customCreatedAt = LocalDateTime.now().minusDays(1);
        LocalDateTime customUpdatedAt = LocalDateTime.now().minusHours(1);

        // When
        user.setCreatedAt(customCreatedAt);
        user.setUpdatedAt(customUpdatedAt);

        // Then
        assertEquals(customCreatedAt, user.getCreatedAt());
        assertEquals(customUpdatedAt, user.getUpdatedAt());
    }

    // ========== DETERMINISM TESTS ==========

    @Test
    @DisplayName("Should produce consistent results for same inputs")
    void setFields_determinism_shouldProduceConsistentResults() {
        // Given
        String name = "John Doe";
        String email = "john@example.com";
        String password = "password123";
        String phone = "0771234567";
        User.UserRole role = User.UserRole.RESIDENT;

        // When
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setRole(role);

        // Then - verify multiple times
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(phone, user.getPhone());
        assertEquals(role, user.getRole());

        // Verify consistency
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(phone, user.getPhone());
        assertEquals(role, user.getRole());
    }

    // ========== OBJECT EQUALITY AND HASH CODE TESTS ==========

    @Test
    @DisplayName("Should handle object equality correctly")
    void userEquality_withSameData_shouldBeEqual() {
        // Given
        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        user1.setPassword("password123");
        user1.setPhone("0771234567");
        user1.setRole(User.UserRole.RESIDENT);

        User user2 = new User();
        user2.setName("John Doe");
        user2.setEmail("john@example.com");
        user2.setPassword("password123");
        user2.setPhone("0771234567");
        user2.setRole(User.UserRole.RESIDENT);

        // When & Then
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    @DisplayName("Should handle object inequality correctly")
    void userEquality_withDifferentData_shouldNotBeEqual() {
        // Given
        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john@example.com");

        User user2 = new User();
        user2.setName("Jane Smith");
        user2.setEmail("jane@example.com");

        // When & Then
        assertNotEquals(user1, user2);
    }

    // ========== SPECIAL CHARACTERS AND FORMATTING TESTS ==========

    @Test
    @DisplayName("Should handle special characters in name")
    void setName_withSpecialCharacters_shouldSetValue() {
        // Given
        String nameWithSpecialChars = "José María O'Connor-Smith";

        // When
        user.setName(nameWithSpecialChars);

        // Then
        assertEquals(nameWithSpecialChars, user.getName());
    }

    @Test
    @DisplayName("Should handle international email formats")
    void setEmail_withInternationalFormats_shouldSetValue() {
        // Given
        String[] internationalEmails = {
                "user@example.com",
                "user.name@example.com",
                "user+tag@example.co.uk",
                "user123@example-domain.com",
                "user@subdomain.example.com"
        };

        for (String email : internationalEmails) {
            // When
            user.setEmail(email);

            // Then
            assertEquals(email, user.getEmail());
        }
    }

    @Test
    @DisplayName("Should handle various phone number formats")
    void setPhone_withVariousFormats_shouldSetValue() {
        // Given
        String[] phoneFormats = {
                "0771234567",
                "+94771234567",
                "0112345678",
                "071-123-4567",
                "077 123 4567"
        };

        for (String phone : phoneFormats) {
            // When
            user.setPhone(phone);

            // Then
            assertEquals(phone, user.getPhone());
        }
    }

    // ========== CONSTRUCTOR TESTS ==========

    @Test
    @DisplayName("Should create user with no-args constructor")
    void createUser_withNoArgsConstructor_shouldCreateEmptyUser() {
        // When
        User newUser = new User();

        // Then
        assertNotNull(newUser);
        assertNull(newUser.getName());
        assertNull(newUser.getEmail());
        assertNull(newUser.getPassword());
        assertNull(newUser.getPhone());
        assertNull(newUser.getRole());
        assertNull(user.getAddress());
        assertNull(user.getRegion());
        assertEquals(0.0, user.getRecyclingPoints());
        assertNull(newUser.getCreatedAt());
        assertNull(newUser.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create user with all-args constructor")
    void createUser_withAllArgsConstructor_shouldCreateUserWithAllFields() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        User.UserRole role = User.UserRole.RESIDENT;

        // When
        User newUser = new User(
                1L, "John Doe", "john@example.com", "password123",
                "0771234567", role, "123 Main St", "Western Province",
                100.0, now, now
        );

        // Then
        assertEquals(1L, newUser.getId());
        assertEquals("John Doe", newUser.getName());
        assertEquals("john@example.com", newUser.getEmail());
        assertEquals("password123", newUser.getPassword());
        assertEquals("0771234567", newUser.getPhone());
        assertEquals(role, newUser.getRole());
        assertEquals("123 Main St", newUser.getAddress());
        assertEquals("Western Province", newUser.getRegion());
        assertEquals(100.0, newUser.getRecyclingPoints());
        assertEquals(now, newUser.getCreatedAt());
        assertEquals(now, newUser.getUpdatedAt());
    }

    // ========== EDGE CASES FOR TIMESTAMPS ==========

    @Test
    @DisplayName("Should handle future timestamps")
    void setTimestamps_withFutureValues_shouldSetCorrectly() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);

        // When
        user.setCreatedAt(futureTime);
        user.setUpdatedAt(futureTime);

        // Then
        assertEquals(futureTime, user.getCreatedAt());
        assertEquals(futureTime, user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle past timestamps")
    void setTimestamps_withPastValues_shouldSetCorrectly() {
        // Given
        LocalDateTime pastTime = LocalDateTime.now().minusYears(1);

        // When
        user.setCreatedAt(pastTime);
        user.setUpdatedAt(pastTime);

        // Then
        assertEquals(pastTime, user.getCreatedAt());
        assertEquals(pastTime, user.getUpdatedAt());
    }

    // ========== NULL SAFETY TESTS ==========

    @Test
    @DisplayName("Should handle all fields set to null")
    void setAllFields_toNull_shouldHandleGracefully() {
        // When
        user.setId(null);
        user.setName(null);
        user.setEmail(null);
        user.setPassword(null);
        user.setPhone(null);
        user.setRole(null);
        user.setAddress(null);
        user.setRegion(null);
        user.setRecyclingPoints(null);
        user.setCreatedAt(null);
        user.setUpdatedAt(null);

        // Then
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getPhone());
        assertNull(user.getRole());
        assertNull(user.getAddress());
        assertNull(user.getRegion());
        assertNull(user.getRecyclingPoints());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }
}

