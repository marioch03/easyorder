const svg = (inner: string) =>
  `data:image/svg+xml,${encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 44 44" fill="none" stroke="#F5EDED" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">${inner}</svg>`,
  )}`;

const TABLE = `<ellipse cx="22" cy="13" rx="19" ry="5"/><path d="M3 13v4c0 2.8 8.5 5 19 5s19-2.2 19-5v-4"/><path d="M18 22v9a4 4 0 0 0 8 0v-9"/><path d="M18 30c-7 0-11 2.5-11 6a2 2 0 0 0 2 2h26a2 2 0 0 0 2-2c0-3.5-4-6-11-6"/>`;

const ORDERS = `<rect x="8" y="10" width="28" height="30" rx="3"/><path d="M16 10V7a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v3"/><line x1="15" y1="18" x2="29" y2="18"/><line x1="15" y1="25" x2="29" y2="25"/><line x1="15" y1="32" x2="23" y2="32"/>`;

const COMANDAS = `<path d="M10 6h24v32l-4-3-4 3-4-3-4 3-4-3-4 3V6z"/><line x1="15" y1="14" x2="29" y2="14"/><line x1="15" y1="21" x2="29" y2="21"/><line x1="15" y1="27" x2="24" y2="27"/>`;

const LOGOUT = `<path d="M17 38H10a4 4 0 0 1-4-4V10a4 4 0 0 1 4-4h7"/><polyline points="27 30 35 22 27 14"/><line x1="35" y1="22" x2="14" y2="22"/>`;

export const icons = {
  tables: svg(TABLE),
  orders: svg(ORDERS),
  comandas: svg(COMANDAS),
  logout: svg(LOGOUT),
};

export type ManagementIconKey = keyof typeof icons;
