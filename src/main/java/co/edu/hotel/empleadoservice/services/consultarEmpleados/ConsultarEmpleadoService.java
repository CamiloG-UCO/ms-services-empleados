package co.edu.hotel.empleadoservice.services.consultarEmpleados;

import co.edu.hotel.empleadoservice.domain.Empleado;
import co.edu.hotel.empleadoservice.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultarEmpleadoService {

    private final EmpleadoRepository repository;

    public ConsultarEmpleadoService(EmpleadoRepository repository) {
        this.repository = repository;
    }

    // Consultar empleado por código
    public Empleado getEmpleadoByCode(String code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Empleado con código " + code + " no encontrado"));
    }

    // Listar empleados por hotel
    public List<Empleado> getEmpleadosByHotel(String hotel) {
        return repository.findByHotelIgnoreCase(hotel);
    }

    // En ConsultarEmpleadoService
    public List<Empleado> getAllEmpleados() {
        return repository.findAll();
    }



}
