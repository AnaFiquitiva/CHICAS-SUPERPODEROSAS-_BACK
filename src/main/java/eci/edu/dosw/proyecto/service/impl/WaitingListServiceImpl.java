package eci.edu.dosw.proyecto.service.impl;


import eci.edu.dosw.proyecto.dto.WaitingListEntryDTO;
import eci.edu.dosw.proyecto.exception.ForbiddenException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.model.UserRole;
import eci.edu.dosw.proyecto.model.WaitingListEntry;
import eci.edu.dosw.proyecto.repository.GroupRepository;
import eci.edu.dosw.proyecto.repository.StudentRepository;
import eci.edu.dosw.proyecto.repository.WaitingListRepository;
import eci.edu.dosw.proyecto.service.interfaces.WaitingListService;
import eci.edu.dosw.proyecto.utils.WaitingListMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que implementa la lógica para consultar la lista de espera de un grupo.
 */
@Service
public class WaitingListServiceImpl implements WaitingListService {

    private final WaitingListRepository waitingListRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final WaitingListMapper waitingListMapper;


    public WaitingListServiceImpl(WaitingListRepository waitingListRepository,
                                  GroupRepository groupRepository,
                                  StudentRepository studentRepository,
                                  WaitingListMapper waitingListMapper) {
        this.waitingListRepository = waitingListRepository;
        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
        this.waitingListMapper = waitingListMapper;
    }

    /**
     * Obtiene la lista de espera de un grupo específico,
     * validando los permisos según el rol y la facultad del usuario solicitante.
     * Lista de estudiantes en espera, ordenada por fecha de solicitud.
     */
    @Override
    public List<WaitingListEntryDTO> getWaitingListByGroup(String groupId, String userRole, String userFaculty) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("El grupo especificado no existe."));


        if (!(userRole.equalsIgnoreCase(UserRole.ADMIN.name()) ||
                userRole.equalsIgnoreCase(UserRole.DEAN.name()))) {
            throw new ForbiddenException("Acceso denegado: solo personal administrativo puede consultar esta lista.");
        }


        if (userRole.equalsIgnoreCase("DEAN") &&
                group.getFaculty() != null &&
                !group.getFaculty().getName().equalsIgnoreCase(userFaculty)) {
            throw new ForbiddenException("Acceso denegado: no puede consultar grupos de otra facultad.");
        }


        List<WaitingListEntry> entries = waitingListRepository.findByGroupIdOrderByRequestDateAsc(groupId);


        return entries.stream()
                .map(entry -> {
                    Student student = studentRepository.findById(entry.getStudentId())
                            .orElseThrow(() -> new NotFoundException("Estudiante no encontrado."));
                    return waitingListMapper.toDTO(entry, student);
                })
                .collect(Collectors.toList());
    }
}
