import { useState } from "react";
import { toast } from "sonner";
import ConfirmModal from "../../../common/ConfirmModal";
import { useSession } from "../../session/useSession";
import CartEmptyState from "../components/CartEmptyState";
import CartSuccessScreen from "../components/CartSuccessScreen";
import { useCartData } from "../hooks/useCartData";
import "../styles.css";

interface CartPageProps {
  onGoToMenu: () => void;
}

export default function CartPage({ onGoToMenu }: CartPageProps) {
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [botonLoading, setBotonLoading] = useState(false);
  const [pedidoRealizado, setPedidoRealizado] = useState(false);

  const { sessionCode } = useSession();

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
      setPedidoRealizado(true);
    } catch (err) {
      console.error(err);
      toast.error("No se pudo enviar el pedido", {
        description: "Revisa tu conexión e inténtalo de nuevo.",
      });
    } finally {
      setBotonLoading(false);
      setConfirmOpen(false);
    }
  };

  const handleVolverAlMenu = () => {
    setPedidoRealizado(false);
    onGoToMenu();
  };

  if (pedidoRealizado) {
    return (
      <div className="cart-page">
        <CartSuccessScreen onGoToMenu={handleVolverAlMenu} />
      </div>
    );
  }

  return (
    <div className="cart-page">
      <h1 className="cart-title">Carrito</h1>

      <div className="cart-items-wrapper">
        {cart.length === 0 ? (
          <CartEmptyState onGoToMenu={onGoToMenu} />
        ) : (
          cart.map((item) => {
            const modsString =
              item.modifiers
                ?.map((m) => m.id)
                .sort()
                .join(",") || "";
            const cartLineId = `${item.id}-${item.note || ""}-${modsString}`;

            const hasModifiers = item.modifiers && item.modifiers.length > 0;
            const hasNote = item.note && item.note.trim() !== "";

            return (
              <div key={cartLineId} className="cart-item">
                <div className="cart-row-main">
                  <div className="cart-info-left">
                    <h4 className="cart-product-name">{item.name}</h4>
                    <span className="cart-product-price">
                      {item.price.toFixed(2)} €
                    </span>
                  </div>

                  <div className="cart-qty-pill">
                    <button onClick={() => decreaseQuantity(cartLineId)}>
                      −
                    </button>
                    <span>{item.quantity}</span>
                    <button onClick={() => increaseQuantity(cartLineId)}>
                      +
                    </button>
                  </div>
                </div>

                {(hasModifiers || hasNote) && (
                  <div className="cart-row-details">
                    {hasModifiers && (
                      <ul className="cart-item-modifiers">
                        {item.modifiers!.map((mod) => (
                          <li key={mod.id}>+ {mod.nombre}</li>
                        ))}
                      </ul>
                    )}

                    {hasNote && (
                      <div className="cart-item-note">
                        <strong>Nota:</strong> {item.note}
                      </div>
                    )}
                  </div>
                )}
              </div>
            );
          })
        )}
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
