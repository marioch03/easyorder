import { useState } from "react";
import ConfirmModal from "../../../common/ConfirmModal";
import { useSession } from "../../session/useSession";
import { useCartData } from "../hooks/useCartData";
import "../styles.css";

export default function CartPage() {
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [botonLoading, setBotonLoading] = useState(false);

  const { sessionCode } = useSession();

  const BASE_IMAGE_URL = "/images/";

  const {
    cart,
    total,
    mesaBloqueada,
    increaseQuantity,
    decreaseQuantity,
    realizarPedido,
  } = useCartData(sessionCode);

  const handleRealizarPedido = async () => {
    if (botonLoading) return;

    setBotonLoading(true);

    try {
      await realizarPedido();
    } catch (err) {
      console.error(err);
    } finally {
      setBotonLoading(false);
      setConfirmOpen(false);
    }
  };

  return (
    <div className="cart-page">
      <h1 className="cart-title">Carrito</h1>

      <div className="cart-items-wrapper">
        {cart.length === 0 && <p>Tu carrito está vacío</p>}

        {cart.map((item) => (
          <div key={item.id} className="cart-item">
            <img src={BASE_IMAGE_URL + item.image} alt={item.name} />

            <div className="cart-info">
              <h4>{item.name}</h4>
              <p>{item.price.toFixed(2)} €</p>
              {item.note && item.note.trim() !== "" && (
                <span className="cart-note-indicator">Personalizado</span>
              )}
            </div>

            <div className="cart-qty">
              <button onClick={() => decreaseQuantity(item.id)}>−</button>

              <span>{item.quantity}</span>

              <button onClick={() => increaseQuantity(item.id)}>+</button>
            </div>
          </div>
        ))}
      </div>

      {cart.length > 0 && (
        <div className="cart-total">
          <div className="bill-divider" />
          <h2>Total: {total.toFixed(2)} €</h2>

          <button
            className="checkout-btn"
            disabled={mesaBloqueada}
            onClick={() => !mesaBloqueada && setConfirmOpen(true)}
          >
            Realizar pedido
          </button>
        </div>
      )}

      {confirmOpen && (
        <ConfirmModal
          open={confirmOpen}
          title="¿Confirmar pedido?"
          message="Se enviará el pedido a cocina."
          loading={botonLoading}
          onCancel={() => setConfirmOpen(false)}
          onConfirm={handleRealizarPedido}
        />
      )}
    </div>
  );
}
