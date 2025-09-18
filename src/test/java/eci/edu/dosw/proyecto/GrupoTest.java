package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.Materia;
import eci.edu.dosw.proyecto.model.Estudiante;
import eci.edu.dosw.proyecto.model.Facultades;
import eci.edu.dosw.proyecto.model.Grupo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class GrupoTest {
    private Materia materia;
    private Grupo grupo;
    private Estudiante estudiante;

    @BeforeEach
    void setUp() {
        materia = new Materia("MAT101", "Matemáticas", 3, Facultades.INGENIERIA);
        grupo = new Grupo("G1", materia, "Prof. García", "Lunes 8-10", 2);
        estudiante = new Estudiante("est-001", "1234567890", "Juan Perez",
                "juan@mail.escuelaing.edu.co", Facultades.INGENIERIA,
                "Ingeniería", 3);
    }

    @Test
    public void testCreacionGrupo() {
        assertNotNull(grupo);
        assertEquals("G1", grupo.getCodigo());
        assertEquals(materia, grupo.getMateria());
        assertEquals("Prof. García", grupo.getProfesor());
        assertEquals("Lunes 8-10", grupo.getHorario());
        assertEquals(2, grupo.getCupoMaximo());
        assertEquals(0, grupo.getCupoActual());
    }

    @Test
    public void testAgregarEstudiante() {
        grupo.agregarEstudiante(estudiante);
        assertEquals(1, grupo.getCupoActual());
        assertTrue(grupo.getEstudiantesInscritos().contains(estudiante));
    }

    @Test
    public void testAgregarEstudianteSinCupo() {
        grupo.agregarEstudiante(estudiante);

        Estudiante estudiante2 = new Estudiante("est-002", "0987654321", "Maria Lopez",
                "maria@mail.escuelaing.edu.co", Facultades.INGENIERIA,
                "Ingeniería", 3);
        grupo.agregarEstudiante(estudiante2);

        // Intentar agregar tercer estudiante a grupo con cupo 2
        Estudiante estudiante3 = new Estudiante("est-003", "1111111111", "Carlos Ruiz",
                "carlos@mail.escuelaing.edu.co", Facultades.INGENIERIA,
                "Ingeniería", 3);

        assertThrows(RuntimeException.class, () -> grupo.agregarEstudiante(estudiante3));
    }

    @Test
    public void testRemoverEstudiante() {
        grupo.agregarEstudiante(estudiante);
        assertEquals(1, grupo.getCupoActual());

        grupo.removerEstudiante(estudiante);
        assertEquals(0, grupo.getCupoActual());
        assertFalse(grupo.getEstudiantesInscritos().contains(estudiante));
    }

    @Test
    public void testVerificarDisponibilidad() {
        assertTrue(grupo.verificarDisponibilidad());

        grupo.agregarEstudiante(estudiante);
        assertTrue(grupo.verificarDisponibilidad());

        Estudiante estudiante2 = new Estudiante("est-002", "0987654321", "Maria Lopez",
                "maria@mail.escuelaing.edu.co", Facultades.INGENIERIA,
                "Ingeniería", 3);
        grupo.agregarEstudiante(estudiante2);

        assertFalse(grupo.verificarDisponibilidad());
    }

    // Tests adicionales para cubrir los métodos faltantes

    @Test
    public void testSetCodigo() {
        grupo.setCodigo("G2");
        assertEquals("G2", grupo.getCodigo());
    }

    @Test
    public void testSetMateria() {
        Materia nuevaMateria = new Materia("FIS101", "Física", 4, Facultades.INGENIERIA);
        grupo.setMateria(nuevaMateria);
        assertEquals(nuevaMateria, grupo.getMateria());
    }

    @Test
    public void testSetProfesor() {
        grupo.setProfesor("Prof. López");
        assertEquals("Prof. López", grupo.getProfesor());
    }

    @Test
    public void testSetHorario() {
        grupo.setHorario("Martes 10-12");
        assertEquals("Martes 10-12", grupo.getHorario());
    }

    @Test
    public void testSetCupoMaximo() {
        grupo.setCupoMaximo(5);
        assertEquals(5, grupo.getCupoMaximo());
    }

    @Test
    public void testSetEstudiantesInscritos() {
        List<Estudiante> nuevosEstudiantes = new ArrayList<>();

        Estudiante estudiante1 = new Estudiante("est-001", "1234567890", "Juan Perez",
                "juan@mail.escuelaing.edu.co", Facultades.INGENIERIA,
                "Ingeniería", 3);
        Estudiante estudiante2 = new Estudiante("est-002", "0987654321", "Maria Lopez",
                "maria@mail.escuelaing.edu.co", Facultades.INGENIERIA,
                "Ingeniería", 3);

        nuevosEstudiantes.add(estudiante1);
        nuevosEstudiantes.add(estudiante2);

        grupo.setEstudiantesInscritos(nuevosEstudiantes);

        assertEquals(2, grupo.getCupoActual());
        assertEquals(2, grupo.getEstudiantesInscritos().size());
        assertTrue(grupo.getEstudiantesInscritos().contains(estudiante1));
        assertTrue(grupo.getEstudiantesInscritos().contains(estudiante2));
    }

    @Test
    public void testToString() {
        String resultado = grupo.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("G1"));
        assertTrue(resultado.contains("Matemáticas"));
        assertTrue(resultado.contains("Prof. García"));
        assertTrue(resultado.contains("Lunes 8-10"));
        assertTrue(resultado.contains("2")); // cupoMaximo
        assertTrue(resultado.contains("0")); // cupoActual inicial
    }

    @Test
    public void testToStringConEstudiantes() {
        // Agregar estudiantes y verificar toString
        grupo.agregarEstudiante(estudiante);

        String resultado = grupo.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("G1"));
        assertTrue(resultado.contains("1")); // cupoActual = 1
    }

    @Test
    public void testToStringConMateriaNula() {
        // Test edge case: materia nula
        Grupo grupoConMateriaNula = new Grupo("G3", null, "Prof. Test", "Miércoles 14-16", 3);
        String resultado = grupoConMateriaNula.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("N/A")); // Cuando materia es null
    }

    @Test
    public void testSetEstudiantesInscritosVacio() {
        // Primero agregar estudiantes
        grupo.agregarEstudiante(estudiante);
        assertEquals(1, grupo.getCupoActual());

        // Luego establecer lista vacía
        grupo.setEstudiantesInscritos(new ArrayList<>());
        assertEquals(0, grupo.getCupoActual());
        assertTrue(grupo.getEstudiantesInscritos().isEmpty());
    }

    @Test
    public void testRemoverEstudianteNoExistente() {
        // Intentar remover un estudiante que no está en el grupo
        Estudiante estudianteNoExistente = new Estudiante("est-999", "9999999999", "No Existe",
                "noexiste@mail.escuelaing.edu.co", Facultades.INGENIERIA,
                "Ingeniería", 3);

        assertEquals(0, grupo.getCupoActual());
        grupo.removerEstudiante(estudianteNoExistente);
        assertEquals(0, grupo.getCupoActual()); // No debería cambiar
    }

    @Test
    public void testModificarCupoMaximoConEstudiantesInscritos() {
        // Agregar estudiantes
        grupo.agregarEstudiante(estudiante);
        assertEquals(1, grupo.getCupoActual());

        // Aumentar cupo máximo
        grupo.setCupoMaximo(5);
        assertEquals(5, grupo.getCupoMaximo());

        // El cupo actual no debería cambiar
        assertEquals(1, grupo.getCupoActual());
    }

    @Test
    public void testSetEstudiantesInscritosActualizaCupoActual() {
        List<Estudiante> listaEstudiantes = new ArrayList<>();

        // Crear 3 estudiantes
        for (int i = 1; i <= 3; i++) {
            Estudiante est = new Estudiante("est-00" + i, "123456789" + i, "Estudiante " + i,
                    "est" + i + "@mail.escuelaing.edu.co", Facultades.INGENIERIA,
                    "Ingeniería", 3);
            listaEstudiantes.add(est);
        }

        grupo.setEstudiantesInscritos(listaEstudiantes);

        // Verificar que el cupo actual se actualiza automáticamente
        assertEquals(3, grupo.getCupoActual());
        assertEquals(3, grupo.getEstudiantesInscritos().size());
    }

    @Test
    public void testCambiarMateriaYProfesor() {
        // Test para verificar que se pueden cambiar múltiples propiedades
        Materia nuevaMateria = new Materia("QUIM101", "Química", 3, Facultades.ADMINISTRACION);

        grupo.setMateria(nuevaMateria);
        grupo.setProfesor("Prof. Química");
        grupo.setHorario("Jueves 16-18");

        assertEquals(nuevaMateria, grupo.getMateria());
        assertEquals("Prof. Química", grupo.getProfesor());
        assertEquals("Jueves 16-18", grupo.getHorario());
    }

    @Test
    public void testDisponibilidadDespuesDeModificarCupo() {
        // Llenar el grupo al máximo
        grupo.agregarEstudiante(estudiante);

        Estudiante estudiante2 = new Estudiante("est-002", "0987654321", "Maria Lopez",
                "maria@mail.escuelaing.edu.co", Facultades.INGENIERIA,
                "Ingeniería", 3);
        grupo.agregarEstudiante(estudiante2);

        assertFalse(grupo.verificarDisponibilidad());

        // Aumentar el cupo máximo
        grupo.setCupoMaximo(3);

        // Ahora debería haber disponibilidad
        assertTrue(grupo.verificarDisponibilidad());
    }
}