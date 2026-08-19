import { useState, useEffect } from 'react'
import Topbar from '../components/Topbar'
import InputField from '../components/InputField'
import FertilizerResult from '../components/FertilizerResult'
import { api } from '../services/api'
import { usePredictionContext } from '../context/PredictionContext'

const DEFAULTS = { temperature: '28', humidity: '60', moisture: '35', nitrogen: '20', potassium: '10', phosphorous: '15' }

export default function Fertilizer() {
  const { lastPrediction } = usePredictionContext()
  const [options, setOptions] = useState({ soilTypes: [], cropTypes: [], fertilizerNames: [] })
  const [form, setForm] = useState(DEFAULTS)
  const [soilType, setSoilType] = useState('')
  const [cropType, setCropType] = useState('')
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    api.getFertilizerOptions().then((opts) => {
      setOptions(opts)
      setSoilType(opts.soilTypes[0] || '')
      setCropType(opts.cropTypes[0] || '')
    }).catch((err) => setError(err.message))
  }, [])

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    try {
      const payload = {
        temperature: Number(form.temperature),
        humidity: Number(form.humidity),
        moisture: Number(form.moisture),
        soilType,
        cropType,
        nitrogen: Number(form.nitrogen),
        potassium: Number(form.potassium),
        phosphorous: Number(form.phosphorous),
      }
      const res = await api.recommendFertilizer(payload)
      setResult(res)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="page">
      <Topbar
        eyebrow="Step 04 · Feed the Soil"
        title="Fertilizer"
        subtitle="A recommendation grounded in real agronomic logic, from your soil report."
      />

      {lastPrediction && (
        <div className="tip-strip green" style={{ marginBottom: 20 }}>
          Your last recommended crop was <strong>{lastPrediction.crop}</strong> — pick the closest match in Crop Type below if you'd like a tailored suggestion.
        </div>
      )}

      <div className="grid-2">
        <div className="card">
          <div className="card-head">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><path d="M4 21V9l8-6 8 6v12M4 21h16" /></svg>
            <span className="card-title">Soil &amp; Crop Report</span>
          </div>
          <div className="card-sub">These categories come straight from the dataset</div>

          <form onSubmit={handleSubmit}>
            <div className="grid-2">
              <InputField name="soilType" label="Soil Type" value={soilType} onChange={(e) => setSoilType(e.target.value)} options={options.soilTypes} />
              <InputField name="cropType" label="Crop Type" value={cropType} onChange={(e) => setCropType(e.target.value)} options={options.cropTypes} />
              <InputField name="temperature" label="Temperature" unit="°C" value={form.temperature} onChange={handleChange} />
              <InputField name="humidity" label="Humidity" unit="%" value={form.humidity} onChange={handleChange} />
              <InputField name="moisture" label="Soil Moisture" unit="%" value={form.moisture} onChange={handleChange} />
              <InputField name="nitrogen" label="Nitrogen (N)" unit="mg/kg" value={form.nitrogen} onChange={handleChange} />
              <InputField name="potassium" label="Potassium (K)" unit="mg/kg" value={form.potassium} onChange={handleChange} />
              <InputField name="phosphorous" label="Phosphorous (P)" unit="mg/kg" value={form.phosphorous} onChange={handleChange} />
            </div>
            {error && <div className="error-banner">{error}</div>}
            <button className="btn-primary" type="submit" disabled={loading}>
              {loading ? <><span className="spinner" /> Working…</> : <>🧪 Get Recommendation</>}
            </button>
          </form>
        </div>

        <div className="card">
          <div className="card-head">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><path d="M12 21s-7-4.5-7-10a5 5 0 0110-1 5 5 0 0110 1c0 5.5-7 10-7 10-2 0-3-2-6-2z" /></svg>
            <span className="card-title">Recommendation</span>
          </div>
          <div className="card-sub">Rule-based, not ML — see why below</div>
          <FertilizerResult result={result} />
        </div>
      </div>
    </section>
  )
}
