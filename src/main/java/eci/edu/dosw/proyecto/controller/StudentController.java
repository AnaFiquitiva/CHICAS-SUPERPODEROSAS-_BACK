package eci.edu.dosw.proyecto.controller;


import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.service.interfaces.StudentService;
import eci.edu.dosw.proyecto.exception.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador que expone los endpoints para gestionar la información de los estudiantes.
 * Controla la creación, consulta, actualización y eliminación según el rol del usuario.
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * Crea un nuevo estudiante.
     * Solo roles ADMIN o DECANO pueden ejecutar esta operación.
     */
    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(
            @Valid @RequestBody StudentCreateDTO studentCreateDTO,
            @RequestParam String role
    ) {
        if (!role.equalsIgnoreCase("ADMIN") && !role.equalsIgnoreCase("DECANO")) {
            throw new CustomException("Solo ADMIN o DECANO pueden registrar nuevos estudiantes.");
        }
        StudentDTO createdStudent = studentService.createStudent(studentCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    /**
     * Consulta un estudiante por su código.
     * Disponible para cualquier rol válido.
     */
    @GetMapping("/{studentCode}")
    public ResponseEntity<StudentDTO> getStudentByCode(@PathVariable String studentCode) {
        StudentDTO student = studentService.getStudentByCode(studentCode);
        return ResponseEntity.ok(student);
    }

    /**
     * Lista todos los estudiantes, con filtros opcionales (nombre, código o programa).
     */
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String program
    ) {
        List<StudentDTO> students = studentService.getAllStudents(name, code, program);
        return ResponseEntity.ok(students);
    }

    /**
     * Actualiza la información completa de un estudiante según el rol:
     * - ADMIN y DECANO: pueden editar toda la información.
     * - ESTUDIANTE: solo puede modificar correo personal.
     */
    @PutMapping("/{studentCode}")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable String studentCode,
            @Valid @RequestBody StudentDTO updatedStudent,
            @RequestParam String role
    ) {
        StudentDTO student = studentService.updateStudent(studentCode, updatedStudent, role);
        return ResponseEntity.ok(student);
    }

    /**
     * Actualiza parcialmente los datos personales de un estudiante (correo, dirección, teléfono).
     * Solo puede ser ejecutado por el estudiante autenticado.
     */
    @PatchMapping("/{studentCode}")
    public ResponseEntity<StudentDTO> updateStudentPartial(
            @PathVariable String studentCode,
            @Valid @RequestBody StudentPartialUpdateDTO partialDTO,
            @RequestParam String role
    ) {
        StudentDTO updated = studentService.updateStudentPartial(studentCode, partialDTO, role);
        return ResponseEntity.ok(updated);
    }

    /**
     * Elimina un estudiante según su código.
     * Solo los usuarios con rol ADMIN pueden realizar esta operación.
     */
    @DeleteMapping("/{studentCode}")
    public ResponseEntity<String> deleteStudent(
            @PathVariable String studentCode,
            @RequestParam String role
    ) {
        studentService.deleteStudent(studentCode, role);
        return ResponseEntity.ok("Estudiante eliminado correctamente.");
    }
}
