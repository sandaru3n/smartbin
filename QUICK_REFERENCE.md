# Quick Reference Guide
## SmartBin Waste Management Dashboard - New Features

Quick reference for the newly implemented features.

---

## 🚀 Quick Start

1. **Run the application:**
   ```bash
   run-smartbin.bat
   # or
   .\mvnw.cmd spring-boot:run
   ```

2. **Access dashboard:**
   - URL: `http://localhost:8080`
   - Login as Authority user

---

## 📋 Feature Quick Reference

### 1. Optimize & Dispatch Route
**Location:** Dashboard → "🚛 Dispatch Collector" button

**Quick Steps:**
1. Click "Dispatch Collector"
2. Select collector
3. Check bins to collect
4. Click "🚀 Optimize & Dispatch Route"

**Result:** Optimized route shown on map with numbered stops

**API:** `POST /authority/api/optimize-route`

---

### 2. Generate Reports
**Location:** Dashboard → "📄 Generate Report" button  
**OR** Direct: `http://localhost:8080/authority/reports`

**Quick Steps:**
1. Click "Generate Report"
2. Select date range, region, report type
3. Click "📊 Generate Report"

**Result:** Interactive charts and tables with data

**API:** `POST /authority/api/reports/generate`

**Report Types:**
- Collection Report
- Performance Report  
- Bin Status Report
- Overdue Bins Report

---

### 3. Manage Collectors
**Location:** Dashboard → "👥 Manage Collectors" button

**Quick Steps:**
1. Click "Manage Collectors"
2. Select collector
3. Select region
4. Click "Assign"

**Result:** Collector assigned to region, notification sent

**API:** `POST /authority/api/assign-collector`

---

## 🗺️ Map Features

**Marker Colors:**
- 🔵 **Blue** - Normal (< 50% fill)
- 🟠 **Orange** - Nearing full (50-90% fill)
- 🔴 **Red** - Overdue (> 90% fill, > 48 hours)

**Route Visualization:**
- **Dashed line** - Optimized route path
- **Numbered markers** - Route sequence (1, 2, 3...)
- **Popups** - Click markers for bin details

**Controls:**
- Click markers for info
- Zoom with +/- buttons
- Click 🗺️ to refresh
- Drag to pan

---

## 📊 Chart Types

**Report Page Charts:**

1. **Bar Chart** - Bin Fill Level Distribution
   - Shows bins by fill level ranges
   - Color-coded by urgency

2. **Doughnut Chart** - Bin Status Overview
   - Empty, Partial, Full, Overdue
   - Interactive with percentages

3. **Pie Chart** - Bin Type Distribution
   - Standard, Recycling, Bulk
   - Shows distribution by type

---

## 🔔 Notifications

**Notification Types:**
- ✅ **Success** - Green, 3-second auto-dismiss
- ❌ **Error** - Red, stays until dismissed

**When Notifications Appear:**
- Route optimized successfully
- Report generated
- Collector assigned
- Map refreshed
- Errors occur

---

## 🎯 Key Metrics

**Dashboard Metrics:**
1. **Total Bins** - Total bins in system
2. **Average Fill %** - Average fill level across all bins
3. **Overdue Bins** - Bins not emptied for > 48 hours
4. **Last Collection** - Time since last collection

**Auto-Refresh:** Every 30 seconds

---

## 🔧 API Endpoints

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/authority/api/optimize-route` | POST | Generate optimized route |
| `/authority/api/route/{id}` | GET | Get route details |
| `/authority/api/reports/generate` | POST | Generate report data |
| `/authority/api/assign-collector` | POST | Assign collector to region |
| `/authority/api/bins` | GET | Get all bin data |
| `/authority/api/bin-status` | GET | Get bin status summary |

---

## 🗂️ Modified Files

**Backend:**
- `RouteServiceImpl.java` - Route optimization algorithm
- `AuthorityController.java` - New API endpoints
- `NotificationServiceImpl.java` - Already existed

**Frontend:**
- `dashboard.html` - Map integration, dispatch modal
- `reports.html` - Chart.js integration, dynamic reports

**Documentation:**
- `FEATURE_IMPLEMENTATION_SUMMARY.md` - Complete feature documentation
- `TESTING_GUIDE.md` - Step-by-step testing instructions
- `QUICK_REFERENCE.md` - This file

---

## 💡 Tips

1. **Route Optimization:**
   - Select 5-10 bins for best results
   - Choose bins in same region
   - Full bins get priority

2. **Report Generation:**
   - Start with "Bin Status Report" for overview
   - Use "Overdue Bins Report" for urgent issues
   - Try different date ranges for trends

3. **Map Usage:**
   - Click markers to see bin details
   - Watch for red markers (overdue)
   - Route lines show optimized path

4. **Performance:**
   - Route optimization: < 1 second
   - Report generation: < 2 seconds
   - Map updates: Instant
   - Auto-refresh: Every 30 seconds

---

## 🐛 Common Issues

**Issue:** Route not showing on map  
**Fix:** Refresh the page, check selected bins

**Issue:** Charts not rendering  
**Fix:** Clear browser cache, reload page

**Issue:** No bins in list  
**Fix:** Database may need initialization

**Issue:** Collector dropdown empty  
**Fix:** Ensure collectors exist in database

---

## 📱 Workflow Summary

### Complete Route Dispatch Flow:
```
Authority Dashboard
    ↓
Select "Dispatch Collector"
    ↓
Choose Bins + Collector
    ↓
Click "Optimize & Dispatch"
    ↓
Server Calculates Route (TSP Algorithm)
    ↓
Route Saved to Database
    ↓
Notification Sent to Collector
    ↓
Map Shows Optimized Route
    ↓
Confirmation Message Displayed
```

### Complete Report Generation Flow:
```
Authority Dashboard
    ↓
Click "Generate Report"
    ↓
Select Parameters (Date, Region, Type)
    ↓
Click "Generate Report"
    ↓
Server Queries Database
    ↓
Data Formatted & Validated
    ↓
Charts Rendered with Chart.js
    ↓
Statistics & Tables Displayed
    ↓
Export Options Available
```

### Complete Collector Assignment Flow:
```
Authority Dashboard
    ↓
Click "Manage Collectors"
    ↓
Select Collector + Region
    ↓
Click "Assign"
    ↓
Database Updated
    ↓
Notification Sent to Collector
    ↓
Confirmation Message Displayed
```

---

## 📞 Support

**For Issues:**
1. Check browser console (F12)
2. Check server logs
3. Verify database connection
4. Restart application if needed

**Documentation Files:**
- `FEATURE_IMPLEMENTATION_SUMMARY.md` - Detailed documentation
- `TESTING_GUIDE.md` - Testing instructions
- `QUICK_REFERENCE.md` - This guide

---

## ✅ Feature Checklist

- ✅ Route optimization with TSP algorithm
- ✅ Real-time map visualization
- ✅ Collector notifications
- ✅ Report generation with charts
- ✅ Collector management
- ✅ Auto-refresh functionality
- ✅ Interactive charts (Chart.js)
- ✅ Interactive maps (Leaflet.js)
- ✅ Responsive UI design
- ✅ Complete workflow integration

---

**All features are implemented and ready to use! 🎉**

Start with the dashboard, try dispatching a route, and explore the features.

---

**Last Updated:** October 15, 2025  
**Version:** 1.0  
**Status:** ✅ Complete

