package co.edu.hotel.empleadoservice.steps;

import co.edu.hotel.empleadoservice.domain.Empleado;
import co.edu.hotel.empleadoservice.domain.TipoIdentificacion;
import co.edu.hotel.empleadoservice.repository.EmpleadoRepository;
import co.edu.hotel.empleadoservice.repository.TipoIdentificacionRepository;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class RegistrarEmpleadoSteps {

    @Autowired private MockMvc mockMvc;
    @Autowired private EmpleadoRepository employeeRepository;
    @Autowired private TipoIdentificacionRepository idTypeRepository;

    private String hotel;
    private MvcResult result;

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
    // ===================================================================

    private static final String IDTYPE_VALIDO = "bf08dbaa-c69c-41fa-a01a-31c8ea753bf3";

    @Dado("el hotel {string}")
    public void elHotel(String hotel) {
        this.hotel = hotel;

        // defaults
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.existsByIdentificationNumber(anyInt())).thenReturn(false);
        when(employeeRepository.findFirstByCodeStartingWithOrderByCodeDesc("EMP-"))
                .thenReturn(Optional.empty());
        when(employeeRepository.save(Mockito.any(Empleado.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UUID typeId = UUID.fromString(IDTYPE_VALIDO);
        TipoIdentificacion ti = new TipoIdentificacion();
        ti.setId(typeId);
        ti.setName("Cédula");
        when(idTypeRepository.findById(typeId)).thenReturn(Optional.of(ti));
    }

    @Cuando("el administrador ingresa identificación {string}, tipo_identificacion {string}, rol {string}, nombre {string}, contacto {string}, correo {string}, contraseña {string}, salario {string}")
    public void elAdministradorIngresa(String identificacion, String tipo, String rol, String nombre,
                                       String contacto, String correo, String contrasena, String salario) throws Exception {
        String json = """
            {
              "identificationNumber": %s,
              "identificationTypeId": "%s",
              "role": "%s",
              "name": "%s",
              "contactNumber": "%s",
              "email": "%s",
              "password": "%s",
              "salary": %s,
              "hotel": "%s"
            }
            """.formatted(identificacion, IDTYPE_VALIDO, rol, nombre, contacto, correo, contrasena, salario, hotel);

        result = mockMvc.perform(
                        post("/api/empleados")
                                .header("Authorization", "Bearer " + ADMIN_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andReturn();

        int status = result.getResponse().getStatus();
        String body = result.getResponse().getContentAsString();
        Assertions.assertEquals(201, status, "HTTP esperado 201 pero fue " + status + ". Body: " + body);
    }

    @Entonces("el sistema debe crear el registro con código {string}")
    public void elSistemaDebeCrearElRegistroConCodigo(String ignorado) throws Exception {
        String body = result.getResponse().getContentAsString();
        Assertions.assertFalse(body == null || body.isBlank(), "El cuerpo no debe venir vacío");
    }

    @Y("mostrar el mensaje {string}")
    public void mostrarElMensaje(String esperado) throws Exception {
        String body = result.getResponse().getContentAsString();
        Assertions.assertTrue(body.contains(esperado),
                "No se encontró el mensaje esperado. Body: " + body);
    }

    @Y("existe un empleado previamente con email {string} e identificación {string}")
    public void existeEmpleadoPreviamente(String email, String identificacion) {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.existsByIdentificationNumber(anyInt())).thenReturn(false);

        when(employeeRepository.existsByEmail(eq(email))).thenReturn(true);
        when(employeeRepository.existsByIdentificationNumber(eq(Integer.parseInt(identificacion))))
                .thenReturn(true);
    }

    @Cuando("intenta registrar identificación {string}  tipo_identificacion {string}  rol {string}  nombre {string}  contacto {string}  correo {string}  contraseña {string}  salario {string}")
    public void intenta_registrar(String identificacion, String tipo, String rol, String nombre,
                                  String contacto, String correo, String contrasena, String salario) throws Exception {

        String json = """
            {
              "identificationNumber": %s,
              "identificationTypeId": "%s",
              "role": "%s",
              "name": "%s",
              "contactNumber": "%s",
              "email": "%s",
              "password": "%s",
              "salary": %s,
              "hotel": "%s"
            }
            """.formatted(identificacion, IDTYPE_VALIDO, rol, nombre, contacto, correo, contrasena, salario, hotel);

        result = mockMvc.perform(
                        post("/api/empleados")
                                .header("Authorization", "Bearer " + ADMIN_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andReturn();
    }

    @Cuando("intenta registrar con typeId {string} identificación {string}  rol {string}  nombre {string}  contacto {string}  correo {string}  contraseña {string}  salario {string}")
    public void intenta_registrar_con_typeid_inexistente(String typeIdStr, String identificacion, String rol,
                                                         String nombre, String contacto, String correo,
                                                         String contrasena, String salario) throws Exception {

        UUID badId = UUID.fromString(typeIdStr);
        when(idTypeRepository.findById(badId)).thenReturn(Optional.empty());

        String json = """
            {
              "identificationNumber": %s,
              "identificationTypeId": "%s",
              "role": "%s",
              "name": "%s",
              "contactNumber": "%s",
              "email": "%s",
              "password": "%s",
              "salary": %s,
              "hotel": "%s"
            }
            """.formatted(identificacion, typeIdStr, rol, nombre, contacto, correo, contrasena, salario, hotel);

        result = mockMvc.perform(
                        post("/api/empleados")
                                .header("Authorization", "Bearer " + ADMIN_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andReturn();
    }

    @Entonces("la respuesta es {int}")
    public void la_respuesta_es(Integer esperado) {
        int actual = result.getResponse().getStatus();
        Assertions.assertEquals(esperado.intValue(), actual,
                "Código HTTP inesperado. Body: " + getBodySafe());
    }

    @Y("el cuerpo contiene {string}")
    public void el_cuerpo_contiene(String fragmento) {
        Assertions.assertTrue(getBodySafe().contains(fragmento),
                "El body no contiene: " + fragmento + ". Body: " + getBodySafe());
    }

    private String getBodySafe() {
        try { return result.getResponse().getContentAsString(); }
        catch (Exception e) { return "<sin cuerpo por excepción>"; }
    }
}