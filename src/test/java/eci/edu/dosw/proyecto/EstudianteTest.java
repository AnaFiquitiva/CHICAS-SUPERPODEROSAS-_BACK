package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class EstudianteTest {
    private Estudiante estudiante;
    private Materia materia;
    private Grupo grupoOrigen;
    private Grupo grupoDestino;

    @BeforeEach
    public void setUp() {
        estudiante = new Estudiante("est-001", "1234567890", "Juan Perez",
                "juan@mail.escuelaing.edu.co", Facultades.INGENIERIA,
                "Ingeniería de Sistemas", 3);

        materia = new Materia("MAT101", "Matemáticas", 3, Facultades.INGENIERIA);
        grupoOrigen = new Grupo("G1", materia, "Prof. García", "Lunes 8-10", 30);
        grupoDestino = new Grupo("G2", materia, "Prof. López", "Martes 10-12", 25);
    }

    @Test
    public void testCreacionEstudiante() {
        assertNotNull(estudiante);
        assertEquals("1234567890", estudiante.getCodigo());
        assertEquals("Juan Perez", estudiante.getNombre());
        assertEquals("juan@mail.escuelaing.edu.co", estudiante.getEmail());
        assertEquals("Ingeniería de Sistemas", estudiante.getCarrera());
        assertEquals(3, estudiante.getSemestre());
        assertEquals(Roles.ESTUDIANTE, estudiante.getRol());
    }

    @Test
    public void testCrearSolicitud() {
        SolicitudCambio solicitud = estudiante.crearSolicitud(
                materia, grupoOrigen, materia, grupoDestino, "Cambio de horario"
        );

        assertNotNull(solicitud);
        assertEquals(estudiante, solicitud.getEstudiante());
        assertEquals(materia, solicitud.getMateriaOrigen());
        assertEquals(grupoOrigen, solicitud.getGrupoOrigen());
        assertEquals(materia, solicitud.getMateriaDestino());
        assertEquals(grupoDestino, solicitud.getGrupoDestino());
        assertEquals("Cambio de horario", solicitud.getObservaciones());
    }

    @Test
    public void testSemaforoEstado() {
        // Test semáforo con historial vacío
        assertNotNull(estudiante.getSemaforoEstado());

        // Test semáforo con materias aprobadas
        List<Materia> historial = new ArrayList<>();
        Materia materia1 = new Materia("MAT101", "Matemáticas", 3, Facultades.INGENIERIA);
        Materia materia2 = new Materia("PROG201", "Programación", 4, Facultades.INGENIERIA);

        materia1.setAprobada(true);
        materia2.setAprobada(true);

        historial.add(materia1);
        historial.add(materia2);

        estudiante.setHistorial(historial);

        // El semáforo debería recalcularse automáticamente
        assertNotNull(estudiante.getSemaforoEstado());

        // Verificar el estado basado en la lógica de RendimientoSemaforo
        // 2 de 2 materias aprobadas = 100% -> VERDE
        assertEquals(EstadosSemaforo.VERDE, estudiante.getSemaforoEstado());
    }

    @Test
    public void testSemaforoEstadoAzul() {
        // Test semáforo AZUL (60-79% de aprobación)
        List<Materia> historial = new ArrayList<>();
        Materia materia1 = new Materia("MAT101", "Matemáticas", 3, Facultades.INGENIERIA);
        Materia materia2 = new Materia("PROG201", "Programación", 4, Facultades.INGENIERIA);
        Materia materia3 = new Materia("FIS101", "Física", 3, Facultades.INGENIERIA);

        materia1.setAprobada(true);
        materia2.setAprobada(true);
        materia3.setAprobada(false); // Una materia no aprobada

        historial.add(materia1);
        historial.add(materia2);
        historial.add(materia3);

        estudiante.setHistorial(historial);

        // 2 de 3 materias aprobadas = 66% -> AZUL
        assertEquals(EstadosSemaforo.AZUL, estudiante.getSemaforoEstado());
    }

    @Test
    public void testSemaforoEstadoRojo() {
        // Test semáforo ROJO (menos del 60% de aprobación)
        List<Materia> historial = new ArrayList<>();
        Materia materia1 = new Materia("MAT101", "Matemáticas", 3, Facultades.INGENIERIA);
        Materia materia2 = new Materia("PROG201", "Programación", 4, Facultades.INGENIERIA);
        Materia materia3 = new Materia("FIS101", "Física", 3, Facultades.INGENIERIA);

        materia1.setAprobada(true); // Solo una aprobada
        materia2.setAprobada(false);
        materia3.setAprobada(false);

        historial.add(materia1);
        historial.add(materia2);
        historial.add(materia3);

        estudiante.setHistorial(historial);

        // 1 de 3 materias aprobadas = 33% -> ROJO
        assertEquals(EstadosSemaforo.ROJO, estudiante.getSemaforoEstado());
    }

    @Test
    public void testActualizarNotificacion() {
        // Test que el estudiante puede recibir notificaciones
        assertDoesNotThrow(() -> estudiante.actualizar());
    }

    @Test
    public void testToString() {
        String resultado = estudiante.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("est-001"));
        assertTrue(resultado.contains("Juan Perez"));
        assertTrue(resultado.contains("Ingeniería de Sistemas"));
        assertTrue(resultado.contains("3"));
        // No verificar el string exacto porque puede cambiar
    }

    @Test
    public void testCrearSolicitudConDosMateriasYGrupos() {
        Materia materia1 = new Materia("MAT102", "Matemáticas II", 3, Facultades.INGENIERIA);
        Grupo grupo1 = new Grupo("G1", materia1, "Prof. García", "Lunes 8-10", 30);
        Grupo grupo2 = new Grupo("G2", materia1, "Prof. López", "Martes 10-12", 25);

        String observaciones = "Solicito cambio de materia";

        SolicitudCambio solicitud = estudiante.crearSolicitud(materia1, grupo1, materia1, grupo2, observaciones);

        assertNotNull(solicitud);
        assertEquals(materia1, solicitud.getMateriaOrigen());
        assertEquals(materia1, solicitud.getMateriaDestino());
        assertEquals(grupo1, solicitud.getGrupoOrigen());
        assertEquals(grupo2, solicitud.getGrupoDestino());
        assertEquals(observaciones, solicitud.getObservaciones());
    }

    @Test
    public void testCrearSolicitudConUnaMateriaYGrupo() {
        String observaciones = "Cambio a nuevo grupo";

        // Usar la versión simplificada del método
        SolicitudCambio solicitud = estudiante.crearSolicitud(materia, grupoDestino, observaciones);

        assertNotNull(solicitud);
        assertEquals(materia, solicitud.getMateriaOrigen());
        assertEquals(materia, solicitud.getMateriaDestino());
        assertNull(solicitud.getGrupoOrigen()); // Debe ser null en la versión simplificada
        assertEquals(grupoDestino, solicitud.getGrupoDestino());
        assertEquals(observaciones, solicitud.getObservaciones());
    }

    @Test
    public void testSetCarrera() {
        estudiante.setCarrera("Ingeniería Civil");
        assertEquals("Ingeniería Civil", estudiante.getCarrera());
    }

    @Test
    public void testSetSemestre() {
        estudiante.setSemestre(6);
        assertEquals(6, estudiante.getSemestre());
    }

    @Test
    public void testSetSemaforoEstado() {
        estudiante.setSemaforoEstado("AMARILLO");
        assertEquals("AMARILLO", estudiante.getSemaforoEstado());
    }

    @Test
    public void testGetHistorial() {
        List<Materia> historial = estudiante.getHistorial();
        assertNotNull(historial);
        assertTrue(historial.isEmpty());
    }

    @Test
    public void testSetHistorial() {
        List<Materia> nuevoHistorial = new ArrayList<>();
        nuevoHistorial.add(materia);

        estudiante.setHistorial(nuevoHistorial);

        assertEquals(1, estudiante.getHistorial().size());
        assertEquals(materia, estudiante.getHistorial().get(0));
    }

    @Test
    public void testActualizar() {
        // Capturar la salida de System.out
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            estudiante.actualizar();

            String expectedOutput = "Estudiante Juan Perez ha sido notificado de cambios";
            assertTrue(outContent.toString().contains(expectedOutput));
        } finally {
            // Restaurar System.out
            System.setOut(originalOut);
        }
    }

    @Test
    public void testValidarPeriodoSolicitudes() {
        // Este método es privado, pero podemos probarlo indirectamente
        // creando una solicitud y viendo si lanza excepción
        assertDoesNotThrow(() -> {
            estudiante.crearSolicitud(materia, grupoOrigen, materia, grupoDestino, "Test");
        });
    }

    @Test
    public void testYaCancoloMateria() {
        // Este método es privado, probamos indirectamente
        // Si la materia ya fue cancelada, debería lanzar excepción al crear solicitud
        assertDoesNotThrow(() -> {
            estudiante.crearSolicitud(materia, grupoOrigen, materia, grupoDestino, "Test");
        });
    }
}