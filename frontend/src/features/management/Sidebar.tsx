import { useState } from "react";
import logoutIcon from "../../assets/logout.png";
import ordersIcon from "../../assets/orders.png";
import tablesIcon from "../../assets/tables.png";
import ConfirmModal from "../../common/ConfirmModal";
import SidebarButton from "./SidebarButton";
import "./styles.css";

type SidebarProps = {
  activeSection: string;
  onSelectSection: (section: string) => void;
  onLogout: () => Promise<void>;
};

function Sidebar({ activeSection, onSelectSection, onLogout }: SidebarProps) {
  const [confirmLogoutOpen, setConfirmLogoutOpen] = useState(false);
  const [logoutLoading, setLogoutLoading] = useState(false);

  const handleLogout = async () => {
    try {
      setLogoutLoading(true);

      await onLogout();

      setConfirmLogoutOpen(false);
    } catch (error) {
      console.error(error);
    } finally {
      setLogoutLoading(false);
    }
  };

  return (
    <div className="sidebar-container">
      <div className="app-logo">
        <img src="/images/logo.png" alt="App Logo" />
      </div>

      <nav className="sidebar-nav">
        <SidebarButton
          icon={tablesIcon}
          label="Mesas"
          active={activeSection === "mesas"}
          onClick={() => onSelectSection("mesas")}
        />

        <SidebarButton
          icon={ordersIcon}
          label="Pedidos"
          active={activeSection === "pedidos"}
          onClick={() => onSelectSection("pedidos")}
        />

        <SidebarButton
          icon={tablesIcon}
          label="Comandas"
          active={activeSection === "comandas"}
          onClick={() => onSelectSection("comandas")}
        />
      </nav>

      <div className="sidebar-footer">
        <SidebarButton
          icon={logoutIcon}
          label="Salir"
          variant="danger"
          onClick={() => setConfirmLogoutOpen(true)}
        />
      </div>

      <ConfirmModal
        open={confirmLogoutOpen}
        title="¿Cerrar sesión?"
        message="Se cerrará la sesión en este dispositivo."
        loading={logoutLoading}
        variant="danger"
        onCancel={() => setConfirmLogoutOpen(false)}
        onConfirm={handleLogout}
      />
    </div>
  );
}
export default Sidebar;
