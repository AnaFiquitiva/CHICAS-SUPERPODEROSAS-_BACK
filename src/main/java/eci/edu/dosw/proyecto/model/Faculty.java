package eci.edu.dosw.proyecto.model;

/**
 * Enum que representa las facultades disponibles en la universidad.
 * Se utiliza para garantizar la consistencia en los datos de grupos y usuarios.
 */
public enum Faculty {
    CIVIL_ENGINEERING("Ingeniería Civil"),
    ELECTRICAL_ENGINEERING("Ingeniería Eléctrica"),
    ELECTRONIC_ENGINEERING("Ingeniería Electrónica"),
    SYSTEMS_ENGINEERING("Ingeniería de Sistemas"),
    INDUSTRIAL_ENGINEERING("Ingeniería Industrial"),
    MECHANICAL_ENGINEERING("Ingeniería Mecánica"),
    ENVIRONMENTAL_ENGINEERING("Ingeniería Ambiental"),
    BIOMEDICAL_ENGINEERING("Ingeniería Biomédica"),
    CYBERSECURITY_ENGINEERING("Ingeniería de Ciberseguridad"),
    ARTIFICIAL_INTELLIGENCE_ENGINEERING("Ingeniería de Inteligencia Artificial"),
    BIOTECHNOLOGY_ENGINEERING("Ingeniería en Biotecnología"),
    STATISTICS_ENGINEERING("Ingeniería Estadística"),
    BUSINESS_ADMINISTRATION("Administración de Empresas"),
    ECONOMY("Economía"),
    MATHEMATICS("Matemáticas");

    private final String name;

    Faculty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
