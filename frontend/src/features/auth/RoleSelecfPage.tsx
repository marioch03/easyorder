import { useNavigate } from "react-router-dom";
import "./styles.css";

const OPTIONS = [
  {
    key: "admin",
    title: "Administración",
    description: "Gestión de mesas, catálogo y usuarios.",
    to: "/admin",
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
        <path d="M12 3l7 3v5c0 4.5-2.9 8.2-7 10-4.1-1.8-7-5.5-7-10V6l7-3z" />
        <path d="M9.5 12l1.8 1.8L15 10" />
      </svg>
    ),
  },
  {
    key: "staff",
    title: "Personal",
    description: "Vista operativa para sala y cocina.",
    to: "/staff",
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="9" cy="8" r="3.2" />
        <path d="M3.5 19v-1.2A4 4 0 0 1 7.5 13.8h3a4 4 0 0 1 4 4V19" />
        <circle cx="17.5" cy="8.5" r="2.3" />
        <path d="M15.8 13.9a3.6 3.6 0 0 1 4.7 3.4V19" />
      </svg>
    ),
  },
];

export default function RoleSelectPage() {
  const navigate = useNavigate();

  return (
    <div className="login-page">
      <div className="role-select-shell">
        <div className="role-select-header">
          <div className="login-mark">LT</div>
          <h1>¿A qué panel quieres entrar?</h1>
          <p>Tu cuenta tiene acceso a ambas interfaces.</p>
        </div>

        <div className="role-select-options">
          {OPTIONS.map((opt) => (
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
      </div>
    </div>
  );
}