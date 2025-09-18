package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class SemaforoTest {

    @Test
    public void testCalcularEstadoSinDatos() {
        List<Materia> historialVacio = new ArrayList<>();
        RendimientoSemaforo semaforo = new RendimientoSemaforo(historialVacio, 1);

        assertEquals("SIN_DATOS", semaforo.calcularEstado());
    }

    @Test
    public void testCalcularEstadoVerde() {
        List<Materia> historial = new ArrayList<>();

        // 4 de 5 materias aprobadas (80%)
        for (int i = 0; i < 4; i++) {
            Materia materia = new Materia("MAT" + i, "Materia " + i, 3, Facultades.INGENIERIA);
            materia.setAprobada(true);
            historial.add(materia);
        }

        Materia noAprobada = new Materia("MAT5", "Materia 5", 3, Facultades.INGENIERIA);
        historial.add(noAprobada);

        RendimientoSemaforo semaforo = new RendimientoSemaforo(historial, 3);
        assertEquals(EstadosSemaforo.VERDE, semaforo.calcularEstado());
    }

    @Test
    public void testCalcularEstadoAzul() {
        List<Materia> historial = new ArrayList<>();

        // 3 de 5 materias aprobadas (60%)
        for (int i = 0; i < 3; i++) {
            Materia materia = new Materia("MAT" + i, "Materia " + i, 3, Facultades.INGENIERIA);
            materia.setAprobada(true);
            historial.add(materia);
        }

        for (int i = 3; i < 5; i++) {
            Materia materia = new Materia("MAT" + i, "Materia " + i, 3, Facultades.INGENIERIA);
            historial.add(materia);
        }

        RendimientoSemaforo semaforo = new RendimientoSemaforo(historial, 3);
        assertEquals(EstadosSemaforo.AZUL, semaforo.calcularEstado());
    }

    @Test
    public void testCalcularEstadoRojo() {
        List<Materia> historial = new ArrayList<>();

        // 2 de 5 materias aprobadas (40%)
        for (int i = 0; i < 2; i++) {
            Materia materia = new Materia("MAT" + i, "Materia " + i, 3, Facultades.INGENIERIA);
            materia.setAprobada(true);
            historial.add(materia);
        }

        for (int i = 2; i < 5; i++) {
            Materia materia = new Materia("MAT" + i, "Materia " + i, 3, Facultades.INGENIERIA);
            historial.add(materia);
        }

        RendimientoSemaforo semaforo = new RendimientoSemaforo(historial, 3);
        assertEquals(EstadosSemaforo.ROJO, semaforo.calcularEstado());
    }
}
