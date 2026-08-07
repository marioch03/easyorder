const svg = (inner: string, color: string = "#F5EDED") =>
  `data:image/svg+xml,${encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="${color}" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${inner}</svg>`,
  )}`;

const MENU = `
  <path d="M5 3v4a3 3 0 0 0 6 0V3"/>
  <line x1="8" y1="3" x2="8" y2="21"/>
  
  <line x1="18" y1="3" x2="18" y2="21"/>
  <path d="M18 3c-3 0-4 2-4 6v3h4"/>
`;

const CART = `
  <circle cx="9" cy="20" r="1.5"/>
  <circle cx="18" cy="20" r="1.5"/>
  <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h8.72a2 2 0 0 0 2-1.61L22 6H6"/>
`;

const BILL = `
  <path d="M7 3h10a1 1 0 0 1 1 1v17l-3-2-3 2-3-2-3 2V4a1 1 0 0 1 1-1z"/>
  <line x1="10" y1="8" x2="14" y2="8"/>
  <line x1="10" y1="12" x2="14" y2="12"/>
`;

const SEARCH = `<circle cx="11" cy="11" r="7"/><line x1="16.5" y1="16.5" x2="21" y2="21"/>`;

const CLEAR = `<line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>`;

const INFO = `<circle cx="12" cy="12" r="9"/><line x1="12" y1="11" x2="12" y2="16"/><line x1="12" y1="8" x2="12" y2="8.01"/>`;

export const customerIcons = {
  menu: svg(MENU),
  cart: svg(CART),
  bill: svg(BILL),

  search: svg(SEARCH, "#334155"),
  clear: svg(CLEAR, "#334155"),
  info: svg(INFO, "#334155"),
};

export type NavIconKey = keyof typeof customerIcons;
