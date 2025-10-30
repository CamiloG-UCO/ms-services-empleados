package co.edu.hotel.empleadoservice.controller.actualizarEmpleado;

import co.edu.hotel.empleadoservice.domain.Empleado;
import co.edu.hotel.empleadoservice.services.actualizarEmpleado.ActualizarEmpleadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empleados")
public class ActualizarEmpleadoController {

    private final ActualizarEmpleadoService service;

    public ActualizarEmpleadoController(ActualizarEmpleadoService service) {
        this.service = service;
    }

    public record UpdateEmployeeRequest(
            String name,
            String contactNumber,
            String role,
            java.math.BigDecimal salary
    ) {}

    @PutMapping("/{id}")
    public ResponseEntity<?> updateById(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String id,
            @RequestBody UpdateEmployeeRequest body
    ) {
        try {
            Empleado updated = service.updateById(
                    id,
                    authorization,
                    new ActualizarEmpleadoService.UpdateCmd(
                            body.name(),
                            body.contactNumber(),
                            body.role(),
                            body.salary()
                    )
            );
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/code/{code}")
    public ResponseEntity<?> updateByCode(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String code,
            @RequestBody UpdateEmployeeRequest body
    ) {
        try {
            Empleado updated = service.updateByCode(
                    code,
                    authorization,
                    new ActualizarEmpleadoService.UpdateCmd(
                            body.name(),
                            body.contactNumber(),
                            body.role(),
                            body.salary()
                    )
            );
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
