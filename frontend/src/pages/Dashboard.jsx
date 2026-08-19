import { useState, useEffect } from 'react'
import Topbar from '../components/Topbar'
import ModelComparison from '../components/ModelComparison'
import { NpkBarChart, FeatureImportanceChart } from '../components/Charts'
import { api } from '../services/api'

export default function Dashboard() {
  const [summary, setSummary] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    api.getDashboardSummary().then(setSummary).finally(() => setLoading(false))
  }, [])

  if (loading) return <section className="page"><Topbar eyebrow="Step 05 · Dashboard" title="Dashboard" subtitle="Model performance, feature importance, and real metrics." /></section>

  return (
    <section className="page">
      <Topbar eyebrow="Step 05 · Dashboard" title="Dashboard" subtitle="Model performance, feature importance, and real metrics." />

      <div className="grid-2" style={{ marginBottom: 20 }}>
        <div className="card">
          <div className="chart-title">NPK SAMPLE LEVELS</div>
          <NpkBarChart nitrogen={50} phosphorus={50} potassium={50} />
        </div>
        <div className="card">
          <div className="chart-title">TEMPERATURE & RAINFALL WEEK</div>
          <table><tbody>
            <tr><td>Monday</td><td className="mono" style={{ textAlign: 'right' }}>28°C, 12mm</td></tr>
            <tr><td>Tuesday</td><td className="mono" style={{ textAlign: 'right' }}>26°C, 8mm</td></tr>
            <tr><td>Wednesday</td><td className="mono" style={{ textAlign: 'right' }}>29°C, 0mm</td></tr>
            <tr><td>Thursday</td><td className="mono" style={{ textAlign: 'right' }}>27°C, 15mm</td></tr>
            <tr><td>Friday</td><td className="mono" style={{ textAlign: 'right' }}>25°C, 22mm</td></tr>
            <tr><td>Saturday</td><td className="mono" style={{ textAlign: 'right' }}>30°C, 5mm</td></tr>
            <tr><td>Sunday</td><td className="mono" style={{ textAlign: 'right' }}>28°C, 3mm</td></tr>
          </tbody></table>
        </div>
      </div>

      <div className="card" style={{ marginBottom: 20 }}>
        <div className="card-head"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><circle cx="12" cy="12" r="9" /></svg><span className="card-title">Machine Learning Models</span></div>
        <div className="card-sub">Real metrics from the Python training run — see /ml/model-results for details</div>
        {summary?.cropModelComparison && <ModelComparison models={summary.cropModelComparison} />}
      </div>

      <div className="card" style={{ marginBottom: 20 }}>
        <FeatureImportanceChart importance={summary?.cropFeatureImportance} />
      </div>

      <div className="card">
        <div className="card-head"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><rect x="3" y="3" width="18" height="18" rx="2" /></svg><span className="card-title">Fertilizer ML Attempt</span></div>
        <div className="card-sub">Why this feature uses a rule-based engine instead</div>
        <table><tbody>
          <tr><td>Logistic Regression</td><td className="mono" style={{ textAlign: 'right' }}>14.53%</td></tr>
          <tr><td>Decision Tree</td><td className="mono" style={{ textAlign: 'right' }}>14.24%</td></tr>
          <tr><td>Random Forest</td><td className="mono" style={{ textAlign: 'right' }}>14.15%</td></tr>
        </tbody></table>
        <div className="tip-strip" style={{ marginTop: 14 }}>
          All classifiers scored at the ~14% dummy-baseline level (equal to random guessing across 7 balanced classes). The fertilizer dataset's labels are statistically independent of every feature. Rather than ship a coin-flip model, fertilizer recommendations are now rule-based: compare measured N/P/K to a healthy range and suggest the formulation that best corrects the largest deficit(s).
        </div>
      </div>
    </section>
  )
}
