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

  const { pedidos } = useOrdersData(token);

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
    if (!pedidoSeleccionado || !token) return;

    try {
      await marcarPedidoServido(pedidoSeleccionado.idPedido, "SERVIDO");

      cerrarModal();
    } catch (err) {
      console.error("Error al marcar pedido como servido:", err);
    }
  };

  return (
    <div className="main-container">
      {pedidosPendientes.length === 0 ? (
        <div className="empty-orders-state">
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
        className="modal-container-orders"
        overlayClassName="modal-overlay-orders"
      >
        {pedidoSeleccionado && (
          <div className="modal-items-orders">
            <div className="modal-top-orders">
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
                Estado:
                <span
                  className={`estado-tag ${estadoClassName(
                    pedidoSeleccionado.nombreEstado,
                  )}`}
                >
                  {pedidoSeleccionado.nombreEstado}
                </span>
              </div>
            </div>
            <div className="pedido-items">
              {pedidoSeleccionado.items.map((item) => (
                <div className="pedido-item" key={item.idProducto}>
                  <div className="item-line">
                    <span>
                      {" "}
                      {item.cantidad} x {item.nombreProducto}
                    </span>
                  </div>

                  {item.nota && (
                    <div className="item-nota">
                      <span className="nota-label">Nota:</span> {item.nota}
                    </div>
                  )}
                </div>
              ))}
            </div>
            <div className="modal-bottom-orders">
              <button className="servido-button" onClick={onClickPedidoServido}>
                Servido
              </button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}
