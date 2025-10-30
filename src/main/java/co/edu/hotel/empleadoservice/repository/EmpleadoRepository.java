package co.edu.hotel.empleadoservice.repository;

import co.edu.hotel.empleadoservice.domain.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, UUID> {
    boolean existsByEmail(String email);
    boolean existsByIdentificationNumber(int identificationNumber);

    Optional<Empleado> findFirstByCodeStartingWithOrderByCodeDesc(String prefix);
    Optional<Empleado> findByCode(String code);
}
