package co.edu.hotel.empleadoservice.services;


import co.edu.hotel.empleadoservice.domain.Empleado;
import co.edu.hotel.empleadoservice.repository.IEmpleadoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class EmpleadoService {

    private final IEmpleadoRepository empleadoRepository;

    private final Empleado empleado;

    public Empleado registrarEmpleado(Empleado empleado, String responsable) {
        // Generar código automático
        String codigo = generarCodigoEmpleado();
        empleado.setCode(codigo);

        // Registrar metadata
        empleado.setRegisteredBy(responsable);
        empleado.setRegistrationDate(LocalDate.now());

        return empleadoRepository.save(empleado);
    }

    public Empleado actualizarEmpleado(String code, String nuevoRol, BigDecimal nuevoSalario, String responsable) {
        return empleadoRepository.save(empleado);
    }

    public Optional<Empleado> consultarEmpleado(String id) {
        return empleadoRepository.findById(id);
    }

    public void eliminarEmpleado(String id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        empleadoRepository.delete(empleado);
    }

    public List<Empleado> listarEmpleados() {
        return empleadoRepository.findAll();
    }

    private String generarCodigoEmpleado() {
        int numero = (int) (Math.random() * 9000) + 1000;
        return "EMP-" + numero;
    }
}
