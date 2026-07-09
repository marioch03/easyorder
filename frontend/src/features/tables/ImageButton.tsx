import "./styles.css";

type ImageButtonProps = {
  icon: string;
  label: string;
  onClick?: () => void;
};

function ImageButton({ icon, label, onClick }: ImageButtonProps) {
  return (
    <button className="image-button" onClick={onClick}>
      <span>{label}</span>
      <img src={icon} alt={label} />
    </button>
  );
}

export default ImageButton;