-- ==============================================================================
-- TruckConnect Development Seed Data
-- FOR DEVELOPMENT & TESTING PURPOSES ONLY
-- ==============================================================================

-- 1. Super Admin User (admin@truckconnect.in / Admin@123)
-- bcrypt hash for 'Admin@123': $2b$10$wOqIglhI3QG4yQk.P5j.UOSkGk32h.N7nUvO.g8x1M5H1C3q5K5qy
INSERT INTO users (id, role, name, phone, email, password_hash, status, language, theme)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'super_admin',
    'Fleet Owner',
    '+919800000001',
    'admin@truckconnect.in',
    '$2b$10$wOqIglhI3QG4yQk.P5j.UOSkGk32h.N7nUvO.g8x1M5H1C3q5K5qy',
    'active',
    'en',
    'indigo'
) ON CONFLICT (phone) DO NOTHING;

-- 2. Dispatcher User (dispatch@truckconnect.in / Dispatch@123)
INSERT INTO users (id, role, name, phone, email, password_hash, status, language, theme)
VALUES (
    '00000000-0000-0000-0000-000000000002',
    'dispatcher',
    'Anil Kumar (Dispatcher)',
    '+919800000002',
    'dispatch@truckconnect.in',
    '$2b$10$wOqIglhI3QG4yQk.P5j.UOSkGk32h.N7nUvO.g8x1M5H1C3q5K5qy',
    'active',
    'ml',
    'indigo'
) ON CONFLICT (phone) DO NOTHING;

-- 3. Driver 1: Ravi K (PIN: 1234)
-- bcrypt hash for '1234': $2b$10$E5zD.4.rK6F05.yq6fLdgeuL9N0P2d2aH.g9Tq4B2g9H2d2aH.g9T
INSERT INTO users (id, role, name, phone, pin_hash, status, language, theme)
VALUES (
    '00000000-0000-0000-0000-000000000011',
    'driver',
    'Ravi Kumar',
    '+919847012345',
    '$2b$10$E5zD.4.rK6F05.yq6fLdgeuL9N0P2d2aH.g9Tq4B2g9H2d2aH.g9T',
    'active',
    'ml',
    'indigo'
) ON CONFLICT (phone) DO NOTHING;

INSERT INTO drivers (
    user_id, licence_no, licence_type, licence_expiry, 
    emergency_contact_name, emergency_contact_phone, emergency_contact_relation,
    approval_status, availability, current_location
) VALUES (
    '00000000-0000-0000-0000-000000000011',
    'KL07 20150012345',
    'HMV',
    '2028-12-31',
    'Suma Ravi',
    '+919447099887',
    'Spouse',
    'approved',
    'on_trip',
    ST_SetSRID(ST_MakePoint(76.2673, 9.9312), 4326) -- Kochi
) ON CONFLICT (user_id) DO NOTHING;

-- 4. Driver 2: Suresh M (PIN: 1234)
INSERT INTO users (id, role, name, phone, pin_hash, status, language, theme)
VALUES (
    '00000000-0000-0000-0000-000000000012',
    'driver',
    'Suresh Menon',
    '+919847054321',
    '$2b$10$E5zD.4.rK6F05.yq6fLdgeuL9N0P2d2aH.g9Tq4B2g9H2d2aH.g9T',
    'active',
    'ml',
    'emerald'
) ON CONFLICT (phone) DO NOTHING;

INSERT INTO drivers (
    user_id, licence_no, licence_type, licence_expiry, 
    emergency_contact_name, emergency_contact_phone, emergency_contact_relation,
    approval_status, availability, current_location
) VALUES (
    '00000000-0000-0000-0000-000000000012',
    'KL08 20180054321',
    'HMV',
    '2030-05-15',
    'Gireesh M',
    '+919447088776',
    'Brother',
    'approved',
    'available',
    ST_SetSRID(ST_MakePoint(76.2144, 10.5276), 4326) -- Thrissur
) ON CONFLICT (user_id) DO NOTHING;

-- 5. Driver 3: Babu T (Pending Approval, PIN: 1234)
INSERT INTO users (id, role, name, phone, pin_hash, status, language, theme)
VALUES (
    '00000000-0000-0000-0000-000000000013',
    'driver',
    'Babu Thomas',
    '+919447011223',
    '$2b$10$E5zD.4.rK6F05.yq6fLdgeuL9N0P2d2aH.g9Tq4B2g9H2d2aH.g9T',
    'pending_verification',
    'ml',
    'sunset'
) ON CONFLICT (phone) DO NOTHING;

INSERT INTO drivers (
    user_id, licence_no, licence_type, licence_expiry, 
    emergency_contact_name, emergency_contact_phone, emergency_contact_relation,
    approval_status, availability
) VALUES (
    '00000000-0000-0000-0000-000000000013',
    'KL05 20190099887',
    'LMV-Transport',
    '2029-08-20',
    'Mary Thomas',
    '+919447077665',
    'Spouse',
    'pending',
    'offline'
) ON CONFLICT (user_id) DO NOTHING;

-- 6. Vehicles
INSERT INTO vehicles (id, reg_no, type, capacity_kg, status, default_driver_id)
VALUES 
    ('00000000-0000-0000-0000-000000000101', 'KL 07 AB 1234', '10-Tonne Multi-Axle', 10000, 'on_trip', '00000000-0000-0000-0000-000000000011'),
    ('00000000-0000-0000-0000-000000000102', 'KL 07 CD 5678', '16-Tonne Heavy Truck', 16000, 'available', '00000000-0000-0000-0000-000000000012'),
    ('00000000-0000-0000-0000-000000000103', 'KL 08 EF 9012', '25-Tonne Container', 25000, 'available', NULL)
ON CONFLICT (reg_no) DO NOTHING;

-- 7. Active Shipment: TC-2026-000123 (Kochi to Kozhikode)
INSERT INTO shipments (
    id, code, status, pickup_address, pickup_geo, pickup_contact_name, pickup_contact_phone,
    drop_address, drop_geo, drop_contact_name, drop_contact_phone, goods_description, weight_kg,
    scheduled_at, driver_id, vehicle_id, created_by
) VALUES (
    '00000000-0000-0000-0000-000000000201',
    'TC-2026-000123',
    'in_transit',
    'Willingdon Island Wharf, Kochi, Kerala',
    ST_SetSRID(ST_MakePoint(76.2673, 9.9312), 4326),
    'Mathew Varghese',
    '+919847000111',
    'Valayanad Industrial Estate, Kozhikode, Kerala',
    ST_SetSRID(ST_MakePoint(75.7804, 11.2588), 4326),
    'Hassan Koya',
    '+919847000222',
    'Rice bags (8 tonnes)',
    8000,
    NOW() - INTERVAL '2 hours',
    '00000000-0000-0000-0000-000000000011',
    '00000000-0000-0000-0000-000000000101',
    '00000000-0000-0000-0000-000000000001'
) ON CONFLICT (code) DO NOTHING;

-- 8. Direct Flavor App Release v1.0.0
INSERT INTO app_releases (
    id, flavor, version_code, version_name, min_supported_version_code,
    apk_url, sha256_checksum, file_size_bytes, release_notes_en, release_notes_ml
) VALUES (
    '00000000-0000-0000-0000-000000000301',
    'direct',
    1,
    '1.0.0',
    1,
    'https://easqjokyjulfuozdxiyp.supabase.co/storage/v1/object/public/releases/app-direct-release-v1.0.0.apk',
    'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
    18456123,
    'Initial launch build with offline trip execution.',
    'ഓഫ്‌ലൈൻ ട്രിപ്പ് സൗകര്യമുള്ള ആദ്യ പതിപ്പ്.'
) ON CONFLICT (flavor, version_code) DO NOTHING;
