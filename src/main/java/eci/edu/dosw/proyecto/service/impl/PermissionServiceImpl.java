package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.model.Administrator;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.AdministratorRepository;
import eci.edu.dosw.proyecto.repository.DeanRepository;
import eci.edu.dosw.proyecto.service.interfaces.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final AdministratorRepository administratorRepository;
    private final DeanRepository deanRepository;

    @Override
    public boolean canViewAllRequests(String employeeCode) {
        // Verificar si es administrador con permiso
        Optional<Administrator> admin = administratorRepository.findByEmployeeCode(employeeCode);
        if (admin.isPresent() && admin.get().getCanManageRequests() != null) {
            return admin.get().getCanManageRequests();
        }

        // Verificar si es decano con permiso
        Optional<Dean> dean = deanRepository.findByEmployeeCode(employeeCode);
        return dean.isPresent() &&
                dean.get().getCanViewAllRequests() != null &&
                dean.get().getCanViewAllRequests();
    }

    @Override
    public boolean canApprovePlanChanges(String employeeCode) {
        Optional<Dean> dean = deanRepository.findByEmployeeCode(employeeCode);
        return dean.isPresent() &&
                dean.get().getCanApprovePlanChanges() != null &&
                dean.get().getCanApprovePlanChanges();
    }

    @Override
    public boolean canManageAcademicPeriods(String employeeCode) {
        Optional<Administrator> admin = administratorRepository.findByEmployeeCode(employeeCode);
        return admin.isPresent() &&
                admin.get().getCanManageAcademicPeriods() != null &&
                admin.get().getCanManageAcademicPeriods();
    }

    @Override
    public boolean canGenerateReports(String employeeCode) {
        Optional<Administrator> admin = administratorRepository.findByEmployeeCode(employeeCode);
        if (admin.isPresent() && admin.get().getCanGenerateReports() != null) {
            return admin.get().getCanGenerateReports();
        }

        Optional<Dean> dean = deanRepository.findByEmployeeCode(employeeCode);
        return dean.isPresent() &&
                dean.get().getCanGenerateFacultyReports() != null &&
                dean.get().getCanGenerateFacultyReports();
    }

    @Override
    public boolean canManageStudents(String employeeCode) {
        Optional<Administrator> admin = administratorRepository.findByEmployeeCode(employeeCode);
        return admin.isPresent() &&
                admin.get().getCanManageStudents() != null &&
                admin.get().getCanManageStudents();
    }

    @Override
    public boolean isDeanOfFaculty(String employeeCode, String facultyId) {
        if (facultyId == null || facultyId.trim().isEmpty()) {
            return false;
        }
        String normalizedFacultyId = facultyId.trim();

        Optional<Dean> deanOpt = deanRepository.findByEmployeeCode(employeeCode);
        if (!deanOpt.isPresent()) {
            return false;
        }

        Faculty faculty = deanOpt.get().getFaculty();
        if (faculty == null) {
            return false;
        }

        if (faculty.name().equalsIgnoreCase(normalizedFacultyId)) {
            return true;
        }
        if (faculty.getName() != null && faculty.getName().equalsIgnoreCase(normalizedFacultyId)) {
            return true;
        }

        return false;
    }

}