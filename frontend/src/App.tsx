import { Routes, Route, NavLink } from 'react-router-dom'
import DashboardPage from './pages/DashboardPage'
import ActivosPage from './pages/ActivosPage'
import OperacionesPage from './pages/OperacionesPage'
import CarteraPage from './pages/CarteraPage'

const navItems = [
  { to: '/', icon: '📊', label: 'Dashboard' },
  { to: '/activos', icon: '🏢', label: 'Activos' },
  { to: '/operaciones', icon: '🔄', label: 'Operaciones' },
  { to: '/cartera', icon: '💼', label: 'Mi Cartera' },
]

export default function App() {
  return (
    <div className="app-layout">
      {/* Sidebar */}
      <nav className="sidebar">
        <div className="sidebar-logo">
          <h1>💹 CarteraSim</h1>
          <span>Simulador educativo</span>
        </div>

        <div className="sidebar-nav">
          {navItems.map(item => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === '/'}
              className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
            >
              <span className="nav-icon">{item.icon}</span>
              {item.label}
            </NavLink>
          ))}
        </div>

        <div className="sidebar-footer">
          <p className="sidebar-disclaimer">
            ⚠️ Simulador educativo con datos ficticios. No constituye asesoramiento financiero.
          </p>
        </div>
      </nav>

      {/* Contenido principal */}
      <main className="main-content">
        <Routes>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/activos" element={<ActivosPage />} />
          <Route path="/operaciones" element={<OperacionesPage />} />
          <Route path="/cartera" element={<CarteraPage />} />
        </Routes>
      </main>
    </div>
  )
}
