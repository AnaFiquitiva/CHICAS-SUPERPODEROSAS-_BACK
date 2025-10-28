package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.RequestType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SpecialApprovalCaseResponse {
    private String id;
    private String requestNumber;
    private String studentName;
    private String studentCode;
    private String programName;
    private String subjectName;
    private String groupCode;
    private String approvedByName;
    private String justification;
    private String constraintsOverridden;
    private LocalDateTime approvedAt;
    private RequestType requestType;
    private String facultyName;
}