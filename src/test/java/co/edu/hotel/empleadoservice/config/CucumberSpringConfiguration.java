package co.edu.hotel.empleadoservice.config;

import co.edu.hotel.empleadoservice.repository.EmpleadoRepository;
import co.edu.hotel.empleadoservice.repository.TipoIdentificacionRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    @MockitoBean
    private EmpleadoRepository employeeRepository;

    @MockitoBean
    private TipoIdentificacionRepository idTypeRepository;
}
