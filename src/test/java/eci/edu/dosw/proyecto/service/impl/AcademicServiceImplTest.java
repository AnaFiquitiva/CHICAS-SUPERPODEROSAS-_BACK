package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.BusinessValidationException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.utils.mappers.AcademicMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcademicServiceImplTest {

    // --- Mocks de todas las dependencias ---
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

    // --- Servicio bajo prueba ---
    @InjectMocks
    private AcademicServiceImpl academicService;

    // --- Datos de prueba comunes ---
    private Faculty testFaculty;
    private Program testProgram;
    private Subject testSubject;
    private FacultyRequest facultyRequest;
    private FacultyResponse facultyResponse;

    @BeforeEach
    void setUp() {
        // Inicializamos objetos que usaremos en varias pruebas
        testFaculty = new Faculty("F-001", "Facultad de Ingeniería", "Descripción...", true, LocalDateTime.now(), null);
        testProgram = new Program("P-001", "Ing. Sistemas", "Descripción...", 160, 10, testFaculty, true, LocalDateTime.now(), null);
        testSubject = new Subject("S-001", "Cálculo I", "Descripción...", 4, testFaculty, true, LocalDateTime.now(), null, null);

        facultyRequest = new FacultyRequest("F-002", "Facultad de Artes", "Nueva facultad");
        facultyResponse = new FacultyResponse("F-002", "Facultad de Artes", "Nueva facultad", true, LocalDateTime.now(), null);
    }

    // === PRUEBAS PARA FACULTAD OPERATIONS ===

    @Test
    @DisplayName("createFaculty - Should save and return faculty response when code is unique")
    void createFaculty_ShouldReturnResponse_WhenCodeIsUnique() {
        // Arrange (Preparar)
        when(facultyRepository.existsByCode(facultyRequest.getCode())).thenReturn(false);
        when(academicMapper.toFaculty(facultyRequest)).thenReturn(testFaculty);
        when(facultyRepository.save(any(Faculty.class))).thenReturn(testFaculty);
        when(academicMapper.toFacultyResponse(testFaculty)).thenReturn(facultyResponse);

        // Act (Actuar)
        FacultyResponse result = academicService.createFaculty(facultyRequest);

        // Assert (Verificar)
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("F-002");
        verify(facultyRepository).existsByCode("F-002");
        verify(facultyRepository).save(any(Faculty.class));
        verify(academicMapper).toFacultyResponse(testFaculty);
    }

    @Test
    @DisplayName("createFaculty - Should throw exception when faculty code already exists")
    void createFaculty_ShouldThrowException_WhenCodeExists() {
        // Arrange
        when(facultyRepository.existsByCode(facultyRequest.getCode())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> academicService.createFaculty(facultyRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe una facultad con el código: " + facultyRequest.getCode());

        // Verificamos que nunca se llamó al método save
        verify(facultyRepository, never()).save(any(Faculty.class));
    }

    @Test
    @DisplayName("getFacultyById - Should return faculty when found")
    void getFacultyById_ShouldReturnFaculty_WhenFound() {
        // Arrange
        when(facultyRepository.findById(testFaculty.getId())).thenReturn(Optional.of(testFaculty));
        when(academicMapper.toFacultyResponse(testFaculty)).thenReturn(facultyResponse);

        // Act
        FacultyResponse result = academicService.getFacultyById(testFaculty.getId());

        // Assert
        assertThat(result).isNotNull();
        verify(facultyRepository).findById(testFaculty.getId());
    }

    @Test
    @DisplayName("getFacultyById - Should throw exception when not found")
    void getFacultyById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(facultyRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> academicService.getFacultyById("invalid-id"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Facultad no encontrada");
    }

    @Test
    @DisplayName("deactivateFaculty - Should deactivate faculty when it has no active programs")
    void deactivateFaculty_ShouldDeactivate_WhenNoActivePrograms() {
        // Arrange
        when(facultyRepository.findById(testFaculty.getId())).thenReturn(Optional.of(testFaculty));
        when(programRepository.findByFacultyAndActiveTrue(testFaculty)).thenReturn(List.of()); // Lista vacía

        // Act
        academicService.deactivateFaculty(testFaculty.getId());

        // Assert
        // Verificamos que se guardó la facultad con active en false
        verify(facultyRepository).save(argThat(faculty -> !faculty.isActive()));
    }

    @Test
    @DisplayName("deactivateFaculty - Should throw exception when faculty has active programs")
    void deactivateFaculty_ShouldThrowException_WhenHasActivePrograms() {
        // Arrange
        when(facultyRepository.findById(testFaculty.getId())).thenReturn(Optional.of(testFaculty));
        when(programRepository.findByFacultyAndActiveTrue(testFaculty)).thenReturn(List.of(testProgram)); // Lista con un programa

        // Act & Assert
        assertThatThrownBy(() -> academicService.deactivateFaculty(testFaculty.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No se puede desactivar una facultad con programas activos");

        verify(facultyRepository, never()).save(any());
    }

    // === PRUEBAS PARA SUBJECT OPERATIONS ===

    @Test
    @DisplayName("validatePrerequisites - Should return true when subject has no prerequisites")
    void validatePrerequisites_ShouldReturnTrue_WhenNoPrerequisites() {
        // Arrange
        String studentId = "S-123";
        testSubject.setPrerequisites(List.of()); // Sin prerrequisitos

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(new Student()));
        when(subjectRepository.findById(testSubject.getId())).thenReturn(Optional.of(testSubject));

        // Act
        boolean result = academicService.validatePrerequisites(studentId, testSubject.getId());

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("validatePrerequisites - Should return true when all prerequisites are approved")
    void validatePrerequisites_ShouldReturnTrue_WhenAllPrerequisitesApproved() {
        // Arrange
        String studentId = "S-123";
        Subject prerequisite = new Subject("S-002", "Física I", "...", 3, testFaculty, true, null, null, null);
        testSubject.setPrerequisites(List.of(prerequisite));

        SubjectProgress approvedProgress = new SubjectProgress();
        approvedProgress.setSubject(prerequisite);
        approvedProgress.setStatus("APPROVED");

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(new Student()));
        when(subjectRepository.findById(testSubject.getId())).thenReturn(Optional.of(testSubject));
        when(subjectProgressRepository.findByStudentIdAndStatus(studentId, "APPROVED"))
                .thenReturn(List.of(approvedProgress));

        // Act
        boolean result = academicService.validatePrerequisites(studentId, testSubject.getId());

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("validatePrerequisites - Should return false when a prerequisite is not approved")
    void validatePrerequisites_ShouldReturnFalse_WhenPrerequisiteNotApproved() {
        // Arrange
        String studentId = "S-123";
        Subject prerequisite = new Subject("S-002", "Física I", "...", 3, testFaculty, true, null, null, null);
        testSubject.setPrerequisites(List.of(prerequisite));

        // El estudiante no tiene ninguna materia aprobada
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(new Student()));
        when(subjectRepository.findById(testSubject.getId())).thenReturn(Optional.of(testSubject));
        when(subjectProgressRepository.findByStudentIdAndStatus(studentId, "APPROVED"))
                .thenReturn(List.of()); // Lista vacía

        // Act
        boolean result = academicService.validatePrerequisites(studentId, testSubject.getId());

        // Assert
        assertThat(result).isFalse();
    }

    // === PRUEBAS PARA ACADEMIC PERIOD OPERATIONS ===

    @Test
    @DisplayName("getCurrentAcademicPeriod - Should return active period")
    void getCurrentAcademicPeriod_ShouldReturnActivePeriod() {
        // Arrange
        AcademicPeriod period = new AcademicPeriod("P-2024-1", "Periodo 2024-1", "Desc", LocalDateTime.now().minusDays(10), LocalDateTime.now().plusDays(80), true, LocalDateTime.now());
        when(academicPeriodRepository.findActivePeriodByDate(any(LocalDateTime.class))).thenReturn(Optional.of(period));

        // Act
        AcademicPeriodResponse result = academicService.getCurrentAcademicPeriod();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("P-2024-1");
        assertThat(result.getName()).isEqualTo("Periodo 2024-1");
    }

    @Test
    @DisplayName("getCurrentAcademicPeriod - Should throw BusinessValidationException when no active period")
    void getCurrentAcademicPeriod_ShouldThrowException_WhenNoActivePeriod() {
        // Arrange
        when(academicPeriodRepository.findActivePeriodByDate(any(LocalDateTime.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> academicService.getCurrentAcademicPeriod())
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("No hay período académico activo");
    }

    @Test
    @DisplayName("isRequestPeriodActive - Should return true if a specific faculty config is active")
    void isRequestPeriodActive_ShouldReturnTrue_WhenFacultyConfigIsActive() {
        // Arrange
        String facultyId = "F-001";
        AcademicPeriodConfig config = new AcademicPeriodConfig(); // No es necesario llenarlo todo
        when(academicPeriodConfigRepository.findActiveConfigByFacultyAndDate(eq(facultyId), any(LocalDateTime.class)))
                .thenReturn(Optional.of(config));

        // Act
        boolean result = academicService.isRequestPeriodActive(facultyId);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isRequestPeriodActive - Should return true if a global config is active")
    void isRequestPeriodActive_ShouldReturnTrue_WhenGlobalConfigIsActive() {
        // Arrange
        String facultyId = "F-999"; // Una facultad sin config específica
        AcademicPeriodConfig globalConfig = new AcademicPeriodConfig();
        globalConfig.setFaculty(null); // Config global

        when(academicPeriodConfigRepository.findActiveConfigByFacultyAndDate(eq(facultyId), any(LocalDateTime.class)))
                .thenReturn(Optional.empty()); // No hay config específica
        when(academicPeriodConfigRepository.findActiveConfigsByDate(any(LocalDateTime.class)))
                .thenReturn(List.of(globalConfig)); // Pero sí hay una global

        // Act
        boolean result = academicService.isRequestPeriodActive(facultyId);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isRequestPeriodActive - Should return false if no config is active")
    void isRequestPeriodActive_ShouldReturnFalse_WhenNoConfigIsActive() {
        // Arrange
        String facultyId = "F-999";
        when(academicPeriodConfigRepository.findActiveConfigByFacultyAndDate(eq(facultyId), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(academicPeriodConfigRepository.findActiveConfigsByDate(any(LocalDateTime.class)))
                .thenReturn(List.of()); // No hay configs activas

        // Act
        boolean result = academicService.isRequestPeriodActive(facultyId);

        // Assert
        assertThat(result).isFalse();
    }
}