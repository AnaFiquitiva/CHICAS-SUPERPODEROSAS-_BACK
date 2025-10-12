package eci.edu.dosw.proyecto.service;

import eci.edu.dosw.proyecto.dto.ScheduleDto;
import eci.edu.dosw.proyecto.model.Schedule;
import eci.edu.dosw.proyecto.repository.ScheduleRepository;
import eci.edu.dosw.proyecto.utils.mappers.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;

    @Override
    public List<ScheduleDto> getCurrentSchedule(String studentCode) {
        List<Schedule> schedules = scheduleRepository.findByStudent_StudentCodeAndSemester_IsCurrent(studentCode, true);
        return scheduleMapper.toDtoList(schedules);
    }

    @Override
    public List<ScheduleDto> getPastSchedules(String studentCode) {
        List<Schedule> schedules = scheduleRepository.findByStudent_StudentCodeAndSemester_IsCurrent(studentCode, false);
        return scheduleMapper.toDtoList(schedules);
    }
}