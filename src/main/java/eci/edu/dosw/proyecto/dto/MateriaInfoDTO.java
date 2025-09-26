package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
class MateriaInfoDTO {
    private String id;
    private String codigo;
    private String nombre;
    private String facultad;
}
