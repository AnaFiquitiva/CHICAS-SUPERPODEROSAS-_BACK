package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.GroupAssignmentRequest;
import eci.edu.dosw.proyecto.dto.GroupAssignmentResponse;
import eci.edu.dosw.proyecto.service.interfaces.GroupAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class GroupAssignmentController {

    private final GroupAssignmentService assignmentService;

    @PostMapping("/assign")
    public ResponseEntity<GroupAssignmentResponse> assignToGroup(@RequestBody GroupAssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.assignStudentToGroup(request));
    }

    @PostMapping("/assign/bulk")
    public ResponseEntity<String> bulkAssign(@RequestBody List<GroupAssignmentRequest> requests) {
        assignmentService.bulkAssignStudents(requests);
        return ResponseEntity.ok("Bulk assignment completed successfully");
    }

    @DeleteMapping("/remove/{enrollmentId}")
    public ResponseEntity<String> removeFromGroup(@PathVariable String enrollmentId) {
        assignmentService.removeStudentFromGroup(enrollmentId);
        return ResponseEntity.ok("Student removed from group successfully");
    }
}