package eci.edu.dosw.proyecto;
import eci.edu.dosw.proyecto.model.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EstadoValidatorTest {

    @Test
    public void testEsEstadoValido() {
        assertTrue(EstadoValidator.esEstadoValido(EstadosSolicitud.PENDIENTE));
        assertTrue(EstadoValidator.esEstadoValido(EstadosSolicitud.APROBADO));
        assertTrue(EstadoValidator.esEstadoValido(EstadosSolicitud.RECHAZADO));
        assertTrue(EstadoValidator.esEstadoValido(EstadosSolicitud.EN_REVISION));
        assertFalse(EstadoValidator.esEstadoValido("INVALIDO"));
        assertFalse(EstadoValidator.esEstadoValido(null));
    }

    @Test
    public void testPuedeTransicionarA() {
        // De PENDIENTE puede ir a APROBADO, RECHAZADO o EN_REVISION
        assertTrue(EstadoValidator.puedeTransicionarA(EstadosSolicitud.PENDIENTE, EstadosSolicitud.APROBADO));
        assertTrue(EstadoValidator.puedeTransicionarA(EstadosSolicitud.PENDIENTE, EstadosSolicitud.RECHAZADO));
        assertTrue(EstadoValidator.puedeTransicionarA(EstadosSolicitud.PENDIENTE, EstadosSolicitud.EN_REVISION));
        assertFalse(EstadoValidator.puedeTransicionarA(EstadosSolicitud.PENDIENTE, "INVALIDO"));

        // De EN_REVISION puede ir a APROBADO o RECHAZADO
        assertTrue(EstadoValidator.puedeTransicionarA(EstadosSolicitud.EN_REVISION, EstadosSolicitud.APROBADO));
        assertTrue(EstadoValidator.puedeTransicionarA(EstadosSolicitud.EN_REVISION, EstadosSolicitud.RECHAZADO));
        assertFalse(EstadoValidator.puedeTransicionarA(EstadosSolicitud.EN_REVISION, EstadosSolicitud.PENDIENTE));

        // Estados finales no pueden cambiar
        assertFalse(EstadoValidator.puedeTransicionarA(EstadosSolicitud.APROBADO, EstadosSolicitud.PENDIENTE));
        assertFalse(EstadoValidator.puedeTransicionarA(EstadosSolicitud.RECHAZADO, EstadosSolicitud.EN_REVISION));
    }
}
