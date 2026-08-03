import { getAlergenoIcon } from "../../../common/allergens";
import "../styles.css";
import type { Alergeno } from "../types/menu";

type Props = {
  isOpen: boolean;
  onClose: () => void;
  alergenos: Alergeno[];
};

export default function AllergenLegendModal({
  isOpen,
  onClose,
  alergenos,
}: Props) {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="legend-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Información de Alérgenos</h2>
          <button className="close-btn" onClick={onClose}>
            ✕
          </button>
        </div>

        <div className="legend-type-info">
          <div className="legend-type-item">
            <span className="allergen-badge">
              <img src={getAlergenoIcon("gluten")} alt="Contiene" />
            </span>
            <span>
              <strong>Contiene:</strong> Presencia directa en la receta
            </span>
          </div>
          <div className="legend-type-item">
            <span className="allergen-badge traza">
              <img src={getAlergenoIcon("gluten")} alt="Trazas" />
            </span>
            <span>
              <strong>Trazas:</strong> Posible contaminación cruzada
            </span>
          </div>
        </div>

        <hr className="legend-divider" />

        <div className="legend-list">
          {alergenos.map((item) => {
            const iconSrc = getAlergenoIcon(item.nombre);
            if (!iconSrc) return null;

            return (
              <div key={item.nombre} className="legend-row">
                <span className="allergen-badge">
                  <img src={iconSrc} alt={item.nombre} />
                </span>
                <div className="legend-text">
                  <strong className="legend-title">{item.nombre}</strong>
                  {item.descripcion && (
                    <p className="legend-description">{item.descripcion}</p>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
