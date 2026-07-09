import { useEffect, useState } from "react";
import { useWS } from "../ws/useWS";
import type { Mesa, Zona } from "./tables";
import { getMesas, getZonas } from "./tablesService";


export function useTablesData(
  token: string | null,
) {
  const [mesas, setMesas] = useState<Mesa[]>(
    [],
  );

  const [zonas, setZonas] = useState<Zona[]>(
    [],
  );

  const { mesas: mesasWS } = useWS();

  useEffect(() => {
    if (
      mesasWS &&
      mesasWS.length > 0
    ) {
      setMesas(mesasWS);
    }
  }, [mesasWS]);

  useEffect(() => {
    if (!token) return;

    const fetchData = async () => {
      try {
        const [mesasData, zonasData] =
          await Promise.all([
            getMesas(),
            getZonas(),
          ]);

        setMesas(mesasData);
        setZonas(zonasData);
      } catch (err) {
        console.error(err);
      }
    };

    fetchData();
  }, [token]);

  return {
    mesas,
    zonas,
    setMesas,
    setZonas
  };
}