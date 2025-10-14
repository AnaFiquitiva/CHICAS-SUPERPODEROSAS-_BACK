package eci.edu.dosw.proyecto.controller;


import eci.edu.dosw.proyecto.dto.WaitingListEntryDTO;
import eci.edu.dosw.proyecto.service.interfaces.WaitingListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el controlador REST de la lista de espera.
 */
@ExtendWith(MockitoExtension.class)
class WaitingListControllerTest {

    @Mock
    private WaitingListService waitingListService;

    @InjectMocks
    private WaitingListController waitingListController;

    private List<WaitingListEntryDTO> mockEntries;

    @BeforeEach
    void setUp() {
        mockEntries = List.of(
                new WaitingListEntryDTO("S001", "Juan Pérez", "Ingeniería de Sistemas", 5, "2025-10-12"),
                new WaitingListEntryDTO("S002", "María Gómez", "Ingeniería de Sistemas", 4, "2025-10-13")
        );
    }

    @Test
    void testGetWaitingList_AsAdmin_ShouldReturnList() {
        when(waitingListService.getWaitingListByGroup("G1", "ADMINISTRATOR", "INGENIERIA_SISTEMAS"))
                .thenReturn(mockEntries);

        ResponseEntity<List<WaitingListEntryDTO>> response =
                waitingListController.getWaitingList("G1", "ADMINISTRATOR", "INGENIERIA_SISTEMAS");

        assertEquals(2, response.getBody().size());
        assertEquals("Juan Pérez", response.getBody().get(0).getStudentName());
        verify(waitingListService, times(1)).getWaitingListByGroup("G1", "ADMINISTRATOR", "INGENIERIA_SISTEMAS");
    }

    @Test
    void testGetWaitingList_EmptyList_ShouldReturnEmptyResponse() {
        when(waitingListService.getWaitingListByGroup("G1", "ADMINISTRATOR", "INGENIERIA_SISTEMAS"))
                .thenReturn(List.of());

        ResponseEntity<List<WaitingListEntryDTO>> response =
                waitingListController.getWaitingList("G1", "ADMINISTRATOR", "INGENIERIA_SISTEMAS");

        assertTrue(response.getBody().isEmpty());
    }
}
