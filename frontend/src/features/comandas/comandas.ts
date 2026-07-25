export type ProductoComandaDTO = {
  id: number;
  nombre: string;
  precio: number;
  idTipoProducto: number;
  disponible: boolean;
};

export type NuevaComandaItemDTO = {
  idProducto: number;
  cantidad: number;
  nota: string | null;
};

export type NuevaComandaDTO = {
  idMesa: number;
  items: NuevaComandaItemDTO[];
};

export type LineaComanda = {
  key: string;
  productoId: number;
  nombre: string;
  precio: number;
  cantidad: number;
  nota: string;
};
