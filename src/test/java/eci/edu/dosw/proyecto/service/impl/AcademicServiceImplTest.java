// java
package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.FacultyRequest;
import eci.edu.dosw.proyecto.dto.FacultyResponse;
import eci.edu.dosw.proyecto.exception.BusinessValidationException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.utils.mappers.AcademicMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcademicServiceImplTest {

    @Mock
    private FacultyRepository facultyRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ProgramRepository programRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private SubjectProgressRepository subjectProgressRepository;
    @Mock
    private AcademicPeriodRepository academicPeriodRepository;
    @Mock
    private AcademicPeriodConfigRepository academicPeriodConfigRepository;
    @Mock
    private AcademicMapper academicMapper;

    private AcademicServiceImpl academicService;

    @BeforeEach
    void setUp() {
        academicService = new AcademicServiceImpl(
                facultyRepository,
                groupRepository,
                programRepository,
                subjectRepository,
                studentRepository,
                subjectProgressRepository,
                academicPeriodRepository,
                academicPeriodConfigRepository,
                academicMapper
        );
    }

    @Test
    void createFaculty_success() {
        FacultyRequest req = new FacultyRequest();
        req.setCode("FAC1");
        req.setName("Facultad 1");
        Faculty mapped = new Faculty();
        mapped.setCode("FAC1");
        mapped.setName("Facultad 1");

        Faculty saved = new Faculty();
        saved.setId("f1");
        saved.setCode("FAC1");
        saved.setName("Facultad 1");
        saved.setActive(true);

        FacultyResponse resp = new FacultyResponse();
        resp.setId("f1");
        resp.setCode("FAC1");
        resp.setName("Facultad 1");

        when(facultyRepository.existsByCode("FAC1")).thenReturn(false);
        when(academicMapper.toFaculty(req)).thenReturn(mapped);
        when(facultyRepository.save(mapped)).thenReturn(saved);
        when(academicMapper.toFacultyResponse(saved)).thenReturn(resp);

        FacultyResponse result = academicService.createFaculty(req);

        assertNotNull(result);
        assertEquals("f1", result.getId());
        verify(facultyRepository).save(mapped);
    }

    @Test
    void createFaculty_codeExists_throws() {
        FacultyRequest req = new FacultyRequest();
        req.setCode("FAC1");

        when(facultyRepository.existsByCode("FAC1")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> academicService.createFaculty(req));
        assertTrue(ex.getMessage().contains("Ya existe una facultad"));
    }

    @Test
    void validatePrerequisites_noPrereqs_returnsTrue() {
        String studentId = "s1";
        String subjectId = "sub1";

        Student student = new Student();
        student.setId(studentId);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setPrerequisites(List.of());
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        boolean ok = academicService.validatePrerequisites(studentId, subjectId);
        assertTrue(ok);
    }

    @Test
    void validatePrerequisites_allApproved_returnsTrue() {
        String studentId = "s1";
        String subjectId = "sub1";
        String prereqId = "p1";

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(new Student()));

        Subject prereq = new Subject();
        prereq.setId(prereqId);
        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setPrerequisites(List.of(prereq));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        SubjectProgress progress = new SubjectProgress();
        Subject progSub = new Subject();
        progSub.setId(prereqId);
        progress.setSubject(progSub);
        when(subjectProgressRepository.findByStudentIdAndStatus(studentId, "APPROVED"))
                .thenReturn(List.of(progress));

        assertTrue(academicService.validatePrerequisites(studentId, subjectId));
    }

    @Test
    void validatePrerequisites_notAllApproved_returnsFalse() {
        String studentId = "s1";
        String subjectId = "sub1";
        String prereqId = "p1";

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(new Student()));

        Subject prereq = new Subject();
        prereq.setId(prereqId);
        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setPrerequisites(List.of(prereq));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        // no approved progresses
        when(subjectProgressRepository.findByStudentIdAndStatus(studentId, "APPROVED"))
                .thenReturn(List.of());

        assertFalse(academicService.validatePrerequisites(studentId, subjectId));
    }

    @Test
    void getCurrentAcademicPeriod_success() {
        AcademicPeriod period = new AcademicPeriod();
        period.setId("ap1");
        period.setName("2025-1");
        period.setDescription("Periodo activo");
        period.setStartDate(LocalDateTime.now().minusDays(1));
        period.setEndDate(LocalDateTime.now().plusDays(30));
        period.setActive(true);
        period.setCreatedAt(LocalDateTime.now().minusDays(10));

        when(academicPeriodRepository.findActivePeriodByDate(any(LocalDateTime.class)))
                .thenReturn(Optional.of(period));

        var response = academicService.getCurrentAcademicPeriod();
        assertNotNull(response);
        assertEquals("2025-1", response.getName());
        assertEquals("ap1", response.getId());
    }

    @Test
    void getCurrentAcademicPeriod_noActive_throws() {
        when(academicPeriodRepository.findActivePeriodByDate(any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(BusinessValidationException.class, () -> academicService.getCurrentAcademicPeriod());
    }

    @Test
    void isRequestPeriodActive_facultySpecific_true() {
        String facultyId = "fac1";
        when(academicPeriodConfigRepository.findActiveConfigByFacultyAndDate(eq(facultyId), any(LocalDateTime.class)))
                .thenReturn(Optional.of(new AcademicPeriodConfig()));

        assertTrue(academicService.isRequestPeriodActive(facultyId));
    }

    @Test
    void isRequestPeriodActive_global_true_and_false() {
        String facultyId = "fac1";
        // case global true
        when(academicPeriodConfigRepository.findActiveConfigByFacultyAndDate(eq(facultyId), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        AcademicPeriodConfig cfgGlobal = new AcademicPeriodConfig();
        cfgGlobal.setFaculty(null);
        when(academicPeriodConfigRepository.findActiveConfigsByDate(any(LocalDateTime.class)))
                .thenReturn(List.of(cfgGlobal));

        assertTrue(academicService.isRequestPeriodActive(facultyId));

        // case global false
        AcademicPeriodConfig cfgWithFaculty = new AcademicPeriodConfig();
        cfgWithFaculty.setFaculty(new Faculty());
        when(academicPeriodConfigRepository.findActiveConfigsByDate(any(LocalDateTime.class)))
                .thenReturn(List.of(cfgWithFaculty));

        assertFalse(academicService.isRequestPeriodActive(facultyId));
    }
}