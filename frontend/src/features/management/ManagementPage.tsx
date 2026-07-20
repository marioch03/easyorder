import { Outlet, useLocation, useNavigate } from "react-router-dom"; // o "react-router"
import Banner from "../../common/Banner";
import { logout } from "../auth/authService";
import Sidebar from "./Sidebar";

function ManagementPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const isPedidos = location.pathname.includes("/pedidos");
  const activeSection = isPedidos ? "pedidos" : "mesas";

  const handleSelectSection = (section: string) => {
    navigate(`/staff/${section}`);
  };

  const handleLogout = async () => {
    try {
      await logout();
    } catch (error) {
      console.error(error);
    }
    navigate("/auth/login");
  };

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <Sidebar
          activeSection={activeSection}
          onSelectSection={handleSelectSection}
          onLogout={handleLogout}
        />
      </aside>

      <main className="main-content">
        <Banner title={activeSection === "mesas" ? "Mesas" : "Pedidos"} />
        
        <Outlet />
      </main>
    </div>
  );
}

export default ManagementPage;