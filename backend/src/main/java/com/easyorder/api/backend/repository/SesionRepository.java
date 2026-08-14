package com.easyorder.api.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.easyorder.api.backend.dto.SesionAuthProjection;
import com.easyorder.api.backend.model.Mesa;
import com.easyorder.api.backend.model.Sesion;
import com.easyorder.api.backend.model.SesionEstado;

public interface SesionRepository extends JpaRepository<Sesion, Long> {
	Optional<Sesion> findByMesaAndEstado(Mesa mesa, SesionEstado estado);

	Optional<Sesion> findByMesaIdAndEstadoNombre(Long mesaId, String nombreEstado);

	List<Sesion> findByMesaInAndEstadoNombre(List<Mesa> mesas, String nombreEstado);

	boolean existsByQrCodeUrlAndEstadoNombre(String qrCodeUrl, String estadoNombre);

	Optional<Sesion> findByQrCodeUrl(String qrCodeUrl);

	@Query("""
			SELECT COUNT(s) > 0
			FROM Sesion s
			WHERE s.qrCodeUrl = :qrCodeUrl AND s.estado.nombre = :estadoNombre
			""")
	int existsByQrCodeUrlAndEstadoNombreUnfiltered(
			@Param("qrCodeUrl") String qrCodeUrl,
			@Param("estadoNombre") String estadoNombre);

	@Query("SELECT s FROM Sesion s WHERE s.qrCodeUrl = :qrCodeUrl")
	Optional<Sesion> findByQrCodeUrlUnfiltered(@Param("qrCodeUrl") String qrCodeUrl);

	@Query(value = """
			SELECT s.id AS id,
			       s.id_tenant AS tenantId,
			       e.nombre AS estadoNombre
			FROM sesion s
			JOIN sesion_estado e ON s.id_estado = e.id
			WHERE s.qr_code_url = :qrCodeUrl
			""", nativeQuery = true)
	Optional<SesionAuthProjection> findAuthProjectionByQrCodeUrl(@Param("qrCodeUrl") String qrCodeUrl);
}
