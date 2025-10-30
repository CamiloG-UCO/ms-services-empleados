package co.edu.hotel.empleadoservice.controller.eliminarEmpleado;

import co.edu.hotel.empleadoservice.services.EliminarEmpleado.EliminarEmpleadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/empleados")
public class EliminarEmpleadoController {

    private final EliminarEmpleadoService service;

    public EliminarEmpleadoController(EliminarEmpleadoService service) {
        this.service = service;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID id
    ) {
        try {
            service.delete(authorization, id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 sin contenido
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
