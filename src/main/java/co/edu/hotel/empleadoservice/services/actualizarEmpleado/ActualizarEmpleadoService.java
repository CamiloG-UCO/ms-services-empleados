package co.edu.hotel.empleadoservice.services.actualizarEmpleado;

import co.edu.hotel.empleadoservice.domain.Empleados;
import co.edu.hotel.empleadoservice.repository.Empleado.EmpleadoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class ActualizarEmpleadoService {

    private final EmpleadoRepository employeeRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public ActualizarEmpleadoService(EmpleadoRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public record UpdateCmd(
            String name,
            String contactNumber,
            String role,
            java.math.BigDecimal salary
    ) {}

    @Transactional
    public Empleados updateById(String id, String authorizationHeader, UpdateCmd cmd) {
        requireAdminRole(authorizationHeader);

        String updatedByEmail = extractEmailFromToken(authorizationHeader);
        if (updatedByEmail == null || updatedByEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token sin email del actualizador");
        }

        UUID employeeId;
        try {
            employeeId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido");
        }

        Optional<Empleados> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado");
        }

        Empleados e = optionalEmployee.get();
        return updateEmployeeFields(e, cmd, updatedByEmail);
    }

    @Transactional
    public Empleados updateByCode(String code, String authorizationHeader, UpdateCmd cmd) {
        requireAdminRole(authorizationHeader);

        String updatedByEmail = extractEmailFromToken(authorizationHeader);
        if (updatedByEmail == null || updatedByEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token sin email del actualizador");
        }

        Empleados e = employeeRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado con código: " + code));

        return updateEmployeeFields(e, cmd, updatedByEmail);
    }

    private Empleados updateEmployeeFields(Empleados e, UpdateCmd cmd, String updatedByEmail) {
        if (cmd.name() != null && !cmd.name().isBlank()) e.setName(cmd.name().trim());
        if (cmd.contactNumber() != null && !cmd.contactNumber().isBlank()) e.setContactNumber(cmd.contactNumber().trim());
        if (cmd.role() != null && !cmd.role().isBlank()) e.setRoles(cmd.role().trim());
        if (cmd.salary() != null) e.setSalary(cmd.salary());

        e.setRegisteredBy(updatedByEmail);
        e.setRegistrationDate(LocalDate.now());

        return employeeRepository.save(e);
    }

    private void requireAdminRole(String authorizationHeader) {
        String role = extractRoleFromToken(authorizationHeader);
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo un ADMIN puede actualizar empleados");
        }
    }

    private String extractRoleFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falta token Bearer");
        }
        try {
            String token = authorizationHeader.substring(7).trim();
            String[] parts = token.split("\\.");
            if (parts.length < 2) throw new IllegalArgumentException("JWT inválido");

            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]),
                    java.nio.charset.StandardCharsets.UTF_8);

            String upper = payloadJson.toUpperCase();
            String lower = payloadJson.toLowerCase();

            if (upper.contains("\"ADMIN\"")) return "ADMIN";
            if (upper.contains("ROLE_ADMIN")) return "ADMIN";
            if (lower.contains("1f674d6a-7978-42e3-87fb-af0740c9e8d2")) return "ADMIN";

            return "UNKNOWN";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede leer el JWT");
        }
    }

    private String extractEmailFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falta token Bearer");
        }
        try {
            String token = authorizationHeader.substring(7).trim();
            String[] parts = token.split("\\.");
            if (parts.length < 2) throw new IllegalArgumentException("JWT inválido");

            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]),
                    java.nio.charset.StandardCharsets.UTF_8);

            var p = mapper.readTree(payloadJson);

            if (p.has("email") && p.get("email").isTextual()) return p.get("email").asText();
            if (p.has("sub") && p.get("sub").isTextual() && p.get("sub").asText().contains("@")) return p.get("sub").asText();
            if (p.has("username") && p.get("username").isTextual() && p.get("username").asText().contains("@")) return p.get("username").asText();
            if (p.has("preferred_username") && p.get("preferred_username").isTextual()
                    && p.get("preferred_username").asText().contains("@")) return p.get("preferred_username").asText();

            var m = java.util.regex.Pattern
                    .compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", java.util.regex.Pattern.CASE_INSENSITIVE)
                    .matcher(payloadJson);
            if (m.find()) return m.group();

            return null;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede leer el JWT");
        }
    }
}
