const svg = (inner: string) =>
  `data:image/svg+xml,${encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="#F5EDED" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${inner}</svg>`,
  )}`;

const CLOSE = `<line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>`;

const QR = `
  <rect x="4" y="4" width="6" height="6" rx="1"/>
  <rect x="6" y="6" width="2" height="2" fill="#F5EDED" stroke="none"/>
  
  <rect x="14" y="4" width="6" height="6" rx="1"/>
  <rect x="16" y="6" width="2" height="2" fill="#F5EDED" stroke="none"/>
  
  <rect x="4" y="14" width="6" height="6" rx="1"/>
  <rect x="6" y="16" width="2" height="2" fill="#F5EDED" stroke="none"/>
  
  <path d="M14 14h2v2h-2z M18 14h2v2h-2z M14 18h2v2h-2z M18 18h2v2h-2z" fill="#F5EDED" stroke="none"/>
`;

const CUENTA = `
  <path d="M7 3h10a1 1 0 0 1 1 1v17l-3-2-3 2-3-2-3 2V4a1 1 0 0 1 1-1z"/>
  <line x1="10" y1="8" x2="14" y2="8"/>
  <line x1="10" y1="12" x2="14" y2="12"/>
`;

export const icons = {
  close: svg(CLOSE),
  qr: svg(QR),
  cuenta: svg(CUENTA),
};

export type TablesIconKey = keyof typeof icons;
