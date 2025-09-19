package eci.edu.dosw.proyecto.servicio;

import eci.edu.dosw.proyecto.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar los datos del sistema universitario
 * Responsable de mantener y proporcionar acceso a estudiantes, materias, grupos y decanos
 */
@Service
public class GestorSistema {

    private List<SolicitudCambio> solicitudes = new ArrayList<>();
    private List<Estudiante> estudiantes = new ArrayList<>();
    private List<Materia> materias = new ArrayList<>();
    private List<Grupo> grupos = new ArrayList<>();
    private List<Decanatura> decanos = new ArrayList<>();

    public GestorSistema() {
        inicializarDatosPrueba();
    }

    /**
     * Inicializa datos de prueba para el sistema
     */
    private void inicializarDatosPrueba() {
        // Crear materias de prueba
        Materia matemáticas = new Materia("MAT101", "Matemáticas Básicas", 3, "INGENIERIA");
        Materia programacion = new Materia("PROG201", "Programación I", 4, "INGENIERIA");
        Materia fisica = new Materia("FIS101", "Física I", 4, "INGENIERIA");

        // Crear grupos de prueba
        Grupo grupoMat1 = new Grupo("G1", matemáticas, "Prof. García", "Lunes 8-10", 30);
        Grupo grupoMat2 = new Grupo("G2", matemáticas, "Prof. López", "Martes 10-12", 25);
        Grupo grupoProg1 = new Grupo("G1", programacion, "Prof. Rodriguez", "Miércoles 14-16", 20);
        Grupo grupoFis1 = new Grupo("G1", fisica, "Prof. Martinez", "Jueves 16-18", 15);

        // Configurar relaciones
        matemáticas.agregarGrupo(grupoMat1);
        matemáticas.agregarGrupo(grupoMat2);
        programacion.agregarGrupo(grupoProg1);
        fisica.agregarGrupo(grupoFis1);

        // Agregar a listas
        materias.add(matemáticas);
        materias.add(programacion);
        materias.add(fisica);

        grupos.add(grupoMat1);
        grupos.add(grupoMat2);
        grupos.add(grupoProg1);
        grupos.add(grupoFis1);

        // Crear estudiantes de prueba
        Estudiante estudiante1 = new Estudiante("est-001", "1001234567", "María García",
                "maria.garcia@mail.escuelaing.edu.co", "INGENIERIA", "Ingeniería de Sistemas", 3);

        Estudiante estudiante2 = new Estudiante("est-002", "1007654321", "Carlos López",
                "carlos.lopez@mail.escuelaing.edu.co", "INGENIERIA", "Ingeniería Civil", 2);

        estudiantes.add(estudiante1);
        estudiantes.add(estudiante2);

        // Crear decanos de prueba
        Decanatura decanoIngenieria = new Decanatura("dec-001", "DEC001", "Dr. Roberto Mendoza",
                "r.mendoza@uni.edu", "INGENIERIA");

        Decanatura decanoAdministracion = new Decanatura("dec-002", "DEC002", "Dra. Ana Silva",
                "a.silva@uni.edu", "ADMINISTRACION");

        decanos.add(decanoIngenieria);
        decanos.add(decanoAdministracion);
    }

    // Métodos de acceso a datos en español

    public Estudiante obtenerEstudiantePorId(String id) {
        return estudiantes.stream()
                .filter(e -> e.getId().equals(id) || e.getCodigo().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con ID: " + id));
    }

    public Materia obtenerMateriaPorCodigo(String codigo) {
        return materias.stream()
                .filter(m -> m.getCodigo().equals(codigo))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Materia no encontrada con código: " + codigo));
    }

    public Grupo obtenerGrupoPorCodigo(String codigo) {
        return grupos.stream()
                .filter(g -> g.getCodigo().equals(codigo))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado con código: " + codigo));
    }

    public Decanatura obtenerDecanoPorFacultad(String facultad) {
        return decanos.stream()
                .filter(d -> d.getFacultad().equals(facultad))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Decano no encontrado para la facultad: " + facultad));
    }

    public SolicitudCambio obtenerSolicitudPorId(String id) {
        return solicitudes.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));
    }

    public void agregarSolicitud(SolicitudCambio solicitud) {
        solicitudes.add(solicitud);
    }

    public List<SolicitudCambio> obtenerTodasSolicitudes() {
        return new ArrayList<>(solicitudes);
    }

    public List<Materia> obtenerTodasMaterias() {
        return new ArrayList<>(materias);
    }

    public List<Estudiante> obtenerTodosEstudiantes() {
        return new ArrayList<>(estudiantes);
    }
}