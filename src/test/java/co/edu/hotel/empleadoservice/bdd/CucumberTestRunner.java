package co.edu.hotel.empleadoservice.bdd;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FEATURES_PROPERTY_NAME;

/**
 * Runner principal para ejecutar todas las pruebas BDD con Cucumber.
 *
 * IMPORTANTE: El GLUE debe apuntar al paquete base 'co.edu.hotel.empleadoservice.bdd'
 * para que Cucumber encuentre tanto CucumberSpringConfiguration como los steps.
 *
 * Ejecutar con:
 * - Maven: mvn test
 * - IDE: Run as JUnit Test
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
        key = FEATURES_PROPERTY_NAME,
        value = "classpath:features"
)
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "co.edu.hotel.empleadoservice.bdd"
)
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-reports/cucumber.html, json:target/cucumber-reports/cucumber.json"
)
public class CucumberTestRunner {
    // Esta clase no necesita código, solo las anotaciones
}