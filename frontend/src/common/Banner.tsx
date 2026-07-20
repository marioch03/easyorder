import "./styles.css";

type PageBannerProps = {
  title: string;
  onExit?: () => void; // Prop opcional para manejar la salida
};

export default function PageBanner({ title, onExit }: PageBannerProps) {
  return (
    <div className="page-banner">
      <div>
        <p className="page-banner-kicker">Panel operativo</p>
        <h1>{title}</h1>
      </div>

      <div className="page-banner-right">
        <span className="page-banner-date">
          {new Date().toLocaleDateString("es-ES", {
            weekday: "long",
            day: "numeric",
            month: "long",
          })}
        </span>

        {onExit && (
          <button 
            type="button" 
            className="page-banner-exit-btn" 
            onClick={onExit}
            title="Salir del panel"
          >
            <svg 
              viewBox="0 0 24 24" 
              fill="none" 
              stroke="currentColor" 
              strokeWidth="2" 
              strokeLinecap="round" 
              strokeLinejoin="round"
              width="18"
              height="18"
            >
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
              <polyline points="16 17 21 12 16 7" />
              <line x1="21" y1="12" x2="9" y2="12" />
            </svg>
            <span>Salir</span>
          </button>
        )}
      </div>
    </div>
  );
}