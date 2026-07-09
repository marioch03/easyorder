import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "./authService";
import "./styles.css";

export default function LoginPage() {
  const [nombre, setNombre] = useState("");

  const [clave, setClave] = useState("");

  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      await login({ nombre, clave });

      navigate("/admin");
    } catch (err) {
      console.error(err);

      alert("Error en el login");
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <h1 className="login-title">Iniciar sesión</h1>

        <form onSubmit={handleLogin} className="login-form">
          <input
            type="text"
            placeholder="Usuario"
            value={nombre}
            onChange={(e) => setNombre(e.target.value)}
            className="login-input"
          />

          <input
            type="password"
            placeholder="Contraseña"
            value={clave}
            onChange={(e) => setClave(e.target.value)}
            className="login-input"
          />

          <button type="submit" className="login-button">
            Entrar
          </button>
        </form>
      </div>
    </div>
  );
}
