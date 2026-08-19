import { useState, useCallback } from 'react'
import { api } from '../services/api'
import { usePredictionContext } from '../context/PredictionContext'

/**
 * Wraps POST /api/crop/predict with loading/error state and stores the
 * result in PredictionContext so other pages can read it.
 */
export function usePrediction() {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const { lastPrediction, setLastPrediction, setLastRequest } = usePredictionContext()

  const predict = useCallback(async (formValues) => {
    setLoading(true)
    setError(null)
    try {
      const payload = {
        nitrogen: Number(formValues.nitrogen),
        phosphorus: Number(formValues.phosphorus),
        potassium: Number(formValues.potassium),
        ph: Number(formValues.ph),
        rainfall: Number(formValues.rainfall),
        temperature: Number(formValues.temperature),
      }
      const result = await api.predictCrop(payload)
      setLastPrediction(result)
      setLastRequest(payload)
      return result
    } catch (err) {
      setError(err.message || 'Prediction failed')
      throw err
    } finally {
      setLoading(false)
    }
  }, [setLastPrediction, setLastRequest])

  return { predict, prediction: lastPrediction, loading, error }
}
