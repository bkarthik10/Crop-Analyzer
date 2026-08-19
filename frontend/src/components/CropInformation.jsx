import { capitalize, formatNumber } from '../utils/formatters'

export default function CropInformation({ analysis }) {
  if (!analysis?.historicalDataAvailable) {
    return (
      <div className="tip-strip muted">
        No APY historical record was matched for {capitalize(analysis?.crop)} in the current dataset.
      </div>
    )
  }

  return (
    <table>
      <tbody>
        <tr><td style={{ fontWeight: 500 }}>Crop Name</td><td className="mono" style={{ textAlign: 'right' }}>{capitalize(analysis.crop)}</td></tr>
        <tr><td style={{ fontWeight: 500 }}>Typical Season</td><td className="mono" style={{ textAlign: 'right' }}>{analysis.typicalSeason}</td></tr>
        <tr><td style={{ fontWeight: 500 }}>Avg. Yield (APY)</td><td className="mono" style={{ textAlign: 'right' }}>{formatNumber(analysis.avgYieldTonPerHectareApy)} t/ha</td></tr>
        <tr><td style={{ fontWeight: 500 }}>Avg. Area Cultivated</td><td className="mono" style={{ textAlign: 'right' }}>{formatNumber(analysis.avgAreaHectare, 1)} ha</td></tr>
        <tr><td style={{ fontWeight: 500 }}>Records Behind This</td><td className="mono" style={{ textAlign: 'right' }}>{analysis.apyRecordCount?.toLocaleString('en-IN')}</td></tr>
      </tbody>
    </table>
  )
}
