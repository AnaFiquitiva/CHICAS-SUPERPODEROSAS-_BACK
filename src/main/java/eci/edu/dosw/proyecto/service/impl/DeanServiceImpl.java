package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.DeanRequest;
import eci.edu.dosw.proyecto.dto.DeanResponse;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.Dean;
import eci.edu.dosw.proyecto.model.Faculty;
import eci.edu.dosw.proyecto.model.User;
import eci.edu.dosw.proyecto.repository.DeanRepository;
import eci.edu.dosw.proyecto.repository.FacultyRepository;
import eci.edu.dosw.proyecto.repository.UserRepository;
import eci.edu.dosw.proyecto.service.interfaces.DeanService;
import eci.edu.dosw.proyecto.utils.mappers.DeanProfessorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeanServiceImpl implements DeanService {

    private final DeanRepository deanRepository;
    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;
    private final DeanProfessorMapper deanMapper;

    @Override
    @Transactional
    public DeanResponse createDean(DeanRequest deanRequest) {
        if (deanRepository.existsByInstitutionalEmail(deanRequest.getInstitutionalEmail())) {
            throw new RuntimeException("Ya existe un decano con ese correo institucional");
        }

        Faculty faculty = facultyRepository.findById(deanRequest.getFacultyId())
                .orElseThrow(() -> new NotFoundException("Facultad", deanRequest.getFacultyId()));

        Dean dean = deanMapper.toDean(deanRequest);
        dean.setFaculty(faculty);
        dean.setActive(true);
        dean.setCreatedAt(LocalDateTime.now());

        // Crear usuario asociado
        User user = User.builder()
                .username(deanRequest.getInstitutionalEmail())
                .email(deanRequest.getInstitutionalEmail())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);
        dean.setUser(savedUser);

        Dean savedDean = deanRepository.save(dean);
        return deanMapper.toDeanResponse(savedDean);
    }

    @Override
    public DeanResponse getDeanById(String id) {
        Dean dean = deanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Decano", id));
        return deanMapper.toDeanResponse(dean);
    }

    @Override
    public DeanResponse getDeanByEmail(String email) {
        Dean dean = deanRepository.findByInstitutionalEmail(email)
                .orElseThrow(() -> new NotFoundException("Decano", email));
        return deanMapper.toDeanResponse(dean);
    }

    @Override
    @Transactional
    public DeanResponse updateDean(String deanId, DeanRequest deanRequest) {
        Dean dean = deanRepository.findById(deanId)
                .orElseThrow(() -> new NotFoundException("Decano", deanId));

        if (!dean.getInstitutionalEmail().equals(deanRequest.getInstitutionalEmail()) &&
                deanRepository.existsByInstitutionalEmail(deanRequest.getInstitutionalEmail())) {
            throw new RuntimeException("Ya existe un decano con ese correo institucional");
        }

        Faculty faculty = facultyRepository.findById(deanRequest.getFacultyId())
                .orElseThrow(() -> new NotFoundException("Facultad", deanRequest.getFacultyId()));

        dean.setFirstName(deanRequest.getFirstName());
        dean.setLastName(deanRequest.getLastName());
        dean.setInstitutionalEmail(deanRequest.getInstitutionalEmail());
        dean.setFaculty(faculty);
        dean.setUpdatedAt(LocalDateTime.now());

        Dean updatedDean = deanRepository.save(dean);
        return deanMapper.toDeanResponse(updatedDean);
    }

    @Override
    @Transactional
    public void deactivateDean(String deanId) {
        Dean dean = deanRepository.findById(deanId)
                .orElseThrow(() -> new NotFoundException("Decano", deanId));

        dean.setActive(false);
        dean.setUpdatedAt(LocalDateTime.now());
        deanRepository.save(dean);

        // Desactivar usuario
        User user = dean.getUser();
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public List<DeanResponse> getDeansByFaculty(String facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new NotFoundException("Facultad", facultyId));
        List<Dean> deans = deanRepository.findByFacultyAndActiveTrue(faculty);
        return deanMapper.toDeanResponseList(deans);
    }

    @Override
    public List<DeanResponse> getAllActiveDeans() {
        List<Dean> deans = deanRepository.findByActiveTrue();
        return deanMapper.toDeanResponseList(deans);
    }

    @Override
    public boolean isDeanOfFaculty(String deanId, String facultyId) {
        Dean dean = deanRepository.findById(deanId)
                .orElseThrow(() -> new NotFoundException("Decano", deanId));
        return dean.getFaculty().getId().equals(facultyId);
    }
}