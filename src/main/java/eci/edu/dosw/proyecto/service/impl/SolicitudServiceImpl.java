package eci.edu.dosw.proyecto.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.SolicitudService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;

    @Override
    public Solicitud crearSolicitud(Solicitud solicitud) {
        if (!solicitud.isValid()) {
            throw new IllegalArgumentException("Solicitud no válida: " + solicitud.getValidationErrors());
        }

        // Validar que el estudiante no tenga solicitudes activas para la misma materia
        List<Solicitud> solicitudesActivas = solicitudRepository.findSolicitudesActivasByEstudiante(solicitud.getEstudiante().getId());
        boolean existeSolicitudActiva = solicitudesActivas.stream()
                .anyMatch(s -> s.getMateriaDestino().getId().equals(solicitud.getMateriaDestino().getId()));

        if (existeSolicitudActiva) {
            throw new IllegalArgumentException("El estudiante ya tiene una solicitud activa para la materia destino.");
        }

        return solicitudRepository.save(solicitud);
    }

    @Override
    public Optional<Solicitud> obtenerSolicitudPorId(String id) {
        return solicitudRepository.findById(id);
    }

    @Override
    public List<Solicitud> obtenerSolicitudesPorEstudiante(String estudianteId) {
        return solicitudRepository.findByEstudianteId(estudianteId);
    }

    @Override
    public List<Solicitud> obtenerSolicitudesPorDecano(String decanoId) {
        return solicitudRepository.findByDecanoAsignadoId(decanoId);
    }

    @Override
    public List<Solicitud> obtenerSolicitudesPorEstado(EstadoSolicitud estado) {
        return solicitudRepository.findByEstado(estado);
    }

    @Override
    public List<Solicitud> obtenerSolicitudesPendientes() {
        return solicitudRepository.findSolicitudesPendientes();
    }

    @Override
    public List<Solicitud> obtenerTodasLasSolicitudes() {
        return solicitudRepository.findAll();
    }

    @Override
    public Solicitud actualizarSolicitud(Solicitud solicitud) {
        if (!solicitud.isValid()) {
            throw new IllegalArgumentException("Solicitud no válida: " + solicitud.getValidationErrors());
        }

        // Verificar que la solicitud exista
        Optional<Solicitud> solicitudExistente = solicitudRepository.findById(solicitud.getId());
        if (solicitudExistente.isEmpty()) {
            throw new IllegalArgumentException("No se puede actualizar. Solicitud no encontrada con id: " + solicitud.getId());
        }

        // Validar que no se modifique una solicitud en estado final
        if (solicitudExistente.get().getEstado().esEstadoFinal()) {
            throw new IllegalArgumentException("No se puede modificar una solicitud en estado final: " + solicitudExistente.get().getEstado());
        }

        return solicitudRepository.save(solicitud);
    }

    @Override
    public Solicitud cambiarEstadoSolicitud(String solicitudId, EstadoSolicitud nuevoEstado, String motivo) {
        Solicitud solicitud = obtenerSolicitudPorId(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada con id: " + solicitudId));

        solicitud.cambiarEstado(nuevoEstado, motivo);
        return solicitudRepository.save(solicitud);
    }

    @Override
    public void eliminarSolicitud(String id) {
        if (!solicitudRepository.existsById(id)) {
            throw new IllegalArgumentException("Solicitud no encontrada con id: " + id);
        }
        solicitudRepository.deleteById(id);
    }

    @Override
    public List<Solicitud> obtenerSolicitudesVencidas() {
        // Considerar vencidas las solicitudes pendientes o en revisión con más de 5 días
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(5);
        return solicitudRepository.findVencidasByEstado(EstadoSolicitud.PENDIENTE, fechaLimite);
    }
}
