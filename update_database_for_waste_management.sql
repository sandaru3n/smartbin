-- Update Database Schema for Manage Daily Waste Features
-- Run this script to add new tables and columns

-- 1. Add recycling_points column to users table (if not exists)
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'recycling_points'
    ) THEN
        ALTER TABLE users ADD COLUMN recycling_points DOUBLE PRECISION DEFAULT 0.0;
    END IF;
END $$;

-- 2. Create recycling_transactions table (if not exists)
CREATE TABLE IF NOT EXISTS recycling_transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recycling_unit_qr_code VARCHAR(255) NOT NULL,
    item_type VARCHAR(50) NOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    quantity INTEGER,
    points_earned DOUBLE PRECISION NOT NULL,
    price_value DOUBLE PRECISION NOT NULL,
    status VARCHAR(20) NOT NULL,
    location VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Create waste_disposals table (if not exists)
CREATE TABLE IF NOT EXISTS waste_disposals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    bin_id BIGINT NOT NULL,
    reported_fill_level INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (bin_id) REFERENCES bins(id) ON DELETE CASCADE
);

-- 4. Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_recycling_transactions_user_id ON recycling_transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_recycling_transactions_created_at ON recycling_transactions(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_waste_disposals_user_id ON waste_disposals(user_id);
CREATE INDEX IF NOT EXISTS idx_waste_disposals_bin_id ON waste_disposals(bin_id);
CREATE INDEX IF NOT EXISTS idx_waste_disposals_created_at ON waste_disposals(created_at DESC);

-- 5. Update existing users to have 0.0 recycling points if NULL
UPDATE users SET recycling_points = 0.0 WHERE recycling_points IS NULL;

-- Verification queries
SELECT 'Schema update completed successfully!' as status;
SELECT COUNT(*) as user_count FROM users;
SELECT COUNT(*) as bin_count FROM bins;
SELECT COUNT(*) as recycling_transaction_count FROM recycling_transactions;
SELECT COUNT(*) as waste_disposal_count FROM waste_disposals;

