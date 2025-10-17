package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.model.Enrollment;
import eci.edu.dosw.proyecto.service.interfaces.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Enrollment>> getStudentEnrollments(@PathVariable String studentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Enrollment>> getGroupEnrollments(@PathVariable String groupId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByGroup(groupId));
    }

    @PostMapping
    public ResponseEntity<Enrollment> createEnrollment(@RequestBody Enrollment enrollment) {
        return ResponseEntity.ok(enrollmentService.createEnrollment(enrollment));
    }

    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<String> cancelEnrollment(@PathVariable String enrollmentId) {
        enrollmentService.cancelEnrollment(enrollmentId);
        return ResponseEntity.ok("Enrollment cancelled successfully");
    }
}