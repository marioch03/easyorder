// icons.ts
// Iconos en línea (stroke) codificados como data URI, sin dependencias externas.
// Trazo en blanco cálido (#F5EDED) pensado para verse sobre las insignias con
// degradado primary → secondary de los botones de la barra lateral.

const svg = (inner: string) =>
  `data:image/svg+xml,${encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="#F5EDED" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${inner}</svg>`,
  )}`;

// --- Piezas base reutilizables ---
const TABLE = `<rect x="3" y="8" width="18" height="3" rx="1"/><line x1="6" y1="11" x2="6" y2="20"/><line x1="18" y1="11" x2="18" y2="20"/>`;
const PACKAGE = `<path d="M4 11l7-3.5L18 11l-7 3.5L4 11z"/><path d="M4 11v7l7 3.5 7-3.5v-7"/><line x1="11" y1="14.5" x2="11" y2="21.5"/>`;
const PLUS_MARK = `<line x1="19" y1="1" x2="19" y2="5"/><line x1="17" y1="3" x2="21" y2="3"/>`;
const MINUS_MARK = `<line x1="17" y1="3" x2="21" y2="3"/>`;
const PENCIL_MARK = `<path d="M18.4 1.6l2 2-8 8-2.7.7.7-2.7z"/>`;
const LOGOUT = `<path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/>`;
const USER =`<path d="M15 20v-1.5a3.5 3.5 0 0 0-3.5-3.5h-4A3.5 3.5 0 0 0 4 18.5V20"/><circle cx="9" cy="8" r="3.5"/>`;

export const icons = {
  addTable: svg(TABLE + PLUS_MARK),
  removeTable: svg(TABLE + MINUS_MARK),
  editTable: svg(TABLE + PENCIL_MARK),
  addUser: svg(USER + PLUS_MARK),
  removeUser: svg(USER + MINUS_MARK),
  addProduct: svg(PACKAGE + PLUS_MARK),
  editProduct: svg(PACKAGE + PENCIL_MARK),
  removeProduct: svg(PACKAGE + MINUS_MARK),
  logout: svg(LOGOUT),
};

export type IconKey = keyof typeof icons;
