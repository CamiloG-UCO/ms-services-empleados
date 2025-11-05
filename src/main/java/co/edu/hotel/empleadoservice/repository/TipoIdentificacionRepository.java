package co.edu.hotel.empleadoservice.repository;

import co.edu.hotel.empleadoservice.domain.TipoIdentificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TipoIdentificacionRepository extends JpaRepository<TipoIdentificacion, UUID> {
}
