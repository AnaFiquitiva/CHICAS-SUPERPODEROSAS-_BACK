package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.GroupResponse;
import eci.edu.dosw.proyecto.dto.ProfessorRequest;
import eci.edu.dosw.proyecto.dto.ProfessorResponse;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.Faculty;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.model.Professor;
import eci.edu.dosw.proyecto.model.User;
import eci.edu.dosw.proyecto.repository.FacultyRepository;
import eci.edu.dosw.proyecto.repository.GroupRepository;
import eci.edu.dosw.proyecto.repository.ProfessorRepository;
import eci.edu.dosw.proyecto.repository.UserRepository;
import eci.edu.dosw.proyecto.service.interfaces.ProfessorService;
import eci.edu.dosw.proyecto.utils.mappers.DeanProfessorMapper;
import eci.edu.dosw.proyecto.utils.mappers.GroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository professorRepository;
    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;   // <-- INYECTADO
    private final GroupMapper groupMapper;           // <-- INYECTADO
    private final DeanProfessorMapper professorMapper;

    @Override
    @Transactional
    public ProfessorResponse createProfessor(ProfessorRequest professorRequest) {
        if (professorRepository.existsByCode(professorRequest.getCode())) {
            throw new RuntimeException("Ya existe un profesor con el código: " + professorRequest.getCode());
        }
        if (professorRepository.existsByInstitutionalEmail(professorRequest.getInstitutionalEmail())) {
            throw new RuntimeException("Ya existe un profesor con el correo: " + professorRequest.getInstitutionalEmail());
        }

        Faculty faculty = facultyRepository.findById(professorRequest.getFacultyId())
                .orElseThrow(() -> new NotFoundException("Facultad", professorRequest.getFacultyId()));

        Professor professor = professorMapper.toProfessor(professorRequest);
        professor.setFaculty(faculty);
        professor.setActive(true);
        professor.setCreatedAt(LocalDateTime.now());

        User user = User.builder()
                .username(professorRequest.getInstitutionalEmail())
                .email(professorRequest.getInstitutionalEmail())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);
        professor.setUser(savedUser);

        Professor savedProfessor = professorRepository.save(professor);
        return professorMapper.toProfessorResponse(savedProfessor);
    }

    @Override
    public ProfessorResponse getProfessorById(String id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profesor", id));
        return professorMapper.toProfessorResponse(professor);
    }

    @Override
    public ProfessorResponse getProfessorByCode(String code) {
        Professor professor = professorRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Profesor", code));
        return professorMapper.toProfessorResponse(professor);
    }

    @Override
    public ProfessorResponse getProfessorByEmail(String email) {
        Professor professor = professorRepository.findByInstitutionalEmail(email)
                .orElseThrow(() -> new NotFoundException("Profesor", email));
        return professorMapper.toProfessorResponse(professor);
    }

    @Override
    @Transactional
    public ProfessorResponse updateProfessor(String professorId, ProfessorRequest professorRequest) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new NotFoundException("Profesor", professorId));

        if (!professor.getCode().equals(professorRequest.getCode()) &&
                professorRepository.existsByCode(professorRequest.getCode())) {
            throw new RuntimeException("Ya existe un profesor con el código: " + professorRequest.getCode());
        }
        if (!professor.getInstitutionalEmail().equals(professorRequest.getInstitutionalEmail()) &&
                professorRepository.existsByInstitutionalEmail(professorRequest.getInstitutionalEmail())) {
            throw new RuntimeException("Ya existe un profesor con el correo: " + professorRequest.getInstitutionalEmail());
        }

        Faculty faculty = facultyRepository.findById(professorRequest.getFacultyId())
                .orElseThrow(() -> new NotFoundException("Facultad", professorRequest.getFacultyId()));

        professor.setCode(professorRequest.getCode());
        professor.setFirstName(professorRequest.getFirstName());
        professor.setLastName(professorRequest.getLastName());
        professor.setInstitutionalEmail(professorRequest.getInstitutionalEmail());
        professor.setFaculty(faculty);
        professor.setUpdatedAt(LocalDateTime.now());

        Professor updatedProfessor = professorRepository.save(professor);
        return professorMapper.toProfessorResponse(updatedProfessor);
    }

    @Override
    @Transactional
    public void deactivateProfessor(String professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new NotFoundException("Profesor", professorId));

        professor.setActive(false);
        professor.setUpdatedAt(LocalDateTime.now());
        professorRepository.save(professor);

        User user = professor.getUser();
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public List<ProfessorResponse> getProfessorsByFaculty(String facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new NotFoundException("Facultad", facultyId));
        List<Professor> professors = professorRepository.findByFacultyAndActiveTrue(faculty);
        return professorMapper.toProfessorResponseList(professors);
    }

    @Override
    public List<ProfessorResponse> getAllActiveProfessors() {
        List<Professor> professors = professorRepository.findByActiveTrue();
        return professorMapper.toProfessorResponseList(professors);
    }

    @Override
    public List<GroupResponse> getProfessorGroups(String professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new NotFoundException("Profesor", professorId));
        List<Group> groups = groupRepository.findByProfessorAndActiveTrue(professor);
        return groupMapper.toGroupResponseList(groups); // <-- Usa groupMapper
    }
}