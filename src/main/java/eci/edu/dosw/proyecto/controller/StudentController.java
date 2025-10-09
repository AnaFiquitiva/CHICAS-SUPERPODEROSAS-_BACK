package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.StudentDTO;
import eci.edu.dosw.proyecto.dto.StudentCreateDTO;
import eci.edu.dosw.proyecto.dto.StudentPartialUpdateDTO;
import eci.edu.dosw.proyecto.service.StudentService;
import eci.edu.dosw.proyecto.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador que expone los endpoints para gestionar la información de los estudiantes.
 * Controla la creación, consulta, actualización y eliminación según el rol del usuario.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Crea un nuevo estudiante.
     * Solo puede ser ejecutado por usuarios con rol ADMIN o DECANO.
     */
    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody StudentCreateDTO studentDTO,
                                           @RequestParam String role) {
        try {
            if (!role.equalsIgnoreCase("ADMIN") && !role.equalsIgnoreCase("DECANO")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Solo ADMIN o DECANO pueden registrar nuevos estudiantes.");
            }

            StudentDTO createdStudent = studentService.createStudent(studentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);

        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Consulta un estudiante por su código.
     * Disponible para cualquier rol válido.
     */
    @GetMapping("/{studentCode}")
    public ResponseEntity<?> getStudentByCode(@PathVariable String studentCode) {
        try {
            StudentDTO student = studentService.getStudentByCode(studentCode);
            return ResponseEntity.ok(student);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Lista todos los estudiantes, con filtros opcionales (nombre, código o programa).
     */
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String program) {
        List<StudentDTO> students = studentService.getAllStudents(name, code, program);
        return ResponseEntity.ok(students);
    }

    /**
     * Actualiza la información de un estudiante según el rol:
     * - ADMIN y DECANO pueden editar toda la información.
     * - ESTUDIANTE solo puede modificar correo personal, dirección y teléfono.
     */
    @PutMapping("/{studentCode}")
    public ResponseEntity<?> updateStudent(@PathVariable String studentCode,
                                           @RequestBody StudentDTO updatedStudent,
                                           @RequestParam String role) {
        try {
            StudentDTO student = studentService.updateStudent(studentCode, updatedStudent, role);
            return ResponseEntity.ok(student);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Elimina un estudiante según su código.
     * Solo los usuarios con rol ADMIN pueden hacerlo.
     */
    @DeleteMapping("/{studentCode}")
    public ResponseEntity<?> deleteStudent(@PathVariable String studentCode,
                                           @RequestParam String role) {
        try {
            studentService.deleteStudent(studentCode, role);
            return ResponseEntity.ok("Estudiante eliminado correctamente.");
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
    /**
     * Actualiza parcialmente los datos de un estudiante (correo, dirección, teléfono).
     * Solo puede ser ejecutado por el estudiante autenticado.
     */
    @PatchMapping("/{studentCode}")
    public ResponseEntity<?> updateStudentPartial(@PathVariable String studentCode,
                                                  @RequestBody StudentPartialUpdateDTO partialDTO,
                                                  @RequestParam String role) {
        try {
            StudentDTO updated = studentService.updateStudentPartial(studentCode, partialDTO, role);
            return ResponseEntity.ok(updated);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}

