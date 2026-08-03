import { useEffect, useState } from "react";
import { getAlergenoIcon } from "../../../common/allergens";
import type { CartItem } from "../../cart/cart";
import { useCart } from "../../cart/useCart";
import "../styles.css";
import type { Product } from "../types/menu";

type Props = {
  product: Product | null;
  onClose: () => void;
};

const BASE_IMAGE_URL = "/images/";

export default function ProductModal({ product, onClose }: Props) {
  const [quantity, setQuantity] = useState(1);
  const [note, setNote] = useState("");
  const { addItem } = useCart();

  useEffect(() => {
    setQuantity(1);
    setNote("");
  }, [product]);

  const handleAdd = () => {
    if (!product) return;

    const item: CartItem = {
      id: product.id,
      name: product.name,
      price: product.price,
      quantity: quantity,
      image: product.imageUrl,
      note: note,
    };

    addItem(item);
    onClose();
  };

  if (!product) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="bottom-sheet" onClick={(e) => e.stopPropagation()}>
        <div className="drag-indicator"></div>

        <div className="modal-header">
          <h2>{product.name}</h2>
          <button className="close-btn" onClick={onClose}>
            ✕
          </button>
        </div>
        <div className="image-wrapper-modal">
          <img
            src={BASE_IMAGE_URL + product.imageUrl}
            alt={product.name}
            className="modal-image"
          />
        </div>
        <h3>Descripción:</h3>
        <p className="modal-description">{product.description}</p>

        {product.allergens && product.allergens.length > 0 && (
          <div className="modal-allergens-section">
            <h3>Alérgenos:</h3>
            <div className="product-allergens">
              {product.allergens.map((item, index) => {
                const iconSrc = getAlergenoIcon(item.nombre);
                if (!iconSrc) return null;
                const isTraza = item.tipo === "PUEDE_CONTENER_TRAZAS";

                return (
                  <span
                    key={index}
                    className={`allergen-badge ${isTraza ? "traza" : ""}`}
                    title={`${item.nombre}${isTraza ? " (Trazas)" : ""}`}
                  >
                    <img src={iconSrc} alt={item.nombre} />
                  </span>
                );
              })}
            </div>
          </div>
        )}

        <div className="modal-order">
          <div className="quantity-selector">
            <button onClick={() => setQuantity(Math.max(1, quantity - 1))}>
              −
            </button>
            <span>{quantity}</span>
            <button onClick={() => setQuantity(quantity + 1)}>+</button>
          </div>

          <span className="modal-price">
            €{(product.price * quantity).toFixed(2)}
          </span>
        </div>
        <div className="modal-note">
          <h3>Nota adicional</h3>

          <textarea
            className="modal-note-input"
            placeholder="Ej: sin cebolla, poco hecho, alergia..."
            value={note}
            onChange={(e) => setNote(e.target.value)}
            rows={3}
          />
        </div>

        <button className="add-cart-btn" onClick={handleAdd}>
          Añadir al carrito
        </button>
      </div>
    </div>
  );
}
