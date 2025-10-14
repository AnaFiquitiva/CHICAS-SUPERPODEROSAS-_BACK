package eci.edu.dosw.proyecto.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que expone la información pública de un estudiante en la lista de espera.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaitingListEntryDTO {
    private String studentCode;
    private String studentName;
    private String program;
    private Integer currentSemester;
    private String requestDate;
}
