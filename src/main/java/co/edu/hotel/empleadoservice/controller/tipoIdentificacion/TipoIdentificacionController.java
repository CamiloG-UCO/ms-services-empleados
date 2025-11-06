package co.edu.hotel.empleadoservice.controller.tipoIdentificacion;

import co.edu.hotel.empleadoservice.domain.TipoIdentificacion;
import co.edu.hotel.empleadoservice.services.tipoidentificacion.TipoIdentificacionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tipoIdentificacion")
public class TipoIdentificacionController {

    private final TipoIdentificacionService tipoIdentificacionService;

    public TipoIdentificacionController(TipoIdentificacionService tipoIdentificacionService) {
        this.tipoIdentificacionService = tipoIdentificacionService;
    }

    @GetMapping
    public List<TipoIdentificacion> getAllTipoIdentificacion() {
        return tipoIdentificacionService.getAll();
    }
}
