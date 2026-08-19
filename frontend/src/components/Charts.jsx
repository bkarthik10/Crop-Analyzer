import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from 'recharts'

const COLORS = { moss: '#5A8B6F', rust: '#9B7865', gold: '#B8956A', sky: '#7A9EBE' }

/** NPK levels for the values currently entered on the Recommendation form. */
export function NpkBarChart({ nitrogen, phosphorus, potassium }) {
  const data = [
    { name: 'N', value: nitrogen ?? 0, fill: COLORS.moss },
    { name: 'P', value: phosphorus ?? 0, fill: COLORS.rust },
    { name: 'K', value: potassium ?? 0, fill: COLORS.gold },
  ]
  return (
    <div>
      <div className="chart-title">NPK LEVELS (mg/kg)</div>
      <ResponsiveContainer width="100%" height={140}>
        <BarChart data={data} margin={{ top: 4, right: 8, left: -20, bottom: 0 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#E8E1D8" vertical={false} />
          <XAxis dataKey="name" tick={{ fontSize: 11, fontFamily: 'IBM Plex Mono, monospace', fill: '#9BA7A0' }} axisLine={false} tickLine={false} />
          <YAxis tick={{ fontSize: 10, fill: '#9BA7A0' }} axisLine={false} tickLine={false} />
          <Tooltip contentStyle={{ fontSize: 12, borderRadius: 8, border: '1px solid #E8E1D8' }} />
          <Bar dataKey="value" radius={[4, 4, 0, 0]}>
            {data.map((d) => <Cell key={d.name} fill={d.fill} />)}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}

/** Real Random Forest feature importances from the deployed crop model. */
export function FeatureImportanceChart({ importance }) {
  if (!importance) return null
  const data = Object.entries(importance)
    .map(([name, value]) => ({ name, value: Number((value * 100).toFixed(1)) }))
    .sort((a, b) => b.value - a.value)

  return (
    <div>
      <div className="chart-title">FEATURE IMPORTANCE — CROP MODEL (%)</div>
      <ResponsiveContainer width="100%" height={180}>
        <BarChart data={data} layout="vertical" margin={{ top: 4, right: 20, left: 10, bottom: 0 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#E8E1D8" horizontal={false} />
          <XAxis type="number" tick={{ fontSize: 10, fill: '#9BA7A0' }} axisLine={false} tickLine={false} />
          <YAxis dataKey="name" type="category" width={80} tick={{ fontSize: 11, fontFamily: 'IBM Plex Mono, monospace', fill: '#6B7B74' }} axisLine={false} tickLine={false} />
          <Tooltip contentStyle={{ fontSize: 12, borderRadius: 8, border: '1px solid #E8E1D8' }} formatter={(v) => `${v}%`} />
          <Bar dataKey="value" fill={COLORS.moss} radius={[0, 4, 4, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}
