package eci.edu.dosw.proyecto.serviceTest;

import eci.edu.dosw.proyecto.dto.CourseDetailDto;
import eci.edu.dosw.proyecto.model.Course;
import eci.edu.dosw.proyecto.repository.CourseRepository;
import eci.edu.dosw.proyecto.service.impl.CourseServiceImpl;
import eci.edu.dosw.proyecto.utils.CourseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnCoursesMatchingQuery() {
        String query = "Software";
        Course course = new Course("1", "CS101", "Software Engineering", 3);
        CourseDetailDto dto = new CourseDetailDto("1", "CS101", "Software Engineering", 3, "Prof. Martin Cantor", "Mon 8-10");

        when(courseRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(query, query))
                .thenReturn(List.of(course));
        when(courseMapper.toDto(course, "Martin Cantor", "Mon 8-10"))
                .thenReturn(dto);

        List<CourseDetailDto> result = courseService.searchCourses(query);

        assertEquals(1, result.size());
        assertEquals("Software Engineering", result.get(0).getName());
        verify(courseRepository, times(1))
                .findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(query, query);
    }

    @Test
    void shouldReturnEmptyListIfNoMatch() {
        String query = "Unknown";

        when(courseRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(query, query))
                .thenReturn(List.of());

        List<CourseDetailDto> result = courseService.searchCourses(query);

        assertTrue(result.isEmpty());
        verify(courseRepository, times(1))
                .findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(query, query);
    }
}
