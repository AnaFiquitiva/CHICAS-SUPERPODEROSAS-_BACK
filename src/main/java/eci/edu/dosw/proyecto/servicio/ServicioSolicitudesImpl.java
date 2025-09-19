package eci.edu.dosw.proyecto.servicio;

import eci.edu.dosw.proyecto.dto.SolicitudRequest;
import eci.edu.dosw.proyecto.dto.SolicitudResponse;
import eci.edu.dosw.proyecto.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de solicitudes de cambio de horario
 */
@Service
public class ServicioSolicitudesImpl implements ServicioSolicitudes {

    private final GestorSistema gestorSistema;

    public ServicioSolicitudesImpl(GestorSistema gestorSistema) {
        this.gestorSistema = gestorSistema;
    }

    @Override
    public SolicitudResponse crearSolicitud(SolicitudRequest solicitudRequest) {
        try {
            // Obtener entidades del sistema
            Estudiante estudiante = gestorSistema.obtenerEstudiantePorId(solicitudRequest.getIdEstudiante());
            Materia materiaOrigen = gestorSistema.obtenerMateriaPorCodigo(solicitudRequest.getCodigoMateriaOrigen());
            Materia materiaDestino = gestorSistema.obtenerMateriaPorCodigo(solicitudRequest.getCodigoMateriaDestino());

            Grupo grupoOrigen = solicitudRequest.getCodigoGrupoOrigen() != null ?
                    gestorSistema.obtenerGrupoPorCodigo(solicitudRequest.getCodigoGrupoOrigen()) : null;

            Grupo grupoDestino = gestorSistema.obtenerGrupoPorCodigo(solicitudRequest.getCodigoGrupoDestino());

            // Crear la solicitud
            SolicitudCambio solicitud = estudiante.crearSolicitud(
                    materiaOrigen, grupoOrigen, materiaDestino, grupoDestino,
                    solicitudRequest.getObservaciones()
            );

            // Guardar la solicitud en el sistema
            gestorSistema.agregarSolicitud(solicitud);

            // Convertir a DTO de respuesta
            return convertirASolicitudResponse(solicitud);

        } catch (Exception e) {
            throw new RuntimeException("Error al crear la solicitud: " + e.getMessage(), e);
        }
    }

    @Override
    public SolicitudResponse aprobarSolicitud(String idSolicitud) {
        SolicitudCambio solicitud = gestorSistema.obtenerSolicitudPorId(idSolicitud);
        Decanatura decano = gestorSistema.obtenerDecanoPorFacultad(solicitud.getMateriaOrigen().getFacultad());

        decano.aprobarSolicitud(solicitud);
        return convertirASolicitudResponse(solicitud);
    }

    @Override
    public SolicitudResponse rechazarSolicitud(String idSolicitud, String motivo) {
        SolicitudCambio solicitud = gestorSistema.obtenerSolicitudPorId(idSolicitud);
        Decanatura decano = gestorSistema.obtenerDecanoPorFacultad(solicitud.getMateriaOrigen().getFacultad());

        decano.rechazarSolicitud(solicitud, motivo);
        return convertirASolicitudResponse(solicitud);
    }

    @Override
    public List<SolicitudResponse> obtenerSolicitudesPorEstado(String estado) {
        return gestorSistema.obtenerTodasSolicitudes().stream()
                .filter(solicitud -> solicitud.getEstadoString().equals(estado))
                .map(this::convertirASolicitudResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SolicitudResponse> obtenerSolicitudesPorEstudiante(String idEstudiante) {
        return gestorSistema.obtenerTodasSolicitudes().stream()
                .filter(solicitud -> solicitud.getEstudiante().getId().equals(idEstudiante) ||
                        solicitud.getEstudiante().getCodigo().equals(idEstudiante))
                .map(this::convertirASolicitudResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SolicitudResponse obtenerSolicitudPorId(String idSolicitud) {
        SolicitudCambio solicitud = gestorSistema.obtenerSolicitudPorId(idSolicitud);
        return convertirASolicitudResponse(solicitud);
    }

    @Override
    public List<SolicitudResponse> obtenerTodasLasSolicitudes() {
        return gestorSistema.obtenerTodasSolicitudes().stream()
                .map(this::convertirASolicitudResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad SolicitudCambio a un DTO SolicitudResponse
     */
    private SolicitudResponse convertirASolicitudResponse(SolicitudCambio solicitud) {
        SolicitudResponse response = new SolicitudResponse();
        response.setId(solicitud.getId());
        response.setNombreEstudiante(solicitud.getEstudiante().getNombre());
        response.setMateriaOrigen(solicitud.getMateriaOrigen().getNombre());
        response.setMateriaDestino(solicitud.getMateriaDestino().getNombre());
        response.setEstado(solicitud.getEstadoString());
        response.setFechaCreacion(solicitud.getFechaCreacion());
        response.setObservaciones(solicitud.getObservaciones());
        response.setPrioridad(solicitud.getPrioridad().calcularPrioridad());
        return response;
    }
}