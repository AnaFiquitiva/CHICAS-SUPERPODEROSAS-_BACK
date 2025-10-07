package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassroomDto {
    private String id;
    private String building;
    private String roomNumber;
}
