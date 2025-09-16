package eci.edu.dosw.proyecto.model;

import java.util.List;

/**
 * Implementación del semáforo de rendimiento académico
 */
public class RendimientoSemaforo implements Semaforo {
    private List<Materia> historial;
    private int semestreActual;

    public RendimientoSemaforo(List<Materia> historial, int semestreActual) {
        this.historial = historial;
        this.semestreActual = semestreActual;
    }

    @Override
    public String calcularEstado() {
        if (historial == null || historial.isEmpty()) {
            return "SIN_DATOS"; // Evita división por cero
        }

        int materiasAprobadas = (int) historial.stream()
                .filter(Materia::isAprobada)
                .count();

        double porcentajeAvance = (double) materiasAprobadas / historial.size();

        if (porcentajeAvance >= 0.8) {
            return "VERDE";
        } else if (porcentajeAvance >= 0.6) {
            return "AZUL";
        } else {
            return "ROJO";
        }
    }
}
