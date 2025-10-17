package eci.edu.dosw.proyecto.controller;


import eci.edu.dosw.proyecto.dto.GroupCapacityResponseDTO;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.service.interfaces.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints relacionados con grupos.
 */
@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService){
        this.groupService = groupService;
    }

    /**
     * Obtener el cupo actual de un grupo por su id.
     * @return DTO con inscritos, maxCapacity y porcentaje de ocupación
     */
    @GetMapping("/{id}/capacity")
    public ResponseEntity<GroupCapacityResponseDTO> getGroupCapacity(@PathVariable("id") String id){
        GroupCapacityResponseDTO dto = groupService.getCapacity(id);
        return ResponseEntity.ok(dto);
    }

        @GetMapping
        public ResponseEntity<List<Group>> getAllGroups() {
            return ResponseEntity.ok(groupService.getAllGroups());
        }

        @GetMapping("/{groupId}")
        public ResponseEntity<Group> getGroupById(@PathVariable String groupId) {
            return ResponseEntity.ok(groupService.getGroupById(groupId));
        }

        @GetMapping("/subject/{subjectId}")
        public ResponseEntity<List<Group>> getGroupsBySubject(@PathVariable String subjectId) {
            return ResponseEntity.ok(groupService.getGroupsBySubject(subjectId));
        }

        @GetMapping("/faculty/{faculty}")
        public ResponseEntity<List<Group>> getGroupsByFaculty(@PathVariable String faculty) {
            return ResponseEntity.ok(groupService.getGroupsByFaculty(faculty));
        }

        @GetMapping("/available/{subjectId}")
        public ResponseEntity<List<Group>> getAvailableGroups(@PathVariable String subjectId) {
            return ResponseEntity.ok(groupService.getAvailableGroups(subjectId));
        }

        @GetMapping("/available")
        public ResponseEntity<List<Group>> getGroupsWithAvailability() {
            return ResponseEntity.ok(groupService.getGroupsWithAvailability());
        }

        @PutMapping("/{groupId}/capacity")
        public ResponseEntity<Group> updateGroupCapacity(@PathVariable String groupId,
                                                         @RequestParam Integer newCapacity) {
            return ResponseEntity.ok(groupService.updateGroupCapacity(groupId, newCapacity));
        }
}
