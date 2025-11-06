package co.edu.hotel.empleadoservice.controller.eliminarEmpleado;

import co.edu.hotel.empleadoservice.services.EliminarEmpleado.EliminarEmpleadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins = "http://localhost:4200")
public class EliminarEmpleadoController {

    private final EliminarEmpleadoService service;

    public EliminarEmpleadoController(EliminarEmpleadoService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEmpleadoPorId(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable UUID id
    ) {
        return service.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No se encontró ningún empleado con el ID " + id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable UUID id
    ) {
        try {
            service.delete(id);
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
