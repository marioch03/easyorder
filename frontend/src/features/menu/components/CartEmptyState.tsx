interface CartEmptyStateProps {
  onGoToMenu: () => void;
}

export default function CartEmptyState({ onGoToMenu }: CartEmptyStateProps) {
  return (
    <div className="cart-empty-state">
      <div className="cart-empty-icon">
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
          <path
            d="M38 44H82L77 78C76.4 82 73 85 69 85H51C47 85 43.6 82 43 78L38 44Z"
            fill="var(--color-secondary)"
            opacity="0.25"
          />
          <path
            d="M38 44H82L77 78C76.4 82 73 85 69 85H51C47 85 43.6 82 43 78L38 44Z"
            stroke="var(--color-primary-dark)"
            strokeWidth="3"
            strokeLinejoin="round"
          />
          <path
            d="M46 44V36C46 29.4 51.4 24 58 24H62C68.6 24 74 29.4 74 36V44"
            stroke="var(--color-primary-dark)"
            strokeWidth="3"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
          <circle cx="30" cy="30" r="4" fill="var(--color-secondary)" />
          <circle cx="92" cy="36" r="3" fill="var(--color-primary)" />
          <circle cx="88" cy="90" r="3.5" fill="var(--color-secondary)" />
        </svg>
      </div>

      <h3 className="cart-empty-title">Tu carrito está vacío</h3>
      <p className="cart-empty-subtitle">
        Explora el menú y añade tus platos favoritos para empezar tu pedido.
      </p>

      <button className="cart-empty-cta" onClick={onGoToMenu}>
        Ver el menú
      </button>
    </div>
  );
}
