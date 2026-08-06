import { useState } from "react";
import Modal from "react-modal";
import QRCode from "react-qr-code";
import { useParams } from "react-router-dom";
import billIcon from "../../assets/bill.png";
import closeIcon from "../../assets/close-window.png";
import qrIcon from "../../assets/qr.png";
import type { Cuenta } from "../menu/types/bill";
import ImageButton from "./ImageButton";
import TableButton from "./TableButton";
import type { MesaDTO } from "./tables";
import {
  cerrarSesionMesa,
  crearSesionMesa,
  getCuentaMesa,
} from "./tablesService";
import { useTablesData } from "./useTablesData";

Modal.setAppElement("#root");

const LEGEND = [
  { key: "green", label: "Libre" },
  { key: "red", label: "Ocupada" },
  { key: "yellow", label: "Esperando cuenta" },
];

export default function TablesPage() {
  const { mesas, zonas, loading, error } = useTablesData();

  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [mesaSeleccionada, setMesaSeleccionada] = useState<MesaDTO | null>(
    null,
  );
  const estadoMesaSeleccionada = mesaSeleccionada?.estado;

  const [qrLoading, setQrLoading] = useState(false);
  const [cuentaLoading, setCuentaLoading] = useState(false);
  const [actionError, setActionError] = useState<string | null>(null);

  const [cuenta, setCuenta] = useState<Cuenta | null>(null);
  const [modalCuentaOpen, setModalCuentaOpen] = useState(false);

  const { slug } = useParams<{ slug: string }>();
  const frontendUrl = import.meta.env.VITE_FRONTEND_URL;
  const currentSlug = slug || localStorage.getItem("tenant_slug") || "";
  const qrUrl = `${frontendUrl}/${currentSlug}/cliente/?sessionCode=`;

  const crearSesion = async (mesa: MesaDTO) => {
    setQrLoading(true);
    setActionError(null);

    try {
      await crearSesionMesa(mesa.id);
      cerrarModal();
    } catch (err) {
      console.error(err);
      setActionError("No se pudo generar el QR. Inténtalo de nuevo.");
    } finally {
      setQrLoading(false);
    }
  };

  const cerrarSesion = async (mesa: MesaDTO) => {
    if (qrLoading) return;

    if (!mesa.sesionActiva) {
      console.error("La mesa no tiene una sesión activa.");
      setActionError("Esta mesa no tiene ninguna sesión activa que cerrar.");
      return;
    }

    setQrLoading(true);
    setActionError(null);

    try {
      await cerrarSesionMesa(mesa.sesionActiva.qrCodeUrl);
      cerrarModal();
    } catch (err) {
      console.error(err);
      setActionError("No se pudo cerrar la sesión. Inténtalo de nuevo.");
    } finally {
      setQrLoading(false);
    }
  };

  const getButtonState = (estadoNombre: string) => {
    switch (estadoNombre) {
      case "LIBRE":
        return "green";
      case "OCUPADA":
        return "red";
      case "ESPERANDO_CUENTA":
        return "yellow";
      default:
        return "gray";
    }
  };

  const abrirModal = (mesa: MesaDTO) => {
    setMesaSeleccionada(mesa);
    setActionError(null);
    setModalIsOpen(true);
  };

  const cerrarModal = () => {
    setMesaSeleccionada(null);
    setModalIsOpen(false);
  };

  const obtenerCuenta = async (mesa: MesaDTO) => {
    setCuentaLoading(true);
    setActionError(null);

    try {
      const data = await getCuentaMesa(mesa.id);
      setCuenta(data);
      setModalCuentaOpen(true);
    } catch (err) {
      console.error("Error obteniendo cuenta:", err);
      setActionError("No se pudo obtener la cuenta. Inténtalo de nuevo.");
    } finally {
      setCuentaLoading(false);
    }
  };

  const mesasPorZona = zonas
    .map((zona) => ({
      zona,
      mesas: mesas.filter((m) => m.zona === zona.nombre),
    }))
    .filter((g) => g.mesas.length > 0);

  if (loading) {
    return <div className="loading">Cargando mesas…</div>;
  }

  if (error) {
    return <div className="loading error">{error}</div>;
  }

  return (
    <div className="main-container">
      <div className="table-legend">
        {LEGEND.map((item) => (
          <span key={item.key} className="table-legend-item">
            <span className={`table-legend-dot ${item.key}`} />
            {item.label}
          </span>
        ))}
      </div>

      <div className="zones-container">
        {mesasPorZona.map(({ zona, mesas }) => (
          <div key={zona.id} className="zone-group">
            <h2 className="zone-title">{zona.nombre}</h2>

            <div className="tables-container">
              {mesas.map((mesa) => (
                <TableButton
                  key={mesa.id}
                  number={mesa.numero}
                  state={getButtonState(mesa.estado)}
                  onClick={() => abrirModal(mesa)}
                />
              ))}
            </div>
          </div>
        ))}
      </div>
      <Modal
        isOpen={modalIsOpen}
        onRequestClose={cerrarModal}
        contentLabel="Detalle Mesa"
        className="modal-staff-container"
        overlayClassName="modal-staff-overlay"
      >
        {mesaSeleccionada && (
          <div className="modal-items-tables">
            <div className="modal-staff-top">
              <h2>Mesa {mesaSeleccionada?.numero}</h2>
              <button className="close-button" onClick={cerrarModal}>
                ×
              </button>
            </div>
            <div className="modal-center-tables">
              <p>
                Estado:
                <span
                  className={`estado-tag estado-${mesaSeleccionada.estado}`}
                >
                  {mesaSeleccionada.estado}
                </span>
              </p>
              <p>Zona: {mesaSeleccionada.zona ?? "Sin zona"}</p>
            </div>
            <div className="modal-bottom-tables">
              {estadoMesaSeleccionada === "LIBRE" ? (
                <ImageButton
                  icon={qrIcon}
                  label="Generar QR"
                  loading={qrLoading}
                  onClick={() => crearSesion(mesaSeleccionada)}
                />
              ) : mesaSeleccionada?.sesionActiva?.qrCodeUrl ? (
                <div className="qr-actions">
                  <div className="qr-code-frame">
                    <QRCode
                      value={`${qrUrl}${mesaSeleccionada?.sesionActiva?.qrCodeUrl}`}
                    />
                  </div>
                  <div className="button-group">
                    <ImageButton
                      icon={closeIcon}
                      label="Cerrar sesión"
                      variant="danger"
                      loading={qrLoading}
                      onClick={() => cerrarSesion(mesaSeleccionada)}
                    />
                    <ImageButton
                      icon={billIcon}
                      label="Ver cuenta"
                      loading={cuentaLoading}
                      onClick={() => obtenerCuenta(mesaSeleccionada)}
                    />
                  </div>
                </div>
              ) : (
                <p>No hay sesión activa.</p>
              )}
              {actionError && <p className="action-error">{actionError}</p>}
            </div>
          </div>
        )}
      </Modal>
      <Modal
        isOpen={modalCuentaOpen}
        onRequestClose={() => setModalCuentaOpen(false)}
        className="modal-staff-container"
        overlayClassName="modal-staff-overlay"
      >
        {cuenta && (
          <div className="modal-items-tables">
            <div className="modal-staff-top">
              <h2>Cuenta de la mesa</h2>
              <button
                className="close-button"
                onClick={() => setModalCuentaOpen(false)}
              >
                ×
              </button>
            </div>

            <div className="pedido-items">
              {cuenta.items.map((item) => (
                <div className="pedido-item" key={item.idProducto}>
                  <span>
                    {item.cantidad} x {item.nombreProducto}
                  </span>

                  <span>
                    {(item.cantidad * item.precioUnitario).toFixed(2)} €
                  </span>
                </div>
              ))}
            </div>

            <div className="modal-bottom-orders">
              <h3>Total: {cuenta.total.toFixed(2)} €</h3>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}
