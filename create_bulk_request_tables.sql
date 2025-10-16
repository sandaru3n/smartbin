-- SQL script to create bulk request tables

-- Create bulk_requests table
CREATE TABLE IF NOT EXISTS bulk_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id VARCHAR(255) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    street_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    latitude DOUBLE,
    longitude DOUBLE,
    estimated_weight DOUBLE,
    estimated_dimensions VARCHAR(255),
    base_price DOUBLE NOT NULL,
    processing_fee DOUBLE NOT NULL,
    tax_amount DOUBLE NOT NULL,
    total_amount DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    payment_reference VARCHAR(255),
    collector_assigned BIGINT,
    scheduled_date DATETIME,
    completed_date DATETIME,
    notes TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create bulk_request_photos table for storing photo URLs
CREATE TABLE IF NOT EXISTS bulk_request_photos (
    bulk_request_id BIGINT NOT NULL,
    photo_url VARCHAR(500),
    FOREIGN KEY (bulk_request_id) REFERENCES bulk_requests(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_bulk_requests_user_id ON bulk_requests(user_id);
CREATE INDEX idx_bulk_requests_status ON bulk_requests(status);
CREATE INDEX idx_bulk_requests_payment_status ON bulk_requests(payment_status);
CREATE INDEX idx_bulk_requests_collector_assigned ON bulk_requests(collector_assigned);
CREATE INDEX idx_bulk_requests_created_at ON bulk_requests(created_at);
CREATE INDEX idx_bulk_requests_scheduled_date ON bulk_requests(scheduled_date);

