package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.WaitingListEntryDTO;
import eci.edu.dosw.proyecto.exception.ForbiddenException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.Faculty;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.model.WaitingListEntry;
import eci.edu.dosw.proyecto.repository.GroupRepository;
import eci.edu.dosw.proyecto.repository.StudentRepository;
import eci.edu.dosw.proyecto.repository.WaitingListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la funcionalidad:
 * "Consultar Lista de Espera de un Grupo"
 *
 * Cada prueba indica explícitamente el criterio de aceptación que valida.
 */
class WaitingListServiceImplTest {

    @Mock
    private WaitingListRepository waitingListRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private WaitingListServiceImpl waitingListService;

    private Group group;
    private Student student;
    private WaitingListEntry entry;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        group = new Group();
        group.setId("G1");
        group.setFaculty(Faculty.SYSTEMS_ENGINEERING);

        student = new Student();
        student.setId("S1");
        student.setStudentCode("2025001");
        student.setName("Valeria Aguilar");
        student.setProgram("Ingeniería de Sistemas");
        student.setCurrentSemester(6);

        entry = new WaitingListEntry();
        entry.setId("E1");
        entry.setGroupId("G1");
        entry.setStudentId("S1");
        entry.setRequestDate(LocalDateTime.now().minusDays(1));
    }

    /**
     *  Criterio de aceptación 1:
     * "Dado que un usuario con rol de decanatura o administrador consulta la lista de espera de un grupo lleno,
     * cuando el grupo tiene estudiantes en espera, entonces el sistema muestra la lista de estudiantes ordenada
     * por fecha y hora de solicitud (de la más antigua a la más reciente), incluyendo su código y nombre."
     */
    @Test
    void testGetWaitingListByGroup_AsAdministrator_Success() {
        when(groupRepository.findById("G1")).thenReturn(Optional.of(group));
        when(waitingListRepository.findByGroupIdOrderByRequestDateAsc("G1"))
                .thenReturn(List.of(entry));
        when(studentRepository.findById("S1")).thenReturn(Optional.of(student));

        List<WaitingListEntryDTO> result = waitingListService.getWaitingListByGroup(
                "G1", "ADMINISTRATOR", "Ingeniería de Sistemas");

        assertEquals(1, result.size());
        assertEquals("Valeria Aguilar", result.get(0).getStudentName());
        verify(groupRepository, times(1)).findById("G1");
    }

    /**
     *  Criterio de aceptación adicional (derivado del comportamiento esperado):
     * "Dado que un usuario con rol de decanatura o administrador consulta la lista de espera de un grupo,
     * cuando el grupo no tiene una lista de espera activa,
     * entonces el sistema muestra una lista vacía o un mensaje indicando que no hay estudiantes en espera."
     *
     * (Este criterio puede estar cubierto implícitamente cuando la lista de espera es vacía,
     * por lo que no requiere excepción sino retorno vacío.)
     */
    @Test
    void testGetWaitingListByGroup_EmptyList_ShouldReturnEmpty() {
        when(groupRepository.findById("G1")).thenReturn(Optional.of(group));
        when(waitingListRepository.findByGroupIdOrderByRequestDateAsc("G1"))
                .thenReturn(List.of());

        List<WaitingListEntryDTO> result = waitingListService.getWaitingListByGroup(
                "G1", "ADMINISTRATOR", "Ingeniería de Sistemas");

        assertTrue(result.isEmpty());
    }

    /**
     *  Criterio de aceptación 2 (error - grupo inexistente):
     * "Dado que un usuario autorizado consulta la lista de espera de un grupo que no existe,
     * cuando realiza la consulta, entonces el sistema lanza un error de tipo 'No encontrado'."
     */
    @Test
    void testGetWaitingListByGroup_GroupNotFound() {
        when(groupRepository.findById("G999")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                waitingListService.getWaitingListByGroup("G999", "ADMINISTRATOR", "Ingeniería de Sistemas"));
    }

    /**
     * Criterio de aceptación 3:
     * "Dado que un estudiante intenta acceder a la lista de espera de un grupo,
     * cuando realiza la petición al endpoint correspondiente,
     * entonces el sistema deniega el acceso y devuelve un error de 'Prohibido' (403)."
     */
    @Test
    void testGetWaitingListByGroup_UnauthorizedRole() {
        when(groupRepository.findById("G1")).thenReturn(Optional.of(group));

        assertThrows(ForbiddenException.class, () ->
                waitingListService.getWaitingListByGroup("G1", "STUDENT", "Ingeniería de Sistemas"));
    }

    /**
     *  Criterio de aceptación 4:
     * "Dado que una decanatura de la Facultad A intenta consultar la lista de espera
     * de un grupo perteneciente a la Facultad B,
     * cuando realiza la consulta, entonces el sistema deniega el acceso
     * y devuelve un error de 'Prohibido' (403 F)."
     */
    @Test
    void testGetWaitingListByGroup_DifferentFaculty() {
        when(groupRepository.findById("G1")).thenReturn(Optional.of(group));

        assertThrows(ForbiddenException.class, () ->
                waitingListService.getWaitingListByGroup("G1", "DEAN", "Ingeniería Civil"));
    }
}
