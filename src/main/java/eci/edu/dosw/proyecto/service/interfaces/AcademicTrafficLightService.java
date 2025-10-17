package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.AcademicTrafficLightResponse;

import java.util.List;
import java.util.Map;

public interface AcademicTrafficLightService {

    /**
     * Generates academic traffic light assessment for a student
     *
     * @param studentId Student identifier
     * @return Comprehensive academic status assessment
     */
    AcademicTrafficLightResponse getAcademicTrafficLight(String studentId);
    List<AcademicTrafficLightResponse> getTrafficLightByProgram(String program);
    List<AcademicTrafficLightResponse> getTrafficLightByFaculty(String faculty);
    Map<String, Object> getProgramSummary(String program);
}
