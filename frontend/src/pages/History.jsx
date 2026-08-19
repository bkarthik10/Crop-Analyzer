import { useState, useEffect } from 'react'
import Topbar from '../components/Topbar'
import HistoryTable from '../components/HistoryTable'
import { api } from '../services/api'

export default function History() {
  const [records, setRecords] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    api.getHistory().then(setRecords).finally(() => setLoading(false))
  }, [])

  return (
    <section className="page">
      <Topbar
        eyebrow="Step 06 · Record"
        title="History"
        subtitle="A running log of every soil reading and its predicted crop."
      />

      <div className="card">
        <div className="card-head"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><circle cx="12" cy="12" r="9" /><path d="M12 7v5l3 3" /></svg><span className="card-title">Prediction History</span></div>
        <div className="card-sub">Last 50 predictions, newest first</div>
        {loading ? <div className="empty-note">Loading…</div> : <HistoryTable records={records} />}
      </div>
    </section>
  )
}
