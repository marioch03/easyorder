const svg = (inner: string, color: string, strokeWidth = 1.8) =>
  `data:image/svg+xml,${encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="${strokeWidth}" stroke-linecap="round" stroke-linejoin="round" style="color:#ffffff"><circle cx="12" cy="12" r="12" fill="${color}" stroke="none"/>${inner}</svg>`,
  )}`;

export const alergenoIcons = {
  // Espiga de trigo: Dorado / Ámbar
  gluten: svg(
    `<path d="M12 21V6"/>
     <path d="M12 8c-1.5-1.5-3-1.5-4-.5M12 8c1.5-1.5 3-1.5 4-.5"/>
     <path d="M12 11c-1.5-1.5-3-1.5-4-.5M12 11c1.5-1.5 3-1.5 4-.5"/>
     <path d="M12 14c-1.5-1.5-3-1.5-4-.5M12 14c1.5-1.5 3-1.5 4-.5"/>
     <path d="M12 6c0-1.5.8-2.5 2-3"/>`,
    "#D97706",
  ),

  // Gamba: Rojo Coral
  crustaceos: svg(
    `<path d="M5 18c0-6 3-11 9-12 2 3 2 6 0 8-3 1-5 0-6-2"/>
     <path d="M8 14l-3 4M11 15l-2.5 4.5M14 14.5l-2 4.5"/>
     <path d="M17 6l2-2"/>
     <circle cx="15.5" cy="6.5" r=".6" fill="currentColor" stroke="none"/>`,
    "#E11D48",
  ),

  // Huevo: Amarillo Yema
  huevos: svg(
    `<path d="M12 21c4 0 6-3.5 6-7.5C18 8 14.5 3 12 3S6 8 6 13.5C6 17.5 8 21 12 21z"/>`,
    "#F59E0B",
  ),

  // Pez: Azul Marino
  pescado: svg(
    `<path d="M3 12c3-4 8-6 13-4 2-3 4-3 5-2-1 2-1 4 0 6-1 1-3 1-5-2-5 2-10 0-13-4z" transform="translate(0,0)"/>
     <circle cx="7.5" cy="11" r=".6" fill="currentColor" stroke="none"/>`,
    "#0284C7",
  ),

  // Cacahuete: Marrón Cáscara
  cacahuetes: svg(
    `<path d="M9 4C6.5 4 5 6 5 8.5c0 1.5.8 2.3 1.8 3-1 .7-1.8 1.6-1.8 3.2 0 2.6 1.8 4.8 5 5.3 3.2-.5 5-2.7 5-5.3 0-1.6-.8-2.5-1.8-3.2 1-.7 1.8-1.5 1.8-3C15 6 13.5 4 11 4"/>
     <path d="M9.2 8.6c.5.5 1.6.5 2.1 0M9.2 15.4c.5.5 2.5.5 3 0"/>`,
    "#854D0E",
  ),

  // Vaina de soja: Verde Lima
  soja: svg(
    `<path d="M7 11c0-4 2-7 5-8 1 2 1 4 0 5.5-2 .5-3.5 2-3.5 4.5"/>
     <path d="M9 8c-3 1-5 4-5 8 0 3 2 5 5 5s5-2.5 5-6c0-1.5-.5-2.5-1.3-3.2"/>
     <circle cx="8.3" cy="14.5" r="1.3"/>
     <circle cx="11" cy="17.5" r="1.3"/>`,
    "#65A30D",
  ),

  // Botella de leche: Azul Lácteo
  leche: svg(
    `<path d="M10 3h4l1 3-1.5 1.5v2L15 12v7a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2v-7l1.5-2.5v-2L9 6l1-3z"/>
     <path d="M9 14h6"/>`,
    "#2563EB",
  ),

  // Fruto de cáscara: Marrón Nuez
  frutosCascara: svg(
    `<path d="M12 21c4.5-1 6-4.5 6-8 0-5-3-9-6-9s-6 4-6 9c0 3.5 1.5 7 6 8z"/>
     <path d="M12 4v17M9 12c1 .8 2 .8 3 0M9 16c1 .8 2 .8 3 0"/>`,
    "#78350F",
  ),

  // Apio: Verde Vegetal
  apio: svg(
    `<path d="M9 21V9M12 21V7M15 21V9"/>
     <path d="M9 9c-1-2-.5-4 1-5M15 9c1-2 .5-4-1-5M12 7c-1.5-1-2-3-1-5M12 7c1.5-1 2-3 1-5"/>`,
    "#16A34A",
  ),

  // Mostaza: Amarillo Mostaza
  mostaza: svg(
    `<path d="M9 8h6l1 12a2 2 0 0 1-2 2H10a2 2 0 0 1-2-2L9 8z"/>
     <path d="M10 8V5a2 2 0 0 1 2-2 2 2 0 0 1 2 2v3"/>
     <circle cx="10.8" cy="13" r=".7" fill="currentColor" stroke="none"/>
     <circle cx="13.3" cy="14.5" r=".7" fill="currentColor" stroke="none"/>
     <circle cx="11.5" cy="17" r=".7" fill="currentColor" stroke="none"/>`,
    "#CA8A04",
  ),

  // Semillas de sésamo: Ocre / Tostado
  sesamo: svg(
    `<ellipse cx="8" cy="9" rx="2" ry="1.1" transform="rotate(-25 8 9)"/>
     <ellipse cx="15" cy="7.5" rx="2" ry="1.1" transform="rotate(15 15 7.5)"/>
     <ellipse cx="7.5" cy="16" rx="2" ry="1.1" transform="rotate(10 7.5 16)"/>
     <ellipse cx="15.5" cy="15" rx="2" ry="1.1" transform="rotate(-20 15.5 15)"/>
     <ellipse cx="12" cy="12" rx="2" ry="1.1" transform="rotate(-5 12 12)"/>`,
    "#A16207",
  ),

  // Sulfitos (Copa de vino): Púrpura / Morado
  sulfitos: svg(
    `<path d="M7 4h10l-1 6a4 4 0 0 1-8 0L7 4z"/>
     <path d="M12 14v5M9 21h6"/>
     <path d="M9 2c0 1-1 1-1 2s1 1 1 2M13 2c0 1-1 1-1 2s1 1 1 2"/>`,
    "#9333EA",
  ),

  // Altramuces: Amarillo Cítrico
  altramuces: svg(
    `<path d="M12 3c-3.5 1-5.5 4-5.5 8 0 5 2.5 9 5.5 10 3-1 5.5-5 5.5-10 0-4-2-7-5.5-8z"/>
     <circle cx="12" cy="9" r="1.2"/>
     <circle cx="12" cy="13.5" r="1.2"/>
     <circle cx="12" cy="18" r="1.2"/>`,
    "#EAB308",
  ),

  // Concha de moluscos: Índigo / Violeta
  moluscos: svg(
    `<path d="M4 13c2.5-6 5-9 8-9s5.5 3 8 9c-2.5 2-5 3-8 3s-5.5-1-8-3z"/>
     <path d="M12 4v12M8.5 5.5L9.5 16M15.5 5.5L14.5 16"/>`,
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
