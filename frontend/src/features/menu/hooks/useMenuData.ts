import { useEffect, useState } from "react";
import { getCategories, getProducts } from "../services/menuService";
import type { CategoryDTO, ProductDTO } from "../types/menu";

export function useMenuData(sessionCode: string | null) {
  const [products, setProducts] = useState<ProductDTO[]>([]);
  const [categories, setCategories] = useState<CategoryDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!sessionCode) return;

    const fetchData = async () => {
      try {
        setLoading(true);

        const [productsData, categoriesData] = await Promise.all([
          getProducts(),
          getCategories(),
        ]);

        setProducts(productsData);
        setCategories(categoriesData);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Error desconocido");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [sessionCode]);

  return { products, categories, loading, error };
}
