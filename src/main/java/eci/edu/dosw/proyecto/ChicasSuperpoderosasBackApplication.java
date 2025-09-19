package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.servicio.MateriaService;
import eci.edu.dosw.proyecto.servicio.SolicitudCambioService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

@SpringBootApplication
public class ChicasSuperpoderosasBackApplication {

    private static final Logger logger = LogManager.getLogger(ChicasSuperpoderosasBackApplication.class);

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private SolicitudCambioService solicitudCambioService;

    // Instancias de usuarios (puedes persistirlos también)
    private Decanatura decano = new Decanatura("dec-001", "DEC001", "Carlos Perez", "c.perez@uni.edu", Facultades.INGENIERIA);
    private Administrador administrador = new Administrador("admin-001", "ADM001", "Ana Gomez", "a.gomez@uni.edu", Facultades.INGENIERIA);

    private static final Pattern ID_ESTUDIANTE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@mail\\.escuelaing\\.edu\\.co$", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) {
        SpringApplication.run(ChicasSuperpoderosasBackApplication.class, args);
    }

    // Puedes usar @PostConstruct para inicializar datos de prueba si lo necesitas

    private void menuEstudiante(Scanner scanner) {
        logger.info("\n--- MENÚ ESTUDIANTE ---");

        String codigo;
        while (true) {
            logger.info("Ingrese su código de estudiante (10 dígitos numéricos): ");
            codigo = scanner.nextLine();
            if (validarIdEstudiante(codigo)) break;
            logger.error("El código debe tener exactamente 10 dígitos numéricos. Ejemplo: 1234567890");
        }

        logger.info("Ingrese su nombre: ");
        String nombre = scanner.nextLine();

        String email;
        while (true) {
            logger.info("Ingrese su email (@mail.escuelaing.edu.co): ");
            email = scanner.nextLine();
            if (validarEmail(email)) break;
            logger.error("El email debe terminar con @mail.escuelaing.edu.co");
        }

        logger.info("Ingrese su carrera: ");
        String carrera = scanner.nextLine();
        logger.info("Ingrese su semestre: ");
        int semestre = scanner.nextInt();
        scanner.nextLine();

        Estudiante estudiante = new Estudiante(
                "est-" + codigo, codigo, nombre, email, Facultades.INGENIERIA,
                carrera, semestre
        );

        List<Materia> materias = materiaService.obtenerTodas();
        logger.info("\nMaterias disponibles:");
        for (int i = 0; i < materias.size(); i++) {
            logger.info("{}. {}", (i + 1), materias.get(i).getNombre());
        }

        logger.info("Seleccione la materia para cambio (número): ");
        int materiaIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (materiaIndex < 0 || materiaIndex >= materias.size()) {
            logger.error("Selección inválida");
            return;
        }
        Materia materiaSeleccionada = materias.get(materiaIndex);

        List<Grupo> gruposMateria = materiaSeleccionada.getGrupos();
        logger.info("\nGrupos disponibles para {}:", materiaSeleccionada.getNombre());
        for (int i = 0; i < gruposMateria.size(); i++) {
            Grupo grupo = gruposMateria.get(i);
            logger.info("{}. Grupo {} - Prof: {} - Horario: {} - Cupos: {}",
                    (i + 1), grupo.getCodigo(), grupo.getProfesor(),
                    grupo.getHorario(), (grupo.getCupoMaximo() - grupo.getCupoActual()));
        }

        logger.info("Seleccione el grupo destino (número): ");
        int grupoIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (grupoIndex < 0 || grupoIndex >= gruposMateria.size()) {
            logger.error("Selección inválida");
            return;
        }
        Grupo grupoDestino = gruposMateria.get(grupoIndex);

        logger.info("Ingrese observaciones: ");
        String observaciones = scanner.nextLine();

        try {
            SolicitudCambio solicitud = estudiante.crearSolicitud(
                    materiaSeleccionada, null, materiaSeleccionada, grupoDestino, observaciones
            );
            solicitud.adjuntar(decano);
            solicitud.adjuntar(estudiante);

            solicitudCambioService.guardar(solicitud);
            decano.agregarSolicitud(solicitud);

            logger.info(" Solicitud creada con ID: {}", solicitud.getId());
            logger.info("Estado inicial: {}", solicitud.getEstadoString());
        } catch (Exception e) {
            logger.error("Error creando solicitud: {}", e.getMessage(), e);
        }
    }

    private boolean validarIdEstudiante(String id) {
        return ID_ESTUDIANTE_PATTERN.matcher(id).matches();
    }

    private boolean validarEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private void menuDecano(Scanner scanner) {
        logger.info("\n--- MENÚ DECANO ---");

        List<SolicitudCambio> solicitudes = solicitudCambioService.obtenerPendientes();
        if (solicitudes.isEmpty()) {
            logger.info("No hay solicitudes pendientes.");
            return;
        }

        logger.info("Solicitudes pendientes:");
        for (int i = 0; i < solicitudes.size(); i++) {
            SolicitudCambio solicitud = solicitudes.get(i);
            logger.info("{}. ID: {} - Estudiante: {} - Materia: {} - Estado: {}",
                    (i + 1), solicitud.getId(), solicitud.getEstudiante().getNombre(),
                    solicitud.getMateriaDestino().getNombre(), solicitud.getEstadoString());
        }

        logger.info("Seleccione solicitud para evaluar (número): ");
        int solicitudIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (solicitudIndex < 0 || solicitudIndex >= solicitudes.size()) {
            logger.error("Selección inválida");
            return;
        }
        SolicitudCambio solicitud = solicitudes.get(solicitudIndex);

        logger.info("\nEvaluando solicitud:");
        logger.info("Estudiante: {}", solicitud.getEstudiante().getNombre());
        logger.info("ID Estudiante: {}", solicitud.getEstudiante().getCodigo());
        logger.info("Email: {}", solicitud.getEstudiante().getEmail());
        logger.info("Materia: {}", solicitud.getMateriaDestino().getNombre());
        logger.info("Grupo destino: {}", solicitud.getGrupoDestino().getCodigo());
        logger.info("Cupo disponible: {}", solicitud.validarCupo() ? "✅ Sí" : "❌ No");
        logger.info("Sin cruce horarios: {}", solicitud.validarCruceHorarios() ? "✅ Sí" : "❌ No");
        logger.info("Observaciones: {}", solicitud.getObservaciones());

        logger.info("\n1. Aprobar solicitud");
        logger.info("2. Rechazar solicitud");
        logger.info("Seleccione opción: ");
        int decision = scanner.nextInt();
        scanner.nextLine();

        try {
            if (decision == 1) {
                if (!solicitud.validarCupo()) {
                    logger.error("No se puede aprobar: No hay cupo disponible");
                    return;
                }
                decano.aprobarSolicitud(solicitud);
                solicitudCambioService.actualizar(solicitud);
                logger.info(" Solicitud APROBADA");
                logger.info(" Notificación enviada al estudiante: {}", solicitud.getEstudiante().getEmail());
            } else if (decision == 2) {
                logger.info("Ingrese motivo del rechazo: ");
                String motivo = scanner.nextLine();
                decano.rechazarSolicitud(solicitud, motivo);
                solicitudCambioService.actualizar(solicitud);
                logger.info(" Solicitud RECHAZADA");
                logger.info(" Notificación enviada al estudiante: {}", solicitud.getEstudiante().getEmail());
            } else {
                logger.warn("Opción inválida");
            }
        } catch (Exception e) {
            logger.error("Error procesando solicitud: {}", e.getMessage(), e);
        }
    }

    private void menuAdministrador(Scanner scanner) {
        logger.info("\n--- MENÚ ADMINISTRADOR ---");
        logger.info("1. Crear nuevo curso");
        logger.info("2. Crear nueva carrera");
        logger.info("3. Ver cursos existentes");
        logger.info("4. Ver carreras existentes");
        logger.info("Seleccione una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1 -> crearCurso(scanner);
            case 2 -> crearCarrera(scanner);
            case 3 -> verCursos();
            case 4 -> verCarreras();
            default -> logger.warn("Opción inválida.");
        }
    }

    private void crearCurso(Scanner scanner) {
        logger.info("Ingrese código del curso: ");
        String codigo = scanner.nextLine();
        logger.info("Ingrese nombre del curso: ");
        String nombre = scanner.nextLine();
        logger.info("Ingrese número de créditos: ");
        int creditos = scanner.nextInt();
        scanner.nextLine();
        logger.info("Ingrese facultad (INGENIERIA/ADMINISTRACION): ");
        String facultad = scanner.nextLine();

        Materia nuevaMateria = new Materia(codigo, nombre, creditos, facultad);
        materiaService.guardar(nuevaMateria);

        logger.info(" Curso creado exitosamente: {}", nombre);
    }

    private void crearCarrera(Scanner scanner) {
        logger.info("Ingrese nombre de la nueva carrera: ");
        String nombreCarrera = scanner.nextLine();
        // Aquí deberías tener un servicio para carreras y persistirlo
        logger.info(" Carrera creada exitosamente: {}", nombreCarrera);
    }

    private void verCursos() {
        logger.info("\n--- CURSOS EXISTENTES ---");
        List<Materia> materias = materiaService.obtenerTodas();
        for (Materia materia : materias) {
            logger.info("- {}: {} ({} créditos)",
                    materia.getCodigo(), materia.getNombre(), materia.getCreditos());
        }
    }

    private void verCarreras() {
        logger.info("\n--- CARRERAS EXISTENTES ---");
        // Aquí deberías consultar las carreras desde el servicio correspondiente
    }
}