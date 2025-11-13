package co.edu.hotel.empleadoservice.controller.consultarEmpleado;

import co.edu.hotel.empleadoservice.domain.Empleado;
import co.edu.hotel.empleadoservice.services.consultarEmpleados.ConsultarEmpleadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class ConsultarEmpleadoController {

    private final ConsultarEmpleadoService service;

    public ConsultarEmpleadoController(ConsultarEmpleadoService service) {
        this.service = service;
    }

    // Consultar empleado por código
    @GetMapping("/consultar/{code}")
    public ResponseEntity<Empleado> getEmpleado(@PathVariable String code) {
        try {
            Empleado empleado = service.getEmpleadoByCode(code);
            return ResponseEntity.ok(empleado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Listar empleados por hotel
    @GetMapping("/consultar/hotel/{hotel}")
    public ResponseEntity<List<Empleado>> getEmpleadosByHotel(@PathVariable String hotel) {
        List<Empleado> empleados = service.getEmpleadosByHotel(hotel);
        return ResponseEntity.ok(empleados); // aunque la lista esté vacía, devolvemos 200 OK
    }

    // En ConsultarEmpleadoController
    @GetMapping("/consultar")
    public ResponseEntity<List<Empleado>> getAllEmpleados() {
        List<Empleado> empleados = service.getAllEmpleados();
        return ResponseEntity.ok(empleados); // aunque la lista esté vacía, devolvemos 200 OK
    }

}