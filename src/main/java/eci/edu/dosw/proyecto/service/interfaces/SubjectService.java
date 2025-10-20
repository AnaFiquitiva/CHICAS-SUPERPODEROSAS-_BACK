package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.model.Subject;
import java.util.List;

public interface SubjectService {
    Subject createSubject(Subject subject);
    Subject updateSubject(String id, Subject subject);
    List<Subject> getAllSubjects();
    Subject getSubjectById(String id);
}
