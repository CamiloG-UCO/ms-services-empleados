package co.edu.hotel.empleadoservice.runner;

import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/delete.feature")
@ConfigurationParameter(
        key = io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME,
        value = "co.edu.hotel.empleadoservice.steps,co.edu.hotel.empleadoservice.config"
)
public class DeleteTestRunner { }
