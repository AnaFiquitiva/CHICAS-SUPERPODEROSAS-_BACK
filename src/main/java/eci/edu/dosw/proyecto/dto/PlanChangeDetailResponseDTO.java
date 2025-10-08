package eci.edu.dosw.proyecto.dto;


import lombok.Data;

@Data
public class PlanChangeDetailResponseDTO {
    private String action;
    private String subjectId;
    private String groupId;
    private String replacementSubjectId;
    private String replacementGroupId;
    private String reason;
}