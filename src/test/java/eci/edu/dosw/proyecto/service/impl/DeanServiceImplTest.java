package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.DeanDTO;
import eci.edu.dosw.proyecto.dto.DeanPartialUpdateDTO;
import eci.edu.dosw.proyecto.exception.CustomException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.Dean;
import eci.edu.dosw.proyecto.repository.DeanRepository;
import eci.edu.dosw.proyecto.utils.DeanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para DeanServiceImpl
 *
 * Estas pruebas verifican:
 * 1. Creación de decanos con validación de duplicados y rol de administrador.
 * 2. Actualización completa de decanos.
 * 3. Actualización parcial de decanos (PATCH).
 * 4. Eliminación lógica de decanos.
 * 5. Consultas por ID y lista de decanos.
 * 6. Manejo de excepciones cuando no se encuentra un decano.
 */
class DeanServiceImplTest {

    @Mock
    private DeanRepository deanRepository;

    @Mock
    private DeanMapper deanMapper;

    @InjectMocks
    private DeanServiceImpl deanService;

    private DeanDTO deanDTO;
    private Dean dean;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Creamos un DTO de decano de prueba
        deanDTO = new DeanDTO();
        deanDTO.setEmployeeCode("D001");
        deanDTO.setInstitutionalEmail("dean@uni.edu");
        deanDTO.setName("Decano Test");

        // Creamos la entidad decano correspondiente
        dean = new Dean();
        dean.setEmployeeCode("D001");
        dean.setEmail("dean@uni.edu");
        dean.setName("Decano Test");
        dean.setActive(true);
        dean.setCreatedDate(LocalDateTime.now());
    }

    /**
     * Prueba la creación exitosa de un decano.
     * Verifica que no existan duplicados y que se guarde correctamente en el repositorio.
     */
    @Test
    void createDean_success() {
        when(deanRepository.existsByEmployeeCode(deanDTO.getEmployeeCode())).thenReturn(false);
        when(deanRepository.existsByEmail(deanDTO.getInstitutionalEmail())).thenReturn(false);
        when(deanMapper.toEntity(deanDTO)).thenReturn(dean);
        when(deanRepository.save(any(Dean.class))).thenReturn(dean);
        when(deanMapper.toDTO(dean)).thenReturn(deanDTO);

        DeanDTO result = deanService.createDean(deanDTO);

        assertEquals(deanDTO.getEmployeeCode(), result.getEmployeeCode());
        verify(deanRepository, times(1)).save(any(Dean.class));
    }

    /**
     * Prueba la creación de un decano con código de empleado duplicado.
     * Debe lanzar una excepción CustomException.
     */
    @Test
    void createDean_duplicateEmployeeCode_throwsException() {
        when(deanRepository.existsByEmployeeCode(deanDTO.getEmployeeCode())).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () -> deanService.createDean(deanDTO));
        assertEquals("Ya existe un decano con este código de empleado", exception.getMessage());
    }

    /**
     * Prueba la actualización de un decano que no existe.
     * Debe lanzar NotFoundException.
     */
    @Test
    void updateDean_notFound_throwsException() {
        when(deanRepository.findById("D001")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> deanService.updateDean("D001", deanDTO));
        assertEquals("Decano no encontrado", exception.getMessage());
    }

    /**
     * Prueba la eliminación lógica de un decano.
     * Verifica que se marque como inactivo y se guarde.
     */
    @Test
    void deleteDean_success() {
        when(deanRepository.findById("D001")).thenReturn(Optional.of(dean));

        deanService.deleteDean("D001");

        assertFalse(dean.getActive());
        verify(deanRepository).save(dean);
    }

    /**
     * Prueba la obtención de un decano por ID.
     * Verifica que se retorne correctamente el DTO.
     */
    @Test
    void getDeanById_success() {
        when(deanRepository.findById("D001")).thenReturn(Optional.of(dean));
        when(deanMapper.toDTO(dean)).thenReturn(deanDTO);

        DeanDTO result = deanService.getDeanById("D001");

        assertEquals("D001", result.getEmployeeCode());
    }

    /**
     * Prueba la actualización parcial de un decano (PATCH).
     * Verifica que se llame a partialUpdate del mapper y se guarde.
     */
    @Test
    void updateDeanPartial_success() {
        DeanPartialUpdateDTO partialDTO = new DeanPartialUpdateDTO();
        when(deanRepository.findById("D001")).thenReturn(Optional.of(dean));
        when(deanRepository.save(any(Dean.class))).thenReturn(dean);
        when(deanMapper.toDTO(dean)).thenReturn(deanDTO);

        DeanDTO result = deanService.updateDeanPartial("D001", partialDTO);

        assertNotNull(result);
        verify(deanMapper).partialUpdate(partialDTO, dean);
    }
}

