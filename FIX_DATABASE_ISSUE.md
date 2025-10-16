# Fix Database Issue - Quick Guide

## 🔴 Problem
The application crashed because the database schema needs to be updated with new tables for the waste management features.

## ✅ Solution Options

### Option 1: Using pgAdmin (Recommended)

1. **Open pgAdmin**
2. **Connect to your database**: `smartbin_db`
3. **Open Query Tool** (Tools → Query Tool)
4. **Copy and paste** the contents of `update_database_for_waste_management.sql`
5. **Execute** the script (press F5 or click Execute button)
6. **Verify** - You should see "Schema update completed successfully!"

### Option 2: Using Command Line (If psql is in PATH)

```bash
# Navigate to project directory
cd D:\ITPproject\smartbin

# Run the SQL script
psql -U postgres -d smartbin_db -f update_database_for_waste_management.sql

# Enter password when prompted: sandaru22
```

### Option 3: Let Hibernate Auto-Create (Temporary Fix)

**Temporarily change application.properties:**

1. Open `src/main/resources/application.properties`
2. Change line 11 from:
   ```
   spring.jpa.hibernate.ddl-auto=update
   ```
   To:
   ```
   spring.jpa.hibernate.ddl-auto=create-drop
   ```
3. Restart application (it will recreate all tables)
4. **IMPORTANT**: Change back to `update` after first start
5. Run `sample_data.sql` again to restore data

---

## 📝 What the SQL Script Does

The script adds:

1. ✅ **recycling_points** column to users table (DOUBLE PRECISION, default 0.0)
2. ✅ **recycling_transactions** table (tracks recycling activities)
3. ✅ **waste_disposals** table (tracks waste disposal forms)
4. ✅ **Indexes** for better performance
5. ✅ **Safety checks** (IF NOT EXISTS clauses)

---

## 🚀 After Running SQL Script

### Restart the Application:

**Option A: Using Maven**
```bash
.\mvnw.cmd spring-boot:run
```

**Option B: Using Batch File**
```bash
.\run-smartbin.bat
```

**Option C: Using PowerShell Script**
```bash
.\run-smartbin.ps1
```

---

## 🔍 Verify It's Working

1. **Check console** - Should see:
   ```
   Tomcat started on port(s): 8085
   Started SmartbinApplication
   ```

2. **Test the port**:
   ```powershell
   netstat -ano | findstr :8085
   ```
   Should show LISTENING status

3. **Open browser**:
   ```
   http://localhost:8085/resident/login
   ```

4. **Login and test**:
   - Scan Bin QR Code
   - Find Recycling Units (with map!)
   - Search Nearby Bins (with map!)

---

## 🐛 Still Having Issues?

### Issue 1: Port Already in Use
```powershell
# Find process using port 8085
netstat -ano | findstr :8085

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

### Issue 2: PostgreSQL Not Running
1. Open Services (Win + R, type `services.msc`)
2. Find **postgresql-x64-XX** service
3. Right-click → Start

### Issue 3: Database Connection Failed
Check `application.properties`:
- Database name: `smartbin_db`
- Username: `postgres`
- Password: `sandaru22`
- Port: `5432`

### Issue 4: Tables Still Not Created
Run this in pgAdmin:
```sql
-- Check if tables exist
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name IN ('recycling_transactions', 'waste_disposals');

-- Check if column exists
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'users' 
AND column_name = 'recycling_points';
```

---

## 📊 Database Schema Reference

### New Tables Created:

#### recycling_transactions
```sql
- id (BIGSERIAL) - Primary Key
- user_id (BIGINT) - Foreign Key → users(id)
- recycling_unit_qr_code (VARCHAR 255)
- item_type (VARCHAR 50)
- weight (DOUBLE PRECISION)
- quantity (INTEGER)
- points_earned (DOUBLE PRECISION)
- price_value (DOUBLE PRECISION)
- status (VARCHAR 20)
- location (VARCHAR 255)
- created_at (TIMESTAMP)
```

#### waste_disposals
```sql
- id (BIGSERIAL) - Primary Key
- user_id (BIGINT) - Foreign Key → users(id)
- bin_id (BIGINT) - Foreign Key → bins(id)
- reported_fill_level (INTEGER)
- status (VARCHAR 20)
- notes (TEXT)
- created_at (TIMESTAMP)
```

#### users (modified)
```sql
+ recycling_points (DOUBLE PRECISION) - New column
```

---

## ✅ Quick Checklist

- [ ] SQL script created: `update_database_for_waste_management.sql`
- [ ] Opened pgAdmin
- [ ] Connected to `smartbin_db`
- [ ] Ran SQL script successfully
- [ ] Verified "Schema update completed successfully!" message
- [ ] Restarted application
- [ ] Application started on port 8085
- [ ] Tested login at http://localhost:8085/resident/login
- [ ] Tested new features (maps, recycling, disposal)

---

## 🎯 Next Steps After Fix

1. **Login** as resident
2. **Test Waste Disposal**:
   - Click "Scan Bin QR Code"
   - Enter BIN001, set fill level 85%
   - Submit

3. **Test Recycling**:
   - Click "Find Recycling Units"
   - See the interactive map!
   - Select a unit, submit recycling
   - Earn points!

4. **Test Bin Search**:
   - Click "Search Nearby Bins"
   - See color-coded markers!
   - Search with different radius

---

## 📞 Need Help?

If you're still having issues:

1. **Check application logs** in console
2. **Check PostgreSQL logs** in pgAdmin
3. **Verify database connection** in application.properties
4. **Ensure PostgreSQL service** is running
5. **Check port 8085** is not blocked by firewall

---

**The database schema update is safe and won't delete any existing data!** ✅

All new tables and columns have `IF NOT EXISTS` checks to prevent errors.

