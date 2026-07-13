import "./styles.css";

type AdminButtonProps = {
  icon: string;
  label: string;
  onClick?: () => void;
  variant?: "default" | "danger";
  active?: boolean;
};

function AdminButton({
  icon,
  label,
  onClick,
  variant = "default",
  active = false,
}: AdminButtonProps) {
  const classNames = [
    "admin-button",
    variant === "danger" ? "danger" : "",
    active ? "active" : "",
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <button className={classNames} onClick={onClick} type="button">
      <img src={icon} alt="" aria-hidden="true" />
      <span>{label}</span>
    </button>
  );
}

export default AdminButton;