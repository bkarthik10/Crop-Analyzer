const STATUS_CLASS = { Optimal: 'status-optimal', Low: 'status-low', High: 'status-high' }

export default function SoilHealth({ score, nutrientStatus }) {
  if (score == null || !nutrientStatus) {
    return <div className="empty-note">Run a crop prediction to see your soil health report.</div>
  }

  const circumference = 2 * Math.PI * 60
  const offset = circumference * (1 - score / 100)

  return (
    <div className="core-gauge">
      <svg width="140" height="140" viewBox="0 0 140 140">
        <circle cx="70" cy="70" r="60" fill="none" stroke="#EEE8D6" strokeWidth="10" />
        <circle
          cx="70" cy="70" r="60" fill="none" stroke="var(--moss)" strokeWidth="10"
          strokeDasharray={circumference} strokeDashoffset={offset} strokeLinecap="round"
          transform="rotate(-90 70 70)"
        />
        <circle cx="70" cy="70" r="44" fill="none" stroke="#F1EAD6" strokeWidth="1" />
        <text x="70" y="66" textAnchor="middle" fontFamily="Fraunces, serif" fontWeight="700" fontSize="26" fill="var(--ink)">{score}%</text>
        <text x="70" y="84" textAnchor="middle" fontFamily="Inter" fontSize="9.5" fill="var(--ink-soft)">Soil Health Score</text>
      </svg>
      <table style={{ flex: 1 }}>
        <tbody>
          {Object.entries(nutrientStatus).map(([nutrient, status]) => (
            <tr key={nutrient}>
              <td>{nutrient}</td>
              <td className={STATUS_CLASS[status] || ''} style={{ textAlign: 'right' }}>{status}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
