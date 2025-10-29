package co.edu.hotel.empleadoservice.services.actualizarEmpleado;

import co.edu.hotel.empleadoservice.domain.Empleados;
import co.edu.hotel.empleadoservice.repository.Empleado.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ActualizarEmpleadoService {

    private final EmpleadoRepository repository;

    public ActualizarEmpleadoService(EmpleadoRepository repository) {
        this.repository = repository;
    }

    public record UpdateCmd(
            UUID id,
            String roles,
            BigDecimal salary,
            String contactNumber
    ) {}

    public Empleados update(String authorization, UpdateCmd cmd) {
        Empleados empleado = repository.findById(cmd.id())
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        if (cmd.roles() != null) empleado.setRoles(cmd.roles());
        if (cmd.salary() != null) empleado.setSalary(cmd.salary());
        if (cmd.contactNumber() != null) empleado.setContactNumber(cmd.contactNumber());

        empleado.setRegistrationDate(LocalDate.now());
        empleado.setRegisteredBy(authorization);

        return repository.save(empleado);
    }
}
