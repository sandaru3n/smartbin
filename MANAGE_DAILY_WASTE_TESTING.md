# Quick Testing Guide: Manage Daily Waste Features

## Prerequisites
1. Start the SmartBin application
2. Login as a Resident user

## Test Credentials
**Resident Account:**
- Email: `resident@smartbin.com` (or create new account)
- Password: Check SAMPLE_CREDENTIALS.md

---

## Feature 1: Scan Bin & Submit Waste Disposal

### Steps:
1. Navigate to Resident Dashboard
2. Click **"Scan Bin QR Code"** quick action button
3. Modal window opens
4. Enter a bin QR code:
   - Format: `BIN001`, `BIN002`, `BIN123`, etc.
   - Valid codes: BIN followed by 3-4 digits
5. Adjust the **Fill Level** slider (try 85%)
6. Add optional notes: "Bin is getting full"
7. Click **"Submit Waste Disposal"**

### Expected Results:
✅ Success message: "Waste disposal recorded successfully!"  
✅ Bin fill level updated in database  
✅ If fill level >= 80%, notification sent to collectors  
✅ Disposal appears in "Recent Activity" section  

### Test Invalid QR:
- Try entering: `ABC123` → Should show validation error
- Try entering: `BIN1` → Should show validation error (needs 3-4 digits)

---

## Feature 2: Find & Use Recycling Units

### Steps:
1. From dashboard, click **"Find Recycling Units"**
2. View list of 5 mock recycling units in Colombo area
3. Each unit shows:
   - Name and QR code
   - Address
   - Distance from you
   - Accepted items
4. Click **"♻️ Recycle Here"** on any unit

### Expected Results:
✅ Shows 5 recycling units  
✅ Distances: 0.5 km, 1.2 km, 1.8 km, 2.3 km, 3.1 km  
✅ Map placeholder displayed  
✅ Clicking "Recycle Here" opens recycling form  

---

## Feature 3: Submit Recycling Transaction

### Steps:
1. On the recycling form, you should see recycling unit QR pre-filled (e.g., `RU001`)
2. Select **Item Type**: Plastic
3. Enter **Weight**: `2.5` kg
4. Enter **Quantity**: `15` items
5. Watch the **Estimated Earnings** preview update:
   - Points: 27.5 (2.5 kg × 10 points/kg × 1.1 bonus for 15 items)
   - Value: LKR 125.00 (2.5 kg × 50 LKR/kg)
6. Click **"♻️ Confirm & Submit Recycling"**

### Expected Results:
✅ Success message: "Recycling confirmed! You earned 27.50 points (Value: LKR 125.00). Keep up the great work!"  
✅ Points added to user account  
✅ Transaction saved in database  
✅ Redirected to dashboard  
✅ Points badge updated  

### Test Different Item Types:

| Item Type   | Weight | Quantity | Expected Points | Expected Value |
|-------------|--------|----------|-----------------|----------------|
| Plastic     | 2.0 kg | 5        | 20.00           | LKR 100.00     |
| Paper       | 3.0 kg | 8        | 15.00           | LKR 90.00      |
| Metal       | 1.5 kg | 12       | 33.00 (bonus)   | LKR 150.00     |
| Glass       | 2.5 kg | 20       | 41.25 (bonus)   | LKR 100.00     |
| Cardboard   | 4.0 kg | 15       | 30.80 (bonus)   | LKR 140.00     |
| Electronics | 1.0 kg | 3        | 50.00           | LKR 200.00     |

**Note:** Bonus (+10%) applies when quantity > 10 items

---

## Feature 4: View Recycling History

### Steps:
1. From dashboard, click **"My Recycling History"** quick action
2. View your statistics:
   - Total Points
   - Total Transactions
   - Total Value Earned
   - Total Weight Recycled
3. Scroll down to see transaction list
4. Each transaction shows:
   - Item type icon
   - Status badge (CONFIRMED/PENDING/FAILED)
   - Weight, quantity, value
   - Points earned
   - Date and time

### Expected Results:
✅ All recycling transactions displayed  
✅ Statistics accurately calculated  
✅ Transactions sorted by date (newest first)  
✅ Green points display for each transaction  

---

## Feature 5: Points Display on Dashboard

### Steps:
1. Return to dashboard
2. Look at the welcome card
3. View the **recycling points badge**
4. Check **Recent Activity** section

### Expected Results:
✅ Points badge shows total accumulated points  
✅ Recent recycling transactions listed (max 5)  
✅ Recent waste disposals listed (max 5)  
✅ Each activity shows:
   - Type and details
   - Date/time
   - Points earned (for recycling)
   - Status

---

## Error Handling Tests

### Test 1: Invalid Bin QR Code
1. Click "Scan Bin QR Code"
2. Enter: `INVALID123`
3. Try to submit

**Expected:** HTML5 validation prevents submission

### Test 2: Invalid Recycling Unit QR
1. Go to recycling form
2. Change QR code to: `INVALID`
3. Try to submit

**Expected:** JavaScript validation shows alert: "Invalid QR code format"

### Test 3: Missing Required Fields
1. On recycling form, leave item type blank
2. Try to submit

**Expected:** Browser validation: "Please select an item"

### Test 4: Non-existent Bin
1. Enter bin QR: `BIN9999` (doesn't exist)
2. Submit disposal form

**Expected:** Error message: "Bin not found" (after retry attempts)

---

## UI/UX Features to Verify

### Dashboard Enhancements:
- ✅ Modern gradient navbar
- ✅ Prominent recycling points badge with gradient background
- ✅ 4 quick action buttons with hover effects
- ✅ 4 dashboard stat cards
- ✅ Recent activity section with card layout
- ✅ Nearby bins section (existing feature)

### Interactive Elements:
- ✅ Modal opens/closes smoothly
- ✅ Fill level slider updates display in real-time
- ✅ Recycling preview calculates on input change
- ✅ Alerts auto-close after 5 seconds
- ✅ Cards have hover effects (slight elevation)
- ✅ Buttons have hover animations

### Responsive Design:
- ✅ Works on desktop (1200px+)
- ✅ Works on tablet (768px+)
- ✅ Works on mobile (320px+)
- ✅ Grid layouts adjust automatically

---

## Mock Data Reference

### Available Bin QR Codes (from sample_data.sql):
- BIN001, BIN002, BIN003, BIN004, BIN005, BIN006, etc.

### Available Recycling Unit QR Codes:
- RU001 - Colombo Central Recycling Hub
- RU002 - Green Point Recycling Center
- RU003 - Eco Recycling Station
- RU004 - Smart Recycle Point
- RU005 - Community Recycling Hub

### Recyclable Item Types:
1. plastic
2. paper
3. metal
4. glass
5. cardboard
6. electronics

---

## Complete User Journey Test

### Full Workflow (15 minutes):

1. **Login as Resident**
   - Navigate to `/resident/login`
   - Login with credentials

2. **View Dashboard**
   - Check points badge (should be 0 initially)
   - Note the 4 quick action buttons
   - See nearby bins

3. **Submit Waste Disposal**
   - Click "Scan Bin QR Code"
   - Enter BIN001
   - Set fill level to 90%
   - Add note: "Testing waste disposal"
   - Submit
   - Verify success message

4. **Find Recycling Units**
   - Click "Find Recycling Units"
   - Browse the 5 available units
   - Note distances and accepted items
   - Click "Recycle Here" on RU001

5. **Submit First Recycling**
   - Select: Plastic
   - Weight: 3.0 kg
   - Quantity: 5
   - Verify preview: 30 points, LKR 150
   - Submit
   - Verify success message with points

6. **Submit Second Recycling (with bonus)**
   - Return to recycling units
   - Select RU002
   - Select: Electronics
   - Weight: 1.5 kg
   - Quantity: 12 items
   - Verify preview: 82.5 points (with 10% bonus)
   - Submit

7. **View History**
   - Click "My Recycling History"
   - Verify statistics:
     - Total Points: 112.5
     - Total Transactions: 2
     - Total Value: LKR 450
     - Total Weight: 4.5 kg
   - Verify both transactions appear in list

8. **Check Dashboard Update**
   - Return to dashboard
   - Verify points badge shows 112.5
   - Verify recent activity shows:
     - 2 recycling transactions
     - 1 waste disposal
   - All with correct details and timestamps

### Success Criteria:
✅ All 8 steps complete without errors  
✅ Points correctly calculated and displayed  
✅ All transactions saved and retrievable  
✅ UI responsive and intuitive  
✅ Error messages clear and helpful  

---

## Performance Checks

### Expected Response Times:
- Dashboard load: < 1 second
- Form submission: < 2 seconds
- Recycling units search: < 500ms (mock data)
- History page: < 1 second

### Database Verification:
After testing, check database tables:
```sql
-- Check recycling transactions
SELECT * FROM recycling_transactions ORDER BY created_at DESC;

-- Check waste disposals
SELECT * FROM waste_disposals ORDER BY created_at DESC;

-- Check user points
SELECT name, recycling_points FROM users WHERE role = 'RESIDENT';

-- Check bin fill levels
SELECT qr_code, fill_level, status FROM bins WHERE qr_code = 'BIN001';
```

---

## Troubleshooting

### Issue: "Bin not found" error
**Solution:** Check that bin exists in database with the entered QR code

### Issue: Points not updating
**Solution:** 
1. Refresh the page
2. Check transaction status (should be CONFIRMED)
3. Verify database update: `SELECT recycling_points FROM users WHERE id = ?`

### Issue: Modal not opening
**Solution:** 
1. Check browser console for JavaScript errors
2. Clear browser cache
3. Try different browser

### Issue: Form validation not working
**Solution:**
1. Ensure using modern browser (Chrome, Firefox, Edge)
2. Check HTML5 pattern attributes are supported
3. JavaScript must be enabled

---

## Next Steps After Testing

1. ✅ Verify all features work as expected
2. ✅ Test error scenarios
3. ✅ Check database consistency
4. ✅ Review user experience
5. 📝 Document any bugs found
6. 🚀 Ready for production deployment

---

## Support

For issues or questions:
- Check: MANAGE_DAILY_WASTE_IMPLEMENTATION.md
- Review: QUICK_START.md
- Debug: Check browser console and application logs

**Happy Testing! ♻️🗑️**

