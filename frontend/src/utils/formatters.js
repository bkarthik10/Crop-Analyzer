export function formatPercent(value, decimals = 1) {
  if (value === null || value === undefined) return '—'
  return `${(value * 100).toFixed(decimals)}%`
}

export function formatInr(value) {
  if (value === null || value === undefined) return '—'
  return `₹${Math.round(value).toLocaleString('en-IN')}`
}

export function formatNumber(value, decimals = 2) {
  if (value === null || value === undefined) return '—'
  return Number(value).toFixed(decimals)
}

export function capitalize(str) {
  if (!str) return ''
  return str.charAt(0).toUpperCase() + str.slice(1)
}

export function formatDate(isoString) {
  if (!isoString) return '—'
  const d = new Date(isoString)
  return d.toLocaleString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit',
  })
}
