package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.DeanDTO;
import eci.edu.dosw.proyecto.dto.DeanPartialUpdateDTO;
import eci.edu.dosw.proyecto.service.interfaces.DeanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/deans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeanController {

    private final DeanService deanService;

    @PostMapping
    public DeanDTO createDean(@RequestBody @Valid DeanDTO deanCreateDTO) {
        return deanService.createDean(deanCreateDTO);
    }

    @PutMapping("/{id}")
    public DeanDTO updateDean(@PathVariable String id, @RequestBody DeanDTO deanDTO) {
        return deanService.updateDean(id, deanDTO);
    }

    @PatchMapping("/{id}")
    public DeanDTO updateDeanPartial(@PathVariable String id,
                                     @RequestBody DeanPartialUpdateDTO partialDTO) {
        return deanService.updateDeanPartial(id, partialDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteDean(@PathVariable String id) {
        deanService.deleteDean(id);
    }

    @GetMapping("/{id}")
    public DeanDTO getDeanById(@PathVariable String id) {
        return deanService.getDeanById(id);
    }

    @GetMapping("/employee-code/{employeeCode}")
    public DeanDTO getDeanByEmployeeCode(@PathVariable String employeeCode) {
        return deanService.getDeanByEmployeeCode(employeeCode);
    }

    @GetMapping
    public List<DeanDTO> getAllDeans() {
        return deanService.getAllDeans();
    }

    @GetMapping("/faculty/{faculty}")
    public List<DeanDTO> getDeansByFaculty(@PathVariable String faculty) {
        return deanService.getDeansByFaculty(faculty);
    }

    @GetMapping("/active")
    public List<DeanDTO> getActiveDeans() {
        return deanService.getActiveDeans();
    }

    @GetMapping("/program/{program}")
    public List<DeanDTO> getDeansByProgram(@PathVariable String program) {
        return deanService.getDeansByProgram(program);
    }
}