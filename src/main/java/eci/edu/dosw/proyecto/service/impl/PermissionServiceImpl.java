package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.model.Administrator;
import eci.edu.dosw.proyecto.model.Dean;
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
}