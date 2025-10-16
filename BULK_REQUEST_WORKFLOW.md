# 🗑️ Bulk Collection Request - Complete Workflow

## 📋 **Overview**
The Bulk Collection Request system allows residents to request special collection for large waste items that cannot be disposed of in regular bins.

---

## 🔄 **Complete Workflow**

### **1. Create New Request**
**Page:** `/resident/bulk-request`

#### **What User Does:**
1. **Navigate** to bulk request page from dashboard
2. **Select Category** (Furniture, Appliances, Electronics, etc.)
3. **Enter Description** of items
4. **Provide Pickup Address** (Street, City, ZIP)
5. **Set Pickup Location** on interactive map
   - Click on map to set location
   - Use "My Location" button for GPS
   - Drag the pink marker to exact position
6. **Upload Photos** (Optional - up to 3 photos)
7. **Review Fee Estimate** (auto-calculated)
8. **Submit Request**

#### **Fee Structure:**
- **Base Price**: LKR 1,500 - 5,000 (based on category)
- **Processing Fee**: LKR 500
- **Tax (5% GST)**: Calculated on base + processing
- **Example Total**: LKR 2,100 - 5,775

---

### **2. Request Submitted**
**Page:** `/resident/bulk-request-success`

#### **What Happens:**
- ✅ Request saved to database
- ✅ Unique Request ID generated (e.g., BULK-1234567890-123)
- ✅ Status set to **PENDING**
- ✅ Confirmation page displayed

#### **User Sees:**
- Request ID
- Category
- Pickup Address
- Total Amount
- Current Status
- Next Steps Information

#### **Actions Available:**
- **Back to Dashboard** - Return to main dashboard
- **View All My Requests** - Go to bulk requests list

---

### **3. View All Requests**
**Page:** `/resident/my-bulk-requests`

#### **Features:**

##### **📊 Statistics Dashboard**
- Total Requests
- Pending Requests
- Completed Requests
- Total Spent (LKR)

##### **🔍 Filters**
- Filter by Status (Pending, Scheduled, Completed, etc.)
- Filter by Category (Furniture, Appliances, etc.)

##### **📝 Request Cards**
Each request card shows:
- Request ID
- Request Date
- Category
- Pickup Address
- Total Amount
- Current Status
- Scheduled Date (if available)

##### **⚡ Actions Per Request**
- **View Details** - See full request information
- **Pay Now** - Process payment (if pending)
- **Cancel** - Cancel request (if eligible)

---

## 🎯 **Request Status Flow**

### **Status Progression:**

```
1. PENDING
   ↓ (Authority reviews and approves)
   
2. APPROVED
   ↓ (Payment instruction sent)
   
3. PAYMENT_PENDING
   ↓ (Resident makes payment)
   
4. PAYMENT_COMPLETED
   ↓ (Authority assigns collector)
   
5. COLLECTOR_ASSIGNED
   ↓ (Collection scheduled)
   
6. SCHEDULED
   ↓ (Collector starts collection)
   
7. IN_PROGRESS
   ↓ (Collection completed)
   
8. COMPLETED ✅
```

**Alternative Paths:**
- **CANCELLED** - Request cancelled by resident
- **REJECTED** - Request rejected by authority

---

## 💳 **Payment Process**

### **When Payment is Required:**
- Status: `PAYMENT_PENDING`
- Payment methods available:
  - Credit/Debit Card
  - Digital Wallet
  - UPI
  - Net Banking

### **Payment Workflow:**
1. User clicks **"Pay Now"**
2. Redirected to payment page
3. Selects payment method
4. Reviews payment summary
5. Completes payment
6. Status updated to `PAYMENT_COMPLETED`

---

## 📱 **User Notifications**

### **SMS Notifications:**
1. **Request Submitted** - Confirmation with Request ID
2. **Request Approved** - Payment instructions
3. **Payment Confirmed** - Collector assignment pending
4. **Collector Assigned** - Collector details & schedule
5. **Collection Scheduled** - Date and time confirmation
6. **Collection Completed** - Thank you message

### **Email Notifications:**
- Same as SMS but with more details
- Includes invoice/receipt

---

## 👤 **User Actions & Features**

### **From Dashboard:**
- ✅ Create new bulk request
- ✅ View bulk request summary
- ✅ Quick access to bulk requests list

### **From My Bulk Requests:**
- ✅ View all requests
- ✅ Filter by status/category
- ✅ See statistics
- ✅ Track request progress
- ✅ Make payments
- ✅ Cancel pending requests
- ✅ View detailed information

### **From Success Page:**
- ✅ See request confirmation
- ✅ View request details
- ✅ Navigate to all requests
- ✅ Return to dashboard

---

## 🎨 **Key Features**

### **Interactive Map**
- Real-time location selection
- Drag-and-drop marker
- GPS location detection
- Zoom controls
- Street view (OpenStreetMap)

### **Dynamic Fee Calculator**
- Real-time calculation
- Category-based pricing
- Transparent breakdown
- Currency formatting (LKR)

### **Photo Upload**
- Camera capture
- Gallery selection
- Multiple photos (up to 3)
- Preview before upload

### **Smart Validation**
- Required field checking
- Address validation
- Location validation
- Form submission protection

---

## 🔒 **Security & Data**

### **Data Stored:**
- Request ID
- User information
- Item details
- Pickup location (coordinates)
- Photos (URLs)
- Fee breakdown
- Payment status
- Timestamps

### **Security Features:**
- Session-based authentication
- CSRF protection
- SQL injection prevention (JPA)
- XSS prevention
- Secure payment processing

---

## 📊 **Database Schema**

### **bulk_requests Table:**
```sql
- id (Primary Key)
- request_id (Unique)
- user_id (Foreign Key → users)
- category (ENUM)
- description (TEXT)
- street_address, city, zip_code
- latitude, longitude
- base_price, processing_fee, tax_amount, total_amount
- status (ENUM)
- payment_status (ENUM)
- payment_method, payment_reference
- collector_assigned
- scheduled_date, completed_date
- notes
- created_at, updated_at
```

### **bulk_request_photos Table:**
```sql
- bulk_request_id (Foreign Key)
- photo_url
```

---

## 🚀 **Future Enhancements**

### **Planned Features:**
1. **Real-time Tracking** - Track collector in real-time
2. **Rating System** - Rate collection service
3. **Recurring Requests** - Schedule recurring bulk collections
4. **Bulk Discounts** - Multiple item discounts
5. **PDF Invoice** - Download invoice/receipt
6. **Push Notifications** - Browser/mobile notifications
7. **Chat Support** - Chat with collector
8. **Photo Recognition** - AI-powered item categorization
9. **Weight Estimation** - Auto-calculate from photos
10. **Eco-Credits** - Earn credits for proper disposal

---

## 📞 **Support**

### **For Users:**
- Help button in dashboard
- FAQ section
- Contact support
- Live chat (coming soon)

### **For Developers:**
- API documentation
- Database schema
- Service layer documentation
- Frontend component guide

---

## ✅ **Testing Checklist**

### **User Journey:**
- [ ] Create new request
- [ ] View success page
- [ ] Access my requests
- [ ] Filter requests
- [ ] View request details
- [ ] Make payment
- [ ] Cancel request
- [ ] Track status

### **Features:**
- [ ] Map interaction
- [ ] Fee calculation
- [ ] Photo upload
- [ ] Form validation
- [ ] Status updates
- [ ] Notifications

---

**Last Updated:** October 15, 2025  
**Version:** 1.0  
**Status:** Production Ready ✅

