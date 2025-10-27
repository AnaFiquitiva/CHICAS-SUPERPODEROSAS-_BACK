package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.StudentRequest;
import eci.edu.dosw.proyecto.dto.StudentResponse;
import eci.edu.dosw.proyecto.dto.StudentUpdateRequest;
import eci.edu.dosw.proyecto.dto.StudyPlanProgressResponse;
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
 * Controlador REST para la gestión de estudiantes.
 *
 * Funcionalidad: Registro de Estudiantes (CRUD)
 * - Gestión de estudiantes por Administradores y Decanos
 * - Consulta y edición limitada por estudiantes
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Estudiantes", description = "Gestión CRUD de estudiantes")
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "Crear estudiante")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest studentRequest) {
        StudentResponse student = studentService.createStudent(studentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    @Operation(summary = "Obtener estudiante por ID")
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable String id) {
        StudentResponse student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @Operation(summary = "Listar todos los estudiantes")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        List<StudentResponse> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "Actualizar estudiante")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable String id, @Valid @RequestBody StudentRequest studentRequest) {
        StudentResponse student = studentService.updateStudent(id, studentRequest);
        return ResponseEntity.ok(student);
    }

    @Operation(summary = "Actualizar datos personales")
    @PatchMapping("/{id}/personal")
    public ResponseEntity<StudentResponse> updatePersonalInfo(@PathVariable String id, @Valid @RequestBody StudentUpdateRequest updateRequest) {
        StudentResponse student = studentService.updatePersonalInfo(id, updateRequest);
        return ResponseEntity.ok(student);
    }

    @Operation(summary = "Desactivar estudiante")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar estudiantes")
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<StudentResponse>> searchStudents(@RequestParam String term) {
        List<StudentResponse> students = studentService.searchStudents(term);
        return ResponseEntity.ok(students);
    }

}