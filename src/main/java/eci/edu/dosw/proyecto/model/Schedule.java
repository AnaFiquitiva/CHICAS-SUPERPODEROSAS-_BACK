package eci.edu.dosw.proyecto.model;

import lombok.Data;

import java.util.List;

@Data
/*
 * Horario de un grupo
 */
public class Schedule {
    private List<DaySchedule> daySchedules;

    public boolean hasConflict(Schedule other) {
        return daySchedules.stream()
                .anyMatch(day -> other.getDaySchedules().stream()
                        .anyMatch(day::hasTimeConflict));
    }
}