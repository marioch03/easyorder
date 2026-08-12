import { useCallback, useEffect, useMemo, useState } from "react";
import { toast } from "sonner";
import type {
  MesaDTO,
  ModificadorDTO,
  ProductoTipoDTO,
  ZonaDTO,
} from "../../common/types";
import { getMesas, getZonas } from "../tables/tablesService";
import type { LineaComanda, ProductoComandaDTO } from "./comandas";
import {
  crearPedidoAdmin,
  getProductosDisponibles,
  getTiposProductoDisponibles,
} from "./comandasService";

export function useComandasData() {
  // --- 📦 ESTADOS DE DATOS (CATÁLOGO Y MESAS) ---
  const [mesas, setMesas] = useState<MesaDTO[]>([]);
  const [zonas, setZonas] = useState<ZonaDTO[]>([]);
  const [tiposProducto, setTiposProducto] = useState<ProductoTipoDTO[]>([]);
  const [productos, setProductos] = useState<ProductoComandaDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // --- 🛒 ESTADOS DEL CARRITO Y PEDIDO ---
  const [lineas, setLineas] = useState<LineaComanda[]>([]);
  const [mesaId, setMesaId] = useState<number | null>(null);
  const [notaAbierta, setNotaAbierta] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);
  const [enviarError, setEnviarError] = useState<string | null>(null);

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

  // --- 🧮 MEMOS DE CÁLCULO ---
  const total = useMemo(
    () =>
      lineas.reduce((acc, l) => {
        const precioMods = l.modificadores.reduce(
          (sum, m) => sum + m.precioExtra,
          0,
        );
        return acc + (l.precio + precioMods) * l.cantidad;
      }, 0),
    [lineas],
  );

  const totalUnidades = useMemo(
    () => lineas.reduce((acc, l) => acc + l.cantidad, 0),
    [lineas],
  );

  // --- 🛠️ FUNCIONES DEL CARRITO Y ACCIONES ---
  const agregarLineaComanda = (
    producto: ProductoComandaDTO,
    modificadores: ModificadorDTO[],
    nota: string,
  ) => {
    setLineas((prev) => {
      const modsIds = modificadores
        .map((m) => m.id)
        .sort()
        .join("-");
      const key = `${producto.id}-mods:${modsIds}-nota:${nota}`;
      const existente = prev.find((l) => l.key === key);

      if (existente) {
        return prev.map((l) =>
          l.key === key ? { ...l, cantidad: l.cantidad + 1 } : l,
        );
      }

      return [
        ...prev,
        {
          key,
          productoId: producto.id,
          nombre: producto.nombre,
          precio: producto.precio,
          cantidad: 1,
          nota,
          modificadores,
        },
      ];
    });
  };

  const cambiarCantidad = (key: string, delta: number) => {
    setLineas((prev) =>
      prev
        .map((l) =>
          l.key === key ? { ...l, cantidad: l.cantidad + delta } : l,
        )
        .filter((l) => l.cantidad > 0),
    );
  };

  const actualizarNota = (key: string, nota: string) => {
    setLineas((prev) => prev.map((l) => (l.key === key ? { ...l, nota } : l)));
  };

  const abrirNota = (linea: LineaComanda) => {
    if (linea.cantidad > 1) {
      const nuevaKey = `${linea.key}-split-${Date.now()}`;
      setLineas((prev) => {
        const resto = prev.map((l) =>
          l.key === linea.key ? { ...l, cantidad: l.cantidad - 1 } : l,
        );
        return [...resto, { ...linea, key: nuevaKey, cantidad: 1 }];
      });
      setNotaAbierta(nuevaKey);
      return;
    }
    setNotaAbierta(notaAbierta === linea.key ? null : linea.key);
  };

  const quitarLinea = (key: string) => {
    setLineas((prev) => prev.filter((l) => l.key !== key));
    if (notaAbierta === key) setNotaAbierta(null);
  };

  const vaciarComanda = () => {
    setLineas([]);
    setMesaId(null);
    setEnviarError(null);
  };

  const realizarPedido = async () => {
    if (!mesaId || lineas.length === 0 || enviando) return;
    setEnviando(true);
    setEnviarError(null);

    try {
      const items = lineas.map((l) => ({
        idProducto: l.productoId,
        cantidad: l.cantidad,
        precioUnitario: l.precio,
        nota: l.nota.trim(),
        modificadores: l.modificadores.map((m) => m.id),
      }));

      await crearPedidoAdmin({ items, total }, mesaId);
      vaciarComanda();
      toast.success("Pedido enviado correctamente");
    } catch (err) {
      console.error(err);
      setEnviarError("Error al enviar la comanda.");
    } finally {
      setEnviando(false);
    }
  };

  return {
    mesas,
    zonas,
    tiposProducto,
    productos,
    loading,
    error,
    cargar,
    lineas,
    mesaId,
    setMesaId,
    notaAbierta,
    enviando,
    enviarError,
    total,
    totalUnidades,
    agregarLineaComanda,
    cambiarCantidad,
    actualizarNota,
    abrirNota,
    quitarLinea,
    vaciarComanda,
    realizarPedido,
  };
}
