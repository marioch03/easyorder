import { useState } from "react";
import type {
  AddProductFormProps,
  CreateTableFormProps,
  DeleteTablePayload,
  EditTableFormProps,
  FormProps,
  RegisterUserFormProps,
  RemoveProductFormProps,
  RemoveProductPayload
} from "./admin";

/* -------------------------------------------------------------
   Listas de opciones — placeholders. Sustitúyelas por las que
   maneje tu app (puede que ya las tengas en otro store/endpoint).
   ------------------------------------------------------------- *

/* ================= Añadir mesa ================= */
export function CreateTableForm({
  onConfirm,
  onCancel,
  zonas
}: CreateTableFormProps) {
  const [numero, setNumero] = useState("");
  const [zona, setZona] = useState<number | null>(null);

  return (
    <form
      className="action-form"
      onSubmit={(e) => {
        e.preventDefault();
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
        <select
          value={zona ?? ""}
          onChange={(e) =>
            setZona(e.target.value ? Number(e.target.value) : null)
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

      <div className="form-actions">
        <button type="submit" className="btn-primary" disabled={!numero}>
          Confirmar
        </button>
        <button type="button" className="btn-ghost" onClick={onCancel}>
          Cancelar
        </button>
      </div>
    </form>
  );
}

/* ================= Eliminar mesa ================= */
export function DeleteTableForm({
  onConfirm,
  onCancel,
}: FormProps<DeleteTablePayload>) {
  const [numero, setNumero] = useState("");

  return (
    <form
      className="action-form"
      onSubmit={(e) => {
        e.preventDefault();
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
        <button type="submit" className="btn-danger" disabled={!numero}>
          Eliminar
        </button>
        <button type="button" className="btn-ghost" onClick={onCancel}>
          Cancelar
        </button>
      </div>
    </form>
  );
}

/* ================= Modificar mesa ================= */
export function EditTableForm({
  onConfirm,
  onCancel,
  zonas
}: EditTableFormProps) {
  const [numero, setNumero] = useState("");
  const [nuevoNumero, setNuevoNumero] = useState("");
  const [nuevaZona, setNuevaZona] = useState<number | null>(null);

  return (
    <form
      className="action-form"
      onSubmit={(e) => {
        e.preventDefault();
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
        <label htmlFor="nuevo-numero-mesa">Nuevo número</label>
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
        <select
          value={nuevaZona ?? ""}
          onChange={(e) =>
            setNuevaZona(e.target.value ? Number(e.target.value) : null)
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

      <div className="form-actions">
        <button type="submit" className="btn-primary" disabled={!numero}>
          Guardar cambios
        </button>
        <button type="button" className="btn-ghost" onClick={onCancel}>
          Cancelar
        </button>
      </div>
    </form>
  );
}

/* ================= Añadir producto ================= */
export function AddProductForm({
  onConfirm,
  onCancel,
  tiposProducto
}: AddProductFormProps) {
  const [nombre, setNombre] = useState("");
  const [descripcion, setDescripcion] = useState("");
  const [precio, setPrecio] = useState("");
  const [tipo, setTipo] = useState<number | null>(null);
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
          imagen: imagen ? `${slugify(nombre) || imagen.name}.png` : null,
        });
      }}
    >
      <div className="form-grid">
        <div className="form-field">
          <label htmlFor="nombre-producto">Nombre</label>
          <input
            id="nombre-producto"
            type="text"
            value={nombre}
            onChange={(e) => setNombre(e.target.value)}
            required
          />
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
          <select
            id="tipo-producto"
            value={tipo ?? ""}
            onChange={(e) =>
              setTipo(e.target.value ? Number(e.target.value) : null)
            }
          >
            <option value="" disabled>
              Selecciona tipo
            </option>
            {tiposProducto.map((tipo) => (
              <option key={tipo.id} value={tipo.id}>
                {tipo.nombre}
              </option>
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
            Se guardará como "{slugify(nombre) || imagen?.name || "producto"}
            .png"
          </span>
        </div>

        <div className="form-field full-width">
          <label htmlFor="descripcion-producto">Descripción</label>
          <textarea
            id="descripcion-producto"
            value={descripcion}
            onChange={(e) => setDescripcion(e.target.value)}
          />
        </div>
      </div>

      <div className="form-actions">
        <button
          type="submit"
          className="btn-primary"
          disabled={!nombre || !precio}
        >
          Guardar producto
        </button>
        <button type="button" className="btn-ghost" onClick={onCancel}>
          Cancelar
        </button>
      </div>
    </form>
  );
}

/* ================= Eliminar producto ================= */
export function RemoveProductForm({
  onConfirm,
  onCancel,
  productos
}: RemoveProductFormProps) {
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<RemoveProductPayload | null>(null);

  const results = productos.filter((p) =>
    p.nombre.toLowerCase().includes(query.toLowerCase()),
  );

  return (
    <form
      className="action-form"
      onSubmit={(e) => {
        e.preventDefault();
        if (!selected) return;
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
                onClick={() => setSelected({ id: p.id, nombre: p.nombre })}
              >
                <span>{p.nombre}</span>
              </div>
            ))
          )}
        </div>
      )}

      <div className="form-actions">
        <button type="submit" className="btn-danger" disabled={!selected}>
          Eliminar
        </button>
        <button type="button" className="btn-ghost" onClick={onCancel}>
          Cancelar
        </button>
      </div>
    </form>
  );
}

/* ================= Registrar usuario ================= */
export function RegisterUserForm({
  onConfirm,
  onCancel,
  usuarioRoles
}: RegisterUserFormProps) {
  const [usuario, setUsuario] = useState("");
  const [clave, setClave] = useState("");
  const [rol, setRol] = useState<number | null>(null);

  return (
    <form
      className="action-form"
      onSubmit={(e) => {
        e.preventDefault();
        onConfirm({ usuario, clave, rol });
      }}
    >
      <div className="form-field">
        <label htmlFor="usuario-nuevo">Usuario</label>
        <input
          id="usuario-nuevo"
          type="text"
          value={usuario}
          onChange={(e) => setUsuario(e.target.value)}
          required
        />
      </div>

      <div className="form-field">
        <label htmlFor="clave-nuevo">Clave</label>
        <input
          id="clave-nuevo"
          type="password"
          value={clave}
          onChange={(e) => setClave(e.target.value)}
          required
        />
      </div>

      <div className="form-field">
        <label htmlFor="rol-nuevo">Rol</label>
        <select
          id="rol-nuevo"
          value={rol ?? ""}
          onChange={(e) =>
            setRol(e.target.value ? Number(e.target.value) : null)
          }
        >
          <option value="" disabled>
            Seleccionar rol
          </option>

          {usuarioRoles.map((rol) => (
            <option key={rol.id} value={rol.id}>
              {rol.nombre}
            </option>
          ))}
        </select>
      </div>

      <div className="form-actions">
        <button
          type="submit"
          className="btn-primary"
          disabled={!usuario || !clave}
        >
          Confirmar
        </button>
        <button type="button" className="btn-ghost" onClick={onCancel}>
          Cancelar
        </button>
      </div>
    </form>
  );
}
