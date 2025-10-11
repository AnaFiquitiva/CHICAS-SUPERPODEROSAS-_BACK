package eci.edu.dosw.proyecto.controller;


import eci.edu.dosw.proyecto.dto.ProfessorDTO;
import eci.edu.dosw.proyecto.dto.ProfessorPartialUpdateDTO;
import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.service.interfaces.ProfessorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProfessorController.
 *
 * Se verifica:
 * - Creación de profesor por Administrador.
 * - Consulta de profesores (lista y por filtros).
 * - Consulta de perfil de profesor con permisos.
 * - Actualización total (PUT) y parcial (PATCH) de profesor.
 * - Eliminación de profesor y control de permisos.
 */
class ProfessorControllerTest {

    @Mock
    private ProfessorService professorService;

    @InjectMocks
    private ProfessorController professorController;

    private ProfessorDTO professorDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        professorDTO = new ProfessorDTO();
        professorDTO.setId("123");
        professorDTO.setName("Juan Pérez");
        professorDTO.setIdentification("123456789");
        professorDTO.setInstitutionalEmail("juan@uni.edu");
        professorDTO.setEmail("juan@gmail.com");
        professorDTO.setPhone("1234567");
        professorDTO.setAddress("Calle 123");
    }

    // --- CREACIÓN ---
    @Test
    void createProfessor_success() {
        // Criterio: Administrador crea un profesor correctamente
        when(professorService.create(professorDTO)).thenReturn(professorDTO);

        var response = professorController.create(professorDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Juan Pérez", response.getBody().getName());
        verify(professorService).create(professorDTO);
    }

    // --- LISTA DE PROFESORES ---
    @Test
    void listProfessors_success() {
        // Criterio: Administrador o Decano consulta la lista completa
        when(professorService.findAll(null, null)).thenReturn(List.of(professorDTO));

        var response = professorController.list(null, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(professorService).findAll(null, null);
    }

    @Test
    void listProfessors_byFaculty_success() {
        // Criterio: Filtro por facultad
        when(professorService.findAll("FAC001", null)).thenReturn(List.of(professorDTO));

        var response = professorController.list("FAC001", null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(professorService).findAll("FAC001", null);
    }

    @Test
    void listProfessors_bySubject_success() {
        // Criterio: Filtro por materia
        when(professorService.findAll(null, "SUB001")).thenReturn(List.of(professorDTO));

        var response = professorController.list(null, "SUB001");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(professorService).findAll(null, "SUB001");
    }

    // --- OBTENER POR ID ---
    @Test
    void getById_success() {
        // Criterio: Profesor ve su propio perfil
        Principal principal = () -> "123";
        when(professorService.findByIdWithPermissions("123", "123")).thenReturn(professorDTO);

        var response = professorController.getById("123", principal);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("123", response.getBody().getId());
        verify(professorService).findByIdWithPermissions("123", "123");
    }

    @Test
    void getById_forbidden() {
        // Criterio: Profesor intenta ver otro perfil
        Principal principal = () -> "999";
        when(professorService.findByIdWithPermissions("123", "999"))
                .thenThrow(new BusinessException("No puede ver perfiles de otros profesores"));

        var exception = assertThrows(BusinessException.class, () ->
                professorController.getById("123", principal));

        assertEquals("No puede ver perfiles de otros profesores", exception.getMessage());
        verify(professorService).findByIdWithPermissions("123", "999");
    }

    // --- ACTUALIZACIÓN ---
    @Test
    void updateAsAdmin_success() {
        // Criterio: Administrador actualiza datos completos
        professorDTO.setName("Juan Actualizado");
        when(professorService.updateAsAdmin("123", professorDTO)).thenReturn(professorDTO);

        var response = professorController.updateAsAdmin("123", professorDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Juan Actualizado", response.getBody().getName());
        verify(professorService).updateAsAdmin("123", professorDTO);
    }

    @Test
    void updateSelf_success() {
        // Criterio: Profesor actualiza solo campos permitidos
        ProfessorPartialUpdateDTO partialDTO = new ProfessorPartialUpdateDTO();
        partialDTO.setEmail("nuevo@gmail.com");
        professorDTO.setEmail("nuevo@gmail.com");
        Principal principal = () -> "123";

        when(professorService.updateSelf("123", partialDTO, "123")).thenReturn(professorDTO);

        var response = professorController.updateSelf("123", partialDTO, principal);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("nuevo@gmail.com", response.getBody().getEmail());
        verify(professorService).updateSelf("123", partialDTO, "123");
    }

    @Test
    void updateSelf_forbidden() {
        // Criterio: Profesor intenta actualizar otro perfil
        ProfessorPartialUpdateDTO partialDTO = new ProfessorPartialUpdateDTO();
        Principal principal = () -> "999";

        when(professorService.updateSelf("123", partialDTO, "999"))
                .thenThrow(new BusinessException("No puede modificar otro perfil"));

        var exception = assertThrows(BusinessException.class, () ->
                professorController.updateSelf("123", partialDTO, principal));

        assertEquals("No puede modificar otro perfil", exception.getMessage());
        verify(professorService).updateSelf("123", partialDTO, "999");
    }

    // --- ELIMINACIÓN ---
    @Test
    void deleteProfessor_success() {
        // Criterio: Administrador elimina correctamente
        doNothing().when(professorService).delete("123");

        var response = professorController.delete("123");

        assertEquals(204, response.getStatusCodeValue());
        verify(professorService).delete("123");
    }

    @Test
    void deleteProfessor_forbidden() {
        // Criterio: Otro rol intenta eliminar
        doThrow(new BusinessException("No tiene permisos")).when(professorService).delete("123");

        var exception = assertThrows(BusinessException.class, () ->
                professorController.delete("123"));

        assertEquals("No tiene permisos", exception.getMessage());
        verify(professorService).delete("123");
    }
}
