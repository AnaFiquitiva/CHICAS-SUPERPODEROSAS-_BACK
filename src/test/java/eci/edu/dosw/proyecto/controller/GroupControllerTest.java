package eci.edu.dosw.proyecto.controller;



import eci.edu.dosw.proyecto.dto.GroupCapacityResponseDTO;
import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.model.Faculty;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.service.interfaces.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupControllerTest {

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    private Group group;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        group = new Group();
        group.setId("1");
        group.setGroupCode("G1");
        group.setMaxCapacity(30);
        group.setCurrentEnrollment(10);
        group.setSubjectId("MAT101");
        group.setFaculty(Faculty.SYSTEMS_ENGINEERING);
    }

    // ✅ Crear grupo exitosamente
    @Test
    void shouldCreateGroupSuccessfully() {
        when(groupService.createGroup(any(Group.class))).thenReturn(group);

        ResponseEntity<Group> response = groupController.createGroup(group);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("G1", response.getBody().getGroupCode());
        verify(groupService, times(1)).createGroup(any(Group.class));
    }

    // ❌ Error al crear grupo (código duplicado)
    @Test
    void shouldReturnError_whenGroupCodeAlreadyExists() {
        when(groupService.createGroup(any(Group.class)))
                .thenThrow(new BusinessException("Código de grupo ya existente."));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> groupController.createGroup(group)
        );

        assertEquals("Código de grupo ya existente.", ex.getMessage());
        verify(groupService).createGroup(any(Group.class));
    }

    // ✅ Obtener todos los grupos
    @Test
    void shouldReturnAllGroupsSuccessfully() {
        when(groupService.getAllGroups()).thenReturn(List.of(group));

        ResponseEntity<List<Group>> response = groupController.getAllGroups();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(groupService).getAllGroups();
    }

    // ✅ Obtener grupo por ID
    @Test
    void shouldReturnGroupByIdSuccessfully() {
        when(groupService.getGroupById("1")).thenReturn(group);

        ResponseEntity<Group> response = groupController.getGroupById("1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("G1", response.getBody().getGroupCode());
        verify(groupService).getGroupById("1");
    }

    // ❌ Error: grupo no encontrado
    @Test
    void shouldThrowException_whenGroupNotFound() {
        when(groupService.getGroupById("999"))
                .thenThrow(new BusinessException("Grupo no encontrado."));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> groupController.getGroupById("999")
        );

        assertEquals("Grupo no encontrado.", ex.getMessage());
        verify(groupService).getGroupById("999");
    }

    // ✅ Obtener capacidad del grupo
    @Test
    void shouldReturnGroupCapacitySuccessfully() {
        GroupCapacityResponseDTO dto = new GroupCapacityResponseDTO(10, 30, 33.3);
        when(groupService.getCapacity("1")).thenReturn(dto);

        ResponseEntity<GroupCapacityResponseDTO> response = groupController.getGroupCapacity("1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(30, response.getBody().getMaxCapacity());
        verify(groupService).getCapacity("1");
    }


    // ✅ Obtener grupos por materia
    @Test
    void shouldReturnGroupsBySubjectSuccessfully() {
        when(groupService.getGroupsBySubject("MAT101")).thenReturn(List.of(group));

        ResponseEntity<List<Group>> response = groupController.getGroupsBySubject("MAT101");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(groupService).getGroupsBySubject("MAT101");
    }

    // ✅ Obtener grupos por facultad
    @Test
    void shouldReturnGroupsByFacultySuccessfully() {
        when(groupService.getGroupsByFaculty("INGENIERIA")).thenReturn(List.of(group));

        ResponseEntity<List<Group>> response = groupController.getGroupsByFaculty("INGENIERIA");

        assertEquals(200, response.getStatusCodeValue());
        verify(groupService).getGroupsByFaculty("INGENIERIA");
    }

    // ✅ Obtener grupos disponibles por materia
    @Test
    void shouldReturnAvailableGroupsBySubjectSuccessfully() {
        when(groupService.getAvailableGroups("MAT101")).thenReturn(List.of(group));

        ResponseEntity<List<Group>> response = groupController.getAvailableGroups("MAT101");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(groupService).getAvailableGroups("MAT101");
    }

    // ✅ Obtener grupos con disponibilidad
    @Test
    void shouldReturnGroupsWithAvailabilitySuccessfully() {
        when(groupService.getGroupsWithAvailability()).thenReturn(List.of(group));

        ResponseEntity<List<Group>> response = groupController.getGroupsWithAvailability();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(groupService).getGroupsWithAvailability();
    }

    // ✅ Actualizar capacidad de grupo
    @Test
    void shouldUpdateGroupCapacitySuccessfully() {
        when(groupService.updateGroupCapacity("1", 40)).thenReturn(group);

        ResponseEntity<Group> response = groupController.updateGroupCapacity("1", 40);

        assertEquals(200, response.getStatusCodeValue());
        verify(groupService).updateGroupCapacity("1", 40);
    }

    // ✅ Asignar profesor a grupo
    @Test
    void shouldAssignProfessorToGroupSuccessfully() {
        when(groupService.assignProfessorToGroup("1", "P1", "principal")).thenReturn(group);

        ResponseEntity<Group> response = groupController.assignProfessorToGroup("1", "P1", "principal");

        assertEquals(200, response.getStatusCodeValue());
        verify(groupService).assignProfessorToGroup("1", "P1", "principal");
    }


    // ✅ Remover profesor de grupo
    @Test
    void shouldRemoveProfessorFromGroupSuccessfully() {
        when(groupService.removeProfessorFromGroup("1", "principal")).thenReturn(group);

        ResponseEntity<Group> response = groupController.removeProfessorFromGroup("1", "principal");

        assertEquals(200, response.getStatusCodeValue());
        verify(groupService).removeProfessorFromGroup("1", "principal");
    }
}

