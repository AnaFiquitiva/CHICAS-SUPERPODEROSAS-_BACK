package eci.edu.dosw.proyecto.service;

import eci.edu.dosw.proyecto.model.SolicitudCambio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import eci.edu.dosw.proyecto.repository.SolicitudCambioRepository;
import java.util.List;

@Service
public class SolicitudCambioService {
    @Autowired
    private SolicitudCambioRepository solicitudCambioRepository;

    public List<SolicitudCambio> obtenerPendientes() {
        // Filtra por estado pendiente si lo necesitas
        return solicitudCambioRepository.findAll();
    }

    public SolicitudCambio guardar(SolicitudCambio solicitud) {
        return solicitudCambioRepository.save(solicitud);
    }

    public SolicitudCambio actualizar(SolicitudCambio solicitud) {
        return solicitudCambioRepository.save(solicitud);
    }
}
