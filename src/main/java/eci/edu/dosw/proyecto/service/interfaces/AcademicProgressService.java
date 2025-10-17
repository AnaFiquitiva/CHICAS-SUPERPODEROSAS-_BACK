/**
 * Service interface for academic progress tracking
 * Defines contract for progress calculation and retrieval
 */
package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.AcademicProgressResponse;

public interface AcademicProgressService {

    /**
     * Retrieves academic progress by student code
     * @param studentCode Unique student identifier code
     * @return Academic progress information
     */
    AcademicProgressResponse getAcademicProgress(String studentCode);

    /**
     * Retrieves academic progress by student ID
     * @param studentId Student's unique identifier
     * @return Academic progress information
     */
    AcademicProgressResponse getAcademicProgressByStudentId(String studentId);
}