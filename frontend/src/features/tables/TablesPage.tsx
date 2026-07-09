import { useEffect, useState } from "react";
import Modal from "react-modal";
import QRCode from "react-qr-code";
import billIcon from "../../assets/bill.png";
import closeIcon from "../../assets/close-window.png";
import qrIcon from "../../assets/qr.png";
import type { Cuenta } from "../menu/types/bill";
import { useWS } from "../ws/useWS";
import ImageButton from "./ImageButton";
import TableButton from "./TableButton";
import type { Mesa } from "./tables";
import {
  cerrarSesionMesa,
  crearMesa,
  crearSesionMesa,
  getCuentaMesa,
  getMesas,
  getZonas,
} from "./tablesService";
import { useTablesData } from "./useTablesData";

Modal.setAppElement("#root");

export default function TablesPage() {
  const token = localStorage.getItem("accessToken");
  const { mesas, zonas, setMesas, setZonas } = useTablesData(token);
  const { mesas: mesasWS } = useWS();

  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [mesaSeleccionada, setMesaSeleccionada] = useState<Mesa | null>(null);
  const [modalCrearMesa, setModalCrearMesa] = useState(false);
  const [numeroMesa, setNumeroMesa] = useState("");
  const [zonaMesa, setZonaMesa] = useState<number | null>(null);
  const estadoMesaSeleccionada = mesaSeleccionada?.estado;

  const [cuenta, setCuenta] = useState<Cuenta | null>(null);
  const [modalCuentaOpen, setModalCuentaOpen] = useState(false);
  const frontendUrl = import.meta.env.VITE_FRONTEND_URL;

  useEffect(() => {
    if (mesasWS && mesasWS.length > 0) {
      setMesas(mesasWS);
    }
  }, [mesasWS]);

  useEffect(() => {
    if (!token) return;

    const cargarDatos = async () => {
      try {
        const [mesasData, zonasData] = await Promise.all([
          getMesas(),
          getZonas(),
        ]);

        setMesas(mesasData);
        setZonas(zonasData);
      } catch (err) {
        console.error(err);
      }
    };

    cargarDatos();
  }, [token]);

  const crearMesaNueva = async () => {
    if (!token) return;

    try {
      if (!numeroMesa) return alert("Debes ingresar un número de mesa");
      if (!zonaMesa) return alert("Debes seleccionar una zona");

      await crearMesa(Number(numeroMesa), zonaMesa);

      setModalCrearMesa(false);
      setNumeroMesa("");
      setZonaMesa(null);

      const mesasData = await getMesas();
      setMesas(mesasData);
    } catch (err) {
      console.error(err);
    }
  };

  const crearSesion = async (mesa: Mesa) => {
    if (!token) return;

    try {
      await crearSesionMesa(mesa.id);
      cerrarModal();
    } catch (err) {
      console.error(err);
    }
  };

  const cerrarSesion = async (mesa: Mesa) => {
    if (!token) return;

    if (!mesa.sesionActiva) {
      throw new Error("La mesa no tiene una sesión activa.");
    }

    try {
      await cerrarSesionMesa(mesa.sesionActiva.qrCodeUrl);

      cerrarModal();
    } catch (err) {
      console.error(err);
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

  const abrirModal = (mesa: Mesa) => {
    setMesaSeleccionada(mesa);
    setModalIsOpen(true);
  };

  const cerrarModal = () => {
    setMesaSeleccionada(null);
    setModalIsOpen(false);
  };

  const obtenerCuenta = async (mesa: Mesa) => {
    if (!token) return;

    try {
      const data = await getCuentaMesa(mesa.id);
      setCuenta(data);
      setModalCuentaOpen(true);
    } catch (err) {
      console.error("Error obteniendo cuenta:", err);
    }
  };

  const mesasPorZona = zonas
    .map((zona) => ({
      zona,
      mesas: mesas.filter((m) => m.zona === zona.nombre),
    }))
    .filter((g) => g.mesas.length > 0);

  return (
    <div className="main-container">
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
        className="modal-container-tables"
        overlayClassName="modal-overlay-tables"
      >
        {mesaSeleccionada && (
          <div className="modal-items-tables">
            <div className="modal-top-tables">
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
                  onClick={() => crearSesion(mesaSeleccionada)}
                />
              ) : mesaSeleccionada?.sesionActiva?.qrCodeUrl ? (
                <div className="qr-actions">
                  <QRCode
                    value={
                      `${frontendUrl}/cliente/?sessionCode=` +
                      mesaSeleccionada?.sesionActiva?.qrCodeUrl
                    }
                  />
                  <div className="button-group">
                    <ImageButton
                      icon={closeIcon}
                      label="Cerrar sesión"
                      onClick={() => cerrarSesion(mesaSeleccionada)}
                    />
                    <ImageButton
                      icon={billIcon}
                      label="Ver cuenta"
                      onClick={() => obtenerCuenta(mesaSeleccionada)}
                    />
                  </div>
                </div>
              ) : (
                <p>No hay sesión activa.</p>
              )}
            </div>
          </div>
        )}
      </Modal>
      <Modal
        isOpen={modalCrearMesa}
        onRequestClose={() => setModalCrearMesa(false)}
        className="modal-container-tables"
        overlayClassName="modal-overlay-tables"
      >
        <div className="modal-items-tables">
          <div className="modal-top-tables">
            <h2>Añadir mesa</h2>

            <button
              className="close-button"
              onClick={() => setModalCrearMesa(false)}
            >
              ×
            </button>
          </div>

          <div className="modal-center-tables">
            <input
              type="number"
              placeholder="Número de mesa"
              value={numeroMesa}
              onChange={(e) => setNumeroMesa(e.target.value)}
            />

            <select
              value={zonaMesa ?? ""}
              onChange={(e) =>
                setZonaMesa(e.target.value ? Number(e.target.value) : null)
              }
            >
              <option value="" disabled>
                Selecciona zona
              </option>

              {zonas.map((zona) => (
                <option key={zona.id} value={zona.id}>
                  {zona.nombre}
                </option>
              ))}
            </select>
          </div>

          <div className="modal-bottom-tables">
            <button className="create-table-button" onClick={crearMesaNueva}>
              Crear mesa
            </button>
          </div>
        </div>
      </Modal>
      <Modal
        isOpen={modalCuentaOpen}
        onRequestClose={() => setModalCuentaOpen(false)}
        className="modal-container-tables"
        overlayClassName="modal-overlay-tables"
      >
        {cuenta && (
          <div className="modal-items-tables">
            <div className="modal-top-tables">
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
      <button
        className="add-table-button"
        onClick={() => setModalCrearMesa(true)}
      >
        +
      </button>
    </div>
  );
}
