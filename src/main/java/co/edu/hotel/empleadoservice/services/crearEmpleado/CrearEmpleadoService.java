package co.edu.hotel.empleadoservice.services.crearEmpleado;

import co.edu.hotel.empleadoservice.domain.Empleado;
import co.edu.hotel.empleadoservice.domain.TipoIdentificacion;
import co.edu.hotel.empleadoservice.repository.EmpleadoRepository;
import co.edu.hotel.empleadoservice.repository.TipoIdentificacionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CrearEmpleadoService {

    private final EmpleadoRepository employeeRepository;
    private final TipoIdentificacionRepository idTypeRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String CODE_PREFIX = "EMP-";
    private static final int CODE_DIGITS = 4; // EMP-0001

    public CrearEmpleadoService(EmpleadoRepository employeeRepository,
                                TipoIdentificacionRepository idTypeRepository) {
        this.employeeRepository = employeeRepository;
        this.idTypeRepository = idTypeRepository;
    }

    /** Comando de entrada para crear empleado **/
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
    public void create(String authorizationHeader, CreateCmd cmd) {
        requireAdminRole(authorizationHeader);

        String createdByEmail = extractEmailFromToken(authorizationHeader);
        if (createdByEmail == null || createdByEmail.isBlank()) {
            throw new IllegalStateException("Token sin email del creador");
        }

        String normalizedEmail = cmd.email().trim().toLowerCase();

        // ---- Validaciones de negocio ----
        if (employeeRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalStateException("El email ya existe");
        }

        if (employeeRepository.existsByIdentificationNumber(cmd.identificationNumber())) {
            throw new IllegalStateException("Número de identificación ya existente");
        }

        var idTypeId = java.util.UUID.fromString(cmd.identificationTypeId());
        TipoIdentificacion idType = idTypeRepository.findById(idTypeId)
                .orElseThrow(() -> new NoSuchElementException("Tipo de identificación no encontrado"));

        String code = nextEmployeeCode();

        // ---- Creación del empleado ----
        Empleado e = new Empleado();
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

        employeeRepository.save(e);
    }

    // ----------- Helpers internos -----------

    /** Solo ADMIN puede crear empleados **/
    private void requireAdminRole(String authorizationHeader) {
        String role = extractRoleFromToken(authorizationHeader);
        System.out.println("[AUTH] Detected role => " + role);
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new AccessDeniedException("Solamente el admin puede crear empleados (detectado: " + role + ")");
        }
    }

    /** Extrae el rol desde el JWT **/
    private String extractRoleFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("No hay token");
        }
        try {
            String token = authorizationHeader.substring(7).trim();
            String[] parts = token.split("\\.");
            if (parts.length < 2) throw new IllegalArgumentException("JWT inválido");

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
            throw new IllegalArgumentException("No se pudo leer el JWT");
        }
    }

    /** Extrae el email del token **/
    private String extractEmailFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("No hay token");
        }
        try {
            String token = authorizationHeader.substring(7).trim();
            String[] parts = token.split("\\.");
            if (parts.length < 2) throw new IllegalArgumentException("JWT inválido");

            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]),
                    java.nio.charset.StandardCharsets.UTF_8);

            var p = mapper.readTree(payloadJson);

            if (p.has("email") && p.get("email").isTextual()) return p.get("email").asText();
            if (p.has("sub") && p.get("sub").isTextual() && p.get("sub").asText().contains("@"))
                return p.get("sub").asText();
            if (p.has("username") && p.get("username").isTextual() && p.get("username").asText().contains("@"))
                return p.get("username").asText();
            if (p.has("preferred_username") && p.get("preferred_username").isTextual()
                    && p.get("preferred_username").asText().contains("@"))
                return p.get("preferred_username").asText();

            var m = java.util.regex.Pattern
                    .compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", java.util.regex.Pattern.CASE_INSENSITIVE)
                    .matcher(payloadJson);
            if (m.find()) return m.group();

            return null;
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo leer el JWT");
        }
    }

    /** Genera siguiente código EMP-XXXX **/
    private String nextEmployeeCode() {
        Optional<Empleado> last = employeeRepository
                .findFirstByCodeStartingWithOrderByCodeDesc(CODE_PREFIX);

        int n = 1;
        if (last.isPresent()) {
            String prev = last.get().getCode();
            String digits = prev.substring(CODE_PREFIX.length()).replaceAll("\\D", "");
            if (!digits.isEmpty()) n = Integer.parseInt(digits) + 1;
        }
        return CODE_PREFIX + String.format("%0" + CODE_DIGITS + "d", n);
    }
}
