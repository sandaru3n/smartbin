-- Quick verification script for bulk_requests tables
-- Run this in pgAdmin Query Tool to check if tables exist

-- Check if bulk_requests table exists
SELECT 
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM information_schema.tables 
            WHERE table_schema = 'public' 
            AND table_name = 'bulk_requests'
        )
        THEN '✅ bulk_requests table EXISTS'
        ELSE '❌ bulk_requests table DOES NOT EXIST - Run create_bulk_tables_NOW.sql'
    END as bulk_requests_status;

-- Check if bulk_request_photos table exists
SELECT 
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM information_schema.tables 
            WHERE table_schema = 'public' 
            AND table_name = 'bulk_request_photos'
        )
        THEN '✅ bulk_request_photos table EXISTS'
        ELSE '❌ bulk_request_photos table DOES NOT EXIST - Run create_bulk_tables_NOW.sql'
    END as photos_table_status;

-- Count existing bulk requests
SELECT 
    COUNT(*) as total_bulk_requests,
    COUNT(CASE WHEN status = 'PENDING' THEN 1 END) as pending,
    COUNT(CASE WHEN status = 'PAYMENT_COMPLETED' THEN 1 END) as paid,
    COUNT(CASE WHEN status = 'COLLECTOR_ASSIGNED' THEN 1 END) as assigned,
    COUNT(CASE WHEN status = 'SCHEDULED' THEN 1 END) as scheduled,
    COUNT(CASE WHEN status = 'IN_PROGRESS' THEN 1 END) as in_progress,
    COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as completed
FROM bulk_requests;

-- Show recent bulk requests
SELECT 
    id, 
    request_id, 
    category, 
    status, 
    payment_status,
    total_amount,
    created_at
FROM bulk_requests
ORDER BY created_at DESC
LIMIT 10;

