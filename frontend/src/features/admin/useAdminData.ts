import { useEffect, useState } from "react";
import type { ProductoDTO, ProductoTipoDTO, UsuarioRolDTO, ZonaDTO } from "./admin";
import { getProductos, getTiposProducto, getUsuarioRoles, getZonas } from "./adminService";

export function useAdminData() {
  const [zonas, setZonas] = useState<ZonaDTO[]>([]);
  const [productos, setProductos] = useState<ProductoDTO[]>([]);
  const [tiposProducto, setTiposProducto] = useState<ProductoTipoDTO[]>([]);
  const [usuarioRoles, setUsuarioRoles] = useState<UsuarioRolDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);

        const [zonasData, productosData, tiposProductoData, usuarioRolesData] = await Promise.all([
          getZonas(),
          getProductos(),
          getTiposProducto(),
          getUsuarioRoles(),
        ]);

        setZonas(zonasData);
        setProductos(productosData);
        setTiposProducto(tiposProductoData);
        setUsuarioRoles(usuarioRolesData);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Error desconocido");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  },);

  return { zonas, productos, tiposProducto, usuarioRoles, loading, error };
}
