-- Water Tanker Tracker - Initial Schema
-- Flyway migration V1

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tankers table
CREATE TABLE tankers (
    id BIGSERIAL PRIMARY KEY,
    registration_number VARCHAR(20) NOT NULL UNIQUE,
    driver_name VARCHAR(100) NOT NULL,
    driver_phone VARCHAR(20) NOT NULL,
    capacity_liters INTEGER NOT NULL CHECK (capacity_liters > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'available'
        CHECK (status IN ('available', 'assigned', 'in_transit', 'loading', 'unloading', 'maintenance', 'offline')),
    current_latitude NUMERIC(10, 8),
    current_longitude NUMERIC(11, 8),
    current_address VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tanker_status ON tankers(status);
CREATE INDEX idx_tanker_registration ON tankers(registration_number);
CREATE INDEX idx_tanker_location ON tankers(current_latitude, current_longitude)
    WHERE current_latitude IS NOT NULL AND current_longitude IS NOT NULL;

-- Drivers table
CREATE TABLE drivers (
    id BIGSERIAL PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    license_number VARCHAR(30) NOT NULL UNIQUE,
    license_expiry DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'available'
        CHECK (status IN ('available', 'on_duty', 'driving', 'on_break', 'offline', 'suspended')),
    current_latitude NUMERIC(10, 8),
    current_longitude NUMERIC(11, 8),
    current_address VARCHAR(255),
    assigned_tanker_id BIGINT REFERENCES tankers(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_driver_status ON drivers(status);
CREATE INDEX idx_driver_employee ON drivers(employee_id);
CREATE INDEX idx_driver_email ON drivers(email);
CREATE INDEX idx_driver_license ON drivers(license_number);
CREATE INDEX idx_driver_location ON drivers(current_latitude, current_longitude)
    WHERE current_latitude IS NOT NULL AND current_longitude IS NOT NULL;

-- Orders table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(30) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    tanker_id BIGINT REFERENCES tankers(id) ON DELETE SET NULL,
    driver_id BIGINT REFERENCES drivers(id) ON DELETE SET NULL,
    source_latitude NUMERIC(10, 8) NOT NULL,
    source_longitude NUMERIC(11, 8) NOT NULL,
    source_address VARCHAR(255),
    destination_latitude NUMERIC(10, 8) NOT NULL,
    destination_longitude NUMERIC(11, 8) NOT NULL,
    destination_address VARCHAR(255),
    quantity_liters INTEGER NOT NULL CHECK (quantity_liters > 0),
    price NUMERIC(10, 2) NOT NULL DEFAULT 0.00 CHECK (price >= 0),
    status VARCHAR(20) NOT NULL DEFAULT 'pending'
        CHECK (status IN ('pending', 'confirmed', 'assigned', 'dispatched', 'in_progress', 'delivered', 'cancelled', 'failed')),
    scheduled_at TIMESTAMPTZ,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_order_customer ON orders(customer_id);
CREATE INDEX idx_order_tanker ON orders(tanker_id);
CREATE INDEX idx_order_driver ON orders(driver_id);
CREATE INDEX idx_order_scheduled ON orders(scheduled_at);
CREATE INDEX idx_order_number ON orders(order_number);
CREATE INDEX idx_order_active ON orders(status, scheduled_at)
    WHERE status IN ('pending', 'confirmed', 'assigned', 'dispatched', 'in_progress');

-- Updated_at trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply updated_at triggers
CREATE TRIGGER update_tankers_updated_at
    BEFORE UPDATE ON tankers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_drivers_updated_at
    BEFORE UPDATE ON drivers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();