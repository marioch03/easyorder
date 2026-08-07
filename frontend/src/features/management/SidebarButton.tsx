import { icons, type ManagementIconKey } from "./icons";
import "./styles.css";

type SidebarButtonProps = {
  /** Nombre del icono definido en icons.ts ("table") o una URL/Data URI directa */
  icon: ManagementIconKey | string;
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
  const iconSrc = icon in icons ? icons[icon as ManagementIconKey] : icon;

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
        <img src={iconSrc} alt="" aria-hidden="true" />
      </span>
      <span>{label}</span>
    </button>
  );
}

export default SidebarButton;
