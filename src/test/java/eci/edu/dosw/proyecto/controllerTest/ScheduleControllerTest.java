package eci.edu.dosw.proyecto.controllerTest;

import eci.edu.dosw.proyecto.controller.ScheduleController;
import eci.edu.dosw.proyecto.dto.ScheduleDto;
import eci.edu.dosw.proyecto.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScheduleController.class)
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    @Test
    void shouldReturnStudentSchedule() throws Exception {
        ScheduleDto scheduleDto = new ScheduleDto();

        // ✅ Ahora usamos String IDs (MongoDB)
        Mockito.when(scheduleService.getCurrentSchedule("1"))
                .thenReturn(List.of(scheduleDto));

        mockMvc.perform(get("/api/schedules/current/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldReturnErrorIfStudentNotExists() throws Exception {
        Mockito.when(scheduleService.getCurrentSchedule("99"))
                .thenThrow(new RuntimeException("Student not found"));

        mockMvc.perform(get("/api/schedules/current/99"))
                .andExpect(status().is5xxServerError());
    }
}
