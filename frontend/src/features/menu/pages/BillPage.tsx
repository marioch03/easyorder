import { useState } from "react";
import { useSessionStore } from "../../session/sessionStore";
import BillRequestedScreen from "../components/BillRequestedScreen";
import { useBillData } from "../hooks/useBillData";
import { solicitarCuentaApi } from "../services/billService";
import "../styles.css";

export default function BillPage() {
  const [confirmOpen, setConfirmOpen] = useState(false);
  const sessionCode = useSessionStore((state) => state.sessionCode);
  const actualizarEstadoMesa = useSessionStore((state) => state.actualizarEstadoMesa);
  const [botonLoading, setBotonLoading] = useState(false);

  const { cuenta, loading, estadoMesa } = useBillData(sessionCode);

  const cuentaSolicitada = estadoMesa === "ESPERANDO_CUENTA";

  const solicitarCuenta = async () => {
    if (!sessionCode) return;
    setBotonLoading(true);
    try {
      await solicitarCuentaApi();
      setConfirmOpen(false);
      actualizarEstadoMesa("ESPERANDO_CUENTA");
    } catch (err) {
      console.error(err);
      alert("No se pudo solicitar la cuenta");
    } finally {
      setBotonLoading(false);
    }
  };

  if (loading) return <p className="bill-loading">Cargando cuenta…</p>;

  if (cuentaSolicitada) {
    return (
      <div className="bill-page">
        <BillRequestedScreen total={Number(cuenta?.total ?? 0)} />
      </div>
    );
  }

  return (
    <div className="bill-page">
      <h1>Cuenta</h1>
      <div className="bill-divider"></div>

      <div className="bill-list">
        {cuenta?.items.map((item, index) => {
          const modsTotal =
            item.modificadores?.reduce(
              (sum, mod) => sum + mod.precioAplicado,
              0,
            ) ?? 0;
          const lineTotal = item.cantidad * (item.precioUnitario + modsTotal);

          return (
            <div key={index} className="bill-item">
              <div className="bill-item-main">
                <div className="bill-left">
                  <span className="bill-qty">{item.cantidad}x</span>
                  <span className="bill-product-name">
                    {item.nombreProducto}
                  </span>
                </div>

                <span className="bill-price">{lineTotal.toFixed(2)} €</span>
              </div>

              {item.modificadores && item.modificadores.length > 0 && (
                <ul className="bill-item-modifiers">
                  {item.modificadores.map((mod) => (
                    <li key={mod.id}>
                      <span>+ {mod.nombre}</span>
                      {mod.precioAplicado > 0 && (
                        <span className="bill-modifier-price">
                          +{mod.precioAplicado.toFixed(2)} €
                        </span>
                      )}
                    </li>
                  ))}
                </ul>
              )}

              {item.nota && item.nota.trim() !== "" && (
                <span className="bill-item-note">Nota: {item.nota}</span>
              )}
            </div>
          );
        })}
      </div>

      <div className="bill-footer">
        <div className="bill-divider"></div>

        <div className="bill-total">
          <span>Total:</span>
          <span>{Number(cuenta?.total ?? 0).toFixed(2)} €</span>
        </div>

        <button
          className="request-bill-btn"
          onClick={() => setConfirmOpen(true)}
        >
          Solicitar cuenta
        </button>
      </div>

      {confirmOpen && (
        <div className="confirm-overlay">
          <div className="confirm-modal">
            <h3>¿Solicitar la cuenta?</h3>
            <p>El camarero recibirá la notificación.</p>

            <div className="confirm-actions">
              <button
                className="cancel-btn"
                onClick={() => setConfirmOpen(false)}
              >
                Cancelar
              </button>

              <button
                className="confirm-btn"
                onClick={solicitarCuenta}
                disabled={botonLoading}
              >
                {botonLoading ? <span className="loader" /> : "Confirmar"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
