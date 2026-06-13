import { useState, useCallback } from 'react'

interface FetchState<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
}

/**
 * Hook genérico para hacer llamadas a la API.
 * Gestiona los estados de carga, error y datos.
 */
export function useFetch<T>(fetchFn: () => Promise<T>) {
  const [state, setState] = useState<FetchState<T>>({
    data: null,
    loading: true,
    error: null,
  })

  const execute = useCallback(async () => {
    setState(prev => ({ ...prev, loading: true, error: null }))
    try {
      const data = await fetchFn()
      setState({ data, loading: false, error: null })
    } catch (err) {
      const mensaje = err instanceof Error ? err.message : 'Error desconocido'
      setState({ data: null, loading: false, error: mensaje })
    }
  }, [fetchFn])

  return { ...state, refetch: execute }
}
