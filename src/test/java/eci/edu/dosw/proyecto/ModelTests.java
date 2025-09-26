package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTests {

    @Test
    void testGetDescripcion() {
        assertEquals("Pendiente", EstadoSolicitud.PENDIENTE.getDescripcion());
        assertEquals("En revisión", EstadoSolicitud.EN_REVISION.getDescripcion());
        assertEquals("Aprobada", EstadoSolicitud.APROBADA.getDescripcion());
    }

    @Test
    void testEsEstadoFinal() {
        assertTrue(EstadoSolicitud.APROBADA.esEstadoFinal());
        assertTrue(EstadoSolicitud.RECHAZADA.esEstadoFinal());
        assertFalse(EstadoSolicitud.PENDIENTE.esEstadoFinal());
        assertFalse(EstadoSolicitud.EN_REVISION.esEstadoFinal());
    }

    @Test
    void testPuedeCambiarA() {
        assertTrue(EstadoSolicitud.PENDIENTE.puedeCambiarA(EstadoSolicitud.EN_REVISION));
        assertTrue(EstadoSolicitud.PENDIENTE.puedeCambiarA(EstadoSolicitud.RECHAZADA));
        assertFalse(EstadoSolicitud.PENDIENTE.puedeCambiarA(EstadoSolicitud.APROBADA));

        assertTrue(EstadoSolicitud.EN_REVISION.puedeCambiarA(EstadoSolicitud.APROBADA));
        assertTrue(EstadoSolicitud.EN_REVISION.puedeCambiarA(EstadoSolicitud.RECHAZADA));
        assertFalse(EstadoSolicitud.EN_REVISION.puedeCambiarA(EstadoSolicitud.PENDIENTE));

        assertFalse(EstadoSolicitud.APROBADA.puedeCambiarA(EstadoSolicitud.RECHAZADA));
    }

    @Test
    void testGetNombre() {
        assertEquals("Lunes", DiaSemana.LUNES.getNombre());
        assertEquals("Miércoles", DiaSemana.MIERCOLES.getNombre());
    }

    @Test
    void testGetNumeroDia() {
        assertEquals(1, DiaSemana.LUNES.getNumeroDia());
        assertEquals(6, DiaSemana.SABADO.getNumeroDia());
    }

    @Test
    void testEsDiaHabil() {
        assertTrue(DiaSemana.LUNES.esDiaHabil());
        assertTrue(DiaSemana.VIERNES.esDiaHabil());
        assertFalse(DiaSemana.SABADO.esDiaHabil());
    }

    @Test
    void testFromNumero() {
        assertEquals(DiaSemana.LUNES, DiaSemana.fromNumero(1));
        assertEquals(DiaSemana.SABADO, DiaSemana.fromNumero(6));
        assertThrows(IllegalArgumentException.class, () -> DiaSemana.fromNumero(7));
    }
    @Test
    void testTieneAltaPrioridad() {
        assertTrue(SemaforoAcademico.ROJO.tieneAltaPrioridad());
        assertFalse(SemaforoAcademico.VERDE.tieneAltaPrioridad());
        assertFalse(SemaforoAcademico.AZUL.tieneAltaPrioridad());
    }

    @Test
    void testFromPromedio() {
        assertEquals(SemaforoAcademico.VERDE, SemaforoAcademico.fromPromedio(4.0));
        assertEquals(SemaforoAcademico.VERDE, SemaforoAcademico.fromPromedio(3.5));
        assertEquals(SemaforoAcademico.AZUL, SemaforoAcademico.fromPromedio(3.0));
        assertEquals(SemaforoAcademico.AZUL, SemaforoAcademico.fromPromedio(2.5));
        assertEquals(SemaforoAcademico.ROJO, SemaforoAcademico.fromPromedio(2.0));
    }
    @Test
    void testPuedeGestionarSolicitudes() {
        assertTrue(RolUsuario.DECANO.puedeGestionarSolicitudes());
        assertTrue(RolUsuario.ADMINISTRADOR.puedeGestionarSolicitudes());
        assertFalse(RolUsuario.ESTUDIANTE.puedeGestionarSolicitudes());
        assertFalse(RolUsuario.PROFESOR.puedeGestionarSolicitudes());
    }

    @Test
    void testPuedeConfigurarSistema() {
        assertTrue(RolUsuario.ADMINISTRADOR.puedeConfigurarSistema());
        assertFalse(RolUsuario.DECANO.puedeConfigurarSistema());
        assertFalse(RolUsuario.PROFESOR.puedeConfigurarSistema());
        assertFalse(RolUsuario.ESTUDIANTE.puedeConfigurarSistema());
    }

    @Test
    void testPuedeVerReportes() {
        assertTrue(RolUsuario.ADMINISTRADOR.puedeVerReportes());
        assertTrue(RolUsuario.DECANO.puedeVerReportes());
        assertTrue(RolUsuario.PROFESOR.puedeVerReportes());
        assertFalse(RolUsuario.ESTUDIANTE.puedeVerReportes());
    }
    private Horario createValidHorario() {
        return Horario.builder()
                .id("1")
                .dia(DiaSemana.LUNES)
                .horaInicio(LocalTime.of(8, 0))
                .horaFin(LocalTime.of(10, 0))
                .aula("A101")
                .edificio("Edificio A")
                .build();
    }



    @Test
    void testHayCruce() {
        Horario horario1 = createValidHorario();
        Horario horario2 = Horario.builder()
                .dia(DiaSemana.LUNES)
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(11, 0))
                .build();

        assertTrue(horario1.hayCruce(horario2));

        horario2.setDia(DiaSemana.MARTES);
        assertFalse(horario1.hayCruce(horario2));

        horario2.setDia(DiaSemana.LUNES);
        horario2.setHoraInicio(LocalTime.of(10, 0));
        horario2.setHoraFin(LocalTime.of(12, 0));
        assertFalse(horario1.hayCruce(horario2));
    }

    @Test
    void testGetDuracionMinutos() {
        Horario horario = createValidHorario();
        assertEquals(120, horario.getDuracionMinutos());
    }

    @Test
    void testGetHorarioFormateado() {
        Horario horario = createValidHorario();
        assertEquals("Lunes 08:00-10:00", horario.getHorarioFormateado());
    }
    private Grupo createValidGrupo() {
        return Grupo.builder()
                .id("1")
                .codigo("GRUPO01")
                .cupoMaximo(30)
                .capacidadActual(15)
                .activo(true)
                .aula("A101")
                .edificio("Edificio A")
                .materia(new Materia())
                .horarios(new ArrayList<>())
                .build();
    }

    @Test
    void testGetDisponibilidad() {
        Grupo grupo = createValidGrupo();
        assertEquals(15, grupo.getDisponibilidad());
    }

    @Test
    void testTieneCupo() {
        Grupo grupo = createValidGrupo();
        assertTrue(grupo.tieneCupo());

        grupo.setCapacidadActual(30);
        assertFalse(grupo.tieneCupo());
    }

    @Test
    void testEstaCercaDelLimite() {
        Grupo grupo = createValidGrupo();
        grupo.setCapacidadActual(27); // 90% de 30
        assertTrue(grupo.estaCercaDelLimite());

        grupo.setCapacidadActual(15);
        assertFalse(grupo.estaCercaDelLimite());
    }

    @Test
    void testIncrementarCapacidad() {
        Grupo grupo = createValidGrupo();
        int capacidadInicial = grupo.getCapacidadActual();

        grupo.incrementarCapacidad();
        assertEquals(capacidadInicial + 1, grupo.getCapacidadActual());

        // Test límite
        grupo.setCapacidadActual(30);
        assertThrows(IllegalStateException.class, () -> grupo.incrementarCapacidad());
    }

    @Test
    void testDecrementarCapacidad() {
        Grupo grupo = createValidGrupo();
        int capacidadInicial = grupo.getCapacidadActual();

        grupo.decrementarCapacidad();
        assertEquals(capacidadInicial - 1, grupo.getCapacidadActual());

        // Test límite inferior
        grupo.setCapacidadActual(0);
        grupo.decrementarCapacidad(); // No debe lanzar excepción
        assertEquals(0, grupo.getCapacidadActual());
    }

    @Test
    void testGetPorcentajeOcupacion() {
        Grupo grupo = createValidGrupo();
        assertEquals(50.0, grupo.getPorcentajeOcupacion(), 0.01);
    }
    private Materia createValidMateria() {
        return Materia.builder()
                .id("1")
                .codigo("MAT101")
                .nombre("Matemáticas I")
                .creditos(3)
                .facultad("Ingeniería")
                .departamento("Matemáticas")
                .activa(true)
                .grupos(new ArrayList<>())
                .prerequisitos(new ArrayList<>())
                .build();
    }



    @Test
    void testGetNumeroGruposActivos() {
        Materia materia = createValidMateria();
        Grupo grupo1 = new Grupo();
        grupo1.setActivo(true);
        Grupo grupo2 = new Grupo();
        grupo2.setActivo(false);

        materia.getGrupos().add(grupo1);
        materia.getGrupos().add(grupo2);

        assertEquals(1, materia.getNumeroGruposActivos());
    }

    @Test
    void testTieneCuposDisponibles() {
        Materia materia = createValidMateria();
        Grupo grupo = new Grupo();
        grupo.setCupoMaximo(30);
        grupo.setCapacidadActual(15);

        materia.getGrupos().add(grupo);
        assertTrue(materia.tieneCuposDisponibles());

        grupo.setCapacidadActual(30);
        assertFalse(materia.tieneCuposDisponibles());
    }
    private Inscripcion createValidInscripcion() {
        Estudiante estudiante = new Estudiante();
        Grupo grupo = new Grupo();
        grupo.setCupoMaximo(30);
        grupo.setCapacidadActual(15);

        return Inscripcion.builder()
                .id("1")
                .estudiante(estudiante)
                .grupo(grupo)
                .fechaInscripcion(LocalDateTime.now())
                .activa(true)
                .build();
    }

    @Test
    void testConstructorDefault() {
        Inscripcion inscripcion = new Inscripcion();
        assertTrue(inscripcion.isActiva());
        assertNotNull(inscripcion.getFechaInscripcion());
    }



    @Test
    void testCancelar() {
        Inscripcion inscripcion = createValidInscripcion();
        int capacidadAntes = inscripcion.getGrupo().getCapacidadActual();

        inscripcion.cancelar();

        assertFalse(inscripcion.isActiva());
        assertNotNull(inscripcion.getFechaCancelacion());
        assertEquals(capacidadAntes - 1, inscripcion.getGrupo().getCapacidadActual());
    }

    @Test
    void testGetEstado() {
        Inscripcion inscripcion = createValidInscripcion();
        assertEquals("Activa", inscripcion.getEstado());

        inscripcion.setActiva(false);
        assertEquals("Cancelada", inscripcion.getEstado());

        inscripcion.setActiva(true);
        inscripcion.setCalificacion(4.0);
        assertEquals("Calificada", inscripcion.getEstado());
    }
    private Notificacion createValidNotificacion() {
        Usuario usuario = new Estudiante();
        return Notificacion.builder()
                .id("1")
                .tipo(TipoNotificacion.CAMBIO_ESTADO_SOLICITUD)
                .titulo("Test Notificación")
                .mensaje("Mensaje de prueba")
                .destinatario(usuario)
                .leida(false)
                .fechaEnvio(LocalDateTime.now())
                .build();
    }



    @Test
    void testMarcarComoLeida() {
        Notificacion notificacion = createValidNotificacion();

        notificacion.marcarComoLeida();

        assertTrue(notificacion.isLeida());
        assertNotNull(notificacion.getFechaLectura());
    }

    @Test
    void testEsReciente() {
        Notificacion notificacion = createValidNotificacion();
        assertTrue(notificacion.esReciente());

        notificacion.setFechaEnvio(LocalDateTime.now().minusDays(10));
        assertFalse(notificacion.esReciente());
    }

    @Test
    void testGetResumen() {
        Notificacion notificacion = createValidNotificacion();
        String mensajeLargo = "A".repeat(150);
        notificacion.setMensaje(mensajeLargo);

        String resumen = notificacion.getResumen();
        assertEquals(103, resumen.length()); // 100 caracteres + "..."
        assertTrue(resumen.endsWith("..."));
    }
    private Solicitud createValidSolicitud() {
        Estudiante estudiante = new Estudiante();
        Materia materiaOrigen = new Materia();
        Grupo grupoOrigen = new Grupo();
        Materia materiaDestino = new Materia();
        Grupo grupoDestino = new Grupo();

        return Solicitud.builder()
                .id("1")
                .numeroRadicado("RAD001")
                .estudiante(estudiante)
                .materiaOrigen(materiaOrigen)
                .grupoOrigen(grupoOrigen)
                .materiaDestino(materiaDestino)
                .grupoDestino(grupoDestino)
                .estado(EstadoSolicitud.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .prioridad(1)
                .build();
    }

    @Test
    void testCambiarEstado() {
        Solicitud solicitud = createValidSolicitud();

        solicitud.cambiarEstado(EstadoSolicitud.EN_REVISION, null);
        assertEquals(EstadoSolicitud.EN_REVISION, solicitud.getEstado());
        assertNotNull(solicitud.getFechaRevision());

        solicitud.cambiarEstado(EstadoSolicitud.APROBADA, null);
        assertEquals(EstadoSolicitud.APROBADA, solicitud.getEstado());
        assertNotNull(solicitud.getFechaResolucion());
    }

    @Test
    void testCambiarEstadoInvalido() {
        Solicitud solicitud = createValidSolicitud();

        assertThrows(IllegalStateException.class,
                () -> solicitud.cambiarEstado(EstadoSolicitud.APROBADA, null));
    }

    @Test
    void testEstaVencida() {
        Solicitud solicitud = createValidSolicitud();
        assertFalse(solicitud.estaVencida());

        solicitud.setFechaVencimiento(LocalDateTime.now().minusDays(1));
        assertTrue(solicitud.estaVencida());

        solicitud.setEstado(EstadoSolicitud.APROBADA);
        assertFalse(solicitud.estaVencida()); // Estados finales no vencen
    }

    @Test
    void testPuedeSerModificada() {
        Solicitud solicitud = createValidSolicitud();
        assertTrue(solicitud.puedeSerModificada());

        solicitud.setEstado(EstadoSolicitud.APROBADA);
        assertFalse(solicitud.puedeSerModificada());

        solicitud = createValidSolicitud();
        solicitud.setFechaVencimiento(LocalDateTime.now().minusDays(1));
        assertFalse(solicitud.puedeSerModificada());
    }

    @Test
    void testGetDiasPendientes() {
        Solicitud solicitud = createValidSolicitud();
        solicitud.setFechaCreacion(LocalDateTime.now().minusDays(5));

        assertEquals(5, solicitud.getDiasPendientes());

        solicitud.setEstado(EstadoSolicitud.APROBADA);
        assertEquals(0, solicitud.getDiasPendientes());
    }
    private Estudiante createValidUsuario() {
        Estudiante estudiante = new Estudiante();
        estudiante.setCodigo("EST001");
        estudiante.setNombre("Juan Pérez");
        estudiante.setEmail("juan@test.com");
        estudiante.setPassword("123456");
        estudiante.setActivo(true);
        estudiante.setFechaCreacion(LocalDateTime.now());
        return estudiante;
    }



    @Test
    void testActualizarUltimoAcceso() {
        Usuario usuario = createValidUsuario();
        LocalDateTime antes = LocalDateTime.now();

        usuario.actualizarUltimoAcceso();

        assertNotNull(usuario.getFechaUltimoAcceso());
        assertTrue(usuario.getFechaUltimoAcceso().isAfter(antes) ||
                usuario.getFechaUltimoAcceso().isEqual(antes));
    }

    @Test
    void testEsActivo() {
        Usuario usuario = createValidUsuario();
        assertTrue(usuario.esActivo());

        usuario.setActivo(false);
        assertFalse(usuario.esActivo());

        usuario.setActivo(true);
        usuario.setFechaUltimoAcceso(LocalDateTime.now().minusMonths(7));
        assertFalse(usuario.esActivo());
    }

    @Test
    void testGetValidationErrors() {
        Usuario usuario = createValidUsuario();
        usuario.setCodigo("");
        usuario.setNombre("A");

        String errors = usuario.getValidationErrors();
        assertTrue(errors.contains("código es obligatorio"));
        assertTrue(errors.contains("nombre debe tener"));
    }
}














