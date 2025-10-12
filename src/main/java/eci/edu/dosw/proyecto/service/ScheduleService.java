package eci.edu.dosw.proyecto.service;

import eci.edu.dosw.proyecto.dto.ScheduleDto;
import java.util.List;

public interface ScheduleService {

    List<ScheduleDto> getCurrentSchedule(String studentCode);

    List<ScheduleDto> getPastSchedules(String studentCode);
}