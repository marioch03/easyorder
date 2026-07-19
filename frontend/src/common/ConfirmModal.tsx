import "./styles.css";

type ConfirmModalProps = {
  open: boolean;
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  loading?: boolean;
  variant?: "default" | "danger";
  onConfirm: () => void;
  onCancel: () => void;
};

export default function ConfirmModal({
  open,
  title,
  message,
  confirmText = "Confirmar",
  cancelText = "Cancelar",
  loading = false,
  variant,
  onConfirm,
  onCancel,
}: ConfirmModalProps) {
  if (!open) return null;

  return (
    <div className="confirm-overlay">
      <div className="confirm-modal">
        <h3>{title}</h3>

        <p>{message}</p>

        <div className="confirm-actions">
          <button className="cancel-btn" onClick={onCancel} disabled={loading}>
            {cancelText}
          </button>

          <button
            className={`confirm-btn${variant === "danger" ? " danger" : ""}`}
            onClick={onConfirm}
            disabled={loading}
          >
            {loading ? <span className="loader" /> : confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}
