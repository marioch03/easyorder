interface CartSuccessScreenProps {
  onGoToMenu: () => void;
}

export default function CartSuccessScreen({
  onGoToMenu,
}: CartSuccessScreenProps) {
  return (
    <div className="order-success-state">
      <div className="order-success-icon">
        <svg
          viewBox="0 0 120 120"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <circle
            cx="60"
            cy="60"
            r="56"
            fill="var(--color-background-secondary)"
          />
          <circle
            cx="60"
            cy="60"
            r="40"
            fill="var(--color-success)"
            opacity="0.15"
          />
          <circle
            cx="60"
            cy="60"
            r="40"
            stroke="var(--color-success)"
            strokeWidth="3"
          />
          <path
            d="M44 61L54 71L78 47"
            stroke="var(--color-success)"
            strokeWidth="5"
            strokeLinecap="round"
            strokeLinejoin="round"
            className="order-success-check"
          />
        </svg>
      </div>

      <h3 className="order-success-title">¡Pedido enviado!</h3>
      <p className="order-success-subtitle">
        Tu pedido ha llegado a cocina. En breve empezarán a prepararlo.
      </p>

      <button className="cart-empty-cta" onClick={onGoToMenu}>
        Volver al menú
      </button>
    </div>
  );
}
