package eci.edu.dosw.proyecto.service.impl;


import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.Subject;
import eci.edu.dosw.proyecto.repository.SubjectRepository;
import eci.edu.dosw.proyecto.service.interfaces.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    @Override
    public Subject createSubject(Subject subject) {
        // Validar código único
        if (subjectRepository.findByCode(subject.getCode()).isPresent()) {
            throw new BusinessException("Código de materia ya existente");
        }
        return subjectRepository.save(subject);
    }

    @Override
    public Subject updateSubject(String id, Subject subject) {
        Subject existing = subjectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Materia no encontrada"));
        existing.setName(subject.getName());
        existing.setCredits(subject.getCredits());
        existing.setMandatory(subject.getMandatory());
        return subjectRepository.save(existing);
    }

    @Override
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Override
    public Subject getSubjectById(String id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Materia no encontrada"));
    }
}
