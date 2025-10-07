package eci.edu.dosw.proyecto.serviceTest;

import eci.edu.dosw.proyecto.dto.ScheduleDto;
import eci.edu.dosw.proyecto.model.Schedule;
import eci.edu.dosw.proyecto.repository.ScheduleRepository;
import eci.edu.dosw.proyecto.service.ScheduleServiceImpl;
import eci.edu.dosw.proyecto.utils.mappers.ScheduleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ScheduleMapper scheduleMapper;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnCurrentSemesterSchedule() {
        String studentId = "1"; // ✅ Mongo usa String IDs
        Schedule schedule = new Schedule();
        ScheduleDto scheduleDto = new ScheduleDto();

        when(scheduleRepository.findByStudent_IdAndSemester_IsCurrent(studentId, true))
                .thenReturn(List.of(schedule));
        when(scheduleMapper.toDtoList(anyList()))
                .thenReturn(List.of(scheduleDto));

        List<ScheduleDto> result = scheduleService.getCurrentSchedule(studentId);

        assertEquals(1, result.size());
        verify(scheduleRepository, times(1))
                .findByStudent_IdAndSemester_IsCurrent(studentId, true);
        verify(scheduleMapper, times(1)).toDtoList(anyList());
    }

    @Test
    void shouldReturnEmptyListIfNoSchedulesFound() {
        String studentId = "99"; // ✅ también String
        when(scheduleRepository.findByStudent_IdAndSemester_IsCurrent(studentId, true))
                .thenReturn(List.of());
        when(scheduleMapper.toDtoList(anyList()))
                .thenReturn(List.of());

        List<ScheduleDto> result = scheduleService.getCurrentSchedule(studentId);

        assertTrue(result.isEmpty());
        verify(scheduleRepository, times(1))
                .findByStudent_IdAndSemester_IsCurrent(studentId, true);
    }
}
