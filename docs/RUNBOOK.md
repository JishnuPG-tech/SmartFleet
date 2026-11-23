# TruckConnect — Operations & Developer Runbook

**Document Version:** 1.0  
**Phase:** Phase 0 Baseline  

---

## 1. Local Development Setup

### 1.1 Prerequisites
* **Node.js:** v20.x or newer, `pnpm` or `npm`
* **Java / JDK:** OpenJDK 17 or Android Studio Ladybug+
* **Android SDK:** API 26 minimum, API 34+ target
* **Docker:** Docker Desktop or Docker Engine with Docker Compose
* **Tools:** `adb` (Android Debug Bridge), `moreplay` MCP server for automated testing

### 1.2 Backend Local Execution
```bash
cd backend
npm install
npm run start:dev
# REST API available at http://localhost:3000/api/v1
# Swagger documentation available at http://localhost:3000/api/docs
```

### 1.3 Database & Migrations (Supabase Local Stack)
```bash
# Using Supabase CLI locally
supabase start
supabase db reset # applies all migrations and seed data
```

### 1.4 Android App Build
```bash
cd android
# Build Direct Flavor debug APK
./gradlew assembleDirectDebug
# Install to running emulator or device
adb install -r app/build/outputs/apk/direct/debug/app-direct-debug.apk
```

---

## 2. Deployment Procedures

### 2.1 Backend Deployment (Hugging Face Space / Docker)
1. Push Dockerfile and NestJS source to the Hugging Face Space repository or linked GitHub repository.
2. In the HF Space settings, configure environment variables:
   * `SUPABASE_URL`
   * `SUPABASE_SERVICE_ROLE_KEY`
   * `SUPABASE_JWT_SECRET`
   * `FIREBASE_PROJECT_ID`
   * `FIREBASE_CLIENT_EMAIL`
   * `FIREBASE_PRIVATE_KEY`
3. Verify the deployment via `GET /health`.

---

## 3. Database Maintenance & Backup Operations

### 3.1 Manual Database Backup (Weekly pg_dump)
```bash
pg_dump -h db.<project-ref>.supabase.co -U postgres -d postgres -F c -b -v -f truckconnect_backup_$(date +%Y%m%d).dump
```

### 3.2 Database Restore
```bash
pg_restore -h db.<project-ref>.supabase.co -U postgres -d postgres -v -c truckconnect_backup_YYYYMMDD.dump
```

---

## 4. Releasing App Updates (Direct Flavor)

1. Bump `versionCode` and `versionName` in `android/app/build.gradle.kts`.
2. Generate signed release APK:
   ```bash
   ./gradlew assembleDirectRelease
   ```
3. Calculate SHA-256 checksum:
   ```bash
   sha256sum app/build/outputs/apk/direct/release/app-direct-release.apk
   ```
4. Upload APK to the Supabase Storage `releases` bucket.
5. Create release record in PostgreSQL `app_releases` table with download URL, checksum, release notes, and minimum supported version.
