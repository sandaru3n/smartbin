# Quick Reference: Manage Daily Waste Features

## 🚀 Quick Start (5 minutes)

### 1. Start Application
```bash
./run-smartbin.bat
```

### 2. Login as Resident
- Navigate to: http://localhost:8080/resident/login
- Use resident credentials from `SAMPLE_CREDENTIALS.md`

### 3. Test Key Features

#### Submit Waste Disposal
1. Click **"Scan Bin QR Code"** button
2. Enter: `BIN001`
3. Set fill level: `85%`
4. Click **Submit**

#### Earn Recycling Points
1. Click **"Find Recycling Units"**
2. Select any unit → Click **"Recycle Here"**
3. Choose: `Plastic`, Weight: `2.5 kg`, Quantity: `12`
4. See preview: **27.5 points**
5. Click **Submit**

#### View History
1. Click **"My Recycling History"**
2. See your statistics and transactions

---

## 📍 New Features at a Glance

| Feature | Location | Action |
|---------|----------|--------|
| **Scan Bin QR** | Dashboard → Modal | Quick disposal form |
| **Find Recycling Units** | Dashboard → Quick Actions | Browse 5 mock units on map |
| **Search Nearby Bins** | Dashboard → Quick Actions | Find bins with interactive map |
| **Submit Recycling** | Recycling Units → Unit Card | Earn points |
| **View History** | Dashboard → Quick Actions | See all transactions |
| **Points Display** | Dashboard → Welcome Card | Badge with total points |
| **Recent Activity** | Dashboard → Bottom Section | Last 5 activities |

---

## 🎯 Valid QR Codes

### Bins (Format: BIN + 3-4 digits)
```
BIN001, BIN002, BIN003, BIN004, BIN005, BIN123, BIN1234
```

### Recycling Units (Format: RU + 3-4 digits)
```
RU001 - Colombo Central Recycling Hub
RU002 - Green Point Recycling Center  
RU003 - Eco Recycling Station
RU004 - Smart Recycle Point
RU005 - Community Recycling Hub
```

---

## 💰 Points Cheat Sheet

| Item | Points/kg | LKR/kg | Example (2kg) |
|------|-----------|--------|---------------|
| Electronics | 50 | 200 | 100 pts, LKR 400 |
| Metal | 20 | 100 | 40 pts, LKR 200 |
| Glass | 15 | 40 | 30 pts, LKR 80 |
| Plastic | 10 | 50 | 20 pts, LKR 100 |
| Cardboard | 7 | 35 | 14 pts, LKR 70 |
| Paper | 5 | 30 | 10 pts, LKR 60 |

**Bonus:** +10% points when quantity > 10 items

---

## 🎨 UI Elements

### Dashboard Components
- **Points Badge** (top) - Shows total recycling points
- **Quick Actions** (4 buttons) - One-click access to features
- **Stats Cards** (4 cards) - Bins, Alerts, Recycling, Disposals
- **Recent Activity** - Last 5 transactions/disposals
- **Nearby Bins** - Map and list view

### Interactive Elements
- **Modal** - QR scanner with slider
- **Forms** - Auto-validation and previews
- **Cards** - Hover effects and animations
- **Alerts** - Auto-dismiss after 5 seconds

---

## ⚡ Quick Actions Guide

### Action 1: Scan Bin QR Code
```
Click → Enter QR → Adjust Slider → Submit
⏱️ Time: 10 seconds
```

### Action 2: Find Recycling Units
```
Click → Browse List → Select Unit → Recycle Here
⏱️ Time: 20 seconds
```

### Action 3: Submit Recycling
```
Select Type → Enter Weight → Enter Quantity → Submit
⏱️ Time: 30 seconds
```

### Action 4: View History
```
Click → See Stats → Browse Transactions
⏱️ Time: 15 seconds
```

---

## 🔍 Testing Checklist

### ✅ Must Test
- [ ] Submit waste disposal with BIN001
- [ ] Submit recycling with RU001 (Plastic, 2kg)
- [ ] View points badge update
- [ ] Check recent activity shows new items
- [ ] View recycling history page
- [ ] Test fill level slider interaction
- [ ] Verify points calculation preview

### ⚠️ Error Tests
- [ ] Try invalid QR: `ABC123` (should fail validation)
- [ ] Leave required fields empty (should show error)
- [ ] Submit with high fill level (85%+) - check notification

---

## 📊 Expected Results

### After First Waste Disposal
✅ Success message appears  
✅ Disposal in recent activity  
✅ If fill >= 80%, alert sent  

### After First Recycling (2.5kg Plastic)
✅ Earned 25 points (or 27.5 with bonus)  
✅ Points badge shows 25  
✅ Transaction in recent activity  
✅ History shows 1 transaction  

### After Multiple Actions
✅ Statistics accurate  
✅ All activities logged  
✅ Points accumulate correctly  
✅ History sorted by date  

---

## 🐛 Common Issues

| Issue | Solution |
|-------|----------|
| Modal won't open | Refresh page, check console |
| Points not updating | Refresh page, check transaction status |
| Invalid QR error | Use format: BIN001 or RU001 |
| Form won't submit | Check required fields filled |

---

## 📁 File Locations

### Backend
```
src/main/java/.../
├── model/
│   ├── RecyclingTransaction.java
│   └── WasteDisposal.java
├── service/
│   ├── RecyclingService.java
│   └── WasteDisposalService.java
└── controller/
    └── ResidentController.java
```

### Frontend
```
src/main/resources/templates/resident/
├── dashboard.html (enhanced)
├── recycling-units.html
├── recycle.html
└── my-recycling.html
```

---

## 📚 Documentation Links

| Document | Purpose |
|----------|---------|
| `MANAGE_DAILY_WASTE_IMPLEMENTATION.md` | Full technical docs |
| `MANAGE_DAILY_WASTE_TESTING.md` | Detailed testing guide |
| `IMPLEMENTATION_SUMMARY.md` | High-level overview |
| `QUICK_REFERENCE_MANAGE_WASTE.md` | This file! |

---

## 🎓 Key Concepts

### SOLID Principles Applied
- **S**: Each service has one responsibility
- **O**: Services use interfaces, extensible
- **L**: Implementations are substitutable
- **I**: Small, focused interfaces
- **D**: Depend on abstractions

### Error Handling
- **Retry Logic**: 3 attempts with backoff
- **Graceful Degradation**: Never crash
- **User Feedback**: Clear error messages

### Mock Data
- **5 Recycling Units**: Colombo locations
- **6 Item Types**: With different rates
- **Distance-based**: 0.5 to 3.1 km

---

## 💡 Pro Tips

### Maximize Points
1. Recycle heavier items (Metal, Electronics)
2. Collect 10+ items for bulk bonus
3. Regular recycling builds points quickly

### Best Practices
1. Report accurate fill levels
2. Add notes for unusual situations
3. Check recent activity to verify submissions
4. Monitor points badge for updates

### Testing Efficiency
1. Use auto-fill for quick testing
2. Browser DevTools to inspect network
3. Check database directly for verification
4. Use different browsers for compatibility

---

## 🚦 Status Indicators

### Transaction Statuses
- 🟢 **CONFIRMED** - Successfully processed
- 🟡 **PENDING** - Awaiting confirmation
- 🔴 **FAILED** - Error occurred, retry needed

### Bin Fill Levels
- 🟢 **0-50%** - Empty/Low
- 🟡 **51-79%** - Partial
- 🔴 **80-100%** - Full (triggers alert)

---

## 📞 Support

### Get Help
1. Check documentation files
2. Review browser console for errors
3. Verify database state
4. Check application logs

### Useful Commands
```bash
# Start application
./run-smartbin.bat

# Check logs
tail -f logs/application.log

# Database console
# Access H2 console at http://localhost:8080/h2-console
```

---

## ✨ Quick Win Test (2 minutes)

**Complete User Journey:**

1. **Start** → Open dashboard
2. **Scan** → Click "Scan Bin QR Code"
3. **Enter** → QR: `BIN001`, Fill: `85%`
4. **Submit** → See success message ✅
5. **Recycle** → Click "Find Recycling Units"
6. **Select** → Choose RU001 → "Recycle Here"
7. **Fill** → Plastic, 2kg, 12 items
8. **Preview** → See 22 points estimated
9. **Submit** → See success + points ✅
10. **Verify** → Check points badge = 22 ✅

**Success!** You've tested the complete workflow! 🎉

---

## 🎯 Next Actions

After testing:
1. ✅ Verify all features work
2. 📝 Document any bugs
3. 🎨 Customize UI if needed
4. 🚀 Deploy to production
5. 📊 Monitor user adoption

---

**Happy Testing! The waste management revolution starts now! ♻️🗑️**

---

*Last Updated: October 15, 2025*

