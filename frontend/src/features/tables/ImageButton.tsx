import { icons, type TablesIconKey } from "./icons";
import "./styles.css";

type ButtonVariant = "default" | "danger";

type ImageButtonProps = {
  icon?: TablesIconKey | string;
  label: string;
  onClick?: () => void;
  loading?: boolean;
  variant?: ButtonVariant;
};

function ImageButton({
  icon,
  label,
  onClick,
  loading = false,
  variant = "default",
}: ImageButtonProps) {
  const iconSrc = icon && (icon in icons ? icons[icon as TablesIconKey] : icon);

  return (
    <button
      className={`image-button ${variant}`}
      onClick={onClick}
      disabled={loading}
      type="button"
    >
      <span className="image-button-content">
        {loading ? <span className="loader" /> : label}
      </span>

      {!loading && iconSrc && <img src={iconSrc} alt="" aria-hidden="true" />}
    </button>
  );
}

export default ImageButton;
