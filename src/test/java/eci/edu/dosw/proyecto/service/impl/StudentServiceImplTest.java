package eci.edu.dosw.proyecto.service.impl;



import eci.edu.dosw.proyecto.dto.StudentCreateDTO;
import eci.edu.dosw.proyecto.dto.StudentDTO;
import eci.edu.dosw.proyecto.dto.StudentPartialUpdateDTO;
import eci.edu.dosw.proyecto.exception.CustomException;
import eci.edu.dosw.proyecto.utils.StudentMapper;
import eci.edu.dosw.proyecto.model.AcademicStatus;
import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para StudentServiceImpl.
 * Evalúa todos los escenarios de negocio de los estudiantes:
 * - Creación
 * - Consulta individual
 * - Actualización según rol
 * - Eliminación
 * - Filtrado por nombre o código
 */
class StudentServiceImplTest {

    private StudentRepository studentRepository;
    private StudentServiceImpl studentService;

    @BeforeEach
    void setup() {
        studentRepository = Mockito.mock(StudentRepository.class);
        studentService = new StudentServiceImpl(studentRepository);
    }

    /**
     * Verifica que un estudiante se cree correctamente cuando no existe previamente.
     */
    @Test
    void shouldCreateStudentSuccessfully() {
        StudentCreateDTO dto = new StudentCreateDTO();
        dto.setStudentCode("A001");
        dto.setName("Valeria Aguilar");
        dto.setInstitutionalEmail("valeria@escuela.edu.co");
        dto.setProgram("Ingeniería de Sistemas");
        dto.setCurrentSemester(5);

        when(studentRepository.findByStudentCode("A001")).thenReturn(Optional.empty());
        when(studentRepository.findByEmail("valeria@escuela.edu.co")).thenReturn(Optional.empty());

        Student saved = StudentMapper.toEntity(dto);
        saved.setStatus(AcademicStatus.BLUE);

        when(studentRepository.save(any(Student.class))).thenReturn(saved);

        StudentDTO result = studentService.createStudent(dto);

        assertNotNull(result);
        assertEquals("A001", result.getStudentCode());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    /**
     * Verifica que se lance excepción si ya existe un estudiante con el mismo código.
     */
    @Test
    void shouldThrowErrorWhenStudentAlreadyExists() {
        StudentCreateDTO dto = new StudentCreateDTO();
        dto.setStudentCode("A001");
        dto.setInstitutionalEmail("valeria@escuela.edu.co");

        Student existing = new Student();
        existing.setStudentCode("A001");
        existing.setStatus(AcademicStatus.BLUE);

        when(studentRepository.findByStudentCode("A001")).thenReturn(Optional.of(existing));
        when(studentRepository.findByEmail("valeria@escuela.edu.co")).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> studentService.createStudent(dto));
        verify(studentRepository, never()).save(any(Student.class));
    }

    /**
     * Verifica la consulta de un estudiante por código correctamente.
     */
    @Test
    void shouldGetStudentByCodeSuccessfully() {
        Student student = new Student();
        student.setStudentCode("A001");
        student.setName("Valeria Aguilar");
        student.setStatus(AcademicStatus.BLUE);

        when(studentRepository.findByStudentCode("A001")).thenReturn(Optional.of(student));

        StudentDTO result = studentService.getStudentByCode("A001");

        assertNotNull(result);
        assertEquals("A001", result.getStudentCode());
        verify(studentRepository, times(1)).findByStudentCode("A001");
    }

    /**
     * Verifica que se lance excepción al buscar un estudiante inexistente.
     */
    @Test
    void shouldThrowErrorWhenStudentNotFound() {
        when(studentRepository.findByStudentCode("A001")).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> studentService.getStudentByCode("A001"));
        verify(studentRepository, times(1)).findByStudentCode("A001");
    }

    /**
     * Verifica que un ADMIN pueda actualizar toda la información de un estudiante.
     */
    @Test
    void shouldUpdateStudentAsAdminSuccessfully() {
        Student existing = new Student();
        existing.setStudentCode("A001");
        existing.setName("Valeria Aguilar");
        existing.setProgram("Sistemas");
        existing.setEmail("valeria@gmail.com");
        existing.setInstitutionalEmail("valeria@escuela.edu.co");
        existing.setCurrentSemester(5);
        existing.setStatus(AcademicStatus.BLUE);

        StudentDTO updateDTO = new StudentDTO();
        updateDTO.setName("Valeria Bermúdez");
        updateDTO.setProgram("Software");
        updateDTO.setInstitutionalEmail("vb@escuela.edu.co");
        updateDTO.setEmail("valeriaB@gmail.com");
        updateDTO.setCurrentSemester(6);

        when(studentRepository.findByStudentCode("A001")).thenReturn(Optional.of(existing));
        when(studentRepository.save(any(Student.class))).thenReturn(existing);

        StudentDTO result = studentService.updateStudent("A001", updateDTO, "ADMIN");

        assertEquals("Valeria Bermúdez", result.getName());
        assertEquals("Software", result.getProgram());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    /**
     * Verifica que un ESTUDIANTE solo pueda actualizar su correo personal.
     */
    @Test
    void shouldUpdateOnlyPersonalEmailWhenRoleIsStudent() {
        Student existing = new Student();
        existing.setStudentCode("A001");
        existing.setEmail("old@mail.com");
        existing.setStatus(AcademicStatus.BLUE);

        StudentDTO updateDTO = new StudentDTO();
        updateDTO.setEmail("new@mail.com");

        when(studentRepository.findByStudentCode("A001")).thenReturn(Optional.of(existing));
        when(studentRepository.save(any(Student.class))).thenReturn(existing);

        StudentDTO result = studentService.updateStudent("A001", updateDTO, "ESTUDIANTE");

        assertEquals("new@mail.com", result.getEmail());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    /**
     * Verifica que se lance excepción si el rol es inválido.
     */
    @Test
    void shouldThrowErrorForInvalidRoleInUpdate() {
        Student existing = new Student();
        existing.setStudentCode("A001");
        existing.setStatus(AcademicStatus.BLUE);

        StudentDTO updateDTO = new StudentDTO();
        updateDTO.setName("Cambio no permitido");

        when(studentRepository.findByStudentCode("A001")).thenReturn(Optional.of(existing));

        assertThrows(CustomException.class, () -> studentService.updateStudent("A001", updateDTO, "GUEST"));
        verify(studentRepository, never()).save(any(Student.class));
    }

    /**
     * Verifica que un ADMIN pueda eliminar un estudiante correctamente.
     */
    @Test
    void shouldDeleteStudentAsAdminSuccessfully() {
        Student existing = new Student();
        existing.setStudentCode("A001");
        existing.setStatus(AcademicStatus.BLUE);

        when(studentRepository.findByStudentCode("A001")).thenReturn(Optional.of(existing));
        doNothing().when(studentRepository).delete(existing);

        studentService.deleteStudent("A001", "ADMIN");

        verify(studentRepository, times(1)).delete(existing);
    }

    /**
     * Verifica que un DECANO o ESTUDIANTE no pueda eliminar un estudiante.
     */
    @Test
    void shouldThrowErrorWhenNonAdminTriesToDelete() {
        Student existing = new Student();
        existing.setStudentCode("A001");
        existing.setStatus(AcademicStatus.BLUE);

        when(studentRepository.findByStudentCode("A001")).thenReturn(Optional.of(existing));

        assertThrows(CustomException.class, () -> studentService.deleteStudent("A001", "DECANO"));
        verify(studentRepository, never()).delete(any(Student.class));
    }

    /**
     * Verifica que se lance excepción al intentar eliminar un estudiante inexistente.
     */
    @Test
    void shouldThrowErrorWhenDeletingNonExistentStudent() {
        when(studentRepository.findByStudentCode("A001")).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> studentService.deleteStudent("A001", "ADMIN"));
        verify(studentRepository, never()).delete(any(Student.class));
    }

    /**
     * Verifica el filtrado de estudiantes por nombre.
     */
    @Test
    void shouldReturnFilteredStudentsList() {
        Student s1 = new Student();
        s1.setName("Valeria Aguilar");
        s1.setProgram("Sistemas");
        s1.setStudentCode("A001");
        s1.setStatus(AcademicStatus.BLUE);

        Student s2 = new Student();
        s2.setName("Laura Gómez");
        s2.setProgram("Industrial");
        s2.setStudentCode("A002");
        s2.setStatus(AcademicStatus.BLUE);

        when(studentRepository.findAll()).thenReturn(List.of(s1, s2));

        List<StudentDTO> results = studentService.getAllStudents("Valeria", null, null);

        assertEquals(1, results.size());
        assertEquals("A001", results.get(0).getStudentCode());
    }
    /**
     * Verifica que un estudiante pueda actualizar solo correo, dirección y teléfono.
     */
    @Test
    void shouldUpdateStudentPartialSuccessfully() {
        Student existing = new Student();
        existing.setStudentCode("A001");
        existing.setEmail("old@mail.com");
        existing.setAddress("Calle 1");
        existing.setPhoneNumber("123456");
        existing.setStatus(AcademicStatus.BLUE);

        StudentPartialUpdateDTO partialDTO = new StudentPartialUpdateDTO();
        partialDTO.setEmail("new@mail.com");
        partialDTO.setAddress("Calle 2");
        partialDTO.setPhoneNumber("654321");

        when(studentRepository.findByStudentCode("A001")).thenReturn(Optional.of(existing));
        when(studentRepository.save(any(Student.class))).thenReturn(existing);

        StudentDTO result = studentService.updateStudentPartial("A001", partialDTO, "ESTUDIANTE");

        assertEquals("new@mail.com", result.getEmail());
        assertEquals("Calle 2", result.getAddress());
        assertEquals("654321", result.getPhoneNumber());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    /**
     * Verifica que un rol que no sea estudiante no pueda actualizar parcialmente.
     */
    @Test
    void shouldThrowErrorForNonStudentRolePartialUpdate() {
        StudentPartialUpdateDTO partialDTO = new StudentPartialUpdateDTO();
        partialDTO.setEmail("new@mail.com");

        assertThrows(CustomException.class,
                () -> studentService.updateStudentPartial("A001", partialDTO, "ADMIN"));
        verify(studentRepository, never()).save(any(Student.class));
    }

}