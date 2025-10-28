package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase AcademicTrafficLight")
class AcademicTrafficLightTest {

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("Debería crear un AcademicTrafficLight correctamente usando el patrón Builder")
    void shouldCreateAcademicTrafficLightUsingBuilder_happyPath() {
        // Arrange
        String expectedId = "atl-123";
        String expectedColor = "GREEN";
        String expectedDescription = "Buen rendimiento académico";
        Double expectedMinGpa = 3.5;
        Double expectedMaxGpa = 5.0;

        // Act
        AcademicTrafficLight trafficLight = AcademicTrafficLight.builder()
                .id(expectedId)
                .color(expectedColor)
                .description(expectedDescription)
                .minimumGpa(expectedMinGpa)
                .maximumGpa(expectedMaxGpa)
                .calculatedAt(now)
                .build();

        // Assert
        assertThat(trafficLight).isNotNull();
        assertThat(trafficLight.getId()).isEqualTo(expectedId);
        assertThat(trafficLight.getColor()).isEqualTo(expectedColor);
        assertThat(trafficLight.getDescription()).isEqualTo(expectedDescription);
        assertThat(trafficLight.getMinimumGpa()).isEqualTo(expectedMinGpa);
        assertThat(trafficLight.getMaximumGpa()).isEqualTo(expectedMaxGpa);
        assertThat(trafficLight.getCalculatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Debería crear un AcademicTrafficLight con valores por defecto usando el constructor vacío")
    void shouldCreateAcademicTrafficLightWithDefaultValues_happyPath() {
        // Act
        AcademicTrafficLight trafficLight = new AcademicTrafficLight();

        // Assert
        assertThat(trafficLight).isNotNull();
        assertThat(trafficLight.getId()).isNull();
        assertThat(trafficLight.getColor()).isNull();
        assertThat(trafficLight.getDescription()).isNull();
        assertThat(trafficLight.getMinimumGpa()).isNull();
        assertThat(trafficLight.getMaximumGpa()).isNull();
        assertThat(trafficLight.getCalculatedAt()).isNull();
    }

    @Test
    @DisplayName("Debería permitir actualizar los valores usando los setters")
    void shouldUpdateValuesUsingSetters_happyPath() {
        // Arrange
        AcademicTrafficLight trafficLight = new AcademicTrafficLight();
        String newColor = "RED";
        Double newMinGpa = 0.0;

        // Act
        trafficLight.setColor(newColor);
        trafficLight.setMinimumGpa(newMinGpa);

        // Assert
        assertThat(trafficLight.getColor()).isEqualTo(newColor);
        assertThat(trafficLight.getMinimumGpa()).isEqualTo(newMinGpa);
        // Verificamos que otros campos no se han visto afectados
        assertThat(trafficLight.getDescription()).isNull();
    }

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode() para objetos iguales")
    void shouldVerifyEqualsAndHashCodeContract_happyPath() {
        // Arrange
        AcademicTrafficLight trafficLight1 = AcademicTrafficLight.builder()
                .id("atl-xyz")
                .color("YELLOW")
                .minimumGpa(2.5)
                .build();

        AcademicTrafficLight trafficLight2 = AcademicTrafficLight.builder()
                .id("atl-xyz")
                .color("YELLOW")
                .minimumGpa(2.5)
                .build();

        AcademicTrafficLight trafficLight3 = AcademicTrafficLight.builder()
                .id("atl-abc")
                .color("RED")
                .build();

        // Act & Assert
        // Dos objetos con los mismos datos deben ser iguales
        assertThat(trafficLight1).isEqualTo(trafficLight2);
        // Y deben tener el mismo hashCode
        assertThat(trafficLight1.hashCode()).isEqualTo(trafficLight2.hashCode());
        // Un objeto diferente no debe ser igual
        assertThat(trafficLight1).isNotEqualTo(trafficLight3);
    }

    @Test
    @DisplayName("Debería generar una representación en cadena (toString) que contenga los datos del objeto")
    void shouldGenerateStringRepresentation_happyPath() {
        // Arrange
        AcademicTrafficLight trafficLight = AcademicTrafficLight.builder()
                .id("atl-789")
                .color("GREEN")
                .description("Excelente")
                .build();

        // Act
        String stringRepresentation = trafficLight.toString();

        // Assert
        assertThat(stringRepresentation).isNotNull();
        assertThat(stringRepresentation).contains("AcademicTrafficLight");
        assertThat(stringRepresentation).contains("id=atl-789");
        assertThat(stringRepresentation).contains("color=GREEN");
        assertThat(stringRepresentation).contains("description=Excelente");
    }
}