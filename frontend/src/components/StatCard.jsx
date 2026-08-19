export default function StatCard({ icon, label, value }) {
  return (
    <div className="stat-tile">
      <div className="stat-label">{icon} {label}</div>
      <div className="stat-value">{value ?? '—'}</div>
    </div>
  )
}
