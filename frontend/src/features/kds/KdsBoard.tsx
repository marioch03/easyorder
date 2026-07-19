import './styles.css';

interface ProductoMock {
  id: number;
  cantidad: number;
  nombre: string;
  mesa: string;
  tiempoEsperaMin: number; // Para calcular si está tarde
  notas?: string;          // Opcional: Alergias o preferencias
}

interface TipoProductoMock {
  id: number;
  nombre: string;
  productos: ProductoMock[];
}

interface KdsBoardProps {
  zonaTrabajo: string;
}

export default function KdsBoard({ zonaTrabajo }: KdsBoardProps) {
  
  // Datos realistas simulados
  const MOCK_DATA: Record<string, TipoProductoMock[]> = {
    COCINA: [
      {
        id: 1, nombre: "TAPAS", productos: [
          { id: 101, cantidad: 2, nombre: "Patatas Bravas", mesa: "Mesa 4", tiempoEsperaMin: 5, notas: "Salsa aparte" },
          { id: 102, cantidad: 1, nombre: "Croquetas", mesa: "Barra", tiempoEsperaMin: 18 }, // Este está tardando mucho
        ]
      },
      {
        id: 2, nombre: "RACIONES", productos: [
          { id: 201, cantidad: 1, nombre: "Pulpo a la Gallega", mesa: "Mesa 2", tiempoEsperaMin: 8, notas: "Sin pimentón picante" },
        ]
      },
      {
        id: 3, nombre: "POSTRES", productos: [
          { id: 301, cantidad: 3, nombre: "Tarta de Queso", mesa: "Terraza 1", tiempoEsperaMin: 2 },
        ]
      }
    ]
  };

  const tiposProducto = MOCK_DATA[zonaTrabajo] || [];

  // Función para decidir el color del borde según el tiempo de espera
  const getClaseTiempo = (minutos: number) => {
    if (minutos >= 15) return "late";    // Más de 15 min -> Borde Rojo
    if (minutos >= 10) return "warning"; // Más de 10 min -> Borde Amarillo
    return "";                           // Normal -> Borde Verde
  };

  return (
    <div className="kds-scope kds-container">
      <div 
        className="kds-grid"
        style={{ gridTemplateColumns: `repeat(${tiposProducto.length || 1}, 1fr)` }}
      >
        {tiposProducto.map((tipo) => (
          <div key={tipo.id} className="kds-column">
            
            <h3 className="kds-column-header">
              {tipo.nombre}
            </h3>

            <div className="kds-product-list">
              {tipo.productos.map((producto) => (
                
                <div key={producto.id} className={`kds-ticket-card ${getClaseTiempo(producto.tiempoEsperaMin)}`}>
                  
                  {/* Parte superior: Cantidad, Nombre y Tiempo */}
                  <div className="kds-ticket-header">
                    <span className="kds-qty-name">
                      <span className="kds-qty">{producto.cantidad}x</span> 
                      {producto.nombre}
                    </span>
                    <span className="kds-time">{producto.tiempoEsperaMin} min</span>
                  </div>

                  {/* Notas / Modificadores (Solo se renderiza si existen) */}
                  {producto.notas && (
                    <div className="kds-notes">
                      ↳ {producto.notas}
                    </div>
                  )}

                  {/* Parte inferior: Mesa y Botón de Acción */}
                  <div className="kds-ticket-footer">
                    <span className="kds-table-name">{producto.mesa}</span>
                    <button type="button" className="kds-btn-ready">
                      LISTO
                    </button>
                  </div>

                </div>
              ))}
            </div>
            
          </div>
        ))}
      </div>
    </div>
  );
}