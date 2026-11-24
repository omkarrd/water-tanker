# Water Tanker Tracking App

Real-time fleet management for water tanker businesses — Life360 for tankers.

## Documentation

- [Requirements & Phase Plan](docs/requirements-phase-plan.md) — Core requirements, 5-phase build plan, tech stack, database schema
- [Future Ideas & Roadmap](docs/future-ideas-roadmap.md) — 40+ feature ideas, ICE prioritization, phase extensions

## Tech Stack (Planned)

| Layer | Technology |
|-------|------------|
| Mobile App | React Native (Expo) + TypeScript |
| Web Admin | Next.js 14 + TypeScript |
| API | Fastify + TypeScript |
| Database | PostgreSQL + TimescaleDB |
| Realtime | Redis + Socket.io |
| Maps | Mapbox GL JS / SDK |
| Auth | Clerk |
| Push | Expo Push / FCM |

## Project Structure

```
water-tanker-tracker/
├── apps/
│   ├── mobile/          # Expo React Native (Driver App)
│   ├── web/             # Next.js (Admin + Customer Portal)
│   └── api/             # Fastify API Server
├── packages/
│   ├── shared/          # Types, Zod schemas, constants
│   ├── ui/              # Shared React components
│   ├── map/             # Mapbox wrapper components
│   └── config/          # Tooling configs
├── docs/
│   ├── requirements-phase-plan.md
│   └── future-ideas-roadmap.md
└── turbo.json
```

## Getting Started

```bash
# Install dependencies (when monorepo is scaffolded)
npm install

# Start development (when apps exist)
npm run dev
```

## Phase 1 MVP (Weeks 1-5)

- [ ] Driver mobile app with "On Duty" toggle + background location
- [ ] Live fleet map in admin dashboard
- [ ] Geofence alerts (source, depot, customer sites)
- [ ] Trip history & basic reports

## License

Private — for internal use only.
