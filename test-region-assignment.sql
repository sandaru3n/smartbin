-- Test Script for Collector Region Assignment Database Integration
-- Run these queries to verify the feature is working correctly

-- 1. Check current collectors and their region assignments
SELECT 
    id,
    name,
    email,
    role,
    region,
    created_at,
    updated_at
FROM users 
WHERE role = 'COLLECTOR'
ORDER BY id;

-- 2. Check if there are any collectors without region assignments
SELECT 
    id,
    name,
    email,
    'Not Assigned' as current_status
FROM users 
WHERE role = 'COLLECTOR' 
AND (region IS NULL OR region = '' OR region = 'Not Assigned');

-- 3. Test region assignment (replace with actual collector ID)
-- UPDATE users 
-- SET region = 'North District', updated_at = CURRENT_TIMESTAMP 
-- WHERE id = 1 AND role = 'COLLECTOR';

-- 4. Verify the update worked
-- SELECT 
--     id,
--     name,
--     region,
--     updated_at
-- FROM users 
-- WHERE id = 1;

-- 5. Check all region assignments summary
SELECT 
    region,
    COUNT(*) as collector_count
FROM users 
WHERE role = 'COLLECTOR' 
AND region IS NOT NULL 
AND region != ''
GROUP BY region
ORDER BY collector_count DESC;

-- 6. Check recent updates (last 24 hours)
SELECT 
    id,
    name,
    region,
    updated_at,
    TIMESTAMPDIFF(HOUR, updated_at, NOW()) as hours_ago
FROM users 
WHERE role = 'COLLECTOR' 
AND updated_at >= DATE_SUB(NOW(), INTERVAL 24 HOUR)
ORDER BY updated_at DESC;

-- 7. Verify authority users (who can make assignments)
SELECT 
    id,
    name,
    email,
    role
FROM users 
WHERE role = 'AUTHORITY';

-- 8. Check for any data integrity issues
SELECT 
    'Collectors without region' as issue_type,
    COUNT(*) as count
FROM users 
WHERE role = 'COLLECTOR' 
AND (region IS NULL OR region = '')

UNION ALL

SELECT 
    'Invalid regions' as issue_type,
    COUNT(*) as count
FROM users 
WHERE role = 'COLLECTOR' 
AND region IS NOT NULL 
AND region NOT IN ('North District', 'South District', 'East District', 'West District', 'Central District')

UNION ALL

SELECT 
    'Total collectors' as issue_type,
    COUNT(*) as count
FROM users 
WHERE role = 'COLLECTOR';
