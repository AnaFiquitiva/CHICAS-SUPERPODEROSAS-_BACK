package eci.edu.dosw.proyecto.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Clase base para sujetos observables (Patrón Observer)
 */
public abstract class Sujeto {
    private List<Observador> observadores = new ArrayList<>();
    
    /**
     * Adjunta un observador
     */
    public void adjuntar(Observador observador) {
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }
    
    /**
     * Desadjunta un observador
     */
    public void desadjuntar(Observador observador) {
        observadores.remove(observador);
    }
    
    /**
     * Notifica a todos los observadores
     */
    protected void notificar() {
        for (Observador observador : observadores) {
            observador.actualizar();
        }
    }
    
    /**
     * Obtiene la cantidad de observadores
     */
    public int cantidadObservadores() {
        return observadores.size();
    }
}
