package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.AdministratorDTO;
import eci.edu.dosw.proyecto.service.interfaces.AdministratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administrators")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdministratorController {

    private final AdministratorService administratorService;

    @PostMapping
    public AdministratorDTO createAdministrator(@RequestBody AdministratorDTO administratorDTO) {
        return administratorService.createAdministrator(administratorDTO);
    }

    @PutMapping("/{id}")
    public AdministratorDTO updateAdministrator(@PathVariable String id, @RequestBody AdministratorDTO administratorDTO) {
        return administratorService.updateAdministrator(id, administratorDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteAdministrator(@PathVariable String id) {
        administratorService.deleteAdministrator(id);
    }

    @GetMapping("/{id}")
    public AdministratorDTO getAdministratorById(@PathVariable String id) {
        return administratorService.getAdministratorById(id);
    }

    @GetMapping("/employee-code/{employeeCode}")
    public AdministratorDTO getAdministratorByEmployeeCode(@PathVariable String employeeCode) {
        return administratorService.getAdministratorByEmployeeCode(employeeCode);
    }

    @GetMapping
    public List<AdministratorDTO> getAllAdministrators() {
        return administratorService.getAllAdministrators();
    }

    @GetMapping("/department/{department}")
    public List<AdministratorDTO> getAdministratorsByDepartment(@PathVariable String department) {
        return administratorService.getAdministratorsByDepartment(department);
    }

    @GetMapping("/active")
    public List<AdministratorDTO> getActiveAdministrators() {
        return administratorService.getActiveAdministrators();
    }
}