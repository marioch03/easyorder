import { useMemo, useState } from "react";
import Modal from "react-modal";
import OrderCard from "./OrderCard";
import { EstadoPedido, type PedidoDTO } from "./orders";
import { marcarPedidoServido } from "./orderService";
import "./styles.css";
import { useOrdersData } from "./useOrdersData";

Modal.setAppElement("#root");

const LEGEND = [
  { key: "pendiente", label: "Pendiente" },
  { key: "parcial", label: "Parcial" },
  { key: "listo", label: "Listo" },
];

const estadoClassName = (estado: string) =>
  "pedido-estado-" + estado.toLowerCase().replaceAll("_", "-");

const porAntiguedad = (a: PedidoDTO, b: PedidoDTO) =>
  new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();

export default function OrderPage() {
  const [pedidoSeleccionado, setPedidoSeleccionado] =
    useState<PedidoDTO | null>(null);
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [servidoLoadingId, setServidoLoadingId] = useState<number | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  const { pedidos, loading, error } = useOrdersData();

  const pedidosPendientes = useMemo(() => {
    return pedidos.filter(
      (pedido) => pedido.nombreEstado === EstadoPedido.PENDIENTE,
    );
  }, [pedidos]);

  const pedidosListos = useMemo(
    () =>
      pedidos
        .filter((p) => p.nombreEstado === EstadoPedido.LISTO)
        .sort(porAntiguedad),
    [pedidos],
  );

  const pedidosParciales = useMemo(
    () =>
      pedidos
        .filter((p) => p.nombreEstado === EstadoPedido.PARCIAL)
        .sort(porAntiguedad),
    [pedidos],
  );

  const abrirModal = (pedido: PedidoDTO) => {
    setPedidoSeleccionado(pedido);
    setActionError(null);
    setModalIsOpen(true);
  };

  const cerrarModal = () => {
    setPedidoSeleccionado(null);
    setModalIsOpen(false);
  };

  const marcarServido = async (idPedido: number) => {
    if (servidoLoadingId !== null) return;

    setServidoLoadingId(idPedido);
    setActionError(null);

    try {
      await marcarPedidoServido(idPedido, "SERVIDO");
      if (pedidoSeleccionado?.idPedido === idPedido) {
        cerrarModal();
      }
    } catch (err) {
      console.error("Error al marcar pedido como servido:", err);
      setActionError(
        "No se pudo marcar el pedido como servido. Inténtalo de nuevo.",
      );
    } finally {
      setServidoLoadingId(null);
    }
  };

  if (loading) {
    return <div className="loading">Cargando pedidos…</div>;
  }

  if (error) {
    return <div className="loading error">{error}</div>;
  }

  const hayPedidos =
    pedidosPendientes.length > 0 ||
    pedidosListos.length > 0 ||
    pedidosParciales.length > 0;

  return (
    <div className="main-container">
      {hayPedidos && (
        <div className="order-legend">
          {LEGEND.map((item) => (
            <span key={item.key} className="order-legend-item">
              <span className={`order-legend-dot ${item.key}`} />
              {item.label}
            </span>
          ))}
        </div>
      )}

      {!hayPedidos ? (
        <div className="empty-orders-state">
          <div className="empty-orders-icon">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="1.8"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M4 12.5l5 5L20 7" />
            </svg>
          </div>
          <h2>¡Todo al día!</h2>
          <p>No hay pedidos pendientes por servir ahora mismo.</p>
        </div>
      ) : (
        <>
          {pedidosListos.length > 0 && (
            <section className="pedidos-section">
              <h2 className="pedidos-section-title listos">
                Listos para servir
                <span className="pedidos-section-count">
                  {pedidosListos.length}
                </span>
              </h2>
              <div className="orders-container">
                {pedidosListos.map((pedido) => (
                  <OrderCard
                    key={pedido.idPedido}
                    idPedido={pedido.idPedido}
                    mesa={pedido.numeroMesa}
                    estado={pedido.nombreEstado}
                    createdAt={pedido.createdAt}
                    items={pedido.items.length}
                    onClick={() => abrirModal(pedido)}
                    onMarcarServido={() => marcarServido(pedido.idPedido)}
                    servidoLoading={servidoLoadingId === pedido.idPedido}
                  />
                ))}
              </div>
            </section>
          )}

          {pedidosParciales.length > 0 && (
            <section className="pedidos-section">
              <h2 className="pedidos-section-title">
                Listo parcialmente
                <span className="pedidos-section-count">
                  {pedidosParciales.length}
                </span>
              </h2>
              <div className="orders-container">
                {pedidosParciales.map((pedido) => (
                  <OrderCard
                    key={pedido.idPedido}
                    idPedido={pedido.idPedido}
                    mesa={pedido.numeroMesa}
                    estado={pedido.nombreEstado}
                    createdAt={pedido.createdAt}
                    items={pedido.items.length}
                    onClick={() => abrirModal(pedido)}
                  />
                ))}
              </div>
            </section>
          )}

          {pedidosPendientes.length > 0 && (
            <section className="pedidos-section">
              <h2 className="pedidos-section-title">
                En cocina
                <span className="pedidos-section-count">
                  {pedidosPendientes.length}
                </span>
              </h2>
              <div className="orders-container">
                {pedidosPendientes.map((pedido) => (
                  <OrderCard
                    key={pedido.idPedido}
                    idPedido={pedido.idPedido}
                    mesa={pedido.numeroMesa}
                    estado={pedido.nombreEstado}
                    createdAt={pedido.createdAt}
                    items={pedido.items.length}
                    onClick={() => abrirModal(pedido)}
                  />
                ))}
              </div>
            </section>
          )}
        </>
      )}

      <Modal
        isOpen={modalIsOpen}
        onRequestClose={cerrarModal}
        contentLabel="Detalle Pedido"
        className="modal-staff-container"
        overlayClassName="modal-staff-overlay"
      >
        {pedidoSeleccionado && (
          <div className="modal-items-orders">
            <div className="modal-staff-top">
              <h2>Pedido {pedidoSeleccionado.idPedido}</h2>
              <button className="close-button" onClick={cerrarModal}>
                ×
              </button>
            </div>
            <div className="modal-center-orders">
              <div>
                <span>Mesa {pedidoSeleccionado.numeroMesa}</span>
              </div>
              <div>
                Estado:{" "}
                <span
                  className={`estado-tag ${estadoClassName(
                    pedidoSeleccionado.nombreEstado,
                  )} order-state`}
                >
                  {pedidoSeleccionado.nombreEstado}
                </span>
              </div>
            </div>
            <div className="order-detail-list">
              {pedidoSeleccionado.items.map((item) => (
                <div className="order-detail-item" key={item.idProducto}>
                  <div className="order-detail-line">
                    <span className="order-detail-qty">{item.cantidad}×</span>
                    <span>{item.nombreProducto}</span>
                  </div>

                  {item.nota && (
                    <div className="order-detail-note">
                      <span>{item.nota}</span>
                    </div>
                  )}
                </div>
              ))}
            </div>

            <div className="modal-bottom-orders">
              {actionError && <p className="action-error">{actionError}</p>}
              <button
                className="servido-button"
                onClick={() => marcarServido(pedidoSeleccionado.idPedido)}
                disabled={servidoLoadingId === pedidoSeleccionado.idPedido}
              >
                {servidoLoadingId === pedidoSeleccionado.idPedido ? (
                  <span className="loader" />
                ) : (
                  "Marcar como servido"
                )}
              </button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}
