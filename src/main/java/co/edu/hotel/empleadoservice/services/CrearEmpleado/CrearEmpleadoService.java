package co.edu.hotel.empleadoservice.services.CrearEmpleado;

import co.edu.hotel.empleadoservice.domain.Empleados;
import co.edu.hotel.empleadoservice.domain.TipoIdentificacion;
import co.edu.hotel.empleadoservice.repository.Empleado.EmpleadoRepository;
import co.edu.hotel.empleadoservice.repository.TipoDeIdentificacion.TipoDeIdentificacionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class CrearEmpleadoService {

    private final EmpleadoRepository employeeRepository;
    private final TipoDeIdentificacionRepository idTypeRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String CODE_PREFIX = "EMP-";
    private static final int CODE_DIGITS = 4; // EMP-0001

    public CrearEmpleadoService(EmpleadoRepository employeeRepository,
                                TipoDeIdentificacionRepository idTypeRepository) {
        this.employeeRepository = employeeRepository;
        this.idTypeRepository = idTypeRepository;
    }

    // simple command (sin DTO extra)
    public record CreateCmd(
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

    @Transactional
    public Empleados create(String authorizationHeader, CreateCmd cmd) {
        requireAdminRole(authorizationHeader);

        String createdByEmail = extractEmailFromToken(authorizationHeader);
        if (createdByEmail == null || createdByEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token sin email del creador");
        }

        String normalizedEmail = cmd.email().trim().toLowerCase();
        if (employeeRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (employeeRepository.existsByIdentificationNumber(cmd.identificationNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Identification number already exists");
        }

        var idTypeId = java.util.UUID.fromString(cmd.identificationTypeId());
        TipoIdentificacion idType = idTypeRepository.findById(idTypeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identification type not found"));

        String code = nextEmployeeCode();

        Empleados e = new Empleados();
        e.setCode(code);
        e.setIdentificationNumber(cmd.identificationNumber());
        e.setIdentificationType(idType);
        e.setRoles(cmd.role());
        e.setName(cmd.name());
        e.setContactNumber(cmd.contactNumber());
        e.setEmail(normalizedEmail);
        e.setPassword(passwordEncoder.encode(cmd.password()));
        e.setSalary(cmd.salary());
        e.setHotel(cmd.hotel());

        e.setRegisteredBy(createdByEmail);

        return employeeRepository.save(e);
    }

    private void requireAdminRole(String authorizationHeader) {
        String role = extractRoleFromToken(authorizationHeader);
        System.out.println("[AUTH] Detected role => " + role);
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only ADMIN can create employees (detected: " + role + ")");
        }
    }

    private String extractRoleFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Bearer token");
        }
        try {
            String token = authorizationHeader.substring(7).trim();
            String[] parts = token.split("\\.");
            if (parts.length < 2) throw new IllegalArgumentException("Invalid JWT");

            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]),
                    java.nio.charset.StandardCharsets.UTF_8);

            System.out.println("[AUTH] JWT payload: " + payloadJson);

            String upper = payloadJson.toUpperCase();
            String lower = payloadJson.toLowerCase();

            if (upper.contains("\"ADMIN\"")) return "ADMIN";
            if (upper.contains("ROLE_ADMIN")) return "ADMIN";
            if (lower.contains("1f674d6a-7978-42e3-87fb-af0740c9e8d2")) return "ADMIN";

            return "UNKNOWN";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot read JWT");
        }
    }

    private String extractEmailFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Bearer token");
        }
        try {
            String token = authorizationHeader.substring(7).trim();
            String[] parts = token.split("\\.");
            if (parts.length < 2) throw new IllegalArgumentException("Invalid JWT");

            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]),
                    java.nio.charset.StandardCharsets.UTF_8);

            var p = mapper.readTree(payloadJson);

            if (p.has("email") && p.get("email").isTextual()) return p.get("email").asText();
            if (p.has("sub") && p.get("sub").isTextual() && p.get("sub").asText().contains("@")) return p.get("sub").asText();
            if (p.has("username") && p.get("username").isTextual() && p.get("username").asText().contains("@")) return p.get("username").asText();
            if (p.has("preferred_username") && p.get("preferred_username").isTextual()
                    && p.get("preferred_username").asText().contains("@")) return p.get("preferred_username").asText();

            // fallback regex si el provider usa otra key
            var m = java.util.regex.Pattern
                    .compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", java.util.regex.Pattern.CASE_INSENSITIVE)
                    .matcher(payloadJson);
            if (m.find()) return m.group();

            return null;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot read JWT");
        }
    }

    private String nextEmployeeCode() {
        Optional<Empleados> last = employeeRepository
                .findFirstByCodeStartingWithOrderByCodeDesc(CODE_PREFIX);

        int n = 1;
        if (last.isPresent()) {
            String prev = last.get().getCode(); // e.g., EMP-0010
            String digits = prev.substring(CODE_PREFIX.length()).replaceAll("\\D", "");
            if (!digits.isEmpty()) n = Integer.parseInt(digits) + 1;
        }
        return CODE_PREFIX + String.format("%0" + CODE_DIGITS + "d", n);
    }
}
