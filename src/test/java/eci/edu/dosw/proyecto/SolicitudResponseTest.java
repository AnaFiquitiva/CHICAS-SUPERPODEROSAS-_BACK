package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dto.*;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


public class SolicitudResponseTest {

    @Test
    public void testSolicitudResponseGettersAndSetters() {
        SolicitudResponse response = new SolicitudResponse();
        Date fecha = new Date();

        response.setId("test-id");
        response.setNombreEstudiante("María García");
        response.setMateriaOrigen("Matemáticas");
        response.setMateriaDestino("Programación");
        response.setEstado("PENDIENTE");
        response.setFechaCreacion(fecha);
        response.setObservaciones("Test obs");
        response.setPrioridad(895);

        assertEquals("test-id", response.getId());
        assertEquals("María García", response.getNombreEstudiante());
        assertEquals("Matemáticas", response.getMateriaOrigen());
        assertEquals("Programación", response.getMateriaDestino());
        assertEquals("PENDIENTE", response.getEstado());
        assertEquals(fecha, response.getFechaCreacion());
        assertEquals("Test obs", response.getObservaciones());
        assertEquals(895, response.getPrioridad());
    }
}