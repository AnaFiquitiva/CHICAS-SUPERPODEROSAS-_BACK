package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
public interface DeanService {
    DeanResponse createDean(DeanRequest deanRequest);
    DeanResponse getDeanById(String id);
    DeanResponse getDeanByEmail(String email);
    DeanResponse updateDean(String deanId, DeanRequest deanRequest);
    void deactivateDean(String deanId);
    List<DeanResponse> getDeansByFaculty(String facultyId);
    List<DeanResponse> getAllActiveDeans();
    boolean isDeanOfFaculty(String deanId, String facultyId);
}