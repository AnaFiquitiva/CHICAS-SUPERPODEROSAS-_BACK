package eci.edu.dosw.proyecto.servicio;

import eci.edu.dosw.proyecto.dto.SolicitudRequest;
import eci.edu.dosw.proyecto.dto.SolicitudResponse;

import java.util.List;

/**
 * Interfaz para el servicio de gestión de solicitudes de cambio de horario
 */
public interface ServicioSolicitudes {

    /**
     * Crea una nueva solicitud de cambio de horario
     */
    SolicitudResponse crearSolicitud(SolicitudRequest solicitudRequest);

    /**
     * Aprueba una solicitud existente
     */
    SolicitudResponse aprobarSolicitud(String idSolicitud);

    /**
     * Rechaza una solicitud existente con un motivo específico
     */
    SolicitudResponse rechazarSolicitud(String idSolicitud, String motivo);

    /**
     * Obtiene todas las solicitudes con un estado específico
     */
    List<SolicitudResponse> obtenerSolicitudesPorEstado(String estado);

    /**
     * Obtiene todas las solicitudes de un estudiante específico
     */
    List<SolicitudResponse> obtenerSolicitudesPorEstudiante(String idEstudiante);

    /**
     * Obtiene una solicitud específica por su ID
     */
    SolicitudResponse obtenerSolicitudPorId(String idSolicitud);

    /**
     * Obtiene todas las solicitudes del sistema
     */
    List<SolicitudResponse> obtenerTodasLasSolicitudes();
}