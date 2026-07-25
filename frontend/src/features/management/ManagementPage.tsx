import { Outlet, useLocation, useNavigate } from "react-router-dom"; // o "react-router"
import Banner from "../../common/Banner";
import { logout } from "../auth/authService";
import Sidebar from "./Sidebar";

function ManagementPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const SECCIONES_VALIDAS = ["mesas", "comandas", "pedidos"];

  const segmento = location.pathname.split("/").pop() ?? "";
  const activeSection = SECCIONES_VALIDAS.includes(segmento)
    ? segmento
    : "mesas";

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

  const TITULOS: Record<string, string> = {
    mesas: "Mesas",
    comandas: "Comandas",
    pedidos: "Pedidos",
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
        <Banner title={TITULOS[activeSection] ?? "Mesas"} />
        <Outlet />
      </main>
    </div>
  );
}

export default ManagementPage;
