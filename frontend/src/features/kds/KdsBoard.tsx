import "./styles.css";
import { useKdsData } from "./useKdsData";

interface KdsBoardProps {
  zonaTrabajo: string;
}

const getClaseTiempo = (minutos: number) => {
  if (minutos >= 15) return "late";
  if (minutos >= 10) return "warning";
  return "";
};

export default function KdsBoard({ zonaTrabajo }: KdsBoardProps) {
  const { columnas, loading, error, marcarListo } = useKdsData(zonaTrabajo);

  if (loading) {
    return <div className="kds-scope kds-status">Cargando comandas…</div>;
  }

  if (error) {
    return (
      <div className="kds-scope kds-status kds-status-error">{error}</div>
    );
  }


  return (
    <div className="kds-scope kds-container">
      <div
        className="kds-grid"
        style={{ gridTemplateColumns: `repeat(${columnas.length || 1}, 1fr)` }}
      >
        {columnas.map((columna) => (
          <div key={columna.id} className="kds-column">
            <h3 className="kds-column-header">
              <span>{columna.nombre}</span>
              <span className="kds-column-count">{columna.productos.length}</span>
            </h3>

            <div className="kds-product-list">
              {columna.productos.length === 0 ? (
                <div className="kds-column-empty">Sin comandas</div>
              ) : (
                columna.productos.map((producto) => (
                  <div
                    key={producto.id}
                    className={`kds-ticket-card ${getClaseTiempo(producto.tiempoEsperaMin)}`}
                  >
                    <div className="kds-ticket-header">
                      <span className="kds-qty-name">
                        <span className="kds-qty">{producto.cantidad}x</span>
                        {producto.nombre}
                      </span>
                      <span className="kds-time">{producto.tiempoEsperaMin} min</span>
                    </div>

                    {producto.nota && (
                      <div className="kds-notes">↳ {producto.nota}</div>
                    )}

                    <div className="kds-ticket-footer">
                      <span className="kds-table-name">Mesa {producto.mesa}</span>
                      <button
                        type="button"
                        className="kds-btn-ready"
                        onClick={() => marcarListo(producto.id)}
                      >
                        Listo
                      </button>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}