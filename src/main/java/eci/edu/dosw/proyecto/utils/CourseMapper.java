package eci.edu.dosw.proyecto.utils;

import eci.edu.dosw.proyecto.dto.CourseDetailDto;
import eci.edu.dosw.proyecto.model.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public CourseDetailDto toDto(Course course, String professorName, String schedule) {
        return CourseDetailDto.builder()
                .id(course.getId())
                .code(course.getCode())
                .name(course.getName())
                .credits(course.getCredits())
                .professorName(professorName)
                .schedule(schedule)
                .build();
    }
}
