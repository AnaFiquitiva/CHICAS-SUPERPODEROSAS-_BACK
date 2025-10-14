package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.GroupCapacityResponseDTO;
import eci.edu.dosw.proyecto.exception.GroupNotFoundException;
import eci.edu.dosw.proyecto.service.interfaces.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    private GroupCapacityResponseDTO mockDTO;

    @BeforeEach
    void setUp() {
        mockDTO = new GroupCapacityResponseDTO("grupo1", 20, 30, 66.67);
    }

    @Test
    void testGetGroupCapacity_Success() {
        when(groupService.getCapacity("grupo1")).thenReturn(mockDTO);
        ResponseEntity<GroupCapacityResponseDTO> response = groupController.getGroupCapacity("grupo1");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockDTO, response.getBody());
    }

    @Test
    void testGetGroupCapacity_GroupNotFound() {
        when(groupService.getCapacity("invalido")).thenThrow(new GroupNotFoundException("Grupo no encontrado"));

        assertThrows(GroupNotFoundException.class, () -> groupController.getGroupCapacity("invalido"));
    }
}
