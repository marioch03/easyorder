export type SesionDTO = {
  id: number;
  qrCodeUrl: string;
};

export type Mesa = {
  id: number;
  numero: number;
  estado: string;
  zona: string | null;
  sesionActiva: SesionDTO | null;
};

export type Zona = {
  id: number;
  nombre: string;
};