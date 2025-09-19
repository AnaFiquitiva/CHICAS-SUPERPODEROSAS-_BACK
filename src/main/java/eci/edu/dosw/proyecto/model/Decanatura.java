package eci.edu.dosw.proyecto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa la decanatura que gestiona solicitudes
 */
@Document(collection = "decanaturas")
public class Decanatura extends Usuario {
    @Id
    private String id;
    private List<SolicitudCambio> solicitudesPendientes;

    public Decanatura(String id, String codigo, String nombre, String email, String facultad) {
        super(id, codigo, nombre, email, Roles.DECANATURA, facultad);
        this.solicitudesPendientes = new ArrayList<>();
    }

    /**
     * Evalúa una solicitud específica
     */
    public void evaluarSolicitud(SolicitudCambio solicitud) {
        System.out.println("Evaluando solicitud: " + solicitud.getId());

        // Verificar que la solicitud pertenece a la facultad
        if (!solicitud.getMateriaOrigen().getFacultad().equals(this.facultad)) {
            throw new RuntimeException("La solicitud no pertenece a esta facultad");
        }

        // Evaluar criterios
        boolean tieneCupo = solicitud.validarCupo();
        boolean sinCruceHorarios = solicitud.validarCruceHorarios();
        
        System.out.println("Cupo disponible: " + tieneCupo);
        System.out.println("Sin cruce horarios: " + sinCruceHorarios);
    }

    /**
     * Aprueba una solicitud
     */
    public void aprobarSolicitud(SolicitudCambio solicitud) {
        if (!solicitud.getEstado().puedeAprobar()) {
            throw new RuntimeException("La solicitud no puede ser aprobada en su estado actual");
        }

        if (!solicitud.validarCupo()) {
            throw new RuntimeException("No hay cupo disponible en el grupo destino");
        }

        // Cambiar estado a aprobado - MÉTODO CORRECTO
        solicitud.actualizarEstado(new Aprobado(solicitud), EstadosSolicitud.APROBADO);
        
        // Realizar el cambio efectivo
        ejecutarCambioHorario(solicitud);
        
        // Remover de pendientes
        solicitudesPendientes.remove(solicitud);
        
        System.out.println("Solicitud aprobada: " + solicitud.getId());
    }

    /**
     * Rechaza una solicitud
     */
    public void rechazarSolicitud(SolicitudCambio solicitud, String motivo) {
        // Cambiar estado a rechazado - MÉTODO CORRECTO  
        solicitud.actualizarEstado(new Rechazado(solicitud), EstadosSolicitud.RECHAZADO);
        
        // Remover de pendientes
        solicitudesPendientes.remove(solicitud);
        
        System.out.println("Solicitud rechazada: " + solicitud.getId() + 
                          (motivo != null ? ". Motivo: " + motivo : ""));
    }

    private void ejecutarCambioHorario(SolicitudCambio solicitud) {
        try {
            // Remover del grupo origen si existe
            if (solicitud.getGrupoOrigen() != null) {
                solicitud.getGrupoOrigen().removerEstudiante(solicitud.getEstudiante());
            }
            
            // Agregar al grupo destino
            solicitud.getGrupoDestino().agregarEstudiante(solicitud.getEstudiante());
            
        } catch (RuntimeException e) {
            throw new RuntimeException("Error ejecutando cambio de horario: " + e.getMessage());
        }
    }

    public void agregarSolicitud(SolicitudCambio solicitud) {
        solicitudesPendientes.add(solicitud);
    }

    public List<SolicitudCambio> getSolicitudesPendientes() {
        return solicitudesPendientes;
    }

    public void setSolicitudesPendientes(List<SolicitudCambio> solicitudesPendientes) {
        this.solicitudesPendientes = solicitudesPendientes;
    }

    @Override
    public void actualizar() {
        System.out.println("Decanatura " + nombre + " ha sido notificada de cambios en el sistema");
    }

    @Override
    public String toString() {
        return "Decanatura{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", facultad='" + facultad + '\'' +
                ", solicitudesPendientes=" + solicitudesPendientes.size() +
                '}';
    }
}
