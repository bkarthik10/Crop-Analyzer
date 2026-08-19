import { formatInr, formatNumber } from '../utils/formatters'

export default function ProfitCard({ analysis }) {
  if (!analysis?.profitDataAvailable) {
    return (
      <div className="tip-strip muted">
        Profit figures aren't available for this crop — the farm-profit dataset only covers Cotton, Maize, Potato, Rice, Sugarcane and Wheat.
      </div>
    )
  }

  return (
    <table>
      <tbody>
        <tr><td style={{ fontWeight: 500 }}>Avg. Total Cost</td><td className="mono" style={{ textAlign: 'right' }}>{formatInr(analysis.avgTotalCostInr)}</td></tr>
        <tr><td style={{ fontWeight: 500 }}>Avg. Revenue</td><td className="mono" style={{ textAlign: 'right' }}>{formatInr(analysis.avgRevenueInr)}</td></tr>
        <tr style={{ background: '#F0F5ED' }}>
          <td style={{ fontWeight: 600 }}>Avg. Net Profit</td>
          <td className="mono" style={{ textAlign: 'right', color: 'var(--moss)', fontWeight: 700 }}>{formatInr(analysis.avgNetProfitInr)}</td>
        </tr>
        <tr><td style={{ fontWeight: 500 }}>Avg. ROI</td><td className="mono" style={{ textAlign: 'right' }}>{formatNumber(analysis.avgRoiPercent, 1)}%</td></tr>
      </tbody>
    </table>
  )
}
