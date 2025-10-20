package eci.edu.dosw.proyecto.controller;

import static org.junit.jupiter.api.Assertions.*;
import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.service.interfaces.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

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
        group.setCurrentEnrollment(0);
        group.setSubjectId("MAT101");
    }

    // HAPPY PATH: crear grupo exitosamente
    @Test
    void shouldCreateGroupSuccessfully() {
        when(groupService.createGroup(any(Group.class))).thenReturn(group);

        ResponseEntity<Group> response = groupController.createGroup(group);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(group.getGroupCode(), response.getBody().getGroupCode());
        verify(groupService, times(1)).createGroup(any(Group.class));
    }

    // ERROR: código duplicado
    @Test
    void shouldReturnError_whenGroupCodeAlreadyExists() {
        when(groupService.createGroup(any(Group.class)))
                .thenThrow(new BusinessException("Código de grupo ya existente."));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> groupController.createGroup(group)
        );

        assertEquals("Código de grupo ya existente.", ex.getMessage());
        verify(groupService, times(1)).createGroup(any(Group.class));
    }

    // HAPPY PATH: listar todos los grupos
    @Test
    void shouldReturnAllGroupsSuccessfully() {
        when(groupService.getAllGroups()).thenReturn(List.of(group));

        ResponseEntity<List<Group>> response = groupController.getAllGroups();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("G1", response.getBody().get(0).getGroupCode());
        verify(groupService, times(1)).getAllGroups();
    }

    //  ERROR: grupo no encontrado
    @Test
    void shouldThrowException_whenGroupNotFound() {
        when(groupService.getGroupById("999"))
                .thenThrow(new BusinessException("Grupo no encontrado."));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> groupController.getGroupById("999")
        );

        assertEquals("Grupo no encontrado.", ex.getMessage());
        verify(groupService, times(1)).getGroupById("999");
    }

    //  HAPPY PATH: obtener grupo por ID existente
    @Test
    void shouldReturnGroupByIdSuccessfully() {
        when(groupService.getGroupById("1")).thenReturn(group);

        ResponseEntity<Group> response = groupController.getGroupById("1");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("G1", response.getBody().getGroupCode());
        verify(groupService, times(1)).getGroupById("1");
    }
}
