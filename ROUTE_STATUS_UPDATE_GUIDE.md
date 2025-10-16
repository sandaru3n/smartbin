# Route Status Update Guide - Collector Routes Page

## 🎯 Overview
Collectors can update route status from **ASSIGNED → IN_PROGRESS → COMPLETED** directly from `http://localhost:8084/collector/routes`.

## ✅ Implemented Features

### **Route Status Workflow:**
```
ASSIGNED → (Activate Route) → IN_PROGRESS → (Complete Route) → COMPLETED
```

## 📍 How It Works

### **1. ASSIGNED Routes**

#### **What You See:**
- Route appears in **Active Routes** section at the top
- Status badge: "ASSIGNED" (blue)
- **"Activate Route"** button is visible (green, pulsing)
- View Details button

#### **What You Can Do:**
- Click **"Activate Route"** to start the route
- This changes status to `IN_PROGRESS`
- System records start time
- Success message appears

#### **Visual Display:**
```
╔══════════════════════════════════════════════════╗
║  📍 Optimized Route - 2025-10-15                ║
║  Status: ASSIGNED                                ║
║  Duration: 12 min | Distance: 2.5 km | Bins: 3  ║
║                                                  ║
║  Assigned Bins:                                  ║
║  [1] QR001 - Location A - 95% - FULL → 0.8 km  ║
║  [2] QR002 - Location B - 87% - FULL → 0.9 km  ║
║  [3] QR003 - Location C - 92% - FULL            ║
║                                                  ║
║  [View Details]  [🎯 Activate Route]            ║
╚══════════════════════════════════════════════════╝
```

### **2. IN_PROGRESS Routes**

#### **What You See:**
- Route appears in **Active Routes** section
- Status badge: "IN_PROGRESS" (orange)
- Progress bar showing completion (if bins collected)
- **"Complete Route"** button is visible (yellow)
- View Details button

#### **What You Can Do:**
- Continue collecting bins
- Click **"Complete Route"** when all bins are collected
- This changes status to `COMPLETED`
- System calculates total duration
- Success message appears

#### **Visual Display:**
```
╔══════════════════════════════════════════════════╗
║  📍 Optimized Route - 2025-10-15                ║
║  Status: IN_PROGRESS                             ║
║  Duration: 12 min | Distance: 2.5 km | Bins: 3  ║
║                                                  ║
║  Progress: [████████░░] 2/3 bins (67%)          ║
║                                                  ║
║  Assigned Bins:                                  ║
║  [1] QR001 - Location A - 95% - FULL → 0.8 km  ║
║  [2] QR002 - Location B - 87% - FULL → 0.9 km  ║
║  [3] QR003 - Location C - 92% - FULL            ║
║                                                  ║
║  [View Details]  [✓ Complete Route]             ║
╚══════════════════════════════════════════════════╝
```

### **3. COMPLETED Routes**

#### **What You See:**
- Route appears in **All Routes History** section
- Status badge: "COMPLETED" (green)
- Completion date and actual duration displayed
- Only "View Details" button

#### **What You Can Do:**
- View route details for reference
- Review collection history

#### **Visual Display:**
```
╔══════════════════════════════════════════════════╗
║  📍 Optimized Route - 2025-10-15                ║
║  Status: COMPLETED                               ║
║  Completed: Oct 15, 20:30                        ║
║  Duration: 11 min | Distance: 2.5 km | Bins: 3  ║
║                                                  ║
║  [View Details]                                  ║
╚══════════════════════════════════════════════════╝
```

## 🚀 Step-by-Step Usage

### **Scenario: Starting and Completing a Route**

#### **Step 1: Login**
```
URL: http://localhost:8084/collector/login
Email: collector1@gmail.com
Password: password123
```

#### **Step 2: Navigate to Routes**
```
URL: http://localhost:8084/collector/routes
```

#### **Step 3: View Active Routes**
- Look at **Active Routes** section (purple gradient background)
- See assigned routes with all details
- Review bins, distances, and locations

#### **Step 4: Activate Route (ASSIGNED → IN_PROGRESS)**
1. Click the **"Activate Route"** button (green, pulsing)
2. See success message: "Route 'Route Name' started successfully! You can now start collecting bins."
3. Route status changes to `IN_PROGRESS`
4. Button changes to **"Complete Route"**
5. Progress bar may appear if bins are tracked

#### **Step 5: Collect Bins**
- Navigate to each bin location
- Scan QR codes at `http://localhost:8084/collector/scan-qr`
- Collect waste at each bin
- System tracks progress

#### **Step 6: Complete Route (IN_PROGRESS → COMPLETED)**
1. Return to `http://localhost:8084/collector/routes`
2. Find your in-progress route
3. Click the **"Complete Route"** button (yellow)
4. See success message: "Route 'Route Name' completed successfully! Great job!"
5. Route status changes to `COMPLETED`
6. Route moves to history section
7. System calculates actual duration

## 🎨 Visual Indicators

### **Button Styles:**

**Activate Route Button:**
```css
Background: Green gradient (#4caf50 → #45a049)
Size: Large (0.75rem × 1.5rem padding)
Animation: Pulsing glow effect
Icon: play_arrow
Text: "Activate Route"
```

**Complete Route Button:**
```css
Background: Warning yellow (#ff9800)
Size: Standard
Icon: check_circle
Text: "Complete Route"
```

**View Details Button:**
```css
Background: Primary blue (#1a73e8)
Size: Standard
Icon: visibility
Text: "View Details"
```

### **Status Badges:**

| Status | Color | Background |
|--------|-------|------------|
| ASSIGNED | Blue (#1976d2) | Light Blue (#e3f2fd) |
| IN_PROGRESS | Orange (#f57c00) | Light Orange (#fff3e0) |
| COMPLETED | Green (#2e7d32) | Light Green (#e8f5e9) |

## 🔄 Real-time Updates

### **Auto-Update Feature:**
- Page automatically polls for updates every 30 seconds
- Route status updates automatically
- Progress bars update in real-time
- Action buttons change based on status
- No page refresh needed

### **Manual Refresh:**
- Browser refresh will show latest status
- All changes are persisted to database

## 📊 Database Updates

### **When Activating a Route:**
```sql
UPDATE routes 
SET 
    status = 'IN_PROGRESS',
    started_date = CURRENT_TIMESTAMP
WHERE id = ?
```

### **When Completing a Route:**
```sql
UPDATE routes 
SET 
    status = 'COMPLETED',
    completed_date = CURRENT_TIMESTAMP,
    actual_duration_minutes = TIMESTAMPDIFF(MINUTE, started_date, CURRENT_TIMESTAMP)
WHERE id = ?
```

## 🎯 Testing Checklist

### **Test ASSIGNED → IN_PROGRESS:**
- [ ] Login as collector
- [ ] Navigate to routes page
- [ ] See route in Active Routes section
- [ ] Click "Activate Route"
- [ ] See success flash message
- [ ] Verify status badge changes to "IN_PROGRESS"
- [ ] Verify button changes to "Complete Route"
- [ ] Verify route stays in Active Routes section

### **Test IN_PROGRESS → COMPLETED:**
- [ ] Find in-progress route
- [ ] Click "Complete Route"
- [ ] See success flash message
- [ ] Verify status badge changes to "COMPLETED"
- [ ] Verify route moves to All Routes History section
- [ ] Verify only "View Details" button remains

### **Test Real-time Updates:**
- [ ] Start a route
- [ ] Wait 30 seconds
- [ ] Verify auto-update occurs
- [ ] Check progress bars update
- [ ] Verify action buttons are correct

### **Test Multiple Routes:**
- [ ] Activate multiple routes
- [ ] Verify all show correct status
- [ ] Complete routes one by one
- [ ] Verify counts update correctly

## 📱 Page Layout

### **Active Routes Section (Top):**
```
┌────────────────────────────────────────┐
│ 🚛 Active Routes                   [3] │
│ Routes ready to start or in progress   │
├────────────────────────────────────────┤
│ [ASSIGNED Route Cards]                 │
│ [IN_PROGRESS Route Cards]              │
└────────────────────────────────────────┘
```

### **Filter Section (Middle):**
```
┌────────────────────────────────────────┐
│ All Routes                             │
│ [All] [Assigned] [In Progress] [Done] │
└────────────────────────────────────────┘
```

### **All Routes History (Bottom):**
```
┌────────────────────────────────────────┐
│ 📋 All Routes History                  │
│ [All Route Cards - Filtered]           │
└────────────────────────────────────────┘
```

## ⚡ Quick Reference

### **Available Actions by Status:**

| Current Status | Available Action | Button | Next Status |
|---------------|------------------|--------|-------------|
| ASSIGNED | Activate Route | Green (pulsing) | IN_PROGRESS |
| IN_PROGRESS | Complete Route | Yellow | COMPLETED |
| COMPLETED | View Details | Blue | - |

### **Flash Messages:**

**On Route Start:**
```
✓ Route 'Optimized Route - 2025-10-15' started successfully! 
  You can now start collecting bins.
```

**On Route Complete:**
```
✓ Route 'Optimized Route - 2025-10-15' completed successfully! 
  Great job!
```

**On Error:**
```
✗ Failed to start route: [error message]
✗ Failed to complete route: [error message]
```

## 🔧 Backend Endpoints

### **Start Route:**
```
POST /collector/route/{id}/start
Redirect: /collector/routes
Flash: success or error message
```

### **Complete Route:**
```
POST /collector/route/{id}/complete
Redirect: /collector/routes
Flash: success or error message
```

### **Get Route Status (Real-time):**
```
GET /collector/api/route-status
Returns: JSON array of route statuses with progress
```

## 🎉 Benefits

### **For Collectors:**
1. ✅ One-click route activation
2. ✅ Clear visual feedback
3. ✅ Real-time progress tracking
4. ✅ Simple status updates
5. ✅ Professional UI/UX

### **For Operations:**
1. ✅ Accurate time tracking
2. ✅ Real-time route status
3. ✅ Automatic duration calculation
4. ✅ Complete audit trail
5. ✅ Performance metrics

## 💡 Tips

1. **Always activate before collecting**: Routes must be activated before bin collection
2. **Check bin details**: Review assigned bins and distances before starting
3. **Monitor progress**: Progress bar shows how many bins are collected
4. **Complete when done**: Mark route complete after all bins are collected
5. **Use real-time updates**: Page auto-refreshes, no manual refresh needed

## 🎯 Summary

The routes page provides **complete route status management**:
- ✅ **ASSIGNED routes**: Click "Activate Route" to start
- ✅ **IN_PROGRESS routes**: Click "Complete Route" to finish
- ✅ **COMPLETED routes**: View details for reference
- ✅ **Real-time updates**: Auto-refresh every 30 seconds
- ✅ **Visual feedback**: Flash messages and status badges
- ✅ **Complete tracking**: Start times, durations, and completion dates

Collectors have **full control** over route status updates with a **professional, intuitive interface**! 🚛✨

