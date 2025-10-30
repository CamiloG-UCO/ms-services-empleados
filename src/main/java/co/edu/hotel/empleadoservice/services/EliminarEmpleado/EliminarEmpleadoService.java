package co.edu.hotel.empleadoservice.services.EliminarEmpleado;

import co.edu.hotel.empleadoservice.repository.Empleado.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EliminarEmpleadoService {

    private final EmpleadoRepository repository;

    public EliminarEmpleadoService(EmpleadoRepository repository) {
        this.repository = repository;
    }

    public void delete(String authorization, UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("El empleado con ID " + id + " no existe.");
        }
        repository.deleteById(id);
    }
}
