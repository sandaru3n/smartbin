# 🚀 SmartBin Quick Start Guide

## ⚠️ Java Setup Issue Fix

If you see the error: **"JAVA_HOME environment variable is not defined correctly"**

### **Solution 1: Use the Batch Script (Easiest)**

Simply double-click or run:
```bash
run-smartbin.bat
```

This script will:
- ✅ Set JAVA_HOME automatically
- ✅ Start the application
- ✅ Show you the dashboard URL

---

### **Solution 2: Set JAVA_HOME Manually**

#### **Step 1: Find Your Java Installation**

Open PowerShell and run:
```powershell
where java
```

Or check these common locations:
- `C:\Program Files\Java\jdk-24`
- `C:\Program Files\Java\jdk-21`
- `C:\Program Files\Java\jdk-17`
- `C:\Program Files\OpenJDK\jdk-21`

#### **Step 2: Set JAVA_HOME (Temporary)**

In PowerShell:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
.\mvnw.cmd clean spring-boot:run
```

#### **Step 3: Set JAVA_HOME (Permanent)**

1. Press `Windows + X` → Select "System"
2. Click "Advanced system settings"
3. Click "Environment Variables"
4. Under "System Variables", click "New"
5. Variable name: `JAVA_HOME`
6. Variable value: `C:\Program Files\Java\jdk-24` (your Java path)
7. Click "OK"
8. Restart your terminal/IDE

---

### **Solution 3: Use IDE (Recommended for Development)**

#### **IntelliJ IDEA:**
1. Open the project in IntelliJ
2. Wait for Maven to import dependencies
3. Find `SmartbinApplication.java`
4. Right-click → "Run SmartbinApplication"

#### **Eclipse:**
1. Import as Maven project
2. Right-click project → "Run As" → "Spring Boot App"

#### **VS Code:**
1. Install "Spring Boot Extension Pack"
2. Open Command Palette (Ctrl+Shift+P)
3. Type "Spring Boot Dashboard"
4. Click the play button next to SmartbinApplication

---

## 🎯 After Application Starts

### **1. Wait for Startup**
Look for this message in the console:
```
Started SmartbinApplication in X seconds
```

### **2. Check Data Initialization**
You should see:
```
✓ Created AUTHORITY user: Waste Manager (waste@gmail.com)
✓ Created 32 sample bins with varied statuses
✓ Created 32 sample collections
✓ Created 10 sample routes
```

### **3. Access Dashboard**
Open your browser and go to:
```
http://localhost:8084/authority/dashboard
```

### **4. Login**
Use any of these credentials:
```
Email: waste@gmail.com
Password: password123
```

Or:
```
Email: admin.authority@smartbin.com
Password: password123
```

---

## 📊 What You Should See

### **Dashboard Metrics:**
- **Total Bins:** 32
- **Average Fill %:** ~67%
- **Overdue Bins:** 3
- **Last Collection:** 2h ago

### **Map:**
- 32 bin markers across Sri Lanka
- Color-coded by status (blue/orange/red)
- Interactive popups with bin details

### **Auto-Refresh:**
- Updates every 30 seconds
- "Live Updates Active" indicator pulsing
- Real-time timestamp

---

## 🐛 Troubleshooting

### **Port Already in Use (8084)**

If you see: `Port 8084 is already in use`

**Option 1: Kill the process**
```powershell
netstat -ano | findstr :8084
taskkill /PID <PID_NUMBER> /F
```

**Option 2: Change the port**
Edit `src/main/resources/application.properties`:
```properties
server.port=8085
```

Then access: `http://localhost:8085/authority/dashboard`

---

### **Database Connection Error**

If you see: `Could not connect to database`

1. **Check MySQL is running:**
   ```bash
   # Start MySQL service
   net start MySQL80
   ```

2. **Verify credentials in `application.properties`:**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/smartbin_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **Create database if it doesn't exist:**
   ```sql
   CREATE DATABASE IF NOT EXISTS smartbin_db;
   ```

---

### **No Mock Data Showing**

If bins don't appear:

1. **Check console for initialization logs**
   - Should see "Created 32 sample bins"
   - Should see "Created 32 sample collections"

2. **Reset database:**
   ```bash
   # Run the reset script
   mysql -u root -p smartbin_db < reset_database.sql
   
   # Restart application
   .\run-smartbin.bat
   ```

3. **Check DataInitializer:**
   - Look for: "Database already contains data. Skipping initialization."
   - If you see this, the database has old data
   - Use reset script above

---

### **Maven Build Errors**

If Maven fails to build:

1. **Clean and rebuild:**
   ```bash
   .\mvnw.cmd clean install
   ```

2. **Update dependencies:**
   ```bash
   .\mvnw.cmd dependency:resolve
   ```

3. **Check Java version:**
   ```bash
   java -version
   # Should be Java 17 or higher
   ```

---

## 📁 Important Files

- **`run-smartbin.bat`** - Easy startup script
- **`reset_database.sql`** - Reset and reload data
- **`MOCK_DATA_SUMMARY.md`** - Complete data overview
- **`DASHBOARD_UPDATES.md`** - Feature documentation
- **`application.properties`** - Configuration settings

---

## 🎓 Quick Commands Reference

### **Start Application:**
```bash
# Option 1: Batch script (easiest)
.\run-smartbin.bat

# Option 2: Maven wrapper
.\mvnw.cmd spring-boot:run

# Option 3: With clean
.\mvnw.cmd clean spring-boot:run
```

### **Stop Application:**
- Press `Ctrl + C` in the terminal

### **Reset Database:**
```bash
mysql -u root -p smartbin_db < reset_database.sql
```

### **Check Application Status:**
```bash
# Check if running on port 8084
netstat -ano | findstr :8084
```

---

## ✅ Success Checklist

After starting the application, verify:

- [ ] Console shows "Started SmartbinApplication"
- [ ] Console shows "Created 32 sample bins"
- [ ] Can access http://localhost:8084
- [ ] Can login with waste@gmail.com
- [ ] Dashboard shows 32 bins
- [ ] Map displays bin markers
- [ ] Metrics show real data
- [ ] "Live Updates Active" indicator is pulsing

---

## 🆘 Still Having Issues?

1. **Check the console output** for error messages
2. **Verify Java version** is 17 or higher
3. **Ensure MySQL is running** and accessible
4. **Check port 8084** is not in use
5. **Review application.properties** settings

---

## 📞 Common Error Messages

| Error | Solution |
|-------|----------|
| "JAVA_HOME not defined" | Use `run-smartbin.bat` or set JAVA_HOME |
| "Port 8084 already in use" | Kill process or change port |
| "Database connection failed" | Start MySQL and check credentials |
| "No data showing" | Run reset_database.sql |
| "Maven build failed" | Run `mvnw.cmd clean install` |

---

**Happy Testing! 🎉**

For detailed data information, see `MOCK_DATA_SUMMARY.md`

