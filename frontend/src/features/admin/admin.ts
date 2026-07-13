import { icons } from "./icons";

export type ActionKey =
  | "addTable"
  | "removeTable"
  | "editTable"
  | "addUser"
  | "addProduct"
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
  group: "Mesas" | "Catálogo" | "Personal";
  variant?: "default" | "danger";
};

export const ACTIONS: Action[] = [
  { key: "addTable", label: "Añadir mesa", icon: icons.addTable, group: "Mesas" },
  { key: "editTable", label: "Modificar mesa", icon: icons.editTable, group: "Mesas" },
  {
    key: "removeTable",
    label: "Eliminar mesa",
    icon: icons.removeTable,
    group: "Mesas",
    variant: "danger",
  },
  { key: "addProduct", label: "Añadir producto", icon: icons.addProduct, group: "Catálogo" },
  {
    key: "removeProduct",
    label: "Eliminar producto",
    icon: icons.removeProduct,
    group: "Catálogo",
    variant: "danger",
  },
  { key: "addUser", label: "Registrar usuario", icon: icons.addUser, group: "Personal" },
];

export const tableActions = ACTIONS.filter((a) => a.group === "Mesas");
export const catalogActions = ACTIONS.filter((a) => a.group === "Catálogo");
export const staffActions = ACTIONS.filter((a) => a.group === "Personal");