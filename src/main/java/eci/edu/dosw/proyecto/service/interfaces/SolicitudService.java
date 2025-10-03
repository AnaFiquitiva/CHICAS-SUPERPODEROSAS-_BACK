package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.model.Solicitud;
import eci.edu.dosw.proyecto.model.EstadoSolicitud;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SolicitudService {
    Solicitud crearSolicitud(Solicitud solicitud);
    Optional<Solicitud> obtenerSolicitudPorId(String id);
    List<Solicitud> obtenerSolicitudesPorEstudiante(String estudianteId);
    List<Solicitud> obtenerSolicitudesPorDecano(String decanoId);
    List<Solicitud> obtenerSolicitudesPorEstado(EstadoSolicitud estado);
    List<Solicitud> obtenerSolicitudesPendientes();
    List<Solicitud> obtenerTodasLasSolicitudes();
    Solicitud actualizarSolicitud(Solicitud solicitud);
    Solicitud cambiarEstadoSolicitud(String solicitudId, EstadoSolicitud nuevoEstado, String motivo);
    void eliminarSolicitud(String id);
    List<Solicitud> obtenerSolicitudesVencidas();
    List<Solicitud> obtenerSolicitudesPorFacultad(String facultad);
    List<Solicitud> obtenerSolicitudesPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);
}