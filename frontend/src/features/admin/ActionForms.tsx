import { useState } from "react";
import type { ActionKey } from "./admin";

/* -------------------------------------------------------------
   Listas de opciones — placeholders. Sustitúyelas por las que
   maneje tu app (puede que ya las tengas en otro store/endpoint).
   ------------------------------------------------------------- */
export const ZONES = ["Interior", "Terraza", "Barra"];
export const PRODUCT_TYPES = ["Entrante", "Plato principal", "Postre", "Bebida"];
export const ROLES = ["Administrador", "Camarero", "Cocina"];

/* Placeholder de catálogo para el buscador de "Eliminar producto".
   Sustitúyelo por tu fuente real (fetch/store). */
const MOCK_PRODUCTS = [
  { id: "p1", nombre: "Ensalada César" },
  { id: "p2", nombre: "Solomillo al whisky" },
  { id: "p3", nombre: "Tarta de queso" },
  { id: "p4", nombre: "Limonada" },
];

/* -------------------------------------------------------------
   Tipos de payload que emite cada formulario al confirmar.
   El padre (AdminPage) decide qué hacer con ellos.
   ------------------------------------------------------------- */
export type CreateTablePayload = { numero: string; zona: string };
export type DeleteTablePayload = { numero: string };
export type EditTablePayload = { numero: string; nuevoNumero?: string; nuevaZona?: string };
export type AddProductPayload = {
  nombre: string;
  descripcion: string;
  precio: string;
  tipo: string;
  imagen: File | null;
  /** Nombre con el que se debería guardar el fichero, p. ej. "tarta-de-queso.png" */
  imagenNombreArchivo: string | null;
};
export type RemoveProductPayload = { id: string; nombre: string };
export type RegisterUserPayload = { usuario: string; clave: string; rol: string };

type FormProps<T> = { onConfirm: (payload: T) => void; onCancel: () => void };

/* ================= Añadir mesa ================= */
export function CreateTableForm({ onConfirm, onCancel }: FormProps<CreateTablePayload>) {
  const [numero, setNumero] = useState("");
  const [zona, setZona] = useState(ZONES[0]);

  return (
    <form
      className="action-form"
      onSubmit={(e) => {
        e.preventDefault();
        // TODO: persistir la mesa (API / base de datos)
        onConfirm({ numero, zona });
      }}
    >
      <div className="form-field">
        <label htmlFor="numero-mesa">Número de mesa</label>
        <input
          id="numero-mesa"
          type="number"
          min={1}
          value={numero}
          onChange={(e) => setNumero(e.target.value)}
          required
        />
      </div>

      <div className="form-field">
        <label htmlFor="zona-mesa">Zona</label>
        <select id="zona-mesa" value={zona} onChange={(e) => setZona(e.target.value)}>
          {ZONES.map((z) => (
            <option key={z} value={z}>{z}</option>
          ))}
        </select>
      </div>

      <div className="form-actions">
        <button type="submit" className="btn-primary" disabled={!numero}>Confirmar</button>
        <button type="button" className="btn-ghost" onClick={onCancel}>Cancelar</button>
      </div>
    </form>
  );
}

/* ================= Eliminar mesa ================= */
export function DeleteTableForm({ onConfirm, onCancel }: FormProps<DeleteTablePayload>) {
  const [numero, setNumero] = useState("");

  return (
    <form
      className="action-form"
      onSubmit={(e) => {
        e.preventDefault();
        // TODO: eliminar la mesa (API / base de datos)
        onConfirm({ numero });
      }}
    >
      <div className="form-field">
        <label htmlFor="numero-mesa-eliminar">Número de mesa</label>
        <input
          id="numero-mesa-eliminar"
          type="number"
          min={1}
          value={numero}
          onChange={(e) => setNumero(e.target.value)}
          required
        />
      </div>

      <div className="form-actions">
        <button type="submit" className="btn-danger" disabled={!numero}>Eliminar</button>
        <button type="button" className="btn-ghost" onClick={onCancel}>Cancelar</button>
      </div>
    </form>
  );
}

/* ================= Modificar mesa ================= */
export function EditTableForm({ onConfirm, onCancel }: FormProps<EditTablePayload>) {
  const [numero, setNumero] = useState("");
  const [nuevoNumero, setNuevoNumero] = useState("");
  const [nuevaZona, setNuevaZona] = useState("");

  return (
    <form
      className="action-form"
      onSubmit={(e) => {
        e.preventDefault();
        // TODO: actualizar la mesa (API / base de datos)
        onConfirm({
          numero,
          nuevoNumero: nuevoNumero || undefined,
          nuevaZona: nuevaZona || undefined,
        });
      }}
    >
      <div className="form-field">
        <label htmlFor="numero-mesa-editar">Número de mesa actual</label>
        <input
          id="numero-mesa-editar"
          type="number"
          min={1}
          value={numero}
          onChange={(e) => setNumero(e.target.value)}
          required
        />
      </div>

      <div className="form-field">
        <label htmlFor="nuevo-numero-mesa">Nuevo número (opcional)</label>
        <input
          id="nuevo-numero-mesa"
          type="number"
          min={1}
          value={nuevoNumero}
          onChange={(e) => setNuevoNumero(e.target.value)}
        />
      </div>

      <div className="form-field">
        <label htmlFor="nueva-zona-mesa">Nueva zona (opcional)</label>
        <select id="nueva-zona-mesa" value={nuevaZona} onChange={(e) => setNuevaZona(e.target.value)}>
          <option value="">Sin cambios</option>
          {ZONES.map((z) => (
            <option key={z} value={z}>{z}</option>
          ))}
        </select>
      </div>

      <div className="form-actions">
        <button type="submit" className="btn-primary" disabled={!numero}>Guardar cambios</button>
        <button type="button" className="btn-ghost" onClick={onCancel}>Cancelar</button>
      </div>
    </form>
  );
}

/* ================= Añadir producto ================= */
export function AddProductForm({ onConfirm, onCancel }: FormProps<AddProductPayload>) {
  const [nombre, setNombre] = useState("");
  const [descripcion, setDescripcion] = useState("");
  const [precio, setPrecio] = useState("");
  const [tipo, setTipo] = useState(PRODUCT_TYPES[0]);
  const [imagen, setImagen] = useState<File | null>(null);
  const [preview, setPreview] = useState<string | null>(null);

  const handleFile = (file: File | null) => {
    setImagen(file);
    setPreview(file ? URL.createObjectURL(file) : null);
  };

  const slugify = (text: string) =>
    text
      .toLowerCase()
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .replace(/[^a-z0-9]+/g, "-")
      .replace(/(^-|-$)/g, "");

  return (
    <form
      className="action-form wide-form"
      onSubmit={(e) => {
        e.preventDefault();
        onConfirm({
          nombre,
          descripcion,
          precio,
          tipo,
          imagen,
          imagenNombreArchivo: imagen ? `${slugify(nombre) || "producto"}.png` : null,
        });
      }}
    >
      <div className="form-grid">
        <div className="form-field">
          <label htmlFor="nombre-producto">Nombre</label>
          <input id="nombre-producto" type="text" value={nombre} onChange={(e) => setNombre(e.target.value)} required />
        </div>

        <div className="form-field">
          <label htmlFor="precio-producto">Precio (€)</label>
          <input
            id="precio-producto"
            type="number"
            min={0}
            step="0.01"
            value={precio}
            onChange={(e) => setPrecio(e.target.value)}
            required
          />
        </div>

        <div className="form-field">
          <label htmlFor="tipo-producto">Tipo</label>
          <select id="tipo-producto" value={tipo} onChange={(e) => setTipo(e.target.value)}>
            {PRODUCT_TYPES.map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </select>
        </div>

        <div className="form-field">
          <label htmlFor="imagen-producto">Imagen (PNG)</label>
          <div className="file-field">
            {preview && <img src={preview} alt="" className="file-preview" />}
            <input
              id="imagen-producto"
              type="file"
              accept="image/png"
              onChange={(e) => handleFile(e.target.files?.[0] ?? null)}
            />
          </div>
          <span className="field-note">
            Se guardará como "{slugify(nombre) || "producto"}.png"
          </span>
        </div>

        <div className="form-field full-width">
          <label htmlFor="descripcion-producto">Descripción</label>
          <textarea id="descripcion-producto" value={descripcion} onChange={(e) => setDescripcion(e.target.value)} />
        </div>
      </div>

      <div className="form-actions">
        <button type="submit" className="btn-primary" disabled={!nombre || !precio}>Guardar producto</button>
        <button type="button" className="btn-ghost" onClick={onCancel}>Cancelar</button>
      </div>
    </form>
  );
}

/* ================= Eliminar producto ================= */
export function RemoveProductForm({ onConfirm, onCancel }: FormProps<RemoveProductPayload>) {
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<{ id: string; nombre: string } | null>(null);

  // TODO: sustituir MOCK_PRODUCTS por tu catálogo real (fetch/store)
  const results = MOCK_PRODUCTS.filter((p) =>
    p.nombre.toLowerCase().includes(query.toLowerCase())
  );

  return (
    <form
      className="action-form"
      onSubmit={(e) => {
        e.preventDefault();
        if (!selected) return;
        // TODO: eliminar el producto (API / base de datos)
        onConfirm(selected);
      }}
    >
      <div className="form-field">
        <label htmlFor="buscar-producto">Buscar producto</label>
        <input
          id="buscar-producto"
          type="text"
          value={query}
          onChange={(e) => {
            setQuery(e.target.value);
            setSelected(null);
          }}
          placeholder="Escribe para buscar…"
        />
      </div>

      {query && (
        <div className="search-results">
          {results.length === 0 ? (
            <div className="search-empty">Sin resultados</div>
          ) : (
            results.map((p) => (
              <div
                key={p.id}
                className={`search-result-item${selected?.id === p.id ? " selected" : ""}`}
                onClick={() => setSelected(p)}
              >
                <span>{p.nombre}</span>
              </div>
            ))
          )}
        </div>
      )}

      <div className="form-actions">
        <button type="submit" className="btn-danger" disabled={!selected}>Eliminar</button>
        <button type="button" className="btn-ghost" onClick={onCancel}>Cancelar</button>
      </div>
    </form>
  );
}

/* ================= Registrar usuario ================= */
export function RegisterUserForm({ onConfirm, onCancel }: FormProps<RegisterUserPayload>) {
  const [usuario, setUsuario] = useState("");
  const [clave, setClave] = useState("");
  const [rol, setRol] = useState(ROLES[0]);

  return (
    <form
      className="action-form"
      onSubmit={(e) => {
        e.preventDefault();
        // TODO: registrar el usuario (API / base de datos, hash de la clave, etc.)
        onConfirm({ usuario, clave, rol });
      }}
    >
      <div className="form-field">
        <label htmlFor="usuario-nuevo">Usuario</label>
        <input id="usuario-nuevo" type="text" value={usuario} onChange={(e) => setUsuario(e.target.value)} required />
      </div>

      <div className="form-field">
        <label htmlFor="clave-nuevo">Clave</label>
        <input id="clave-nuevo" type="password" value={clave} onChange={(e) => setClave(e.target.value)} required />
      </div>

      <div className="form-field">
        <label htmlFor="rol-nuevo">Rol</label>
        <select id="rol-nuevo" value={rol} onChange={(e) => setRol(e.target.value)}>
          {ROLES.map((r) => (
            <option key={r} value={r}>{r}</option>
          ))}
        </select>
      </div>

      <div className="form-actions">
        <button type="submit" className="btn-primary" disabled={!usuario || !clave}>Confirmar</button>
        <button type="button" className="btn-ghost" onClick={onCancel}>Cancelar</button>
      </div>
    </form>
  );
}

export const ACTION_META: Record<ActionKey, { title: string; hint: string }> = {
  addTable: { title: "Añadir mesa", hint: "Da de alta una nueva mesa en el local." },
  removeTable: { title: "Eliminar mesa", hint: "Elimina una mesa existente por su número." },
  editTable: { title: "Modificar mesa", hint: "Cambia el número y/o la zona de una mesa." },
  addProduct: { title: "Añadir producto", hint: "Crea un nuevo producto para el catálogo." },
  removeProduct: { title: "Eliminar producto", hint: "Busca un producto y elimínalo del catálogo." },
  addUser: { title: "Registrar usuario", hint: "Da de alta un nuevo usuario del sistema." },
};