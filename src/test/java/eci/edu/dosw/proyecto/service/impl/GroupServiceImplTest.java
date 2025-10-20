package eci.edu.dosw.proyecto.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.model.Subject;
import eci.edu.dosw.proyecto.repository.GroupRepository;
import eci.edu.dosw.proyecto.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    private Group group;
    private Subject subject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        subject = new Subject();
        subject.setId("MAT101");
        subject.setName("Matemáticas Básicas");

        group = new Group();
        group.setGroupCode("G1");
        group.setMaxCapacity(30);
        group.setSubjectId("MAT101");
    }

    // HAPPY PATH
    @Test
    void shouldCreateGroupSuccessfully_whenSubjectExistsAndValidData() {
        when(subjectRepository.findById("MAT101")).thenReturn(Optional.of(subject));
        when(groupRepository.findByGroupCode("G1")).thenReturn(Optional.empty());
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Group created = groupService.createGroup(group);

        assertNotNull(created);
        assertEquals("G1", created.getGroupCode());
        assertEquals(0, created.getCurrentEnrollment());
        assertEquals(0, created.getWaitingListCount());
        assertTrue(created.getActive());
        verify(groupRepository).save(any(Group.class));
    }

    //ERROR: Materia inexistente
    @Test
    void shouldThrowException_whenSubjectDoesNotExist() {
        when(subjectRepository.findById("MAT101")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> groupService.createGroup(group));
        assertEquals("Debe asociar una materia existente al grupo.", ex.getMessage());
    }

    // ERROR: Código de grupo duplicado
    @Test
    void shouldThrowException_whenGroupCodeAlreadyExists() {
        when(subjectRepository.findById("MAT101")).thenReturn(Optional.of(subject));
        when(groupRepository.findByGroupCode("G1")).thenReturn(Optional.of(group));

        BusinessException ex = assertThrows(BusinessException.class, () -> groupService.createGroup(group));
        assertEquals("Código de grupo ya existente.", ex.getMessage());
    }

    // ERROR: Cupo máximo menor a 1
    @Test
    void shouldThrowException_whenMaxCapacityInvalid() {
        group.setMaxCapacity(0); // cupo inválido
        when(subjectRepository.findById("MAT101")).thenReturn(Optional.of(subject));
        when(groupRepository.findByGroupCode("G1")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> groupService.createGroup(group));
        assertEquals("El cupo máximo debe ser mayor o igual a 1.", ex.getMessage());
    }

    // ERROR: Materia no asociada (null)
    @Test
    void shouldThrowException_whenSubjectIdIsNull() {
        group.setSubjectId(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> groupService.createGroup(group));
        assertEquals("Debe asociar una materia existente al grupo.", ex.getMessage());
    }
}
