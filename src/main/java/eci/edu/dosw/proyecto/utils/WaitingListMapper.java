package eci.edu.dosw.proyecto.utils;


import eci.edu.dosw.proyecto.dto.WaitingListEntryDTO;
import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.model.WaitingListEntry;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entidades de la lista de espera a DTOs.
 * Convierte la información interna de la base de datos en datos legibles para la vista.
 */
@Component
public class WaitingListMapper {

    /**
     * Convierte una entrada de la lista de espera y su estudiante asociado en un DTO.
     */
    public WaitingListEntryDTO toDTO(WaitingListEntry entry, Student student) {
        return new WaitingListEntryDTO(
                student.getStudentCode(),
                student.getName(),
                student.getProgram(),
                student.getCurrentSemester(),
                entry.getRequestDate().toString()
        );
    }
}
