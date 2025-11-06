package co.edu.hotel.empleadoservice.steps;

import co.edu.hotel.empleadoservice.domain.Empleado;
import co.edu.hotel.empleadoservice.repository.EmpleadoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.es.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

public class DeleteEmpleadoSteps {

    @Autowired private MockMvc mockMvc;
    @Autowired private EmpleadoRepository employeeRepository;

    private MvcResult result;
    private Empleado empleado;

    private static final String AUTH_URL =
            "https://ms-services-users.up.railway.app/api/v1/auth/login";

    private static final String ADMIN_EMAIL =
            System.getProperty("TEST_ADMIN_EMAIL", "simoncardenasramirez41@gmail.com");
    private static final String ADMIN_PASS =
            System.getProperty("TEST_ADMIN_PASS", "abcd1234");

    private static final String ADMIN_TOKEN = obtenerTokenDesdeAuth();

    private static String obtenerTokenDesdeAuth() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String bodyJson = """
                {"email":"%s","password":"%s"}
                """.formatted(ADMIN_EMAIL, ADMIN_PASS);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AUTH_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                    .build();

            HttpResponse<String> resp =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() / 100 != 2) {
                throw new IllegalStateException(
                        "Login Auth falló. HTTP " + resp.statusCode() + " Body: " + resp.body());
            }

            ObjectMapper mapper = new ObjectMapper();
            String token = mapper.readTree(resp.body()).path("token").asText(null);
            if (token == null || token.isBlank()) {
                throw new IllegalStateException("Login Auth no devolvió 'token'. Body: " + resp.body());
            }
            return token;
        } catch (Exception e) {
            throw new RuntimeException("No se pudo obtener token dinámico de Auth", e);
        }
    }

    // ==================================================================
    @Dado("el empleado {string} sin tareas ni turnos activos")
    public void elEmpleadoSinTareasNiTurnosActivos(String codigoEmpleado) {
        // 1. Creamos un empleado simulado
        empleado = new Empleado();
        empleado.setId(UUID.randomUUID());
        empleado.setCode(codigoEmpleado);
        empleado.setName("Empleado Prueba");
        empleado.setEmail("empleado@prueba.com");
        empleado.setContactNumber("3001234567");
        empleado.setHotel("Hotel Test");
        empleado.setSalary(new java.math.BigDecimal("1800000"));

        // 2. Aceptamos cualquier UUID para findById()
        when(employeeRepository.findById(Mockito.any(UUID.class)))
                .thenReturn(Optional.of(empleado));

        // 3. Simulamos que la eliminación se realiza sin lanzar errores
        Mockito.doNothing().when(employeeRepository).delete(Mockito.any(Empleado.class));

        System.out.println("Empleado simulado creado con ID: " + empleado.getId());
    }

    @Cuando("el usuario de RRHH confirme la acción con {string}")
    public void elUsuarioConfirmaLaAccionCon(String confirmacion) throws Exception {
        if (!confirmacion.equalsIgnoreCase("SI, ELIMINAR")) {
            throw new IllegalArgumentException("La confirmación debe ser 'SI, ELIMINAR'");
        }

        result = mockMvc.perform(
                        delete("/api/empleados/" + empleado.getId())
                                .header("Authorization", "Bearer " + ADMIN_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Entonces("el sistema debe eliminar el registro {string} y mostrar el mensaje {string}")
    public void elSistemaDebeEliminarElRegistroYMostrarElMensaje(String codigoEsperado, String mensajeEsperado) throws Exception {
        int status = result.getResponse().getStatus();
        String body = result.getResponse().getContentAsString();

        Assertions.assertEquals(200, status,
                "Se esperaba HTTP 200, pero fue " + status + ". Body: " + body);
        Assertions.assertTrue(body.contains(mensajeEsperado),
                "El cuerpo no contiene el mensaje esperado. Body: " + body);
    }
}
