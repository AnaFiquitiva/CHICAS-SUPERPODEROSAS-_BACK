package eci.edu.dosw.proyecto.controller;


import eci.edu.dosw.proyecto.dto.GroupCapacityResponseDTO;
import eci.edu.dosw.proyecto.service.interfaces.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
