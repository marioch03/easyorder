import { useEffect, useState } from "react";
import type { MesaDTO, ProductoDTO, ProductoTipoDTO, UsuarioDTO, UsuarioRolDTO, ZonaDTO } from "./admin";
import { getMesas, getProductos, getTiposProducto, getUsuarioRoles, getUsuarios, getZonas } from "./adminService";

export function useAdminData() {
  const [zonas, setZonas] = useState<ZonaDTO[]>([]);
  const [mesas, setMesas] = useState<MesaDTO[]>([]);
  const [productos, setProductos] = useState<ProductoDTO[]>([]);
  const [tiposProducto, setTiposProducto] = useState<ProductoTipoDTO[]>([]);
  const [usuarioRoles, setUsuarioRoles] = useState<UsuarioRolDTO[]>([]);
  const [usuarios, setUsuarios] = useState<UsuarioDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);

        const [zonasData, mesasData, productosData, tiposProductoData, usuarioRolesData, usuariosData] = await Promise.all([
          getZonas(),
          getMesas(),
          getProductos(),
          getTiposProducto(),
          getUsuarioRoles(),
          getUsuarios(),
        ]);

        setZonas(zonasData);
        setMesas(mesasData);
        setProductos(productosData);
        setTiposProducto(tiposProductoData);
        setUsuarioRoles(usuarioRolesData);
        setUsuarios(usuariosData);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Error desconocido");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  },[]);

  return { zonas, mesas, setMesas, productos, tiposProducto, usuarioRoles, usuarios, setUsuarios, loading, error };
}
