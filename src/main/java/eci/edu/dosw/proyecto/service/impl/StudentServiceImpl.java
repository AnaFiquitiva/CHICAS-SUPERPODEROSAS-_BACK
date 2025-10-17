package eci.edu.dosw.proyecto.service.impl;


import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.repository.StudentRepository;
import eci.edu.dosw.proyecto.service.interfaces.StudentService;
import eci.edu.dosw.proyecto.utils.StudentMapper;
import eci.edu.dosw.proyecto.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que implementa la lógica de negocio para la gestión de estudiantes:
 * creación, consulta, actualización parcial o completa y eliminación.
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public StudentDTO createStudent(StudentCreateDTO studentCreateDTO) {
        studentRepository.findByStudentCode(studentCreateDTO.getStudentCode())
                .ifPresent(s -> { throw new CustomException("Ya existe un estudiante con el mismo código."); });

        studentRepository.findByEmail(studentCreateDTO.getInstitutionalEmail())
                .ifPresent(s -> { throw new CustomException("Ya existe un estudiante con el mismo correo institucional."); });

        Student student = StudentMapper.toEntity(studentCreateDTO);
        studentRepository.save(student);
        return StudentMapper.toDTO(student);
    }

    @Override
    public StudentDTO getStudentByCode(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new CustomException("Estudiante no encontrado."));
        return StudentMapper.toDTO(student);
    }

    @Override
    public List<StudentDTO> getAllStudents(String name, String code, String program) {
        return studentRepository.findAll().stream()
                .filter(s -> name == null || s.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(s -> code == null || s.getStudentCode().equalsIgnoreCase(code))
                .filter(s -> program == null || s.getProgram().equalsIgnoreCase(program))
                .map(StudentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDTO updateStudent(String studentCode, StudentDTO updatedStudent, String role) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new CustomException("Estudiante no encontrado."));

        if ("ADMIN".equalsIgnoreCase(role) || "DECANO".equalsIgnoreCase(role)) {
            student.setName(updatedStudent.getName());
            student.setProgram(updatedStudent.getProgram());
            student.setInstitutionalEmail(updatedStudent.getInstitutionalEmail());
            student.setCurrentSemester(updatedStudent.getCurrentSemester());
            student.setEmail(updatedStudent.getEmail());
        } else if ("ESTUDIANTE".equalsIgnoreCase(role)) {
            student.setEmail(updatedStudent.getEmail());
        } else {
            throw new CustomException("Rol no autorizado para esta operación.");
        }

        studentRepository.save(student);
        return StudentMapper.toDTO(student);
    }

    @Override
    public StudentDTO updateStudentPartial(String studentCode, StudentPartialUpdateDTO partialDTO, String role) {
        if (!"ESTUDIANTE".equalsIgnoreCase(role)) {
            throw new CustomException("Solo los estudiantes pueden actualizar sus datos personales.");
        }

        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new CustomException("Estudiante no encontrado."));

        if (partialDTO.getEmail() != null) student.setEmail(partialDTO.getEmail());
        if (partialDTO.getAddress() != null) student.setAddress(partialDTO.getAddress());
        if (partialDTO.getPhoneNumber() != null) student.setPhoneNumber(partialDTO.getPhoneNumber());

        studentRepository.save(student);
        return StudentMapper.toDTO(student);
    }

    @Override
    public void deleteStudent(String studentCode, String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new CustomException("Solo los administradores pueden eliminar estudiantes.");
        }

        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new CustomException("Estudiante no encontrado."));

        studentRepository.delete(student);
    }
    // Agregar estos métodos a tu StudentServiceImpl existente

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentById(String studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
    }

    @Override
    public List<Student> getStudentsByProgram(String program) {
        return studentRepository.findByProgram(program);
    }

    @Override
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(String studentId, Student student) {
        Student existingStudent = getStudentById(studentId);

        // Actualizar campos
        existingStudent.setName(student.getName());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setProgram(student.getProgram());
        existingStudent.setCurrentSemester(student.getCurrentSemester());
        // Actualizar otros campos según necesidad

        return studentRepository.save(existingStudent);
    }
}
