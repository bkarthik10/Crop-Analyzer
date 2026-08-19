import { NavLink } from 'react-router-dom'

const NAV_ITEMS = [
  { to: '/', label: 'Home', icon: <path d="M3 11l9-8 9 8M5 10v10h14V10" /> },
  { to: '/recommendation', label: 'Recommendation', icon: <path d="M12 2C8 6 6 10 6 13a6 6 0 0012 0c0-3-2-7-6-11z" /> },
  { to: '/farm-analysis', label: 'Farm Analysis', icon: <><rect x="3" y="3" width="18" height="18" rx="2" /><line x1="9" y1="9" x2="15" y2="9" /><line x1="9" y1="15" x2="15" y2="15" /></> },
  { to: '/fertilizer', label: 'Fertilizer', icon: <path d="M4 21V9l8-6 8 6v12M4 21h16M9 21v-6h6v6" /> },
  { to: '/dashboard', label: 'Dashboard', icon: <path d="M4 19V10M11 19V5M18 19v-7" /> },
  { to: '/history', label: 'History', icon: <><circle cx="12" cy="12" r="9" /><path d="M12 7v5l3 3" /></> },
]

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="brand">
        <div className="brand-mark">CA</div>
        <div className="brand-name">Crop Analyzer</div>
      </div>
      <div className="brand-sub">AI-powered crop recommendation &amp; precision agriculture, kept like a field log.</div>
      <div className="sidebar-divider"></div>

      <nav className="pages">
        {NAV_ITEMS.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.to === '/'}
            className={({ isActive }) => `nav-item${isActive ? ' active' : ''}`}
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">{item.icon}</svg>
            {item.label}
            <span className="nav-num">{item.num}</span>
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-footer">
        <a href="bkarthik7399@gmail.com">bkarthik7399@gmail.com</a>
        <a href="https://github.com/bkarthik" target="_blank" rel="noreferrer">github.com/bkarthik</a>
        <div style={{ marginTop: 10, color: '#6E7F6E' }}>© 2026 Smart Farming AI</div>
      </div>
    </aside>
  )
}
  