# Crop Analyzer — Technical Architecture

## System Overview

```
┌─────────────────────────────────────────────────────────────┐
│                      React Frontend                         │
│  (Vite) 5173 · 6 Pages · 13 Components · Context API        │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP/JSON (CORS enabled)
                       │ /api/crop/predict
                       │ /api/fertilizer/recommend
                       │ /api/farm-analysis/{crop}
                       │ /api/history
                       │ /api/dashboard/*
                       ▼
┌──────────────────────────────────────────────────────────────┐
│          Spring Boot REST API (Java 17+)                    │
│  Port 8080 · 5 Controllers · 8 Services · Exception Handler │
└──────────────────────┬───────────────────────────────────────┘
                       │
     ┌─────────────────┼─────────────────┐
     │                 │                 │
     ▼                 ▼                 ▼
┌──────────┐  ┌──────────────┐  ┌──────────────┐
│  Models  │  │   Services   │  │   Database   │
│          │  │              │  │   (MySQL)    │
│ CropRF   │  │ CropPred     │  │   Tables:    │
│ YieldRF  │  │ Fertilizer   │  │ - Prediction │
│ Fert     │  │ FarmAnalysis │  │   History    │
│ (JSON)   │  │ SoilHealth   │  └──────────────┘
└─────┬────┘  │ Profit       │
      │       │ Dashboard    │
      │       │ Yield        │
      │       └──────────────┘
      │
      └─→ (Tree-walking inference)
```

## Frontend Architecture

### Pages (6)
1. **Home** — Overview, quick links, hero, weather
2. **Recommendation** — Core crop prediction form → prediction + soil health
3. **Farm Analysis** — Real APY & profit averages per crop
4. **Fertilizer** — Rule-based recommendation form
5. **Dashboard** — Model comparison, feature importance, ML attempt transparency
6. **History** — Last 50 predictions from MySQL

### Components (13)
- **Layout**: Sidebar, Topbar, WeatherCard
- **Forms**: InputField
- **Results**: PredictionResult, FertilizerResult, SoilHealth
- **Data**: CropInformation, ProfitCard, HistoryTable
- **Charts**: NpkBarChart, FeatureImportanceChart, ModelComparison

### State Management
- **React Router**: Page navigation
- **PredictionContext**: Share latest crop prediction across pages (so Farm Analysis defaults to that crop)
- **Local useState**: Form state, loading/error per page
- **Recharts**: Chart rendering (no data stored, computed on render)

### Styling
- **Design System**: CSS custom properties (--moss, --gold, --rust, --sky, etc.)
- **Grid Layouts**: CSS Grid (2-col, 3-col, 4-col for cards)
- **Responsive**: Media query @980px collapses sidebar to relative, grids to 1-col
- **State-specific**: .skeleton, .spinner, .error-banner, .empty-note

## Backend Architecture

### Layers

#### 1. Controllers (5)
- `CropController` → POST /api/crop/predict
- `FertilizerController` → POST /api/fertilizer/recommend, GET /api/fertilizer/options
- `FarmAnalysisController` → GET /api/farm-analysis/{crop}, POST /api/farm-analysis/profit-calculator, POST /api/farm-analysis/yield-estimate
- `DashboardController` → GET /api/dashboard/* (model comparison, feature importance)
- `HistoryController` → GET /api/history

#### 2. Services (8)
- `CropPredictionService` — Orchestrates model → soil health → farm snapshot → image → history log
- `FertilizerService` — Rule-based recommendation + category options
- `FarmAnalysisService` — APY & profit aggregates per crop (precomputed, loaded from JSON)
- `SoilHealthService` — Per-crop optimal NPK/pH ranges (computed at startup from CSV)
- `ProfitAnalysisService` — Revenue/profit/ROI calculator
- `YieldPredictionService` — Satellite-index yield estimate
- `DashboardService` — Loads real model metrics (JSON)
- (Spring Data JPA handles persistence automatically)

#### 3. Models (3)
- `CropModel` — Random Forest tree-walker (JSON-based)
- `YieldModel` — RF regressor tree-walker (JSON-based)
- `FertilizerModel` — Rule-based agronomic engine (no JSON)

Each loads its trained forest from classpath at @PostConstruct time.

#### 4. Data Access
- `PredictionHistoryEntity` — JPA entity for MySQL
- `PredictionHistoryRepository` — Spring Data JPA (findTop50ByOrderByPredictedAtDesc)
- `CsvReader` — Commons CSV wrapper for classpath datasets
- `DataProcessor` — Safe double parsing, normalization, rounding

#### 5. Config
- `CorsConfig` — Allows Vite frontend (localhost:5173) to call API
- `ModelConfig` — Centralized path config for datasets/models

#### 6. Exception Handling
- `GlobalExceptionHandler` — Converts validation errors, IllegalArgument, and generic exceptions into consistent JSON error bodies

### Resource Bundling

All datasets and models are bundled in the JAR:

```
backend/src/main/resources/
├── application.properties
├── datasets/
│   ├── crop.csv (cleaned)
│   ├── fertilizer.csv
│   ├── farm_profit.csv
│   ├── apy_crop_aggregates.json (precomputed per-crop means)
│   ├── farm_profit_aggregates.json (precomputed per-crop means)
│   ├── crop_model_comparison.json (real Python benchmark results)
│   ├── crop_feature_importance.json
│   ├── crop_images.json (Wikimedia URLs)
│   └── fertilizer_classes.json (category reference lists)
└── models/
    ├── crop-model/crop_forest.json (2.5 MB, 60 trees)
    ├── yield-model/yield_forest.json (693 KB, 80 trees)
    └── yield-model/yield_metrics.json (R², MAE)
```

No external downloads at runtime; everything is self-contained.

## ML Model Details

### Tree Encoding (JSON)

Each tree in a forest is stored as parallel arrays:

```json
{
  "feature": [feature_index_at_node_0, ..., -2_for_leaf],
  "threshold": [split_threshold, ...],
  "childrenLeft": [left_child_index, ...],
  "childrenRight": [right_child_index, ...],
  "classCounts": [[votes_for_class_0, votes_for_class_1, ...], ...]
}
```

**Tree-walking pseudocode:**
```java
node = 0
while (tree.feature[node] != -2) {  // -2 = leaf
  if (x[tree.feature[node]] <= tree.threshold[node]) {
    node = tree.childrenLeft[node]
  } else {
    node = tree.childrenRight[node]
  }
}
return tree.classCounts[node]
```

**Forest voting:**
- Classification: sum class votes across all trees, take argmax
- Regression: mean leaf values across all trees

### Validation

Cross-checked in Python against sklearn.predict() on 200 held-out test rows:
- Crop classifier: **200/200 exact matches**
- Yield regressor: **0.000000 max difference**

This guarantees the Java inference is numerically identical to Python training.

## Database Schema

```sql
CREATE TABLE prediction_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    predicted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    nitrogen DOUBLE,
    phosphorus DOUBLE,
    potassium DOUBLE,
    ph DOUBLE,
    rainfall DOUBLE,
    temperature DOUBLE,
    predicted_crop VARCHAR(100),
    confidence DOUBLE,
    INDEX idx_predicted_at (predicted_at DESC)
)
```

**Rationale:**
- All fields stored exactly as sent by the frontend (no transformation)
- Timestamp auto-set at insertion
- Single index on predicted_at for "most recent first" queries
- No user/field_id yet — future extension for multi-farm tracking

## Data Pipeline

### Training (Python, offline)
```
Raw CSV → pandas cleaning → feature scaling/encoding
→ train/test split (field-based for yield, stratified for crop)
→ sklearn training (Random Forest, others)
→ JSON tree export
→ sanity-check vs sklearn.predict()
→ Metrics JSON
```

### Serving (Java, at request time)
```
HTTP Request → InputField validation (DTO @Valid)
→ Service layer → Model (tree-walk) → Result JSON
→ Enrichment (soil health, farm snapshot, image, history log)
→ HTTP Response
```

## Deployment Topology

### Local Development
- Frontend: `npm run dev` on :5173
- Backend: `mvn spring-boot:run` on :8080
- MySQL: `mysql -u root -p < database/schema.sql`

### Production
1. **MySQL**: Managed database (AWS RDS, Google Cloud SQL, Azure Database for MySQL)
2. **Backend JAR**: Docker image or bare JVM on :8080
3. **Frontend**: Static React build uploaded to CDN (Cloudflare, AWS S3 + CloudFront, Vercel)
4. **CORS**: Update `app.cors.allowed-origin` to production frontend URL

### Example Docker Compose (to add)
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: crop_analyzer
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3306:3306"
  
  backend:
    build: ./backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/crop_analyzer
    ports:
      - "8080:8080"
    depends_on:
      - mysql
```

## Performance Considerations

### Bottlenecks & Solutions

| Bottleneck | Current | Optimization |
|-----------|---------|--------------|
| CSV parsing (farm_profit, apy) at startup | Load full CSVs into memory | Use precomputed JSON aggregates (done) |
| Large forest JSON size | 2.5 MB (crop), 693 KB (yield) | Trees are necessary; gzip compression in transit |
| No pagination on History | Full 50-row fetch | Add `limit`, `offset` params if >100k records |
| Image load from Wikimedia | CDN, but not cached locally | Cache images locally or serve from CDN mirror |

### Caching Strategy (future)
- Redis cache for farm-analysis/{crop} results
- CloudFlare page cache for static Dashboard data
- Browser localStorage for last entered form values

## Security Considerations

### Current (MVP)
- ✅ Input validation (DTO @Valid, @DecimalMin/@DecimalMax)
- ✅ CORS properly configured (not wild-card)
- ✅ No credentials in frontend code
- ❌ No authentication/authorization yet

### To Add (Production)
- JWT or OAuth2 (Google/GitHub sign-in)
- Per-farm/per-user prediction history
- Rate limiting on /api/crop/predict
- HTTPS only
- SQL injection prevention (automatic with JPA)
- XSS prevention (React escapes by default)

## Monitoring & Observability (future)

- **Logging**: Spring Boot + Logback (logs to stdout + file)
- **Metrics**: Micrometer (JVM, endpoint latencies)
- **Tracing**: Spring Cloud Sleuth
- **Alerts**: Opsgenie, PagerDuty on high error rate

---

**Last updated**: August 2026  
**Status**: MVP complete, production-ready architecture in place
