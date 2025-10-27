package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {FacultyMapper.class, UserMapper.class})
public interface ReportMapper {

    // === Report ===
    @Mapping(source = "faculty", target = "faculty")
    @Mapping(source = "generatedBy", target = "generatedBy")
    ReportResponse toReportResponse(Report report);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "reportData", ignore = true)
    @Mapping(target = "totalRequests", ignore = true)
    @Mapping(target = "approvedRequests", ignore = true)
    @Mapping(target = "rejectedRequests", ignore = true)
    @Mapping(target = "approvalRate", ignore = true)
    @Mapping(target = "rejectionRate", ignore = true)
    @Mapping(target = "generatedBy", ignore = true)
    @Mapping(target = "generatedAt", ignore = true)
    @Mapping(target = "filePath", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    @Mapping(target = "academicPeriod", ignore = true)
    Report toReport(ReportRequest reportRequest);

    // === Métodos personalizados (para consultas nativas) ===

    // Evita el error: se implementa manualmente
    default ApprovalStatsResponse toApprovalStatsResponse(Object[] stats) {
        ApprovalStatsResponse response = new ApprovalStatsResponse();
        if (stats != null && stats.length >= 4) {
            double total = toDouble(stats[1]);
            double approved = toDouble(stats[2]);
            double rejected = toDouble(stats[3]);
            response.setApprovalRate(total > 0 ? (approved / total) * 100 : 0.0);
            response.setRejectionRate(total > 0 ? (rejected / total) * 100 : 0.0);
        } else {
            response.setApprovalRate(0.0);
            response.setRejectionRate(0.0);
        }
        return response;
    }

    default GroupDemandResponse toGroupDemandResponse(Object[] data) {
        GroupDemandResponse r = new GroupDemandResponse();
        if (data != null && data.length >= 2) {
            r.setGroupCode(String.valueOf(data[0]));
            r.setSubjectName(String.valueOf(data[1]));
        }
        return r;
    }

    // Utilidad segura
    default double toDouble(Object val) {
        if (val == null) return 0.0;
        if (val instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(val.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
