import { useEffect, useState } from "react";
import { useWS } from "../ws/useWS";
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

export function useTablesData(token: string | null) {
  const { mesas: mesasWS } = useWS();

  const [mesas, setMesas] = useState<Mesa[]>(mesasWS || []);
  const [zonas, setZonas] = useState<Zona[]>(leerZonasCache);
  const [loading, setLoading] = useState(!mesasWS || mesasWS.length === 0);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (mesasWS && mesasWS.length > 0) {
      setMesas(mesasWS);
      setLoading(false);
      setError(null);
    }
  }, [mesasWS]);

  useEffect(() => {
    if (!token) return;

    let cancelado = false;

    const fetchData = async () => {
      setLoading(true);
      setError(null);

      try {
        const [mesasData, zonasData] = await Promise.all([
          getMesas(),
          getZonas(),
        ]);

        if (cancelado) return;

        setMesas((prev) => (mesasWS && mesasWS.length > 0 ? prev : mesasData));
        setZonas(zonasData);
        localStorage.setItem("zonas", JSON.stringify(zonasData));
      } catch (err) {
        if (cancelado) return;
        console.error(err);
        setError(
          "No se han podido cargar las mesas. Comprueba tu conexión e inténtalo de nuevo.",
        );
      } finally {
        if (!cancelado) setLoading(false);
      }
    };

    fetchData();

    return () => {
      cancelado = true;
    };
  }, [token]);

  return {
    mesas,
    zonas,
    loading,
    error,
  };
}