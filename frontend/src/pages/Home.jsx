import { useNavigate } from 'react-router-dom'
import Topbar from '../components/Topbar'
import WeatherCard from '../components/WeatherCard'

export default function Home() {
  const navigate = useNavigate()

  return (
    <section className="page">
      <Topbar
        eyebrow="Field Overview"
        title="Home"
        subtitle="Empowering farmers with AI-driven insight into crops, fertilizer and soil."
      />

      <div className="hero">
        <div>
          <div className="hero-eyebrow">Precision Agriculture, AI-Assisted</div>
          <h2>Read your field like a <em>log book</em>, backed by machine learning.</h2>
          <p>Crop Analyzer helps farmers choose the best crops, optimize fertilizer use, and improve productivity sustainably — turning soil and climate readings into a clear recommendation.</p>
        </div>
        <div className="ledger">
          <div className="ledger-row"><span className="ledger-num mono">94%</span><span className="ledger-label">Crop Model<br />Accuracy</span></div>
          <div className="ledger-row"><span className="ledger-num mono">40</span><span className="ledger-label">Crops<br />Supported</span></div>
          <div className="ledger-row"><span className="ledger-num mono">7,165</span><span className="ledger-label">Training<br />Records</span></div>
          <div className="ledger-row"><span className="ledger-num mono">345K+</span><span className="ledger-label">Historical Yield<br />Records (APY)</span></div>
        </div>
      </div>

      <WeatherCard variant="panel" />

      <div className="home-strip">
        <div className="strip-card" onClick={() => navigate('/recommendation')}>
          <h4>🌾 Start a Recommendation</h4>
          <p>Enter soil nutrients and climate values to get your best-fit crop.</p>
        </div>
        <div className="strip-card" onClick={() => navigate('/fertilizer')}>
          <h4>🧪 Check Fertilizer Needs</h4>
          <p>Get a fertilizer recommendation grounded in real agronomic logic.</p>
        </div>
        <div className="strip-card" onClick={() => navigate('/dashboard')}>
          <h4>📊 Review the Dashboard</h4>
          <p>See real model accuracy, feature importance and comparisons.</p>
        </div>
      </div>

      <footer>
        <span>© 2026 Crop Analyzer — Smart Farming AI Platform</span>
        <span>Built as a field log, not a dashboard.</span>
      </footer>
    </section>
  )
}
