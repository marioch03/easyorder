import type { GrupoModificadorDTO, ModificadorDTO } from "../../common/types";

export type ProductoComandaDTO = {
  id: number;
  nombre: string;
  precio: number;
  idTipoProducto: number;
  disponible: boolean;
  gruposModificadores?: GrupoModificadorDTO[];
};

export type ProductoComandaLike = {
  id: number;
  nombre: string;
  precio: number;
};

export type NuevaComandaItemDTO = {
  idProducto: number;
  cantidad: number;
  nota: string | null;
  modificadores: number[];
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
  modificadores: ModificadorDTO[];
};
