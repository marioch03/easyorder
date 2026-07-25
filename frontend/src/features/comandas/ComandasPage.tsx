import { useEffect, useMemo, useState } from "react";
import type { OrderItemDTO } from "../../common/types";
import type { LineaComanda } from "./comandas";
import { crearPedidoAdmin } from "./comandasService";
import "./styles.css";
import { useComandasData } from "./useComandasData";

const ESTADO_MESA_CLASE: Record<string, string> = {
  LIBRE: "libre",
  OCUPADA: "ocupada",
  ESPERANDO_CUENTA: "esperando-cuenta",
};

export default function ComandasPage() {
  const { mesas, zonas, tiposProducto, productos, loading, error } =
    useComandasData();

  const [tipoActivo, setTipoActivo] = useState<number | null>(null);
  const [mesaId, setMesaId] = useState<number | null>(null);
  const [lineas, setLineas] = useState<LineaComanda[]>([]);
  const [notaAbierta, setNotaAbierta] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);
  const [enviarError, setEnviarError] = useState<string | null>(null);

  const productosPorTipo = useMemo(
    () =>
      tiposProducto.map((tipo) => ({
        ...tipo,
        productos: productos.filter((p) => p.idTipoProducto === tipo.id),
      })),
    [tiposProducto, productos],
  );

  useEffect(() => {
    if (tipoActivo !== null) return;
    const primeraConProductos = productosPorTipo.find(
      (t) => t.productos.length > 0,
    );
    if (primeraConProductos) setTipoActivo(primeraConProductos.id);
  }, [productosPorTipo, tipoActivo]);

  const mesasPorZona = useMemo(
    () =>
      zonas
        .map((zona) => ({
          zona,
          mesas: mesas.filter((m) => m.zona === zona.nombre),
        }))
        .filter((g) => g.mesas.length > 0),
    [zonas, mesas],
  );

  const mesaSeleccionada = mesas.find((m) => m.id === mesaId) ?? null;

  const productosVisibles =
    productosPorTipo.find((t) => t.id === tipoActivo)?.productos ?? [];

  const total = useMemo(
    () => lineas.reduce((acc, l) => acc + l.precio * l.cantidad, 0),
    [lineas],
  );

  const totalUnidades = useMemo(
    () => lineas.reduce((acc, l) => acc + l.cantidad, 0),
    [lineas],
  );

  const agregarProducto = (producto: ProductoComandaLike) => {
    setLineas((prev) => {
      const existente = prev.find(
        (l) => l.productoId === producto.id && l.nota === "",
      );
      if (existente) {
        return prev.map((l) =>
          l.key === existente.key ? { ...l, cantidad: l.cantidad + 1 } : l,
        );
      }
      return [
        ...prev,
        {
          key: `${producto.id}-${Date.now()}`,
          productoId: producto.id,
          nombre: producto.nombre,
          precio: producto.precio,
          cantidad: 1,
          nota: "",
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

  // Antes, escribir una nota en una línea agrupada (cantidad > 1) se la
  // aplicaba a TODAS las unidades de esa línea, porque solo hay un campo
  // `nota` por línea. Al abrir el editor, si hay más de 1 unidad, separamos
  // 1 en una línea propia (con su propia `key`) y dejamos el resto agrupado
  // tal cual — así la nota solo afecta a la unidad que de verdad la lleva.
  const abrirNota = (linea: LineaComanda) => {
    if (linea.cantidad > 1) {
      const nuevaKey = `${linea.productoId}-${Date.now()}`;

      setLineas((prev) => {
        const restoActualizado = prev.map((l) =>
          l.key === linea.key ? { ...l, cantidad: l.cantidad - 1 } : l,
        );
        return [...restoActualizado, { ...linea, key: nuevaKey, cantidad: 1 }];
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
      const items: OrderItemDTO[] = lineas.map((l) => ({
        idProducto: l.productoId,
        cantidad: l.cantidad,
        precioUnitario: l.precio,
        nota: l.nota.trim(),
      }));

      await crearPedidoAdmin({ items }, mesaId);

      vaciarComanda();
    } catch (err) {
      console.error("Error al enviar la comanda:", err);
      setEnviarError("No se pudo enviar la comanda. Inténtalo de nuevo.");
    } finally {
      setEnviando(false);
    }
  };

  if (loading) {
    return <div className="loading">Cargando comandas…</div>;
  }

  if (error) {
    return <div className="loading error">{error}</div>;
  }

  return (
    <div className="comandas-layout">
      {/* ---------- Catálogo ---------- */}
      <div className="comandas-catalog">
        <div className="comandas-tabs">
          {productosPorTipo
            .filter((t) => t.productos.length > 0)
            .map((tipo) => (
              <button
                key={tipo.id}
                type="button"
                className={`comandas-tab${tipoActivo === tipo.id ? " active" : ""}`}
                onClick={() => setTipoActivo(tipo.id)}
              >
                {tipo.nombre}
              </button>
            ))}
        </div>

        <div className="comandas-grid">
          {productosVisibles.length === 0 ? (
            <div className="comandas-empty">
              Esta categoría no tiene productos disponibles.
            </div>
          ) : (
            productosVisibles.map((producto) => (
              <button
                key={producto.id}
                type="button"
                className={`comanda-product${producto.disponible ? "" : " disabled"}`}
                onClick={() => producto.disponible && agregarProducto(producto)}
                disabled={!producto.disponible}
              >
                <span className="comanda-product-name">{producto.nombre}</span>
                <span className="comanda-product-price">
                  {producto.precio.toFixed(2)} €
                </span>
                {!producto.disponible && (
                  <span className="comanda-product-unavailable">
                    No disponible
                  </span>
                )}
              </button>
            ))
          )}
        </div>
      </div>

      {/* ---------- Ticket de la comanda ---------- */}
      <aside className="comanda-ticket">
        <div className="comanda-ticket-header">
          <h2>Comanda</h2>
          {lineas.length > 0 && (
            <button
              type="button"
              className="comanda-clear"
              onClick={vaciarComanda}
            >
              Vaciar
            </button>
          )}
        </div>

        <div className="mesa-picker">
          {mesasPorZona.length === 0 && (
            <span className="mesa-picker-empty">No hay mesas disponibles.</span>
          )}
          {mesasPorZona.map(({ zona, mesas: mesasZona }) => (
            <div key={zona.id} className="mesa-picker-zone">
              <span className="mesa-picker-zone-label">{zona.nombre}</span>
              <div className="mesa-picker-chips">
                {mesasZona.map((mesa) => (
                  <button
                    key={mesa.id}
                    type="button"
                    className={`mesa-chip ${ESTADO_MESA_CLASE[mesa.estado] ?? ""}${mesaId === mesa.id ? " selected" : ""}`}
                    onClick={() => setMesaId(mesa.id)}
                  >
                    {mesa.numero}
                  </button>
                ))}
              </div>
            </div>
          ))}
        </div>

        <div className="comanda-lineas">
          {lineas.length === 0 ? (
            <div className="comanda-lineas-empty">
              Toca un producto del catálogo para añadirlo a la comanda.
            </div>
          ) : (
            lineas.map((linea) => (
              <div key={linea.key} className="comanda-linea">
                <div className="comanda-linea-top">
                  <span className="comanda-linea-nombre">{linea.nombre}</span>
                  <span className="comanda-linea-precio">
                    {(linea.precio * linea.cantidad).toFixed(2)} €
                  </span>
                </div>

                <div className="comanda-linea-bottom">
                  <div className="comanda-linea-qty">
                    <button
                      type="button"
                      onClick={() => cambiarCantidad(linea.key, -1)}
                    >
                      −
                    </button>
                    <span>{linea.cantidad}</span>
                    <button
                      type="button"
                      onClick={() => cambiarCantidad(linea.key, 1)}
                    >
                      +
                    </button>
                  </div>

                  <button
                    type="button"
                    className="comanda-linea-nota-toggle"
                    onClick={() => abrirNota(linea)}
                  >
                    {linea.nota ? "Nota ✓" : "+ Nota"}
                  </button>

                  <button
                    type="button"
                    className="comanda-linea-quitar"
                    onClick={() => quitarLinea(linea.key)}
                    aria-label={`Quitar ${linea.nombre}`}
                  >
                    ×
                  </button>
                </div>

                {notaAbierta === linea.key && (
                  <input
                    type="text"
                    className="comanda-linea-nota-input"
                    placeholder="Ej. sin cebolla, para llevar…"
                    value={linea.nota}
                    onChange={(e) => actualizarNota(linea.key, e.target.value)}
                    autoFocus
                  />
                )}
              </div>
            ))
          )}
        </div>

        <div className="comanda-footer">
          {enviarError && <p className="action-error">{enviarError}</p>}

          <div className="comanda-total-row">
            <span className="comanda-total-label">
              Total {totalUnidades > 0 && `· ${totalUnidades} uds.`}
            </span>
            <span className="comanda-total-value">{total.toFixed(2)} €</span>
          </div>

          <button
            type="button"
            className="btn-primary comanda-submit"
            disabled={!mesaId || lineas.length === 0 || enviando}
            onClick={realizarPedido}
          >
            {enviando ? (
              <span className="loader" />
            ) : mesaSeleccionada ? (
              `Realizar pedido · Mesa ${mesaSeleccionada.numero}`
            ) : (
              "Selecciona una mesa"
            )}
          </button>
        </div>
      </aside>
    </div>
  );
}

type ProductoComandaLike = { id: number; nombre: string; precio: number };
