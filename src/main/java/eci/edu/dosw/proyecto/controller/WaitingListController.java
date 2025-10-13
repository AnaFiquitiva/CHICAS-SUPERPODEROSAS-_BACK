package eci.edu.dosw.proyecto.controller;



import eci.edu.dosw.proyecto.dto.WaitingListEntryDTO;
import eci.edu.dosw.proyecto.service.interfaces.WaitingListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la funcionalidad de consulta de listas de espera.
 * Solo accesible para usuarios con rol de Decanatura o Administrador.
 */
@Slf4j
@RestController
@RequestMapping("/api/groups")
public class WaitingListController {

    private final WaitingListService waitingListService;

    public WaitingListController(WaitingListService waitingListService) {
        this.waitingListService = waitingListService;
    }

    /**
     * Consultar la lista de espera de un grupo.
     */
    @GetMapping("/{groupId}/waiting-list")
    public ResponseEntity<List<WaitingListEntryDTO>> getWaitingList(
            @PathVariable String groupId,
            @RequestParam String userRole,
            @RequestParam String userFaculty) {

        log.info("{} consultando lista de espera del grupo {} perteneciente a la facultad {}",
                userRole, groupId, userFaculty);

        List<WaitingListEntryDTO> waitingList =
                waitingListService.getWaitingListByGroup(groupId, userRole, userFaculty);

        if (waitingList.isEmpty()) {
            log.info("El grupo {} no tiene estudiantes en lista de espera.", groupId);
        } else {
            log.info("Se encontraron {} estudiantes en lista de espera para el grupo {}.", waitingList.size(), groupId);
        }

        return ResponseEntity.ok(waitingList);
    }
}
