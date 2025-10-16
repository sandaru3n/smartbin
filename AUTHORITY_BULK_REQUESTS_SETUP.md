# Authority Bulk Requests - Complete Setup Guide

This guide will help you see bulk requests on the `/authority/bulk-requests` page.

## Prerequisites

The server is running on **http://localhost:8086**

---

## Step 1: Create Database Tables

You need to create the `bulk_requests` table in your PostgreSQL database.

### Option A: Using pgAdmin (Recommended)

1. Open **pgAdmin**
2. Connect to your PostgreSQL server
3. Navigate to: **Servers → PostgreSQL → Databases → smartbin_db**
4. Right-click on **smartbin_db** and select **Query Tool**
5. Copy and paste the entire SQL script from `create_bulk_tables_NOW.sql` (see below)
6. Click **Execute** (F5) or the ▶ button
7. You should see: `Tables created successfully!`

### SQL Script to Run:

```sql
-- COPY AND PASTE THIS INTO pgAdmin Query Tool
-- Database: smartbin_db

-- Create bulk_requests table
CREATE TABLE IF NOT EXISTS bulk_requests (
    id BIGSERIAL PRIMARY KEY,
    request_id VARCHAR(255) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    street_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    estimated_weight DOUBLE PRECISION,
    estimated_dimensions VARCHAR(255),
    base_price DOUBLE PRECISION NOT NULL,
    processing_fee DOUBLE PRECISION NOT NULL,
    tax_amount DOUBLE PRECISION NOT NULL,
    total_amount DOUBLE PRECISION NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    payment_reference VARCHAR(255),
    collector_assigned BIGINT,
    scheduled_date TIMESTAMP,
    completed_date TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_bulk_requests_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create bulk_request_photos table
CREATE TABLE IF NOT EXISTS bulk_request_photos (
    bulk_request_id BIGINT NOT NULL,
    photo_url VARCHAR(500),
    CONSTRAINT fk_bulk_request_photos FOREIGN KEY (bulk_request_id) REFERENCES bulk_requests(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_bulk_requests_user_id ON bulk_requests(user_id);
CREATE INDEX IF NOT EXISTS idx_bulk_requests_status ON bulk_requests(status);
CREATE INDEX IF NOT EXISTS idx_bulk_requests_payment_status ON bulk_requests(payment_status);

-- Verify tables were created
SELECT 'Tables created successfully!' as message;
SELECT table_name FROM information_schema.tables WHERE table_name IN ('bulk_requests', 'bulk_request_photos');
```

---

## Step 2: Login as Authority User

1. Go to: **http://localhost:8086/authority/login**
2. Use these credentials:
   - **Email**: `admin.authority@smartbin.com`
   - **Password**: `password123`
3. Click **Sign In**

---

## Step 3: Access Bulk Requests Page

Once logged in, navigate to: **http://localhost:8086/authority/bulk-requests**

### What You'll See:

- **If NO bulk requests exist**: "No bulk collection requests found"
- **If bulk requests exist**: A list of all bulk requests with options to:
  - Filter by status
  - Assign collectors
  - Schedule pickups
  - Mark as completed

---

## Step 4: Create Test Bulk Requests

To see bulk requests on the authority page, you need residents to submit them:

### Create a Bulk Request as Resident:

1. **Logout** from authority account (or open a new incognito/private window)
2. Go to: **http://localhost:8086/resident/login**
3. Login with:
   - **Email**: `john.resident@smartbin.com`
   - **Password**: `password123`
4. After login, go to: **http://localhost:8086/resident/bulk-request**
5. Fill out the form:
   - **Category**: Select any (Furniture, Electronics, etc.)
   - **Description**: Enter a description
   - **Address**: Fill in street address, city, zip code
   - **Optional**: Click on map to set location
6. Click **Submit Request**
7. You'll be redirected to the **payment page**
8. Select a payment method
9. Click **Pay Now**
10. Payment will be processed

### Verify:

1. **As Resident**: Go to http://localhost:8086/resident/my-bulk-requests
   - You should see your submitted request
2. **As Authority**: Login and go to http://localhost:8086/authority/bulk-requests
   - You should now see the bulk request!

---

## Step 5: Test Authority Features

Once you have bulk requests visible, you can test:

### Filter Requests:
- Use the dropdown to filter by status:
  - Pending Payment
  - Payment Completed
  - Collector Assigned
  - Scheduled
  - In Progress
  - Completed

### Assign Collector:
1. Find a request with status "Payment Completed"
2. Click **Assign Collector**
3. Select a collector from the dropdown
4. Click **Assign**

### Schedule Pickup:
1. Find a request with a collector assigned
2. Click **Schedule Pickup**
3. Select date and time (must be at least 24 hours from now)
4. Click **Schedule**

### Mark Completed:
1. Find a request with status "In Progress"
2. Click **Mark Completed**
3. Add optional completion notes
4. Click **Mark Completed**

---

## Quick Test Credentials

### Authority:
- Email: `admin.authority@smartbin.com`
- Password: `password123`

### Resident (to create test requests):
- Email: `john.resident@smartbin.com`
- Password: `password123`

### Other Residents:
- Email: `sarah.resident@smartbin.com`, Password: `password123`
- Email: `michael.resident@smartbin.com`, Password: `password123`

---

## Troubleshooting

### Issue: "No bulk collection requests found"

**Causes**:
1. Database tables don't exist → Run the SQL script above
2. No bulk requests have been submitted → Create a test request as a resident
3. Not logged in as authority user → Login first

### Issue: Page shows error or redirects to login

**Solution**: Make sure you're logged in as an authority user first.

### Issue: Cannot submit bulk request as resident

**Solutions**:
1. Clear browser cache (Ctrl+Shift+Delete)
2. Make sure server is running
3. Check browser console for errors (F12)
4. Ensure `bulk_requests` table exists

---

## Workflow Summary

```
1. RESIDENT submits bulk request
   ↓
2. RESIDENT pays for the request
   ↓
3. AUTHORITY sees request on /authority/bulk-requests
   ↓
4. AUTHORITY assigns collector
   ↓
5. AUTHORITY schedules pickup
   ↓
6. COLLECTOR collects the waste
   ↓
7. AUTHORITY marks as completed
```

---

## Current Status

✅ Server is running on port 8086
✅ `/authority/bulk-requests` page exists
✅ All endpoints are implemented
⚠️ Database tables need to be created (run SQL script)
⚠️ Need to create test bulk requests

---

## Next Steps

1. ✅ Run the SQL script in pgAdmin to create tables
2. ✅ Login as resident and create a test bulk request
3. ✅ Complete payment for the request
4. ✅ Login as authority and view the request on `/authority/bulk-requests`
5. ✅ Test assigning collector, scheduling, and completing

---

**Last Updated**: October 16, 2025

