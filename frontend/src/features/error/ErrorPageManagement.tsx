import React from 'react';
import { useNavigate } from 'react-router-dom';
import './styles.css'; // Importación del CSS separado

const Error404Page: React.FC = () => {
  const navigate = useNavigate();

  const handleGoBack = () => {
    if (window.history.length > 1) {
      navigate(-1);
    } else {
      navigate('/auth/login');
    }
  };

  return (
    <div className="error-management-container">
      <div className="error-management-content">
        <img 
          src="https://cdn.pixabay.com/photo/2021/07/21/12/49/error-6482984_1280.png" 
          alt="Error 404 - Ruta no encontrada" 
          className="error-management-image"
        />
        
        <h1 className="error-management-title">¡Oops! Te has perdido.</h1>
        <p className="error-management-subtitle">
          Parece que la mesa o el plato que buscas no están en nuestro menú hoy. 
          La página solicitada no existe o ha sido movida.
        </p>

        <div className="error-management-button-container">
          <button 
            onClick={handleGoBack} 
            className="btn btn-secondary"
          >
            ← Volver atrás
          </button>
          
          <button 
            onClick={() => navigate('/auth/login')} 
            className="btn btn-primary"
          >
            Ir al Inicio (Login)
          </button>
        </div>
        
        <span className="error-management-code">Código de error: 404 (Not Found)</span>
      </div>
    </div>
  );
};

export default Error404Page;