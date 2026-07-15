import { useEffect, useState } from "react";
import { useWS } from "../ws/useWS";
import type { Mesa, Zona } from "./tables";
import { getMesas, getZonas } from "./tablesService";

export function useTablesData(token: string | null) {
  const { mesas: mesasWS } = useWS();

  const [mesas, setMesas] = useState<Mesa[]>(mesasWS || []);
  const [zonas, setZonas] = useState<Zona[]>(() => {
    const zonasLocal = localStorage.getItem("zonas");
    return zonasLocal ? JSON.parse(zonasLocal) : [];
  });
  const [loading, setLoading] = useState(!mesasWS || mesasWS.length === 0);

  useEffect(() => {
    if (mesasWS && mesasWS.length  > 0 ) {
      setMesas(mesasWS);
      setLoading(false);
    }
  }, [mesasWS]);

  useEffect(() => {
    if (!token) return;
    if (mesasWS && mesasWS.length > 0 && zonas.length > 0) {
      setLoading(false);
      return;
    }

    const fetchData = async () => {
      setLoading(true);
      try {
        const [mesasData, zonasData] = await Promise.all([
          getMesas(),
          getZonas(),
        ]);

        setMesas(mesasData);
        setZonas(zonasData);
        localStorage.setItem("zonas", JSON.stringify(zonasData));
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [token]);

  return {
    mesas,
    zonas,
    loading,
  };
}