package co.edu.hotel.empleadoservice.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Configuración de integración entre Cucumber y Spring Boot.
 *
 * Esta clase es ESENCIAL para que Cucumber pueda:
 * 1. Cargar el contexto de Spring Boot completo
 * 2. Permitir inyección de dependencias (@Autowired) en los Step Definitions
 * 3. Acceder a repositorios, servicios y componentes de Spring
 *
 * Características:
 * - @CucumberContextConfiguration: Indica a Cucumber que esta es la configuración de Spring
 * - @SpringBootTest: Levanta el contexto completo de Spring Boot para testing
 * - @ActiveProfiles("test"): Usa el perfil 'test' (application-test.properties)
 * - WebEnvironment.RANDOM_PORT: Levanta el servidor en un puerto aleatorio para evitar conflictos
 *
 * IMPORTANTE:
 * - Solo debe existir UNA clase con @CucumberContextConfiguration por proyecto
 * - Se comparte entre todos los features y step definitions
 * - No necesita código adicional, solo las anotaciones
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    /**
     * Esta clase actúa como puente entre Cucumber y Spring Boot.
     * No necesita métodos ni lógica adicional.
     *
     * Las anotaciones hacen todo el trabajo:
     * - Cucumber detecta esta configuración automáticamente
     * - Spring Boot levanta el contexto de aplicación
     * - Los @Autowired en los Step Definitions funcionan correctamente
     */

    // Opcionalmente, puedes agregar hooks de Cucumber aquí si los necesitas:

    // @Before
    // public void beforeScenario() {
    //     // Se ejecuta antes de cada escenario
    //     System.out.println("Iniciando escenario...");
    // }

    // @After
    // public void afterScenario() {
    //     // Se ejecuta después de cada escenario
    //     System.out.println("Finalizando escenario...");
    // }

    // @BeforeAll
    // public static void beforeAllScenarios() {
    //     // Se ejecuta una vez antes de todos los escenarios
    //     System.out.println("Iniciando suite de pruebas BDD...");
    // }

    // @AfterAll
    // public static void afterAllScenarios() {
    //     // Se ejecuta una vez después de todos los escenarios
    //     System.out.println("Finalizando suite de pruebas BDD...");
    // }
}