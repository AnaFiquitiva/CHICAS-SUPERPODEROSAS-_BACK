package eci.edu.dosw.proyecto.service.impl;



import eci.edu.dosw.proyecto.dto.ProfessorDTO;
import eci.edu.dosw.proyecto.dto.ProfessorPartialUpdateDTO;
import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.exception.ValidationException;
import eci.edu.dosw.proyecto.model.Professor;
import eci.edu.dosw.proyecto.repository.ProfessorRepository;
import eci.edu.dosw.proyecto.utils.ProfessorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProfessorServiceImpl.
 *
 * Se verifica:
 * - Creación, lectura, actualización y eliminación.
 * - Validaciones de campos obligatorios.
 * - Casos de profesor inactivo.
 * - Permisos para actualizar y consultar perfiles.
 */

class ProfessorServiceImplTest {

    @Mock
    private ProfessorRepository repository;

    @Mock
    private ProfessorMapper mapper;

    @InjectMocks
    private ProfessorServiceImpl service;

    private Professor professor;
    private ProfessorDTO professorDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        professor = new Professor();
        professor.setId("123");
        professor.setName("Juan Pérez");
        professor.setIdentification("123456789");
        professor.setInstitutionalEmail("juan@uni.edu");
        professor.setEmail("juan@gmail.com");
        professor.setPhone("1234567");
        professor.setAddress("Calle 123");
        professor.setActive(true);

        professorDTO = new ProfessorDTO();
        professorDTO.setId("123");
        professorDTO.setName("Juan Pérez");
        professorDTO.setIdentification("123456789");
        professorDTO.setInstitutionalEmail("juan@uni.edu");
        professorDTO.setEmail("juan@gmail.com");
        professorDTO.setPhone("1234567");
        professorDTO.setAddress("Calle 123");
    }


    @Test
    void create_success() {

        when(mapper.toEntity(professorDTO)).thenReturn(professor);
        when(mapper.toDto(professor)).thenReturn(professorDTO);

        var result = service.create(professorDTO);

        assertEquals("Juan Pérez", result.getName());
        verify(repository).save(professor);
    }

    @Test
    void create_validationError() {

        professorDTO.setName(null);

        var exception = assertThrows(ValidationException.class, () -> service.create(professorDTO));
        assertEquals("El nombre es obligatorio", exception.getMessage());
    }


    @Test
    void findById_success() {
        when(repository.findById("123")).thenReturn(Optional.of(professor));
        when(mapper.toDto(professor)).thenReturn(professorDTO);

        var result = service.findById("123");

        assertEquals("Juan Pérez", result.getName());
    }

    @Test
    void findById_notFound() {
        when(repository.findById("123")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById("123"));
    }




    @Test
    void updateAsAdmin_success() {
        professorDTO.setName("Juan Actualizado");
        when(repository.findById("123")).thenReturn(Optional.of(professor));
        doNothing().when(mapper).updateFromDto(professorDTO, professor);
        when(mapper.toDto(professor)).thenReturn(professorDTO);

        var result = service.updateAsAdmin("123", professorDTO);

        assertEquals("Juan Actualizado", result.getName());
        verify(repository).save(professor);
    }

    @Test
    void updateAsAdmin_inactiveProfessor() {
        professor.setActive(false);
        when(repository.findById("123")).thenReturn(Optional.of(professor));

        assertThrows(NotFoundException.class, () -> service.updateAsAdmin("123", professorDTO));
    }


    @Test
    void updateSelf_forbidden() {
        assertThrows(BusinessException.class, () ->
                service.updateSelf("123", new ProfessorPartialUpdateDTO(), "999"));
    }


    @Test
    void delete_success() {
        when(repository.findById("123")).thenReturn(Optional.of(professor));

        service.delete("123");

        assertFalse(professor.isActive());
        verify(repository).save(professor);
    }

    @Test
    void delete_alreadyInactiveProfessor() {
        professor.setActive(false);
        when(repository.findById("123")).thenReturn(Optional.of(professor));

        service.delete("123"); // No se guarda nada, no lanza excepción
        verify(repository, never()).save(professor);
    }

    // --- FILTROS ---
    @Test
    void findAll_withFilters() {
        when(repository.findByFacultyIdAndActiveTrue("FAC001")).thenReturn(List.of(professor));
        when(mapper.toDto(professor)).thenReturn(professorDTO);

        var result = service.findAll("FAC001", null);
        assertEquals(1, result.size());

        when(repository.findBySubjectIdsContainingAndActiveTrue("SUB001")).thenReturn(List.of(professor));
        var result2 = service.findAll(null, "SUB001");
        assertEquals(1, result2.size());

        when(repository.findByActiveTrue()).thenReturn(List.of(professor));
        var result3 = service.findAll(null, null);
        assertEquals(1, result3.size());
    }
}
