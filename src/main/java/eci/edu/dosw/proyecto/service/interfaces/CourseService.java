package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.CourseDetailDto;
import java.util.List;

public interface CourseService {
    List<CourseDetailDto> searchCourses(String query);
}
