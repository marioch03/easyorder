import { useNavigate, useParams } from "react-router-dom";
import "./styles.css";

const ADMIN_OPTION = {
  key: "admin",
  title: "Administración",
  description: "Gestión de mesas, catálogo y usuarios.",
  to: "admin",
  icon: (
    <svg
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M12 3l7 3v5c0 4.5-2.9 8.2-7 10-4.1-1.8-7-5.5-7-10V6l7-3z" />
      <path d="M9.5 12l1.8 1.8L15 10" />
    </svg>
  ),
};

const SECONDARY_OPTIONS = [
  {
    key: "staff",
    title: "Personal",
    description: "Vista operativa para sala y gestión de pedidos.",
    to: "staff",
    icon: (
      <svg
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      >
        <circle cx="9" cy="8" r="3.2" />
        <path d="M3.5 19v-1.2A4 4 0 0 1 7.5 13.8h3a4 4 0 0 1 4 4V19" />
        <circle cx="17.5" cy="8.5" r="2.3" />
        <path d="M15.8 13.9a3.6 3.6 0 0 1 4.7 3.4V19" />
      </svg>
    ),
  },
  {
    key: "kds",
    title: "Cocina (KDS)",
    description: "Pantalla de comandas en tiempo real para la cocina.",
    to: "kds",
    icon: (
      <svg
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      >
        <rect x="2" y="3" width="20" height="13" rx="2" />
        <path d="M8 21h8" />
        <path d="M12 16v5" />
        <path d="M7 7.5h10" />
        <path d="M7 11.5h6" />
      </svg>
    ),
  },
];

export default function RoleSelectPage() {
  const navigate = useNavigate();
  const { slug } = useParams<{ slug: string }>();

  const handleNavigate = (targetPath: string) => {
    const currentSlug = slug || localStorage.getItem("tenant_slug");
    if (currentSlug) {
      navigate(`/${currentSlug}/${targetPath}`);
    } else {
      navigate("/error", { replace: true });
    }
  };

  return (
    <div className="login-page">
      <div className="role-select-shell">
        <div className="role-select-header">
          <div className="login-mark">LT</div>
          <h1>¿A qué panel quieres entrar?</h1>
          <p>Selecciona la interfaz a la que deseas acceder.</p>
        </div>

        <div className="role-select-options">
          <button
            type="button"
            className="role-select-card admin-card"
            onClick={() => handleNavigate(ADMIN_OPTION.to)}
          >
            <span className="role-select-icon">{ADMIN_OPTION.icon}</span>
            <span className="role-select-title">{ADMIN_OPTION.title}</span>
            <span className="role-select-description">
              {ADMIN_OPTION.description}
            </span>
          </button>

          <div className="role-select-row">
            {SECONDARY_OPTIONS.map((opt) => (
              <button
                key={opt.key}
                type="button"
                className="role-select-card"
                onClick={() => handleNavigate(opt.to)}
              >
                <span className="role-select-icon">{opt.icon}</span>
                <span className="role-select-title">{opt.title}</span>
                <span className="role-select-description">
                  {opt.description}
                </span>
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
