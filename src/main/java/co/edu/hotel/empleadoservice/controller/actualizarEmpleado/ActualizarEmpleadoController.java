package co.edu.hotel.empleadoservice.controller.actualizarEmpleado;

import co.edu.hotel.empleadoservice.domain.Empleados;
import co.edu.hotel.empleadoservice.services.actualizarEmpleado.ActualizarEmpleadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/empleados")
public class ActualizarEmpleadoController {

    private final ActualizarEmpleadoService service;

    public ActualizarEmpleadoController(ActualizarEmpleadoService service) {
        this.service = service;
    }

    public record UpdateEmployeeRequest(
            String roles,
            BigDecimal salary,
            String contactNumber
    ) {}

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID id,
            @RequestBody UpdateEmployeeRequest body
    ) {
        try {
            Empleados updated = service.update(
                    authorization,
                    new ActualizarEmpleadoService.UpdateCmd(
                            id,
                            body.roles(),
                            body.salary(),
                            body.contactNumber()
                    )
            );
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
