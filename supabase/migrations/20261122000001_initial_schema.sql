-- ==============================================================================
-- TruckConnect Database Schema v1.0
-- Target: Supabase PostgreSQL with PostGIS
-- ==============================================================================

-- Enable Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "postgis";

-- ------------------------------------------------------------------------------
-- Enums
-- ------------------------------------------------------------------------------
DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('super_admin', 'dispatcher', 'driver');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE user_status AS ENUM ('pending_verification', 'active', 'inactive', 'suspended');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE driver_availability AS ENUM ('available', 'on_trip', 'on_leave', 'offline');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE vehicle_status AS ENUM ('available', 'on_trip', 'maintenance');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE shipment_status AS ENUM (
        'created', 'assigned', 'accepted', 'rejected', 
        'reached_pickup', 'loaded', 'in_transit', 'reached_drop', 
        'delivered', 'cancelled', 'failed'
    );
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE approval_status AS ENUM ('pending', 'approved', 'rejected');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE vehicle_ownership AS ENUM ('company_fleet', 'owner_operator');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE document_kind AS ENUM (
        'driving_licence_front', 'driving_licence_back', 'aadhaar_card', 
        'rc_book', 'insurance_policy', 'fitness_certificate', 'permit', 'puc'
    );
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- ------------------------------------------------------------------------------
-- 1. Users Table
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    role user_role NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    pin_hash VARCHAR(255),
    pin_failed_attempts INT DEFAULT 0,
    pin_locked_until TIMESTAMPTZ,
    status user_status NOT NULL DEFAULT 'active',
    language VARCHAR(10) DEFAULT 'ml',
    theme VARCHAR(20) DEFAULT 'indigo',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 2. Drivers Table
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS drivers (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    licence_no VARCHAR(50) NOT NULL,
    licence_type VARCHAR(50) NOT NULL,
    licence_expiry DATE NOT NULL,
    emergency_contact_name VARCHAR(255) NOT NULL,
    emergency_contact_phone VARCHAR(20) NOT NULL,
    emergency_contact_relation VARCHAR(50) NOT NULL,
    vehicle_ownership vehicle_ownership DEFAULT 'company_fleet',
    preferred_vehicle_id UUID,
    approval_status approval_status DEFAULT 'approved',
    rejection_reason TEXT,
    approved_by UUID REFERENCES users(id),
    approved_at TIMESTAMPTZ,
    availability driver_availability DEFAULT 'offline',
    current_location GEOMETRY(Point, 4326),
    last_location_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 3. Vehicles Table
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS vehicles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reg_no VARCHAR(20) UNIQUE NOT NULL,
    type VARCHAR(50) NOT NULL,
    capacity_kg INT NOT NULL,
    status vehicle_status DEFAULT 'available',
    default_driver_id UUID REFERENCES users(id),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 4. Documents Table
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_type VARCHAR(50) NOT NULL, -- 'driver' or 'vehicle'
    owner_id UUID NOT NULL,
    kind document_kind NOT NULL,
    file_url TEXT NOT NULL,
    expiry_date DATE,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 5. Shipments Table
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS shipments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) UNIQUE NOT NULL,
    status shipment_status NOT NULL DEFAULT 'created',
    pickup_address TEXT NOT NULL,
    pickup_geo GEOMETRY(Point, 4326),
    pickup_contact_name VARCHAR(255),
    pickup_contact_phone VARCHAR(20),
    drop_address TEXT NOT NULL,
    drop_geo GEOMETRY(Point, 4326),
    drop_contact_name VARCHAR(255),
    drop_contact_phone VARCHAR(20),
    goods_description TEXT NOT NULL,
    weight_kg INT NOT NULL,
    scheduled_at TIMESTAMPTZ NOT NULL,
    expected_at TIMESTAMPTZ,
    driver_id UUID REFERENCES users(id),
    vehicle_id UUID REFERENCES vehicles(id),
    created_by UUID REFERENCES users(id) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 6. Shipment Stops (for multi-stop routing)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS shipment_stops (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    shipment_id UUID NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    seq INT NOT NULL,
    type VARCHAR(20) NOT NULL, -- 'pickup' or 'drop'
    address TEXT NOT NULL,
    geo_point GEOMETRY(Point, 4326),
    contact_name VARCHAR(255),
    contact_phone VARCHAR(20)
);

-- ------------------------------------------------------------------------------
-- 7. Shipment Events (State Machine Audit Log)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS shipment_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    shipment_id UUID NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    status shipment_status NOT NULL,
    actor_id UUID NOT NULL REFERENCES users(id),
    geo_point GEOMETRY(Point, 4326),
    note TEXT,
    client_request_id VARCHAR(64) UNIQUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 8. Location Pings
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS location_pings (
    id BIGSERIAL PRIMARY KEY,
    driver_id UUID NOT NULL REFERENCES users(id),
    shipment_id UUID REFERENCES shipments(id),
    geo_point GEOMETRY(Point, 4326) NOT NULL,
    speed_kmh FLOAT,
    accuracy_meters FLOAT,
    battery_pct INT,
    recorded_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 9. Proofs of Delivery
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS proofs_of_delivery (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    shipment_id UUID UNIQUE NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    photo_urls TEXT[] NOT NULL,
    receiver_name VARCHAR(255) NOT NULL,
    signature_url TEXT,
    otp_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 10. Devices Table (FCM & Device Binding)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS devices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    fcm_token TEXT NOT NULL,
    device_model VARCHAR(100),
    app_version VARCHAR(50),
    last_seen TIMESTAMPTZ DEFAULT NOW(),
    revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT uq_user_device UNIQUE (user_id, fcm_token)
);

-- ------------------------------------------------------------------------------
-- 11. Notifications Table
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL DEFAULT '{}'::jsonb,
    sent_at TIMESTAMPTZ DEFAULT NOW(),
    read_at TIMESTAMPTZ
);

-- ------------------------------------------------------------------------------
-- 12. App Releases Table (Direct In-App Updater Registry)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS app_releases (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    flavor VARCHAR(20) NOT NULL,
    version_code INT NOT NULL,
    version_name VARCHAR(50) NOT NULL,
    min_supported_version_code INT NOT NULL,
    apk_url TEXT NOT NULL,
    sha256_checksum VARCHAR(64) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    release_notes_en TEXT,
    release_notes_ml TEXT,
    rollout_percentage INT DEFAULT 100,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT uq_flavor_version UNIQUE (flavor, version_code)
);

-- ------------------------------------------------------------------------------
-- 13. Audit Logs Table
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    actor_id UUID REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100),
    before JSONB,
    after JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- Spatial & Performance Indices
-- ------------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_drivers_availability ON drivers(availability);
CREATE INDEX IF NOT EXISTS idx_drivers_approval_status ON drivers(approval_status);
CREATE INDEX IF NOT EXISTS idx_drivers_location ON drivers USING GIST(current_location);
CREATE INDEX IF NOT EXISTS idx_vehicles_status ON vehicles(status);
CREATE INDEX IF NOT EXISTS idx_shipments_status ON shipments(status);
CREATE INDEX IF NOT EXISTS idx_shipments_driver ON shipments(driver_id);
CREATE INDEX IF NOT EXISTS idx_shipments_pickup_geo ON shipments USING GIST(pickup_geo);
CREATE INDEX IF NOT EXISTS idx_shipments_drop_geo ON shipments USING GIST(drop_geo);
CREATE INDEX IF NOT EXISTS idx_location_pings_driver_time ON location_pings(driver_id, recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_location_pings_geo ON location_pings USING GIST(geo_point);
CREATE INDEX IF NOT EXISTS idx_notifications_user_unread ON notifications(user_id) WHERE read_at IS NULL;

-- ------------------------------------------------------------------------------
-- Row Level Security (RLS) Configuration
-- ------------------------------------------------------------------------------
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE drivers ENABLE ROW LEVEL SECURITY;
ALTER TABLE vehicles ENABLE ROW LEVEL SECURITY;
ALTER TABLE documents ENABLE ROW LEVEL SECURITY;
ALTER TABLE shipments ENABLE ROW LEVEL SECURITY;
ALTER TABLE shipment_stops ENABLE ROW LEVEL SECURITY;
ALTER TABLE shipment_events ENABLE ROW LEVEL SECURITY;
ALTER TABLE location_pings ENABLE ROW LEVEL SECURITY;
ALTER TABLE proofs_of_delivery ENABLE ROW LEVEL SECURITY;
ALTER TABLE devices ENABLE ROW LEVEL SECURITY;
ALTER TABLE notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE app_releases ENABLE ROW LEVEL SECURITY;
ALTER TABLE audit_logs ENABLE ROW LEVEL SECURITY;

-- Service Role Full Access Bypass Policies
CREATE POLICY service_role_all_users ON users FOR ALL TO service_role USING (true) WITH CHECK (true);
CREATE POLICY service_role_all_drivers ON drivers FOR ALL TO service_role USING (true) WITH CHECK (true);
CREATE POLICY service_role_all_vehicles ON vehicles FOR ALL TO service_role USING (true) WITH CHECK (true);
CREATE POLICY service_role_all_documents ON documents FOR ALL TO service_role USING (true) WITH CHECK (true);
CREATE POLICY service_role_all_shipments ON shipments FOR ALL TO service_role USING (true) WITH CHECK (true);
CREATE POLICY service_role_all_stops ON shipment_stops FOR ALL TO service_role USING (true) WITH CHECK (true);
CREATE POLICY service_role_all_events ON shipment_events FOR ALL TO service_role USING (true) WITH CHECK (true);
CREATE POLICY service_role_all_pings ON location_pings FOR ALL TO service_role USING (true) WITH CHECK (true);
CREATE POLICY service_role_all_pod ON proofs_of_delivery FOR ALL TO service_role USING (true) WITH CHECK (true);
CREATE POLICY service_role_all_devices ON devices FOR ALL TO service_role USING (true) WITH CHECK (true);
CREATE POLICY service_role_all_notifications ON notifications FOR ALL TO service_role USING (true) WITH CHECK (true);
CREATE POLICY service_role_all_releases ON app_releases FOR ALL TO service_role USING (true) WITH CHECK (true);
CREATE POLICY service_role_all_audit ON audit_logs FOR ALL TO service_role USING (true) WITH CHECK (true);

-- Authenticated User Read Policies
CREATE POLICY users_read_own ON users FOR SELECT TO authenticated USING (auth.uid() = id);
CREATE POLICY drivers_read_own ON drivers FOR SELECT TO authenticated USING (auth.uid() = user_id);
CREATE POLICY shipments_read_driver ON shipments FOR SELECT TO authenticated USING (auth.uid() = driver_id);
CREATE POLICY notifications_read_own ON notifications FOR SELECT TO authenticated USING (auth.uid() = user_id);
CREATE POLICY app_releases_public_read ON app_releases FOR SELECT TO anon, authenticated USING (true);
