import { useEffect, useMemo, useState } from "react";
import { toast } from "sonner";
import type { GrupoModificadorDTO, ModificadorDTO } from "../../common/types"; // Ajusta ruta
import type { ProductoComandaDTO } from "./comandas";

interface ConfigProductoModalProps {
  producto: ProductoComandaDTO;
  onClose: () => void;
  onConfirm: (
    producto: ProductoComandaDTO,
    modificadores: ModificadorDTO[],
    nota: string,
  ) => void;
}

export default function ConfigProductoModal({
  producto,
  onClose,
  onConfirm,
}: ConfigProductoModalProps) {
  const [modsSeleccionados, setModsSeleccionados] = useState<ModificadorDTO[]>(
    [],
  );
  const [notaConfig, setNotaConfig] = useState("");

  useEffect(() => {
    setModsSeleccionados([]);
    setNotaConfig("");
  }, [producto]);

  const handleToggleModificador = (
    mod: ModificadorDTO,
    grupo: GrupoModificadorDTO,
  ) => {
    const isSelected = modsSeleccionados.some((m) => m.id === mod.id);
    const modsInGroup = modsSeleccionados.filter((m) =>
      grupo.modificadores.some((gm) => gm.id === m.id),
    );

    if (isSelected) {
      setModsSeleccionados((prev) => prev.filter((m) => m.id !== mod.id));
    } else {
      if (grupo.seleccionMaxima === 1) {
        // Modo Radio
        setModsSeleccionados((prev) => [
          ...prev.filter(
            (m) => !grupo.modificadores.some((gm) => gm.id === m.id),
          ),
          mod,
        ]);
      } else if (modsInGroup.length < grupo.seleccionMaxima) {
        // Modo Checkbox
        setModsSeleccionados((prev) => [...prev, mod]);
      } else {
        toast.warning(
          `Máximo ${grupo.seleccionMaxima} opciones en ${grupo.nombre}`,
        );
      }
    }
  };

  const configuracionValida = useMemo(() => {
    if (!producto.gruposModificadores) return true;
    return producto.gruposModificadores.every((grupo) => {
      const seleccionadosDelGrupo = modsSeleccionados.filter((m) =>
        grupo.modificadores.some((gm) => gm.id === m.id),
      ).length;
      return seleccionadosDelGrupo >= grupo.seleccionMinima;
    });
  }, [producto, modsSeleccionados]);

  const precioActualConfig = useMemo(() => {
    const precioMods = modsSeleccionados.reduce(
      (sum, m) => sum + m.precioExtra,
      0,
    );
    return producto.precio + precioMods;
  }, [producto, modsSeleccionados]);

  return (
    <div className="config-modal-overlay" onClick={onClose}>
      <div
        className="config-modal-content"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="config-modal-header">
          <h3>{producto.nombre}</h3>
          <button className="config-modal-close" onClick={onClose}>
            ×
          </button>
        </div>

        <div className="config-modal-body">
          {/* Muestra los modificadores SOLO si el producto los tiene */}
          {producto.gruposModificadores?.map((grupo) => (
            <div key={grupo.id} className="modifier-group">
              <div className="modifier-group-header">
                <h4>{grupo.nombre}</h4>
                <span className="modifier-group-rules">
                  {grupo.seleccionMinima > 0
                    ? `Elige al menos ${grupo.seleccionMinima}`
                    : "Opcional"}
                  {grupo.seleccionMaxima > 1
                    ? ` (Máx ${grupo.seleccionMaxima})`
                    : ""}
                </span>
              </div>
              <div className="modifier-grid">
                {grupo.modificadores.map((mod) => (
                  <button
                    key={mod.id}
                    className={`modifier-btn ${modsSeleccionados.some((m) => m.id === mod.id) ? "selected" : ""}`}
                    onClick={() => handleToggleModificador(mod, grupo)}
                  >
                    <span className="modifier-name">{mod.nombre}</span>
                    {mod.precioExtra > 0 && (
                      <span className="modifier-price">
                        +{mod.precioExtra.toFixed(2)}€
                      </span>
                    )}
                  </button>
                ))}
              </div>
            </div>
          ))}

          {/* El campo de nota AHORA APARECE SIEMPRE, para cualquier producto */}
          <div className="modifier-group">
            <div className="modifier-group-header">
              <h4>Añadir Nota (Opcional)</h4>
            </div>
            <textarea
              className="config-note-input"
              placeholder="Ej: La carne muy hecha, para llevar, sin hielo..."
              value={notaConfig}
              onChange={(e) => setNotaConfig(e.target.value)}
              rows={2}
            />
          </div>
        </div>

        <div className="config-modal-footer">
          <button
            className="config-modal-submit"
            disabled={!configuracionValida}
            onClick={() =>
              onConfirm(producto, modsSeleccionados, notaConfig.trim())
            }
          >
            Añadir al pedido · {precioActualConfig.toFixed(2)} €
          </button>
        </div>
      </div>
    </div>
  );
}
