import { useEffect, useCallback } from 'react'
import {
  AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend
} from 'recharts'
import { carteraApi } from '../services/api'
import { useFetch } from '../hooks/useFetch'
import { formatEuros, formatPorcentaje, clasePorValor } from '../services/utils'

// Colores para el gráfico de tarta
const COLORES_PIE = ['#6c63ff', '#22c55e', '#f59e0b', '#ef4444', '#06b6d4', '#ec4899']

export default function DashboardPage() {
  const fetchResumen = useCallback(() => carteraApi.resumen(), [])
  const { data: resumen, loading, error, refetch } = useFetch(fetchResumen)

  useEffect(() => { refetch() }, [refetch])

  if (loading) {
    return (
      <div className="state-loading">
        <div className="spinner" />
        <p>Calculando cartera...</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="state-error">
        <p>⚠️ {error}</p>
        <button className="btn btn-ghost" style={{ marginTop: 12 }} onClick={refetch}>
          Reintentar
        </button>
      </div>
    )
  }

  if (!resumen) return null

  // Preparar datos para el gráfico de distribución
  const datosPie = Object.entries(resumen.distribucionPorTipo).map(([tipo, pct]) => ({
    name: tipo,
    value: Number(pct),
  }))

  const esPositivo = resumen.beneficioPerdidaTotal >= 0

  return (
    <div>
      <div className="page-header">
        <h2>Dashboard</h2>
        <p>Resumen general de tu cartera simulada</p>
      </div>

      {/* KPIs principales */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-label">Valor total</div>
          <div className="stat-value">{formatEuros(resumen.valorTotalCartera)}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Dinero invertido</div>
          <div className="stat-value">{formatEuros(resumen.totalInvertido)}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Beneficio / Pérdida</div>
          <div className={`stat-value ${clasePorValor(resumen.beneficioPerdidaTotal)}`}>
            {formatEuros(resumen.beneficioPerdidaTotal)}
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Rentabilidad</div>
          <div className={`stat-value ${clasePorValor(resumen.rentabilidadTotal)}`}>
            {formatPorcentaje(resumen.rentabilidadTotal)}
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Dividendos cobrados</div>
          <div className="stat-value positive">{formatEuros(resumen.dividendosTotales)}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Nº activos</div>
          <div className="stat-value">{resumen.numeroActivos}</div>
        </div>
      </div>

      {/* Gráficas */}
      <div className="charts-grid">
        {/* Evolución simulada */}
        <div className="card">
          <div className="card-title">Evolución de la cartera</div>
          <ResponsiveContainer width="100%" height={220}>
            <AreaChart data={resumen.evolucion} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
              <defs>
                <linearGradient id="gradCartera" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#6c63ff" stopOpacity={0.3} />
                  <stop offset="95%" stopColor="#6c63ff" stopOpacity={0} />
                </linearGradient>
              </defs>
              <XAxis
                dataKey="mes"
                tick={{ fill: '#94a3b8', fontSize: 11 }}
                tickLine={false}
                axisLine={false}
              />
              <YAxis
                tick={{ fill: '#94a3b8', fontSize: 11 }}
                tickLine={false}
                axisLine={false}
                tickFormatter={(v) => `${(v / 1000).toFixed(0)}k`}
              />
              <Tooltip
                formatter={(value: number) => [formatEuros(value), 'Valor']}
                contentStyle={{
                  background: '#1a1d27',
                  border: '1px solid #2a2d3e',
                  borderRadius: 6,
                  fontSize: 12,
                  color: '#e2e8f0',
                }}
              />
              <Area
                type="monotone"
                dataKey="valor"
                stroke="#6c63ff"
                strokeWidth={2}
                fill="url(#gradCartera)"
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        {/* Distribución por tipo */}
        <div className="card">
          <div className="card-title">Distribución por tipo</div>
          {datosPie.length === 0 ? (
            <div className="state-empty">Sin datos</div>
          ) : (
            <ResponsiveContainer width="100%" height={220}>
              <PieChart>
                <Pie
                  data={datosPie}
                  cx="50%"
                  cy="50%"
                  innerRadius={55}
                  outerRadius={85}
                  paddingAngle={3}
                  dataKey="value"
                >
                  {datosPie.map((_, index) => (
                    <Cell key={index} fill={COLORES_PIE[index % COLORES_PIE.length]} />
                  ))}
                </Pie>
                <Tooltip
                  formatter={(value: number) => [`${value.toFixed(1)}%`, 'Peso']}
                  contentStyle={{
                    background: '#1a1d27',
                    border: '1px solid #2a2d3e',
                    borderRadius: 6,
                    fontSize: 12,
                    color: '#e2e8f0',
                  }}
                />
                <Legend
                  iconType="circle"
                  iconSize={8}
                  wrapperStyle={{ fontSize: 12, color: '#94a3b8' }}
                />
              </PieChart>
            </ResponsiveContainer>
          )}
        </div>
      </div>

      {/* Top posiciones */}
      {resumen.posiciones.length > 0 && (
        <div className="card">
          <div className="card-title" style={{ marginBottom: 16 }}>Posiciones actuales</div>
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Activo</th>
                  <th>Tipo</th>
                  <th className="text-right">Cantidad</th>
                  <th className="text-right">Valor actual</th>
                  <th className="text-right">B/P</th>
                  <th className="text-right">Rent.</th>
                  <th className="text-right">Peso</th>
                </tr>
              </thead>
              <tbody>
                {resumen.posiciones.map(pos => (
                  <tr key={pos.activoId}>
                    <td>
                      <strong>{pos.ticker}</strong>
                      <span style={{ color: 'var(--color-text-muted)', marginLeft: 6, fontSize: 12 }}>
                        {pos.nombre}
                      </span>
                    </td>
                    <td>
                      <span className={`badge badge-${pos.tipo}`}>{pos.tipo}</span>
                    </td>
                    <td className="text-right">{pos.cantidadActual}</td>
                    <td className="text-right">{formatEuros(pos.valorActual)}</td>
                    <td className={`text-right ${clasePorValor(pos.beneficioPerdida)}`}>
                      {formatEuros(pos.beneficioPerdida)}
                    </td>
                    <td className={`text-right ${clasePorValor(pos.rentabilidadPorcentual)}`}>
                      {formatPorcentaje(pos.rentabilidadPorcentual)}
                    </td>
                    <td className="text-right">{pos.pesoEnCartera.toFixed(1)}%</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  )
}
