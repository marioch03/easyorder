import logoutIcon from "../../assets/logout.png";
import ordersIcon from "../../assets/orders.png";
import tablesIcon from "../../assets/tables.png";
import SidebarButton from "./SidebarButton";
import "./styles.css";

type SidebarProps = {
  onSelectSection: (section: string) => void;
  onLogout: () => void;
};

function Sidebar({ onSelectSection, onLogout }: SidebarProps) {
  return (
    <div className="sidebar-container">
      <div className="app-logo">
        <img src="/images/logo.png" alt="App Logo" />
      </div>
      <div className="sidebar">
        <SidebarButton
          icon={tablesIcon}
          label="Mesas"
          onClick={() => onSelectSection("mesas")}
        />
        <SidebarButton
          icon={ordersIcon}
          label="Pedidos"
          onClick={() => onSelectSection("pedidos")}
        />
        <SidebarButton
          icon={logoutIcon}
          label="Salir"
          onClick={() => {
            if (confirm("¿Seguro que quiere cerrar sesión?")) {
              onLogout();
            }
          }}
        />
      </div>
    </div>
  );
}

export default Sidebar;
