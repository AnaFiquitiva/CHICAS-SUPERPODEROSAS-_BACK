package eci.edu.dosw.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDetailDto {
    private String id;
    private String code;
    private String name;
    private int credits;
    private String professorName;
    private String schedule;
}
