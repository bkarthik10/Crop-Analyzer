export default function FertilizerResult({ result }) {
  if (!result) {
    return <div className="empty-note">Fill in your soil report and click "Get Recommendation" to see a fertilizer suggestion.</div>
  }

  return (
    <div>
      <div className="result-crop-label">Recommended Fertilizer</div>
      <div className="result-crop-name" style={{ fontSize: 30 }}>{result.fertilizerName}</div>

      <div className="tip-strip green" style={{ marginTop: 16 }}>
        <strong>Why:</strong> {result.reasoning}
      </div>
      {result.usageNote && (
        <div className="tip-strip" style={{ marginTop: 10 }}>
          {result.usageNote}
        </div>
      )}
      <div className="tip-strip muted" style={{ marginTop: 10 }}>
        This is a rule-based recommendation, not a machine-learning prediction — the raw fertilizer dataset's
        labels turned out to be statistically independent of its features (~14% accuracy, no better than
        guessing), so exact per-acre dosage isn't shown here. Confirm application rates with a soil test or
        local agronomist.
      </div>
    </div>
  )
}
