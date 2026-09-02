import { useEffect, useMemo, useRef, useState } from "react";
import { useSessionStore } from "../../session/sessionStore";
import AllergenLegendModal from "../components/AllergenLegendModal"; // 1. Importamos la leyenda
import ProductCard from "../components/ProductCard";
import ProductModal from "../components/ProductModal";
import { customerIcons } from "../customerIcons";
import { useMenuData } from "../hooks/useMenuData";
import "../styles.css";
import type { Alergeno, ProductDTO } from "../types/menu";

export default function MenuPage() {
  const [selectedProduct, setSelectedProduct] = useState<ProductDTO | null>(
    null,
  );
  const [isLegendOpen, setIsLegendOpen] = useState(false);
  const [activeCategory, setActiveCategory] = useState<number | null>(null);
  const categoryRefs = useRef<Record<number, HTMLElement | null>>({});
  const [searchQuery, setSearchQuery] = useState("");
  const sessionCode = useSessionStore((state) => state.sessionCode);
  const { products, categories, loading, error } = useMenuData(sessionCode);

  const productosDisponibles = useMemo(() => {
    return products.filter((p) => p.disponible);
  }, [products]);

  const listaAlergenosUnicos = useMemo(() => {
    const map = new Map<string, Alergeno>();
    products.forEach((p) => {
      p.alergenos?.forEach((a) => {
        if (!map.has(a.nombre)) {
          map.set(a.nombre, a);
        }
      });
    });
    return Array.from(map.values());
  }, [products]);

  const productosPorCategorias = useMemo(() => {
    return categories.map((cat) => ({
      id: cat.id,
      nombre: cat.nombre,
      products: productosDisponibles.filter((p) => p.tipoId === cat.id),
    }));
  }, [categories, productosDisponibles]);

  const productosFiltrados = useMemo(() => {
    const query = searchQuery.toLowerCase();

    return productosPorCategorias
      .map((cat) => ({
        ...cat,
        products: cat.products.filter(
          (p) =>
            p.nombre.toLowerCase().includes(query) ||
            p.descripcion?.toLowerCase().includes(query),
        ),
      }))
      .filter((cat) => cat.products.length > 0);
  }, [productosPorCategorias, searchQuery]);

  useEffect(() => {
    const container = document.querySelector(".menu-content");

    const onScroll = () => {
      if (!container) return;

      const scrollPosition = container.scrollTop + 100;

      for (const cat of productosPorCategorias) {
        const el = categoryRefs.current[cat.id];
        if (!el) continue;

        if (
          scrollPosition >= el.offsetTop &&
          scrollPosition < el.offsetTop + el.offsetHeight
        ) {
          setActiveCategory(cat.id);
        }
      }
    };

    container?.addEventListener("scroll", onScroll);
    return () => container?.removeEventListener("scroll", onScroll);
  }, [productosPorCategorias]);

  const scrollToCategory = (id: number) => {
    const container = document.querySelector(".menu-content");
    const element = categoryRefs.current[id];

    if (element && container) {
      const containerTop = container.getBoundingClientRect().top;
      const elementTop = element.getBoundingClientRect().top;

      const scrollTarget =
        container.scrollTop + (elementTop - containerTop) - 60;

      container.scrollTo({
        top: scrollTarget,
        behavior: "smooth",
      });
    }
  };

  if (loading) return <p className="bill-loading">Cargando productos...</p>;
  if (error) return <p className="bill-loading">Error: {error}</p>;

  return (
    <div className="page-menu">
      <div className="sticky-header">
        <div className="search-container">
          <div className="search-input-wrapper">
            <img
              src={customerIcons.search}
              className="search-icon"
              alt=""
              aria-hidden="true"
            />
            <input
              type="text"
              placeholder="¿Qué te apetece hoy?"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="modern-search-input"
            />
            {searchQuery && (
              <button
                className="clear-search"
                onClick={() => setSearchQuery("")}
              >
                <img src={customerIcons.clear} alt="" aria-hidden="true" />
              </button>
            )}
          </div>

          <button
            className="allergen-legend-btn"
            onClick={() => setIsLegendOpen(true)}
            title="Leyenda de alérgenos"
          >
            <img
              src={customerIcons.info}
              className="info-icon"
              alt=""
              aria-hidden="true"
            />
          </button>
        </div>

        <div className="categories-scroll">
          {productosFiltrados.map((cat) => (
            <button
              key={cat.id}
              data-id={cat.id}
              className={`category-tab ${activeCategory === cat.id ? "active" : ""}`}
              onClick={() => scrollToCategory(cat.id)}
            >
              {cat.nombre}
            </button>
          ))}
        </div>
      </div>

      <div className="menu-content">
        {productosFiltrados.map((cat) => (
          <section
            key={cat.id}
            className="menu-category"
            ref={(el) => {
              categoryRefs.current[cat.id] = el ?? null;
            }}
          >
            <h2 className="category-title">{cat.nombre}</h2>
            <div className="category-products">
              {cat.products.map((product) => (
                <ProductCard
                  key={product.id}
                  onClick={setSelectedProduct}
                  product={{
                    id: product.id,
                    nombre: product.nombre,
                    precio: product.precio,
                    descripcion: product.descripcion ?? "",
                    alergenos: product.alergenos ?? [],
                    imagen: product.imagen ?? "food.png",
                    tipoId: product.tipoId,
                    disponible: product.disponible,
                    gruposModificadores: product.gruposModificadores,
                  }}
                />
              ))}
            </div>
          </section>
        ))}
      </div>

      <ProductModal
        product={selectedProduct}
        onClose={() => setSelectedProduct(null)}
      />

      <AllergenLegendModal
        isOpen={isLegendOpen}
        onClose={() => setIsLegendOpen(false)}
        alergenos={listaAlergenosUnicos}
      />
    </div>
  );
}
