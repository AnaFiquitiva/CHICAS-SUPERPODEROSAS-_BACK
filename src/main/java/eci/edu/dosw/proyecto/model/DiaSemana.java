package eci.edu.dosw.proyecto.model;

public enum DiaSemana {
    LUNES("Lunes", 1),
    MARTES("Martes", 2),
    MIERCOLES("Miércoles", 3),
    JUEVES("Jueves", 4),
    VIERNES("Viernes", 5),
    SABADO("Sábado", 6);

    private final String nombre;
    private final int numeroDia;

    DiaSemana(String nombre, int numeroDia) {
        this.nombre = nombre;
        this.numeroDia = numeroDia;
    }

    public String getNombre() {
        return nombre;
    }

    public int getNumeroDia() {
        return numeroDia;
    }

    public boolean esDiaHabil() {
        return this != SABADO;
    }

    public static DiaSemana fromNumero(int numero) {
        for (DiaSemana dia : values()) {
            if (dia.numeroDia == numero) return dia;
        }
        throw new IllegalArgumentException("Número de día inválido: " + numero);
    }
}
