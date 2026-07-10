import { useState } from "react";
import { useSession } from "../../session/useSession";
import { useBillData } from "../hooks/useBillData";
import { solicitarCuentaApi } from "../services/billService";
import "../styles.css";

export default function BillPage() {
  const [confirmOpen, setConfirmOpen] = useState(false);
  const { sessionCode, actualizarEstadoMesa } = useSession();
  const [botonLoading, setBotonLoading] = useState(false);

  const { cuenta, loading, idMesa, estadoMesa } = useBillData(sessionCode);

  const cuentaSolicitada = estadoMesa === "ESPERANDO_CUENTA";

  const solicitarCuenta = async () => {
    if (!idMesa || !sessionCode) return;
    setBotonLoading(true);
    try {
      await solicitarCuentaApi(idMesa);
      setConfirmOpen(false);
      actualizarEstadoMesa("ESPERANDO_CUENTA");
    } catch (err) {
      console.error(err);

      alert("No se pudo solicitar la cuenta");
    } finally {
      setBotonLoading(false);
    }
  };
  if (loading) return <p>Cargando cuenta...</p>;

  return (
    <div className="bill-page">
      <h1>Cuenta</h1>
      <div className="bill-divider"></div>

      <div className="bill-list">
        {cuenta?.items.map((item, index) => (
          <div key={index} className="bill-item">
            <div className="bill-left">
              <span className="bill-qty">{item.cantidad}x</span>
              <span>{item.nombreProducto}</span>
            </div>

            <span className="bill-price">
              {(item.cantidad * item.precioUnitario).toFixed(2)} €
            </span>
          </div>
        ))}
      </div>

      <div className="bill-footer">
        <div className="bill-divider"></div>

        <div className="bill-total">
          <span>Total:</span>
          <span>{Number(cuenta?.total ?? 0).toFixed(2)} €</span>
        </div>

        <button
          className="request-bill-btn"
          disabled={cuentaSolicitada}
          onClick={() => !cuentaSolicitada && setConfirmOpen(true)}
        >
          {cuentaSolicitada ? "Cuenta solicitada" : "Solicitar cuenta"}
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
