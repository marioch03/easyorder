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
  modificadores: number[];
};

export type OrderDTO = {
  items: OrderItemDTO[];
  total: number;
};

export type ModificadorDTO = {
  id: number;
  nombre: string;
  precioExtra: number;
};

export type GrupoModificadorDTO = {
  id: number;
  nombre: string;
  seleccionMinima: number;
  seleccionMaxima: number;
  modificadores: ModificadorDTO[];
};
