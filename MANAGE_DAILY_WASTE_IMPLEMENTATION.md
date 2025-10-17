# Manage Daily Waste Use Case Implementation

## Overview
This document describes the implementation of the "Manage Daily Waste" use case for the SmartBin system. The implementation follows SOLID principles and includes comprehensive error handling with retry logic.

## Implementation Date
October 15, 2025

## Use Case Summary
**Name:** Manage Daily Waste  
**Primary Actor:** Resident / Business User  
**Goal:** Allow users to dispose of standard and recyclable waste efficiently, track bin status, and earn recycling points.

---

## Architecture & SOLID Principles

### 1. Single Responsibility Principle (SRP)
- **WasteDisposalService**: Handles only waste disposal operations
- **RecyclingService**: Manages only recycling transactions and points calculation
- **ResidentController**: Coordinates user interactions and delegates to services

### 2. Open/Closed Principle (OCP)
- Services are designed to be open for extension but closed for modification
- Interface-based design allows adding new implementations without changing existing code

### 3. Liskov Substitution Principle (LSP)
- Service implementations can be substituted without breaking functionality
- All implementations adhere to their interface contracts

### 4. Interface Segregation Principle (ISP)
- Small, focused interfaces (RecyclingService, WasteDisposalService)
- Each interface has a specific purpose and minimal methods

### 5. Dependency Inversion Principle (DIP)
- Controllers depend on service interfaces, not concrete implementations
- Services depend on repository interfaces
- Promotes testability and flexibility

---

## Components Created

### Models

#### 1. RecyclingTransaction.java
- Tracks recycling activities by users
- Fields: user, recyclingUnitQrCode, itemType, weight, quantity, pointsEarned, priceValue, status
- Enums: TransactionStatus (PENDING, CONFIRMED, FAILED, CANCELLED)

#### 2. WasteDisposal.java
- Records waste disposal submissions
- Fields: user, bin, reportedFillLevel, status, notes
- Enums: DisposalStatus (SUBMITTED, CONFIRMED, FAILED)

#### 3. User Model Extension
- Added `recyclingPoints` field (Double) to track earned points

### Repositories

#### 1. RecyclingTransactionRepository
- findByUserOrderByCreatedAtDesc()
- findByUserAndStatus()

#### 2. WasteDisposalRepository
- findByUserOrderByCreatedAtDesc()

### Services

#### 1. RecyclingService Interface
Key methods:
- `processRecyclingTransaction()` - Process recycling and award points
- `calculatePoints()` - Calculate points based on item type and weight
- `calculatePrice()` - Calculate monetary value
- `getUserTransactions()` - Get user's recycling history
- `confirmTransaction()` - Confirm and credit points
- `getNearbyRecyclingUnits()` - Get nearby recycling centers (mock data)

**Recycling Rates:**
- Electronics: 50 points/kg (LKR 200/kg)
- Metal: 20 points/kg (LKR 100/kg)
- Glass: 15 points/kg (LKR 40/kg)
- Plastic: 10 points/kg (LKR 50/kg)
- Cardboard: 7 points/kg (LKR 35/kg)
- Paper: 5 points/kg (LKR 30/kg)
- **Bonus:** +10% points for bulk recycling (10+ items)

#### 2. WasteDisposalService Interface
Key methods:
- `submitDisposal()` - Submit waste disposal with retry logic
- `getUserDisposals()` - Get user's disposal history
- `validateBinQrCode()` - Validate QR code format (BIN + 3-4 digits)
- `checkIfBinNeedsCollection()` - Check if bin fill level >= 80%

**Error Handling:**
- Automatic retry (up to 3 attempts) with exponential backoff
- Failed transactions are logged with FAILED status
- Network errors are caught and reported to users

### Controller Endpoints

#### ResidentController - New Endpoints

**Waste Disposal:**
- `GET /resident/scan-bin` - QR code scanning page
- `POST /resident/submit-disposal` - Submit waste disposal form
  - Parameters: qrCode, fillLevel, notes (optional)
  - Validates QR format: BIN + 3-4 digits
  - Updates bin fill level
  - Triggers notifications if bin >= 80% full

**Recycling:**
- `GET /resident/recycling-units` - Find nearby recycling centers
  - Parameters: latitude, longitude, radius (optional)
  - Returns mock data of 5 recycling units in Colombo area
  
- `GET /resident/recycle` - Recycling submission page
  - Parameter: unitQrCode (optional)
  
- `POST /resident/submit-recycling` - Submit recycling transaction
  - Parameters: unitQrCode, itemType, weight, quantity (optional)
  - Validates QR format: RU + 3-4 digits
  - Calculates and awards points
  - Shows success message with points earned

- `GET /resident/my-recycling` - View recycling history
  - Shows all transactions
  - Displays total points, transactions, value earned, weight recycled

### User Interface Pages

#### 1. Enhanced Dashboard (dashboard.html)
**Features:**
- **Points Display:** Prominent badge showing total recycling points
- **Quick Actions:**
  - Scan Bin QR Code (opens modal)
  - Find Recycling Units
  - Search Nearby Bins
  - My Recycling History
  
- **Statistics Cards:**
  - Total Bins
  - Bin Alerts
  - Recent Recycling Transactions
  - Waste Disposals

- **Recent Activity Section:**
  - Recent Recycling Transactions (last 5)
  - Recent Waste Disposals (last 5)
  - Shows item type, weight, value, points earned

- **QR Code Scanner Modal:**
  - QR code input with format validation
  - Interactive fill level slider (0-100%)
  - Live preview of fill level
  - Optional notes field
  - Form validation

#### 2. Recycling Units Page (recycling-units.html)
**Features:**
- Map placeholder showing recycling unit locations
- Grid view of all recycling units
- Each unit card shows:
  - Name and QR code
  - Address
  - Distance from user
  - Accepted item types
  - "Recycle Here" button

#### 3. Recycle Submission Page (recycle.html)
**Features:**
- Information box with recycling rates
- Form fields:
  - Recycling unit QR code (RU format validation)
  - Item type selection (6 types)
  - Weight input (kg)
  - Quantity input (optional)
- **Live Preview:**
  - Real-time calculation of estimated points
  - Real-time calculation of monetary value
  - Updates as user types
- Form validation before submission

#### 4. My Recycling History Page (my-recycling.html)
**Features:**
- **Statistics Dashboard:**
  - Total points earned
  - Total transactions
  - Total value earned (LKR)
  - Total weight recycled (kg)
  
- **Transaction List:**
  - Each transaction shows:
    - Item type
    - Status badge (CONFIRMED, PENDING, FAILED)
    - Recycling unit
    - Weight and quantity
    - Value in LKR
    - Points earned
    - Transaction date/time
  - Empty state with call-to-action

---

## Error Handling & Exception Management

### 1. Backend Failures
**Strategy:** Retry with exponential backoff
```java
// WasteDisposalServiceImpl
- Maximum 3 retry attempts
- Exponential backoff: 1s, 2s, 3s
- Failed transactions logged with status FAILED
- Error details captured in notes field
```

### 2. Network Failures
**User Feedback:**
- Clear error messages displayed
- "Network error: [details]. Please check your connection and try again."
- Alert auto-closes after 5 seconds

### 3. Recycling Unit Connection Failures
**Handling:**
- Transaction marked as FAILED
- User prompted to retry
- "Recycling unit failed to connect. Please retry or contact support."

### 4. Validation Errors
**QR Code Validation:**
- Bin QR: Must match pattern `BIN\d{3,4}`
- Recycling Unit QR: Must match pattern `RU\d{3,4}`
- HTML5 pattern validation + backend validation

### 5. Notification Failures
**Strategy:**
- Catch exceptions during notification sending
- Log errors to console
- Don't block main transaction flow
- Comment suggests implementing retry queue in production

---

## Mock Data

### Recycling Units (5 locations in Colombo)
1. **Colombo Central Recycling Hub** (RU001)
   - Location: 123 Main Street, Colombo 01
   - Distance: 0.5 km
   - Accepts: plastic, paper, glass, metal, cardboard

2. **Green Point Recycling Center** (RU002)
   - Location: 456 Green Road, Colombo 03
   - Distance: 1.2 km
   - Accepts: plastic, paper, electronics

3. **Eco Recycling Station** (RU003)
   - Location: 789 Park Avenue, Colombo 05
   - Distance: 2.3 km
   - Accepts: glass, metal, electronics, cardboard

4. **Smart Recycle Point** (RU004)
   - Location: 321 Lake Road, Colombo 02
   - Distance: 1.8 km
   - Accepts: plastic, paper, glass, metal

5. **Community Recycling Hub** (RU005)
   - Location: 555 Beach Road, Colombo 06
   - Distance: 3.1 km
   - Accepts: paper, cardboard, plastic

---

## User Flow Diagrams

### Main Flow: Waste Disposal
```
1. User opens dashboard
2. Clicks "Scan Bin QR Code"
3. Modal opens
4. User enters QR code (e.g., BIN001)
5. User adjusts fill level slider
6. User adds optional notes
7. User submits form
8. Backend validates QR code
9. Backend finds bin
10. Backend creates disposal record
11. Backend updates bin fill level
12. If fill >= 80%:
    - Backend sends notification to collectors
13. User sees success message
14. Disposal appears in recent activity
```

### Alternate Flow: Recycling
```
1. User clicks "Find Recycling Units"
2. System shows nearby recycling units
3. User selects a unit
4. User clicks "Recycle Here"
5. User fills recycling form:
   - Item type
   - Weight
   - Quantity (optional)
6. Live preview shows estimated points/value
7. User submits
8. Backend processes transaction:
   - Calculates points
   - Calculates monetary value
   - Creates transaction record
   - Credits points to user account
9. User sees confirmation with points earned
10. Transaction appears in history
```

---

## Database Schema Changes

### New Tables

#### recycling_transactions
```sql
CREATE TABLE recycling_transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    recycling_unit_qr_code VARCHAR(255) NOT NULL,
    item_type VARCHAR(50) NOT NULL,
    weight DOUBLE NOT NULL,
    quantity INT,
    points_earned DOUBLE NOT NULL,
    price_value DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL,
    location VARCHAR(255),
    created_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### waste_disposals
```sql
CREATE TABLE waste_disposals (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    bin_id BIGINT NOT NULL,
    reported_fill_level INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (bin_id) REFERENCES bins(id)
);
```

#### users (Modified)
```sql
ALTER TABLE users ADD COLUMN recycling_points DOUBLE DEFAULT 0.0;
```

---

## Testing Guide

### Manual Testing Steps

#### Test 1: Waste Disposal
1. Login as resident
2. Click "Scan Bin QR Code"
3. Enter QR code: BIN001
4. Set fill level: 85%
5. Add notes: "Bin is almost full"
6. Submit
7. **Expected:** Success message, bin updated, notification sent

#### Test 2: Invalid QR Code
1. Click "Scan Bin QR Code"
2. Enter invalid QR: ABC123
3. Try to submit
4. **Expected:** HTML5 validation error

#### Test 3: Recycling Transaction
1. Click "Find Recycling Units"
2. Select any recycling unit
3. Click "Recycle Here"
4. Select item type: Plastic
5. Enter weight: 2.5 kg
6. Enter quantity: 15 items
7. **Expected:** Preview shows ~27.5 points (2.5 * 10 * 1.1 bonus)
8. Submit
9. **Expected:** Success message, points credited

#### Test 4: View Recycling History
1. Click "My Recycling History"
2. **Expected:** 
   - Statistics show total points
   - Transaction list shows all recycling activities
   - Each transaction shows correct details

#### Test 5: Error Handling
1. Disconnect network (simulate)
2. Try to submit recycling
3. **Expected:** Network error message displayed

---

## Key Features Summary

### ✅ Implemented Features

1. **QR Code Scanning**
   - Modal-based interface
   - Format validation
   - Interactive fill level slider

2. **Waste Disposal Form**
   - Fill level reporting (0-100%)
   - Optional notes
   - Backend validation
   - Auto-notification on full bins

3. **Recycling System**
   - 6 recyclable item types
   - Dynamic points calculation
   - Monetary value calculation
   - Bulk recycling bonus

4. **Nearby Bin Search**
   - Existing feature integrated
   - Distance-based search
   - Map view (placeholder)

5. **Recycling Units Finder**
   - Mock data of 5 units
   - Distance display
   - Accepted items list
   - Direct recycling link

6. **Points System**
   - Real-time points calculation
   - Automatic crediting on confirmation
   - Points display on dashboard
   - Transaction history

7. **Error Handling**
   - Retry logic with exponential backoff
   - User-friendly error messages
   - Failed transaction logging
   - Network error detection

8. **User Experience**
   - Modern, responsive UI
   - Real-time previews
   - Auto-closing alerts
   - Activity tracking

---

## Future Enhancements

### Suggested Improvements

1. **Real QR Code Scanning**
   - Integrate camera API for actual QR scanning
   - Add barcode library (e.g., QuaggaJS, ZXing)

2. **Google Maps Integration**
   - Replace map placeholder with real Google Maps
   - Show actual locations and routes
   - Real-time distance calculation

3. **Push Notifications**
   - Implement WebSockets for real-time notifications
   - Mobile push notifications
   - Email notifications

4. **Advanced Analytics**
   - Charts for recycling trends
   - Environmental impact metrics
   - Leaderboard system

5. **Gamification**
   - Achievements/badges
   - Challenges and rewards
   - Social sharing features

6. **Payment Integration**
   - Convert points to cash
   - Payment gateway integration
   - Reward redemption system

---

## Technical Dependencies

### Existing Dependencies (from pom.xml)
- Spring Boot 3.x
- Spring Data JPA
- Thymeleaf
- Lombok
- H2/MySQL Database

### No New Dependencies Required
All features implemented using existing framework capabilities.

---

## File Structure

### New Files Created
```
src/main/java/com/sliit/smartbin/smartbin/
├── model/
│   ├── RecyclingTransaction.java          [NEW]
│   └── WasteDisposal.java                  [NEW]
├── repository/
│   ├── RecyclingTransactionRepository.java [NEW]
│   └── WasteDisposalRepository.java        [NEW]
├── service/
│   ├── RecyclingService.java               [NEW]
│   └── WasteDisposalService.java           [NEW]
└── service/impl/
    ├── RecyclingServiceImpl.java           [NEW]
    └── WasteDisposalServiceImpl.java       [NEW]

src/main/resources/templates/resident/
├── recycling-units.html                    [NEW]
├── recycle.html                            [NEW]
└── my-recycling.html                       [NEW]
```

### Modified Files
```
src/main/java/com/sliit/smartbin/smartbin/
├── model/User.java                         [MODIFIED - added recyclingPoints]
├── controller/ResidentController.java      [MODIFIED - added new endpoints]

src/main/resources/templates/resident/
└── dashboard.html                          [MODIFIED - enhanced UI]
```

---

## Compilation Status
✅ **BUILD SUCCESS**  
All code compiles without errors.  
Date: October 15, 2025, 12:45 PM

---

## Conclusion

The "Manage Daily Waste" use case has been fully implemented following SOLID principles with comprehensive error handling. The system now supports:

- ✅ Waste disposal with QR code scanning
- ✅ Fill level reporting
- ✅ Recycling transactions
- ✅ Points earning system
- ✅ Nearby bin search
- ✅ Recycling unit finder
- ✅ Transaction history
- ✅ Error handling with retry logic
- ✅ Modern, responsive UI

All features are ready for testing and can be extended in the future without major refactoring thanks to the SOLID architecture.

---

## Contact & Support
For questions or issues related to this implementation, please refer to:
- QUICK_START.md - How to run the application
- TESTING_GUIDE.md - Comprehensive testing procedures
- SAMPLE_CREDENTIALS.md - Login credentials for testing

**Implementation completed by AI Assistant**  
**Date:** October 15, 2025

