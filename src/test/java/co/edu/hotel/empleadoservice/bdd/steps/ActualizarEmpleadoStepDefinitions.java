package co.edu.hotel.empleadoservice.bdd.steps;

import co.edu.hotel.empleadoservice.domain.Empleados;
import co.edu.hotel.empleadoservice.repository.Empleado.EmpleadoRepository;
import co.edu.hotel.empleadoservice.services.actualizarEmpleado.ActualizarEmpleadoService;
import io.cucumber.java.es.*;
import io.cucumber.datatable.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step Definitions para las pruebas BDD de Actualizar Empleado.
 * Implementa todos los pasos definidos en actualizar_empleado.feature
 */
@SpringBootTest
@ContextConfiguration
public class ActualizarEmpleadoStepDefinitions {

    @Autowired
    private ActualizarEmpleadoService service;

    @Autowired
    private EmpleadoRepository repository;

    // Variables de estado compartidas entre steps
    private Empleados empleadoInicial;
    private Empleados empleadoActualizado;
    private Exception thrownException;
    private String authorizationToken;
    private String empleadoId;
    private String empleadoCode;
    private ActualizarEmpleadoService.UpdateCmd updateCmd;

    // ROLES UUID - Lista proporcionada
    private static final String RECEPTIONIST = "2bed922b-bc39-4842-8297-4a3d9be34856";
    private static final String ADMIN = "1f674d6a-7978-42e3-87fb-af0740c9e8d2";
    private static final String CUSTOMER = "878c5024-f10c-4d35-a8a1-b78332344e54";
    private static final String STAFF = "a1541ebd-1323-43a7-8d16-30309bb6ade9";

    // ========================================
    // GIVEN - Preparación del contexto
    // ========================================

    @Dado("que existe un empleado {string} con:")
    public void queExisteUnEmpleadoCon(String nombre, DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        empleadoInicial = new Empleados();
        empleadoInicial.setId(UUID.fromString(data.get("id")));
        empleadoInicial.setCode(data.get("code"));
        empleadoInicial.setName(nombre);
        empleadoInicial.setRoles(data.get("roles"));
        empleadoInicial.setSalary(new BigDecimal(data.get("salary")));
        empleadoInicial.setContactNumber(data.get("contactNumber"));
        empleadoInicial.setRegistrationDate(LocalDate.now().minusDays(30));
        empleadoInicial.setRegisteredBy("sistema");

        repository.save(empleadoInicial);
        empleadoId = data.get("id");
        empleadoCode = data.get("code");
    }

    @Dado("que existe un empleado con id {string} y cargo {string}")
    public void queExisteUnEmpleadoConIdYCargo(String id, String cargoNombre) {
        empleadoId = id;

        // Mapear nombre de cargo a UUID
        String roleUuid = switch (cargoNombre.toUpperCase()) {
            case "RECEPTIONIST" -> RECEPTIONIST;
            case "ADMIN" -> ADMIN;
            case "STAFF" -> STAFF;
            case "CUSTOMER" -> CUSTOMER;
            default -> RECEPTIONIST;
        };

        empleadoInicial = new Empleados();
        empleadoInicial.setId(UUID.fromString(id));
        empleadoInicial.setCode("EMP-" + id.substring(0, 8));
        empleadoInicial.setName("Empleado Test");
        empleadoInicial.setRoles(roleUuid);
        empleadoInicial.setSalary(new BigDecimal("1500000"));
        empleadoInicial.setContactNumber("3001111111");
        empleadoInicial.setRegistrationDate(LocalDate.now().minusDays(15));
        empleadoInicial.setRegisteredBy("sistema");

        repository.save(empleadoInicial);
    }

    @Dado("que NO existe un empleado con id {string}")
    public void queNOExisteUnEmpleadoConId(String id) {
        empleadoId = id;
        try {
            repository.deleteById(UUID.fromString(id));
        } catch (Exception e) {
            // Ignorar si no existe
        }
    }

    @Dado("que NO existe un empleado con código {string}")
    public void queNOExisteUnEmpleadoConCodigo(String code) {
        empleadoCode = code;
        repository.findByCode(code).ifPresent(emp -> repository.deleteById(emp.getId()));
    }

    @Dado("el usuario tiene un token JWT de administrador con email {string}")
    public void elUsuarioTieneUnTokenJWTDeAdministradorConEmail(String email) {
        authorizationToken = crearTokenJWT(email, ADMIN);
    }

    @Dado("el usuario tiene un token JWT de STAFF con email {string}")
    public void elUsuarioTieneUnTokenJWTDeSTAFFConEmail(String email) {
        authorizationToken = crearTokenJWT(email, STAFF);
    }

    @Dado("el usuario NO tiene un token de autorización")
    public void elUsuarioNOTieneUnTokenDeAutorizacion() {
        authorizationToken = null;
    }

    @Dado("el usuario tiene un token JWT inválido")
    public void elUsuarioTieneUnTokenJWTInvalido() {
        authorizationToken = "Bearer token.invalido.aqui";
    }

    // ========================================
    // WHEN - Acciones/Ejecución
    // ========================================

    @Cuando("el supervisor cambia los datos del empleado por ID:")
    public void elSupervisorCambiaLosDatosDelEmpleadoPorID(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        try {
            updateCmd = new ActualizarEmpleadoService.UpdateCmd(
                    data.get("name"),
                    data.get("contactNumber"),
                    data.get("role"),
                    new BigDecimal(data.get("salary"))
            );

            empleadoActualizado = service.updateById(empleadoId, authorizationToken, updateCmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Cuando("el supervisor cambia los datos del empleado por código {string}:")
    public void elSupervisorCambiaLosDatosDelEmpleadoPorCodigo(String code, DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        try {
            updateCmd = new ActualizarEmpleadoService.UpdateCmd(
                    data.get("name"),
                    data.get("contactNumber"),
                    data.get("role"),
                    new BigDecimal(data.get("salary"))
            );

            empleadoActualizado = service.updateByCode(code, authorizationToken, updateCmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Cuando("el supervisor cambia solo el cargo a {string} por ID")
    public void elSupervisorCambiaSoloElCargoAPorID(String nuevoRol) {
        try {
            updateCmd = new ActualizarEmpleadoService.UpdateCmd(
                    null,
                    null,
                    nuevoRol,
                    null
            );

            empleadoActualizado = service.updateById(empleadoId, authorizationToken, updateCmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Cuando("el supervisor intenta cambiar el cargo a {string} por ID")
    public void elSupervisorIntentaCambiarElCargoAPorID(String nuevoRol) {
        try {
            updateCmd = new ActualizarEmpleadoService.UpdateCmd(
                    null,
                    null,
                    nuevoRol,
                    null
            );

            empleadoActualizado = service.updateById(empleadoId, authorizationToken, updateCmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Cuando("el supervisor intenta cambiar el cargo por código {string}")
    public void elSupervisorIntentaCambiarElCargoPorCodigo(String code) {
        try {
            updateCmd = new ActualizarEmpleadoService.UpdateCmd(
                    null,
                    null,
                    STAFF,
                    null
            );

            empleadoActualizado = service.updateByCode(code, authorizationToken, updateCmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Cuando("el usuario intenta cambiar el salario a {string}")
    public void elUsuarioIntentaCambiarElSalarioA(String nuevoSalario) {
        try {
            updateCmd = new ActualizarEmpleadoService.UpdateCmd(
                    null,
                    null,
                    null,
                    new BigDecimal(nuevoSalario)
            );

            empleadoActualizado = service.updateById(empleadoId, authorizationToken, updateCmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Cuando("el usuario intenta actualizar un empleado con ID inválido {string}")
    public void elUsuarioIntentaActualizarUnEmpleadoConIDInvalido(String idInvalido) {
        try {
            updateCmd = new ActualizarEmpleadoService.UpdateCmd(
                    null,
                    null,
                    STAFF,
                    null
            );

            empleadoActualizado = service.updateById(idInvalido, authorizationToken, updateCmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // ========================================
    // THEN - Verificaciones/Assertions
    // ========================================

    @Entonces("el sistema debe guardar los cambios exitosamente")
    public void elSistemaDebeGuardarLosCambiosExitosamente() {
        assertNull(thrownException, "No debería haber excepciones: " +
                (thrownException != null ? thrownException.getMessage() : ""));
        assertNotNull(empleadoActualizado, "El empleado actualizado no debe ser nulo");
    }

    @Entonces("el empleado debe tener el cargo actualizado a {string}")
    public void elEmpleadoDebeTenerElCargoActualizadoA(String expectedRol) {
        assertEquals(expectedRol, empleadoActualizado.getRoles());
    }

    @Entonces("el empleado debe tener el salario actualizado a {string}")
    public void elEmpleadoDebeTenerElSalarioActualizadoA(String expectedSalary) {
        assertEquals(new BigDecimal(expectedSalary), empleadoActualizado.getSalary());
    }

    @Entonces("el empleado debe tener el número de contacto actualizado a {string}")
    public void elEmpleadoDebeTenerElNumeroDeContactoActualizadoA(String expectedContact) {
        assertEquals(expectedContact, empleadoActualizado.getContactNumber());
    }

    @Entonces("debe registrar la fecha de actualización")
    public void debeRegistrarLaFechaDeActualizacion() {
        assertNotNull(empleadoActualizado.getRegistrationDate());
        assertEquals(LocalDate.now(), empleadoActualizado.getRegistrationDate());
    }

    @Entonces("debe registrar el responsable de la actualización como {string}")
    public void debeRegistrarElResponsableDeLaActualizacionComo(String expectedEmail) {
        assertEquals(expectedEmail, empleadoActualizado.getRegisteredBy());
    }

    @Entonces("debe mantener el salario anterior")
    public void debeMantenerElSalarioAnterior() {
        assertEquals(empleadoInicial.getSalary(), empleadoActualizado.getSalary());
    }

    @Entonces("debe mantener el número de contacto anterior")
    public void debeMantenerElNumeroDeContactoAnterior() {
        assertEquals(empleadoInicial.getContactNumber(), empleadoActualizado.getContactNumber());
    }

    @Entonces("el sistema debe retornar un error NOT_FOUND")
    public void elSistemaDebeRetornarUnErrorNOTFOUND() {
        assertNotNull(thrownException, "Debería haber una excepción");
        assertTrue(thrownException instanceof ResponseStatusException);
        ResponseStatusException rse = (ResponseStatusException) thrownException;
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, rse.getStatusCode());
    }

    @Entonces("el sistema debe retornar un error FORBIDDEN")
    public void elSistemaDebeRetornarUnErrorFORBIDDEN() {
        assertNotNull(thrownException, "Debería haber una excepción");
        assertTrue(thrownException instanceof ResponseStatusException);
        ResponseStatusException rse = (ResponseStatusException) thrownException;
        assertEquals(org.springframework.http.HttpStatus.FORBIDDEN, rse.getStatusCode());
    }

    @Entonces("el sistema debe retornar un error UNAUTHORIZED")
    public void elSistemaDebeRetornarUnErrorUNAUTHORIZED() {
        assertNotNull(thrownException, "Debería haber una excepción");
        assertTrue(thrownException instanceof ResponseStatusException);
        ResponseStatusException rse = (ResponseStatusException) thrownException;
        assertEquals(org.springframework.http.HttpStatus.UNAUTHORIZED, rse.getStatusCode());
    }

    @Entonces("el sistema debe retornar un error BAD_REQUEST")
    public void elSistemaDebeRetornarUnErrorBADREQUEST() {
        assertNotNull(thrownException, "Debería haber una excepción");
        assertTrue(thrownException instanceof ResponseStatusException);
        ResponseStatusException rse = (ResponseStatusException) thrownException;
        assertEquals(org.springframework.http.HttpStatus.BAD_REQUEST, rse.getStatusCode());
    }

    @Entonces("el mensaje debe indicar {string}")
    public void elMensajeDebeIndicar(String expectedMessage) {
        assertNotNull(thrownException);
        String actualMessage = thrownException.getMessage();
        assertTrue(actualMessage.contains(expectedMessage),
                "Se esperaba que el mensaje contuviera '" + expectedMessage +
                        "' pero fue: " + actualMessage);
    }

    // ========================================
    // MÉTODO AUXILIAR - Generación de JWT
    // ========================================

    /**
     * Crea un token JWT de prueba con estructura válida (header.payload.signature)
     * compatible con la lógica de extracción del servicio ActualizarEmpleadoService.
     *
     * @param email Email del usuario (extraído del token)
     * @param roleUuid UUID del rol (usado para validación de permisos)
     * @return Token JWT en formato "Bearer header.payload.signature"
     */
    private String crearTokenJWT(String email, String roleUuid) {
        // Header estándar JWT
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(header.getBytes(StandardCharsets.UTF_8));

        // Payload con email y role UUID
        String payload = String.format(
                "{\"email\":\"%s\",\"role\":\"%s\",\"sub\":\"%s\"}",
                email, roleUuid, email
        );
        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));

        // Signature simplificada para testing (no se valida en el servicio)
        String signature = "test-signature-for-bdd-testing";
        String encodedSignature = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(signature.getBytes(StandardCharsets.UTF_8));

        return "Bearer " + encodedHeader + "." + encodedPayload + "." + encodedSignature;
    }
}