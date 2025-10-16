-- Clean Database for Fresh Start
-- This script removes old data and prepares database for the new features

-- Step 1: Drop all existing data (in correct order to avoid FK constraints)
TRUNCATE TABLE route_bins CASCADE;
TRUNCATE TABLE collections CASCADE;
TRUNCATE TABLE routes CASCADE;
TRUNCATE TABLE recycling_transactions CASCADE;
TRUNCATE TABLE waste_disposals CASCADE;
TRUNCATE TABLE bins CASCADE;
TRUNCATE TABLE users CASCADE;

-- Step 2: Reset sequences
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE bins_id_seq RESTART WITH 1;
ALTER SEQUENCE routes_id_seq RESTART WITH 1;
ALTER SEQUENCE collections_id_seq RESTART WITH 1;
ALTER SEQUENCE route_bins_id_seq RESTART WITH 1;
ALTER SEQUENCE recycling_transactions_id_seq RESTART WITH 1;
ALTER SEQUENCE waste_disposals_id_seq RESTART WITH 1;

-- Verification
SELECT 'Database cleaned successfully! Ready for fresh data.' as status;
SELECT 
    'users' as table_name, COUNT(*) as row_count FROM users
UNION ALL SELECT 'bins', COUNT(*) FROM bins
UNION ALL SELECT 'recycling_transactions', COUNT(*) FROM recycling_transactions
UNION ALL SELECT 'waste_disposals', COUNT(*) FROM waste_disposals;

