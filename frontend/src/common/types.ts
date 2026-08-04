// --- Tipos de producto ---
export type ProductoTipoDTO = {
  id: number;
  nombre: string;
};

// --- Tipos de mesa y sesión ---
export type SesionDTO = {
  id: number;
  qrCodeUrl: string;
};

export type MesaDTO = {
  id: number;
  numero: number;
  estado: string;
  zona: string | null;
  sesionActiva: SesionDTO | null;
};

export type ZonaDTO = {
  id: number;
  nombre: string;
};

// --- Otros ---
export type SseTopic = "mesas" | "pedidos" | "kds";

export type OrderItemDTO = {
  idProducto: number;
  cantidad: number;
  precioUnitario: number;
  nota: string;
};

export type OrderDTO = {
  items: OrderItemDTO[];
};

