import type {
  Activo, ActivoRequest,
  Operacion, OperacionRequest,
  ResumenCartera, TipoOperacion
} from '../types';

const BASE_URL = '/api';

// Helper para manejar errores de la API de forma consistente
async function fetchJson<T>(url: string, options?: RequestInit): Promise<T> {
  const response = await fetch(url, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });

  if (!response.ok) {
    // Intentar leer el mensaje de error del backend
    let mensaje = `Error ${response.status}`;
    try {
      const errorData = await response.json();
      mensaje = errorData.mensaje || mensaje;
    } catch {
      // si no hay cuerpo JSON, usar el mensaje por defecto
    }
    throw new Error(mensaje);
  }

  // Para respuestas 204 No Content (DELETE)
  if (response.status === 204) {
    return undefined as T;
  }

  return response.json();
}

// ====== Activos ======
export const activosApi = {
  listar: (buscar?: string): Promise<Activo[]> => {
    const url = buscar
      ? `${BASE_URL}/activos?buscar=${encodeURIComponent(buscar)}`
      : `${BASE_URL}/activos`;
    return fetchJson<Activo[]>(url);
  },

  obtener: (id: number): Promise<Activo> =>
    fetchJson<Activo>(`${BASE_URL}/activos/${id}`),

  crear: (data: ActivoRequest): Promise<Activo> =>
    fetchJson<Activo>(`${BASE_URL}/activos`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  actualizar: (id: number, data: ActivoRequest): Promise<Activo> =>
    fetchJson<Activo>(`${BASE_URL}/activos/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  eliminar: (id: number): Promise<void> =>
    fetchJson<void>(`${BASE_URL}/activos/${id}`, { method: 'DELETE' }),
};

// ====== Operaciones ======
export const operacionesApi = {
  listar: (activoId?: number, tipo?: TipoOperacion): Promise<Operacion[]> => {
    const params = new URLSearchParams();
    if (activoId) params.append('activoId', String(activoId));
    if (tipo) params.append('tipo', tipo);
    const query = params.toString() ? `?${params.toString()}` : '';
    return fetchJson<Operacion[]>(`${BASE_URL}/operaciones${query}`);
  },

  registrar: (data: OperacionRequest): Promise<Operacion> =>
    fetchJson<Operacion>(`${BASE_URL}/operaciones`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  eliminar: (id: number): Promise<void> =>
    fetchJson<void>(`${BASE_URL}/operaciones/${id}`, { method: 'DELETE' }),
};

// ====== Cartera ======
export const carteraApi = {
  resumen: (): Promise<ResumenCartera> =>
    fetchJson<ResumenCartera>(`${BASE_URL}/cartera/resumen`),
};
