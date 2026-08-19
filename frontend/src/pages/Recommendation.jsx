import { useState } from 'react'
import Topbar from '../components/Topbar'
import InputField from '../components/InputField'
import PredictionResult from '../components/PredictionResult'
import SoilHealth from '../components/SoilHealth'
import { usePrediction } from '../hooks/usePrediction'

const DEFAULTS = { nitrogen: '50', phosphorus: '50', potassium: '50', ph: '6.50', rainfall: '100', temperature: '25' }

// Note: unlike the original mockup, there's no Humidity field here — the crop
// dataset this model was trained on (Train_Dataset.csv) has no humidity
// column. Humidity is collected on the Fertilizer page instead, where the
// dataset actually has it.
const FIELDS = [
  { name: 'nitrogen', label: 'Nitrogen (N)', unit: 'mg/kg' },
  { name: 'phosphorus', label: 'Phosphorus (P)', unit: 'mg/kg' },
  { name: 'potassium', label: 'Potassium (K)', unit: 'mg/kg' },
  { name: 'ph', label: 'pH Value', unit: 'pH', step: '0.1' },
  { name: 'rainfall', label: 'Rainfall (mm)', unit: 'mm' },
  { name: 'temperature', label: 'Temperature (°C)', unit: '°C' },
]

export default function Recommendation() {
  const [form, setForm] = useState(DEFAULTS)
  const { predict, prediction, loading, error } = usePrediction()

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      await predict(form)
    } catch {
      // error state is already surfaced via the hook
    }
  }

  return (
    <section className="page">
      <Topbar
        eyebrow="Step 02 · Predict"
        title="Crop Recommendation"
        subtitle="Enter soil nutrients and climate readings to get the best-fit crop."
      />

      <div className="grid-2">
        <div className="card">
          <div className="card-head">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><path d="M12 2C8 6 6 10 6 13a6 6 0 0012 0c0-3-2-7-6-11z" /></svg>
            <span className="card-title">Enter Soil &amp; Climate Details</span>
          </div>
          <div className="card-sub">Provide accurate values for better recommendations</div>

          <form onSubmit={handleSubmit}>
            <div className="grid-2">
              {FIELDS.map((f) => (
                <InputField
                  key={f.name}
                  name={f.name}
                  label={f.label}
                  unit={f.unit}
                  step={f.step}
                  value={form[f.name]}
                  onChange={handleChange}
                />
              ))}
            </div>
            {error && <div className="error-banner">{error}</div>}
            <button className="btn-primary" type="submit" disabled={loading}>
              {loading ? <><span className="spinner" /> Predicting…</> : <>🌾 Predict Best Crop</>}
            </button>
          </form>
        </div>

        <div className="card">
          <div className="card-head">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><path d="M12 2l2.5 6.5L21 9l-5 4.5L17.5 21 12 17l-5.5 4L8 13.5 3 9l6.5-.5z" /></svg>
            <span className="card-title">Prediction Result</span>
          </div>
          <div className="card-sub">Recommended crop for the values above</div>
          <PredictionResult result={prediction} />
        </div>
      </div>

      {prediction && (
        <div className="card" style={{ marginTop: 22 }}>
          <div className="card-head">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><path d="M12 21s-7-4.5-7-10a5 5 0 0110-1 5 5 0 0110 1c0 5.5-7 10-7 10-2 0-3-2-6-2z" /></svg>
            <span className="card-title">Soil Health Report</span>
          </div>
          <div className="card-sub">Your values against {prediction.crop}'s real optimal range (25th–75th percentile of the training data)</div>
          <SoilHealth score={prediction.soilHealth?.score} nutrientStatus={prediction.soilHealth?.nutrientStatus} />
        </div>
      )}
    </section>
  )
}
