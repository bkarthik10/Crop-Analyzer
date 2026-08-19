const API_BASE = 'http://localhost:8080/api'

async function request(path, options = {}) {
  const res = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  if (!res.ok) {
    let message = `Request failed (${res.status})`
    try {
      const body = await res.json()
      message = body.error || message
      if (body.fieldErrors) {
        message += ': ' + Object.entries(body.fieldErrors).map(([f, m]) => `${f} — ${m}`).join(', ')
      }
    } catch {
      // response wasn't JSON; keep the generic message
    }
    throw new Error(message)
  }
  return res.status === 204 ? null : res.json()
}

export const api = {
  predictCrop: (payload) =>
    request('/crop/predict', { method: 'POST', body: JSON.stringify(payload) }),

  getFertilizerOptions: () => request('/fertilizer/options'),

  recommendFertilizer: (payload) =>
    request('/fertilizer/recommend', { method: 'POST', body: JSON.stringify(payload) }),

  getFarmAnalysis: (crop) => request(`/farm-analysis/${encodeURIComponent(crop)}`),

  calculateProfit: (payload) =>
    request('/farm-analysis/profit-calculator', { method: 'POST', body: JSON.stringify(payload) }),

  estimateYield: (payload) =>
    request('/farm-analysis/yield-estimate', { method: 'POST', body: JSON.stringify(payload) }),

  getDashboardSummary: () => request('/dashboard/summary'),

  getHistory: () => request('/history'),
}
