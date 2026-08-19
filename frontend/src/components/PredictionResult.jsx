import { useState } from 'react'
import StatCard from './StatCard'
import { formatPercent, formatNumber, formatInr } from '../utils/formatters'

/** Large botanical emoji shown when a crop has no curated real photo, or the photo fails to load. */
const FALLBACK_EMOJI = '🌱'

export default function PredictionResult({ result }) {
  const [imgFailed, setImgFailed] = useState(false)

  if (!result) {
    return (
      <div className="empty-note">
        Enter your soil &amp; climate readings and click "Predict Best Crop" to see a recommendation here.
      </div>
    )
  }

  const showImage = result.imageUrl && !imgFailed

  return (
    <div>
      <div className="result-hero">
        {showImage ? (
          <img
            className="result-img"
            src={result.imageUrl}
            alt={`${result.crop} crop`}
            onError={() => setImgFailed(true)}
          />
        ) : (
          <div className="result-img-fallback" aria-hidden="true">{FALLBACK_EMOJI}</div>
        )}
        <div>
          <div className="result-crop-label">Recommended Crop</div>
          <div className="result-crop-name">{result.crop}</div>
          <div className="confidence-row">
            <span style={{ fontSize: 11.5, color: 'var(--ink-soft)' }}>Confidence Score</span>
            <span className="mono" style={{ fontWeight: 600 }}>{formatPercent(result.confidence, 1)}</span>
          </div>
          <div className="confidence-bar">
            <div className="confidence-fill" style={{ width: formatPercent(result.confidence, 1) }} />
          </div>

          {result.alternatives?.length > 0 && (
            <div className="alt-crops">
              {result.alternatives.map((alt) => (
                <span key={alt.crop} className="alt-chip">{alt.crop} · {formatPercent(alt.probability, 0)}</span>
              ))}
            </div>
          )}
        </div>
      </div>

      <div className="grid-2">
        <StatCard icon="🌱" label="Typical Season" value={result.farmSnapshot?.typicalSeason ?? 'Not available'} />
        <StatCard
          icon="🌾"
          label="Historical Avg Yield"
          value={result.farmSnapshot?.avgYieldTonPerHectare ? `${formatNumber(result.farmSnapshot.avgYieldTonPerHectare)} t/ha` : 'Not available'}
        />
        <StatCard
          icon="📐"
          label="Typical Area"
          value={result.farmSnapshot?.avgAreaHectare ? `${formatNumber(result.farmSnapshot.avgAreaHectare, 1)} ha` : 'Not available'}
        />
        <StatCard
          icon="₹"
          label="Avg Net Profit"
          value={result.farmSnapshot?.avgNetProfitInr != null ? formatInr(result.farmSnapshot.avgNetProfitInr) : 'Not available'}
        />
      </div>
      {!result.farmSnapshot && (
        <div className="tip-strip muted">
          No historical yield/profit data is available for {result.crop} in the current datasets — figures above are left blank rather than guessed.
        </div>
      )}
    </div>
  )
}
