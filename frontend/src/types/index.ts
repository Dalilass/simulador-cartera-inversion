// ====== Enums ======
export type TipoActivo = 'ACCION' | 'ETF' | 'CRYPTO' | 'BONO' | 'FONDO';
export type TipoOperacion = 'COMPRA' | 'VENTA' | 'DIVIDENDO';

// ====== Activo ======
export interface Activo {
  id: number;
  ticker: string;
  nombre: string;
  tipo: TipoActivo;
  sector: string;
  moneda: string;
  precioActual: number;
}

export interface ActivoRequest {
  ticker: string;
  nombre: string;
  tipo: TipoActivo;
  sector: string;
  moneda: string;
  precioActual: number;
}

// ====== Operación ======
export interface Operacion {
  id: number;
  activoId: number;
  activoTicker: string;
  activoNombre: string;
  tipoOperacion: TipoOperacion;
  fecha: string; // ISO date string "YYYY-MM-DD"
  cantidad: number;
  precioUnitario: number;
  importeTotal: number;
}

export interface OperacionRequest {
  activoId: number;
  tipoOperacion: TipoOperacion;
  fecha: string;
  cantidad: number;
  precioUnitario: number;
}

// ====== Cartera ======
export interface Posicion {
  activoId: number;
  ticker: string;
  nombre: string;
  tipo: TipoActivo;
  sector: string;
  moneda: string;
  cantidadActual: number;
  precioMedioCompra: number;
  precioActual: number;
  valorActual: number;
  costeTotal: number;
  beneficioPerdida: number;
  rentabilidadPorcentual: number;
  dividendosCobrados: number;
  pesoEnCartera: number;
}

export interface PuntoEvolucion {
  mes: string;
  valor: number;
}

export interface ResumenCartera {
  valorTotalCartera: number;
  totalInvertido: number;
  beneficioPerdidaTotal: number;
  rentabilidadTotal: number;
  dividendosTotales: number;
  numeroActivos: number;
  distribucionPorTipo: Record<string, number>;
  posiciones: Posicion[];
  evolucion: PuntoEvolucion[];
}

// ====== Estado genérico para llamadas API ======
export interface ApiState<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
}
