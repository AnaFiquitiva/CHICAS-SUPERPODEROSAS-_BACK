package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.BusinessValidationException;
import eci.edu.dosw.proyecto.exception.ForbiddenException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.AcademicService;
import eci.edu.dosw.proyecto.service.interfaces.AlertService;
import eci.edu.dosw.proyecto.service.interfaces.WaitingListService;
import eci.edu.dosw.proyecto.utils.mappers.GroupMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private ProfessorRepository professorRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StudentScheduleRepository studentScheduleRepository;
    @Mock
    private AlertRepository alertRepository;
    @Mock
    private GroupMapper groupMapper;
    @Mock
    private SystemConfigServiceImpl systemConfigService;
    @Mock
    private AlertService alertService;
    @Mock
    private WaitingListService waitingListService;
    @Mock
    private AcademicService academicService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupCapacityHistoryRepository groupCapacityHistoryRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    private Group baseGroup;
    private Subject baseSubject;
    private Professor baseProfessor;

    @BeforeEach
    void setup() {
        Faculty faculty = new Faculty();
        faculty.setId("fac1");

        baseSubject = new Subject();
        baseSubject.setId("sub1");
        baseSubject.setName("Programación I");
        baseSubject.setFaculty(faculty);

        baseProfessor = new Professor();
        baseProfessor.setId("prof1");
        baseProfessor.setFaculty(faculty);

        baseGroup = new Group();
        baseGroup.setId("g1");
        baseGroup.setGroupCode("G1");
        baseGroup.setSubject(baseSubject);
        baseGroup.setCurrentEnrollment(0);
        baseGroup.setMaxCapacity(30);
        baseGroup.setActive(true);
        baseGroup.setSchedules(new ArrayList<>());
    }

    // ========== createGroup ==========
    @Test
    void createGroup_success() {
        GroupRequest req = new GroupRequest();
        req.setSubjectId("sub1");
        req.setGroupCode("G1");
        req.setMaxCapacity(30);
        req.setProfessorId("prof1");

        when(subjectRepository.findById("sub1")).thenReturn(Optional.of(baseSubject));
        when(groupRepository.findByGroupCodeAndSubjectAndActiveTrue("G1", baseSubject))
                .thenReturn(Optional.empty());
        when(professorRepository.findById("prof1")).thenReturn(Optional.of(baseProfessor));

        Group mapped = new Group();
        mapped.setGroupCode("G1");
        mapped.setMaxCapacity(30);
        when(groupMapper.toGroup(req)).thenReturn(mapped);

        Group saved = new Group();
        saved.setId("g1");
        saved.setGroupCode("G1");
        saved.setSubject(baseSubject);
        saved.setProfessor(baseProfessor);
        saved.setMaxCapacity(30);
        saved.setCurrentEnrollment(0);

        when(groupRepository.save(any(Group.class))).thenReturn(saved);

        GroupResponse resp = new GroupResponse();
        resp.setId("g1");
        resp.setGroupCode("G1");
        when(groupMapper.toGroupResponse(saved)).thenReturn(resp);

        GroupResponse result = groupService.createGroup(req);

        assertNotNull(result);
        assertEquals("g1", result.getId());
        assertEquals("G1", result.getGroupCode());
        verify(groupRepository).save(any(Group.class));
    }

    @Test
    void createGroup_failsWhenGroupCodeAlreadyExists() {
        GroupRequest req = new GroupRequest();
        req.setSubjectId("sub1");
        req.setGroupCode("G1");
        req.setMaxCapacity(30);

        when(subjectRepository.findById("sub1")).thenReturn(Optional.of(baseSubject));
        when(groupRepository.findByGroupCodeAndSubjectAndActiveTrue("G1", baseSubject))
                .thenReturn(Optional.of(new Group()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> groupService.createGroup(req));
        assertTrue(ex.getMessage().contains("Ya existe un grupo"));
    }

    @Test
    void createGroup_failsWhenCapacityInvalid() {
        GroupRequest req = new GroupRequest();
        req.setSubjectId("sub1");
        req.setGroupCode("G1");
        req.setMaxCapacity(0);

        when(subjectRepository.findById("sub1")).thenReturn(Optional.of(baseSubject));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> groupService.createGroup(req));
        assertTrue(ex.getMessage().contains("La capacidad máxima debe ser mayor a 0"));
    }

    // ========== getGroupById ==========
    @Test
    void getGroupById_success() {
        when(groupRepository.findById("g1")).thenReturn(Optional.of(baseGroup));

        GroupResponse mapped = new GroupResponse();
        mapped.setId("g1");
        mapped.setGroupCode("G1");
        when(groupMapper.toGroupResponse(baseGroup)).thenReturn(mapped);

        GroupResponse resp = groupService.getGroupById("g1");

        assertEquals("g1", resp.getId());
        verify(groupRepository).findById("g1");
    }

    @Test
    void getGroupById_notFound_throws() {
        when(groupRepository.findById("bad")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> groupService.getGroupById("bad"));
        assertTrue(ex.getMessage().contains("Grupo no encontrado"));
    }

    // ========== updateGroup ==========
    @Test
    void updateGroup_success() {
        GroupUpdateRequest req = new GroupUpdateRequest();
        req.setMaxCapacity(40);

        Group existing = new Group();
        existing.setId("g1");
        existing.setCurrentEnrollment(10);
        existing.setMaxCapacity(30);

        when(groupRepository.findById("g1")).thenReturn(Optional.of(existing));

        // simulamos que el mapper aplica los cambios al entity existente
        doAnswer(invocation -> {
            GroupUpdateRequest r = invocation.getArgument(0);
            Group g = invocation.getArgument(1);
            if (r.getMaxCapacity() != null) {
                g.setMaxCapacity(r.getMaxCapacity());
            }
            return null;
        }).when(groupMapper).updateGroupFromRequest(eq(req), eq(existing));

        Group saved = new Group();
        saved.setId("g1");
        saved.setMaxCapacity(40);
        saved.setCurrentEnrollment(10);

        when(groupRepository.save(existing)).thenReturn(saved);

        GroupResponse mapped = new GroupResponse();
        mapped.setId("g1");
        mapped.setGroupCode("G1");
        when(groupMapper.toGroupResponse(saved)).thenReturn(mapped);

        GroupResponse resp = groupService.updateGroup("g1", req);

        assertNotNull(resp);
        assertEquals("g1", resp.getId());
        verify(groupRepository).save(existing);
    }

    @Test
    void updateGroup_failsWhenNewCapacityLowerThanEnrollment() {
        GroupUpdateRequest req = new GroupUpdateRequest();
        req.setMaxCapacity(5); // menor al currentEnrollment

        Group existing = new Group();
        existing.setId("g1");
        existing.setCurrentEnrollment(10);

        when(groupRepository.findById("g1")).thenReturn(Optional.of(existing));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> groupService.updateGroup("g1", req));
        assertTrue(ex.getMessage().contains("La capacidad máxima no puede ser menor"));
    }

    // ========== updateGroupCapacity(Integer newCapacity) ==========
    @Test
    void updateGroupCapacity_simple_success() {
        Group existing = new Group();
        existing.setId("g1");
        existing.setCurrentEnrollment(10);
        existing.setMaxCapacity(20);

        when(groupRepository.findById("g1")).thenReturn(Optional.of(existing));

        Group saved = new Group();
        saved.setId("g1");
        saved.setMaxCapacity(25);
        saved.setCurrentEnrollment(10);
        when(groupRepository.save(existing)).thenReturn(saved);

        GroupResponse mapped = new GroupResponse();
        mapped.setId("g1");
        when(groupMapper.toGroupResponse(saved)).thenReturn(mapped);

        GroupResponse resp = groupService.updateGroupCapacity("g1", 25);

        assertNotNull(resp);
        assertEquals("g1", resp.getId());
        assertEquals(25, saved.getMaxCapacity());
    }

    @Test
    void updateGroupCapacity_simple_failsWhenNewCapacityZeroOrLess() {
        Group existing = new Group();
        existing.setId("g1");
        existing.setCurrentEnrollment(5);
        existing.setMaxCapacity(10);

        when(groupRepository.findById("g1")).thenReturn(Optional.of(existing));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> groupService.updateGroupCapacity("g1", 0));
        assertTrue(ex.getMessage().contains("mayor a 0"));
    }

    @Test
    void updateGroupCapacity_simple_failsWhenNewCapacityLowerThanEnrolled() {
        Group existing = new Group();
        existing.setId("g1");
        existing.setCurrentEnrollment(10);
        existing.setMaxCapacity(20);

        when(groupRepository.findById("g1")).thenReturn(Optional.of(existing));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> groupService.updateGroupCapacity("g1", 5));
        assertTrue(ex.getMessage().contains("no puede ser menor"));
    }

    // ========== deactivateGroup ==========
    @Test
    void deactivateGroup_successWhenNoStudents() {
        Group g = new Group();
        g.setId("g1");
        g.setCurrentEnrollment(0);
        g.setActive(true);

        when(groupRepository.findById("g1")).thenReturn(Optional.of(g));

        groupService.deactivateGroup("g1");

        assertFalse(g.isActive());
        verify(groupRepository).save(g);
    }

    @Test
    void deactivateGroup_failsIfStudentsEnrolled() {
        Group g = new Group();
        g.setId("g1");
        g.setCurrentEnrollment(3);

        when(groupRepository.findById("g1")).thenReturn(Optional.of(g));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> groupService.deactivateGroup("g1"));
        assertTrue(ex.getMessage().contains("No se puede desactivar un grupo con estudiantes inscritos"));
    }

    // ========== getAvailableSpots / hasAvailableSpots ==========
    @Test
    void getAvailableSpots_ok() {
        Group g = new Group();
        g.setId("g1");
        g.setMaxCapacity(30);
        g.setCurrentEnrollment(12);

        when(groupRepository.findById("g1")).thenReturn(Optional.of(g));

        Integer spots = groupService.getAvailableSpots("g1");
        assertEquals(18, spots);
    }

    @Test
    void hasAvailableSpots_true() {
        Group g = new Group();
        g.setId("g1");
        g.setMaxCapacity(30);
        g.setCurrentEnrollment(12);

        when(groupRepository.findById("g1")).thenReturn(Optional.of(g));

        assertTrue(groupService.hasAvailableSpots("g1"));
    }

    // ========== addSchedule ==========
    @Test
    void addSchedule_success() {
        ScheduleRequest req = new ScheduleRequest();
        req.setGroupId("g1");
        req.setDayOfWeek("LUNES");
        req.setStartTime("08:00");
        req.setEndTime("10:00");
        req.setClassroom("A-101");

        Group g = new Group();
        g.setId("g1");
        g.setActive(true);

        when(groupRepository.findById("g1")).thenReturn(Optional.of(g));
        when(scheduleRepository.findByGroupAndGroupActiveTrue(g)).thenReturn(List.of()); // sin conflicto

        Schedule mapped = new Schedule();
        mapped.setId("sch1");
        when(groupMapper.toSchedule(req)).thenReturn(mapped);

        Schedule saved = new Schedule();
        saved.setId("sch1");
        saved.setGroup(g);
        saved.setDayOfWeek("LUNES");
        saved.setStartTime("08:00");
        saved.setEndTime("10:00");
        saved.setClassroom("A-101");

        when(scheduleRepository.save(any(Schedule.class))).thenReturn(saved);

        ScheduleResponse res = new ScheduleResponse();
        res.setId("sch1");
        when(groupMapper.toScheduleResponse(saved)).thenReturn(res);

        ScheduleResponse result = groupService.addSchedule(req);

        assertNotNull(result);
        assertEquals("sch1", result.getId());
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    void addSchedule_conflict_throws() {
        ScheduleRequest req = new ScheduleRequest();
        req.setGroupId("g1");
        req.setDayOfWeek("LUNES");
        req.setStartTime("08:00");
        req.setEndTime("10:00");

        Group g = new Group();
        g.setId("g1");

        Schedule existing = new Schedule();
        existing.setId("schExist");
        existing.setDayOfWeek("LUNES");
        existing.setStartTime("09:00");
        existing.setEndTime("11:00");

        when(groupRepository.findById("g1")).thenReturn(Optional.of(g));
        when(scheduleRepository.findByGroupAndGroupActiveTrue(g)).thenReturn(List.of(existing));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> groupService.addSchedule(req));
        assertTrue(ex.getMessage().contains("se superpone"));
    }

    // ========== enrollStudentInGroup ==========
    @Test
    void enrollStudentInGroup_success() {
        Group g = new Group();
        g.setId("g1");
        g.setMaxCapacity(30);
        g.setCurrentEnrollment(10);

        Student st = new Student();
        st.setId("s1");

        StudentSchedule sched = new StudentSchedule();
        sched.setStudent(st);
        sched.setEnrolledGroups(new ArrayList<>());

        when(groupRepository.findById("g1")).thenReturn(Optional.of(g));
        when(studentRepository.findById("s1")).thenReturn(Optional.of(st));

        when(academicService.getCurrentAcademicPeriodName()).thenReturn("2025-1");
        when(studentScheduleRepository.findByStudentIdAndAcademicPeriod("s1", "2025-1"))
                .thenReturn(Optional.of(sched));

        Group savedGroup = new Group();
        savedGroup.setId("g1");
        savedGroup.setCurrentEnrollment(11);
        savedGroup.setMaxCapacity(30);
        when(groupRepository.save(any(Group.class))).thenReturn(savedGroup);

        GroupResponse mapped = new GroupResponse();
        mapped.setId("g1");
        when(groupMapper.toGroupResponse(savedGroup)).thenReturn(mapped);

        GroupResponse resp = groupService.enrollStudentInGroup("g1", "s1");

        assertNotNull(resp);
        assertEquals("g1", resp.getId());
        assertEquals(1, sched.getEnrolledGroups().size());
        verify(alertService).checkAndCreateGroupCapacityAlert("g1");
    }

    @Test
    void enrollStudentInGroup_noSpots_throws() {
        Group g = new Group();
        g.setId("g1");
        g.setMaxCapacity(10);
        g.setCurrentEnrollment(10);

        when(groupRepository.findById("g1")).thenReturn(Optional.of(g));
        when(studentRepository.findById("s1")).thenReturn(Optional.of(new Student()));

        BusinessValidationException ex = assertThrows(
                BusinessValidationException.class,
                () -> groupService.enrollStudentInGroup("g1", "s1")
        );
        assertTrue(ex.getMessage().contains("No hay cupos disponibles"));
    }

    // ========== unenrollStudentFromGroup ==========
    @Test
    void unenrollStudentFromGroup_success() {
        Group g = new Group();
        g.setId("g1");
        g.setMaxCapacity(20);
        g.setCurrentEnrollment(10);

        Student st = new Student();
        st.setId("s1");

        StudentSchedule sched = new StudentSchedule();
        sched.setStudent(st);

        Group already = new Group();
        already.setId("g1");
        sched.setEnrolledGroups(new ArrayList<>(List.of(already)));

        when(groupRepository.findById("g1")).thenReturn(Optional.of(g));
        when(studentRepository.findById("s1")).thenReturn(Optional.of(st));

        when(academicService.getCurrentAcademicPeriodName()).thenReturn("2025-1");
        when(studentScheduleRepository.findByStudentIdAndAcademicPeriod("s1", "2025-1"))
                .thenReturn(Optional.of(sched));

        Group saved = new Group();
        saved.setId("g1");
        saved.setCurrentEnrollment(9);
        saved.setMaxCapacity(20);
        when(groupRepository.save(any(Group.class))).thenReturn(saved);

        GroupResponse mapped = new GroupResponse();
        mapped.setId("g1");
        when(groupMapper.toGroupResponse(saved)).thenReturn(mapped);

        GroupResponse resp = groupService.unenrollStudentFromGroup("g1", "s1");

        assertNotNull(resp);
        assertEquals("g1", resp.getId());
        assertEquals(0, sched.getEnrolledGroups().size());
        verify(waitingListService).promoteNextStudent("g1");
    }

    // ========== updateGroupCapacity(GroupCapacityUpdateRequest) ==========
    @Test
    void updateGroupCapacity_secure_successByAdmin() {
        // mock usuario autenticado ADMIN
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("adminUser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        User adminUser = new User();
        adminUser.setId("u1");
        adminUser.setUsername("adminUser");
        adminUser.setRole(adminRole);

        when(userRepository.findByUsername("adminUser"))
                .thenReturn(Optional.of(adminUser));

        Group g = new Group();
        g.setId("g1");
        g.setMaxCapacity(20);
        g.setCurrentEnrollment(10);

        when(groupRepository.findById("g1")).thenReturn(Optional.of(g));

        GroupCapacityUpdateRequest req = new GroupCapacityUpdateRequest();
        req.setNewCapacity(25);
        req.setJustification("Ajuste de cupos");

        when(groupCapacityHistoryRepository.save(any(GroupCapacityHistory.class)))
                .thenReturn(new GroupCapacityHistory());

        Group saved = new Group();
        saved.setId("g1");
        saved.setMaxCapacity(25);
        saved.setCurrentEnrollment(10);
        when(groupRepository.save(any(Group.class))).thenReturn(saved);

        GroupResponse mapped = new GroupResponse();
        mapped.setId("g1");
        when(groupMapper.toGroupResponse(saved)).thenReturn(mapped);

        GroupResponse resp = groupService.updateGroupCapacity("g1", req);

        assertEquals("g1", resp.getId());
        assertEquals(25, saved.getMaxCapacity());
        verify(waitingListService).promoteNextStudent("g1");
    }

    @Test
    void updateGroupCapacity_secure_failsIfBelowEnrolled() {
        // mock usuario autenticado ADMIN
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("adminUser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        User adminUser = new User();
        adminUser.setUsername("adminUser");
        adminUser.setRole(adminRole);

        when(userRepository.findByUsername("adminUser"))
                .thenReturn(Optional.of(adminUser));

        Group g = new Group();
        g.setId("g1");
        g.setCurrentEnrollment(15);
        g.setMaxCapacity(20);

        when(groupRepository.findById("g1")).thenReturn(Optional.of(g));

        GroupCapacityUpdateRequest req = new GroupCapacityUpdateRequest();
        req.setNewCapacity(10);
        req.setJustification("Bajar cupo");

        BusinessValidationException ex = assertThrows(
                BusinessValidationException.class,
                () -> groupService.updateGroupCapacity("g1", req)
        );
        assertTrue(ex.getMessage().contains("no puede ser menor"));
    }

    @Test
    void updateGroupCapacity_secure_failsIfNotAuthorized() {
        // mock usuario autenticado con rol NO autorizado
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("teacherUser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        Role role = new Role();
        role.setName("TEACHER");

        User teacherUser = new User();
        teacherUser.setUsername("teacherUser");
        teacherUser.setRole(role);

        when(userRepository.findByUsername("teacherUser"))
                .thenReturn(Optional.of(teacherUser));

        // NO stub de groupRepository.findById(...) aquí,
        // porque el método debe fallar ANTES de leer el grupo.
        GroupCapacityUpdateRequest req = new GroupCapacityUpdateRequest();
        req.setNewCapacity(30);
        req.setJustification("Subir cupo");

        ForbiddenException ex = assertThrows(
                ForbiddenException.class,
                () -> groupService.updateGroupCapacity("g1", req)
        );
        assertTrue(ex.getMessage().contains("Solo los administradores"));
    }

    // ========== assignProfessorToGroup ==========
    @Test
    void assignProfessorToGroup_successByAdminOrDean() {
        // mock usuario autenticado ADMIN
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("adminUser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        User adminUser = new User();
        adminUser.setUsername("adminUser");
        adminUser.setRole(adminRole);

        when(userRepository.findByUsername("adminUser"))
                .thenReturn(Optional.of(adminUser));

        Faculty faculty = new Faculty();
        faculty.setId("fac1");

        Professor prof = new Professor();
        prof.setId("prof1");
        prof.setFaculty(faculty);

        Subject subj = new Subject();
        subj.setId("sub1");
        subj.setFaculty(faculty);

        Group g = new Group();
        g.setId("g1");
        g.setSubject(subj);

        when(groupRepository.findById("g1")).thenReturn(Optional.of(g));
        when(professorRepository.findById("prof1")).thenReturn(Optional.of(prof));

        Group saved = new Group();
        saved.setId("g1");
        saved.setProfessor(prof);
        when(groupRepository.save(any(Group.class))).thenReturn(saved);

        GroupResponse mapped = new GroupResponse();
        mapped.setId("g1");
        when(groupMapper.toGroupResponse(saved)).thenReturn(mapped);

        GroupResponse resp = groupService.assignProfessorToGroup("g1", "prof1");

        assertEquals("g1", resp.getId());
        assertNotNull(saved.getProfessor());
    }

    @Test
    void assignProfessorToGroup_failsDifferentFaculty() {
        // mock usuario autenticado ADMIN
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("adminUser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        User adminUser = new User();
        adminUser.setUsername("adminUser");
        adminUser.setRole(adminRole);

        when(userRepository.findByUsername("adminUser"))
                .thenReturn(Optional.of(adminUser));

        Faculty fac1 = new Faculty();
        fac1.setId("fac1");
        Faculty fac2 = new Faculty();
        fac2.setId("fac2");

        Professor prof = new Professor();
        prof.setId("prof1");
        prof.setFaculty(fac2);

        Subject subj = new Subject();
        subj.setId("sub1");
        subj.setFaculty(fac1);

        Group g = new Group();
        g.setId("g1");
        g.setSubject(subj);

        when(groupRepository.findById("g1")).thenReturn(Optional.of(g));
        when(professorRepository.findById("prof1")).thenReturn(Optional.of(prof));

        BusinessValidationException ex = assertThrows(
                BusinessValidationException.class,
                () -> groupService.assignProfessorToGroup("g1", "prof1")
        );
        assertTrue(ex.getMessage().contains("no pertenece a la facultad"));
    }

    @Test
    void assignProfessorToGroup_forbiddenIfRoleNotAdminOrDean() {
        // mock usuario autenticado TEACHER
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("teacherUser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        Role role = new Role();
        role.setName("TEACHER");

        User teacherUser = new User();
        teacherUser.setUsername("teacherUser");
        teacherUser.setRole(role);

        when(userRepository.findByUsername("teacherUser"))
                .thenReturn(Optional.of(teacherUser));

        // No stubbing de groupRepository.findById ni professorRepository.findById,
        // porque el método debe lanzar ForbiddenException ANTES.
        ForbiddenException ex = assertThrows(
                ForbiddenException.class,
                () -> groupService.assignProfessorToGroup("g1", "prof1")
        );
        assertTrue(ex.getMessage().contains("Solo administradores o decanos"));
    }

    // ========== removeProfessorFromGroup ==========
    @Test
    void removeProfessorFromGroup_successByAdminOrDean() {
        // mock usuario autenticado DEAN
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("deanUser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        Role deanRole = new Role();
        deanRole.setName("DEAN");

        User deanUser = new User();
        deanUser.setUsername("deanUser");
        deanUser.setRole(deanRole);

        when(userRepository.findByUsername("deanUser"))
                .thenReturn(Optional.of(deanUser));

        Group g = new Group();
        g.setId("g1");
        g.setProfessor(new Professor());

        when(groupRepository.findById("g1")).thenReturn(Optional.of(g));

        Group saved = new Group();
        saved.setId("g1");
        saved.setProfessor(null);

        when(groupRepository.save(any(Group.class))).thenReturn(saved);

        GroupResponse mapped = new GroupResponse();
        mapped.setId("g1");
        when(groupMapper.toGroupResponse(saved)).thenReturn(mapped);

        GroupResponse resp = groupService.removeProfessorFromGroup("g1");

        assertEquals("g1", resp.getId());
        assertNull(saved.getProfessor());
    }

    @Test
    void removeProfessorFromGroup_forbiddenIfNotAdminOrDean() {
        // mock usuario autenticado TEACHER
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("teacherUser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        Role role = new Role();
        role.setName("TEACHER");

        User teacherUser = new User();
        teacherUser.setUsername("teacherUser");
        teacherUser.setRole(role);

        when(userRepository.findByUsername("teacherUser"))
                .thenReturn(Optional.of(teacherUser));

        // No stubbing de groupRepository.findById aquí,
        // porque debe fallar por permisos antes.
        ForbiddenException ex = assertThrows(
                ForbiddenException.class,
                () -> groupService.removeProfessorFromGroup("g1")
        );
        assertTrue(ex.getMessage().contains("Solo administradores o decanos"));
    }
}
