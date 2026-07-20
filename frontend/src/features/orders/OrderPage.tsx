import { useMemo, useState } from "react";
import Modal from "react-modal";
import OrderCard from "./OrderCard";
import type { PedidoDTO } from "./orders";
import { marcarPedidoServido } from "./orderService";
import "./styles.css";
import { useOrdersData } from "./useOrdersData";

Modal.setAppElement("#root");

export default function OrderPage() {
  const token = localStorage.getItem("accessToken");
  const [pedidoSeleccionado, setPedidoSeleccionado] =
    useState<PedidoDTO | null>(null);
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const estadoClassName = (estado: string) =>
    "pedido-estado-" + estado.toLowerCase().replaceAll("_", "-");
  const [botonLoading, setBotonLoading] = useState(false);

  const { pedidos, loading, error } = useOrdersData();

  const pedidosPendientes = useMemo(() => {
    return pedidos.filter((pedido) => pedido.nombreEstado !== "SERVIDO");
  }, [pedidos]);

  const abrirModal = (pedido: PedidoDTO) => {
    setPedidoSeleccionado(pedido);
    setModalIsOpen(true);
  };

  const cerrarModal = () => {
    setPedidoSeleccionado(null);
    setModalIsOpen(false);
  };

  const onClickPedidoServido = async () => {
    if (!pedidoSeleccionado || !token || botonLoading) return;

    setBotonLoading(true);
    try {
      await marcarPedidoServido(pedidoSeleccionado.idPedido, "SERVIDO");

      cerrarModal();
    } catch (err) {
      console.error("Error al marcar pedido como servido:", err);
    } finally {
      setBotonLoading(false);
    }
  };

  if (loading) {
    return <div className="loading">Cargando pedidos…</div>;
  }

   if (error) {
    return <div className="loading error">{error}</div>;
  }

  return (
    <div className="main-container">
      {pedidosPendientes.length === 0 ? (
        <div className="empty-orders-state">
          <div className="empty-orders-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
              <path d="M4 12.5l5 5L20 7" />
            </svg>
          </div>
          <h2>¡Todo al día!</h2>
          <p>No hay pedidos pendientes por servir ahora mismo.</p>
        </div>
      ) : (
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
                Estado: {" "}
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
              <button
                className="servido-button"
                onClick={onClickPedidoServido}
                disabled={botonLoading}
              >
                {botonLoading ? <span className="loader" /> : "Marcar como servido"}
              </button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}