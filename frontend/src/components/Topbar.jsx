import WeatherCard from './WeatherCard'

export default function Topbar({ eyebrow, title, subtitle }) {
  return (
    <div className="topbar">
      <div>
        <div className="topbar-eyebrow">{eyebrow}</div>
        <h1>{title}</h1>
        <p>{subtitle}</p>
      </div>
      <WeatherCard variant="chip" />
    </div>
  )
}
