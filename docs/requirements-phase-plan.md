# Water Tanker Tracking App — Requirements & Phase-by-Phase Plan

**Version:** 1.0  
**Date:** 2026-11-24  
**Author:** Ommya & Claude Code  
**Status:** Draft for Review

---

## 1. Project Overview

### 1.1 Vision Statement
Build a **Life360-style real-time tracking platform** for water tanker businesses — giving owners live visibility of every tanker, automated trip logging, geofence alerts, driver behavior monitoring, and a professional admin dashboard.

### 1.2 Problem Statement
Water tanker operators lose revenue from:
- **Unknown tanker locations** — can't dispatch efficiently
- **Unlogged "ghost trips"** — drivers selling water off-books
- **Fuel theft & misuse** — no accountability on routes
- **Customer complaints** — "Where is my tanker?" calls
- **No trip history** — can't audit, bill, or optimize

### 1.3 Target Users
| User | Role | Primary Needs |
|------|------|---------------|
| **Business Owner** (your friend) | Admin / Dispatcher | Live map, trip reports, alerts, driver management, billing data |
| **Drivers** | Mobile App Users | Simple "On Duty/Off Duty" toggle, navigation to pickup/drop, job assignments |
| **Customers** (future) | Web Link Recipients | Track their specific delivery via shareable link |

### 1.4 Success Metrics (MVP)
- All tankers visible on map within **5 seconds** of app open
- **Zero** unlogged trips after 30 days
- **< 30 seconds** for owner to answer "Where is Tanker X?"
- Driver adoption: **100%** of active drivers using app daily

---

## 2. Core Features (Life360-Inspired + Business Extensions)

### 2.1 Real-Time Location (Core)
| Feature | Life360 Equivalent | Business Adaptation |
|---------|-------------------|---------------------|
| Live map with all tankers | Family circle map | Fleet map with tanker IDs, status badges |
| Background GPS (phone) | Always-on sharing | "On Duty" mode only — privacy-respecting |
| Location accuracy indicator | Signal strength | GPS accuracy + last update timestamp |
| Offline queue & sync | N/A | Store locally, sync when online |

### 2.2 Geofences & Places (Core)
| Feature | Life360 Equivalent | Business Adaptation |
|---------|-------------------|---------------------|
| Saved places (home/school) | Places | **Water Source**, **Depot**, **Customer Sites** |
| Arrival/Departure alerts | Place alerts | **"Tanker 3 reached Customer Site A"** |
| Dwell time tracking | N/A | **Loading time**, **Unloading time**, **Total stop duration** |
| Geofence radius config | Fixed radius | Per-place custom radius (e.g., 50m for narrow lanes) |

### 2.3 Trip History & Logging (Core)
| Feature | Life360 Equivalent | Business Adaptation |
|---------|-------------------|---------------------|
| Location history timeline | 30-day history | **Full trip timeline**: pickup → transit → delivery → return |
| Trip summary (distance, time) | Driving summary | **Job card**: Customer, Quantity (liters), Source, Destination, Duration |
| Export / reports | Weekly email | **PDF/Excel daily/weekly/monthly reports** for billing & audit |

### 2.4 Driver Behavior & Safety (Phase 2)
| Feature | Life360 Equivalent | Business Value |
|---------|-------------------|----------------|
| Speed alerts | Driving reports | Reduce fuel, tire wear, accidents |
| Hard braking / acceleration | Phone use detection | Maintenance savings, safety |
| Route deviation alerts | N/A | Detect unauthorized detours |
| Idle time tracking | N/A | Fuel waste reduction |

### 2.5 Admin Dashboard (Core)
- **Live Fleet Map** — all tankers, filterable by status
- **Tanker Detail Panel** — current job, location, battery, signal
- **Job Board** — create/assign deliveries (Phase 2)
- **Reports Center** — trip logs, fuel estimates, driver scorecards
- **Settings** — geofences, alerts, driver accounts, business profile

### 2.6 Driver Mobile App (Core)
- **On Duty / Off Duty** toggle (big button)
- **Assigned Jobs List** — pickup & drop addresses, quantity, customer name
- **Navigation** — one-tap "Navigate to Pickup" / "Navigate to Drop" (opens Google Maps/Waze)
- **Job Actions** — "Start Loading", "Finish Loading", "Start Delivery", "Complete Delivery"
- **Profile** — name, phone, vehicle number, license expiry

### 2.7 Customer Tracking Link (Phase 2)
- Shareable **public link** per delivery (expires on completion)
- Shows: tanker live location, ETA, driver name/phone
- No login required — works in browser

---

## 3. Technical Architecture

### 3.1 High-Level Architecture
```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Driver App     │     │  Admin Web App  │     │  Customer Link  │
│  (React Native) │     │  (React/Next.js)│     │  (React/Next.js)│
└────────┬────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │   API Gateway / BFF     │
                    │   (Node.js / Go / etc.) │
                    └────────────┬────────────┘
                                 │
         ┌───────────────────────┼───────────────────────┐
         │                       │                       │
┌────────▼────────┐   ┌─────────▼─────────┐   ┌────────▼────────┐
│  Location       │   │  Trip/Job         │   │  Auth/User      │
│  Service        │   │  Service          │   │  Service        │
│  (WebSocket +   │   │  (REST + DB)      │   │  (JWT + Roles)  │
│  Redis + TS)    │   │                   │   │                 │
└─────────────────┘   └───────────────────┘   └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │   PostgreSQL +          │
                    │   TimescaleDB / Redis   │
                    └─────────────────────────┘
```

### 3.2 Technology Stack Recommendations

| Layer | Recommendation | Rationale |
|-------|---------------|-----------|
| **Mobile App** | React Native (Expo) | Single codebase iOS/Android, fast dev, GPS background support |
| **Web Admin** | Next.js 14+ (App Router) | SSR for SEO (customer links), great DX, Vercel deploy |
| **API** | Node.js (Fastify) or Go (Gin) | WebSocket support, performance, type safety |
| **Real-time** | Socket.io or native WS + Redis Pub/Sub | Scale to 1000+ concurrent connections |
| **Database** | PostgreSQL + **TimescaleDB** extension | Time-series location data, automatic partitioning |
| **Cache/Queue** | Redis | Pub/Sub for location, session cache, job queues |
| **Maps** | Mapbox GL JS (web) / Mapbox SDK (mobile) | Better performance & styling than Google Maps |
| **Auth** | Clerk or Auth0 (managed) or custom JWT | Social login, MFA, role-based access |
| **Push Notifications** | Expo Push / Firebase FCM | Cross-platform, reliable |
| **Hosting** | Railway / Render / AWS ECS | Simple container deploy, managed DB |
| **Monitoring** | Sentry + Logtail / Datadog | Error tracking, logs, uptime |

### 3.3 Database Schema (PostgreSQL + TimescaleDB)

```sql
-- Enable TimescaleDB
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- Users & Roles
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE,
    password_hash VARCHAR(255),
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('owner', 'dispatcher', 'driver')),
    business_id UUID REFERENCES businesses(id),
    avatar_url TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- Business / Organization
CREATE TABLE businesses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    owner_id UUID REFERENCES users(id),
    address TEXT,
    phone VARCHAR(20),
    gst_number VARCHAR(20),
    settings JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Tankers (Vehicles)
CREATE TABLE tankers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID REFERENCES businesses(id) ON DELETE CASCADE,
    registration_number VARCHAR(30) UNIQUE NOT NULL,
    capacity_liters INTEGER NOT NULL,
    driver_id UUID REFERENCES users(id) ON DELETE SET NULL,
    make_model VARCHAR(100),
    year INTEGER,
    gps_device_id VARCHAR(100), -- for hardware trackers later
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Geofences / Places
CREATE TABLE geofences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID REFERENCES businesses(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) CHECK (type IN ('source', 'depot', 'customer', 'fuel', 'maintenance')),
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    radius_meters INTEGER DEFAULT 100,
    address TEXT,
    contact_name VARCHAR(100),
    contact_phone VARCHAR(20),
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Jobs / Deliveries
CREATE TABLE jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID REFERENCES businesses(id) ON DELETE CASCADE,
    tanker_id UUID REFERENCES tankers(id) ON DELETE SET NULL,
    driver_id UUID REFERENCES users(id) ON DELETE SET NULL,
    source_geofence_id UUID REFERENCES geofences(id),
    destination_geofence_id UUID REFERENCES geofences(id),
    customer_name VARCHAR(200),
    customer_phone VARCHAR(20),
    customer_address TEXT,
    quantity_liters INTEGER NOT NULL,
    price_per_liter DECIMAL(10,2),
    status VARCHAR(30) DEFAULT 'pending' CHECK (status IN (
        'pending', 'assigned', 'en_route_to_source', 'loading', 
        'en_route_to_destination', 'delivering', 'completed', 'cancelled'
    )),
    scheduled_at TIMESTAMPTZ,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- Location Points (TimescaleDB Hypertable)
CREATE TABLE location_points (
    time TIMESTAMPTZ NOT NULL,
    tanker_id UUID REFERENCES tankers(id) ON DELETE CASCADE,
    driver_id UUID REFERENCES users(id) ON DELETE SET NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    accuracy_meters DOUBLE PRECISION,
    altitude_meters DOUBLE PRECISION,
    speed_kmh DOUBLE PRECISION,
    heading_degrees DOUBLE PRECISION,
    battery_percent INTEGER,
    is_charging BOOLEAN,
    network_type VARCHAR(20), -- wifi, 4g, 5g, offline
    activity_type VARCHAR(20), -- stationary, walking, driving, unknown
    metadata JSONB DEFAULT '{}'
);

-- Convert to hypertable (partitioned by time)
SELECT create_hypertable('location_points', 'time', 
    chunk_time_interval => INTERVAL '1 day',
    if_not_exists => TRUE
);

-- Indexes for common queries
CREATE INDEX idx_location_points_tanker_time ON location_points (tanker_id, time DESC);
CREATE INDEX idx_location_points_driver_time ON location_points (driver_id, time DESC);
CREATE INDEX idx_jobs_tanker_status ON jobs (tanker_id, status);
CREATE INDEX idx_jobs_driver_status ON jobs (driver_id, status);

-- Trip Segments (aggregated from location_points)
CREATE TABLE trips (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tanker_id UUID REFERENCES tankers(id) ON DELETE CASCADE,
    driver_id UUID REFERENCES users(id) ON DELETE SET NULL,
    job_id UUID REFERENCES jobs(id) ON DELETE SET NULL,
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ,
    start_lat DOUBLE PRECISION,
    start_lng DOUBLE PRECISION,
    end_lat DOUBLE PRECISION,
    end_lng DOUBLE PRECISION,
    distance_km DOUBLE PRECISION,
    duration_seconds INTEGER,
    avg_speed_kmh DOUBLE PRECISION,
    max_speed_kmh DOUBLE PRECISION,
    idle_duration_seconds INTEGER DEFAULT 0,
    harsh_events_count INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'in_progress' CHECK (status IN ('in_progress', 'completed', 'archived')),
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Alerts / Notifications
CREATE TABLE alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID REFERENCES businesses(id) ON DELETE CASCADE,
    tanker_id UUID REFERENCES tankers(id) ON DELETE SET NULL,
    driver_id UUID REFERENCES users(id) ON DELETE SET NULL,
    type VARCHAR(50) NOT NULL, -- geofence_enter, geofence_exit, speed, idle, route_deviation, low_battery
    severity VARCHAR(10) DEFAULT 'info' CHECK (severity IN ('info', 'warning', 'critical')),
    title VARCHAR(200) NOT NULL,
    message TEXT,
    metadata JSONB DEFAULT '{}',
    is_read BOOLEAN DEFAULT false,
    is_dismissed BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Customer Tracking Links
CREATE TABLE tracking_links (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id UUID REFERENCES jobs(id) ON DELETE CASCADE,
    token VARCHAR(64) UNIQUE NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    is_active BOOLEAN DEFAULT true,
    view_count INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT now()
);
```

---

## 4. Phase-by-Phase Development Plan

### Phase 0: Foundation (Week 1-2) — **Setup & Infrastructure**
**Goal:** Running dev environment, CI/CD, auth, empty apps deployable

| Task | Owner | Deliverable |
|------|-------|-------------|
| Repo setup (monorepo: `apps/mobile`, `apps/web`, `apps/api`, `packages/shared`) | Dev | GitHub repo with Turbo/Nx |
| PostgreSQL + TimescaleDB (local Docker + Railway/Neon for prod) | Dev | Running DB, migrations working |
| Redis (local + managed) | Dev | Pub/Sub for location |
| Auth integration (Clerk recommended) | Dev | Login, roles, protected routes |
| Mapbox account + tokens | Dev | Map loads in web & mobile |
| CI/CD (GitHub Actions → Railway/Render/Vercel) | Dev | Auto-deploy on merge |
| Shared TypeScript types (API contracts) | Dev | `packages/shared` published |
| Basic UI component library (Tailwind + shadcn/ui) | Dev | Button, Card, Input, Map components |

**Exit Criteria:** Developer can `git push` → both apps deploy → login works → map renders.

---

### Phase 1: Core Tracking MVP (Week 3-5) — **Live Map + Background Location**
**Goal:** Owner sees all tankers live on map; drivers toggle "On Duty" and location streams.

#### 4.1.1 Driver Mobile App (Expo + React Native)
| Feature | Details |
|---------|---------|
| **Auth** | Clerk login → role=driver → redirect to driver screen |
| **On Duty Toggle** | Big central button; when ON: starts background location task |
| **Background Location** | `expo-location` + `expo-task-manager`; foreground service (Android) / background modes (iOS) |
| **Location Config** | 10s interval on duty, 60s off duty; high accuracy; batched send (every 30s) |
| **Offline Queue** | SQLite (WatermelonDB or expo-sqlite) → sync when online |
| **Battery Optimization** | Adaptive: slower updates when stationary (detected via activity) |
| **Driver Profile** | Name, phone, tanker assignment, license expiry reminder |
| **Push Notifications** | "New job assigned", "Duty reminder at 6 AM" |

#### 4.1.2 Location Ingestion API
| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/v1/location/batch` | POST | Receive batched points (array of 10-50) |
| `/api/v1/location/stream` | WS | Real-time push to admin dashboard |
| `GET /api/v1/tankers/:id/location` | GET | Latest known location (fallback) |

**Location Payload (per point):**
```json
{
  "tankerId": "uuid",
  "driverId": "uuid",
  "timestamp": "2026-11-24T10:30:00.000Z",
  "latitude": 12.9716,
  "longitude": 77.5946,
  "accuracy": 5.2,
  "speed": 45.3,
  "heading": 180,
  "battery": 78,
  "isCharging": false,
  "networkType": "4g",
  "activityType": "driving"
}
```

#### 4.1.3 Admin Web Dashboard
| View | Components |
|------|------------|
| **Live Fleet Map** | Mapbox GL + tanker markers (color by status), clustering at zoom < 12 |
| **Tanker List Sidebar** | Searchable, filterable; shows: status, last update, battery, current job |
| **Tanker Detail Panel** | Click marker → slide-over: live trail (last 30 min), job info, actions |
| **Real-time Updates** | Socket.io → marker moves smoothly (interpolated) |

#### 4.1.4 Geofence Engine (Core Logic)
- **In-memory geofence index** (R-tree via `rbush` or PostGIS) for fast point-in-polygon
- **Event types:** `enter`, `exit`, `dwell_complete` (configurable min dwell time)
- **Alert generation** → write to `alerts` table + push notification to owner/dispatcher

**Exit Criteria:** 
- 5 drivers can go "On Duty" → owner sees all 5 moving on map in real-time
- Geofence alerts fire correctly at source/depot
- Location persists after app kill/restart (background task works)
- Battery drain < 5%/hour on duty

---

### Phase 2: Trip Logging & Job Management (Week 6-8) — **Business Workflow**
**Goal:** Complete trip records with customer, quantity, source/destination; job assignment flow.

#### 4.2.1 Job CRUD (Admin)
| Feature | Details |
|---------|---------|
| **Create Job** | Modal: customer, phone, address → geocode → link/create geofence, quantity, price, schedule |
| **Assign Job** | Drag tanker → job, or dropdown; driver gets push notification |
| **Job Status Board** | Kanban: Pending → Assigned → In Progress → Completed |
| **Recurring Jobs** | Daily/weekly templates (e.g., "Hotel X - 5000L every morning") |

#### 4.2.2 Driver Job Flow
| Step | Driver Action | System Action |
|------|---------------|---------------|
| 1. Job Assigned | Push notification | Job appears in "My Jobs" |
| 2. Start → Source | Tap "Navigate to Source" | Opens Maps; status: `en_route_to_source` |
| 3. Arrive Source | Auto (geofence enter) OR manual "Arrived" | Status: `loading`; start dwell timer |
| 4. Finish Loading | Tap "Loading Complete" | Record `loading_duration`; status: `en_route_to_destination` |
| 5. Arrive Destination | Auto (geofence) OR manual | Status: `delivering` |
| 6. Complete Delivery | Tap "Delivered" + optional photo/signature | Status: `completed`; generate trip record |

#### 4.2.3 Trip Auto-Generation
- Background worker: when job completes → aggregate `location_points` → create `trips` record
- Calculate: distance (Haversine or Mapbox Matching API), duration, avg/max speed, idle time
- Link to job for billing

#### 4.2.4 Reports (Admin)
| Report | Filters | Output |
|--------|---------|--------|
| **Daily Trip Sheet** | Date, tanker, driver | PDF: all trips with customer, qty, times, distance |
| **Tanker Utilization** | Date range | % time on duty, km driven, trips count |
| **Driver Scorecard** | Month | Trips, distance, harsh events, on-time % |
| **Revenue Summary** | Month | Total liters, revenue, pending payments |

**Exit Criteria:**
- Owner creates job → assigns → driver completes → trip auto-logged → report generates
- Zero manual entry needed for trip data
- PDF report matches what customer expects for billing

---

### Phase 3: Intelligence & Alerts (Week 9-11) — **Operational Excellence**
**Goal:** Proactive alerts, driver behavior, fuel estimation, route optimization.

#### 4.3.1 Advanced Alerts
| Alert | Trigger | Action |
|-------|---------|--------|
| **Speed Violation** | > 60 km/h for 30s (configurable) | Push to owner + log |
| **Excessive Idling** | Engine on (charging + stationary) > 15 min | Push + fuel waste estimate |
| **Route Deviation** | > 500m from expected polyline | Push + "Call driver" button |
| **Geofence Skip** | Job requires source → destination but source skipped | Critical alert |
| **Low Battery** | Driver phone < 20% while on duty | Push to driver + owner |
| **Offline Too Long** | No location > 10 min on duty | Push to owner |
| **Maintenance Due** | Odometer estimate (from distance) > threshold | Schedule reminder |

#### 4.3.2 Driver Behavior Scoring
- **Inputs:** speed events, harsh braking (accelerometer), idle time, route adherence, on-time %
- **Output:** 0-100 score per driver per week; trend chart
- **Gamification:** "Top Driver" badge, monthly bonus eligibility

#### 4.3.3 Fuel Estimation
- **Formula:** `distance_km × (liters_per_km)` + `idle_hours × idle_consumption_lph`
- **Per-tanker calibration:** Owner enters actual fuel fills → system learns consumption rate
- **Alert:** Estimated fuel vs actual diverges > 15% → possible theft

#### 4.3.4 Route Optimization (Basic)
- **Multi-stop:** For tankers doing multiple drops, suggest optimal order (TSP approximation)
- **Traffic-aware ETA:** Mapbox Matrix API for real-time ETAs to customers

**Exit Criteria:** Owner receives actionable alerts daily; fuel estimates within 10% of reality; driver scores visible.

---

### Phase 4: Customer Experience & Scale (Week 12-14) — **Growth Features**
**Goal:** Customer-facing tracking, multi-business support, hardware tracker integration.

#### 4.4.1 Customer Tracking Links
- **Generate:** Auto-create when job assigned; token = nanoid(32)
- **Page:** Public Next.js route `/track/[token]` — shows tanker marker, ETA, driver contact
- **Expiry:** Auto-expire 2 hours after job completion
- **Branding:** Business logo, colors, WhatsApp share button

#### 4.4.2 Hardware GPS Tracker Integration (Optional)
| Protocol | Implementation |
|----------|----------------|
| **TK103 / GT06** | TCP server parsing NMEA/proprietary → normalize → insert to `location_points` |
| **MQTT Trackers** | Subscribe to topic → transform → ingest |
| **OBD-II Dongles** | Bluetooth → driver app reads → sends (backup) |
| **Unified Ingestion** | Single `/api/v1/ingest/:protocol` endpoint per device type |

#### 4.4.3 Multi-Business (SaaS Ready)
- **Super-admin** panel to manage businesses
- **Subscription plans:** Free (5 tankers), Pro (unlimited + alerts), Enterprise (API + hardware)
- **Billing:** Stripe integration
- **Data isolation:** Row-level security (RLS) on all tables

#### 4.4.4 API for Integrations
- **Webhooks:** Job created/completed, alerts, location updates
- **REST API:** Read tankers, jobs, trips (for ERP/Tally integration)
- **API Keys:** Per-business, scoped permissions

**Exit Criteria:** Customer opens link → sees live tanker → calls driver if needed; hardware tracker feeds data identically to phone app.

---

### Phase 5: Polish & Production Hardening (Week 15-16) — **Launch Ready**
**Goal:** Zero-downtime deploy, observability, security audit, documentation.

| Area | Tasks |
|------|-------|
| **Observability** | Sentry (errors), Logtail (logs), Prometheus/Grafana (metrics), uptime monitoring |
| **Security** | Pen test (OWASP Top 10), rate limiting, input validation, secrets audit, RLS verification |
| **Performance** | Load test 500 concurrent tankers; optimize WS, DB indexes, map tile caching |
| **Reliability** | Chaos test: kill API pod → WS reconnects; DB failover; offline mobile sync |
| **Documentation** | API docs (OpenAPI), mobile build guide, admin user manual, driver onboarding video |
| **App Store** | TestFlight / Play Console internal testing; privacy policy; permissions justification |
| **Backup/DR** | Daily PG dump → S3; point-in-time recovery tested; RTO < 1hr, RPO < 15min |

**Exit Criteria:** Passes security review; handles 2x expected load; documented runbooks; app store approved.

---

## 5. Development Best Practices

### 5.1 Code Organization (Monorepo)
```
water-tanker-tracker/
├── apps/
│   ├── mobile/          # Expo React Native
│   ├── web/             # Next.js Admin + Customer Tracking
│   └── api/             # Fastify/Go API Server
├── packages/
│   ├── shared/          # Types, Zod schemas, constants
│   ├── ui/              # Shared React components (web)
│   ├── map/             # Mapbox wrapper components
│   └── config/          # ESLint, Prettier, Tailwind, TSConfig
├── docker-compose.yml   # Local dev stack
├── turbo.json           # Turborepo config
└── package.json
```

### 5.2 Git Workflow
- **Main branch:** Protected, requires PR + CI pass
- **Feature branches:** `feat/job-assignment`, `fix/geofence-alerts`
- **Commits:** Conventional Commits (`feat:`, `fix:`, `chore:`)
- **Releases:** Semantic versioning via `changesets`

### 5.3 Testing Strategy
| Layer | Tool | Target |
|-------|------|--------|
| Unit | Vitest + React Native Testing Library | Business logic, utils, hooks |
| Integration | Vitest + Supertest | API endpoints, DB transactions |
| E2E | Playwright (web) / Detox (mobile) | Critical flows: login → job → complete |
| Contract | Pact or Zod schema validation | API ↔ Mobile, API ↔ Web |
| Load | k6 | 500 tankers sending location every 10s |

### 5.4 Privacy & Compliance (India Context)
- **Data minimization:** Only collect location when "On Duty"
- **Driver consent:** Explicit in-app consent screen on first duty toggle
- **Data retention:** Location points → 90 days hot (TimescaleDB), 7 years cold (S3 Parquet)
- **Right to deletion:** Admin can purge driver data on request
- **DPDP Act 2023:** Privacy notice, data protection officer contact, breach notification plan

---

## 6. Estimated Effort & Team

| Phase | Duration | Devs | Key Skills |
|-------|----------|------|------------|
| 0: Foundation | 2 weeks | 1-2 | Full-stack, DevOps, DB |
| 1: Core Tracking | 3 weeks | 2 | React Native, WebSockets, Maps |
| 2: Jobs & Trips | 3 weeks | 2 | React, State machines, Reports |
| 3: Intelligence | 3 weeks | 1-2 | Algorithms, Background jobs |
| 4: Customer & Scale | 3 weeks | 2 | Public pages, Hardware protocols |
| 5: Hardening | 2 weeks | 1-2 | Security, Observability, App Store |
| **Total** | **16 weeks (4 months)** | **2-3** | |

**Solo founder path:** 6-8 months part-time, or 3-4 months full-time.

---

## 7. Budget Estimate (Monthly Operational)

| Item | Cost (INR) | Notes |
|------|------------|-------|
| **Hosting (API + DB + Redis)** | ₹8,000 - ₹20,000 | Railway/Render/Neon; scales with tankers |
| **Mapbox** | ₹4,000 - ₹15,000 | 50k-200k map loads/month |
| **Push Notifications (FCM/Expo)** | Free - ₹2,000 | Up to 1M/month free |
| **Auth (Clerk)** | Free - ₹8,000 | Free tier: 10k MAU |
| **Monitoring (Sentry + Logtail)** | ₹3,000 - ₹10,000 | |
| **SMS/WhatsApp (Twilio/Gupshup)** | ₹2,000 - ₹5,000 | Alerts, OTP |
| **App Store Fees** | ₹8,000/yr | Apple $99, Google $25 (one-time) |
| **Domain + SSL** | ₹1,000/yr | |
| **Total/Month** | **₹25,000 - ₹60,000** | For 20-50 tankers |

---

## 8. Risks & Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| **Drivers refuse tracking** | High | High | "On Duty only" policy; incentive (bonus for 100% duty tracking); union discussion upfront |
| **Battery drain complaints** | Medium | Medium | Adaptive intervals; show battery % to owner; allow "Low Power Mode" (30s updates) |
| **GPS accuracy in narrow lanes** | Medium | Medium | Mapbox snap-to-road; geofence radius 50-100m; manual "Arrived" button |
| **Network dead zones** | Medium | High | Offline queue (SQLite) → auto-sync; show "Last seen 12 min ago" |
| **Hardware tracker integration complexity** | Low (Phase 4) | Medium | Start with phone-only; standardize on 1-2 tracker models later |
| **Competitor (Fleetx, LocoNav, etc.)** | High | Medium | Focus on water-tanker specific: quantity, source/destination, customer links |
| **Regulatory (Govt mandates GPS)** | Low | High | Build export formats (CSV/GPX) for compliance reports |

---

## 9. Immediate Next Steps (This Week)

1. **Confirm scope with your friend** — How many tankers? Drivers on smartphones? Budget?
2. **Set up repo & deploy empty apps** — Get "Hello World" on mobile + web + API
3. **Create Mapbox account** — Get tokens, design map style (dark mode for night ops)
4. **Design 3-4 key screens in Figma** — Driver duty toggle, Admin live map, Job create modal
5. **Pick auth provider** — Clerk (easiest) vs custom JWT (more control)
6. **Write first migration** — `businesses`, `users`, `tankers`, `geofences`, `location_points` (hypertable)
7. **Build location ingestion endpoint** — POST batch → TimescaleDB → Redis Pub/Sub → WS broadcast

---

## 10. Appendix: Useful Resources

- **Expo Background Location:** https://docs.expo.dev/versions/latest/sdk/location/#background-location
- **TimescaleDB Hypertables:** https://docs.timescale.com/use-timescale/latest/hypertables/
- **Mapbox GL JS:** https://docs.mapbox.com/mapbox-gl-js/guides/
- **Socket.io Scaling:** https://socket.io/docs/v4/using-multiple-nodes/
- **React Native Background Fetch:** https://github.com/transistorsoft/react-native-background-fetch
- **DPDP Act 2023 Summary:** https://www.meity.gov.in/writereaddata/files/DPDP_Act_2023.pdf

---

**Document Control**
- Save as: `Water Tanker Tracking App Requirements & Phase Plan.md`
- Convert to PDF: `pandoc -o "Water Tanker Tracking App Requirements & Phase Plan.pdf" "Water Tanker Tracking App Requirements & Phase Plan.md" --pdf-engine=weasyprint`
- Review weekly; update as decisions are made

---

*End of Document*