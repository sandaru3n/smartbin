# 🎯 START HERE - SmartBin Application

## 🚀 Quick Start (3 Easy Steps)

### **Step 1: Run the Application**

Choose **ONE** of these methods:

#### **Method A: Batch Script (Easiest - Windows)**
```bash
# Just double-click this file:
run-smartbin.bat
```

#### **Method B: PowerShell Script**
```powershell
# Right-click and "Run with PowerShell":
.\run-smartbin.ps1
```

#### **Method C: Manual Command**
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
.\mvnw.cmd clean spring-boot:run
```

---

### **Step 2: Wait for Startup**

Watch the console. You should see:
```
✓ Created AUTHORITY user: Waste Manager (waste@gmail.com)
✓ Created 32 sample bins with varied statuses
✓ Created 32 sample collections  
✓ Created 10 sample routes (7 completed, 2 in-progress, 1 assigned)

Started SmartbinApplication in X seconds (JVM running for Y)
```

**This means your mock data is loaded! ✅**

---

### **Step 3: Login First**

1. Open your browser
2. Go to: **http://localhost:8084/authority/login** (LOGIN PAGE FIRST!)
3. Login with:
   - **Email:** `waste@gmail.com`
   - **Password:** `password123`
4. You'll be redirected to: **http://localhost:8084/authority/dashboard**

---

## 🎉 What You'll See

### **Dashboard Features:**

✅ **32 Bins** on interactive map
✅ **Real-time metrics** updating every 30 seconds
✅ **3 Overdue bins** with alert banner
✅ **Live updates** indicator pulsing
✅ **Color-coded markers** (blue/orange/red)

### **Expected Metrics:**
```
Total Bins: 32
Average Fill %: 67%
Overdue Bins: 3
Last Collection: 2h ago
```

---

## ⚠️ Troubleshooting

### **Problem: JAVA_HOME Error**

**Solution:** Use the batch script `run-smartbin.bat` - it sets JAVA_HOME automatically!

---

### **Problem: No Mock Data Showing**

**Check 1:** Look at console during startup. Do you see:
```
✓ Created 32 sample bins
```

**If YES:** Data is loaded! Refresh your browser.

**If NO - You see:** "Database already contains data. Skipping initialization."

**Solution:** Reset the database:
```bash
# In MySQL:
mysql -u root -p smartbin_db < reset_database.sql

# Then restart application:
.\run-smartbin.bat
```

---

### **Problem: Port 8084 Already in Use**

**Solution 1:** Kill the existing process:
```powershell
netstat -ano | findstr :8084
taskkill /PID <NUMBER> /F
```

**Solution 2:** Change port in `application.properties`:
```properties
server.port=8085
```

---

### **Problem: Database Connection Failed**

**Solution:**
1. Start MySQL service:
   ```bash
   net start MySQL80
   ```

2. Check credentials in `src/main/resources/application.properties`

3. Create database:
   ```sql
   CREATE DATABASE smartbin_db;
   ```

---

## 📊 Mock Data Summary

Your dashboard now includes:

### **👥 Users (10)**
- 4 Authorities (including waste@gmail.com)
- 3 Collectors
- 3 Residents

### **🗑️ Bins (32)**
- 12 FULL bins (37%)
- 10 PARTIAL bins (31%)
- 7 EMPTY bins (22%)
- 3 OVERDUE bins (10%)

### **📋 Collections (32)**
- 28 Completed
- 4 Assigned/Pending
- Spanning 7 days

### **🗺️ Routes (10)**
- 7 Completed routes
- 2 In-progress routes
- 1 Assigned route

---

## 🔄 Real-Time Features

The dashboard updates **every 30 seconds** automatically:
- ✅ Bin metrics
- ✅ Map markers
- ✅ Collection times
- ✅ Status indicators

---

## 📁 Important Files

| File | Purpose |
|------|---------|
| `run-smartbin.bat` | **START HERE** - Easy launcher |
| `run-smartbin.ps1` | PowerShell launcher |
| `QUICK_START.md` | Detailed troubleshooting |
| `MOCK_DATA_SUMMARY.md` | Complete data overview |
| `reset_database.sql` | Reset and reload data |

---

## 🎓 All Login Credentials

### **Authority Users:**
```
waste@gmail.com / password123
admin.authority@smartbin.com / password123
lisa.authority@smartbin.com / password123
robert.authority@smartbin.com / password123
```

### **Collector Users:**
```
david.collector@smartbin.com / password123
emma.collector@smartbin.com / password123
james.collector@smartbin.com / password123
```

### **Resident Users:**
```
john.resident@smartbin.com / password123
sarah.resident@smartbin.com / password123
michael.resident@smartbin.com / password123
```

---

## ✅ Success Checklist

After starting, verify:

- [ ] Console shows "Started SmartbinApplication"
- [ ] Console shows "Created 32 sample bins"
- [ ] Can access http://localhost:8084
- [ ] Can login with waste@gmail.com
- [ ] Dashboard shows 32 bins on map
- [ ] Metrics display real numbers
- [ ] "Live Updates Active" is pulsing
- [ ] Alert banner shows "3 bins are overdue"

---

## 🆘 Still Not Working?

1. **Check Java is installed:**
   ```bash
   java -version
   # Should show Java 17 or higher
   ```

2. **Check MySQL is running:**
   ```bash
   net start MySQL80
   ```

3. **Try clean build:**
   ```bash
   .\mvnw.cmd clean install
   .\mvnw.cmd spring-boot:run
   ```

4. **Check console for errors** and see `QUICK_START.md` for solutions

---

## 📞 Need Help?

- **Detailed Guide:** See `QUICK_START.md`
- **Data Info:** See `MOCK_DATA_SUMMARY.md`
- **Features:** See `DASHBOARD_UPDATES.md`
- **Console Logs:** Check terminal output for errors

---

## 🎯 Quick Test

Once logged in, try these:

1. **View Map** - See 32 bin markers
2. **Click Dispatch** - Select bins and assign collector
3. **Generate Report** - Create collection report
4. **Manage Collectors** - Assign to regions
5. **Watch Updates** - Wait 30 seconds, see metrics refresh

---

**That's it! Your SmartBin dashboard is ready! 🎊**

**Dashboard URL:** http://localhost:8084/authority/dashboard
**Login:** waste@gmail.com / password123

Enjoy exploring your waste management system! 🚀

