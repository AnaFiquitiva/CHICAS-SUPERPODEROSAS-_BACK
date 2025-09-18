package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class PrioridadPorFechaTest {

    @Test
    public void testCalcularPrioridad() {
        Date fechaAntigua = new Date(System.currentTimeMillis() - 1000000); // Más antigua
        Date fechaReciente = new Date(); // Más reciente

        PrioridadPorFecha prioridadAntigua = new PrioridadPorFecha(fechaAntigua);
        PrioridadPorFecha prioridadReciente = new PrioridadPorFecha(fechaReciente);

        // La fecha más antigua debe tener mayor prioridad (FIFO)
        assertTrue(prioridadAntigua.calcularPrioridad() > prioridadReciente.calcularPrioridad());
    }

    @Test
    public void testGetSetFechaCreacion() {
        Date fecha1 = new Date();
        PrioridadPorFecha prioridad = new PrioridadPorFecha(fecha1);

        assertEquals(fecha1, prioridad.getFechaCreacion());

        Date fecha2 = new Date(System.currentTimeMillis() - 5000);
        prioridad.setFechaCreacion(fecha2);

        assertEquals(fecha2, prioridad.getFechaCreacion());
    }
}