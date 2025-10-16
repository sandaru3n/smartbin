# 🔧 Bulk Request Setup & Troubleshooting Guide

## ✅ **Step 1: Database Tables Setup**

### **Option A: Automatic (Recommended)**
Your `application.properties` has `spring.jpa.hibernate.ddl-auto=update`, so tables will be created automatically when you:

1. **Start the application:**
   ```bash
   .\mvnw.cmd spring-boot:run
   ```

2. **Hibernate will automatically create tables:**
   - `bulk_requests`
   - `bulk_request_photos`

3. **Check console output** for:
   ```
   Hibernate: create table bulk_requests (...)
   Hibernate: create table bulk_request_photos (...)
   ```

### **Option B: Manual (If needed)**
If automatic creation fails, run the SQL script manually:

1. **Open pgAdmin or psql**
2. **Connect to `smartbin_db` database**
3. **Run the script:**
   ```bash
   psql -U postgres -d smartbin_db -f create_bulk_request_tables_postgresql.sql
   ```
   OR copy and paste the contents of `create_bulk_request_tables_postgresql.sql`

---

## 🐛 **Step 2: Debugging Form Submission**

### **Check Browser Console (F12)**

When you click "Submit Request", you should see these console logs:

```
1. Submit button clicked
2. All validations passed, form will submit...
3. Form submit event triggered
4. Form action: http://localhost:8085/resident/bulk-request
5. Form method: post
6. Form data:
   category: FURNITURE
   description: Old sofa
   streetAddress: 123 Main St
   city: Colombo
   zipCode: 10100
   latitude: 6.927079
   longitude: 79.861244
```

### **If Nothing Appears in Console:**
- JavaScript not loading
- Check for errors in console (red text)
- Refresh page (Ctrl + F5)

### **If Validation Alert Shows:**
- Fill in all required fields:
  - ✅ Category
  - ✅ Description
  - ✅ Street Address
  - ✅ City
  - ✅ ZIP Code

---

## 🔍 **Step 3: Check Server Response**

### **Expected Flow:**

1. **Form Submits** → `POST /resident/bulk-request`
2. **Controller Processes** → Creates bulk request
3. **Redirects to** → `/resident/bulk-request-success?requestId=BULK-xxx`
4. **Success Page Shows** → Request details

### **If You See Error Page:**

#### **Check Spring Boot Console:**
Look for stack traces starting with:
```
java.lang.Exception: ...
```

Common errors:

1. **Table doesn't exist:**
   ```
   ERROR: relation "bulk_requests" does not exist
   ```
   **Solution:** Run SQL script manually (Option B above)

2. **Foreign key constraint:**
   ```
   ERROR: insert or update on table "bulk_requests" violates foreign key constraint
   ```
   **Solution:** Make sure you're logged in as a resident user

3. **Category enum mismatch:**
   ```
   ERROR: invalid input value for enum bulk_category
   ```
   **Solution:** Already fixed - using uppercase values (FURNITURE, etc.)

---

## 📋 **Step 4: Test the Complete Flow**

### **Test Checklist:**

1. ✅ **Login as Resident**
   - URL: `http://localhost:8085/resident/login`
   - Use resident credentials

2. ✅ **Navigate to Bulk Request**
   - Dashboard → Click "Bulk Collection Request"
   - OR direct: `http://localhost:8085/resident/bulk-request`

3. ✅ **Fill Form**
   - Select Category: "Furniture"
   - Description: "Old wooden table"
   - Street Address: "123 Main Street"
   - City: "Colombo"
   - ZIP Code: "10100"
   - Map: Click anywhere or use "My Location"

4. ✅ **Open Browser Console (F12)**
   - Go to "Console" tab
   - Keep it open

5. ✅ **Click Submit Request**
   - Watch console for logs
   - Button should show "Processing..."
   - Page should redirect

6. ✅ **Success Page Should Load**
   - Shows Request ID
   - Shows request details
   - Has "View All My Requests" button

7. ✅ **Check Database (Optional)**
   ```sql
   SELECT * FROM bulk_requests ORDER BY created_at DESC LIMIT 1;
   ```

---

## 🚨 **Common Issues & Solutions**

### **Issue 1: Form Does Nothing**
**Symptoms:** Click submit, nothing happens, no console logs

**Solutions:**
1. Check if JavaScript is enabled
2. Refresh page (Ctrl + F5)
3. Check browser console for errors
4. Make sure you're on the correct page

### **Issue 2: Validation Alerts Keep Showing**
**Symptoms:** Alert says "Please enter city" but field is filled

**Solutions:**
1. Check field IDs match:
   - `id="category"`
   - `id="description"`
   - `id="streetAddress"`
   - `id="city"`
   - `id="zipCode"`
2. Clear browser cache
3. Try different browser

### **Issue 3: Whitelabel Error Page**
**Symptoms:** Form submits but shows error page

**Solutions:**
1. **Check console output** for stack trace
2. **Check database connection:**
   ```
   spring.datasource.url=jdbc:postgresql://localhost:5432/smartbin_db
   ```
3. **Verify user is logged in:**
   - Session might be expired
   - Login again
4. **Check if tables exist:**
   ```sql
   \dt bulk_*
   ```

### **Issue 4: 404 Not Found**
**Symptoms:** Page not found error

**Solutions:**
1. Check controller is loaded:
   ```
   Mapped "{[/resident/bulk-request],methods=[GET]}"
   Mapped "{[/resident/bulk-request],methods=[POST]}"
   ```
2. Restart application
3. Clean and rebuild:
   ```bash
   .\mvnw.cmd clean package
   ```

---

## 📊 **Verify Tables Were Created**

### **Using pgAdmin:**
1. Open pgAdmin
2. Navigate: Servers → PostgreSQL → Databases → smartbin_db → Schemas → public → Tables
3. Look for:
   - `bulk_requests` ✅
   - `bulk_request_photos` ✅

### **Using psql:**
```sql
\c smartbin_db
\dt bulk_*
```

Should show:
```
                List of relations
 Schema |         Name          | Type  |  Owner   
--------+-----------------------+-------+----------
 public | bulk_request_photos   | table | postgres
 public | bulk_requests         | table | postgres
```

---

## 🎯 **Quick Test Command**

Run this after starting the application:

```bash
# Check if tables exist
psql -U postgres -d smartbin_db -c "\dt bulk_*"

# Check table structure
psql -U postgres -d smartbin_db -c "\d bulk_requests"
```

---

## ✅ **Success Indicators**

You'll know it's working when:

1. ✅ **Console logs appear** when clicking submit
2. ✅ **Page redirects** to success page
3. ✅ **Success page shows** request details with Request ID
4. ✅ **Database has record:**
   ```sql
   SELECT count(*) FROM bulk_requests;
   -- Should return 1 (or more)
   ```

---

## 📞 **Need More Help?**

1. **Copy console output** (both browser and Spring Boot)
2. **Copy error message** (complete stack trace)
3. **Check database** for table existence
4. **Verify user session** is active

---

**Last Updated:** October 15, 2025  
**Status:** Ready for Testing ✅

