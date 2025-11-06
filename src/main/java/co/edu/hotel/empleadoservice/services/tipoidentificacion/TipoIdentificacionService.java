package co.edu.hotel.empleadoservice.services.tipoidentificacion;

import co.edu.hotel.empleadoservice.domain.TipoIdentificacion;
import co.edu.hotel.empleadoservice.repository.TipoIdentificacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoIdentificacionService {

    private TipoIdentificacionRepository tipoIdentificacionRepository;
    public TipoIdentificacionService(TipoIdentificacionRepository tipoIdentificacionRepository) {
        this.tipoIdentificacionRepository = tipoIdentificacionRepository;
    }

    public List<TipoIdentificacion> getAll() {
        return tipoIdentificacionRepository.findAll();
    }
}

