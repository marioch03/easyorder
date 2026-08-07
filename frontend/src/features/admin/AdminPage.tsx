import { useState } from "react";
import { useNavigate, useParams } from "react-router";

import ConfirmModal from "../../common/ConfirmModal";
import { logout } from "../auth/authService";
import {
  AddProductForm,
  CreateTableForm,
  DeleteTableForm,
  EditProductForm,
  EditTableForm,
  RegisterUserForm,
  RemoveProductForm,
  RemoveUserForm,
} from "./ActionForms";
import {
  ACTION_META,
  type ActionKey,
  ACTIONS,
  type ActivityEntry,
  catalogActions,
  logoutActions,
  staffActions,
  tableActions,
} from "./admin";
import AdminButton from "./AdminButton";
import {
  crearMesa,
  deshabilitarUsuario,
  editarProducto,
  eliminarMesa,
  registrarUsuario,
} from "./adminService";
import StatCard from "./StatCard";
import "./styles.css";
import { useAdminData } from "./useAdminData";

function AdminPage() {
  const [activeKey, setActiveKey] = useState<ActionKey | null>(null);
  const [activity, setActivity] = useState<ActivityEntry[]>([]);
  const [confirmLogoutOpen, setConfirmLogoutOpen] = useState(false);
  const [logoutLoading, setLogoutLoading] = useState(false);
  const { slug } = useParams<{ slug: string }>();

  const currentSlug = slug || localStorage.getItem("tenant_slug") || "";
  const navigate = useNavigate();
  const {
    zonas,
    mesas,
    setMesas,
    productos,
    tiposProducto,
    usuarioRoles,
    usuarios,
    setUsuarios,
  } = useAdminData();
  const usuariosActivos = usuarios.filter((u) => u.activo).length;

  const handleSelect = (action: (typeof ACTIONS)[number]) => {
    setActiveKey(action.key === activeKey ? null : action.key);
  };

  const handleLogout = async () => {
    try {
      setLogoutLoading(true);
      await logout();
      if (currentSlug) {
        navigate(`/${currentSlug}/auth/login`, { replace: true });
      } else {
        navigate("/error", { replace: true });
      }
    } catch (error) {
      console.error(error);
    } finally {
      setLogoutLoading(false);
    }
  };

  const logActivity = (label: string, danger = false) => {
    setActivity(
      (prev) =>
        [
          {
            id: Date.now(),
            label,
            danger,
            time: new Date().toLocaleTimeString("es-ES", {
              hour: "2-digit",
              minute: "2-digit",
            }),
          },
          ...prev,
        ].slice(0, 5), // Guardamos las 5 más recientes
    );
  };

  const closeForm = () => setActiveKey(null);

  const renderActionBody = () => {
    if (!activeKey) {
      return (
        <div className="action-empty">
          <div className="empty-icon">👈</div>
          <p>Selecciona una acción en la barra lateral para empezar.</p>
        </div>
      );
    }

    switch (activeKey) {
      case "addTable":
        return (
          <CreateTableForm
            onCancel={closeForm}
            onConfirm={async (payload) => {
              try {
                const nuevaMesa = await crearMesa(
                  Number(payload.numero),
                  payload.zona,
                );
                setMesas((prev) => [...prev, nuevaMesa]);
                logActivity(`Mesa ${payload.numero} añadida (${payload.zona})`);
                closeForm();
              } catch (error) {
                console.error(error);
              }
            }}
            zonas={zonas}
          />
        );
      case "removeTable":
        return (
          <DeleteTableForm
            onCancel={closeForm}
            onConfirm={async (payload) => {
              try {
                await eliminarMesa(Number(payload.numero));
                setMesas((prev) =>
                  prev.filter((m) => m.numero !== Number(payload.numero)),
                );
                logActivity(`Mesa ${payload.numero} eliminada`, true);
                closeForm();
              } catch (error) {
                console.error(error);
              }
            }}
          />
        );
      case "editTable":
        return (
          <EditTableForm
            onCancel={closeForm}
            onConfirm={(payload) => {
              logActivity(`Mesa ${payload.numero} modificada`);
              closeForm();
            }}
            zonas={zonas}
          />
        );
      case "addProduct":
        return (
          <AddProductForm
            onCancel={closeForm}
            onConfirm={(payload) => {
              logActivity(`Producto "${payload.nombre}" añadido`);
              closeForm();
            }}
            tiposProducto={tiposProducto}
          />
        );
      case "editProduct":
        return (
          <EditProductForm
            productos={productos}
            tiposProducto={tiposProducto}
            onCancel={closeForm}
            onConfirm={async (payload) => {
              try {
                const productoActualizado = await editarProducto(payload);
                logActivity(
                  `Producto "${productoActualizado.nombre}" modificado`,
                );
                closeForm();
              } catch (error) {
                console.error(error);
              }
            }}
          />
        );
      case "removeProduct":
        return (
          <RemoveProductForm
            onCancel={closeForm}
            onConfirm={(payload) => {
              logActivity(`Producto "${payload.nombre}" eliminado`, true);
              closeForm();
            }}
            productos={productos}
          />
        );
      case "addUser":
        return (
          <RegisterUserForm
            onCancel={closeForm}
            onConfirm={async (payload) => {
              try {
                await registrarUsuario(
                  payload.nombre,
                  payload.rol,
                  payload.clave,
                  currentSlug,
                );
                logActivity(
                  `Usuario ${payload.nombre} registrado, Rol: "${payload.rol}"`,
                  true,
                );
                closeForm();
              } catch (error) {
                console.error(error);
              }
            }}
            usuarioRoles={usuarioRoles}
          />
        );
      case "removeUser":
        return (
          <RemoveUserForm
            usuarios={usuarios}
            onCancel={closeForm}
            onConfirm={async ({ id }) => {
              await deshabilitarUsuario(id);
              setUsuarios((prev) =>
                prev.map((u) => (u.id === id ? { ...u, activo: false } : u)),
              );
              logActivity(`Usuario inhabilitado correctamente`, true);
              closeForm();
            }}
          />
        );
      default:
        return null;
    }
  };

  return (
    <div className="admin-shell">
      <aside className="admin-sidebar">
        <div className="admin-brand">
          <div className="mark">PT</div>
          <div className="brand-text">
            <h1>PdTú</h1>
            <p>Admin Workspace</p>
          </div>
        </div>

        <div className="admin-nav">
          <div className="admin-section-label">Gestión de Mesas</div>
          <div className="admin-section">
            {tableActions.map((a) => (
              <AdminButton
                key={a.key}
                icon={a.icon}
                label={a.label}
                variant={a.variant}
                active={activeKey === a.key}
                onClick={() => handleSelect(a)}
              />
            ))}
          </div>

          <div className="admin-section-label">Catálogo</div>
          <div className="admin-section">
            {catalogActions.map((a) => (
              <AdminButton
                key={a.key}
                icon={a.icon}
                label={a.label}
                variant={a.variant}
                active={activeKey === a.key}
                onClick={() => handleSelect(a)}
              />
            ))}
          </div>

          <div className="admin-section-label">Personal</div>
          <div className="admin-section">
            {staffActions.map((a) => (
              <AdminButton
                key={a.key}
                icon={a.icon}
                label={a.label}
                variant={a.variant}
                active={activeKey === a.key}
                onClick={() => handleSelect(a)}
              />
            ))}
          </div>
        </div>

        <div className="logout-section">
          {logoutActions.map((a) => (
            <AdminButton
              key={a.key}
              icon={a.icon}
              label={a.label}
              variant={a.variant}
              active={activeKey === a.key}
              onClick={() => setConfirmLogoutOpen(true)}
            />
          ))}
        </div>

        <ConfirmModal
          open={confirmLogoutOpen}
          title="¿Cerrar sesión?"
          message="Se cerrará la sesión en este dispositivo."
          loading={logoutLoading}
          variant="danger"
          onCancel={() => setConfirmLogoutOpen(false)}
          onConfirm={handleLogout}
        />
      </aside>

      <main className="admin-main">
        <div className="admin-header">
          <div className="header-titles">
            <h2>Panel de Administración</h2>
            <p className="subtitle">
              Gestiona mesas, catálogo y personal desde un mismo lugar.
            </p>
          </div>
          <div className="header-widgets">
            <span className="date-pill">
              {new Date().toLocaleDateString("es-ES", {
                weekday: "long",
                day: "numeric",
                month: "long",
              })}
            </span>
          </div>
        </div>

        <div className="stat-grid">
          <StatCard label="Mesas activas" value={mesas.length.toString()} />
          <StatCard
            label="Productos en catálogo"
            value={productos.length.toString()}
          />
          <StatCard
            label="Usuarios activos"
            value={usuariosActivos.toString()}
          />
        </div>

        <div className="admin-content-grid">
          <section className="action-container">
            {activeKey && (
              <div className="action-header">
                <h3>{ACTION_META[activeKey].title}</h3>
                <p className="action-hint">{ACTION_META[activeKey].hint}</p>
              </div>
            )}
            <div className="action-body">{renderActionBody()}</div>
          </section>

          <section className="activity-panel">
            <h3>Actividad reciente</h3>
            {activity.length === 0 ? (
              <div className="activity-empty">
                Sin acciones registradas todavía.
              </div>
            ) : (
              <ul className="activity-list">
                {activity.map((entry) => (
                  <li
                    key={entry.id}
                    className={`activity-item ${entry.danger ? "danger" : ""}`}
                  >
                    <span className="activity-dot" />
                    <div className="activity-content">
                      <span className="activity-label">{entry.label}</span>
                      <time className="activity-time">{entry.time}</time>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </section>
        </div>
      </main>
    </div>
  );
}

export default AdminPage;
