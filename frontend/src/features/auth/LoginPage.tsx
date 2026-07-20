import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "./authService";
import { getRolesFromToken } from "./jwtService";
import "./styles.css";

export default function LoginPage() {
  const [nombre, setNombre] = useState("");
  const [clave, setClave] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);

    try {
      await login({ nombre, clave });

      const accessToken = localStorage.getItem("accessToken");
      const roles = accessToken ? getRolesFromToken(accessToken) : [];

      if (roles.includes("ADMIN")) {
        navigate("/select-interface", { replace: true });
      } else if (roles.includes("KDS")) {
        navigate("/kds", { replace: true });
      } else if (roles.includes("PERSONAL")) {
        navigate("/staff", { replace: true });
      } else {
        navigate("/forbidden", { replace: true });
      }
    } catch (err) {
      console.error(err);
      setError("Usuario o contraseña incorrectos.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-shell">
        <div className="login-branding">
          <div className="login-branding-content">
            <div className="login-mark">PT</div>
            <h2>PideTÚ</h2>
            <p>Panel de administración</p>
            <ul className="login-branding-list">
              <li>Gestión de mesas en tiempo real</li>
              <li>Catálogo de productos centralizado</li>
              <li>Control de personal y roles</li>
            </ul>
          </div>
        </div>

        <div className="login-panel">
          <div className="login-card">
            <h1 className="login-title">Iniciar sesión</h1>
            <p className="login-subtitle">Accede con tus credenciales de acceso.</p>

            <form onSubmit={handleLogin} className="login-form">
              <div className="input-group">
                <svg className="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M16 21v-1.5a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4V21" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
                <input
                  type="text"
                  placeholder="Usuario"
                  value={nombre}
                  onChange={(e) => setNombre(e.target.value)}
                  className="login-input"
                  autoComplete="username"
                  required
                />
              </div>

              <div className="input-group">
                <svg className="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
                  <rect x="4" y="10.5" width="16" height="10" rx="2" />
                  <path d="M7.5 10.5V7a4.5 4.5 0 0 1 9 0v3.5" />
                </svg>
                <input
                  type="password"
                  placeholder="Contraseña"
                  value={clave}
                  onChange={(e) => setClave(e.target.value)}
                  className="login-input"
                  autoComplete="current-password"
                  required
                />
              </div>

              {error && <div className="login-error">{error}</div>}

              <button type="submit" className="login-button" disabled={isLoading}>
                {isLoading ? "Entrando…" : "Entrar"}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}