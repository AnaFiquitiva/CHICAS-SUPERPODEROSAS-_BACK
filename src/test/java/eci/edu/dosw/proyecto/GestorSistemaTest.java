package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.servicio.GestorSistema;

import eci.edu.dosw.proyecto.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GestorSistemaTest {

    private GestorSistema gestorSistema;

    @BeforeEach
    public void setUp() {
        gestorSistema = new GestorSistema();
    }

    @Test
    public void testObtenerEstudiantePorIdExistente() {
        Estudiante estudiante = gestorSistema.obtenerEstudiantePorId("est-001");
        assertNotNull(estudiante);
        assertEquals("María García", estudiante.getNombre());
    }

    @Test
    public void testObtenerEstudiantePorCodigoExistente() {
        Estudiante estudiante = gestorSistema.obtenerEstudiantePorId("1001234567");
        assertNotNull(estudiante);
        assertEquals("María García", estudiante.getNombre());
    }

    @Test
    public void testObtenerEstudiantePorIdNoExistente() {
        assertThrows(RuntimeException.class, () -> {
            gestorSistema.obtenerEstudiantePorId("no-existe");
        });
    }

    @Test
    public void testObtenerMateriaPorCodigoExistente() {
        Materia materia = gestorSistema.obtenerMateriaPorCodigo("MAT101");
        assertNotNull(materia);
        assertEquals("Matemáticas Básicas", materia.getNombre());
    }

    @Test
    public void testObtenerMateriaPorCodigoNoExistente() {
        assertThrows(RuntimeException.class, () -> {
            gestorSistema.obtenerMateriaPorCodigo("NOEXISTE");
        });
    }

    @Test
    public void testObtenerGrupoPorCodigoExistente() {
        Grupo grupo = gestorSistema.obtenerGrupoPorCodigo("G1");
        assertNotNull(grupo);
        assertEquals("Prof. García", grupo.getProfesor());
    }

    @Test
    public void testObtenerGrupoPorCodigoNoExistente() {
        assertThrows(RuntimeException.class, () -> {
            gestorSistema.obtenerGrupoPorCodigo("NOEXISTE");
        });
    }

    @Test
    public void testObtenerDecanoPorFacultadExistente() {
        Decanatura decano = gestorSistema.obtenerDecanoPorFacultad("INGENIERIA");
        assertNotNull(decano);
        assertEquals("Dr. Roberto Mendoza", decano.getNombre());
    }

    @Test
    public void testObtenerDecanoPorFacultadNoExistente() {
        assertThrows(RuntimeException.class, () -> {
            gestorSistema.obtenerDecanoPorFacultad("NOEXISTE");
        });
    }

    @Test
    public void testAgregarYObtenerSolicitud() {
        // Crear una solicitud usando el método real de creación
        Estudiante estudiante = gestorSistema.obtenerEstudiantePorId("est-001");
        Materia materiaOrigen = gestorSistema.obtenerMateriaPorCodigo("MAT101");
        Materia materiaDestino = gestorSistema.obtenerMateriaPorCodigo("PROG201");
        Grupo grupoDestino = gestorSistema.obtenerGrupoPorCodigo("G1");

        // Crear solicitud usando el método del estudiante
        SolicitudCambio solicitud = estudiante.crearSolicitud(
                materiaOrigen,
                null, // grupoOrigen (puede ser null)
                materiaDestino,
                grupoDestino,
                "Test observaciones"
        );

        gestorSistema.agregarSolicitud(solicitud);

        // Obtener todas las solicitudes y verificar que se agregó
        List<SolicitudCambio> solicitudes = gestorSistema.obtenerTodasSolicitudes();
        assertEquals(1, solicitudes.size());
        assertEquals("Test observaciones", solicitudes.get(0).getObservaciones());
    }

    @Test
    public void testObtenerSolicitudPorIdNoExistente() {
        assertThrows(RuntimeException.class, () -> {
            gestorSistema.obtenerSolicitudPorId("no-existe");
        });
    }

    @Test
    public void testObtenerTodasSolicitudesInicialmenteVacia() {
        List<SolicitudCambio> solicitudes = gestorSistema.obtenerTodasSolicitudes();
        assertNotNull(solicitudes);
        assertTrue(solicitudes.isEmpty());
    }

    @Test
    public void testObtenerTodasMaterias() {
        List<Materia> materias = gestorSistema.obtenerTodasMaterias();
        assertNotNull(materias);
        assertEquals(3, materias.size());

        // Verificar que contiene las materias esperadas
        assertTrue(materias.stream().anyMatch(m -> m.getCodigo().equals("MAT101")));
        assertTrue(materias.stream().anyMatch(m -> m.getCodigo().equals("PROG201")));
        assertTrue(materias.stream().anyMatch(m -> m.getCodigo().equals("FIS101")));
    }

    @Test
    public void testObtenerTodosEstudiantes() {
        List<Estudiante> estudiantes = gestorSistema.obtenerTodosEstudiantes();
        assertNotNull(estudiantes);
        assertEquals(2, estudiantes.size());

        // Verificar que contiene los estudiantes esperados
        assertTrue(estudiantes.stream().anyMatch(e -> e.getCodigo().equals("1001234567")));
        assertTrue(estudiantes.stream().anyMatch(e -> e.getCodigo().equals("1007654321")));
    }

    @Test
    public void testInicializacionDatosPrueba() {
        // Verificar que los datos de prueba se inicializaron correctamente
        assertEquals(2, gestorSistema.obtenerTodosEstudiantes().size());
        assertEquals(3, gestorSistema.obtenerTodasMaterias().size());

        // Corregir esta línea - verificar que existe un decano
        Decanatura decano = gestorSistema.obtenerDecanoPorFacultad("INGENIERIA");
        assertNotNull(decano);
        assertEquals("Dr. Roberto Mendoza", decano.getNombre());
    }
}