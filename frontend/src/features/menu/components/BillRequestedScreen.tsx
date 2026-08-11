interface BillRequestedScreenProps {
  total: number;
}

export default function BillRequestedScreen({
  total,
}: BillRequestedScreenProps) {
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
            fill="var(--color-preparing)"
            opacity="0.15"
          />
          <circle
            cx="60"
            cy="60"
            r="40"
            stroke="var(--color-preparing)"
            strokeWidth="3"
          />
          <path
            d="M60 40V62L74 70"
            stroke="var(--color-preparing)"
            strokeWidth="5"
            strokeLinecap="round"
            strokeLinejoin="round"
            className="bill-requested-clock"
          />
        </svg>
      </div>

      <h3 className="order-success-title">Cuenta solicitada</h3>
      <p className="order-success-subtitle">
        Tu camarero se acercará en breve para cobrar. Total a pagar:{" "}
        <strong>{total.toFixed(2)} €</strong>
      </p>
    </div>
  );
}
