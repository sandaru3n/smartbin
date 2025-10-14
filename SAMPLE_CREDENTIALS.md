# SmartBin - Sample Login Credentials

This file contains sample user credentials that are automatically created when you first run the application.

## Quick Access Links

- **Home Page**: http://localhost:8081
- **Resident Login**: http://localhost:8081/resident/login
- **Collector Login**: http://localhost:8081/collector/login
- **Authority Login**: http://localhost:8081/authority/login

---

## 🏠 Resident Accounts

| Name | Email | Password | Phone | Address |
|------|-------|----------|-------|---------|
| John Doe | john.resident@smartbin.com | password123 | +94 771234567 | 123 Main Street, Colombo 05 |
| Sarah Williams | sarah.resident@smartbin.com | password123 | +94 772345678 | 456 Lake Road, Kandy |
| Michael Brown | michael.resident@smartbin.com | password123 | +94 773456789 | 789 Park Avenue, Galle |

**Login at**: http://localhost:8081/resident/login

---

## 🚛 Waste Collector Accounts

| Name | Email | Password | Phone | Address |
|------|-------|----------|-------|---------|
| David Collector | david.collector@smartbin.com | password123 | +94 774567890 | 321 Collector Lane, Colombo 03 |
| Emma Wilson | emma.collector@smartbin.com | password123 | +94 775678901 | 654 Service Road, Negombo |
| James Taylor | james.collector@smartbin.com | password123 | +94 776789012 | 987 Industrial Area, Kelaniya |

**Login at**: http://localhost:8081/collector/login

---

## 🏛️ Authority Accounts

| Name | Email | Password | Phone | Address |
|------|-------|----------|-------|---------|
| Admin Authority | admin.authority@smartbin.com | password123 | +94 777890123 | Municipal Building, Colombo 07 |
| Lisa Manager | lisa.authority@smartbin.com | password123 | +94 778901234 | City Council Office, Kandy |
| Robert Supervisor | robert.authority@smartbin.com | password123 | +94 779012345 | District Office, Galle |

**Login at**: http://localhost:8081/authority/login

---

## Testing Instructions

### Quick Test Flow

1. **Start the application**: `mvn spring-boot:run`

2. **Test Resident Login**:
   - Go to http://localhost:8081/resident/login
   - Email: `john.resident@smartbin.com`
   - Password: `password123`
   - Click "Sign In"

3. **Test Collector Login**:
   - Go to http://localhost:8081/collector/login
   - Email: `david.collector@smartbin.com`
   - Password: `password123`
   - Click "Sign In"

4. **Test Authority Login**:
   - Go to http://localhost:8081/authority/login
   - Email: `admin.authority@smartbin.com`
   - Password: `password123`
   - Click "Sign In"

---

## Important Notes

⚠️ **Security Notice**: These are sample credentials for development/testing purposes only. 

- The password `password123` is encrypted using BCrypt before storage
- Sample data is automatically inserted only if the database is empty
- These credentials should NOT be used in production

---

## Resetting Sample Data

If you need to reset the database and reload sample data:

```sql
-- In PostgreSQL
DROP DATABASE smartbin_db;
CREATE DATABASE smartbin_db;
```

Then restart the application, and sample data will be automatically reinserted.

---

**Application Version**: 0.0.1-SNAPSHOT  
**Last Updated**: October 2025

