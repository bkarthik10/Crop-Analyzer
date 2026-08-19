import { Routes, Route } from 'react-router-dom'
import Sidebar from './components/Sidebar'
import Home from './pages/Home'
import Recommendation from './pages/Recommendation'
import FarmAnalysis from './pages/FarmAnalysis'
import Fertilizer from './pages/Fertilizer'
import Dashboard from './pages/Dashboard'
import History from './pages/History'

export default function App() {
  return (
    <div className="app">
      <Sidebar />
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/recommendation" element={<Recommendation />} />
          <Route path="/farm-analysis" element={<FarmAnalysis />} />
          <Route path="/fertilizer" element={<Fertilizer />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/history" element={<History />} />
        </Routes>
      </main>
    </div>
  )
}
