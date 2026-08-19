/**
 * Static/illustrative weather display, matching the original design.
 * Wiring this to live data would need a weather API key (e.g. OpenWeatherMap)
 * which isn't part of this project's tech stack — swap the constants below
 * for a real fetch if you add one.
 */
const CURRENT = { location: 'Hyderabad, India', tempC: 28, condition: 'Partly Cloudy', humidity: 65, rainfallMm: 0, windKmh: 12 }

export default function WeatherCard({ variant = 'chip' }) {
  if (variant === 'chip') {
    return (
      <div className="weather-chip">
        <div>
          <div className="wloc">{CURRENT.location}</div>
          <div className="temp">{CURRENT.tempC}°C</div>
        </div>
        <div className="wcond">{CURRENT.condition.replace(' ', '\n')}</div>
      </div>
    )
  }

  return (
    <div className="weather-panel">
      <div className="weather-main">
        <div className="wloc-big">📍 {CURRENT.location} — today's conditions</div>
        <div className="wtemp-big">{CURRENT.tempC}°C <span>{CURRENT.condition}</span></div>
        <div className="wsub-grid">
          <div>Humidity<strong>{CURRENT.humidity}%</strong></div>
          <div>Rainfall<strong>{CURRENT.rainfallMm} mm</strong></div>
          <div>Wind<strong>{CURRENT.windKmh} km/h</strong></div>
        </div>
      </div>
      <div style={{ fontSize: 46 }}>⛅</div>
    </div>
  )
}
