package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.Materia;
import eci.edu.dosw.proyecto.model.Grupo;
import eci.edu.dosw.proyecto.model.Facultades;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class MateriaTest {
    private Materia materia;

    @BeforeEach
    public void setUp() {
        materia = new Materia("MAT101", "Matemáticas", 3, Facultades.INGENIERIA);
    }

    @Test
    public void testCreacionMateria() {
        assertNotNull(materia);
        assertEquals("MAT101", materia.getCodigo());
        assertEquals("Matemáticas", materia.getNombre());
        assertEquals(3, materia.getCreditos());
        assertEquals(Facultades.INGENIERIA, materia.getFacultad());
        assertFalse(materia.isAprobada());
    }

    @Test
    public void testAgregarGrupo() {
        Grupo grupo = new Grupo("G1", materia, "Prof. García", "Lunes 8-10", 30);
        materia.agregarGrupo(grupo);

        List<Grupo> grupos = materia.getGrupos();
        assertEquals(1, grupos.size());
        assertEquals(grupo, grupos.get(0));
    }

    @Test
    public void testObtenerGrupos() {
        Grupo grupo1 = new Grupo("G1", materia, "Prof. García", "Lunes 8-10", 30);
        Grupo grupo2 = new Grupo("G2", materia, "Prof. López", "Martes 10-12", 25);

        materia.agregarGrupo(grupo1);
        materia.agregarGrupo(grupo2);

        List<Grupo> grupos = materia.obtenerGrupos();
        assertEquals(2, grupos.size());
        assertTrue(grupos.contains(grupo1));
        assertTrue(grupos.contains(grupo2));
    }

    @Test
    public void testSetAprobada() {
        materia.setAprobada(true);
        assertTrue(materia.isAprobada());
    }

    // Tests adicionales para cubrir los métodos faltantes

    @Test
    public void testSetCodigo() {
        materia.setCodigo("MAT102");
        assertEquals("MAT102", materia.getCodigo());
    }

    @Test
    public void testSetNombre() {
        materia.setNombre("Álgebra Lineal");
        assertEquals("Álgebra Lineal", materia.getNombre());
    }

    @Test
    public void testSetCreditos() {
        materia.setCreditos(4);
        assertEquals(4, materia.getCreditos());
    }

    @Test
    public void testSetFacultad() {
        materia.setFacultad(Facultades.ADMINISTRACION);
        assertEquals(Facultades.ADMINISTRACION, materia.getFacultad());
    }

    @Test
    public void testSetGrupos() {
        List<Grupo> nuevosGrupos = new ArrayList<>();
        Grupo grupo1 = new Grupo("G1", materia, "Prof. García", "Lunes 8-10", 30);
        Grupo grupo2 = new Grupo("G2", materia, "Prof. López", "Martes 10-12", 25);

        nuevosGrupos.add(grupo1);
        nuevosGrupos.add(grupo2);

        materia.setGrupos(nuevosGrupos);

        assertEquals(2, materia.getGrupos().size());
        assertTrue(materia.getGrupos().contains(grupo1));
        assertTrue(materia.getGrupos().contains(grupo2));
    }

    @Test
    public void testToString() {
        String resultado = materia.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("MAT101"));
        assertTrue(resultado.contains("Matemáticas"));
        assertTrue(resultado.contains("3"));
        assertTrue(resultado.contains(Facultades.INGENIERIA));
        assertTrue(resultado.contains("false")); // aprobada = false
        assertTrue(resultado.contains("0")); // grupos = 0
    }

    @Test
    public void testToStringConGrupos() {
        // Test toString con grupos agregados
        Grupo grupo = new Grupo("G1", materia, "Prof. García", "Lunes 8-10", 30);
        materia.agregarGrupo(grupo);
        materia.setAprobada(true);

        String resultado = materia.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("MAT101"));
        assertTrue(resultado.contains("Matemáticas"));
        assertTrue(resultado.contains("true")); // aprobada = true
        assertTrue(resultado.contains("1")); // grupos = 1
    }

    @Test
    public void testObtenerGruposDevuelveCopia() {
        // Verificar que obtenerGrupos() devuelve una copia y no la lista original
        Grupo grupo = new Grupo("G1", materia, "Prof. García", "Lunes 8-10", 30);
        materia.agregarGrupo(grupo);

        List<Grupo> grupos1 = materia.obtenerGrupos();
        List<Grupo> grupos2 = materia.obtenerGrupos();

        // Deben tener el mismo contenido pero ser objetos diferentes
        assertEquals(grupos1.size(), grupos2.size());
        assertNotSame(grupos1, grupos2);
        assertTrue(grupos1.contains(grupo));
        assertTrue(grupos2.contains(grupo));
    }

    @Test
    public void testSetGruposConListaVacia() {
        // Agregar primero algunos grupos
        materia.agregarGrupo(new Grupo("G1", materia, "Prof. García", "Lunes 8-10", 30));
        assertEquals(1, materia.getGrupos().size());

        // Luego establecer una lista vacía
        materia.setGrupos(new ArrayList<>());
        assertEquals(0, materia.getGrupos().size());
    }

    @Test
    public void testSetGruposConNull() {
        // Test edge case: qué pasa si se pasa null
        assertDoesNotThrow(() -> materia.setGrupos(null));
    }

    @Test
    public void testModificarCreditos() {
        // Test para verificar que se pueden modificar los créditos múltiples veces
        materia.setCreditos(2);
        assertEquals(2, materia.getCreditos());

        materia.setCreditos(5);
        assertEquals(5, materia.getCreditos());
    }

    @Test
    public void testEstadoInicialAprobada() {
        // Verificar que una materia nueva siempre inicia como no aprobada
        Materia nuevaMateria = new Materia("FIS101", "Física", 4, Facultades.INGENIERIA);
        assertFalse(nuevaMateria.isAprobada());
    }
}