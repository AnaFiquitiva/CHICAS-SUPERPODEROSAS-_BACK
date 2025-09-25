package eci.edu.dosw.proyecto.dto;

public class SolicitudDTO {
    private String estudianteId;
    private String materiaOrigenId;
    private String grupoOrigenId;
    private String materiaDestinoId;
    private String grupoDestinoId;
    private String observaciones;

    public SolicitudDTO() {}

    // Getters y Setters
    public String getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(String estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getMateriaOrigenId() {
        return materiaOrigenId;
    }

    public void setMateriaOrigenId(String materiaOrigenId) {
        this.materiaOrigenId = materiaOrigenId;
    }

    public String getGrupoOrigenId() {
        return grupoOrigenId;
    }

    public void setGrupoOrigenId(String grupoOrigenId) {
        this.grupoOrigenId = grupoOrigenId;
    }

    public String getMateriaDestinoId() {
        return materiaDestinoId;
    }

    public void setMateriaDestinoId(String materiaDestinoId) {
        this.materiaDestinoId = materiaDestinoId;
    }

    public String getGrupoDestinoId() {
        return grupoDestinoId;
    }

    public void setGrupoDestinoId(String grupoDestinoId) {
        this.grupoDestinoId = grupoDestinoId;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}