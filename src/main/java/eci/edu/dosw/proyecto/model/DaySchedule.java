package eci.edu.dosw.proyecto.model;

import lombok.Data;

@Data
/*
 * Horario específico de un día
 */
public class DaySchedule {
    private DayOfWeek day;
    private String startTime;
    private String endTime;

    public boolean hasTimeConflict(DaySchedule other) {
        return this.day == other.getDay() &&
                this.startTime.compareTo(other.getEndTime()) < 0 &&
                other.getStartTime().compareTo(this.endTime) < 0;
    }
}