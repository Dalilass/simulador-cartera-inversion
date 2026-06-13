import { useState, useEffect, useCallback } from 'react'
import { activosApi } from '../services/api'
import { useFetch } from '../hooks/useFetch'
import { formatEuros } from '../services/utils'
import type { Activo, ActivoRequest, TipoActivo } from '../types'

const TIPOS_ACTIVO: TipoActivo[] = ['ACCION', 'ETF', 'CRYPTO', 'BONO', 'FONDO']

const ACTIVO_VACIO: ActivoRequest = {
  ticker: '', nombre: '', tipo: 'ACCION',
  sector: '', moneda: 'EUR', precioActual: 0
}

export default function ActivosPage() {
  const [busqueda, setBusqueda] = useState('')
  const [modalAbierto, setModalAbierto] = useState(false)
  const [activoEditando, setActivoEditando] = useState<Activo | null>(null)
  const [form, setForm] = useState<ActivoRequest>(ACTIVO_VACIO)
  const [guardando, setGuardando] = useState(false)
  const [errorForm, setErrorForm] = useState('')

  const fetchActivos = useCallback(
    () => activosApi.listar(busqueda || undefined),
    [busqueda]
  )
  const { data: activos, loading, error, refetch } = useFetch(fetchActivos)

  useEffect(() => { refetch() }, [refetch])

  const abrirCrear = () => {
    setActivoEditando(null)
    setForm(ACTIVO_VACIO)
    setErrorForm('')
    setModalAbierto(true)
  }

  const abrirEditar = (activo: Activo) => {
    setActivoEditando(activo)
    setForm({
      ticker: activo.ticker,
      nombre: activo.nombre,
      tipo: activo.tipo,
      sector: activo.sector || '',
      moneda: activo.moneda || 'EUR',
      precioActual: activo.precioActual,
    })
    setErrorForm('')
    setModalAbierto(true)
  }

  const cerrarModal = () => {
    setModalAbierto(false)
    setActivoEditando(null)
  }

  const handleSubmit = async () => {
    if (!form.ticker || !form.nombre || !form.precioActual) {
      setErrorForm('Ticker, nombre y precio son obligatorios.')
      return
    }
    setGuardando(true)
    setErrorForm('')
    try {
      if (activoEditando) {
        await activosApi.actualizar(activoEditando.id, form)
      } else {
        await activosApi.crear(form)
      }
      cerrarModal()
      refetch()
    } catch (err) {
      setErrorForm(err instanceof Error ? err.message : 'Error al guardar')
    } finally {
      setGuardando(false)
    }
  }

  const handleEliminar = async (activo: Activo) => {
    if (!confirm(`¿Eliminar el activo "${activo.nombre}"? También se eliminarán sus operaciones.`)) return
    try {
      await activosApi.eliminar(activo.id)
      refetch()
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Error al eliminar')
    }
  }

  return (
    <div>
      <div className="page-header">
        <h2>Activos</h2>
        <p>Gestiona los activos de tu simulador</p>
      </div>

      <div className="toolbar">
        <div className="search-input-wrapper">
          <span className="search-icon">🔍</span>
          <input
            type="text"
            placeholder="Buscar por nombre o ticker..."
            value={busqueda}
            onChange={e => setBusqueda(e.target.value)}
          />
        </div>
        <button className="btn btn-primary" onClick={abrirCrear}>
          + Nuevo activo
        </button>
      </div>

      {loading && (
        <div className="state-loading">
          <div className="spinner" />
          <p>Cargando activos...</p>
        </div>
      )}

      {error && <div className="state-error">⚠️ {error}</div>}

      {!loading && !error && activos?.length === 0 && (
        <div className="state-empty">
          <p>No hay activos. ¡Crea el primero!</p>
        </div>
      )}

      {!loading && !error && activos && activos.length > 0 && (
        <div className="card">
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Ticker</th>
                  <th>Nombre</th>
                  <th>Tipo</th>
                  <th>Sector</th>
                  <th>Moneda</th>
                  <th className="text-right">Precio actual</th>
                  <th className="text-right">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {activos.map(activo => (
                  <tr key={activo.id}>
                    <td>
                      <strong style={{ fontFamily: 'var(--font-mono)', fontSize: 13 }}>
                        {activo.ticker}
                      </strong>
                    </td>
                    <td>{activo.nombre}</td>
                    <td>
                      <span className={`badge badge-${activo.tipo}`}>{activo.tipo}</span>
                    </td>
                    <td style={{ color: 'var(--color-text-muted)' }}>{activo.sector || '—'}</td>
                    <td style={{ color: 'var(--color-text-muted)' }}>{activo.moneda}</td>
                    <td className="text-right">{formatEuros(activo.precioActual)}</td>
                    <td className="text-right">
                      <button
                        className="btn btn-ghost btn-sm"
                        onClick={() => abrirEditar(activo)}
                        style={{ marginRight: 6 }}
                      >
                        ✏️ Editar
                      </button>
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => handleEliminar(activo)}
                      >
                        🗑️
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Modal crear/editar */}
      {modalAbierto && (
        <div className="modal-overlay" onClick={cerrarModal}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3>{activoEditando ? 'Editar activo' : 'Nuevo activo'}</h3>
              <button className="modal-close" onClick={cerrarModal}>×</button>
            </div>

            <div className="form-grid">
              <div className="form-group">
                <label>Ticker *</label>
                <input
                  type="text"
                  placeholder="Ej: MSFT"
                  value={form.ticker}
                  onChange={e => setForm(f => ({ ...f, ticker: e.target.value.toUpperCase() }))}
                  maxLength={20}
                />
              </div>

              <div className="form-group">
                <label>Tipo *</label>
                <select
                  value={form.tipo}
                  onChange={e => setForm(f => ({ ...f, tipo: e.target.value as TipoActivo }))}
                >
                  {TIPOS_ACTIVO.map(t => <option key={t} value={t}>{t}</option>)}
                </select>
              </div>

              <div className="form-group full">
                <label>Nombre *</label>
                <input
                  type="text"
                  placeholder="Ej: Microsoft Corporation"
                  value={form.nombre}
                  onChange={e => setForm(f => ({ ...f, nombre: e.target.value }))}
                />
              </div>

              <div className="form-group">
                <label>Sector</label>
                <input
                  type="text"
                  placeholder="Ej: Tecnología"
                  value={form.sector}
                  onChange={e => setForm(f => ({ ...f, sector: e.target.value }))}
                />
              </div>

              <div className="form-group">
                <label>Moneda</label>
                <select
                  value={form.moneda}
                  onChange={e => setForm(f => ({ ...f, moneda: e.target.value }))}
                >
                  <option value="EUR">EUR</option>
                  <option value="USD">USD</option>
                  <option value="GBP">GBP</option>
                </select>
              </div>

              <div className="form-group full">
                <label>Precio actual * (€)</label>
                <input
                  type="number"
                  step="0.01"
                  min="0.0001"
                  placeholder="0.00"
                  value={form.precioActual || ''}
                  onChange={e => setForm(f => ({ ...f, precioActual: parseFloat(e.target.value) || 0 }))}
                />
              </div>
            </div>

            {errorForm && (
              <p style={{ color: 'var(--color-danger)', fontSize: 13, marginBottom: 12 }}>
                ⚠️ {errorForm}
              </p>
            )}

            <div className="form-actions">
              <button className="btn btn-ghost" onClick={cerrarModal}>Cancelar</button>
              <button className="btn btn-primary" onClick={handleSubmit} disabled={guardando}>
                {guardando ? 'Guardando...' : activoEditando ? 'Guardar cambios' : 'Crear activo'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
