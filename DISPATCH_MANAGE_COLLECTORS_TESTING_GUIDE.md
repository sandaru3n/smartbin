# Dispatch Collectors & Manage Collectors - Testing Guide

## Overview
This guide provides comprehensive testing instructions for both the "Dispatch Collectors" and "Manage Collectors" features to ensure they work properly.

## Feature 1: Dispatch Collectors

### **Endpoint**: `/authority/dispatch`

### **Purpose**: 
- Authority can select bins that need collection
- Select a collector to dispatch
- System optimizes route using TSP algorithm
- Saves route to database and notifies collector

### **Testing Steps**:

#### **1. Navigate to Dispatch Page**
1. Login as Authority user
2. Go to Dashboard
3. Click "🚛 Dispatch Collector" button
4. Should redirect to `/authority/dispatch`

#### **2. Verify Page Loads Correctly**
✅ **Check These Elements**:
- Page title: "Dispatch Collector - SmartBin Authority"
- Breadcrumb: "Dashboard › Dispatch Collector"
- Left panel with collector selection
- Right panel with bins needing collection
- Filter buttons: "All", "Full", "Overdue", "Alerts"
- "Optimize & Dispatch Route" button

#### **3. Test Collector Selection**
1. Click on collector dropdown
2. Should show available collectors
3. Select a collector
4. Verify collector info displays correctly

#### **4. Test Bin Selection**
1. Check bins that need collection are displayed
2. Verify bin information shows:
   - QR Code
   - Location
   - Fill Level %
   - Status (Full/Overdue/Alert)
3. Select multiple bins by checking checkboxes
4. Verify selection counter updates

#### **5. Test Route Optimization**
1. Select a collector
2. Select 2-3 bins
3. Click "🚀 Optimize & Dispatch Route"
4. Should show loading state: "Optimizing Route..."
5. Should receive success notification
6. Should redirect to dashboard after 3 seconds

#### **6. Verify Database Operations**
```sql
-- Check if route was created
SELECT id, route_name, collector_id, status, created_at 
FROM routes 
ORDER BY created_at DESC 
LIMIT 5;

-- Check route bins were assigned
SELECT rb.route_id, rb.bin_id, rb.sequence_order, rb.status
FROM route_bins rb
JOIN routes r ON rb.route_id = r.id
ORDER BY r.created_at DESC, rb.sequence_order;
```

### **API Endpoint Testing**:

#### **POST /authority/api/optimize-route**
```javascript
// Test payload
{
    "collectorId": 1,
    "binIds": [1, 2, 3]
}
```

#### **Expected Response**:
```json
{
    "success": true,
    "message": "Route optimized and assigned successfully!",
    "routeId": 123,
    "routeName": "Optimized Route - 2024-01-15 10:30",
    "estimatedDuration": 45,
    "totalDistance": 12.5,
    "numBins": 3
}
```

### **Common Issues & Solutions**:

#### **Issue 1: No bins showing**
**Solution**: Check if bins exist with FULL, OVERDUE, or alertFlag=true status
```sql
SELECT id, qr_code, location, fill_level, status, alert_flag 
FROM bins 
WHERE status IN ('FULL', 'OVERDUE') OR alert_flag = true;
```

#### **Issue 2: Route optimization fails**
**Solution**: Check console logs for type casting errors
- Ensure binIds are properly converted from Integer to Long
- Verify collector exists in database

#### **Issue 3: No collectors available**
**Solution**: Check if collectors exist
```sql
SELECT id, name, email, role FROM users WHERE role = 'COLLECTOR';
```

---

## Feature 2: Manage Collectors

### **Endpoint**: `/authority/manage-collectors`

### **Purpose**:
- View all collectors and their current region assignments
- Assign collectors to specific regions
- Update region assignments in database
- Send notifications to collectors

### **Testing Steps**:

#### **1. Navigate to Manage Collectors Page**
1. Login as Authority user
2. Go to Dashboard
3. Click "👥 Manage Collectors" button
4. Should redirect to `/authority/manage-collectors`

#### **2. Verify Page Loads Correctly**
✅ **Check These Elements**:
- Page title: "Manage Collectors - SmartBin Authority"
- Statistics cards: Total Collectors, Active, On Route, Regions Covered
- Collectors table with columns:
  - Collector Info (Name, ID)
  - Email
  - Phone
  - Status
  - Region (with color-coded badges)
  - Active Routes
  - Actions (Assign Region, View)

#### **3. Test Collector Display**
1. Verify collectors are loaded from database
2. Check region badges show correctly:
   - 🔵 Blue badge for assigned regions
   - 🔴 Red badge for "Not Assigned"
3. Verify search functionality works
4. Check status badges (Active/Inactive/Busy)

#### **4. Test Region Assignment**
1. Click "📍 Assign Region" for any collector
2. Modal should open showing:
   - Collector name (pre-filled)
   - Current region (if assigned)
   - Region dropdown with options:
     - 📍 North District
     - 📍 South District
     - 📍 East District
     - 📍 West District
     - 📍 Central District
3. Select a region
4. Click "✅ Assign Region"

#### **5. Verify Assignment Process**
1. Should show loading state with spinner
2. Should display success notification with details
3. Modal should close
4. Page should auto-reload after 2 seconds
5. Region badge should update in table

#### **6. Verify Database Operations**
```sql
-- Check region assignments
SELECT id, name, email, region, updated_at 
FROM users 
WHERE role = 'COLLECTOR'
ORDER BY updated_at DESC;

-- Verify specific assignment
UPDATE users 
SET region = 'North District', updated_at = CURRENT_TIMESTAMP 
WHERE id = ? AND role = 'COLLECTOR';
```

### **API Endpoint Testing**:

#### **POST /authority/api/assign-collector**
```javascript
// Test payload
{
    "collectorId": 1,
    "region": "North District"
}
```

#### **Expected Response**:
```json
{
    "success": true,
    "message": "Collector John Doe assigned to North District successfully!"
}
```

### **Common Issues & Solutions**:

#### **Issue 1: Collectors not loading**
**Solution**: Check if collectors exist in database
```sql
SELECT COUNT(*) FROM users WHERE role = 'COLLECTOR';
```

#### **Issue 2: Region assignment fails**
**Solution**: Check console logs and verify:
- Collector ID is valid
- Region parameter is correct
- Database connection is working

#### **Issue 3: Region not updating in UI**
**Solution**: 
- Check if page reloads after assignment
- Verify database was updated
- Check browser console for JavaScript errors

---

## End-to-End Testing Scenarios

### **Scenario 1: Complete Dispatch Workflow**
1. **Setup**: Ensure you have:
   - At least 1 collector in database
   - At least 3 bins with FULL or OVERDUE status
   - Authority user logged in

2. **Test Steps**:
   - Navigate to dispatch page
   - Select a collector
   - Select 3 bins needing collection
   - Click "Optimize & Dispatch Route"
   - Verify success notification
   - Check database for new route
   - Verify collector notification was sent

### **Scenario 2: Complete Region Assignment Workflow**
1. **Setup**: Ensure you have:
   - At least 2 collectors in database
   - Authority user logged in

2. **Test Steps**:
   - Navigate to manage collectors page
   - View current region assignments
   - Assign collector 1 to "North District"
   - Assign collector 2 to "South District"
   - Verify assignments saved to database
   - Verify notifications sent to collectors

### **Scenario 3: Error Handling**
1. **Test with no bins selected**:
   - Try to dispatch without selecting bins
   - Should show error message

2. **Test with no collector selected**:
   - Try to dispatch without selecting collector
   - Should show error message

3. **Test duplicate region assignment**:
   - Try to assign same region to collector
   - Should show error message

## Database Verification Queries

### **Check Dispatch Functionality**:
```sql
-- Recent routes created
SELECT r.id, r.route_name, u.name as collector_name, r.status, r.created_at
FROM routes r
JOIN users u ON r.collector_id = u.id
ORDER BY r.created_at DESC
LIMIT 10;

-- Route bins assigned
SELECT rb.route_id, b.qr_code, b.location, rb.sequence_order
FROM route_bins rb
JOIN bins b ON rb.bin_id = b.id
ORDER BY rb.route_id, rb.sequence_order;
```

### **Check Manage Collectors Functionality**:
```sql
-- Collector region assignments
SELECT id, name, email, region, updated_at,
       CASE 
           WHEN region IS NULL OR region = '' THEN 'Not Assigned'
           ELSE region
       END as assignment_status
FROM users 
WHERE role = 'COLLECTOR'
ORDER BY updated_at DESC;

-- Region distribution
SELECT 
    CASE 
        WHEN region IS NULL OR region = '' THEN 'Not Assigned'
        ELSE region
    END as region,
    COUNT(*) as collector_count
FROM users 
WHERE role = 'COLLECTOR'
GROUP BY region
ORDER BY collector_count DESC;
```

## Performance Testing

### **Load Testing**:
1. **Dispatch with many bins**: Test with 10+ bins
2. **Manage many collectors**: Test with 50+ collectors
3. **Concurrent assignments**: Multiple authority users assigning regions

### **Response Time Expectations**:
- **Dispatch page load**: < 2 seconds
- **Route optimization**: < 5 seconds
- **Region assignment**: < 1 second
- **Page reloads**: < 3 seconds

## Browser Compatibility Testing

### **Supported Browsers**:
- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

### **Mobile Testing**:
- Test on mobile devices
- Verify responsive design
- Check touch interactions

## Security Testing

### **Authorization Tests**:
1. Try accessing dispatch page without login
2. Try accessing manage collectors without login
3. Try API calls without valid session
4. Verify only AUTHORITY users can access these features

### **Input Validation Tests**:
1. Try sending invalid collector IDs
2. Try sending invalid bin IDs
3. Try sending invalid regions
4. Verify all inputs are properly validated

## Troubleshooting Guide

### **Common Error Messages**:

#### **"Failed to optimize route: class java.lang.Integer cannot be cast to class java.lang.Long"**
**Solution**: This has been fixed with proper type conversion in AuthorityController

#### **"Collector not found"**
**Solution**: Verify collector exists in database with COLLECTOR role

#### **"No bins found for the provided IDs"**
**Solution**: Check if selected bins exist and are accessible

#### **"Failed to assign collector"**
**Solution**: Check database connection and collector permissions

### **Debug Steps**:
1. Check browser console for JavaScript errors
2. Check server logs for Java exceptions
3. Verify database connectivity
4. Check session validity
5. Verify user permissions

## Success Criteria

### **Dispatch Collectors Feature**:
✅ Page loads with bins needing collection
✅ Collectors are selectable from dropdown
✅ Bin selection works with checkboxes
✅ Route optimization completes successfully
✅ Database saves route and route_bins
✅ Success notification displays
✅ Redirects to dashboard after completion

### **Manage Collectors Feature**:
✅ Page loads with all collectors
✅ Region assignments display correctly
✅ Assignment modal opens and functions
✅ Region selection works
✅ Database saves region assignment
✅ Success notification displays
✅ Page reloads with updated assignments

Both features should work seamlessly with proper error handling, user feedback, and database persistence.
