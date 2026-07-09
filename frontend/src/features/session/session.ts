export type SessionContextType = {
  sessionCode: string | null;
  sessionData: SesionClienteDTO | null;
  actualizarEstadoMesa: (nuevoEstado: string) => void;
};

export type SesionClienteDTO = {
  sesionId: number;
  sessionCode: string;
  mesaId: number;
  numeroMesa: number;
  estadoMesa: string;
}