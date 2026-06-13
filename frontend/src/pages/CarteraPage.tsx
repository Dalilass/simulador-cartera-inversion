import { useEffect, useCallback } from 'react'
import { carteraApi } from '../services/api'
import { useFetch } from '../hooks/useFetch'
import { formatEuros, formatPorcentaje, formatCantidad, clasePorValor } from '../services/utils'

export default function CarteraPage() {
  const fetchResumen = useCallback(() => carteraApi.resumen(), [])
  const { data: resumen, loading, error, refetch } = useFetch(fetchResumen)

  useEffect(() => { refetch() }, [refetch])

  if (loading) {
    return (
      <div className="state-loading">
        <div className="spinner" />
        <p>Calculando posiciones...</p>
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

  const { posiciones } = resumen

  return (
    <div>
      <div className="page-header">
        <h2>Mi Cartera</h2>
        <p>Posición actual de cada activo</p>
      </div>

      {/* Resumen rápido */}
      <div className="stats-grid" style={{ marginBottom: 24 }}>
        <div className="stat-card">
          <div className="stat-label">Valor total</div>
          <div className="stat-value">{formatEuros(resumen.valorTotalCartera)}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Total invertido</div>
          <div className="stat-value">{formatEuros(resumen.totalInvertido)}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Resultado total</div>
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
      </div>

      {posiciones.length === 0 ? (
        <div className="card">
          <div className="state-empty">
            <p>No tienes posiciones abiertas. Registra una compra para empezar.</p>
          </div>
        </div>
      ) : (
        <div className="card">
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Activo</th>
                  <th>Tipo</th>
                  <th className="text-right">Cantidad</th>
                  <th className="text-right">P. medio compra</th>
                  <th className="text-right">P. actual</th>
                  <th className="text-right">Coste total</th>
                  <th className="text-right">Valor actual</th>
                  <th className="text-right">B/P (€)</th>
                  <th className="text-right">Rent. %</th>
                  <th className="text-right">Dividendos</th>
                  <th className="text-right">Peso</th>
                </tr>
              </thead>
              <tbody>
                {posiciones.map(pos => (
                  <tr key={pos.activoId}>
                    <td>
                      <div style={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <strong style={{ fontFamily: 'var(--font-mono)', fontSize: 13 }}>
                          {pos.ticker}
                        </strong>
                        <span style={{ fontSize: 11, color: 'var(--color-text-muted)' }}>
                          {pos.nombre}
                        </span>
                      </div>
                    </td>
                    <td>
                      <span className={`badge badge-${pos.tipo}`}>{pos.tipo}</span>
                    </td>
                    <td className="text-right">{formatCantidad(pos.cantidadActual)}</td>
                    <td className="text-right">{formatEuros(pos.precioMedioCompra)}</td>
                    <td className="text-right">{formatEuros(pos.precioActual)}</td>
                    <td className="text-right" style={{ color: 'var(--color-text-muted)' }}>
                      {formatEuros(pos.costeTotal)}
                    </td>
                    <td className="text-right">
                      <strong>{formatEuros(pos.valorActual)}</strong>
                    </td>
                    <td className={`text-right ${clasePorValor(pos.beneficioPerdida)}`}>
                      {formatEuros(pos.beneficioPerdida)}
                    </td>
                    <td className={`text-right ${clasePorValor(pos.rentabilidadPorcentual)}`}>
                      <strong>{formatPorcentaje(pos.rentabilidadPorcentual)}</strong>
                    </td>
                    <td className="text-right positive">
                      {pos.dividendosCobrados > 0 ? formatEuros(pos.dividendosCobrados) : '—'}
                    </td>
                    <td className="text-right">
                      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 6 }}>
                        <div
                          style={{
                            width: 40,
                            height: 4,
                            borderRadius: 2,
                            background: 'var(--color-border)',
                            overflow: 'hidden',
                          }}
                        >
                          <div
                            style={{
                              width: `${pos.pesoEnCartera}%`,
                              height: '100%',
                              background: 'var(--color-accent)',
                              borderRadius: 2,
                            }}
                          />
                        </div>
                        <span>{pos.pesoEnCartera.toFixed(1)}%</span>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Nota informativa */}
      <p style={{
        marginTop: 20,
        fontSize: 12,
        color: 'var(--color-text-faint)',
        textAlign: 'center',
      }}>
        ⚠️ Todos los datos son ficticios y tienen fines exclusivamente educativos.
        No constituyen recomendación de compra ni venta de ningún instrumento financiero.
      </p>
    </div>
  )
}
