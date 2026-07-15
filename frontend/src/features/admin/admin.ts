import type { SesionDTO } from "../tables/tables";
import { icons } from "./icons";

export type ActionKey =
  | "addTable"
  | "removeTable"
  | "editTable"
  | "addUser"
  | "removeUser"
  | "logout"
  | "addProduct"
  | "editProduct"
  | "removeProduct";

export type ActivityEntry = {
  id: number;
  label: string;
  danger: boolean;
  time: string;
};

export type Action = {
  key: ActionKey;
  label: string;
  icon: string;
  group: "Mesas" | "Catálogo" | "Personal" | "Logout";
  variant?: "default" | "danger";
};

export const ACTIONS: Action[] = [
  {
    key: "addTable",
    label: "Añadir mesa",
    icon: icons.addTable,
    group: "Mesas",
  },
  {
    key: "editTable",
    label: "Modificar mesa",
    icon: icons.editTable,
    group: "Mesas",
  },
  {
    key: "removeTable",
    label: "Eliminar mesa",
    icon: icons.removeTable,
    group: "Mesas",
    variant: "danger",
  },
  {
    key: "addProduct",
    label: "Añadir producto",
    icon: icons.addProduct,
    group: "Catálogo",
  },
  {
    key: "editProduct",
    label: "Editar producto",
    icon: icons.editProduct,
    group: "Catálogo",
  },
  {
    key: "removeProduct",
    label: "Eliminar producto",
    icon: icons.removeProduct,
    group: "Catálogo",
    variant: "danger",
  },
  {
    key: "addUser",
    label: "Registrar usuario",
    icon: icons.addUser,
    group: "Personal",
  },
  {
    key: "removeUser",
    label: "Eliminar usuario",
    icon: icons.removeUser,
    group: "Personal",
    variant: "danger",
  },
  {
    key: "logout",
    label: "Cerrar sesión",
    icon: icons.logout,
    group: "Logout",
  },
];

export const tableActions = ACTIONS.filter((a) => a.group === "Mesas");
export const catalogActions = ACTIONS.filter((a) => a.group === "Catálogo");
export const staffActions = ACTIONS.filter((a) => a.group === "Personal");
export const logoutActions = ACTIONS.filter((a) => a.group === "Logout");

//Form payload types
export type CreateTableFormProps = FormProps<CreateTablePayload> & {
  zonas: ZonaDTO[];
};
export type CreateTablePayload = { numero: string; zona: number };
export type DeleteTablePayload = { numero: string };
export type EditTableFormProps = FormProps<EditTablePayload> & {
  zonas: ZonaDTO[];
};
export type EditTablePayload = {
  numero: string;
  nuevoNumero?: string;
  nuevaZona?: number | null;
};
export type AddProductFormProps = FormProps<AddProductPayload> & {
  tiposProducto: ProductoTipoDTO[];
};

export type AddProductPayload = {
  nombre: string;
  descripcion: string;
  precio: number;
  tipo: number | null;
  imagen: string | null;
};

export type EditProductPayload = {
  id: number,
  nombre: string;
  descripcion: string;
  precio: number;
  tipoId: number | null;
  activo: boolean
};

export type EditProductFormProps = FormProps<EditProductPayload> & {
  productos: ProductoDTO[];
  tiposProducto: ProductoTipoDTO[];
};
export type RemoveProductFormProps = FormProps<RemoveProductPayload> & {
  productos: ProductoDTO[];
};
export type RemoveProductPayload = { id: number; nombre: string };
export type RegisterUserFormProps = FormProps<RegisterUserPayload> & {
  usuarioRoles: UsuarioRolDTO[];
};
export type RegisterUserPayload = {
  nombre: string;
  rol: string;
  clave: string;
};
export type RemoveUserPayload = { id: number };
export type RemoveUserFormProps = FormProps<RemoveUserPayload> & {
  usuarios: UsuarioDTO[];
};
export type FormProps<T> = {
  onConfirm: (payload: T) => void;
  onCancel: () => void;
};
//Tipos de datos para formularios
export type ZonaDTO = {
  id: number;
  nombre: string;
};
export type MesaDTO = {
  id: number;
  numero: number;
  estado: string;
  zona: string | null;
  sesionActiva: SesionDTO | null;
};

export type ProductoDTO = {
  id: number;
  nombre: string;
  descripcion?: string | null;
  precio: number;
  disponible: boolean;
  imagen?: string | null;
  tipoId: number;
};

export type ProductoTipoDTO = {
  id: number;
  nombre: string;
};

export type UsuarioRolDTO = {
  id: number;
  nombre: string;
};

export type UsuarioDTO = {
  id: number;
  nombre: string;
  activo: boolean;
};

export const ACTION_META: Record<ActionKey, { title: string; hint: string }> = {
  addTable: {
    title: "Añadir mesa",
    hint: "Da de alta una nueva mesa en el local.",
  },
  removeTable: {
    title: "Eliminar mesa",
    hint: "Elimina una mesa existente por su número.",
  },
  editTable: {
    title: "Modificar mesa",
    hint: "Cambia el número y/o la zona de una mesa.",
  },
  addProduct: {
    title: "Añadir producto",
    hint: "Crea un nuevo producto para el catálogo.",
  },
  editProduct: {
    title: "Editar producto",
    hint: "Edita un producto existente en el catálogo.",
  },
  removeProduct: {
    title: "Eliminar producto",
    hint: "Busca un producto y elimínalo del catálogo.",
  },
  addUser: {
    title: "Registrar usuario",
    hint: "Da de alta un nuevo usuario del sistema.",
  },
  removeUser: {
    title: "Eliminar usuario",
    hint: "Marca como inactivo el usuario indicado",
  },
  logout: {
    title: "Cerrar sesión",
    hint: "Cierra la sesión del usuario actual.",
  },
};
