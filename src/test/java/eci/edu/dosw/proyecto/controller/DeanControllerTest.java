package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.DeanDTO;
import eci.edu.dosw.proyecto.dto.DeanPartialUpdateDTO;
import eci.edu.dosw.proyecto.service.interfaces.DeanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para DeanController
 *
 * Estas pruebas simulan llamadas REST y verifican:
 * 1. La creación de decanos.
 * 2. Obtención de todos los decanos.
 * 3. Actualización parcial de decanos (PATCH).
 * 4. Que el servicio subyacente (DeanService) se llame correctamente.
 */
class DeanControllerTest {

    @Mock
    private DeanService deanService;

    @InjectMocks
    private DeanController deanController;

    private DeanDTO deanDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        deanDTO = new DeanDTO();
        deanDTO.setEmployeeCode("D001");
        deanDTO.setInstitutionalEmail("dean@uni.edu");
        deanDTO.setName("Decano Test");
    }

    /**
     * Prueba la creación de un decano a través del controller.
     * Verifica que el servicio se llame y retorne correctamente el DTO.
     */
    @Test
    void createDean_success() {
        when(deanService.createDean(deanDTO)).thenReturn(deanDTO);

        DeanDTO result = deanController.createDean(deanDTO);

        assertNotNull(result);
        assertEquals("D001", result.getEmployeeCode());
        verify(deanService).createDean(deanDTO);
    }

    /**
     * Prueba la obtención de todos los decanos a través del controller.
     * Verifica que se retorne la lista correcta.
     */
    @Test
    void getAllDeans_success() {
        when(deanService.getAllDeans()).thenReturn(List.of(deanDTO));

        List<DeanDTO> result = deanController.getAllDeans();

        assertEquals(1, result.size());
        verify(deanService).getAllDeans();
    }

    /**
     * Prueba la actualización parcial de un decano a través del controller.
     * Verifica que se llame al servicio con los parámetros correctos.
     */
    @Test
    void updateDeanPartial_success() {
        DeanPartialUpdateDTO partialDTO = new DeanPartialUpdateDTO();
        when(deanService.updateDeanPartial("D001", partialDTO)).thenReturn(deanDTO);

        DeanDTO result = deanController.updateDeanPartial("D001", partialDTO);

        assertEquals("D001", result.getEmployeeCode());
        verify(deanService).updateDeanPartial("D001", partialDTO);
    }
}
