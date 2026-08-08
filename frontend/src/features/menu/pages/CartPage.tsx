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

        {cart.map((item) => {
          // 1. GENERAMOS EL ID ÚNICO DE LA LÍNEA PARA REACT Y PARA SUMAR/RESTAR
          const modsString =
            item.modifiers
              ?.map((m) => m.id)
              .sort()
              .join(",") || "";
          const cartLineId = `${item.id}-${item.note || ""}-${modsString}`;

          return (
            <div key={cartLineId} className="cart-item">
              <img src={BASE_IMAGE_URL + item.image} alt={item.name} />

              <div className="cart-info">
                <h4>{item.name}</h4>

                {/* 2. MOSTRAMOS LOS MODIFICADORES SELECCIONADOS */}
                {item.modifiers && item.modifiers.length > 0 && (
                  <ul className="cart-item-modifiers">
                    {item.modifiers.map((mod) => (
                      <li key={mod.id}>+ {mod.nombre}</li>
                    ))}
                  </ul>
                )}

                <p>{item.price.toFixed(2)} €</p>

                {item.note && item.note.trim() !== "" && (
                  <span className="cart-note-indicator">
                    Personalizado: {item.note}
                  </span>
                )}
              </div>

              <div className="cart-qty">
                <button onClick={() => decreaseQuantity(cartLineId)}>−</button>
                <span>{item.quantity}</span>
                <button onClick={() => increaseQuantity(cartLineId)}>+</button>
              </div>
            </div>
          );
        })}
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
