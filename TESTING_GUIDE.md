# Testing Guide for New Features
## SmartBin Waste Management Dashboard

This guide will help you test all the newly implemented features.

---

## 🚀 Prerequisites

1. **Start the Application:**
   ```bash
   # Windows
   run-smartbin.bat
   
   # Or use Maven
   mvnw spring-boot:run
   ```

2. **Access the Dashboard:**
   - Open browser: `http://localhost:8080`
   - Login as Authority user
   - Sample credentials should be in `SAMPLE_CREDENTIALS.md`

---

## ✅ Test Scenarios

### Test 1: Optimized Route Generation

**Objective:** Test the route optimization algorithm and map visualization

**Steps:**
1. Login to the dashboard as Authority user
2. You should see the dashboard with metrics and map
3. Click the **"🚛 Dispatch Collector"** button
4. In the modal:
   - Select a collector from the dropdown
   - Check at least 3-5 bins from the list (preferably bins that are FULL)
   - Click **"🚀 Optimize & Dispatch Route"**
5. Wait for the optimization to complete

**Expected Results:**
- Loading state: Button shows "Optimizing Route..."
- Success notification appears with route details:
  - Route ID
  - Total Distance (in km)
  - Estimated Duration (in minutes)
  - Number of bins
- Map updates to show:
  - Dashed polyline connecting all bins in optimized order
  - Numbered markers (1, 2, 3, etc.) showing the sequence
  - Map auto-zooms to fit the entire route
- Console shows notification log for collector

**What to Verify:**
✅ Route is optimized (bins visited in logical order)  
✅ Distance calculation is reasonable  
✅ Duration estimation makes sense (10 min/bin + travel time)  
✅ Map visualization is clear and intuitive  
✅ Notification is sent to collector  

---

### Test 2: Report Generation with Charts

**Objective:** Test dynamic report generation with Chart.js visualizations

**Steps:**
1. From the dashboard, click **"📄 Generate Report"** button
2. In the modal, fill out:
   - **Date Range:** Select "This Week"
   - **Region:** Select "North District" (or any region)
   - **Report Type:** Select "Bin Status Report"
3. Click **"📊 Generate Report"**
4. Wait for the report to generate

**Expected Results:**
- Loading state: Button shows "Generating Report..."
- Page redirects to reports page (`/authority/reports`)
- Report displays with:
  - **Title:** "Bin Status Report - NORTH (This Week)"
  - **Summary Statistics:** 4 cards showing:
    - Total Bins
    - Average Fill %
    - Overdue Bins
    - Collections
  - **Three Charts:**
    1. Bar Chart: Bin Fill Level Distribution (0-25%, 26-50%, 51-75%, 76-100%)
    2. Doughnut Chart: Bin Status Overview (Empty, Partial, Full, Overdue)
    3. Pie Chart: Bin Type Distribution (Standard, Recycling, Bulk)
  - **Data Table:** Detailed bin information
- Page auto-scrolls to the report section

**What to Verify:**
✅ Charts render properly with actual data  
✅ Charts are interactive (hover shows values)  
✅ Summary statistics display correct numbers  
✅ Data table shows relevant information  
✅ Export buttons are visible (PDF, CSV, Print)  

**Additional Tests:**
- Try different report types: "Collection Report", "Performance Report", "Overdue Bins Report"
- Try different regions: "All Regions", "South District", etc.
- Try different date ranges: "Today", "This Month"

---

### Test 3: Collector Management

**Objective:** Test collector assignment to regions

**Steps:**
1. From the dashboard, click **"👥 Manage Collectors"** button
2. In the modal:
   - Select a collector from the dropdown
   - Select a region (e.g., "North District")
   - Click **"Assign"**
3. Wait for the assignment to complete

**Expected Results:**
- Loading state during submission
- Success notification appears: "Collector assigned to region successfully!"
- Modal closes automatically
- Console shows notification log for collector
- Collector's region is updated in the database

**What to Verify:**
✅ Assignment completes without errors  
✅ Notification is sent to collector  
✅ Success message is displayed  
✅ Modal closes after successful assignment  

---

### Test 4: Auto-Refresh and Real-Time Updates

**Objective:** Test the auto-refresh functionality

**Steps:**
1. Stay on the dashboard
2. Watch the "Last updated" time at the bottom
3. Observe the refresh icon (🔄) spinning
4. Wait 30 seconds

**Expected Results:**
- Every 30 seconds:
  - Bin markers update with new fill levels
  - Metrics refresh with latest data
  - "Last updated" timestamp changes
  - "Map refreshed successfully!" notification appears (optional)

**What to Verify:**
✅ Auto-refresh happens every 30 seconds  
✅ Bin status updates automatically  
✅ Metrics cards update with new data  
✅ No page reload required  

---

### Test 5: Map Interactions

**Objective:** Test map interactivity

**Steps:**
1. On the dashboard map:
   - Click on different bin markers
   - Zoom in/out using controls
   - Pan around the map
   - Click the 🗺️ refresh button

**Expected Results:**
- Clicking markers shows popup with:
  - Bin QR Code
  - Fill Level percentage
  - Status
  - Location name
- Zoom and pan work smoothly
- Refresh button updates markers

**What to Verify:**
✅ Markers are color-coded correctly:
  - Blue (Normal < 50%)
  - Orange (Nearing 50-90%)
  - Red (Overdue > 90%, > 48 hours)
✅ Popups display correct information  
✅ Map controls work properly  

---

## 🐛 Troubleshooting

### Issue: Route optimization fails
**Solution:**
- Check that bins are properly initialized in the database
- Verify collector exists in the database
- Check browser console for errors
- Check server logs for exceptions

### Issue: Charts don't render
**Solution:**
- Verify Chart.js is loaded (check browser console)
- Check that report data is returned from API
- Clear browser cache and reload
- Check for JavaScript errors in console

### Issue: Map doesn't show bins
**Solution:**
- Verify bin data exists in database
- Check `/authority/api/bins` endpoint in browser
- Verify Leaflet.js is loaded
- Check JavaScript console for errors

### Issue: Notifications not appearing
**Solution:**
- Check browser console for logs
- Verify notification service is working
- Check server logs for notification entries
- Ensure session is valid

---

## 📊 Test Data

The system should have 32 sample bins initialized:
- **North District:** QR001-QR007 (Colombo Fort, Pettah, Galle Face, etc.)
- **South District:** QR008-QR012 (Wellawatte, Dehiwala, Mount Lavinia, etc.)
- **East District:** QR013-QR017 (Borella, Rajagiriya, Nugegoda, etc.)
- **West District:** QR018-QR022 (Negombo, Wattala, Ja-Ela, etc.)
- **Kandy Area:** QR023-QR027 (City Center, Temple, Lake, etc.)
- **Galle Area:** QR028-QR032 (Fort, Market, Unawatuna, etc.)

**Bin Statuses:**
- Mix of EMPTY, PARTIAL, FULL, and OVERDUE
- Fill levels: 15% to 97%
- Various bin types: STANDARD, RECYCLING, BULK

---

## 📝 Test Checklist

### Route Optimization ✅
- [ ] Can select multiple bins
- [ ] Can select collector
- [ ] Route optimization completes successfully
- [ ] Route is displayed on map with polyline
- [ ] Route stops are numbered sequentially
- [ ] Distance and duration are calculated
- [ ] Notification is sent to collector
- [ ] Success message is displayed

### Report Generation ✅
- [ ] Can select report parameters
- [ ] Report generates successfully
- [ ] Bar chart displays correctly
- [ ] Doughnut chart displays correctly
- [ ] Pie chart displays correctly
- [ ] Summary statistics are accurate
- [ ] Data table is populated
- [ ] Can test different report types
- [ ] Can test different regions

### Collector Management ✅
- [ ] Can select collector
- [ ] Can select region
- [ ] Assignment completes successfully
- [ ] Notification is sent
- [ ] Success message is displayed
- [ ] Modal closes automatically

### Real-Time Features ✅
- [ ] Auto-refresh works (30 seconds)
- [ ] Bin status updates automatically
- [ ] Metrics update automatically
- [ ] Last updated timestamp changes
- [ ] Live indicator is active

### Map Features ✅
- [ ] Bins display at correct locations
- [ ] Markers are color-coded correctly
- [ ] Popups show bin information
- [ ] Zoom and pan work smoothly
- [ ] Refresh button works
- [ ] Route visualization works

---

## 🎯 Success Criteria

All features are working correctly if:

1. **Route Optimization:**
   - Routes are calculated in < 1 second
   - Distance calculations are accurate
   - Map visualization is clear
   - Notifications are sent

2. **Report Generation:**
   - Reports generate in < 2 seconds
   - All charts render correctly
   - Data is accurate and up-to-date
   - Export options are available

3. **Collector Management:**
   - Assignments complete instantly
   - Database is updated correctly
   - Notifications are sent
   - UI confirms success

4. **Real-Time Updates:**
   - Auto-refresh works every 30 seconds
   - Data stays synchronized
   - No performance issues
   - No memory leaks

5. **User Experience:**
   - All interactions are smooth
   - Loading states are clear
   - Error messages are helpful
   - UI is intuitive and responsive

---

## 📞 Support

If you encounter any issues during testing:

1. Check the browser console for JavaScript errors
2. Check server logs for backend exceptions
3. Verify database connectivity
4. Ensure all sample data is initialized
5. Clear browser cache and reload
6. Restart the application if needed

---

**Happy Testing! 🚀**

All features have been implemented and should work as described. If you find any issues, please check the logs and verify the test data is properly initialized.

---

**Last Updated:** October 15, 2025  
**Version:** 1.0

