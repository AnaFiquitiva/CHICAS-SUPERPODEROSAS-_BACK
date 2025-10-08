package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.utils.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RequestNumberGeneratorTest {

    private RequestNumberGenerator generator;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @BeforeEach
    void setUp() {
        generator = new RequestNumberGenerator();
    }

    // ==================== HAPPY PATH TESTS ====================

    @Test
    @DisplayName("Happy Path: Generar número de solicitud con formato correcto")
    void generateRequestNumber_CorrectFormat() {
        // Act
        String requestNumber = generator.generateRequestNumber();

        // Assert
        assertNotNull(requestNumber);
        assertTrue(requestNumber.startsWith("REQ-"));
        assertTrue(requestNumber.matches("REQ-\\d{8}-\\d{4}"));
    }

    @Test
    @DisplayName("Happy Path: Generar número con fecha actual")
    void generateRequestNumber_ContainsCurrentDate() {
        // Arrange
        String expectedDate = LocalDateTime.now().format(FORMATTER);

        // Act
        String requestNumber = generator.generateRequestNumber();

        // Assert
        assertTrue(requestNumber.contains(expectedDate));
    }

    @Test
    @DisplayName("Happy Path: Generar número con secuencia inicial 0001")
    void generateRequestNumber_StartsWithSequence0001() {
        // Act
        String requestNumber = generator.generateRequestNumber();

        // Assert
        assertTrue(requestNumber.endsWith("-0001"));
    }

    @Test
    @DisplayName("Happy Path: Generar múltiples números con secuencia incremental")
    void generateRequestNumber_IncrementalSequence() {
        // Act
        String request1 = generator.generateRequestNumber();
        String request2 = generator.generateRequestNumber();
        String request3 = generator.generateRequestNumber();

        // Assert
        assertTrue(request1.endsWith("-0001"));
        assertTrue(request2.endsWith("-0002"));
        assertTrue(request3.endsWith("-0003"));
    }

    @Test
    @DisplayName("Happy Path: Generar 100 números únicos")
    void generateRequestNumber_100UniqueNumbers() {
        // Arrange
        Set<String> requestNumbers = new HashSet<>();

        // Act
        for (int i = 0; i < 100; i++) {
            requestNumbers.add(generator.generateRequestNumber());
        }

        // Assert
        assertEquals(100, requestNumbers.size());
    }

    @Test
    @DisplayName("Happy Path: Secuencia formateada con ceros a la izquierda")
    void generateRequestNumber_PaddedSequence() {
        // Act
        String request1 = generator.generateRequestNumber(); // 0001
        String request9 = null;
        for (int i = 2; i <= 9; i++) {
            request9 = generator.generateRequestNumber();
        }
        String request10 = generator.generateRequestNumber(); // 0010

        // Assert
        assertTrue(request1.endsWith("-0001"));
        assertTrue(request9.endsWith("-0009"));
        assertTrue(request10.endsWith("-0010"));
    }

    @Test
    @DisplayName("Happy Path: Secuencia hasta 4 dígitos")
    void generateRequestNumber_FourDigitSequence() {
        // Arrange
        RequestNumberGenerator newGenerator = new RequestNumberGenerator();

        // Act - generar hasta llegar a 1000
        String result = null;
        for (int i = 0; i < 1000; i++) {
            result = newGenerator.generateRequestNumber();
        }

        // Assert
        assertTrue(result.endsWith("-1000"));
    }

    @Test
    @DisplayName("Happy Path: Formato completo REQ-YYYYMMDD-NNNN")
    void generateRequestNumber_CompleteFormat() {
        // Arrange
        String expectedDate = LocalDateTime.now().format(FORMATTER);
        String expectedPrefix = "REQ-" + expectedDate + "-";

        // Act
        String requestNumber = generator.generateRequestNumber();

        // Assert
        assertTrue(requestNumber.startsWith(expectedPrefix));
        assertEquals(18, requestNumber.length()); // REQ-(8 digits)-(4 digits)
    }

    @Test
    @DisplayName("Happy Path: Números consecutivos tienen misma fecha")
    void generateRequestNumber_SameDateForConsecutive() {
        // Act
        String request1 = generator.generateRequestNumber();
        String request2 = generator.generateRequestNumber();

        // Assert
        String date1 = request1.substring(4, 12);
        String date2 = request2.substring(4, 12);
        assertEquals(date1, date2);
    }

    @Test
    @DisplayName("Happy Path: Thread-safe - secuencia atómica")
    void generateRequestNumber_ThreadSafe() throws InterruptedException {
        // Arrange
        Set<String> requestNumbers = new HashSet<>();
        int threadCount = 10;
        int requestsPerThread = 10;
        Thread[] threads = new Thread[threadCount];

        // Act
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < requestsPerThread; j++) {
                    synchronized (requestNumbers) {
                        requestNumbers.add(generator.generateRequestNumber());
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        assertEquals(threadCount * requestsPerThread, requestNumbers.size());
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    @DisplayName("Edge Case: Secuencia mayor a 9999")
    void generateRequestNumber_SequenceOver9999() {
        // Arrange
        RequestNumberGenerator newGenerator = new RequestNumberGenerator();

        // Act - generar 10000 números
        String result = null;
        for (int i = 0; i < 10000; i++) {
            result = newGenerator.generateRequestNumber();
        }

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("-10000"));
    }

    @Test
    @DisplayName("Edge Case: Llamadas rápidas consecutivas")
    void generateRequestNumber_RapidConsecutiveCalls() {
        // Act
        String request1 = generator.generateRequestNumber();
        String request2 = generator.generateRequestNumber();
        String request3 = generator.generateRequestNumber();
        String request4 = generator.generateRequestNumber();
        String request5 = generator.generateRequestNumber();

        // Assert
        assertNotEquals(request1, request2);
        assertNotEquals(request2, request3);
        assertNotEquals(request3, request4);
        assertNotEquals(request4, request5);
    }

    @Test
    @DisplayName("Edge Case: Todos los números generados tienen mismo formato")
    void generateRequestNumber_ConsistentFormat() {
        // Act & Assert
        for (int i = 0; i < 50; i++) {
            String requestNumber = generator.generateRequestNumber();
            assertTrue(requestNumber.matches("REQ-\\d{8}-\\d{4,}"));
        }
    }

    @Test
    @DisplayName("Edge Case: Secuencia no se resetea entre llamadas")
    void generateRequestNumber_SequenceDoesNotReset() {
        // Act
        String request1 = generator.generateRequestNumber();
        int sequence1 = extractSequence(request1);

        String request2 = generator.generateRequestNumber();
        int sequence2 = extractSequence(request2);

        String request3 = generator.generateRequestNumber();
        int sequence3 = extractSequence(request3);

        // Assert
        assertEquals(sequence1 + 1, sequence2);
        assertEquals(sequence2 + 1, sequence3);
    }

    @Test
    @DisplayName("Edge Case: Formato de fecha YYYYMMDD")
    void generateRequestNumber_DateFormatYYYYMMDD() {
        // Act
        String requestNumber = generator.generateRequestNumber();
        String datePart = requestNumber.substring(4, 12);

        // Assert
        assertEquals(8, datePart.length());
        assertTrue(datePart.matches("\\d{8}"));

        // Validar que puede parsearse como fecha
        assertDoesNotThrow(() -> {
            LocalDateTime.parse(datePart + "0000", DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        });
    }

    @Test
    @DisplayName("Edge Case: Generar 1000 números - todos únicos y ordenados")
    void generateRequestNumber_1000UniqueOrdered() {
        // Arrange
        Set<String> uniqueNumbers = new HashSet<>();
        String previous = null;

        // Act
        for (int i = 0; i < 1000; i++) {
            String current = generator.generateRequestNumber();
            uniqueNumbers.add(current);

            if (previous != null) {
                int prevSeq = extractSequence(previous);
                int currSeq = extractSequence(current);
                assertTrue(currSeq > prevSeq, "Sequence should be increasing");
            }
            previous = current;
        }

        // Assert
        assertEquals(1000, uniqueNumbers.size());
    }

    @Test
    @DisplayName("Edge Case: Prefijo siempre es REQ-")
    void generateRequestNumber_AlwaysStartsWithREQ() {
        // Act & Assert
        for (int i = 0; i < 20; i++) {
            String requestNumber = generator.generateRequestNumber();
            assertTrue(requestNumber.startsWith("REQ-"));
        }
    }

    @Test
    @DisplayName("Edge Case: Longitud mínima es 18 caracteres")
    void generateRequestNumber_MinimumLength() {
        // Act
        String requestNumber = generator.generateRequestNumber();

        // Assert
        assertTrue(requestNumber.length() >= 18);
    }

    @Test
    @DisplayName("Edge Case: Componentes separados por guiones")
    void generateRequestNumber_HyphenSeparated() {
        // Act
        String requestNumber = generator.generateRequestNumber();
        String[] parts = requestNumber.split("-");

        // Assert
        assertEquals(3, parts.length);
        assertEquals("REQ", parts[0]);
        assertEquals(8, parts[1].length());
        assertTrue(parts[2].length() >= 4);
    }

    @Test
    @DisplayName("Edge Case: Sin caracteres especiales excepto guiones")
    void generateRequestNumber_OnlyAlphanumericAndHyphens() {
        // Act
        String requestNumber = generator.generateRequestNumber();

        // Assert
        assertTrue(requestNumber.matches("[A-Z0-9-]+"));
    }

    // ==================== HELPER METHODS ====================

    private int extractSequence(String requestNumber) {
        String[] parts = requestNumber.split("-");
        return Integer.parseInt(parts[2]);
    }
}