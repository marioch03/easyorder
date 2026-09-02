import { useEffect, useMemo, useState } from "react";
import { toast } from "sonner";
import { getAlergenoIcon } from "../../../common/allergens";
import type { ModificadorDTO } from "../../../common/types";
import type { CartItem } from "../../cart/cartStore";
import { useCartStore } from "../../cart/cartStore";
import "../styles.css";
import type { ProductDTO } from "../types/menu";

type Props = {
  product: ProductDTO | null;
  onClose: () => void;
};

const BASE_IMAGE_URL = "/images/";

export default function ProductModal({ product, onClose }: Props) {
  const [quantity, setQuantity] = useState(1);
  const [note, setNote] = useState("");
  const [selectedModifiers, setSelectedModifiers] = useState<
    Record<number, ModificadorDTO[]>
  >({});
  const addItem = useCartStore((state) => state.addItem);

  useEffect(() => {
    setQuantity(1);
    setNote("");
    setSelectedModifiers({});
  }, [product]);

  const handleModifierToggle = (
    groupId: number,
    mod: ModificadorDTO,
    max: number,
  ) => {
    setSelectedModifiers((prev) => {
      const currentGroupSelections = prev[groupId] || [];
      const isAlreadySelected = currentGroupSelections.some(
        (m) => m.id === mod.id,
      );

      if (isAlreadySelected) {
        // Si ya está seleccionado, lo quitamos
        return {
          ...prev,
          [groupId]: currentGroupSelections.filter((m) => m.id !== mod.id),
        };
      } else {
        // Si no está seleccionado, intentamos añadirlo
        if (max === 1) {
          // Comportamiento Radio Button (reemplaza el anterior)
          return { ...prev, [groupId]: [mod] };
        } else if (currentGroupSelections.length < max) {
          // Comportamiento Checkbox (añade a la lista)
          return { ...prev, [groupId]: [...currentGroupSelections, mod] };
        }
        // Si ya llegó al máximo, no hacemos nada
        return prev;
      }
    });
  };

  const isSelectionValid = useMemo(() => {
    if (!product?.gruposModificadores) return true;
    return product.gruposModificadores.every((grupo) => {
      const seleccionados = selectedModifiers[grupo.id]?.length || 0;
      return seleccionados >= grupo.seleccionMinima;
    });
  }, [product, selectedModifiers]);

  const { unitPrice, totalPrice } = useMemo(() => {
    if (!product) return { unitPrice: 0, totalPrice: 0 };

    const extraPrice = Object.values(selectedModifiers)
      .flat()
      .reduce((sum, mod) => sum + mod.precioExtra, 0);

    const unit = product.precio + extraPrice;
    return { unitPrice: unit, totalPrice: unit * quantity };
  }, [product, selectedModifiers, quantity]);

  const handleAdd = () => {
    if (!product || !isSelectionValid) return;

    const flatModifiers = Object.values(selectedModifiers).flat();

    const item: CartItem = {
      id: product.id,
      name: product.nombre,
      price: unitPrice,
      quantity: quantity,
      image: product.imagen ? product.imagen : "food.png",
      note: note,
      modifiers: flatModifiers,
    };

    addItem(item);
    toast.success(`${product.nombre} añadido al carrito`, {
      description: `${quantity} ud. · ${(unitPrice * quantity).toFixed(2)} €`,
    });
    onClose();
  };

  if (!product) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="bottom-sheet" onClick={(e) => e.stopPropagation()}>
        <div className="drag-indicator"></div>

        <div className="modal-header">
          <h2>{product.nombre}</h2>
          <button className="close-btn" onClick={onClose}>
            ✕
          </button>
        </div>
        <div className="image-wrapper-modal">
          <img
            src={BASE_IMAGE_URL + product.imagen}
            alt={product.nombre}
            className="modal-image"
          />
        </div>
        <h3>Descripción:</h3>
        <p className="modal-description">{product.descripcion}</p>

        {product.alergenos && product.alergenos.length > 0 && (
          <div className="modal-allergens-section">
            <h3>Alérgenos:</h3>
            <div className="product-allergens">
              {product.alergenos.map((item, index) => {
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
            €{(product.precio * quantity).toFixed(2)}
          </span>
        </div>

        {product.gruposModificadores &&
          product.gruposModificadores.length > 0 && (
            <div className="modal-modifiers-section">
              {product.gruposModificadores.map((grupo) => {
                const seleccionados = selectedModifiers[grupo.id] || [];
                const isRequired = grupo.seleccionMinima > 0;
                const hasMetMinimum =
                  seleccionados.length >= grupo.seleccionMinima;

                return (
                  <div key={grupo.id} className="modifier-group">
                    <div className="modifier-group-header">
                      <h3>{grupo.nombre}</h3>
                      <span className="modifier-rules">
                        {isRequired ? (
                          <span
                            style={{ color: hasMetMinimum ? "green" : "red" }}
                          >
                            Obligatorio (Elige {grupo.seleccionMinima})
                          </span>
                        ) : (
                          <span>Opcional (Máx {grupo.seleccionMaxima})</span>
                        )}
                      </span>
                    </div>

                    <div className="modifier-options">
                      {grupo.modificadores.map((mod) => {
                        const isSelected = seleccionados.some(
                          (m) => m.id === mod.id,
                        );

                        return (
                          <label
                            key={mod.id}
                            className={`modifier-option ${isSelected ? "selected" : ""}`}
                          >
                            <input
                              type={
                                grupo.seleccionMaxima === 1
                                  ? "radio"
                                  : "checkbox"
                              }
                              name={`grupo-${grupo.id}`}
                              checked={isSelected}
                              onChange={() =>
                                handleModifierToggle(
                                  grupo.id,
                                  mod,
                                  grupo.seleccionMaxima,
                                )
                              }
                            />
                            <span className="mod-name">{mod.nombre}</span>
                            {mod.precioExtra > 0 && (
                              <span className="mod-price">
                                +€{mod.precioExtra.toFixed(2)}
                              </span>
                            )}
                          </label>
                        );
                      })}
                    </div>
                  </div>
                );
              })}
            </div>
          )}
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
