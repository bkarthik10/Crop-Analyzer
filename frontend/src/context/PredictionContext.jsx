import { createContext, useContext, useState } from 'react'

/**
 * Holds the most recent crop prediction so other pages (Farm Analysis,
 * Fertilizer) can default to "the crop I just got recommended" without
 * re-fetching or prop-drilling through the router. Not in the original
 * file list — added because Recommendation, Farm Analysis and Fertilizer
 * all need to share this one piece of state.
 */
const PredictionContext = createContext(null)

export function PredictionProvider({ children }) {
  const [lastPrediction, setLastPrediction] = useState(null)
  const [lastRequest, setLastRequest] = useState(null)

  return (
    <PredictionContext.Provider value={{ lastPrediction, setLastPrediction, lastRequest, setLastRequest }}>
      {children}
    </PredictionContext.Provider>
  )
}

export function usePredictionContext() {
  const ctx = useContext(PredictionContext)
  if (!ctx) throw new Error('usePredictionContext must be used within a PredictionProvider')
  return ctx
}
