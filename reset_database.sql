-- Reset SmartBin Database
-- Run this to clear all data and allow DataInitializer to recreate sample data

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Clear all tables
TRUNCATE TABLE route_bins;
TRUNCATE TABLE collections;
TRUNCATE TABLE routes;
TRUNCATE TABLE bins;
TRUNCATE TABLE users;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Verify tables are empty
SELECT 'Users:' as Table_Name, COUNT(*) as Record_Count FROM users
UNION ALL
SELECT 'Bins:', COUNT(*) FROM bins
UNION ALL
SELECT 'Collections:', COUNT(*) FROM collections
UNION ALL
SELECT 'Routes:', COUNT(*) FROM routes
UNION ALL
SELECT 'Route Bins:', COUNT(*) FROM route_bins;

SELECT 'Database reset complete! Restart the application to load sample data.' as Status;

