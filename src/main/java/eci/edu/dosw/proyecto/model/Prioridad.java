package eci.edu.dosw.proyecto.model;
/**
 * Interface para calcular la prioridad de una solicitud
 */
public interface Prioridad {
    /**
     * Calcula la prioridad basada en criterios específicos
     * @return valor de prioridad (mayor valor = mayor prioridad)
     */
    int calcularPrioridad();
}