package eci.edu.dosw.proyecto.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaginacionDTO {
    private int pagina = 0;
    private int tamaño = 20;
    private String ordenarPor;
    private String direccion = "asc";

    public int getOffset() {
        return pagina * tamaño;
    }
}