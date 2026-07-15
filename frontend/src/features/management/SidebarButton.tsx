import "./styles.css";

type SidebarButtonProps = {
  icon: string;
  label: string;
  onClick?: () => void;
  active?: boolean;
  variant?: "default" | "danger";
};

function SidebarButton({
  icon,
  label,
  onClick,
  active = false,
  variant = "default",
}: SidebarButtonProps) {
  const classNames = [
    "sidebar-button",
    variant === "danger" ? "danger" : "",
    active ? "active" : "",
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <button className={classNames} onClick={onClick} type="button">
      <span className="sidebar-icon-badge">
        <img src={icon} alt="" aria-hidden="true" />
      </span>
      <span>{label}</span>
    </button>
  );
}

export default SidebarButton;