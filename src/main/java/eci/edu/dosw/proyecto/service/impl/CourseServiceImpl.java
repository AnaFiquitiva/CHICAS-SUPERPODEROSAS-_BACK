package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.CourseDetailDto;
import eci.edu.dosw.proyecto.model.Course;
import eci.edu.dosw.proyecto.repository.CourseRepository;
import eci.edu.dosw.proyecto.service.interfaces.CourseService;
import eci.edu.dosw.proyecto.utils.CourseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    public List<CourseDetailDto> searchCourses(String query) {
        List<Course> byName = courseRepository.findByNameContainingIgnoreCase(query);
        List<Course> byCode = courseRepository.findByCodeContainingIgnoreCase(query);

        List<Course> merged = byName.stream().collect(Collectors.toList());

        byCode.stream()
                .filter(course -> merged.stream().noneMatch(c -> c.getId().equals(course.getId())))
                .forEach(merged::add);

        // 🔧 En este punto podrías obtener profesor y horario de otras entidades.
        // Por ahora, usamos valores de ejemplo:
        return merged.stream()
                .map(course -> courseMapper.toDto(course, "Professor Example", "Mon-Wed 10:00-12:00"))
                .collect(Collectors.toList());
    }
}
