package co.edu.hotel.empleadoservice.repository.TipoDeIdentificacion;

import co.edu.hotel.empleadoservice.domain.TipoIdentificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TipoDeIdentificacionRepository extends JpaRepository<TipoIdentificacion, UUID> {
}
