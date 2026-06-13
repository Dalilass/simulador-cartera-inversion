import { useState, useEffect, useCallback } from 'react'
import { operacionesApi, activosApi } from '../services/api'
import { useFetch } from '../hooks/useFetch'
import { formatEuros, formatFecha, formatCantidad } from '../services/utils'
import type { Activo, OperacionRequest, TipoOperacion } from '../types'

const TIPOS: TipoOperacion[] = ['COMPRA', 'VENTA', 'DIVIDENDO']

const FORM_VACIO: OperacionRequest = {
  activoId: 0,
  tipoOperacion: 'COMPRA',
  fecha: new Date().toISOString().split('T')[0],
  cantidad: 0,
  precioUnitario: 0,
}

export default function OperacionesPage() {
  const [filtroTipo, setFiltroTipo] = useState<TipoOperacion | undefined>()
  const [modalAbierto, setModalAbierto] = useState(false)
  const [form, setForm] = useState<OperacionRequest>(FORM_VACIO)
  const [guardando, setGuardando] = useState(false)
  const [errorForm, setErrorForm] = useState('')
  const [activos, setActivos] = useState<Activo[]>([])

  const fetchOperaciones = useCallback(
    () => operacionesApi.listar(undefined, filtroTipo),
    [filtroTipo]
  )
  const { data: operaciones, loading, error, refetch } = useFetch(fetchOperaciones)

  useEffect(() => { refetch() }, [refetch])

  useEffect(() => {
    activosApi.listar().then(setActivos).catch(console.error)
  }, [])

  const abrirModal = () => {
    setForm({ ...FORM_VACIO, activoId: activos[0]?.id || 0 })
    setErrorForm('')
    setModalAbierto(true)
  }

  const cerrarModal = () => setModalAbierto(false)

  const importeCalculado = (form.cantidad || 0) * (form.precioUnitario || 0)

  const handleSubmit = async () => {
    if (!form.activoId || !form.cantidad || !form.precioUnitario || !form.fecha) {
      setErrorForm('Todos los campos son obligatorios.')
      return
    }
    setGuardando(true)
    setErrorForm('')
    try {
      await operacionesApi.registrar(form)
      cerrarModal()
      refetch()
    } catch (err) {
      setErrorForm(err instanceof Error ? err.message : 'Error al registrar')
    } finally {
      setGuardando(false)
    }
  }

  const handleEliminar = async (id: number) => {
    if (!confirm('¿Eliminar esta operación?')) return
    try {
      await operacionesApi.eliminar(id)
      refetch()
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Error al eliminar')
    }
  }

  return (
    <div>
      <div className="page-header">
        <h2>Operaciones</h2>
        <p>Historial de compras, ventas y dividendos</p>
      </div>

      <div className="toolbar">
        <div className="filter-bar" style={{ marginBottom: 0 }}>
          <button
            className={`filter-btn ${!filtroTipo ? 'active' : ''}`}
            onClick={() => setFiltroTipo(undefined)}
          >
            Todas
          </button>
          {TIPOS.map(t => (
            <button
              key={t}
              className={`filter-btn ${filtroTipo === t ? 'active' : ''}`}
              onClick={() => setFiltroTipo(t)}
            >
              {t}
            </button>
          ))}
        </div>
        <button className="btn btn-primary" onClick={abrirModal}>
          + Registrar operación
        </button>
      </div>

      {loading && (
        <div className="state-loading">
          <div className="spinner" />
          <p>Cargando operaciones...</p>
        </div>
      )}

      {error && <div className="state-error">⚠️ {error}</div>}

      {!loading && !error && operaciones?.length === 0 && (
        <div className="state-empty">
          <p>No hay operaciones registradas.</p>
        </div>
      )}

      {!loading && !error && operaciones && operaciones.length > 0 && (
        <div className="card">
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Fecha</th>
                  <th>Activo</th>
                  <th>Tipo</th>
                  <th className="text-right">Cantidad</th>
                  <th className="text-right">Precio unit.</th>
                  <th className="text-right">Importe total</th>
                  <th className="text-right">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {operaciones.map(op => (
                  <tr key={op.id}>
                    <td style={{ color: 'var(--color-text-muted)' }}>{formatFecha(op.fecha)}</td>
                    <td>
                      <strong style={{ fontFamily: 'var(--font-mono)', fontSize: 12 }}>
                        {op.activoTicker}
                      </strong>
                      <span style={{ color: 'var(--color-text-muted)', marginLeft: 6, fontSize: 12 }}>
                        {op.activoNombre}
                      </span>
                    </td>
                    <td>
                      <span className={`badge badge-${op.tipoOperacion}`}>{op.tipoOperacion}</span>
                    </td>
                    <td className="text-right">{formatCantidad(op.cantidad)}</td>
                    <td className="text-right">{formatEuros(op.precioUnitario)}</td>
                    <td className="text-right">
                      <strong>{formatEuros(op.importeTotal)}</strong>
                    </td>
                    <td className="text-right">
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => handleEliminar(op.id)}
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

      {/* Modal registrar operación */}
      {modalAbierto && (
        <div className="modal-overlay" onClick={cerrarModal}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Registrar operación</h3>
              <button className="modal-close" onClick={cerrarModal}>×</button>
            </div>

            <div className="form-grid">
              <div className="form-group full">
                <label>Activo *</label>
                <select
                  value={form.activoId}
                  onChange={e => setForm(f => ({ ...f, activoId: Number(e.target.value) }))}
                >
                  <option value={0} disabled>Selecciona un activo</option>
                  {activos.map(a => (
                    <option key={a.id} value={a.id}>{a.ticker} — {a.nombre}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Tipo de operación *</label>
                <select
                  value={form.tipoOperacion}
                  onChange={e => setForm(f => ({ ...f, tipoOperacion: e.target.value as TipoOperacion }))}
                >
                  {TIPOS.map(t => <option key={t} value={t}>{t}</option>)}
                </select>
              </div>

              <div className="form-group">
                <label>Fecha *</label>
                <input
                  type="date"
                  value={form.fecha}
                  onChange={e => setForm(f => ({ ...f, fecha: e.target.value }))}
                />
              </div>

              <div className="form-group">
                <label>
                  {form.tipoOperacion === 'DIVIDENDO' ? 'Nº acciones (para cálculo)' : 'Cantidad'} *
                </label>
                <input
                  type="number"
                  step="0.0001"
                  min="0.0001"
                  placeholder="0"
                  value={form.cantidad || ''}
                  onChange={e => setForm(f => ({ ...f, cantidad: parseFloat(e.target.value) || 0 }))}
                />
              </div>

              <div className="form-group">
                <label>
                  {form.tipoOperacion === 'DIVIDENDO' ? 'Dividendo por acción (€)' : 'Precio unitario (€)'} *
                </label>
                <input
                  type="number"
                  step="0.0001"
                  min="0.0001"
                  placeholder="0.00"
                  value={form.precioUnitario || ''}
                  onChange={e => setForm(f => ({ ...f, precioUnitario: parseFloat(e.target.value) || 0 }))}
                />
              </div>

              {/* Importe calculado en tiempo real */}
              <div className="form-group full">
                <label>Importe total (calculado)</label>
                <input
                  type="text"
                  readOnly
                  value={formatEuros(importeCalculado)}
                  style={{ background: 'var(--color-surface-hover)', cursor: 'default' }}
                />
              </div>
            </div>

            {form.tipoOperacion === 'DIVIDENDO' && (
              <p style={{ fontSize: 12, color: 'var(--color-text-muted)', marginBottom: 12 }}>
                💡 El dividendo no aumenta la cantidad de activos en cartera, solo suma al resultado.
              </p>
            )}

            {errorForm && (
              <p style={{ color: 'var(--color-danger)', fontSize: 13, marginBottom: 12 }}>
                ⚠️ {errorForm}
              </p>
            )}

            <div className="form-actions">
              <button className="btn btn-ghost" onClick={cerrarModal}>Cancelar</button>
              <button className="btn btn-primary" onClick={handleSubmit} disabled={guardando}>
                {guardando ? 'Registrando...' : 'Registrar operación'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
