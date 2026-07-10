import "./styles.css";

type ImageButtonProps = {
  icon?: string;
  label: string;
  onClick?: () => void;
  loading?: boolean;
};

function ImageButton({
  icon,
  label,
  onClick,
  loading = false,
}: ImageButtonProps) {
  return (
    <button className="image-button" onClick={onClick} disabled={loading}>
      <span className="image-button-content">
        {loading ? <span className="loader" /> : label}
      </span>

      {!loading && icon && <img src={icon} alt={label} />}
    </button>
  );
}

export default ImageButton;
