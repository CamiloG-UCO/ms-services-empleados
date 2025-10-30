package co.edu.hotel.empleadoservice.controller;

import co.edu.hotel.empleadoservice.services.CrearEmpleado.CrearEmpleadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empleados")
public class CrearEmpleadoController {
    private final CrearEmpleadoService service;

    public CrearEmpleadoController(CrearEmpleadoService service) {
        this.service = service;
    }

    public record CreateEmployeeRequest(
            int identificationNumber,
            String identificationTypeId,
            String role,
            String name,
            String contactNumber,
            String email,
            String password,
            java.math.BigDecimal salary,
            String hotel
    ) {}

    @PostMapping
    public ResponseEntity<String> create(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreateEmployeeRequest body
    ) {
        try {
            service.create(
                    authorization,
                    new CrearEmpleadoService.CreateCmd(
                            body.identificationNumber(),
                            body.identificationTypeId(),
                            body.role(),
                            body.name(),
                            body.contactNumber(),
                            body.email(),
                            body.password(),
                            body.salary(),
                            body.hotel()
                    )
            );
            return ResponseEntity.status(HttpStatus.CREATED).body("Empleado registrado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
