# Crop Analyzer — Smart Farming AI Platform

A full-stack AI-powered agricultural recommendation system for precision farming. Predict the best crop to plant, get fertilizer recommendations, analyze farm profitability, and track historical predictions — all backed by real ML models trained on 345K+ government crop records and validated against 18,000+ soil samples.

## Quick Start
## Prerequisites

- Java 21
- Maven 3.9+
- Node.js 18+
- npm
- MySQL 8.0+

### 1. Database Setup
```bash
mysql -u root -p < database/schema.sql
```

Edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/crop_analyzer
spring.datasource.username=root
```

### 2. Start Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
# Runs on http://localhost:8080
```

### 3. Start Frontend
```bash
cd frontend
npm install
npm run dev
# Runs on http://localhost:5173
```

Open ``http://localhost:5173`` in your browser.

---

## Features

### 🌾 Crop Recommendation
Input soil NPK/pH and climate (rainfall/temperature) → get the best crop via a Random Forest classifier (94% accuracy, 40 crop classes). Real soil health scoring computed against each crop's actual 25th–75th percentile range from the training data.

### 🧪 Fertilizer Guidance
Rule-based agronomic engine (the raw dataset has no ML signal) recommends one of 7 real formulations (Urea, DAP, balanced blends) based on N/P/K deficits. Includes usage notes with approximate compositions.

### 📊 Farm Analysis
Real historical APY (government crop statistics) and farm-profit averages for your predicted crop. For crops not in those datasets, fields show "not available" rather than invented numbers.

### 💰 Profit Calculator
Ad-hoc calculator: enter area, expected yield, market price, and costs → see revenue, net profit, and ROI. Formulas verified against the farm-profit dataset.

### 📈 Dashboard
Real metrics from the actual Python training run: 5-model comparison (Logistic Regression, SVM, Decision Tree, Random Forest, Gradient Boosting), feature importances, and a transparent explanation of why fertilizer recommendations are rule-based (ML attempt scored 14%, no better than random guessing).

### 🕐 History
Browse your last 50 crop predictions with timestamps, soil readings, and confidence scores — all stored in MySQL.

---

## Architecture

### Tech Stack
| Layer | Technology |
|-------|------------|
| Frontend | React 18, React Router, Recharts |
| Backend | Java 17, Spring Boot 3.3, Spring Data JPA |
| ML Serving | Custom JSON tree-walker (no external ML library) |
| Database | MySQL 8, Hibernate |
| Build | Maven (backend), Vite (frontend) |

### Data Flow
```
React UI (http://localhost:5173)
    ↓ REST/JSON
Spring Boot API (http://localhost:8080/api)
    ↓
Service Layer (Crop, Fertilizer, Farm Analysis, etc.)
    ↓
Random Forest Tree-Walkers (JSON-based inference)
    ↓
MySQL (prediction_history)
    ↓
CSV Datasets (crop.csv, fertilizer.csv, etc., bundled in JAR)
```

### ML Model Details

#### Crop Classifier
- **Type**: Random Forest (60 trees, max depth 10)
- **Training Data**: 7,165 rows (after removing duplicates), 40 crop classes
- **Features**: Nitrogen, Phosphorus, Potassium, pH, Rainfall, Temperature
- **Performance**: 94.00% accuracy, 0.9211 F1 score (weighted)
- **Export**: JSON tree structure, verified exact match vs sklearn.predict()

#### Yield Regressor
- **Type**: Random Forest (80 trees, max depth 8)
- **Training Data**: 90 unique fields, field-based train/test split (no leakage)
- **Features**: NDVI, GNDVI, NDWI, SAVI, soil moisture, temperature, rainfall
- **Performance**: R² = 0.876, MAE = 1.39 tons/hectare
- **Not yet wired to UI** — ready for future satellite-imagery integration

#### Fertilizer Engine
- **Type**: Rule-based (no ML)
- **Reason**: Fertilizer_Prediction.csv (100K rows) showed zero learnable signal — every classifier scored ~14% (random baseline). Benchmark documented in `/ml/model-results/fertilizer_ml_attempt_metrics.json`.
- **Logic**: Measure N/P/K vs. healthy range (90, 45, 45 mg/kg defaults) → recommend the formulation that best corrects the largest deficit(s)

---

## Dataset Coverage

### Crop Dataset (Train_Dataset.csv)
- 40 crops including millets (ragi/finger millet, jowar/sorghum)
- 18,079 raw rows → 7,165 deduplicated
- 7 features: N, P, K, pH, rainfall, temperature

### APY Historical Data (APY.csv)
- 345,336 government records spanning multiple years and states
- 23 of 40 crop-model crops matched to real historical yields
- For unmatched crops, Farm Analysis shows "not available"

### Farm Profit Dataset
- 10,000 records across 6 crops (Cotton, Maize, Potato, Rice, Sugarcane, Wheat)
- Cost breakdowns: seed, fertilizer, pesticide, labor, irrigation
- Revenue, net profit, ROI calculated and verified

### Yield (Satellite) Dataset
- 1,625 rows from 90 unique fields
- Vegetation indices (NDVI, GNDVI, NDWI, SAVI) + weather + satellite yield labels

---

## Project Structure

```
crop-analyzer/
├── frontend/
│   ├── src/
│   │   ├── components/       (13 reusable UI components)
│   │   ├── pages/            (6 full-page views: Home, Recommendation, etc.)
│   │   ├── context/          (PredictionContext for cross-page state)
│   │   ├── services/         (api.js — REST client)
│   │   ├── hooks/            (usePrediction — loading/error state)
│   │   ├── utils/            (formatters — date, INR, percent)
│   │   ├── index.css         (complete design system, faithful to mockup)
│   │   └── App.jsx, main.jsx
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
├── backend/
│   ├── src/main/java/com/smartfarming/ai/
│   │   ├── controller/       (5 REST controllers)
│   │   ├── service/          (8 services: Crop, Farm, Fertilizer, etc.)
│   │   ├── model/            (3 ML models: CropModel, YieldModel, FertilizerModel)
│   │   ├── entity/           (PredictionHistory JPA entity)
│   │   ├── repository/       (Spring Data Repository)
│   │   ├── dto/              (Request/Response DTOs with validation)
│   │   ├── config/           (CORS, ModelConfig, paths)
│   │   ├── exception/        (GlobalExceptionHandler)
│   │   ├── util/             (CsvReader, DataProcessor)
│   │   └── SmartFarmingApplication.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── datasets/         (crop.csv, fertilizer.csv, farm_profit.csv, agg JSON)
│   │   └── models/           (crop_forest.json, yield_forest.json)
│   ├── pom.xml
│   └── [no test/ directory yet — add as needed]
│
├── database/
│   └── schema.sql            (MySQL table + sample data)
│
├── ml/
│   ├── datasets/
│   │   ├── raw/              (original untouched CSVs)
│   │   └── processed/        (cleaned, deduplicated CSVs)
│   ├── notebooks/            (Python training scripts: export_final.py, etc.)
│   └── model-results/        (JSON metrics, feature importance, model comparison)
│
├── docs/
│   └── [README.md, architecture diagrams, API docs — add as needed]
│
└── [.gitignore, LICENSE, etc.]
```

---

## Key Design Decisions

### 1. No External ML Library Dependency
Smile/Tribuo couldn't be compiled and verified in the build environment, so models are trained in Python and exported as plain JSON tree structures. A small dependency-free Java class walks them via standard feature/threshold comparisons. This guarantees the deployed model is **numerically identical** to the training run (verified 200/200 exact matches).

### 2. Rule-Based Fertilizer (Not ML)
The fertilizer dataset has zero learnable signal. Rather than ship a 14% accuracy classifier and pretend it's AI, the recommendation engine uses transparent agronomic logic: compare soil NPK to a healthy baseline and suggest the formulation that best corrects the deficit. This is more useful and honest.

### 3. Field-Based Yield Validation
Standard random train/test split on the yield dataset would leak the same field into both sets, inflating performance. Instead, we split by unique field_id (72 train, 18 test) for realistic evaluation. R² = 0.876 reflects honest performance.

### 4. Data-First Development
Before writing any backend code, all 5 datasets were inspected in Python to catch real-world issues: no humidity in the crop dataset (removed from UI), crop name mismatches between datasets (built synonym mapping), fertilizer ML failure (documented transparently). This prevented architectural misfires later.

### 5. Honest Data Availability
For crops/metrics not in a given dataset, the UI shows "not available" rather than fallback/interpolated values. Better to acknowledge gaps than invent numbers.

---

## Running Tests

### Backend Unit Tests (to implement)
```bash
cd backend
mvn test
```

### Manual API Testing (Postman)
1. Import `Crop Prediction`:
   ```json
   POST http://localhost:8080/api/crop/predict
   {
     "nitrogen": 50, "phosphorus": 50, "potassium": 50,
     "ph": 6.5, "rainfall": 100, "temperature": 25
   }
   ```

2. Fetch Fertilizer Options:
   ```
   GET http://localhost:8080/api/fertilizer/options
   ```

3. View History:
   ```
   GET http://localhost:8080/api/history
   ```

---

## Deployment

### Production Build (Frontend)
```bash
cd frontend
npm run build
# Creates dist/ folder, deploy to any static host or CDN
```

### Production Build (Backend)
```bash
cd backend
mvn clean package -DskipTests
# Creates target/crop-analyzer.jar
java -jar target/crop-analyzer.jar
```

### Docker (optional, to add)
Provide `Dockerfile` and `docker-compose.yml` for containerized deployment.

---

## Future Enhancements

- [ ] Live weather API integration (OpenWeatherMap, weatherapi.com)
- [ ] Satellite imagery ingestion for yield predictions
- [ ] Pest/disease prediction models
- [ ] Market price forecasting
- [ ] Multi-language support (Hindi, Tamil, Telugu)
- [ ] Mobile app (React Native)
- [ ] Farmer authentication & per-farm history
- [ ] Advisory email/SMS alerts
- [ ] Integration with government subsidy schemes (e-NAM, AIBP)

---

## License
MIT License — see LICENSE file.

## Author
**B Karthik** (karthik7399@gmail.com)  
GitHub: [karthikb-dev](https://github.com/karthik10)

---

## Support & Contribution
If you find bugs, have feature requests, or want to improve the models, please open an issue or submit a pull request. All contributions welcome!

---

**Built 2026** — Empowering precision agriculture with AI & open data. 🌱
