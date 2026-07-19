export type PedidoItemKds = {
  id: number;
  nombre: string;
  idProductoTipo: number;
  cantidad: number;
  nota: string | null;
  mesa: number;
  listoParaServir: boolean;
  createdAt: string;
}

export type ColumnaKds = {
  id: number;
  nombre: string;
  productos: TicketKds[];
};

export type TicketKds = PedidoItemKds & { tiempoEsperaMin: number };
