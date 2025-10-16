-- PostgreSQL SQL script to create bulk request tables and add sample data
-- Run this in your smartbin_db database

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

-- Create bulk_request_photos table for storing photo URLs
CREATE TABLE IF NOT EXISTS bulk_request_photos (
    bulk_request_id BIGINT NOT NULL,
    photo_url VARCHAR(500),
    CONSTRAINT fk_bulk_request_photos FOREIGN KEY (bulk_request_id) REFERENCES bulk_requests(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_bulk_requests_user_id ON bulk_requests(user_id);
CREATE INDEX IF NOT EXISTS idx_bulk_requests_request_id ON bulk_requests(request_id);
CREATE INDEX IF NOT EXISTS idx_bulk_requests_status ON bulk_requests(status);
CREATE INDEX IF NOT EXISTS idx_bulk_requests_payment_status ON bulk_requests(payment_status);
CREATE INDEX IF NOT EXISTS idx_bulk_requests_collector_assigned ON bulk_requests(collector_assigned);
CREATE INDEX IF NOT EXISTS idx_bulk_requests_created_at ON bulk_requests(created_at);
CREATE INDEX IF NOT EXISTS idx_bulk_requests_scheduled_date ON bulk_requests(scheduled_date);

-- Insert sample bulk requests for testing
-- Note: Adjust user_id values based on your existing users table

-- Sample Request 1: Pending Payment
INSERT INTO bulk_requests (
    request_id, user_id, category, description, 
    street_address, city, zip_code, latitude, longitude,
    estimated_weight, estimated_dimensions,
    base_price, processing_fee, tax_amount, total_amount,
    status, payment_status, created_at, updated_at
) VALUES (
    'BULK-' || EXTRACT(EPOCH FROM NOW())::BIGINT || '-001',
    1, -- Replace with actual resident user_id
    'FURNITURE',
    'Old sofa set (3-seater + 2 single chairs) in good condition. Needs to be picked up from ground floor.',
    '123 Galle Road',
    'Colombo',
    '00300',
    6.9271,
    79.8612,
    150.0,
    '200cm x 90cm x 85cm',
    3500.00,
    500.00,
    200.00,
    4200.00,
    'PENDING',
    'PENDING',
    NOW(),
    NOW()
) ON CONFLICT (request_id) DO NOTHING;

-- Sample Request 2: Payment Completed (Awaiting Collector Assignment)
INSERT INTO bulk_requests (
    request_id, user_id, category, description,
    street_address, city, zip_code, latitude, longitude,
    estimated_weight, estimated_dimensions,
    base_price, processing_fee, tax_amount, total_amount,
    status, payment_status, payment_method, payment_reference,
    created_at, updated_at
) VALUES (
    'BULK-' || (EXTRACT(EPOCH FROM NOW())::BIGINT + 1) || '-002',
    1, -- Replace with actual resident user_id
    'APPLIANCES',
    'Refrigerator (not working) and washing machine. Both need to be removed from 2nd floor.',
    '456 Duplication Road',
    'Colombo',
    '00400',
    6.9150,
    79.8700,
    200.0,
    'Refrigerator: 180cm tall, Washing machine: standard size',
    5000.00,
    500.00,
    275.00,
    5775.00,
    'PAYMENT_COMPLETED',
    'COMPLETED',
    'CREDIT_CARD',
    'PAY-' || EXTRACT(EPOCH FROM NOW())::BIGINT,
    NOW() - INTERVAL '2 hours',
    NOW() - INTERVAL '2 hours'
) ON CONFLICT (request_id) DO NOTHING;

-- Sample Request 3: Collector Assigned
INSERT INTO bulk_requests (
    request_id, user_id, category, description,
    street_address, city, zip_code, latitude, longitude,
    estimated_weight, estimated_dimensions,
    base_price, processing_fee, tax_amount, total_amount,
    status, payment_status, payment_method, payment_reference,
    collector_assigned,
    created_at, updated_at
) VALUES (
    'BULK-' || (EXTRACT(EPOCH FROM NOW())::BIGINT + 2) || '-003',
    1, -- Replace with actual resident user_id
    'ELECTRONICS',
    'Old TV, computer monitor, printer, and keyboard. All electronic waste.',
    '789 Baseline Road',
    'Colombo',
    '00900',
    6.9000,
    79.8800,
    50.0,
    'TV: 32 inch, Monitor: 24 inch',
    2500.00,
    500.00,
    150.00,
    3150.00,
    'COLLECTOR_ASSIGNED',
    'COMPLETED',
    'UPI',
    'UPI-' || EXTRACT(EPOCH FROM NOW())::BIGINT,
    2, -- Replace with actual collector user_id
    NOW() - INTERVAL '4 hours',
    NOW() - INTERVAL '1 hour'
) ON CONFLICT (request_id) DO NOTHING;

-- Sample Request 4: Scheduled for Pickup
INSERT INTO bulk_requests (
    request_id, user_id, category, description,
    street_address, city, zip_code, latitude, longitude,
    estimated_weight, estimated_dimensions,
    base_price, processing_fee, tax_amount, total_amount,
    status, payment_status, payment_method, payment_reference,
    collector_assigned, scheduled_date,
    created_at, updated_at
) VALUES (
    'BULK-' || (EXTRACT(EPOCH FROM NOW())::BIGINT + 3) || '-004',
    1, -- Replace with actual resident user_id
    'MATTRESS',
    'King size mattress and box spring. Both in worn condition.',
    '321 High Level Road',
    'Colombo',
    '00600',
    6.8900,
    79.8900,
    80.0,
    '200cm x 180cm x 30cm',
    3000.00,
    500.00,
    175.00,
    3675.00,
    'SCHEDULED',
    'COMPLETED',
    'DEBIT_CARD',
    'CARD-' || EXTRACT(EPOCH FROM NOW())::BIGINT,
    2, -- Replace with actual collector user_id
    NOW() + INTERVAL '1 day',
    NOW() - INTERVAL '6 hours',
    NOW() - INTERVAL '30 minutes'
) ON CONFLICT (request_id) DO NOTHING;

-- Sample Request 5: Completed
INSERT INTO bulk_requests (
    request_id, user_id, category, description,
    street_address, city, zip_code, latitude, longitude,
    estimated_weight, estimated_dimensions,
    base_price, processing_fee, tax_amount, total_amount,
    status, payment_status, payment_method, payment_reference,
    collector_assigned, scheduled_date, completed_date,
    notes,
    created_at, updated_at
) VALUES (
    'BULK-' || (EXTRACT(EPOCH FROM NOW())::BIGINT + 4) || '-005',
    1, -- Replace with actual resident user_id
    'CONSTRUCTION',
    'Construction debris including broken tiles, cement bags, and wood pieces.',
    '555 Marine Drive',
    'Colombo',
    '00300',
    6.9200,
    79.8500,
    300.0,
    'Mixed construction waste',
    7500.00,
    500.00,
    400.00,
    8400.00,
    'COMPLETED',
    'COMPLETED',
    'NET_BANKING',
    'NET-' || EXTRACT(EPOCH FROM NOW())::BIGINT,
    2, -- Replace with actual collector user_id
    NOW() - INTERVAL '2 days',
    NOW() - INTERVAL '1 day',
    'Collection completed successfully. All items removed and disposed properly.',
    NOW() - INTERVAL '3 days',
    NOW() - INTERVAL '1 day'
) ON CONFLICT (request_id) DO NOTHING;

-- Add some photo URLs for sample requests
INSERT INTO bulk_request_photos (bulk_request_id, photo_url)
SELECT id, 'https://example.com/photos/bulk-request-' || id || '-photo1.jpg'
FROM bulk_requests
WHERE request_id LIKE 'BULK-%'
ON CONFLICT DO NOTHING;

-- Display summary
SELECT 
    'Bulk Requests Setup Complete!' as message,
    COUNT(*) as total_requests_created
FROM bulk_requests;

SELECT 
    request_id,
    category,
    status,
    payment_status,
    total_amount,
    created_at
FROM bulk_requests
ORDER BY created_at DESC;

