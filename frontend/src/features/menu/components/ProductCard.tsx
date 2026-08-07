import { getAlergenoIcon } from "../../../common/allergens";
import "../styles.css";
import type { ProductDTO } from "../types/menu";

type ProductCardProps = {
  product: ProductDTO;
  onClick: (product: ProductDTO) => void;
};

const BASE_IMAGE_URL = "/images/";

export default function ProductCard({ product, onClick }: ProductCardProps) {
  return (
    <div className="product-card" onClick={() => onClick(product)}>
      <div className="product-content">
        <div className="product-info">
          <span className="product-name">{product.nombre}</span>
          <span className="product-price">€{product.precio.toFixed(2)}</span>
          <span className="product-description">{product.descripcion}</span>

          {product.alergenos.length > 0 && (
            <div className="product-allergens">
              {product.alergenos.map((alergeno) => {
                const icon = getAlergenoIcon(alergeno.nombre);
                if (!icon) return null;

                const esTraza = alergeno.tipo === "PUEDE_CONTENER_TRAZAS";

                return (
                  <span
                    key={alergeno.nombre}
                    className={`allergen-badge${esTraza ? " traza" : ""}`}
                    title={
                      esTraza
                        ? `Puede contener trazas de ${alergeno.nombre.toLowerCase()}`
                        : `Contiene ${alergeno.nombre.toLowerCase()}`
                    }
                  >
                    <img src={icon} alt={alergeno.nombre} />
                  </span>
                );
              })}
            </div>
          )}
        </div>

        <div className="product-image-wrapper">
          <img
            src={BASE_IMAGE_URL + product.imagen}
            alt={product.nombre}
            className="product-image"
          />
        </div>
      </div>
    </div>
  );
}
