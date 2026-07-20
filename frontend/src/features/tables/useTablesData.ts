import { useCallback, useEffect, useState } from "react";
import { useSseSubscription } from "../../common/useSseSuscription";
import type { Mesa, Zona } from "./tables";
import { getMesas, getZonas } from "./tablesService";

function leerZonasCache(): Zona[] {
  try {
    const zonasLocal = localStorage.getItem("zonas");
    return zonasLocal ? JSON.parse(zonasLocal) : [];
  } catch {
    return [];
  }
}

export function useTablesData() { 
  const [mesas, setMesas] = useState<Mesa[]>([]);
  const [zonas, setZonas] = useState<Zona[]>(leerZonasCache);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const cargar = useCallback(async () => {
    try {
      const [mesasData, zonasData] = await Promise.all([
        getMesas(),
        getZonas(),
      ]);

      setMesas(mesasData);
      setZonas(zonasData);
      localStorage.setItem("zonas", JSON.stringify(zonasData));
      setError(null);
    } catch (err) {
      console.error(err);
      setError(
        "No se han podido cargar las mesas. Comprueba tu conexión e inténtalo de nuevo.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    setLoading(true);
    cargar();
  }, [cargar]);

  useSseSubscription({
    topic: "mesas",
    onRefresh: cargar,
  });

  return {
    mesas,
    zonas,
    loading,
    error,
    recargar: cargar,
  };
}