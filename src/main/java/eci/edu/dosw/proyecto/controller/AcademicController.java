package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.service.interfaces.AcademicService;
import eci.edu.dosw.proyecto.service.interfaces.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de configuración académica.
 *
 * Funcionalidades: Configuración Académica
 * - Gestión de facultades, programas y materias
 * - Configuración de períodos para cambios
 * - Consulta del avance del plan de estudios
 */
@RestController
@RequestMapping("/api/academic")
@RequiredArgsConstructor
@Tag(name = "Académico", description = "Gestión de configuración académica")
public class AcademicController {

    private final AcademicService academicService;
    private final StudentService studentService;

    // === FACULTADES ===

    @Operation(summary = "Crear facultad")
    @PostMapping("/faculties")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacultyResponse> createFaculty(@Valid @RequestBody FacultyRequest facultyRequest) {
        FacultyResponse faculty = academicService.createFaculty(facultyRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(faculty);
    }

    @Operation(summary = "Obtener facultad por ID")
    @GetMapping("/faculties/{id}")
    public ResponseEntity<FacultyResponse> getFacultyById(@PathVariable String id) {
        FacultyResponse faculty = academicService.getFacultyById(id);
        return ResponseEntity.ok(faculty);
    }

    @Operation(summary = "Obtener facultad por código")
    @GetMapping("/faculties/code/{code}")
    public ResponseEntity<FacultyResponse> getFacultyByCode(@PathVariable String code) {
        FacultyResponse faculty = academicService.getFacultyByCode(code);
        return ResponseEntity.ok(faculty);
    }

    @Operation(summary = "Listar todas las facultades")
    @GetMapping("/faculties")
    public ResponseEntity<List<FacultyResponse>> getAllFaculties() {
        List<FacultyResponse> faculties = academicService.getAllFaculties();
        return ResponseEntity.ok(faculties);
    }

    @Operation(summary = "Actualizar facultad")
    @PutMapping("/faculties/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacultyResponse> updateFaculty(@PathVariable String id, @Valid @RequestBody FacultyRequest facultyRequest) {
        FacultyResponse faculty = academicService.updateFaculty(id, facultyRequest);
        return ResponseEntity.ok(faculty);
    }

    @Operation(summary = "Desactivar facultad")
    @DeleteMapping("/faculties/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateFaculty(@PathVariable String id) {
        academicService.deactivateFaculty(id);
        return ResponseEntity.noContent().build();
    }

    // === PROGRAMAS ===

    @Operation(summary = "Crear programa")
    @PostMapping("/programs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProgramResponse> createProgram(@Valid @RequestBody ProgramRequest programRequest) {
        ProgramResponse program = academicService.createProgram(programRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(program);
    }

    @Operation(summary = "Obtener programa por ID")
    @GetMapping("/programs/{id}")
    public ResponseEntity<ProgramResponse> getProgramById(@PathVariable String id) {
        ProgramResponse program = academicService.getProgramById(id);
        return ResponseEntity.ok(program);
    }

    @Operation(summary = "Obtener programa por código")
    @GetMapping("/programs/code/{code}")
    public ResponseEntity<ProgramResponse> getProgramByCode(@PathVariable String code) {
        ProgramResponse program = academicService.getProgramByCode(code);
        return ResponseEntity.ok(program);
    }

    @Operation(summary = "Listar programas por facultad")
    @GetMapping("/programs/faculty/{facultyId}")
    public ResponseEntity<List<ProgramResponse>> getProgramsByFaculty(@PathVariable String facultyId) {
        List<ProgramResponse> programs = academicService.getProgramsByFaculty(facultyId);
        return ResponseEntity.ok(programs);
    }

    @Operation(summary = "Listar todos los programas")
    @GetMapping("/programs")
    public ResponseEntity<List<ProgramResponse>> getAllPrograms() {
        List<ProgramResponse> programs = academicService.getAllPrograms();
        return ResponseEntity.ok(programs);
    }

    @Operation(summary = "Actualizar programa")
    @PutMapping("/programs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProgramResponse> updateProgram(@PathVariable String id, @Valid @RequestBody ProgramRequest programRequest) {
        ProgramResponse program = academicService.updateProgram(id, programRequest);
        return ResponseEntity.ok(program);
    }

    @Operation(summary = "Desactivar programa")
    @DeleteMapping("/programs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateProgram(@PathVariable String id) {
        academicService.deactivateProgram(id);
        return ResponseEntity.noContent().build();
    }

    // === MATERIAS ===

    @Operation(summary = "Crear materia")
    @PostMapping("/subjects")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<SubjectResponse> createSubject(@Valid @RequestBody SubjectRequest subjectRequest) {
        SubjectResponse subject = academicService.createSubject(subjectRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(subject);
    }

    @Operation(summary = "Obtener materia por ID")
    @GetMapping("/subjects/{id}")
    public ResponseEntity<SubjectResponse> getSubjectById(@PathVariable String id) {
        SubjectResponse subject = academicService.getSubjectById(id);
        return ResponseEntity.ok(subject);
    }

    @Operation(summary = "Obtener materia por código")
    @GetMapping("/subjects/code/{code}")
    public ResponseEntity<SubjectResponse> getSubjectByCode(@PathVariable String code) {
        SubjectResponse subject = academicService.getSubjectByCode(code);
        return ResponseEntity.ok(subject);
    }

    @Operation(summary = "Listar materias por facultad")
    @GetMapping("/subjects/faculty/{facultyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<SubjectResponse>> getSubjectsByFaculty(@PathVariable String facultyId) {
        List<SubjectResponse> subjects = academicService.getSubjectsByFaculty(facultyId);
        return ResponseEntity.ok(subjects);
    }

    @Operation(summary = "Listar todas las materias")
    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectResponse>> getAllSubjects() {
        List<SubjectResponse> subjects = academicService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    @Operation(summary = "Buscar materias")
    @GetMapping("/subjects/search")
    public ResponseEntity<List<SubjectResponse>> searchSubjects(@RequestParam String searchTerm) {
        List<SubjectResponse> subjects = academicService.searchSubjects(searchTerm);
        return ResponseEntity.ok(subjects);
    }

    @Operation(summary = "Actualizar materia")
    @PutMapping("/subjects/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<SubjectResponse> updateSubject(@PathVariable String id, @Valid @RequestBody SubjectRequest subjectRequest) {
        SubjectResponse subject = academicService.updateSubject(id, subjectRequest);
        return ResponseEntity.ok(subject);
    }

    @Operation(summary = "Desactivar materia")
    @DeleteMapping("/subjects/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<Void> deactivateSubject(@PathVariable String id) {
        academicService.deactivateSubject(id);
        return ResponseEntity.noContent().build();
    }

    // === PERÍODOS ACADÉMICOS ===

    @Operation(summary = "Obtener período académico actual")
    @GetMapping("/periods/current")
    public ResponseEntity<AcademicPeriodResponse> getCurrentAcademicPeriod() {
        AcademicPeriodResponse period = academicService.getCurrentAcademicPeriod();
        return ResponseEntity.ok(period);
    }

    @Operation(summary = "Listar períodos académicos activos")
    @GetMapping("/periods/active")
    public ResponseEntity<List<AcademicPeriodResponse>> getActiveAcademicPeriods() {
        List<AcademicPeriodResponse> periods = academicService.getActiveAcademicPeriods();
        return ResponseEntity.ok(periods);
    }

    @Operation(summary = "Verificar si período de solicitudes está activo")
    @GetMapping("/periods/active/faculty/{facultyId}")
    public ResponseEntity<Boolean> isRequestPeriodActive(@PathVariable String facultyId) {
        boolean isActive = academicService.isRequestPeriodActive(facultyId);
        return ResponseEntity.ok(isActive);
    }

    // === VALIDACIÓN ACADÉMICA ===

    @Operation(summary = "Validar prerrequisitos")
    @GetMapping("/validate-prerequisites/student/{studentId}/subject/{subjectId}")
    public ResponseEntity<Boolean> validatePrerequisites(@PathVariable String studentId, @PathVariable String subjectId) {
        boolean isValid = academicService.validatePrerequisites(studentId, subjectId);
        return ResponseEntity.ok(isValid);
    }

    @Operation(summary = "Obtener avance del plan de estudios")
    @GetMapping("/study-plan-progress/student/{studentId}")
    public ResponseEntity<StudyPlanProgressResponse> getStudyPlanProgress(@PathVariable String studentId) {
        StudyPlanProgressResponse progress = studentService.getStudyPlanProgress(studentId);
        return ResponseEntity.ok(progress);
    }
    @Operation(summary = "Actualizar progreso de materias del estudiante")
    @PostMapping("/study-plan-progress/student/{studentId}/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN') or @securityService.isStudentOwner(authentication, #studentId)")
    public ResponseEntity<Void> updateSubjectProgress(@PathVariable String studentId) {
        studentService.updateSubjectProgress(studentId);
        return ResponseEntity.noContent().build();
    }
}