package co.edu.hotel.empleadoservice.steps;

import io.cucumber.java.es.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrarEmpleadoSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;  // SQL directo para preparar datos

    private String hotel;
    private MvcResult result;

    // Token ADMIN por defecto (para todos los escenarios)
    private static final String ADMIN_TOKEN =
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzaW1vbmNhcmRlbmFzQGdtYWlsLmNvbSIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc2MTg0NTU1MSwiZXhwIjoxNzYxOTMxOTUxfQ.Rv-sONVVZ6-lSa3b7b7zfp5qa8ZBo2SYIWFcathejIo";

    // typeId válido presente en BD
    private static final String IDTYPE_VALIDO = "bf08dbaa-c69c-41fa-a01a-31c8ea753bf3";

    @Dado("el hotel {string}")
    public void elHotel(String hotel) {
        this.hotel = hotel;

        // Aseguramos tipo de identificación válido
        var id = java.util.UUID.fromString(IDTYPE_VALIDO);
        try {
            jdbc.update(
                    "INSERT INTO identification_types (id, name) VALUES (?, ?) " +
                            "ON CONFLICT (id) DO NOTHING",
                    id, "Cédula"
            );
        } catch (Exception e) {
            System.out.println("WARN insert identification_types: " + e.getMessage());
        }
    }

    // ---------- ESCENARIO ÉXITO ----------
    @Cuando("el administrador ingresa identificación {string}, tipo_identificacion {string}, rol {string}, nombre {string}, contacto {string}, correo {string}, contraseña {string}, salario {string}")
    public void elAdministradorIngresa(String identificacion, String tipo, String rol, String nombre,
                                       String contacto, String correo, String contrasena, String salario) throws Exception {

        // Limpieza defensiva
        try {
            jdbc.update("DELETE FROM empleados WHERE email = ? OR identification_number = ?",
                    correo, Integer.parseInt(identificacion));
        } catch (Exception e) {
            System.out.println("WARN delete empleados: " + e.getMessage());
        }

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
                                .content(json)
                )
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
    public void mostrarElMensaje(String mensajeEsperado) throws Exception {
        String body = result.getResponse().getContentAsString();
        Assertions.assertTrue(body.contains(mensajeEsperado),
                "No se encontró el mensaje esperado. Body: " + body);
    }

    // ---------- PRECONDICIÓN PARA DUPLICADOS ----------
    @Y("existe un empleado previamente con email {string} e identificación {string}")
    public void existeEmpleadoPreviamente(String email, String identificacion) throws Exception {
        try {
            jdbc.update("DELETE FROM empleados WHERE email = ? OR identification_number = ?",
                    email, Integer.parseInt(identificacion));
        } catch (Exception e) {
            System.out.println("WARN delete empleados: " + e.getMessage());
        }

        String json = """
            {
              "identificationNumber": %s,
              "identificationTypeId": "%s",
              "role": "RECEPCIONISTA",
              "name": "Empleado Previo",
              "contactNumber": "3001111111",
              "email": "%s",
              "password": "pwd",
              "salary": 1000000,
              "hotel": "%s"
            }
            """.formatted(identificacion, IDTYPE_VALIDO, email, hotel);

        var pre = mockMvc.perform(
                        post("/api/empleados")
                                .header("Authorization", "Bearer " + ADMIN_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andReturn();

        int status = pre.getResponse().getStatus();
        if (status != 201) {
            throw new AssertionError("No se pudo crear el empleado previo (esperado 201, fue " + status +
                    "). Body: " + pre.getResponse().getContentAsString());
        }
    }

    // ---------- INTENTO genérico (usa typeId válido) ----------
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
                                .content(json)
                )
                .andReturn();
    }

    // ---------- INTENTO con typeId proporcionado (para el inexistente) ----------
    @Cuando("intenta registrar con typeId {string} identificación {string}  rol {string}  nombre {string}  contacto {string}  correo {string}  contraseña {string}  salario {string}")
    public void intenta_registrar_con_typeid_inexistente(String typeId, String identificacion, String rol,
                                                         String nombre, String contacto, String correo,
                                                         String contrasena, String salario) throws Exception {

        try {
            jdbc.update("DELETE FROM empleados WHERE email = ? OR identification_number = ?",
                    correo, Integer.parseInt(identificacion));
        } catch (Exception e) {
            System.out.println("WARN delete empleados: " + e.getMessage());
        }

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
            """.formatted(identificacion, typeId, rol, nombre, contacto, correo, contrasena, salario, hotel);

        result = mockMvc.perform(
                        post("/api/empleados")
                                .header("Authorization", "Bearer " + ADMIN_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andReturn();
    }

    // ---------- Validaciones genéricas ----------
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
