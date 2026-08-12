import { useEffect, useMemo, useState } from "react";
import type { ModificadorDTO } from "../../common/types";
import type { ProductoComandaDTO } from "./comandas";
import ConfigProductoModal from "./ConfigProductoModal"; // Ajusta la ruta
import "./styles.css";
import { useComandasData } from "./useComandasData";

const ESTADO_MESA_CLASE: Record<string, string> = {
  LIBRE: "libre",
  OCUPADA: "ocupada",
  ESPERANDO_CUENTA: "esperando-cuenta",
};

export default function ComandasPage() {
  // Ahora el hook nos trae tanto los datos como la lógica del carrito
  const {
    mesas,
    zonas,
    tiposProducto,
    productos,
    loading,
    error,
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
  } = useComandasData();

  // Estados locales solo para la interfaz (UI)
  const [tipoActivo, setTipoActivo] = useState<number | null>(null);
  const [productoConfigurando, setProductoConfigurando] =
    useState<ProductoComandaDTO | null>(null);
  const [mostrarTicketMovil, setMostrarTicketMovil] = useState(false);

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
    const primera = productosPorTipo.find((t) => t.productos.length > 0);
    if (primera) setTipoActivo(primera.id);
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

  // Al pulsar un producto, SIEMPRE se abre el modal (tenga modificadores o no) para poder añadir la nota
  const iniciarAgregarProducto = (producto: ProductoComandaDTO) => {
    setProductoConfigurando(producto);
  };

  const handleConfirmarConfig = (
    producto: ProductoComandaDTO,
    mods: ModificadorDTO[],
    nota: string,
  ) => {
    agregarLineaComanda(producto, mods, nota);
    setProductoConfigurando(null);
  };

  if (loading) return <div className="loading">Cargando comandas…</div>;
  if (error) return <div className="loading error">{error}</div>;

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
                className={`comandas-tab${tipoActivo === tipo.id ? " active" : ""}`}
                onClick={() => setTipoActivo(tipo.id)}
              >
                {tipo.nombre}
              </button>
            ))}
        </div>

        <div className="comandas-grid">
          {productosVisibles.map((producto) => (
            <button
              key={producto.id}
              className={`comanda-product${producto.disponible ? "" : " disabled"}`}
              onClick={() =>
                producto.disponible && iniciarAgregarProducto(producto)
              }
              disabled={!producto.disponible}
            >
              <span className="comanda-product-name">{producto.nombre}</span>
              <span className="comanda-product-price">
                {producto.precio.toFixed(2)} €
              </span>
            </button>
          ))}
        </div>
      </div>

      {/* ---------- Ticket Móvil Bar ---------- */}
      <div className="mobile-floating-bar">
        <div className="mobile-floating-info">
          <span className="mobile-floating-qty">{totalUnidades} artículos</span>
          <span className="mobile-floating-total">{total.toFixed(2)} €</span>
        </div>
        <button
          className="mobile-floating-btn"
          onClick={() => setMostrarTicketMovil(true)}
        >
          Ver Comanda
        </button>
      </div>

      {/* ---------- Ticket ---------- */}
      <aside className={`comanda-ticket ${mostrarTicketMovil ? "open" : ""}`}>
        <div className="comanda-ticket-header">
          <div className="comanda-ticket-header-title">
            <button
              className="mobile-close-ticket"
              onClick={() => setMostrarTicketMovil(false)}
            >
              ×
            </button>
            <h2>Comanda</h2>
          </div>
          {lineas.length > 0 && (
            <button className="comanda-clear" onClick={vaciarComanda}>
              Vaciar
            </button>
          )}
        </div>

        <div className="mesa-picker">
          {mesasPorZona.map(({ zona, mesas: mesasZona }) => (
            <div key={zona.id} className="mesa-picker-zone">
              <span className="mesa-picker-zone-label">{zona.nombre}</span>
              <div className="mesa-picker-chips">
                {mesasZona.map((mesa) => (
                  <button
                    key={mesa.id}
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
          {lineas.map((linea) => {
            const precioMods = linea.modificadores.reduce(
              (s, m) => s + m.precioExtra,
              0,
            );
            const precioTotalUnidad = linea.precio + precioMods;

            return (
              <div key={linea.key} className="comanda-linea">
                <div className="comanda-linea-top">
                  <div className="comanda-linea-info">
                    <span className="comanda-linea-nombre">{linea.nombre}</span>
                    {linea.modificadores.length > 0 && (
                      <span className="comanda-linea-mods">
                        {linea.modificadores
                          .map((m) => `+ ${m.nombre}`)
                          .join(", ")}
                      </span>
                    )}
                  </div>
                  <span className="comanda-linea-precio">
                    {(precioTotalUnidad * linea.cantidad).toFixed(2)} €
                  </span>
                </div>

                <div className="comanda-linea-bottom">
                  <div className="comanda-linea-qty">
                    <button onClick={() => cambiarCantidad(linea.key, -1)}>
                      −
                    </button>
                    <span>{linea.cantidad}</span>
                    <button onClick={() => cambiarCantidad(linea.key, 1)}>
                      +
                    </button>
                  </div>
                  <button
                    className="comanda-linea-nota-toggle"
                    onClick={() => abrirNota(linea)}
                  >
                    {linea.nota ? "Nota ✓" : "+ Nota"}
                  </button>
                  <button
                    className="comanda-linea-quitar"
                    onClick={() => quitarLinea(linea.key)}
                  >
                    ×
                  </button>
                </div>

                {notaAbierta === linea.key && (
                  <input
                    type="text"
                    className="comanda-linea-nota-input"
                    value={linea.nota}
                    onChange={(e) => actualizarNota(linea.key, e.target.value)}
                    autoFocus
                  />
                )}
              </div>
            );
          })}
        </div>

        <div className="comanda-footer">
          {enviarError && <p className="action-error">{enviarError}</p>}
          <button
            className="btn-primary comanda-submit"
            disabled={!mesaId || lineas.length === 0 || enviando}
            onClick={realizarPedido}
          >
            {mesaSeleccionada
              ? `Realizar pedido · Mesa ${mesaSeleccionada.numero}`
              : "Selecciona una mesa"}
          </button>
        </div>
      </aside>

      {productoConfigurando && (
        <ConfigProductoModal
          producto={productoConfigurando}
          onClose={() => setProductoConfigurando(null)}
          onConfirm={handleConfirmarConfig}
        />
      )}
    </div>
  );
}
