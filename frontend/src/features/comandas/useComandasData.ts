import { useCallback, useEffect, useState } from "react";
import type { MesaDTO, ProductoTipoDTO, ZonaDTO } from "../../common/types";
import { getMesas, getZonas } from "../tables/tablesService";
import type { ProductoComandaDTO } from "./comandas";
import {
  getProductosDisponibles,
  getTiposProductoDisponibles,
} from "./comandasService";

export function useComandasData() {
  const [mesas, setMesas] = useState<MesaDTO[]>([]);
  const [zonas, setZonas] = useState<ZonaDTO[]>([]);
  const [tiposProducto, setTiposProducto] = useState<ProductoTipoDTO[]>([]);
  const [productos, setProductos] = useState<ProductoComandaDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const cargar = useCallback(async () => {
    try {
      const [mesasData, zonasData, tiposData, productosData] =
        await Promise.all([
          getMesas(),
          getZonas(),
          getTiposProductoDisponibles(),
          getProductosDisponibles(),
        ]);

      setMesas(mesasData);
      setZonas(zonasData);
      setTiposProducto(tiposData);
      setProductos(productosData);
      setError(null);
    } catch (err) {
      console.error(err);
      setError(
        "No se han podido cargar mesas o catálogo. Comprueba tu conexión e inténtalo de nuevo.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    setLoading(true);
    cargar();
  }, [cargar]);

  return { mesas, zonas, tiposProducto, productos, loading, error };
}
