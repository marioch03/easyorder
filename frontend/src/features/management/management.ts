import { icons } from "./icons";

export type ManagementActionKey = "tables" | "orders" | "comandas" | "logout";

export type ManagementAction = {
  key: ManagementActionKey;
  label: string;
  icon: string;
  group: "Mesas" | "Pedidos" | "Comandas" | "Cerrar Sesión";
  variant?: "default" | "danger";
};

export const MANAGEMENT_ACTIONS: ManagementAction[] = [
  {
    key: "tables",
    label: "Mesas",
    icon: icons.tables,
    group: "Mesas",
  },
  {
    key: "orders",
    label: "Pedidos",
    icon: icons.orders,
    group: "Pedidos",
  },
  {
    key: "comandas",
    label: "Comandas",
    icon: icons.comandas,
    group: "Comandas",
  },
  {
    key: "logout",
    label: "Cerrar Sesión",
    icon: icons.logout,
    group: "Cerrar Sesión",
    variant: "danger",
  },
];

export const MANAGEMENT_ACTION_META: Record<
  ManagementActionKey,
  { title: string; hint: string }
> = {
  tables: {
    title: "Gestión de mesas",
    hint: "Visualiza el plano y estado general de las mesas.",
  },
  orders: {
    title: "Gestión de pedidos",
    hint: "El camarero gestionará los pedidos de los clientes.",
  },
  comandas: {
    title: "Comandas",
    hint: "El camarero tomará nota de los pedidos de los clientes.",
  },
  logout: {
    title: "Cerrar Sesión",
    hint: "Cierra la sesión del usuario.",
  },
};
