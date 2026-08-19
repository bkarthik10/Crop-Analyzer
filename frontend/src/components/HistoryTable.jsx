import { formatDate, formatPercent, formatNumber } from '../utils/formatters'

export default function HistoryTable({ records }) {
  if (!records) return <div className="empty-note">Loading history…</div>
  if (records.length === 0) {
    return <div className="empty-note">No predictions logged yet — run one from the Recommendation page.</div>
  }

  return (
    <table>
      <thead>
        <tr>
          <th>Date &amp; Time</th><th className="num">N</th><th className="num">P</th><th className="num">K</th>
          <th className="num">pH</th><th>Crop</th><th className="num">Confidence</th>
        </tr>
      </thead>
      <tbody>
        {records.map((r) => (
          <tr key={r.id}>
            <td>{formatDate(r.predictedAt)}</td>
            <td className="mono">{formatNumber(r.nitrogen, 0)}</td>
            <td className="mono">{formatNumber(r.phosphorus, 0)}</td>
            <td className="mono">{formatNumber(r.potassium, 0)}</td>
            <td className="mono">{formatNumber(r.ph, 2)}</td>
            <td>{r.predictedCrop}</td>
            <td className="mono status-optimal">{formatPercent(r.confidence, 0)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}
