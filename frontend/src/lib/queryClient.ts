import { MutationCache, QueryCache, QueryClient } from "@tanstack/react-query";
import { parseApiError } from "../utils/errorHandler";

export const queryClient = new QueryClient({
  mutationCache: new MutationCache({
    onError: (error) => {
      // Normaliza el error globalmente (útil para logs o toasts)
      return parseApiError(error);
    },
  }),
  queryCache: new QueryCache({
    onError: (error) => {
      return parseApiError(error);
    },
  }),
  defaultOptions: {
    queries: {
      retry: (failureCount, error) => {
        const { statusCode } = parseApiError(error);
        // Si es un error del cliente (400-499), no reintentar
        if (statusCode >= 400 && statusCode < 500) return false;
        // Si es un error 5xx o de red, reintentar máximo 2 veces
        return failureCount < 2;
      },
      staleTime: 1000 * 60 * 5, // Datos frescos durante 5 minutos
      refetchOnWindowFocus: false, // No reconsultar al cambiar de pestaña
    },
    mutations: {
      retry: false, // Nunca reintentar mutaciones automáticamente
    },
  },
});

export default queryClient;
