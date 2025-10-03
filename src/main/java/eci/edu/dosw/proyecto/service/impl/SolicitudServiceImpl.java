package eci.edu.dosw.proyecto.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.SolicitudService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final EstudianteRepository estudianteRepository;
    private final MateriaRepository materiaRepository;
    private final GrupoRepository grupoRepository;

    @Override
    public Solicitud crearSolicitud(Solicitud solicitud) {
        if (!solicitud.isValid()) {
            throw new IllegalArgumentException("Solicitud no válida: " + solicitud.getValidationErrors());
        }

        // Generar número de radicado único
        solicitud.setNumeroRadicado(generarNumeroRadicado());

        // Validar que el estudiante no tenga solicitudes activas para la misma materia
        List<Solicitud> solicitudesActivas = solicitudRepository.findSolicitudesActivasByEstudiante(solicitud.getEstudiante().getId());
        boolean existeSolicitudActiva = solicitudesActivas.stream()
                .anyMatch(s -> s.getMateriaDestino().getId().equals(solicitud.getMateriaDestino().getId()));

        if (existeSolicitudActiva) {
            throw new IllegalArgumentException("El estudiante ya tiene una solicitud activa para la materia destino.");
        }

        // Validar que el estudiante esté inscrito en el grupo origen
        Estudiante estudiante = estudianteRepository.findById(solicitud.getEstudiante().getId())
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        if (!estudiante.tieneInscripcionActiva(solicitud.getGrupoOrigen())) {
            throw new IllegalArgumentException("El estudiante no está inscrito en el grupo origen.");
        }

        // Validar que el grupo destino tenga cupo disponible
        if (solicitud.getGrupoDestino().estaLleno()) {
            throw new IllegalArgumentException("El grupo destino no tiene cupos disponibles.");
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

        Optional<Solicitud> solicitudExistente = solicitudRepository.findById(solicitud.getId());
        if (solicitudExistente.isEmpty()) {
            throw new IllegalArgumentException("No se puede actualizar. Solicitud no encontrada con id: " + solicitud.getId());
        }

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
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(5);
        List<Solicitud> vencidas = solicitudRepository.findVencidasByEstado(EstadoSolicitud.PENDIENTE, fechaLimite);
        vencidas.addAll(solicitudRepository.findVencidasByEstado(EstadoSolicitud.EN_REVISION, fechaLimite));
        return vencidas;
    }

    @Override
    public List<Solicitud> obtenerSolicitudesPorFacultad(String facultad) {
        return solicitudRepository.findByFacultadDestino(facultad);
    }

    @Override
    public List<Solicitud> obtenerSolicitudesPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return solicitudRepository.findByFechaCreacionBetween(inicio, fin);
    }

    private String generarNumeroRadicado() {
        return "SOL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() +
                "-" + LocalDateTime.now().getYear();
    }
}