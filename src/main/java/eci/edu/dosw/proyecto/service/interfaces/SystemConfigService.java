package eci.edu.dosw.proyecto.service.interfaces;


import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.AlertType;
import org.springframework.stereotype.Service;

import java.util.List;
public interface SystemConfigService {
    SystemConfigResponse getSystemConfig();
    SystemConfigResponse updateSystemConfig(SystemConfigRequest systemConfigRequest);

    // Academic Period Configuration
    AcademicPeriodConfigResponse createAcademicPeriodConfig(AcademicPeriodConfigRequest configRequest);
    AcademicPeriodConfigResponse updateAcademicPeriodConfig(String configId, AcademicPeriodConfigRequest configRequest);
    List<AcademicPeriodConfigResponse> getActiveAcademicPeriodConfigs();
    AcademicPeriodConfigResponse getAcademicPeriodConfigByFacultyAndDate(String facultyId);
    void deactivateAcademicPeriodConfig(String configId);

    // Alert Management
    List<AlertResponse> getActiveAlerts();
    List<AlertResponse> getAlertsByType(AlertType type);
    AlertResponse resolveAlert(String alertId, String resolvedBy);
    void checkGroupOccupancyAlerts();

    // Monitoring
    void monitorGroupOccupancy();
    List<GroupResponse> getGroupsNearingCapacity(Double threshold);
}