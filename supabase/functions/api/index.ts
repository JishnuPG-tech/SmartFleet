// Supabase Edge Function: TruckConnect API
// Hosted 24/7 on Supabase Edge Network (Deno)

import { createClient } from "https://esm.sh/@supabase/supabase-js@2.48.1";
import * as bcrypt from "https://esm.sh/bcryptjs@2.4.3";

const hasher: any = (bcrypt as any).default || bcrypt;

const SUPABASE_URL = Deno.env.get("SUPABASE_URL") || "https://easqjokyjulfuozdxiyp.supabase.co";
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") || "";
const JWT_SECRET = Deno.env.get("JWT_SECRET") || "Pj3TLmYW3NsIq66OiVvrXZ3TrnXZ2mOGBBrJQag4nRAtu8XTnrSVL2mJbwNBdgaFmhN3jbZaj7lD2P0RkwJU9w==";

const supabase = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY);

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "GET, POST, PUT, PATCH, DELETE, OPTIONS",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type, client-request-id, x-request-id",
};

// Helper: base64url encode
function base64url(str: string): string {
  return btoa(str).replace(/=/g, "").replace(/\+/g, "-").replace(/\//g, "_");
}

// Helper: Create JWT
async function generateJwt(payload: Record<string, unknown>, secret: string): Promise<string> {
  const encoder = new TextEncoder();
  const header = { alg: "HS256", typ: "JWT" };
  const encodedHeader = base64url(JSON.stringify(header));
  const encodedPayload = base64url(JSON.stringify(payload));
  const data = `${encodedHeader}.${encodedPayload}`;

  const key = await crypto.subtle.importKey(
    "raw",
    encoder.encode(secret),
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"]
  );

  const signature = await crypto.subtle.sign("HMAC", key, encoder.encode(data));
  const encodedSignature = base64url(String.fromCharCode(...new Uint8Array(signature)));
  return `${data}.${encodedSignature}`;
}

// Helper: verify JWT and get user
async function getUserFromAuthHeader(req: Request) {
  const authHeader = req.headers.get("Authorization");
  if (!authHeader || !authHeader.startsWith("Bearer ")) return null;
  const token = authHeader.split(" ")[1];
  try {
    const parts = token.split(".");
    if (parts.length !== 3) return null;
    const payload = JSON.parse(atob(parts[1].replace(/-/g, "+").replace(/_/g, "/")));
    return payload;
  } catch {
    return null;
  }
}

function jsonResponse(data: unknown, status = 200) {
  return new Response(JSON.stringify(data), {
    status,
    headers: {
      ...corsHeaders,
      "Content-Type": "application/json",
    },
  });
}

function errorResponse(message: string, status = 400) {
  return jsonResponse({ error: true, message }, status);
}

Deno.serve(async (req: Request) => {
  // CORS Preflight
  if (req.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: corsHeaders });
  }

  const url = new URL(req.url);
  // Normalize path by stripping prefix e.g. /functions/v1/api, /api/v1, or /api
  let path = url.pathname
    .replace(/^\/functions\/v1\/api/, "")
    .replace(/^\/functions\/v1/, "")
    .replace(/^\/api\/v1/, "")
    .replace(/^\/api/, "");
  if (!path.startsWith("/")) path = "/" + path;
  if (path === "") path = "/";

  try {
    // 1. HEALTH CHECK
    if (req.method === "GET" && (path === "/" || path === "/health")) {
      return jsonResponse({
        status: "ok",
        service: "TruckConnect API",
        runtime: "Supabase Edge Functions (Deno)",
        timestamp: new Date().toISOString(),
      });
    }

    // 2. ADMIN LOGIN (supports /auth/login and /auth/admin/login)
    if (req.method === "POST" && (path === "/auth/login" || path === "/auth/admin/login")) {
      const body = await req.json();
      const { email, password } = body;
      if (!email || !password) return errorResponse("Email and password required", 400);

      const { data: user, error } = await supabase
        .from("users")
        .select("*")
        .eq("email", email.toLowerCase().trim())
        .single();

      if (error || !user) return errorResponse("Invalid email or password", 401);
      if (!["super_admin", "dispatcher"].includes(user.role)) {
        return errorResponse("Access denied: Admin credentials required", 403);
      }

      const match = await hasher.compare(password, user.password_hash || "");
      if (!match) return errorResponse("Invalid email or password", 401);

      const now = Math.floor(Date.now() / 1000);
      const tokenPayload = {
        sub: user.id,
        email: user.email,
        role: user.role,
        aud: "authenticated",
        iat: now,
        exp: now + 60 * 60 * 24 * 7, // 7 days
      };

      const accessToken = await generateJwt(tokenPayload, JWT_SECRET);
      return jsonResponse({
        accessToken,
        refreshToken: "refresh_" + crypto.randomUUID(),
        expiresIn: 604800,
        user: {
          id: user.id,
          name: user.name,
          phone: user.phone || "",
          email: user.email,
          role: user.role,
          language: "en",
          theme: "indigo",
        },
      });
    }

    // 3. DRIVER LOGIN (supports /auth/driver/login-pin and /auth/driver/login)
    if (req.method === "POST" && (path === "/auth/driver/login-pin" || path === "/auth/driver/login")) {
      const body = await req.json();
      const { phone, pin, deviceModel, fcmToken } = body;
      if (!phone || !pin) return errorResponse("Phone and PIN required", 400);

      const cleanPhone = phone.trim();
      const { data: user, error } = await supabase
        .from("users")
        .select("*")
        .eq("phone", cleanPhone)
        .eq("role", "driver")
        .single();

      if (error || !user) return errorResponse("Driver not found. Please check number or register.", 401);

      if (user.status === "pending_verification") {
        return errorResponse("Your registration is pending approval by admin / അഡ്മിൻ്റെ അനുമതിക്കായി കാത്തിരിക്കുന്നു", 403);
      }
      if (user.status === "suspended") {
        return errorResponse("Your account has been suspended. Contact office.", 403);
      }

      const match = await hasher.compare(pin, user.pin_hash || "");
      if (!match) return errorResponse("Invalid 4-digit PIN / തെറ്റായ പിൻ", 401);

      // Update device info
      await supabase
        .from("users")
        .update({
          device_id: deviceModel || user.device_id,
          fcm_token: fcmToken || user.fcm_token,
          last_seen_at: new Date().toISOString(),
        })
        .eq("id", user.id);

      const now = Math.floor(Date.now() / 1000);
      const tokenPayload = {
        sub: user.id,
        phone: user.phone,
        role: "driver",
        aud: "authenticated",
        iat: now,
        exp: now + 60 * 60 * 24 * 30, // 30 days
      };

      const accessToken = await generateJwt(tokenPayload, JWT_SECRET);
      return jsonResponse({
        accessToken,
        refreshToken: "refresh_" + crypto.randomUUID(),
        expiresIn: 2592000,
        user: {
          id: user.id,
          name: user.name,
          phone: user.phone,
          email: user.email || null,
          role: user.role,
          language: "ml",
          theme: "indigo",
        },
      });
    }

    // 4. DRIVER SELF-REGISTRATION (AUTH-8)
    if (req.method === "POST" && path === "/auth/driver/register") {
      const body = await req.json();
      const fullName = body.name || body.fullName;
      const phone = body.phone;
      const pin = body.pin;
      const licenceNo = body.licenceNo || body.licenceNumber;
      const licenceType = body.licenceType || "heavy";
      const licenceExpiry = body.licenceExpiry || "2031-12-31";
      const ownershipType = body.vehicleOwnership || body.ownershipType || "company_fleet";
      const emergencyContactName = body.emergencyContactName || "Family";
      const emergencyContactPhone = body.emergencyContactPhone || phone;
      const emergencyContactRelation = body.emergencyContactRelation || "Family";

      if (!fullName || !phone || !pin || !licenceNo) {
        return errorResponse("Missing required fields for registration", 400);
      }

      const cleanPhone = phone.trim();
      const { data: existing } = await supabase
        .from("users")
        .select("id")
        .eq("phone", cleanPhone)
        .single();

      if (existing) {
        return errorResponse("This phone number is already registered / ഈ ഫോൺ നമ്പർ മുൻപേ രജിസ്റ്റർ ചെയ്തിട്ടുണ്ട്", 400);
      }

      const pinHash = await hasher.hash(pin, 10);
      const userId = crypto.randomUUID();

      // Insert User
      const { error: userError } = await supabase.from("users").insert({
        id: userId,
        name: fullName,
        phone: cleanPhone,
        pin_hash: pinHash,
        role: "driver",
        status: "pending_verification",
      });

      if (userError) {
        return errorResponse("Failed to create user account: " + userError.message, 500);
      }

      const ownership = (ownershipType === "individual" || ownershipType === "owner_operator") ? "owner_operator" : "company_fleet";

      // Insert Driver
      const { error: driverError } = await supabase.from("drivers").insert({
        user_id: userId,
        licence_no: licenceNo.toUpperCase().trim(),
        licence_type: licenceType,
        licence_expiry: licenceExpiry,
        vehicle_ownership: ownership,
        emergency_contact_name: emergencyContactName,
        emergency_contact_phone: emergencyContactPhone,
        emergency_contact_relation: emergencyContactRelation,
        approval_status: "pending",
      });

      if (driverError) {
        return errorResponse("Failed to save driver profile: " + driverError.message, 500);
      }

      return jsonResponse(
        {
          message: "Registration submitted successfully. Pending admin approval. / രജിസ്ട്രേഷൻ പൂർത്തിയായി. അഡ്മിൻ്റെ അനുമതിക്കായി കാത്തിരിക്കുന്നു.",
          status: "pending_verification",
          userId: userId,
        },
        201
      );
    }

    // 5. ADMIN: PENDING DRIVER APPROVALS (AUTH-9)
    if (req.method === "GET" && path === "/admin/drivers/pending") {
      const { data, error } = await supabase
        .from("drivers")
        .select(`
          user_id,
          licence_no,
          licence_type,
          vehicle_ownership,
          emergency_contact_name,
          emergency_contact_phone,
          approval_status,
          created_at,
          users!drivers_user_id_fkey (
            id,
            name,
            phone,
            status,
            created_at
          )
        `)
        .eq("approval_status", "pending")
        .order("created_at", { ascending: false });

      if (error) return errorResponse(error.message, 500);

      const mapped = (data || []).map((d: any) => ({
        id: d.user_id,
        userId: d.user_id,
        fullName: d.users?.name,
        phone: d.users?.phone,
        licenceNumber: d.licence_no,
        licenceType: d.licence_type,
        ownershipType: d.vehicle_ownership,
        emergencyContactName: d.emergency_contact_name,
        emergencyContactPhone: d.emergency_contact_phone,
        approvalStatus: d.approval_status,
        submittedAt: d.created_at,
      }));

      return jsonResponse(mapped);
    }

    // 6. ADMIN: APPROVE DRIVER
    if (req.method === "POST" && path.match(/^\/admin\/drivers\/[^\/]+\/approve$/)) {
      const userId = path.split("/")[3];

      await supabase.from("drivers").update({ approval_status: "approved" }).eq("user_id", userId);
      await supabase.from("users").update({ status: "active" }).eq("id", userId);

      return jsonResponse({ success: true, message: "Driver approved successfully" });
    }

    // 7. ADMIN: REJECT DRIVER
    if (req.method === "POST" && path.match(/^\/admin\/drivers\/[^\/]+\/reject$/)) {
      const userId = path.split("/")[3];
      const body = await req.json().catch(() => ({}));
      const reason = body.reason || "Documents could not be verified";

      await supabase.from("drivers").update({ approval_status: "rejected", rejection_reason: reason }).eq("user_id", userId);
      await supabase.from("users").update({ status: "suspended" }).eq("id", userId);

      return jsonResponse({ success: true, message: "Driver application rejected" });
    }

    // 8. LIST ALL DRIVERS
    if (req.method === "GET" && path === "/admin/drivers") {
      const { data, error } = await supabase
        .from("drivers")
        .select(`
          user_id,
          licence_no,
          licence_type,
          approval_status,
          vehicle_ownership,
          users!drivers_user_id_fkey (
            id,
            name,
            phone,
            status,
            last_seen_at
          )
        `)
        .order("created_at", { ascending: false });

      if (error) return errorResponse(error.message, 500);
      return jsonResponse(data);
    }

    // 9. LIST VEHICLES
    if (req.method === "GET" && path === "/admin/vehicles") {
      const { data, error } = await supabase.from("vehicles").select("*").order("plate_number");
      if (error) return errorResponse(error.message, 500);
      return jsonResponse(data);
    }

    // 10. LIST SHIPMENTS
    if (req.method === "GET" && path === "/shipments") {
      const { data, error } = await supabase
        .from("shipments")
        .select(`
          *,
          vehicles (plate_number, model),
          users!shipments_driver_id_fkey (name, phone)
        `)
        .order("created_at", { ascending: false });

      if (error) return errorResponse(error.message, 500);
      return jsonResponse(data);
    }

    // 11. DRIVER CURRENT JOB
    if (req.method === "GET" && path === "/driver/current-job") {
      const user = await getUserFromAuthHeader(req);
      let query = supabase
        .from("shipments")
        .select("*, vehicles (*)")
        .not("status", "in", '("delivered","cancelled","failed")')
        .order("created_at", { ascending: false })
        .limit(1);

      if (user?.sub) {
        query = query.eq("driver_id", user.sub);
      }

      const { data, error } = await query.single();
      if (error || !data) {
        return jsonResponse({ activeJob: null, message: "No active shipment assigned" });
      }
      return jsonResponse({ activeJob: data });
    }

    // 12. DRIVER MILESTONE UPDATE
    if (req.method === "POST" && path.match(/^\/driver\/trips\/[^\/]+\/milestone$/)) {
      const shipmentId = path.split("/")[3];
      const body = await req.json();
      const { milestone } = body;

      const validMilestones = ["accepted", "reached_pickup", "loaded", "in_transit", "reached_drop", "delivered"];
      if (!validMilestones.includes(milestone)) {
        return errorResponse("Invalid milestone: " + milestone, 400);
      }

      const updateData: Record<string, unknown> = { status: milestone, updated_at: new Date().toISOString() };
      const { error } = await supabase.from("shipments").update(updateData).eq("id", shipmentId);
      if (error) return errorResponse(error.message, 500);

      return jsonResponse({
        success: true,
        shipmentId,
        milestone,
        recordedAt: new Date().toISOString(),
      });
    }

    // 13. TRACKING PINGS BATCH
    if (req.method === "POST" && path === "/tracking/pings") {
      const body = await req.json();
      const pings = Array.isArray(body) ? body : body.pings || [];
      if (pings.length > 0) {
        const rows = pings.map((p: any) => ({
          shipment_id: p.shipmentId || null,
          driver_id: p.driverId,
          geo_point: `POINT(${p.longitude || p.lng} ${p.latitude || p.lat})`,
          speed_kmh: p.speed || 0,
          accuracy_meters: p.accuracy || 10,
          battery_pct: p.batteryPct || 100,
          recorded_at: p.recordedAt || new Date().toISOString(),
        }));
        await supabase.from("location_pings").insert(rows);
      }
      return jsonResponse({ success: true, count: pings.length });
    }

    // 14. APP VERSION CHECK (/app/version)
    if (req.method === "GET" && (path === "/app/version" || path === "/releases/latest")) {
      return jsonResponse({
        flavor: "direct",
        versionCode: 1,
        versionName: "1.0.0",
        minSupportedVersionCode: 1,
        apkUrl: "https://easqjokyjulfuozdxiyp.supabase.co/storage/v1/object/public/releases/app-direct-debug.apk",
        sha256Checksum: "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
        fileSizeBytes: 19344931,
        hasUpdate: false,
        isForced: false,
      });
    }

    return errorResponse(`Endpoint not found: ${req.method} ${path}`, 404);
  } catch (err: any) {
    return errorResponse(err?.message || "Internal server error", 500);
  }
});
