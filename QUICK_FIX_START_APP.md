# ⚡ Quick Fix - Start Application Now

## 🎯 Fastest Solution (2 minutes)

Follow these exact steps to get the application running immediately:

### Step 1: Stop Any Running Process
```powershell
# Find and kill any process on port 8085
netstat -ano | findstr :8085
# If you see a process, note the PID (last number) and run:
taskkill /PID <NUMBER> /F
```

### Step 2: Run the SQL Script in pgAdmin

1. **Open pgAdmin** (PostgreSQL administration tool)

2. **Connect to database**:
   - Server: localhost
   - Database: `smartbin_db`
   - Username: `postgres`
   - Password: `sandaru22`

3. **Open Query Tool**: 
   - Right-click on `smartbin_db`
   - Select "Query Tool"

4. **Copy this SQL** and paste into Query Tool:

```sql
-- Add recycling_points column if not exists
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'recycling_points'
    ) THEN
        ALTER TABLE users ADD COLUMN recycling_points DOUBLE PRECISION DEFAULT 0.0;
    END IF;
END $$;

-- Create recycling_transactions table
CREATE TABLE IF NOT EXISTS recycling_transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recycling_unit_qr_code VARCHAR(255) NOT NULL,
    item_type VARCHAR(50) NOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    quantity INTEGER,
    points_earned DOUBLE PRECISION NOT NULL,
    price_value DOUBLE PRECISION NOT NULL,
    status VARCHAR(20) NOT NULL,
    location VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create waste_disposals table
CREATE TABLE IF NOT EXISTS waste_disposals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    bin_id BIGINT NOT NULL,
    reported_fill_level INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (bin_id) REFERENCES bins(id) ON DELETE CASCADE
);

-- Update existing users
UPDATE users SET recycling_points = 0.0 WHERE recycling_points IS NULL;

SELECT 'Database updated successfully!' as message;
```

5. **Click Execute** (F5 or play button)

6. **Verify** - Should see: "Database updated successfully!"

### Step 3: Start the Application
```powershell
# In your project directory
cd D:\ITPproject\smartbin

# Start the application
.\mvnw.cmd spring-boot:run
```

### Step 4: Wait for Startup
Watch the console. When you see:
```
Tomcat started on port(s): 8085
Started SmartbinApplication in X.XXX seconds
```

### Step 5: Test It!
Open browser:
```
http://localhost:8085/resident/login
```

---

## ✅ Success Indicators

You'll know it's working when:
- ✅ No errors in console
- ✅ Application says "Started SmartbinApplication"
- ✅ Login page loads at http://localhost:8085
- ✅ Can login as resident
- ✅ See new features (maps, recycling, disposal)

---

## 🔥 Alternative: Use Batch File

If the SQL approach doesn't work, use this **temporary fix**:

### Edit application.properties
Open `src/main/resources/application.properties` and change line 11:

**From:**
```properties
spring.jpa.hibernate.ddl-auto=update
```

**To:**
```properties
spring.jpa.hibernate.ddl-auto=create
```

**Then:**
1. Start application with `.\mvnw.cmd spring-boot:run`
2. Wait for it to start
3. **Stop** the application (Ctrl+C)
4. Change `create` back to `update`
5. Run `sample_data.sql` in pgAdmin to restore data
6. Start application again

---

## 📞 Still Not Working?

### Check PostgreSQL Service
```powershell
# Open Services
services.msc

# Find "postgresql-x64-XX" service
# Right-click → Start (if stopped)
```

### Verify Database Exists
In pgAdmin, check that `smartbin_db` database exists. If not:
```sql
CREATE DATABASE smartbin_db;
```

### Check Connection
Test connection string in application.properties:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/smartbin_db
spring.datasource.username=postgres
spring.datasource.password=sandaru22
```

---

## 🎉 Once It's Running

Test the new features:

1. **Interactive Maps** 🗺️
   - Find Recycling Units → See map with green markers
   - Search Nearby Bins → See map with color-coded markers

2. **Waste Disposal** 🗑️
   - Scan Bin QR Code → Submit fill level

3. **Recycling** ♻️
   - Find recycling units → Submit recyclables → Earn points

4. **View History** 📊
   - My Recycling History → See all transactions

---

**That's it! The application should now be running on port 8085!** 🚀

