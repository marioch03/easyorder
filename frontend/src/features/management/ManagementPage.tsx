import { Outlet, useLocation, useNavigate, useParams } from "react-router-dom";
import Banner from "../../common/Banner";
import { logout } from "../auth/authService";
import Sidebar from "./Sidebar";

function ManagementPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { slug } = useParams<{ slug: string }>();

  const currentSlug = slug || localStorage.getItem("tenant_slug") || "";
  const SECCIONES_VALIDAS = ["mesas", "comandas", "pedidos"];

  const segmentos = location.pathname.split("/").filter(Boolean);
  const segmento = segmentos[segmentos.length - 1] ?? "";

  const activeSection = SECCIONES_VALIDAS.includes(segmento)
    ? segmento
    : "mesas";

  const handleSelectSection = (section: string) => {
    if (currentSlug) {
      navigate(`/${currentSlug}/staff/${section}`);
    } else {
      navigate("/error", { replace: true });
    }
  };

  const handleLogout = async () => {
    try {
      await logout();
    } catch (error) {
      console.error(error);
    }

    if (currentSlug) {
      navigate(`/${currentSlug}/auth/login`, { replace: true });
    } else {
      navigate("/error", { replace: true });
    }
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
