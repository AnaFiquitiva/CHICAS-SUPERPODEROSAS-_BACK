package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

@SpringBootApplication
public class ChicasSuperpoderosasBackApplication {

    private static final Logger logger = LogManager.getLogger(ChicasSuperpoderosasBackApplication.class);

    // Datos de prueba
    private static List<SolicitudCambio> solicitudes = new ArrayList<>();
    private static List<Materia> materias = new ArrayList<>();
    private static List<Grupo> grupos = new ArrayList<>();
    private static List<String> carreras = new ArrayList<>();

    // Crear instancias de usuarios
    private static Decanatura decano = new Decanatura("dec-001", "DEC001", "Carlos Perez", "c.perez@uni.edu", Facultades.INGENIERIA);
    private static Administrador administrador = new Administrador("admin-001", "ADM001", "Ana Gomez", "a.gomez@uni.edu", Facultades.INGENIERIA);

    // Patrones de validación
    private static final Pattern ID_ESTUDIANTE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@mail\\.escuelaing\\.edu\\.co$", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) {
        logger.info("Iniciando aplicación ChicasSuperpoderosasBackApplication");
        SpringApplication.run(ChicasSuperpoderosasBackApplication.class, args);

        // Inicializar datos de prueba
        inicializarDatos();

        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            logger.info("\n=== SISTEMA UNIVERSITARIO ===");
            logger.info("1. Soy Estudiante");
            logger.info("2. Soy Decano");
            logger.info("3. Soy Administrador");
            logger.info("4. Salir");
            logger.info("Seleccione una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // limpiar buffer

            switch (opcion) {
                case 1 -> menuEstudiante(scanner);
                case 2 -> menuDecano(scanner);
                case 3 -> menuAdministrador(scanner);
                case 4 -> {
                    logger.info("Saliendo del sistema...");
                    salir = true;
                }
                default -> logger.warn("Opción inválida, intente de nuevo.");
            }
        }
        logger.info("Aplicación finalizada");
    }

    private static void inicializarDatos() {
        logger.debug("Inicializando datos de prueba");

        // Crear materias de prueba
        Materia matemáticas = new Materia("MAT101", "Matemáticas Básicas", 3, Facultades.INGENIERIA);
        Materia programacion = new Materia("PROG201", "Programación I", 4, Facultades.INGENIERIA);

        materias.add(matemáticas);
        materias.add(programacion);

        // Crear grupos de prueba
        Grupo grupoMat1 = new Grupo("G1", matemáticas, "Prof. García", "Lunes 8-10", 30);
        Grupo grupoMat2 = new Grupo("G2", matemáticas, "Prof. López", "Martes 10-12", 25);
        Grupo grupoProg1 = new Grupo("G1", programacion, "Prof. Rodriguez", "Miércoles 14-16", 20);

        grupos.add(grupoMat1);
        grupos.add(grupoMat2);
        grupos.add(grupoProg1);

        // Agregar grupos a materias
        matemáticas.agregarGrupo(grupoMat1);
        matemáticas.agregarGrupo(grupoMat2);
        programacion.agregarGrupo(grupoProg1);

        // Carreras iniciales
        carreras.add("Ingeniería de Sistemas");
        carreras.add("Administración de Empresas");

        logger.info("Datos de prueba inicializados: {} materias, {} grupos, {} carreras",
                materias.size(), grupos.size(), carreras.size());
    }

    private static void menuEstudiante(Scanner scanner) {
        logger.info("\n--- MENÚ ESTUDIANTE ---");

        // Validar ID de estudiante (10 dígitos numéricos)
        String codigo;
        while (true) {
            logger.info("Ingrese su código de estudiante (10 dígitos numéricos): ");
            codigo = scanner.nextLine();

            if (validarIdEstudiante(codigo)) {
                break;
            } else {
                logger.error("El código debe tener exactamente 10 dígitos numéricos. Ejemplo: 1234567890");
            }
        }

        logger.info("Ingrese su nombre: ");
        String nombre = scanner.nextLine();

        // Validar email (debe terminar con @mail.escuelaing.edu.co)
        String email;
        while (true) {
            logger.info("Ingrese su email (@mail.escuelaing.edu.co): ");
            email = scanner.nextLine();

            if (validarEmail(email)) {
                break;
            } else {
                logger.error("El email debe terminar con @mail.escuelaing.edu.co");
                logger.info("Ejemplo: juan.perez@mail.escuelaing.edu.co");
            }
        }

        logger.info("Ingrese su carrera: ");
        String carrera = scanner.nextLine();
        logger.info("Ingrese su semestre: ");
        int semestre = scanner.nextInt();
        scanner.nextLine();

        // Crear estudiante
        Estudiante estudiante = new Estudiante(
                "est-" + codigo, codigo, nombre, email, Facultades.INGENIERIA,
                carrera, semestre
        );

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

        logger.info("\nGrupos disponibles para {}:", materiaSeleccionada.getNombre());
        List<Grupo> gruposMateria = materiaSeleccionada.getGrupos();
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
            // Crear solicitud (usando grupo origen null para simplicidad)
            SolicitudCambio solicitud = estudiante.crearSolicitud(
                    materiaSeleccionada, null, materiaSeleccionada, grupoDestino, observaciones
            );

            // Adjuntar decano como observador
            solicitud.adjuntar(decano);
            solicitud.adjuntar(estudiante);

            solicitudes.add(solicitud);
            decano.agregarSolicitud(solicitud);

            logger.info(" Solicitud creada con ID: {}", solicitud.getId());
            logger.info("Estado inicial: {}", solicitud.getEstadoString());

        } catch (Exception e) {
            logger.error("Error creando solicitud: {}", e.getMessage(), e);
        }
    }

    // Métodos de validación
    private static boolean validarIdEstudiante(String id) {
        return ID_ESTUDIANTE_PATTERN.matcher(id).matches();
    }

    private static boolean validarEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private static void menuDecano(Scanner scanner) {
        logger.info("\n--- MENÚ DECANO ---");

        if (solicitudes.isEmpty()) {
            logger.info("No hay solicitudes pendientes.");
            return;
        }

        logger.info("Solicitudes pendientes:");
        for (int i = 0; i < solicitudes.size(); i++) {
            SolicitudCambio solicitud = solicitudes.get(i);
            if (solicitud.getEstadoString().equals(EstadosSolicitud.PENDIENTE)) {
                logger.info("{}. ID: {} - Estudiante: {} - Materia: {} - Estado: {}",
                        (i + 1), solicitud.getId(), solicitud.getEstudiante().getNombre(),
                        solicitud.getMateriaDestino().getNombre(), solicitud.getEstadoString());
            }
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
                logger.info(" Solicitud APROBADA");
                logger.info(" Notificación enviada al estudiante: {}", solicitud.getEstudiante().getEmail());
            } else if (decision == 2) {
                logger.info("Ingrese motivo del rechazo: ");
                String motivo = scanner.nextLine();
                decano.rechazarSolicitud(solicitud, motivo);
                logger.info(" Solicitud RECHAZADA");
                logger.info(" Notificación enviada al estudiante: {}", solicitud.getEstudiante().getEmail());
            } else {
                logger.warn("Opción inválida");
            }
        } catch (Exception e) {
            logger.error("Error procesando solicitud: {}", e.getMessage(), e);
        }
    }

    private static void menuAdministrador(Scanner scanner) {
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

    private static void crearCurso(Scanner scanner) {
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
        materias.add(nuevaMateria);

        logger.info(" Curso creado exitosamente: {}", nombre);
    }

    private static void crearCarrera(Scanner scanner) {
        logger.info("Ingrese nombre de la nueva carrera: ");
        String nombreCarrera = scanner.nextLine();

        carreras.add(nombreCarrera);
        logger.info(" Carrera creada exitosamente: {}", nombreCarrera);
    }

    private static void verCursos() {
        logger.info("\n--- CURSOS EXISTENTES ---");
        for (Materia materia : materias) {
            logger.info("- {}: {} ({} créditos)",
                    materia.getCodigo(), materia.getNombre(), materia.getCreditos());
        }
    }

    private static void verCarreras() {
        logger.info("\n--- CARRERAS EXISTENTES ---");
        for (String carrera : carreras) {
            logger.info("- {}", carrera);
        }
    }
}