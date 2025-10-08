package eci.edu.dosw.proyecto.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RequestNumberGenerator {

    private final AtomicInteger sequence = new AtomicInteger(1);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String generateRequestNumber() {
        String datePart = LocalDateTime.now().format(FORMATTER);
        int sequenceNumber = sequence.getAndIncrement();
        return String.format("REQ-%s-%04d", datePart, sequenceNumber);
    }
}
