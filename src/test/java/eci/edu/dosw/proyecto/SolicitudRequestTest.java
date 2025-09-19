package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dto.*;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class SolicitudRequestTest {

    @Test
    public void testSolicitudRequestGettersAndSetters() {
        SolicitudRequest request = new SolicitudRequest();

        request.setIdEstudiante("1001234567");
        request.setCodigoMateriaOrigen("MAT101");
        request.setCodigoGrupoOrigen("G1");
        request.setCodigoMateriaDestino("PROG201");
        request.setCodigoGrupoDestino("G2");
        request.setObservaciones("Test observaciones");

        assertEquals("1001234567", request.getIdEstudiante());
        assertEquals("MAT101", request.getCodigoMateriaOrigen());
        assertEquals("G1", request.getCodigoGrupoOrigen());
        assertEquals("PROG201", request.getCodigoMateriaDestino());
        assertEquals("G2", request.getCodigoGrupoDestino());
        assertEquals("Test observaciones", request.getObservaciones());
    }
}

