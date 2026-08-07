const svg = (inner: string, color: string, strokeWidth = 1.8) =>
  `data:image/svg+xml,${encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="${strokeWidth}" stroke-linecap="round" stroke-linejoin="round" style="color:#ffffff"><circle cx="12" cy="12" r="12" fill="${color}" stroke="none"/>${inner}</svg>`,
  )}`;

export const alergenoIcons = {
  // Espiga de trigo: Dorado / Ámbar
  gluten: svg(
    `<path d="M12 20V4"/>
     <path d="M12 6c-2-1.5-4-1-4 1s2 2.5 4 1"/>
     <path d="M12 6c2-1.5 4-1 4 1s-2 2.5-4 1"/>
     <path d="M12 10c-2-1.5-4-1-4 1s2 2.5 4 1"/>
     <path d="M12 10c2-1.5 4-1 4 1s-2 2.5-4 1"/>
     <path d="M12 14c-2-1.5-4-1-4 1s2 2.5 4 1"/>
     <path d="M12 14c2-1.5 4-1 4 1s-2 2.5-4 1"/>
     <path d="M12 4l-2-2M12 4l2-2"/>`,
    "#D97706",
  ),

  // Cangrejo / Crustáceo: Rojo Coral
  crustaceos: svg(
    `<path d="M12 16c-3.5 0-6.5-2-6.5-4.8 0-2.8 2.9-4.7 6.5-4.7s6.5 1.9 6.5 4.7c0 2.8-3 4.8-6.5 4.8z"/>
     <path d="M6 11.2C4.2 10 3 8.2 3 6.8 3 5.3 4.5 4.5 5.8 5.8L7.5 8"/>
     <path d="M18 11.2c1.8-1.2 3-3 3-4.4 0-1.5-1.5-2.3-2.8-1L16.5 8"/>
     <path d="M7.5 15.5L6 18M10 16l-1 2.5M14 16l1 2.5M16.5 15.5L18 18"/>
     <circle cx="9.5" cy="8.8" r=".7" fill="currentColor" stroke="none"/>
     <circle cx="14.5" cy="8.8" r=".7" fill="currentColor" stroke="none"/>`,
    "#E11D48",
  ),

  // Huevo: Amarillo Yema
  huevos: svg(
    `<path d="M12 3.5c-3.8 0-7 3.5-7 8.2 0 4.2 3.1 7.8 7 7.8s7-3.6 7-7.8c0-4.7-3.2-8.2-7-8.2z"/>
     <circle cx="12" cy="13.2" r="2.8"/>`,
    "#F59E0B",
  ),

  // Pez: Azul Marino
  pescado: svg(
    `<path d="M4 12c3-4 8.5-6 13.5-3.5 1.5-2.5 3-3 4.5-2.5-.5 2-.5 3.5.5 5.5-1 2-1 3.5-.5 5.5-1.5.5-3 0-4.5-2.5C12.5 18 7 16 4 12z"/>
     <path d="M14.5 9.5c-1 1.5-1 3.5 0 5"/>
     <circle cx="7.5" cy="11" r=".8" fill="currentColor" stroke="none"/>`,
    "#0284C7",
  ),

  // Cacahuete: Marrón Cáscara
  cacahuetes: svg(
    `<path d="M12 4.5c-2.2 0-3.8 1.5-3.8 3.3 0 1.3.7 2.3 1.7 2.9-1.2.8-1.9 2-1.9 3.6 0 2.3 1.8 4.2 4 4.2s4-1.9 4-4.2c0-1.6-.7-2.8-1.9-3.6 1-.6 1.7-1.6 1.7-2.9 0-1.8-1.6-3.3-3.8-3.3z"/>
     <path d="M10 8c.8.6 3.2.6 4 0M9.5 14.5c1.2.8 3.8.8 5 0"/>`,
    "#854D0E",
  ),

  // Vaina de soja: Verde Lima
  soja: svg(
    `<path d="M6 18c2.5 1 6.5.5 10.5-3.5S20 7.5 18 5c-3.5 0-8 2.5-11 6.5S4.5 16 6 18z"/>
     <path d="M9 13.5c1.5-2 4-3.5 6.5-4.5"/>
     <circle cx="9.5" cy="14.5" r="1.1" fill="currentColor"/>
     <circle cx="13.5" cy="11" r="1.1" fill="currentColor"/>`,
    "#65A30D",
  ),

  // Botella de leche: Azul Lácteo
  leche: svg(
    `<path d="M10 3.5h4"/>
     <path d="M10 3.5v2L8.5 8v10a1.5 1.5 0 0 0 1.5 1.5h4a1.5 1.5 0 0 0 1.5-1.5V8L14 5.5v-2"/>
     <path d="M8.5 12.5c1.8 1 3.2-1 5 0"/>`,
    "#2563EB",
  ),

  // Fruto de cáscara (Avellana/Nuez): Marrón Nuez
  frutosCascara: svg(
    `<path d="M12 19.5c-3.5-3.5-6.5-7-6.5-11 0-2.5 2.5-4.5 6.5-4.5s6.5 2 6.5 4.5c0 4-3 7.5-6.5 11z"/>
     <path d="M5.8 8.5c1.8-1.2 4.2.2 6.2-.8 2 1 4.4-.4 6.2.8"/>
     <path d="M12 4v15.5"/>`,
    "#78350F",
  ),

  // Apio: Verde Vegetal
  apio: svg(
    `<path d="M8 19.5V9c0-2-1-3.5-2.5-4.5M12 19.5V8c0-2.5 0-4.5 0-5.5M16 19.5V9c0-2 1-3.5 2.5-4.5"/>
     <path d="M5.5 4.5C4 3.5 4 2 5.5 2S8 3 7.5 4.5M16.5 4.5C15 3 15 1.5 16.5 1.5S19 2.5 18.5 4.5"/>
     <path d="M8 16h8"/>`,
    "#16A34A",
  ),

  // Biberón de mostaza: Amarillo Mostaza
  mostaza: svg(
    `<path d="M12 3v3M10.5 6h3l.5 3H10l.5-3z"/>
     <path d="M9 9h6a1.5 1.5 0 0 1 1.5 1.5v8a2 2 0 0 1-2 2h-5a2 2 0 0 1-2-2v-8A1.5 1.5 0 0 1 9 9z"/>
     <path d="M10.5 14c1 .8 2 .8 3 0"/>`,
    "#CA8A04",
  ),

  // Semillas de sésamo: Ocre / Tostado
  sesamo: svg(
    `<path d="M12 4.5c-1.2 1.8-1.8 3.2-1.8 4.5 0 1.2.8 2 1.8 2s1.8-.8 1.8-2c0-1.3-.6-2.7-1.8-4.5z"/>
     <path d="M6.5 11c-1.8 1.2-2.7 2.3-2.7 3.5 0 1.2.8 2 1.8 2 1.2 0 2.2-1 2.7-2.2.5-1.3.2-2.3-1.8-3.3z"/>
     <path d="M17.5 11c1.8 1.2 2.7 2.3 2.7 3.5 0 1.2-.8 2-1.8 2-1.2 0-2.2-1-2.7-2.2-.5-1.3-.2-2.3 1.8-3.3z"/>`,
    "#A16207",
  ),

  // Sulfitos (Copa de vino): Púrpura / Morado
  sulfitos: svg(
    `<path d="M7 4.5h10l-1 5.5a4 4 0 0 1-8 0L7 4.5z"/>
     <path d="M12 14v5M9 19h6"/>
     <path d="M7.5 8h9"/>
     <circle cx="10" cy="2.5" r=".6" fill="currentColor" stroke="none"/>
     <circle cx="14" cy="2" r=".8" fill="currentColor" stroke="none"/>`,
    "#9333EA",
  ),

  // Altramuces: Amarillo Cítrico
  altramuces: svg(
    `<path d="M12 4.5c-4 0-7.2 3.1-7.2 7.5s3.2 7.5 7.2 7.5 7.2-3.1 7.2-7.5S16 4.5 12 4.5z"/>
     <path d="M12 8a1.5 1.5 0 1 0 0 3 1.5 1.5 0 0 0 0-3z"/>
     <path d="M12 13.5v2.5"/>`,
    "#EAB308",
  ),

  // Concha de molusco (Vieira): Índigo / Violeta
  moluscos: svg(
    `<path d="M5 13.5c0-4.5 3.1-8.5 7-8.5s7 4 7 8.5c-2 1.5-4.5 2-7 2s-5-.5-7-2z"/>
     <path d="M9.5 15.5l-1 3.5h7l-1-3.5"/>
     <path d="M12 5v10.5M8.5 6.5L10 15.5M15.5 6.5L14 15.5"/>`,
    "#4F46E5",
  ),
};

export type AlergenoIconKey = keyof typeof alergenoIcons;

export function getAlergenoIcon(nombre: string): string | undefined {
  const clave = nombre
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^a-z]/g, "");

  const mapa: Record<string, AlergenoIconKey> = {
    gluten: "gluten",
    crustaceos: "crustaceos",
    huevos: "huevos",
    pescado: "pescado",
    cacahuetes: "cacahuetes",
    soja: "soja",
    leche: "leche",
    frutosdecascara: "frutosCascara",
    apio: "apio",
    mostaza: "mostaza",
    sesamo: "sesamo",
    sulfitos: "sulfitos",
    altramuces: "altramuces",
    moluscos: "moluscos",
  };

  const key = mapa[clave];
  return key ? alergenoIcons[key] : undefined;
}
