package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase AcademicPeriod")
class AcademicPeriodTest {

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime later = now.plusMonths(4);

    @Test
    @DisplayName("Debería crear un AcademicPeriod correctamente usando el patrón Builder")
    void shouldCreateAcademicPeriodUsingBuilder_happyPath() {
        // Arrange
        String expectedId = "period-2024-1";
        String expectedName = "Semestre 2024-1";
        String expectedDescription = "Primer semestre del año 2024";

        // Act
        AcademicPeriod period = AcademicPeriod.builder()
                .id(expectedId)
                .name(expectedName)
                .description(expectedDescription)
                .startDate(now)
                .endDate(later)
                .active(true)
                .createdAt(now)
                .build();

        // Assert
        assertThat(period).isNotNull();
        assertThat(period.getId()).isEqualTo(expectedId);
        assertThat(period.getName()).isEqualTo(expectedName);
        assertThat(period.getDescription()).isEqualTo(expectedDescription);
        assertThat(period.getStartDate()).isEqualTo(now);
        assertThat(period.getEndDate()).isEqualTo(later);
        assertThat(period.isActive()).isTrue();
        assertThat(period.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Debería crear un AcademicPeriod con valores por defecto usando el constructor vacío")
    void shouldCreateAcademicPeriodWithDefaultValues_happyPath() {
        // Act
        AcademicPeriod period = new AcademicPeriod();

        // Assert
        assertThat(period).isNotNull();
        assertThat(period.getId()).isNull();
        assertThat(period.getName()).isNull();
        assertThat(period.getDescription()).isNull();
        assertThat(period.getStartDate()).isNull();
        assertThat(period.getEndDate()).isNull();
        assertThat(period.isActive()).isFalse(); // El valor por defecto de boolean es false
        assertThat(period.getCreatedAt()).isNull();
    }

    @Test
    @DisplayName("Debería permitir actualizar los valores usando los setters")
    void shouldUpdateValuesUsingSetters_happyPath() {
        // Arrange
        AcademicPeriod period = new AcademicPeriod();
        String newName = "Semestre 2024-2";

        // Act
        period.setName(newName);
        period.setActive(true);

        // Assert
        assertThat(period.getName()).isEqualTo(newName);
        assertThat(period.isActive()).isTrue();
        // Verificamos que otros campos no se han visto afectados
        assertThat(period.getId()).isNull();
    }

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode() para objetos iguales")
    void shouldVerifyEqualsAndHashCodeContract_happyPath() {
        // Arrange
        AcademicPeriod period1 = AcademicPeriod.builder()
                .id("period-123")
                .name("Semestre X")
                .build();

        AcademicPeriod period2 = AcademicPeriod.builder()
                .id("period-123")
                .name("Semestre X")
                .build();

        AcademicPeriod period3 = AcademicPeriod.builder()
                .id("period-456")
                .name("Semestre Y")
                .build();

        // Act & Assert
        // Dos objetos con los mismos datos deben ser iguales
        assertThat(period1).isEqualTo(period2);
        // Y deben tener el mismo hashCode
        assertThat(period1.hashCode()).isEqualTo(period2.hashCode());
        // Un objeto diferente no debe ser igual
        assertThat(period1).isNotEqualTo(period3);
    }

    @Test
    @DisplayName("Debería generar una representación en cadena (toString) que contenga los datos del objeto")
    void shouldGenerateStringRepresentation_happyPath() {
        // Arrange
        AcademicPeriod period = AcademicPeriod.builder()
                .id("period-789")
                .name("Verano 2024")
                .active(true)
                .build();

        // Act
        String stringRepresentation = period.toString();

        // Assert
        assertThat(stringRepresentation).isNotNull();
        assertThat(stringRepresentation).contains("AcademicPeriod");
        assertThat(stringRepresentation).contains("id=period-789");
        assertThat(stringRepresentation).contains("name=Verano 2024");
        assertThat(stringRepresentation).contains("active=true");
    }
}