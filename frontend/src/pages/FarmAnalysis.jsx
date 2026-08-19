import { useState, useEffect, useCallback } from 'react'
import Topbar from '../components/Topbar'
import CropInformation from '../components/CropInformation'
import ProfitCard from '../components/ProfitCard'
import SoilHealth from '../components/SoilHealth'
import { api } from '../services/api'
import { usePredictionContext } from '../context/PredictionContext'
import { capitalize } from '../utils/formatters'

export default function FarmAnalysis() {
  const { lastPrediction } = usePredictionContext()
  const [crop, setCrop] = useState(lastPrediction?.crop?.toLowerCase() || 'rice')
  const [analysis, setAnalysis] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const load = useCallback(async (cropName) => {
    setLoading(true)
    setError(null)
    try {
      const data = await api.getFarmAnalysis(cropName)
      setAnalysis(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { load(crop) }, [crop, load])

  return (
    <section className="page">
      <Topbar
        eyebrow="Step 03 · Analyze"
        title="Farm Analysis"
        subtitle="Crop performance, soil health status and profit projections."
      />

      <div className="card" style={{ marginBottom: 20 }}>
        <div className="field" style={{ maxWidth: 280, marginBottom: 0 }}>
          <label htmlFor="crop-select">Crop</label>
          <div className="input-wrap">
            <input
              id="crop-select"
              value={crop}
              onChange={(e) => setCrop(e.target.value.toLowerCase())}
              onBlur={() => load(crop)}
              placeholder="e.g. rice, wheat, banana…"
            />
          </div>
        </div>
        {lastPrediction && (
          <div className="tip-strip green" style={{ marginTop: 12 }}>
            Your last recommendation was <strong>{lastPrediction.crop}</strong> —
            {' '}<a href="#" onClick={(e) => { e.preventDefault(); setCrop(lastPrediction.crop.toLowerCase()) }} style={{ color: 'var(--moss-dark)', textDecoration: 'underline' }}>view its analysis</a>.
          </div>
        )}
      </div>

      {error && <div className="error-banner">{error}</div>}

      <div className="grid-3" style={{ marginBottom: 20 }}>
        <div className="card">
          <div className="card-head">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><path d="M12 2l2.5 6.5L21 9l-5 4.5L17.5 21 12 17l-5.5 4L8 13.5 3 9l6.5-.5z" /></svg>
            <span className="card-title">Crop Information</span>
          </div>
          <div className="card-sub">From 345K+ historical APY records</div>
          {loading ? <div className="skeleton" style={{ height: 100 }} /> : <CropInformation analysis={analysis} />}
        </div>

        <div className="card">
          <div className="card-head">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><path d="M12 21s-7-4.5-7-10a5 5 0 0110-1 5 5 0 0110 1c0 5.5-7 10-7 10-2 0-3-2-6-2z" /></svg>
            <span className="card-title">Soil Health Status</span>
          </div>
          <div className="card-sub">From your last Recommendation run</div>
          {lastPrediction ? (
            <SoilHealth score={lastPrediction.soilHealth?.score} nutrientStatus={lastPrediction.soilHealth?.nutrientStatus} />
          ) : (
            <div className="empty-note">Run a crop recommendation first to see soil health here.</div>
          )}
        </div>

        <div className="card">
          <div className="card-head">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><path d="M12 2v20M2 12h20" /></svg>
            <span className="card-title">Profit Analysis</span>
          </div>
          <div className="card-sub">Avg. figures for {capitalize(crop)}</div>
          {loading ? <div className="skeleton" style={{ height: 100 }} /> : <ProfitCard analysis={analysis} />}
        </div>
      </div>
    </section>
  )
}
