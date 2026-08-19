import { formatPercent } from '../utils/formatters'

export default function ModelComparison({ models }) {
  if (!models?.length) return <div className="empty-note">Loading model comparison…</div>

  return (
    <>
      <table>
        <thead>
          <tr><th>Model</th><th className="num">Accuracy</th><th className="num">Precision</th><th className="num">Recall</th><th className="num">F1</th></tr>
        </thead>
        <tbody>
          {models.map((m) => (
            <tr key={m.model} className={m.deployed ? 'model-row-best' : ''}>
              <td style={m.deployed ? { fontWeight: 600, color: 'var(--moss-dark)' } : undefined}>
                {m.model}{m.deployed ? ' — deployed' : ''}
              </td>
              <td className="mono" style={m.deployed ? { fontWeight: 700, color: 'var(--moss-dark)' } : undefined}>{m.accuracy}%</td>
              <td className="mono">{formatPercent(m.precision, 1)}</td>
              <td className="mono">{formatPercent(m.recall, 1)}</td>
              <td className="mono">{formatPercent(m.f1, 1)}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="chart-title" style={{ marginTop: 22 }}>MODEL ACCURACY COMPARISON</div>
      {models.map((m) => (
        <div className="hbar-row" key={m.model}>
          <div className="hbar-label">{m.model}</div>
          <div className="hbar-track">
            <div className="hbar-fill" style={{ width: `${m.accuracy}%`, background: m.deployed ? 'var(--gold)' : 'var(--moss)' }} />
          </div>
          <div className="hbar-val mono" style={m.deployed ? { fontWeight: 700 } : undefined}>{m.accuracy}%</div>
        </div>
      ))}
    </>
  )
}
