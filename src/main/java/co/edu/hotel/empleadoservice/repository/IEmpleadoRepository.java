package co.edu.hotel.empleadoservice.repository;

import co.edu.hotel.empleadoservice.domain.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IEmpleadoRepository extends JpaRepository<Empleado, String> {
    Optional<Empleado> findById(String id);
}
