import { useEffect, useMemo, useRef, useState } from "react";
import { useSession } from "../../session/useSession";
import ProductCard from "../components/ProductCard";
import ProductModal from "../components/ProductModal";
import { useMenuData } from "../hooks/useMenuData";
import "../styles.css";
import type { Product } from "../types/menu";
export default function MenuPage() {
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [activeCategory, setActiveCategory] = useState<number | null>(null);
  const categoryRefs = useRef<Record<number, HTMLElement | null>>({});
  const [searchQuery, setSearchQuery] = useState("");
  const { sessionCode } = useSession();
  const { products, categories, loading, error } = useMenuData(sessionCode);

  const productosDisponibles = useMemo(() => {
    return products.filter((p) => p.disponible);
  }, [products]);

  const productosPorCategorias = useMemo(() => {
    return categories.map((cat) => ({
      id: cat.id,
      nombre: cat.nombre,
      products: productosDisponibles.filter((p) => p.idTipo === cat.id),
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

  if (loading) return <p>Cargando productos...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div className="page-menu">
      <div className="sticky-header">
        <div className="search-container">
          <div className="search-input-wrapper">
            <span className="search-icon">🔍</span>
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
                ✕
              </button>
            )}
          </div>
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
                    name: product.nombre,
                    price: product.precio,
                    description: product.descripcion ?? "",
                    allergens: "",
                    imageUrl: product.imagen ?? "food.png",
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
    </div>
  );
}
