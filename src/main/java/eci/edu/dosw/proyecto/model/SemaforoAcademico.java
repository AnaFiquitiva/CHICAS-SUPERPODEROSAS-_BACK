package eci.edu.dosw.proyecto.model;

public enum SemaforoAcademico {
    VERDE("En curso normal aprobado", 1),
    AZUL("En progreso", 2),
    ROJO("Pérdida", 3);

    private final String descripcion;
    private final int nivelPrioridad;

    SemaforoAcademico(String descripcion, int nivelPrioridad) {
        this.descripcion = descripcion;
        this.nivelPrioridad = nivelPrioridad;
    }

    public boolean tieneAltaPrioridad() {
        return this == ROJO;
    }

    public static SemaforoAcademico fromPromedio(double promedio) {
        if (promedio >= 3.5) return VERDE;
        if (promedio >= 2.5) return AZUL;
        return ROJO;
    }
}