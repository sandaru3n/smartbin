-- Check if bulk_requests table exists and create if needed
-- Run this in pgAdmin Query Tool

-- Check if table exists
SELECT 
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'bulk_requests')
        THEN 'bulk_requests table EXISTS'
        ELSE 'bulk_requests table DOES NOT EXIST'
    END as table_status;

-- Create bulk_requests table if it doesn't exist
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

-- Create bulk_request_photos table if it doesn't exist
CREATE TABLE IF NOT EXISTS bulk_request_photos (
    bulk_request_id BIGINT NOT NULL,
    photo_url VARCHAR(500),
    CONSTRAINT fk_bulk_request_photos FOREIGN KEY (bulk_request_id) REFERENCES bulk_requests(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_bulk_requests_user_id ON bulk_requests(user_id);
CREATE INDEX IF NOT EXISTS idx_bulk_requests_status ON bulk_requests(status);
CREATE INDEX IF NOT EXISTS idx_bulk_requests_payment_status ON bulk_requests(payment_status);

-- Verify tables were created
SELECT 'Tables created successfully!' as message;
SELECT table_name FROM information_schema.tables WHERE table_name IN ('bulk_requests', 'bulk_request_photos');
