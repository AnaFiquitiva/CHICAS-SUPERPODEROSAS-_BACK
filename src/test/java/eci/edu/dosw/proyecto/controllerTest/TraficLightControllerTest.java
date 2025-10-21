package eci.edu.dosw.proyecto.controllerTest;

import eci.edu.dosw.proyecto.controller.TrafficLightController;
import eci.edu.dosw.proyecto.dto.TrafficLightDto;
import eci.edu.dosw.proyecto.service.interfaces.TrafficLightService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrafficLightController.class)
class TrafficLightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrafficLightService trafficLightService;

    @Test
    void shouldReturnTrafficLightSuccessfully() throws Exception {
        TrafficLightDto dto = new TrafficLightDto("1000093971", "Samuel Albarracin", 4.5, "GREEN");

        Mockito.when(trafficLightService.getStudentTrafficLight("1"))
                .thenReturn(dto);

        mockMvc.perform(get("/api/traffic-light/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("GREEN"))
                .andExpect(jsonPath("$.average").value(4.5));
    }

    @Test
    void shouldReturnErrorIfStudentNotFound() throws Exception {
        Mockito.when(trafficLightService.getStudentTrafficLight("99"))
                .thenThrow(new RuntimeException("Student not found"));

        mockMvc.perform(get("/api/traffic-light/99"))
                .andExpect(status().is5xxServerError());
    }
}
