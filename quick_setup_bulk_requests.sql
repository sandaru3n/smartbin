-- Quick setup for bulk_requests with sample data
-- Copy and paste this into pgAdmin Query Tool

-- Drop existing tables if you want fresh start (optional)
-- DROP TABLE IF EXISTS bulk_request_photos CASCADE;
-- DROP TABLE IF EXISTS bulk_requests CASCADE;

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

-- Insert sample data (3 requests - PAID and ready for authority to assign)
-- Get first resident user_id
DO $$
DECLARE
    resident_user_id BIGINT;
BEGIN
    -- Get first resident
    SELECT id INTO resident_user_id FROM users WHERE role = 'RESIDENT' LIMIT 1;
    
    -- If no resident found, use user_id 1
    IF resident_user_id IS NULL THEN
        resident_user_id := 1;
    END IF;
    
    -- Request 1: PAYMENT_COMPLETED - Ready for collector assignment
    INSERT INTO bulk_requests (
        request_id, user_id, category, description,
        street_address, city, zip_code, latitude, longitude,
        base_price, processing_fee, tax_amount, total_amount,
        status, payment_status, payment_method, payment_reference,
        created_at, updated_at
    ) VALUES (
        'BULK-' || FLOOR(EXTRACT(EPOCH FROM NOW()))::TEXT || '-001',
        resident_user_id,
        'FURNITURE',
        'Old sofa set (3-seater) needs removal. Located on ground floor.',
        '123 Galle Road',
        'Colombo',
        '00300',
        6.9271, 79.8612,
        3500.00, 500.00, 200.00, 4200.00,
        'PAYMENT_COMPLETED',
        'COMPLETED',
        'CREDIT_CARD',
        'PAY-' || FLOOR(EXTRACT(EPOCH FROM NOW()))::TEXT,
        NOW() - INTERVAL '2 hours',
        NOW() - INTERVAL '2 hours'
    ) ON CONFLICT (request_id) DO NOTHING;
    
    -- Request 2: PAYMENT_COMPLETED - Ready for assignment
    INSERT INTO bulk_requests (
        request_id, user_id, category, description,
        street_address, city, zip_code, latitude, longitude,
        base_price, processing_fee, tax_amount, total_amount,
        status, payment_status, payment_method, payment_reference,
        created_at, updated_at
    ) VALUES (
        'BULK-' || (FLOOR(EXTRACT(EPOCH FROM NOW()))::BIGINT + 1)::TEXT || '-002',
        resident_user_id,
        'APPLIANCES',
        'Refrigerator (not working) and washing machine.',
        '456 Duplication Road',
        'Colombo',
        '00400',
        6.9150, 79.8700,
        5000.00, 500.00, 275.00, 5775.00,
        'PAYMENT_COMPLETED',
        'COMPLETED',
        'UPI',
        'UPI-' || FLOOR(EXTRACT(EPOCH FROM NOW()))::TEXT,
        NOW() - INTERVAL '1 hour',
        NOW() - INTERVAL '1 hour'
    ) ON CONFLICT (request_id) DO NOTHING;
    
    -- Request 3: PENDING - Not paid yet (won't show in authority)
    INSERT INTO bulk_requests (
        request_id, user_id, category, description,
        street_address, city, zip_code, latitude, longitude,
        base_price, processing_fee, tax_amount, total_amount,
        status, payment_status,
        created_at, updated_at
    ) VALUES (
        'BULK-' || (FLOOR(EXTRACT(EPOCH FROM NOW()))::BIGINT + 2)::TEXT || '-003',
        resident_user_id,
        'ELECTRONICS',
        'Old TV and computer equipment.',
        '789 Marine Drive',
        'Colombo',
        '00300',
        6.9200, 79.8500,
        2500.00, 500.00, 150.00, 3150.00,
        'PENDING',
        'PENDING',
        NOW() - INTERVAL '30 minutes',
        NOW() - INTERVAL '30 minutes'
    ) ON CONFLICT (request_id) DO NOTHING;
    
    RAISE NOTICE 'Sample bulk requests created for user_id: %', resident_user_id;
END $$;

-- Verify data
SELECT 
    request_id,
    category,
    status,
    payment_status,
    total_amount,
    created_at
FROM bulk_requests
ORDER BY created_at DESC;

-- Show summary
SELECT 
    '✅ Setup Complete!' as message,
    COUNT(*) as total_requests,
    COUNT(*) FILTER (WHERE status = 'PAYMENT_COMPLETED') as ready_for_authority
FROM bulk_requests;

