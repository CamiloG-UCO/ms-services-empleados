package co.edu.hotel.empleadoservice.services.EliminarEmpleado;

import co.edu.hotel.empleadoservice.domain.Empleado;
import co.edu.hotel.empleadoservice.repository.EmpleadoRepository;
import co.edu.hotel.empleadoservice.services.consultarEmpleados.ConsultarEmpleadoService;
import org.springframework.stereotype.Service;

@Service
public class EliminarEmpleadoService {

    private final EmpleadoRepository repository;
    private final ConsultarEmpleadoService consultarEmpleadoService;

    public EliminarEmpleadoService(EmpleadoRepository repository, ConsultarEmpleadoService consultarEmpleadoService) {
        this.repository = repository;
        this.consultarEmpleadoService = consultarEmpleadoService;
    }

    // 🔍 Eliminar empleado por código (usando el servicio de consulta)
    public void deleteByCode(String code) {
        try {
            Empleado empleado = consultarEmpleadoService.getEmpleadoByCode(code); // reutiliza la lógica existente
            repository.delete(empleado);
        } catch (Exception e) {
            throw new IllegalArgumentException("No se encontró el empleado con código " + code);
        }
    }
}
