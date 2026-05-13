# SmartCrop Kenya

**SmartCrop Kenya** is an agriculture mobile application designed to empower smallholder farmers with data-driven crop recommendations. By integrating real-time seasonal weather forecasts and localized soil health data, the app suggests the top three most viable crops for a specific subcounty.

## 🚀 The Problem
Agriculture contributes ~33% of Kenya's GDP, yet many farmers lack access to localized soil and weather data. Traditional methods often lead to poor crop selection and low yields. SmartCrop Kenya bridges this gap using Machine Learning and automated data retrieval.

## ✨ Key Features
- **Subcounty-Level Precision:** Searchable database of all 290+ Kenyan subcounties with geometric centroid mapping.
- **Automated Soil Analysis:** Fetches Nitrogen, Phosphorus, Potassium, and pH levels via the **iSDAsoil API**.
- **Seasonal Weather Outlook:** Aggregates 3-month forecasts (Temperature & Rainfall) using the **Open-Meteo API**.
- **AI-Powered Recommendations:** Uses a **Random Forest Classifier** (95%+ accuracy) to rank the top 3 crops with confidence scores.
- **Lightweight Mobile Client:** Offloads heavy processing and API orchestration to a centralized "Smart Hub" backend.

## 🏗️ Architecture
The system uses a **Client-Server-API** architecture:
1. **Android Client (Kotlin):** Handles user input and displays results.
2. **FastAPI Backend (Hugging Face):** Orchestrates data from iSDAsoil and Open-Meteo, then runs the ML model.
3. **ML Model:** Trained on the Kaggle Crop Recommendation dataset.

## 🛠️ Technology Stack
- **Frontend:** Kotlin, Jetpack Compose, Retrofit, OkHttp, KotlinX Serialization.
- **Backend:** Python, FastAPI, Scikit-learn, Joblib, Pandas, Requests.
- **Hosting:** Hugging Face Spaces (Backend), GitHub (Version Control).
- **Data Sources:** iSDAsoil (Africa Soil Data), Open-Meteo (Global Weather), GADM (Geospatial Boundaries).

## 📂 Project Structure
```text
├── android-app/             # Kotlin Jetpack Compose source code
│   └── app/src/main/assets/ # contains locations_dict.json
├── backend/                 # FastAPI source code
│   ├── main.py              # API Orchestrator logic
│   ├── Dockerfile           # Deployment config
│   └── requirements.txt     # Python dependencies
├── ml-research/             # Google Colab notebooks & training scripts
└── data-processing/         # Python scripts for GeoJSON centroid extraction
```

## ⚙️ Installation & Setup
 
### Backend (Hugging Face)
 
1. Create a new **Docker Space** on Hugging Face.
2. Upload the following files:
   - `main.py`
   - `Dockerfile`
   - `requirements.txt`
   - `crop_recommendation_model.joblib`
3. Add the following **Secrets** in Space Settings:
   - `ISDA_USERNAME` — Your iSDA account email
   - `ISDA_PASSWORD` — Your iSDA account password
### Frontend (Android)
 
1. Clone the repository and open it in **Android Studio**.
2. Ensure `locations_dict.json` is present in the `assets` folder.
3. Update the `BASE_URL` in your Retrofit configuration to point to your Hugging Face Space URL.
4. Sync Gradle and run the app on an emulator or physical device.
---
 
## 📡 API Reference
 
### Predict by Location
 
**Endpoint:** `POST /predict_by_location`
 
**Request Body:**
 
```json
{
  "latitude": -1.26,
  "longitude": 36.80
}
```
 
**Response Body:**
 
```json
{
  "recommendations": [
    { "crop": "maize", "confidence": 88.5 },
    { "crop": "beans", "confidence": 7.2 },
    { "crop": "coffee", "confidence": 4.3 }
  ],
  "detected_conditions": {
    "soil": { "...": "..." },
    "weather": { "...": "..." }
  }
}
```
 
---
 
## 👥 Contributors
 
- **Kiundi Eric Ndua**
- **Wanza Tess Christine**
