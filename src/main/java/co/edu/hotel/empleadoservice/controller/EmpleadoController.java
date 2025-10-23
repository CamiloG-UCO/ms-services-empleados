package co.edu.hotel.empleadoservice.controller;

import co.edu.hotel.empleadoservice.domain.Empleado;
import co.edu.hotel.empleadoservice.services.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService service;
    @Autowired
    private Empleado empleado;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Empleado empleado) {
        Empleado creado = service.registrarEmpleado(empleado, "ADMIN");
        return ResponseEntity.ok("Empleado registrado exitosamente con código " + creado.getCode());
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<?> actualizar() {
        return null;
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Empleado> consultar(@PathVariable String id) {
        return null;
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        service.eliminarEmpleado(id);
        return ResponseEntity.ok("Empleado eliminado exitosamente");
    }
}
