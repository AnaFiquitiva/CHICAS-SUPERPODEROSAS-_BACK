package eci.edu.dosw.proyecto.service;

import eci.edu.dosw.proyecto.dto.StudentDTO;
import eci.edu.dosw.proyecto.dto.StudentCreateDTO;
import eci.edu.dosw.proyecto.dto.StudentPartialUpdateDTO;
import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.repository.StudentRepository;
import eci.edu.dosw.proyecto.mapper.StudentMapper;
import eci.edu.dosw.proyecto.exception.CustomException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio que contiene la lógica de negocio
 * para el registro, consulta, actualización y eliminación de estudiantes.
 *
 *
 */
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public StudentDTO createStudent(StudentCreateDTO studentDTO) {
        Optional<Student> existingByCode = studentRepository.findByStudentCode(studentDTO.getStudentCode());
        Optional<Student> existingByEmail = studentRepository.findByEmail(studentDTO.getInstitutionalEmail());

        if (existingByCode.isPresent() || existingByEmail.isPresent()) {
            throw new CustomException("Ya existe un estudiante con el mismo código o correo institucional.");
        }

        Student newStudent = StudentMapper.toEntity(studentDTO);
        studentRepository.save(newStudent);
        return StudentMapper.toDTO(newStudent);
    }

    @Override
    public StudentDTO getStudentByCode(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new CustomException("Estudiante no encontrado."));
        return StudentMapper.toDTO(student);
    }

    @Override
    public List<StudentDTO> getAllStudents(String name, String code, String program) {
        List<Student> students = studentRepository.findAll();

        return students.stream()
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
    public void deleteStudent(String studentCode, String role) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new CustomException("Estudiante no encontrado."));

        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new CustomException("Solo los administradores pueden eliminar estudiantes.");
        }

        studentRepository.delete(student);
    }

    @Override
    public StudentDTO updateStudentPartial(String studentCode, StudentPartialUpdateDTO updatedFields, String role) {
        if (!"ESTUDIANTE".equalsIgnoreCase(role)) {
            throw new CustomException("Solo los estudiantes pueden actualizar sus datos personales.");
        }

        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new CustomException("Estudiante no encontrado."));

        if (updatedFields.getEmail() != null) student.setEmail(updatedFields.getEmail());
        if (updatedFields.getAddress() != null) student.setAddress(updatedFields.getAddress());
        if (updatedFields.getPhoneNumber() != null) student.setPhoneNumber(updatedFields.getPhoneNumber());

        studentRepository.save(student);
        return StudentMapper.toDTO(student);
    }

}
