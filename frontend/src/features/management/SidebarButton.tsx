import "./styles.css";

type SidebarButtonProps = {
  icon: string;
  label: string;
  onClick?: () => void;
};

function SidebarButton({ icon, label, onClick }: SidebarButtonProps) {
  return (
    <button className="sidebar-button" onClick={onClick}>
      <img src={icon} alt={label} />
      <span>{label}</span>
    </button>
  );
}

export default SidebarButton;