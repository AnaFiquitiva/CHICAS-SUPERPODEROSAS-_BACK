package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {
    private String id;
    private String code;
    private String name;
    private String email;
    private List<ScheduleDto> schedules;
}
