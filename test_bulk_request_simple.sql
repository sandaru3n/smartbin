-- Simple test: Check and add one bulk request
-- Run this in pgAdmin on smartbin_db database

-- First, check if tables exist
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name IN ('bulk_requests', 'bulk_request_photos');

-- Check existing users
SELECT id, name, email, role FROM users ORDER BY id;

-- If bulk_requests table doesn't exist, Hibernate will create it when app starts
-- Just restart the app and it will auto-create the tables

-- Once tables exist, insert a simple test request
-- Replace user_id with your actual resident ID from users table
INSERT INTO bulk_requests (
    request_id, user_id, category, description,
    street_address, city, zip_code,
    latitude, longitude,
    base_price, processing_fee, tax_amount, total_amount,
    status, payment_status,
    created_at, updated_at
) VALUES (
    'BULK-TEST-001',
    1,  -- CHANGE THIS to your resident user_id
    'FURNITURE',
    'Test furniture removal - old sofa',
    '123 Test Street',
    'Colombo',
    '00100',
    6.9271, 79.8612,
    3500.00, 500.00, 200.00, 4200.00,
    'PENDING', 'PENDING',
    NOW(), NOW()
) ON CONFLICT (request_id) DO UPDATE 
SET updated_at = NOW();

-- Verify the insert
SELECT 
    id,
    request_id,
    user_id,
    category,
    status,
    payment_status,
    total_amount,
    created_at
FROM bulk_requests
ORDER BY created_at DESC
LIMIT 5;

-- Check count
SELECT COUNT(*) as total_bulk_requests FROM bulk_requests;

