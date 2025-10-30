package co.edu.hotel.empleadoservice.repository.Empleado;

import co.edu.hotel.empleadoservice.domain.Empleados;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmpleadoRepository extends JpaRepository<Empleados, UUID> {
    boolean existsByEmail(String email);
    boolean existsByIdentificationNumber(int identificationNumber);

    Optional<Empleados> findFirstByCodeStartingWithOrderByCodeDesc(String prefix);
    Optional<Empleados> findByCode(String code);

}
