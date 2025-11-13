package co.edu.hotel.empleadoservice.services.EliminarEmpleado;

import co.edu.hotel.empleadoservice.domain.Empleado;
import co.edu.hotel.empleadoservice.services.consultarEmpleados.ConsultarEmpleadoService;
import co.edu.hotel.empleadoservice.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

@Service
public class EliminarEmpleadoService {

    private final EmpleadoRepository repository;
    private final ConsultarEmpleadoService consultarEmpleadoService;

    public EliminarEmpleadoService(EmpleadoRepository repository,
                                   ConsultarEmpleadoService consultarEmpleadoService) {
        this.repository = repository;
        this.consultarEmpleadoService = consultarEmpleadoService;
    }

    // Eliminar empleado por código reutilizando la lógica de consulta
    public void deleteByCode(String code) {
        Empleado empleado = consultarEmpleadoService.getEmpleadoByCode(code);

        if (empleado == null) {
            throw new IllegalArgumentException("No se encontró el empleado con código " + code);
        }

        repository.delete(empleado);
    }
}
