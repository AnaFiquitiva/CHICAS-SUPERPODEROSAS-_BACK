package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.BusinessValidationException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.AcademicService;
import eci.edu.dosw.proyecto.utils.mappers.AcademicMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicServiceImpl implements AcademicService {

    private final FacultyRepository facultyRepository;
    private final GroupRepository groupRepository;
    private final ProgramRepository programRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final SubjectProgressRepository subjectProgressRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final AcademicPeriodConfigRepository academicPeriodConfigRepository;
    private final AcademicTrafficLightRepository academicTrafficLightRepository;
    private final AcademicMapper academicMapper;

    // === FACULTY OPERATIONS ===

    @Override
    @Transactional
    public FacultyResponse createFaculty(FacultyRequest facultyRequest) {
        if (facultyRepository.existsByCode(facultyRequest.getCode())) {
            throw new RuntimeException("Ya existe una facultad con el código: " + facultyRequest.getCode());
        }

        Faculty faculty = academicMapper.toFaculty(facultyRequest);
        faculty.setActive(true);
        faculty.setCreatedAt(LocalDateTime.now());

        Faculty savedFaculty = facultyRepository.save(faculty);
        return academicMapper.toFacultyResponse(savedFaculty);
    }

    @Override
    public FacultyResponse getFacultyById(String id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));
        return academicMapper.toFacultyResponse(faculty);
    }

    @Override
    public FacultyResponse getFacultyByCode(String code) {
        Faculty faculty = facultyRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada con código: " + code));
        return academicMapper.toFacultyResponse(faculty);
    }

    @Override
    public List<FacultyResponse> getAllFaculties() {
        List<Faculty> faculties = facultyRepository.findByActiveTrue();
        return academicMapper.toFacultyResponseList(faculties);
    }

    @Override
    @Transactional
    public FacultyResponse updateFaculty(String facultyId, FacultyRequest facultyRequest) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));

        // Verificar unicidad del código si cambia
        if (!faculty.getCode().equals(facultyRequest.getCode()) &&
                facultyRepository.existsByCode(facultyRequest.getCode())) {
            throw new RuntimeException("Ya existe una facultad con el código: " + facultyRequest.getCode());
        }

        faculty.setCode(facultyRequest.getCode());
        faculty.setName(facultyRequest.getName());
        faculty.setDescription(facultyRequest.getDescription());
        faculty.setUpdatedAt(LocalDateTime.now());

        Faculty updatedFaculty = facultyRepository.save(faculty);
        return academicMapper.toFacultyResponse(updatedFaculty);
    }

    @Override
    @Transactional
    public void deactivateFaculty(String facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));

        // Validar que no tenga programas activos
        long activePrograms = programRepository.findByFacultyAndActiveTrue(faculty).size();
        if (activePrograms > 0) {
            throw new RuntimeException("No se puede desactivar una facultad con programas activos");
        }

        faculty.setActive(false);
        faculty.setUpdatedAt(LocalDateTime.now());
        facultyRepository.save(faculty);
    }

    // === PROGRAM OPERATIONS ===

    @Override
    @Transactional
    public ProgramResponse createProgram(ProgramRequest programRequest) {
        Faculty faculty = facultyRepository.findById(programRequest.getFacultyId())
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));

        if (programRepository.existsByCode(programRequest.getCode())) {
            throw new RuntimeException("Ya existe un programa con el código: " + programRequest.getCode());
        }

        Program program = academicMapper.toProgram(programRequest);
        program.setFaculty(faculty);
        program.setActive(true);
        program.setCreatedAt(LocalDateTime.now());

        Program savedProgram = programRepository.save(program);
        return academicMapper.toProgramResponse(savedProgram);
    }

    @Override
    public ProgramResponse getProgramById(String id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Programa no encontrado"));
        return academicMapper.toProgramResponse(program);
    }

    @Override
    public ProgramResponse getProgramByCode(String code) {
        Program program = programRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Programa no encontrado con código: " + code));
        return academicMapper.toProgramResponse(program);
    }

    @Override
    public List<ProgramResponse> getProgramsByFaculty(String facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));

        List<Program> programs = programRepository.findByFacultyAndActiveTrue(faculty);
        return academicMapper.toProgramResponseList(programs);
    }

    @Override
    public List<ProgramResponse> getAllPrograms() {
        List<Program> programs = programRepository.findByActiveTrue();
        return academicMapper.toProgramResponseList(programs);
    }

    @Override
    @Transactional
    public ProgramResponse updateProgram(String programId, ProgramRequest programRequest) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Programa no encontrado"));

        Faculty faculty = facultyRepository.findById(programRequest.getFacultyId())
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));

        // Verificar unicidad del código si cambia
        if (!program.getCode().equals(programRequest.getCode()) &&
                programRepository.existsByCode(programRequest.getCode())) {
            throw new RuntimeException("Ya existe un programa con el código: " + programRequest.getCode());
        }

        program.setCode(programRequest.getCode());
        program.setName(programRequest.getName());
        program.setDescription(programRequest.getDescription());
        program.setFaculty(faculty);
        program.setTotalCredits(programRequest.getTotalCredits());
        program.setDurationSemesters(programRequest.getDurationSemesters());
        program.setUpdatedAt(LocalDateTime.now());

        Program updatedProgram = programRepository.save(program);
        return academicMapper.toProgramResponse(updatedProgram);
    }

    @Override
    @Transactional
    public void deactivateProgram(String programId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Programa no encontrado"));

        // Validar que no tenga estudiantes activos
        long activeStudents = studentRepository.findByProgramAndActiveTrue(program).size();
        if (activeStudents > 0) {
            throw new RuntimeException("No se puede desactivar un programa con estudiantes activos");
        }

        program.setActive(false);
        program.setUpdatedAt(LocalDateTime.now());
        programRepository.save(program);
    }

    // === SUBJECT OPERATIONS ===

    @Override
    @Transactional
    public SubjectResponse createSubject(SubjectRequest subjectRequest) {
        Faculty faculty = facultyRepository.findById(subjectRequest.getFacultyId())
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));

        if (subjectRepository.existsByCode(subjectRequest.getCode())) {
            throw new RuntimeException("Ya existe una materia con el código: " + subjectRequest.getCode());
        }

        Subject subject = academicMapper.toSubject(subjectRequest);
        subject.setFaculty(faculty);
        subject.setActive(true);
        subject.setCreatedAt(LocalDateTime.now());

        // Asignar prerrequisitos si existen
        if (subjectRequest.getPrerequisiteIds() != null && !subjectRequest.getPrerequisiteIds().isEmpty()) {
            List<Subject> prerequisites = subjectRepository.findAllById(subjectRequest.getPrerequisiteIds());
            if (prerequisites.size() != subjectRequest.getPrerequisiteIds().size()) {
                throw new RuntimeException("Uno o más prerrequisitos no existen");
            }
            subject.setPrerequisites(prerequisites);
        }

        Subject savedSubject = subjectRepository.save(subject);
        return academicMapper.toSubjectResponse(savedSubject);
    }

    @Override
    public SubjectResponse getSubjectById(String id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
        return academicMapper.toSubjectResponse(subject);
    }

    @Override
    public SubjectResponse getSubjectByCode(String code) {
        Subject subject = subjectRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada con código: " + code));
        return academicMapper.toSubjectResponse(subject);
    }

    @Override
    public List<SubjectResponse> getSubjectsByFaculty(String facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));

        List<Subject> subjects = subjectRepository.findByFacultyAndActiveTrue(faculty);
        return academicMapper.toSubjectResponseList(subjects);
    }

    @Override
    public List<SubjectResponse> getAllSubjects() {
        List<Subject> subjects = subjectRepository.findByActiveTrue();
        return academicMapper.toSubjectResponseList(subjects);
    }

    @Override
    public List<SubjectResponse> searchSubjects(String searchTerm) {
        List<Subject> subjects = subjectRepository.searchSubjects(searchTerm);
        return academicMapper.toSubjectResponseList(subjects);
    }

    @Override
    @Transactional
    public SubjectResponse updateSubject(String subjectId, SubjectRequest subjectRequest) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        Faculty faculty = facultyRepository.findById(subjectRequest.getFacultyId())
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));

        // Verificar unicidad del código si cambia
        if (!subject.getCode().equals(subjectRequest.getCode()) &&
                subjectRepository.existsByCode(subjectRequest.getCode())) {
            throw new RuntimeException("Ya existe una materia con el código: " + subjectRequest.getCode());
        }

        subject.setCode(subjectRequest.getCode());
        subject.setName(subjectRequest.getName());
        subject.setDescription(subjectRequest.getDescription());
        subject.setFaculty(faculty);
        subject.setCredits(subjectRequest.getCredits());

        // Actualizar prerrequisitos
        if (subjectRequest.getPrerequisiteIds() != null && !subjectRequest.getPrerequisiteIds().isEmpty()) {
            List<Subject> prerequisites = subjectRepository.findAllById(subjectRequest.getPrerequisiteIds());
            if (prerequisites.size() != subjectRequest.getPrerequisiteIds().size()) {
                throw new RuntimeException("Uno o más prerrequisitos no existen");
            }
            subject.setPrerequisites(prerequisites);
        } else {
            subject.setPrerequisites(List.of());
        }

        subject.setUpdatedAt(LocalDateTime.now());
        Subject updatedSubject = subjectRepository.save(subject);
        return academicMapper.toSubjectResponse(updatedSubject);
    }

    @Override
    @Transactional
    public void deactivateSubject(String subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        // Validar que no tenga grupos activos
        long activeGroups = groupRepository.findBySubjectAndActiveTrue(subject).size();
        if (activeGroups > 0) {
            throw new RuntimeException("No se puede desactivar una materia con grupos activos");
        }

        subject.setActive(false);
        subject.setUpdatedAt(LocalDateTime.now());
        subjectRepository.save(subject);
    }

    @Override
    public boolean validatePrerequisites(String studentId, String subjectId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        // Si no tiene prerrequisitos, está validado
        if (subject.getPrerequisites() == null || subject.getPrerequisites().isEmpty()) {
            return true;
        }

        // Obtener materias aprobadas por el estudiante
        List<SubjectProgress> approvedSubjects = subjectProgressRepository.findByStudentIdAndStatus(studentId, "APPROVED");

        // Verificar que todas las prerrequisitos estén aprobadas
        return subject.getPrerequisites().stream()
                .allMatch(prereq ->
                        approvedSubjects.stream()
                                .anyMatch(progress -> progress.getSubject().getId().equals(prereq.getId()))
                );
    }

    // === ACADEMIC PERIOD OPERATIONS ===
    @Override
    public String getCurrentAcademicPeriodName() {
        return getCurrentAcademicPeriod().getName();
    }

    @Override
    public AcademicPeriodResponse getCurrentAcademicPeriod() {
        return academicPeriodRepository.findActivePeriodByDate(LocalDateTime.now())
                .map(period -> {
                    AcademicPeriodResponse response = new AcademicPeriodResponse();
                    response.setId(period.getId());
                    response.setName(period.getName());
                    response.setDescription(period.getDescription());
                    response.setStartDate(period.getStartDate());
                    response.setEndDate(period.getEndDate());
                    response.setActive(period.isActive());
                    response.setCreatedAt(period.getCreatedAt());
                    return response;
                })
                .orElseThrow(() -> new BusinessValidationException("No hay período académico activo"));
    }

    @Override
    public List<AcademicPeriodResponse> getActiveAcademicPeriods() {
        List<AcademicPeriod> periods = academicPeriodRepository.findCurrentActivePeriods();
        return periods.stream()
                .map(period -> {
                    AcademicPeriodResponse response = new AcademicPeriodResponse();
                    response.setId(period.getId());
                    response.setName(period.getName());
                    response.setDescription(period.getDescription());
                    response.setStartDate(period.getStartDate());
                    response.setEndDate(period.getEndDate());
                    response.setActive(period.isActive());
                    response.setCreatedAt(period.getCreatedAt());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean isRequestPeriodActive(String facultyId) {
        LocalDateTime now = LocalDateTime.now();

        // Buscar configuración específica para la facultad
        var configByFaculty = academicPeriodConfigRepository.findActiveConfigByFacultyAndDate(facultyId, now);
        if (configByFaculty.isPresent()) {
            return true;
        }

        // Buscar configuración global (sin facultad)
        var globalConfigs = academicPeriodConfigRepository.findActiveConfigsByDate(now);
        return globalConfigs.stream()
                .anyMatch(config -> config.getFaculty() == null);
    }
}