package co.edu.hotel.empleadoservice.controller.eliminarEmpleado;

import co.edu.hotel.empleadoservice.services.EliminarEmpleado.EliminarEmpleadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins = "http://localhost:4200")
public class EliminarEmpleadoController {

    private final EliminarEmpleadoService service;

    public EliminarEmpleadoController(EliminarEmpleadoService service) {
        this.service = service;
    }

    @DeleteMapping("/codigo/{code}")
    public ResponseEntity<?> deleteByCode(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String code
    ) {
        try {
            service.deleteByCode(code);
            return ResponseEntity.ok(Map.of("message", "Empleado eliminado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Error al eliminar empleado: " + e.getMessage()));
        }
    }
}