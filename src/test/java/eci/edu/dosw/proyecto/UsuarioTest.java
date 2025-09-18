package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {
    private Usuario usuario;
    private Usuario usuarioConcreto;

    // Clase concreta para testing de Usuario abstracto
    private static class UsuarioConcreto extends Usuario {
        public UsuarioConcreto(String id, String codigo, String nombre, String email, String rol, String facultad) {
            super(id, codigo, nombre, email, rol, facultad);
        }

        @Override
        public void actualizar() {
            System.out.println("Usuario concreto actualizado");
        }
    }

    @BeforeEach
    public void setUp() {
        usuarioConcreto = new UsuarioConcreto(
                "test-001",
                "TEST001",
                "Usuario Test",
                "test@mail.escuelaing.edu.co",
                Roles.ESTUDIANTE,
                Facultades.INGENIERIA
        );
        usuario = usuarioConcreto;
    }

    @Test
    public void testCreacionUsuario() {
        assertNotNull(usuario);
        assertEquals("test-001", usuario.getId());
        assertEquals("TEST001", usuario.getCodigo());
        assertEquals("Usuario Test", usuario.getNombre());
        assertEquals("test@mail.escuelaing.edu.co", usuario.getEmail());
        assertEquals(Roles.ESTUDIANTE, usuario.getRol());
        assertEquals(Facultades.INGENIERIA, usuario.getFacultad());
    }

    @Test
    public void testAutenticarPasswordValido() {
        assertTrue(usuario.autenticar("password123"));
        assertTrue(usuario.autenticar("cualquier cosa")); // Según tu implementación actual
    }

    @Test
    public void testAutenticarPasswordInvalido() {
        assertFalse(usuario.autenticar(""));
        assertFalse(usuario.autenticar(null));
    }

    @Test
    public void testActualizar() {
        // Test que verifica que no lance excepción
        assertDoesNotThrow(() -> usuario.actualizar());

        // Podemos verificar la salida del console output si es necesario
        // Para esto necesitaríamos capturar System.out, pero por simplicidad
        // nos aseguramos que no lance excepción
    }

    @Test
    public void testSetters() {
        // Test setId
        usuario.setId("new-id-001");
        assertEquals("new-id-001", usuario.getId());

        // Test setCodigo
        usuario.setCodigo("NEW001");
        assertEquals("NEW001", usuario.getCodigo());

        // Test setNombre
        usuario.setNombre("Nuevo Nombre");
        assertEquals("Nuevo Nombre", usuario.getNombre());

        // Test setEmail
        usuario.setEmail("nuevo@mail.escuelaing.edu.co");
        assertEquals("nuevo@mail.escuelaing.edu.co", usuario.getEmail());

        // Test setRol
        usuario.setRol(Roles.DECANATURA);
        assertEquals(Roles.DECANATURA, usuario.getRol());

        // Test setFacultad
        usuario.setFacultad(Facultades.ADMINISTRACION);
        assertEquals(Facultades.ADMINISTRACION, usuario.getFacultad());
    }

    @Test
    public void testGetters() {
        // Los getters ya se prueban indirectamente en testCreacionUsuario,
        // pero los probamos explícitamente aquí

        assertEquals("test-001", usuario.getId());
        assertEquals("TEST001", usuario.getCodigo());
        assertEquals("Usuario Test", usuario.getNombre());
        assertEquals("test@mail.escuelaing.edu.co", usuario.getEmail());
        assertEquals(Roles.ESTUDIANTE, usuario.getRol());
        assertEquals(Facultades.INGENIERIA, usuario.getFacultad());
    }

    @Test
    public void testToString() {
        String resultado = usuario.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("test-001"));
        assertTrue(resultado.contains("Usuario Test"));
        assertTrue(resultado.contains("TEST001"));
        assertTrue(resultado.contains("test@mail.escuelaing.edu.co"));
        assertTrue(resultado.contains(Roles.ESTUDIANTE));
        assertTrue(resultado.contains(Facultades.INGENIERIA));

        // Verificar formato específico
        assertTrue(resultado.startsWith("Usuario{"));
        assertTrue(resultado.contains("id='test-001'"));
        assertTrue(resultado.contains("nombre='Usuario Test'"));
    }

    @Test
    public void testEqualsAndHashCode() {
        Usuario otroUsuario = new UsuarioConcreto(
                "test-001", // Mismo ID
                "TEST002",
                "Otro Usuario",
                "otro@mail.escuelaing.edu.co",
                Roles.DECANATURA,
                Facultades.ADMINISTRACION
        );

        Usuario usuarioDiferente = new UsuarioConcreto(
                "test-002", // Diferente ID
                "TEST001",
                "Usuario Test",
                "test@mail.escuelaing.edu.co",
                Roles.ESTUDIANTE,
                Facultades.INGENIERIA
        );

        // equals debería comparar por ID
        assertEquals(usuario, otroUsuario); // Mismo ID
        assertNotEquals(usuario, usuarioDiferente); // Diferente ID

        // hashCode debería ser consistente con equals
        assertEquals(usuario.hashCode(), otroUsuario.hashCode());
        assertNotEquals(usuario.hashCode(), usuarioDiferente.hashCode());
    }

    @Test
    public void testEqualsWithNull() {
        assertNotEquals(usuario, null);
    }

    @Test
    public void testEqualsWithDifferentClass() {
        assertNotEquals(usuario, "string object");
    }

    @Test
    public void testHashCodeConsistency() {
        int hashCode1 = usuario.hashCode();
        int hashCode2 = usuario.hashCode();
        assertEquals(hashCode1, hashCode2);
    }
}