package eci.edu.dosw.proyecto.controllerTest;

import eci.edu.dosw.proyecto.controller.CourseController;
import eci.edu.dosw.proyecto.dto.CourseDetailDto;
import eci.edu.dosw.proyecto.service.interfaces.CourseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Test
    void shouldReturnCoursesByQuery() throws Exception {
        CourseDetailDto dto = new CourseDetailDto("123", "CS101", "Software Engineering", 3, "Prof. John", "Mon 8-10");

        Mockito.when(courseService.searchCourses("Software"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/courses/search")
                        .param("query", "Software"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Software Engineering"))
                .andExpect(jsonPath("$[0].professor").value("Prof. John"));
    }

    @Test
    void shouldReturnEmptyListIfNoCoursesFound() throws Exception {
        Mockito.when(courseService.searchCourses("Unknown"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/courses/search")
                        .param("query", "Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
