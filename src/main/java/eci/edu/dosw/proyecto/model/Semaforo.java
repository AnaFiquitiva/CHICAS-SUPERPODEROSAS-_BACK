package eci.edu.dosw.proyecto.model;

/**
 * Interface para calcular el estado del semáforo académico
 */
public interface Semaforo {
    /**
     * Calcula el estado actual del semáforo
     * @return estado del semáforo (VERDE, AZUL, ROJO)
     */
    String calcularEstado();
}
