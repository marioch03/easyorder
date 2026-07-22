import { useNavigate } from "react-router-dom";
import { logout } from "./authService";
import "./styles.css";

const KDS_ZONES = [
  {
    key: "cocina",
    title: "Cocina",
    description: "Gestión de platos calientes, plancha y freidora.",
    to: "/kds/cocina",
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
        <path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
      </svg>
    ),
  },
  {
    key: "barra",
    title: "Barra",
    description: "Gestión de bebidas, cócteles y postres.",
    to: "/kds/barra",
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
        <path d="M17 10l-5-5-5 5h10z" />
        <path d="M12 10v10" />
        <path d="M8 20h8" />
      </svg>
    ),
  },
];

export default function KdsSelectPage() {
  const navigate = useNavigate();

    const handleLogout = async () => {
      try {
        await logout();
      } catch (error) {
        console.error(error);
      }
      navigate("/auth/login");
    };

  return (
    <div className="login-page">
      <div className="role-select-shell">
        <div className="role-select-header">
          <div className="login-mark">KDS</div>
          <h1>¿Qué estación vas a gestionar?</h1>
          <p>Selecciona tu zona de trabajo actual.</p>
        </div>

        <div className="role-select-options">
          {KDS_ZONES.map((opt) => (
            <button
              key={opt.key}
              type="button"
              className="role-select-card"
              onClick={() => navigate(opt.to)}
            >
              <span className="role-select-icon">{opt.icon}</span>
              <span className="role-select-title">{opt.title}</span>
              <span className="role-select-description">{opt.description}</span>
            </button>
          ))}
        </div>

        <div className="role-select-footer">
          <button 
            type="button" 
            className="role-select-logout-btn"
            onClick={handleLogout}
          >
            Cerrar sesión
          </button>
        </div>
      </div>
    </div>
  );
}