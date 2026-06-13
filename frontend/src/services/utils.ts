/**
 * Formatea un número como moneda en euros.
 */
export function formatEuros(valor: number): string {
  return new Intl.NumberFormat('es-ES', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(valor)
}

/**
 * Formatea un número como porcentaje.
 */
export function formatPorcentaje(valor: number): string {
  const signo = valor > 0 ? '+' : ''
  return `${signo}${valor.toFixed(2)}%`
}

/**
 * Formatea una fecha ISO a formato legible en español.
 */
export function formatFecha(fecha: string): string {
  return new Date(fecha + 'T00:00:00').toLocaleDateString('es-ES', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

/**
 * Formatea un número con 4 decimales (para cantidades de criptos, etc.)
 */
export function formatCantidad(valor: number): string {
  if (valor === Math.floor(valor)) return valor.toLocaleString('es-ES')
  return valor.toLocaleString('es-ES', { maximumFractionDigits: 4 })
}

/**
 * Devuelve la clase CSS según si el valor es positivo o negativo.
 */
export function clasePorValor(valor: number): string {
  if (valor > 0) return 'positive'
  if (valor < 0) return 'negative'
  return ''
}
