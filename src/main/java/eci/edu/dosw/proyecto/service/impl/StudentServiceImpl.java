// eci.edu.dosw.proyecto.service.impl.StudentServiceImpl.java
package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.AcademicService;
import eci.edu.dosw.proyecto.service.interfaces.StudentService;
import eci.edu.dosw.proyecto.utils.mappers.ProgressMapper;
import eci.edu.dosw.proyecto.utils.mappers.StudentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ProgramRepository programRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AcademicTrafficLightRepository trafficLightRepository;
    private final StudyPlanProgressRepository studyPlanProgressRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final StudentMapper studentMapper;
    private final SubjectProgressRepository subjectProgressRepository;
    private final SubjectRepository subjectRepository;
    private final ProgressMapper studyPlanProgressMapper;
    private final AcademicService academicService;

    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest studentRequest) {
        if (studentRepository.existsByCode(studentRequest.getCode())) {
            throw new RuntimeException("Ya existe un estudiante con este código");
        }
        if (studentRepository.existsByInstitutionalEmail(studentRequest.getInstitutionalEmail())) {
            throw new RuntimeException("Ya existe un estudiante con este email institucional");
        }

        Program program = programRepository.findById(studentRequest.getProgramId())
                .orElseThrow(() -> new RuntimeException("Programa no encontrado"));

        User user = new User();
        user.setUsername(studentRequest.getCode());
        user.setEmail(studentRequest.getInstitutionalEmail());
        user.setPassword(generateDefaultPassword());
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());

        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new RuntimeException("Rol STUDENT no encontrado"));
        user.setRole(studentRole);
        User savedUser = userRepository.save(user);

        Student student = studentMapper.toStudent(studentRequest);
        student.setProgram(program);
        student.setUser(savedUser);
        student.setActive(true);
        student.setCreatedAt(LocalDateTime.now());

        AcademicTrafficLight defaultTrafficLight = trafficLightRepository.findByColor("GREEN")
                .orElseThrow(() -> new RuntimeException("Semáforo académico por defecto no encontrado"));
        student.setTrafficLight(defaultTrafficLight);

        Student savedStudent = studentRepository.save(student);
        return studentMapper.toStudentResponse(savedStudent);
    }

    @Override
    public StudentResponse getStudentById(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        return studentMapper.toStudentResponse(student);
    }

    @Override
    public StudentResponse getStudentByCode(String code) {
        Student student = studentRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        return studentMapper.toStudentResponse(student);
    }

    @Override
    public StudentResponse getStudentByEmail(String email) {
        Student student = studentRepository.findByInstitutionalEmail(email)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        return studentMapper.toStudentResponse(student);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(String id, StudentRequest studentRequest) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Estudiante", id));

        if (!student.getCode().equals(studentRequest.getCode()) &&
                studentRepository.existsByCode(studentRequest.getCode())) {
            throw new RuntimeException("Ya existe un estudiante con el código: " + studentRequest.getCode());
        }
        if (!student.getInstitutionalEmail().equals(studentRequest.getInstitutionalEmail()) &&
                studentRepository.existsByInstitutionalEmail(studentRequest.getInstitutionalEmail())) {
            throw new RuntimeException("Ya existe un estudiante con el correo institucional: " + studentRequest.getInstitutionalEmail());
        }

        student.setCode(studentRequest.getCode());
        student.setFirstName(studentRequest.getFirstName());
        student.setLastName(studentRequest.getLastName());
        student.setInstitutionalEmail(studentRequest.getInstitutionalEmail());
        student.setProgram(programRepository.findById(studentRequest.getProgramId())
                .orElseThrow(() -> new NotFoundException("Programa", studentRequest.getProgramId())));
        student.setCurrentSemester(studentRequest.getCurrentSemester());
        student.setUpdatedAt(LocalDateTime.now());

        Student updatedStudent = studentRepository.save(student);
        return studentMapper.toStudentResponse(updatedStudent);
    }

    @Override
    @Transactional
    public StudentResponse updatePersonalInfo(String id, StudentUpdateRequest updateRequest) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Estudiante", id));

        student.setPersonalEmail(updateRequest.getPersonalEmail());
        student.setPhone(updateRequest.getPhone());
        student.setAddress(updateRequest.getAddress());
        student.setUpdatedAt(LocalDateTime.now());

        Student updatedStudent = studentRepository.save(student);
        return studentMapper.toStudentResponse(updatedStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Estudiante", id));

        student.setActive(false);
        student.setUpdatedAt(LocalDateTime.now());
        studentRepository.save(student);

        if (student.getUser() != null) {
            User user = student.getUser();
            user.setActive(false);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        List<Student> students = studentRepository.findByActiveTrue();
        return students.stream()
                .map(studentMapper::toStudentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentResponse> getStudentsByProgram(String programId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Programa no encontrado"));
        List<Student> students = studentRepository.findByProgramAndActiveTrue(program);
        return studentMapper.toStudentResponseList(students);
    }

    @Override
    public List<StudentResponse> getStudentsByFaculty(String facultyId) {
        List<Student> students = studentRepository.findByProgramFacultyAndActiveTrue(facultyId);
        return studentMapper.toStudentResponseList(students);
    }

    @Override
    public List<StudentResponse> getStudentsBySemester(Integer semester) {
        List<Student> students = studentRepository.findByCurrentSemesterAndActiveTrue(semester);
        return studentMapper.toStudentResponseList(students);
    }

    @Override
    public List<StudentResponse> searchStudents(String searchTerm) {
        List<Student> students = studentRepository.searchActiveStudents(searchTerm);
        return studentMapper.toStudentResponseList(students);
    }

    @Override
    public StudentBasicResponse getStudentBasicInfo(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        return studentMapper.toStudentBasicResponse(student);
    }

    @Override
    public StudyPlanProgressResponse getStudyPlanProgress(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante", studentId));

        StudyPlanProgress progress = studyPlanProgressRepository.findByStudent(student)
                .orElseGet(() -> createNewStudyPlanProgress(student));

        return studyPlanProgressMapper.toStudyPlanProgressResponse(progress);
    }

    @Override
    public StudentScheduleResponse getStudentSchedule(String studentId, String academicPeriod) {
        // Implementación real usando studentScheduleRepository
        throw new UnsupportedOperationException("Método no implementado");
    }

    @Override
    public AcademicTrafficLightResponse calculateAcademicTrafficLight(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        AcademicTrafficLight trafficLight = student.getTrafficLight();
        AcademicTrafficLightResponse response = new AcademicTrafficLightResponse();
        response.setColor(trafficLight.getColor());
        response.setDescription(trafficLight.getDescription());
        response.setCalculatedAt(LocalDateTime.now());
        return response;
    }

    @Override
    public void updateAcademicTrafficLight(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante", studentId));

        Double gpa = calculateStudentGPA(studentId);
        AcademicTrafficLight trafficLight = trafficLightRepository
                .findByGpaRange(gpa)
                .orElseGet(() -> createDefaultTrafficLight(gpa));

        student.setTrafficLight(trafficLight);
        studentRepository.save(student);
    }

    @Override
    public Double calculateStudentGPA(String studentId) {
        List<SubjectProgress> approvedSubjects = subjectProgressRepository
                .findByStudentIdAndStatus(studentId, "APPROVED");

        if (approvedSubjects.isEmpty()) {
            return 0.0;
        }

        double totalGrade = approvedSubjects.stream()
                .mapToDouble(SubjectProgress::getGrade)
                .filter(grade -> false)
                .sum();

        long validGrades = approvedSubjects.stream()
                .map(SubjectProgress::getGrade)
                .filter(grade -> grade != null && grade >= 3.0 && grade <= 5.0)
                .count();

        return validGrades > 0 ? totalGrade / validGrades : 0.0;
    }

    @Override
    public void updateSubjectProgress(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante", studentId));

        List<Subject> programSubjects = subjectRepository.findByFacultyAndActiveTrue(
                student.getProgram().getFaculty()
        );

        StudyPlanProgress studyProgress = studyPlanProgressRepository.findByStudent(student)
                .orElseGet(() -> createNewStudyPlanProgress(student));

        for (Subject subject : programSubjects) {
            updateSubjectStatus(studentId, subject, studyProgress);
        }

        studyPlanProgressRepository.save(studyProgress);
    }

    @Override
    public boolean hasApprovedSubject(String studentId, String subjectId) {
        Optional<SubjectProgress> approved = subjectProgressRepository.findByStudentIdAndSubjectId(studentId, subjectId);
        return approved.stream()
                .anyMatch(sp -> sp.getGrade() != null && sp.getGrade() >= 3.0);
    }

    @Override
    public boolean hasFailedSubject(String studentId, String subjectId) {
        Optional<SubjectProgress> failed = subjectProgressRepository.findByStudentIdAndSubjectId(studentId, subjectId);
        return failed.stream()
                .anyMatch(sp -> sp.getGrade() != null && sp.getGrade() < 3.0);
    }

    @Override
    public boolean canRetakeSubject(String studentId, String subjectId) {
        Optional<SubjectProgress> failedAttempts = subjectProgressRepository.findByStudentIdAndSubjectId(studentId, subjectId);
        long failedCount = failedAttempts.stream()
                .filter(sp -> sp.getGrade() != null && sp.getGrade() < 3.0)
                .count();
        return failedCount < 3;
    }

    @Override
    public boolean meetsAcademicRequirements(String studentId, String subjectId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante", studentId));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new NotFoundException("Materia", subjectId));

        if (subject.getPrerequisites() == null || subject.getPrerequisites().isEmpty()) {
            return true;
        }

        List<SubjectProgress> approvedSubjects = subjectProgressRepository.findByStudentIdAndStatus(studentId, "APPROVED");

        return subject.getPrerequisites().stream()
                .allMatch(prereq ->
                        approvedSubjects.stream()
                                .anyMatch(progress -> progress.getSubject().getId().equals(prereq.getId()))
                );
    }

    @Override
    public long countStudentsByProgram(String programId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Programa no encontrado"));
        return studentRepository.countByProgramAndActiveTrue(program);
    }

    @Override
    public long countStudentsByFaculty(String facultyId) {
        return studentRepository.countByProgramFacultyAndActiveTrue(facultyId);
    }

    @Override
    public long countByProgramAndActiveTrue(String programId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new NotFoundException("Programa", programId));
        return studentRepository.countByProgramAndActiveTrue(program);
    }

    @Override
    public long countByProgramFacultyAndActiveTrue(String facultyId) {
        return studentRepository.countByProgramFacultyAndActiveTrue(facultyId);
    }

    // === MÉTODOS AUXILIARES ===

    private StudyPlanProgress createNewStudyPlanProgress(Student student) {
        StudyPlanProgress progress = StudyPlanProgress.builder()
                .student(student)
                .program(student.getProgram())
                .totalCredits(student.getProgram().getTotalCredits())
                .completedCredits(0)
                .progressPercentage(0.0)
                .subjectProgress(new ArrayList<>())
                .calculatedAt(LocalDateTime.now())
                .build();
        return studyPlanProgressRepository.save(progress);
    }

    private void updateSubjectStatus(String studentId, Subject subject, StudyPlanProgress studyProgress) {
        Optional<SubjectProgress> existingProgress = studyProgress.getSubjectProgress().stream()
                .filter(sp -> sp.getSubject().getId().equals(subject.getId()))
                .findFirst();

        SubjectProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
        } else {
            progress = SubjectProgress.builder()
                    .subject(subject)
                    .status("PENDING")
                    .attempt(0)
                    .academicPeriod(getCurrentAcademicPeriod())
                    .build();
            studyProgress.getSubjectProgress().add(progress);
        }

        updateSubjectStatusFromEnrollments(studentId, subject, progress);
    }

    private void updateSubjectStatusFromEnrollments(String studentId, Subject subject, SubjectProgress progress) {
        String currentPeriod = getCurrentAcademicPeriod();
        Optional<StudentSchedule> currentSchedules = studentScheduleRepository.findByStudentIdAndAcademicPeriod(
                studentId, currentPeriod
        );

        boolean isInCurrentSemester = currentSchedules.stream()
                .flatMap(schedule -> schedule.getEnrolledGroups().stream())
                .anyMatch(group -> group.getSubject().getId().equals(subject.getId()));

        if (isInCurrentSemester) {
            progress.setStatus("IN_PROGRESS");
            progress.setGrade(null);
            progress.setCompletedAt(null);
        } else {
            Optional<SubjectProgress> historicalGrades = subjectProgressRepository.findByStudentIdAndSubjectId(
                    studentId, subject.getId()
            );

            Optional<SubjectProgress> latestGrade = historicalGrades.stream()
                    .max((sp1, sp2) -> sp2.getCompletedAt().compareTo(sp1.getCompletedAt()));

            if (latestGrade.isPresent()) {
                Double grade = latestGrade.get().getGrade();
                if (grade != null) {
                    if (grade >= 3.0) {
                        progress.setStatus("APPROVED");
                        progress.setGrade(grade);
                        progress.setCompletedAt(latestGrade.get().getCompletedAt());
                    } else {
                        progress.setStatus("FAILED");
                        progress.setGrade(grade);
                        progress.setCompletedAt(latestGrade.get().getCompletedAt());

                        long failedAttempts = historicalGrades.stream()
                                .filter(sp -> sp.getGrade() != null && sp.getGrade() < 3.0)
                                .count();
                        progress.setAttempt((int) failedAttempts);
                    }
                } else {
                    progress.setStatus("PENDING");
                    progress.setGrade(null);
                    progress.setAttempt(0);
                }
            } else {
                progress.setStatus("PENDING");
                progress.setGrade(null);
                progress.setAttempt(0);
            }
        }
    }

    private String getCurrentAcademicPeriod() {
        return academicService.getCurrentAcademicPeriodName();
    }

    private AcademicTrafficLight createDefaultTrafficLight(Double gpa) {
        String color = "RED";
        String description = "Rendimiento académico bajo";

        if (gpa >= 3.5) {
            color = "GREEN";
            description = "Rendimiento académico excelente";
        } else if (gpa >= 2.5) {
            color = "YELLOW";
            description = "Rendimiento académico regular";
        }

        return AcademicTrafficLight.builder()
                .color(color)
                .description(description)
                .minimumGpa(getMinGpaForColor(color))
                .maximumGpa(getMaxGpaForColor(color))
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    private Double getMinGpaForColor(String color) {
        return switch (color) {
            case "GREEN" -> 3.5;
            case "YELLOW" -> 2.5;
            default -> 0.0;
        };
    }

    private Double getMaxGpaForColor(String color) {
        return switch (color) {
            case "GREEN" -> 5.0;
            case "YELLOW" -> 3.49;
            default -> 2.49;
        };
    }

    private String generateDefaultPassword() {
        return "defaultPassword123";
    }
}