import { useCallback, useEffect, useMemo, useState } from "react";
import type { ProductoTipoDTO } from "../../common/types";
import type { ColumnaKds, PedidoItemKds } from "./kds";
import { getComandasKds, getTiposProductos, marcarListoItem } from "./kdsService";

const POLL_INTERVAL_MS = 20_000;
const CLOCK_TICK_MS = 30_000;

function useNow(intervalMs: number) {
  const [now, setNow] = useState(() => Date.now());

  useEffect(() => {
    const id = setInterval(() => setNow(Date.now()), intervalMs);
    return () => clearInterval(id);
  }, [intervalMs]);

  return now;
}

export function useKdsData(zonaTrabajo: string) {
  const [tipos, setTipos] = useState<ProductoTipoDTO[]>([]);
  const [items, setItems] = useState<PedidoItemKds[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const now = useNow(CLOCK_TICK_MS);

  const cargar = useCallback(async () => {
    try {
      const [tiposData, itemsData] = await Promise.all([
        getTiposProductos(zonaTrabajo),
        getComandasKds(zonaTrabajo),
      ]);

      setTipos(tiposData);
      setItems(itemsData);
      setError(null);
    } catch (err) {
      console.error(err);
      setError(
        "No se han podido cargar las comandas. Comprueba tu conexión e inténtalo de nuevo.",
      );
    } finally {
      setLoading(false);
    }
  }, [zonaTrabajo]);

  useEffect(() => {
    let cancelado = false;

    setLoading(true);
    cargar();

    const poll = setInterval(() => {
      if (!cancelado) cargar();
    }, POLL_INTERVAL_MS);

    return () => {
      cancelado = true;
      clearInterval(poll);
    };
  }, [cargar]);

  const marcarListo = useCallback(async (idItem: number) => {
    let itemEliminado: PedidoItemKds | undefined;
    setItems((prev) => {
      itemEliminado = prev.find((item) => item.id === idItem);
      return prev.filter((item) => item.id !== idItem);
    });

    try {
      await marcarListoItem(idItem);
    } catch (err) {
      console.error(err);
      setError("No se pudo marcar el producto como listo. Se ha restaurado.");
      if (itemEliminado) {
        const restaurado = itemEliminado;
        setItems((prev) => [...prev, restaurado]);
      }
    }
  }, []);

  const columnas: ColumnaKds[] = useMemo(() => {
    return tipos.map((tipo) => ({
      id: tipo.id,
      nombre: tipo.nombre,
      productos: items
        .filter((item) => item.idProductoTipo === tipo.id)
        .map((item) => ({
          ...item,
          tiempoEsperaMin: Math.max(
            0,
            Math.floor((now - new Date(item.createdAt).getTime()) / 60000),
          ),
        }))
        .sort((a, b) => b.tiempoEsperaMin - a.tiempoEsperaMin),
    }));
  }, [tipos, items, now]);

  return { columnas, loading, error, marcarListo, recargar: cargar };
}