import React from "react";
import "./styles.css";

export const ErrorPageCliente: React.FC = () => {
  return (
    <div className="error-page-container">
      <div className="error-card">
        <div className="error-icon" role="img" aria-label="Alerta">
          ⚠️
        </div>
        <h1 className="error-title">Sesión no válida</h1>
        <p className="error-message">
          La sesión de esta mesa ha expirado o no es válida. Si cree que se
          trata de un error, por favor solicite asistencia al personal del
          establecimiento para obtener ayuda.
        </p>
        <footer className="error-footer">
          EasyOrder: Servicio Digital de Hostelería
        </footer>
      </div>
    </div>
  );
};

export default ErrorPageCliente;