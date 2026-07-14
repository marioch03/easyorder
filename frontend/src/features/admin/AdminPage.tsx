import { useState } from "react";
import { useNavigate } from "react-router";

import { logout } from "../management/managementService";
import {
  AddProductForm,
  CreateTableForm,
  DeleteTableForm,
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
import { crearMesa, deshabilitarUsuario, eliminarMesa, registrarUsuario } from "./adminService";
import StatCard from "./StatCard";
import "./styles.css";
import { useAdminData } from "./useAdminData";

function AdminPage() {
  const [activeKey, setActiveKey] = useState<ActionKey | null>(null);
  const [activity, setActivity] = useState<ActivityEntry[]>([]);
  const navigate = useNavigate();
  const { zonas, mesas, setMesas, productos, tiposProducto, usuarioRoles, usuarios, setUsuarios } =
    useAdminData();
  const usuariosActivos = usuarios.filter(u => u.activo).length;
  const handleSelect = (action: (typeof ACTIONS)[number]) => {
    setActiveKey(action.key === activeKey ? null : action.key);
  };

  const handleLogout = async () => {
    try {
      await logout();
    } catch (error) {
      console.error(error);
    }

    navigate("/auth/login");
  };

  const logActivity = (label: string, danger = false) => {
    setActivity((prev) =>
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
      ].slice(0, 4),
    );
  };

  const closeForm = () => setActiveKey(null);

  const renderActionBody = () => {
    if (!activeKey) {
      return (
        <div className="action-empty">
          Selecciona una acción en la barra lateral para empezar.
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
          <h1>PdTú</h1>
          <p>Panel de administración</p>
        </div>

        <div className="admin-section-label">Mesas</div>
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
        <div className="logout-section">
          {logoutActions.map((a) => (
            <AdminButton
              key={a.key}
              icon={a.icon}
              label={a.label}
              variant={a.variant}
              active={activeKey === a.key}
              onClick={() => {
                if (confirm("¿Seguro que quiere cerrar sesión?")) {
                  handleLogout();
                }
              }}
            />
          ))}
        </div>
      </aside>

      <main className="admin-main">
        <div className="admin-header">
          <div>
            <h2>Panel de administración</h2>
            <p className="subtitle">
              Gestiona mesas, catálogo y personal desde un mismo lugar.
            </p>
          </div>
          <span className="date-pill">
            {new Date().toLocaleDateString("es-ES", {
              weekday: "long",
              day: "numeric",
              month: "long",
            })}
          </span>
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

        <section className="action-container">
          {activeKey && (
            <>
              <h3>{ACTION_META[activeKey].title}</h3>
              <p className="action-hint">{ACTION_META[activeKey].hint}</p>
            </>
          )}
          {renderActionBody()}
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
                  className={`activity-item${entry.danger ? " danger" : ""}`}
                >
                  <span className="dot" />
                  <span>{entry.label}</span>
                  <time>{entry.time}</time>
                </li>
              ))}
            </ul>
          )}
        </section>
      </main>
    </div>
  );
}

export default AdminPage;
