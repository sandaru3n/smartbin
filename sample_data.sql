-- SmartBin Sample Data
-- This file contains SQL to manually insert sample data
-- Note: The application automatically inserts this data on first run
-- Only use this if you need to manually populate the database

-- ============================================
-- SAMPLE USERS
-- ============================================
-- Password for all users: password123
-- Passwords below are BCrypt hashed versions of 'password123'

-- RESIDENT USERS
INSERT INTO users (name, email, password, phone, address, role, created_at, updated_at) 
VALUES 
(
    'John Doe', 
    'john.resident@smartbin.com', 
    '$2a$10$XQK7P9h5QlYGj5BXlRXUl.eI1YHQOQlQvJNQ8fzGNNNQa7hQYGQYG',
    '+94 771234567',
    '123 Main Street, Colombo 05',
    'RESIDENT',
    NOW(),
    NOW()
),
(
    'Sarah Williams', 
    'sarah.resident@smartbin.com', 
    '$2a$10$XQK7P9h5QlYGj5BXlRXUl.eI1YHQOQlQvJNQ8fzGNNNQa7hQYGQYG',
    '+94 772345678',
    '456 Lake Road, Kandy',
    'RESIDENT',
    NOW(),
    NOW()
),
(
    'Michael Brown', 
    'michael.resident@smartbin.com', 
    '$2a$10$XQK7P9h5QlYGj5BXlRXUl.eI1YHQOQlQvJNQ8fzGNNNQa7hQYGQYG',
    '+94 773456789',
    '789 Park Avenue, Galle',
    'RESIDENT',
    NOW(),
    NOW()
);

-- COLLECTOR USERS
INSERT INTO users (name, email, password, phone, address, role, created_at, updated_at) 
VALUES 
(
    'David Collector', 
    'david.collector@smartbin.com', 
    '$2a$10$XQK7P9h5QlYGj5BXlRXUl.eI1YHQOQlQvJNQ8fzGNNNQa7hQYGQYG',
    '+94 774567890',
    '321 Collector Lane, Colombo 03',
    'COLLECTOR',
    NOW(),
    NOW()
),
(
    'Emma Wilson', 
    'emma.collector@smartbin.com', 
    '$2a$10$XQK7P9h5QlYGj5BXlRXUl.eI1YHQOQlQvJNQ8fzGNNNQa7hQYGQYG',
    '+94 775678901',
    '654 Service Road, Negombo',
    'COLLECTOR',
    NOW(),
    NOW()
),
(
    'James Taylor', 
    'james.collector@smartbin.com', 
    '$2a$10$XQK7P9h5QlYGj5BXlRXUl.eI1YHQOQlQvJNQ8fzGNNNQa7hQYGQYG',
    '+94 776789012',
    '987 Industrial Area, Kelaniya',
    'COLLECTOR',
    NOW(),
    NOW()
);

-- AUTHORITY USERS
INSERT INTO users (name, email, password, phone, address, role, created_at, updated_at) 
VALUES 
(
    'Admin Authority', 
    'admin.authority@smartbin.com', 
    '$2a$10$XQK7P9h5QlYGj5BXlRXUl.eI1YHQOQlQvJNQ8fzGNNNQa7hQYGQYG',
    '+94 777890123',
    'Municipal Building, Colombo 07',
    'AUTHORITY',
    NOW(),
    NOW()
),
(
    'Lisa Manager', 
    'lisa.authority@smartbin.com', 
    '$2a$10$XQK7P9h5QlYGj5BXlRXUl.eI1YHQOQlQvJNQ8fzGNNNQa7hQYGQYG',
    '+94 778901234',
    'City Council Office, Kandy',
    'AUTHORITY',
    NOW(),
    NOW()
),
(
    'Robert Supervisor', 
    'robert.authority@smartbin.com', 
    '$2a$10$XQK7P9h5QlYGj5BXlRXUl.eI1YHQOQlQvJNQ8fzGNNNQa7hQYGQYG',
    '+94 779012345',
    'District Office, Galle',
    'AUTHORITY',
    NOW(),
    NOW()
);

-- ============================================
-- VERIFICATION QUERIES
-- ============================================

-- Check all users
SELECT id, name, email, role, created_at FROM users ORDER BY role, name;

-- Count users by role
SELECT role, COUNT(*) as count FROM users GROUP BY role;

-- Check residents only
SELECT name, email, phone, address FROM users WHERE role = 'RESIDENT';

-- Check collectors only
SELECT name, email, phone, address FROM users WHERE role = 'COLLECTOR';

-- Check authority users only
SELECT name, email, phone, address FROM users WHERE role = 'AUTHORITY';

