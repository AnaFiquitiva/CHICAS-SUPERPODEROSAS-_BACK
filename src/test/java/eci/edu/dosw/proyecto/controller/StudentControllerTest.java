package eci.edu.dosw.proyecto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eci.edu.dosw.proyecto.dto.StudentCreateDTO;
import eci.edu.dosw.proyecto.dto.StudentDTO;
import eci.edu.dosw.proyecto.dto.StudentPartialUpdateDTO;
import eci.edu.dosw.proyecto.exception.CustomException;
import eci.edu.dosw.proyecto.service.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias del controlador StudentController.
 * Evalúa todos los endpoints del CRUD de estudiantes:
 * - Creación
 * - Consulta individual
 * - Listado general
 * - Actualización
 * - Eliminación
 *
 * Utiliza Mockito para simular el servicio sin levantar Spring completo.
 */
@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudentServiceImpl studentService;

    @InjectMocks
    private StudentController studentController;

    private ObjectMapper objectMapper;
    private StudentDTO studentDTO;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
        objectMapper = new ObjectMapper();

        studentDTO = new StudentDTO();
        studentDTO.setStudentCode("A001");
        studentDTO.setName("Valeria Aguilar");
        studentDTO.setProgram("Ingeniería de Sistemas");
        studentDTO.setEmail("valeria@gmail.com");
        studentDTO.setInstitutionalEmail("valeria@escuela.edu.co");
        studentDTO.setCurrentSemester(5);
    }

    /**
     * Verifica la creación de un estudiante con rol ADMIN.
     */
    @Test
    void shouldCreateStudentSuccessfully() throws Exception {
        StudentCreateDTO createDTO = new StudentCreateDTO();
        createDTO.setStudentCode("A001");
        createDTO.setName("Valeria Aguilar");
        createDTO.setProgram("Ingeniería de Sistemas");
        createDTO.setInstitutionalEmail("valeria@escuela.edu.co");
        createDTO.setCurrentSemester(5);

        when(studentService.createStudent(any(StudentCreateDTO.class))).thenReturn(studentDTO);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("role", "ADMIN")
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentCode").value("A001"))
                .andExpect(jsonPath("$.name").value("Valeria Aguilar"));

        verify(studentService, times(1)).createStudent(any(StudentCreateDTO.class));
    }

    /**
     * Verifica la obtención de un estudiante por código.
     */
    @Test
    void shouldGetStudentByCodeSuccessfully() throws Exception {
        when(studentService.getStudentByCode("A001")).thenReturn(studentDTO);

        mockMvc.perform(get("/api/students/A001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Valeria Aguilar"))
                .andExpect(jsonPath("$.program").value("Ingeniería de Sistemas"));

        verify(studentService, times(1)).getStudentByCode("A001");
    }

    /**
     * Verifica la obtención de todos los estudiantes.
     */
    @Test
    void shouldReturnListOfStudents() throws Exception {
        when(studentService.getAllStudents(null, null, null)).thenReturn(List.of(studentDTO));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Valeria Aguilar"));

        verify(studentService, times(1)).getAllStudents(null, null, null);
    }

    /**
     * Verifica la actualización de un estudiante por ADMIN.
     */
    @Test
    void shouldUpdateStudentSuccessfully() throws Exception {
        StudentDTO updatedDTO = new StudentDTO();
        updatedDTO.setName("Valeria Bermúdez");
        updatedDTO.setEmail("vb@gmail.com");

        when(studentService.updateStudent(eq("A001"), any(StudentDTO.class), eq("ADMIN"))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/students/A001")
                        .param("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Valeria Bermúdez"))
                .andExpect(jsonPath("$.email").value("vb@gmail.com"));

        verify(studentService, times(1)).updateStudent(eq("A001"), any(StudentDTO.class), eq("ADMIN"));
    }

    /**
     * Verifica la eliminación de un estudiante por ADMIN.
     */
    @Test
    void shouldDeleteStudentSuccessfully() throws Exception {
        doNothing().when(studentService).deleteStudent("A001", "ADMIN");

        mockMvc.perform(delete("/api/students/A001")
                        .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().string("Estudiante eliminado correctamente."));

        verify(studentService, times(1)).deleteStudent("A001", "ADMIN");
    }
    @Test
    void shouldUpdateStudentPartialEndpointSuccessfully() throws Exception {
        StudentDTO updated = new StudentDTO();
        updated.setEmail("new@mail.com");
        updated.setAddress("Calle 2");
        updated.setPhoneNumber("654321");

        when(studentService.updateStudentPartial(eq("A001"), any(StudentPartialUpdateDTO.class), eq("ESTUDIANTE")))
                .thenReturn(updated);

        mockMvc.perform(patch("/api/students/A001")
                        .param("role", "ESTUDIANTE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@mail.com\",\"address\":\"Calle 2\",\"phoneNumber\":\"654321\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@mail.com"))
                .andExpect(jsonPath("$.address").value("Calle 2"))
                .andExpect(jsonPath("$.phoneNumber").value("654321"));
    }

    /**
     * Verifica que un usuario que no sea ESTUDIANTE no pueda realizar
     * la actualización parcial de sus datos personales.
     * Se espera un estado 400 BAD_REQUEST.
     */
    @Test
    void shouldReturnBadRequestForNonStudentRolePatch() throws Exception {
        // Forzar que el servicio lance CustomException
        when(studentService.updateStudentPartial(eq("A001"), any(StudentPartialUpdateDTO.class), eq("ADMIN")))
                .thenThrow(new CustomException("Solo los estudiantes pueden actualizar sus datos personales."));

        mockMvc.perform(patch("/api/students/A001")
                        .param("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@mail.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Solo los estudiantes pueden actualizar sus datos personales."));
    }



}
