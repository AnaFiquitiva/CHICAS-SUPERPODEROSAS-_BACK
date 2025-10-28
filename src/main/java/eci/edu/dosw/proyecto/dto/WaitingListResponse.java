package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WaitingListResponse {
    private String id;
    private GroupBasicResponse group;
    private List<WaitingListEntryResponse> entries;
    private Integer totalEntries;
    private LocalDateTime createdAt;
}

