package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.SystemConfigService;
import eci.edu.dosw.proyecto.utils.mappers.SystemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;
    private final AcademicPeriodConfigRepository academicPeriodConfigRepository;
    private final AlertRepository alertRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final SystemMapper systemMapper;

    @Override
    public SystemConfigResponse getSystemConfig() {
        SystemConfig config = systemConfigRepository.findActiveConfig()
                .orElseGet(this::createDefaultSystemConfig);
        return systemMapper.toSystemConfigResponse(config);
    }

    @Override
    @Transactional
    public SystemConfigResponse updateSystemConfig(SystemConfigRequest systemConfigRequest) {
        SystemConfig config = systemConfigRepository.findActiveConfig()
                .orElseGet(this::createDefaultSystemConfig);

        systemMapper.updateSystemConfigFromRequest(systemConfigRequest, config);
        config.setLastModifiedBy(getCurrentUser());
        config.setUpdatedAt(LocalDateTime.now());

        SystemConfig savedConfig = systemConfigRepository.save(config);
        return systemMapper.toSystemConfigResponse(savedConfig);
    }

    // Academic Period Configuration
    @Override
    @Transactional
    public AcademicPeriodConfigResponse createAcademicPeriodConfig(AcademicPeriodConfigRequest configRequest) {
        // Validar que no se superpongan periodos para la misma facultad
        if (hasOverlappingPeriods(configRequest)) {
            throw new RuntimeException("Existe un período configurado que se superpone con las fechas especificadas");
        }

        AcademicPeriodConfig config = new AcademicPeriodConfig();
        config.setPeriodName(configRequest.getPeriodName());
        config.setDescription(configRequest.getDescription());
        config.setStartDate(configRequest.getStartDate());
        config.setEndDate(configRequest.getEndDate());
        config.setAllowedRequestTypes(configRequest.getAllowedRequestTypes());
        config.setActive(true);
        config.setCreatedAt(LocalDateTime.now());
        config.setCreatedBy(getCurrentUser());

        if (configRequest.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(configRequest.getFacultyId())
                    .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));
            config.setFaculty(faculty);
        }

        AcademicPeriodConfig savedConfig = academicPeriodConfigRepository.save(config);
        return systemMapper.toAcademicPeriodConfigResponse(savedConfig);
    }

    @Override
    @Transactional
    public AcademicPeriodConfigResponse updateAcademicPeriodConfig(String configId, AcademicPeriodConfigRequest configRequest) {
        AcademicPeriodConfig config = academicPeriodConfigRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Configuración de período no encontrada"));

        // Validar superposición excluyendo el período actual
        if (hasOverlappingPeriods(configRequest, configId)) {
            throw new RuntimeException("Existe un período configurado que se superpone con las fechas especificadas");
        }

        config.setPeriodName(configRequest.getPeriodName());
        config.setDescription(configRequest.getDescription());
        config.setStartDate(configRequest.getStartDate());
        config.setEndDate(configRequest.getEndDate());
        config.setAllowedRequestTypes(configRequest.getAllowedRequestTypes());
        config.setUpdatedAt(LocalDateTime.now());

        if (configRequest.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(configRequest.getFacultyId())
                    .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));
            config.setFaculty(faculty);
        } else {
            config.setFaculty(null);
        }

        AcademicPeriodConfig savedConfig = academicPeriodConfigRepository.save(config);
        return systemMapper.toAcademicPeriodConfigResponse(savedConfig);
    }

    @Override
    public List<AcademicPeriodConfigResponse> getActiveAcademicPeriodConfigs() {
        List<AcademicPeriodConfig> configs = academicPeriodConfigRepository.findByActiveTrue();
        return systemMapper.toAcademicPeriodConfigResponseList(configs);
    }

    @Override
    public AcademicPeriodConfigResponse getAcademicPeriodConfigByFacultyAndDate(String facultyId) {
        LocalDateTime now = LocalDateTime.now();
        Optional<AcademicPeriodConfig> config = academicPeriodConfigRepository
                .findActiveConfigByFacultyAndDate(facultyId, now);

        return config.map(systemMapper::toAcademicPeriodConfigResponse)
                .orElseThrow(() -> new RuntimeException("No hay período activo configurado para la facultad"));
    }

    @Override
    @Transactional
    public void deactivateAcademicPeriodConfig(String configId) {
        AcademicPeriodConfig config = academicPeriodConfigRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Configuración de período no encontrada"));

        config.setActive(false);
        config.setUpdatedAt(LocalDateTime.now());
        academicPeriodConfigRepository.save(config);
    }

    // Alert Management
    @Override
    public List<AlertResponse> getActiveAlerts() {
        List<Alert> alerts = alertRepository.findByResolvedFalse();
        return systemMapper.toAlertResponseList(alerts);
    }

    @Override
    public List<AlertResponse> getAlertsByType(AlertType type) {
        List<Alert> alerts = alertRepository.findByTypeAndResolvedFalse(type);
        return systemMapper.toAlertResponseList(alerts);
    }

    @Override
    @Transactional
    public AlertResponse resolveAlert(String alertId, String resolvedBy) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));

        User resolver = userRepository.findById(resolvedBy)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        alert.setResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolvedBy(resolver);

        Alert savedAlert = alertRepository.save(alert);
        return systemMapper.toAlertResponse(savedAlert);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 6 * * ?") // Ejecutar todos los días a las 6:00 AM
    public void checkGroupOccupancyAlerts() {
        SystemConfig config = systemConfigRepository.findActiveConfig()
                .orElseGet(this::createDefaultSystemConfig);

        Double threshold = config.getOccupancyAlertThreshold() != null ?
                config.getOccupancyAlertThreshold() : 0.9; // 90% por defecto

        checkGroupOccupancyAlerts(threshold);
    }

    // Monitoring
    @Override
    @Transactional
    public void monitorGroupOccupancy() {
        SystemConfig config = systemConfigRepository.findActiveConfig()
                .orElseGet(this::createDefaultSystemConfig);

        Double threshold = config.getOccupancyAlertThreshold() != null ?
                config.getOccupancyAlertThreshold() : 0.9;

        checkGroupOccupancyAlerts(threshold);
    }

    @Override
    public List<GroupResponse> getGroupsNearingCapacity(Double threshold) {
        List<Group> groups = groupRepository.findGroupsWithOccupancy();

        return groups.stream()
                .filter(group -> {
                    double occupancy = group.getCurrentEnrollment().doubleValue() / group.getMaxCapacity().doubleValue();
                    return occupancy >= threshold;
                })
                .map(this::mapToGroupResponse)
                .collect(Collectors.toList());
    }

    // Métodos auxiliares
    private SystemConfig createDefaultSystemConfig() {
        SystemConfig config = new SystemConfig();
        config.setDefaultMaxGroupCapacity(30);
        config.setOccupancyAlertThreshold(0.9);
        config.setMaxWaitingListSize(10);
        config.setAllowSpecialApprovals(true);
        config.setSessionTimeoutMinutes(30);
        config.setMaxAcademicLoad(20);
        config.setMinAcademicLoad(12);
        config.setActive(true);
        config.setCreatedAt(LocalDateTime.now());

        return systemConfigRepository.save(config);
    }

    private boolean hasOverlappingPeriods(AcademicPeriodConfigRequest newConfig) {
        return hasOverlappingPeriods(newConfig, null);
    }

    private boolean hasOverlappingPeriods(AcademicPeriodConfigRequest newConfig, String excludeConfigId) {
        List<AcademicPeriodConfig> existingConfigs = academicPeriodConfigRepository.findByActiveTrue();

        return existingConfigs.stream()
                .filter(config -> excludeConfigId == null || !config.getId().equals(excludeConfigId))
                .filter(config -> config.getFaculty() == null ||
                        (newConfig.getFacultyId() == null || config.getFaculty().getId().equals(newConfig.getFacultyId())))
                .anyMatch(config -> isOverlapping(
                        config.getStartDate(), config.getEndDate(),
                        newConfig.getStartDate(), newConfig.getEndDate()
                ));
    }

    private boolean isOverlapping(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    private void checkGroupOccupancyAlerts(Double threshold) {
        List<Group> groups = groupRepository.findGroupsWithOccupancy();

        for (Group group : groups) {
            double occupancy = group.getCurrentEnrollment().doubleValue() / group.getMaxCapacity().doubleValue();

            if (occupancy >= threshold) {
                // Verificar si ya existe una alerta activa para este grupo
                Optional<Alert> existingAlert = alertRepository
                        .findUnresolvedAlertByTypeAndGroup(AlertType.GROUP_CAPACITY_90, group.getId());

                if (existingAlert.isEmpty()) {
                    createOccupancyAlert(group, occupancy);
                }
            }
        }
    }

    private void createOccupancyAlert(Group group, double occupancy) {
        Alert alert = new Alert();
        alert.setType(AlertType.GROUP_CAPACITY_90);
        alert.setTitle("Grupo alcanzando capacidad máxima");
        alert.setDescription(String.format(
                "El grupo %s de %s ha alcanzado el %.1f%% de su capacidad (%d/%d estudiantes)",
                group.getGroupCode(), group.getSubject().getName(),
                occupancy * 100, group.getCurrentEnrollment(), group.getMaxCapacity()
        ));
        alert.setSeverity(occupancy >= 0.95 ? "HIGH" : "MEDIUM");
        alert.setGroup(group);
        alert.setResolved(false);
        alert.setCreatedAt(LocalDateTime.now());

        alertRepository.save(alert);
    }

    private User getCurrentUser() {
        // En una aplicación real, esto vendría del contexto de seguridad
        return userRepository.findAll().stream()
                .filter(user -> "ADMIN".equals(user.getRole().getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró usuario administrador"));
    }

    private GroupResponse mapToGroupResponse(Group group) {
        GroupResponse response = new GroupResponse();
        response.setId(group.getId());
        response.setGroupCode(group.getGroupCode());
        response.setMaxCapacity(group.getMaxCapacity());
        response.setCurrentEnrollment(group.getCurrentEnrollment());
        response.setOccupancyPercentage(group.getOccupancyPercentage());
        response.setHasAvailableSpots(group.hasAvailableSpots());

        // Mapear subject básico
        SubjectBasicResponse subject = new SubjectBasicResponse();
        subject.setId(group.getSubject().getId());
        subject.setCode(group.getSubject().getCode());
        subject.setName(group.getSubject().getName());
        subject.setCredits(group.getSubject().getCredits());
        response.setSubject(subject);

        return response;
    }

    // Métodos para uso interno de otros services
    public boolean isRequestPeriodActive() {
        return isRequestPeriodActive(null);
    }

    public boolean isRequestPeriodActive(String facultyId) {
        LocalDateTime now = LocalDateTime.now();

        if (facultyId != null) {
            return academicPeriodConfigRepository.findActiveConfigByFacultyAndDate(facultyId, now)
                    .isPresent();
        } else {
            return academicPeriodConfigRepository.findActiveConfigsByDate(now)
                    .stream()
                    .findAny()
                    .isPresent();
        }
    }

    public boolean isSpecialApprovalAllowed() {
        SystemConfig config = systemConfigRepository.findActiveConfig()
                .orElseGet(this::createDefaultSystemConfig);
        return config.isAllowSpecialApprovals();
    }

    public int getMaxAcademicLoad() {
        SystemConfig config = systemConfigRepository.findActiveConfig()
                .orElseGet(this::createDefaultSystemConfig);
        return config.getMaxAcademicLoad() != null ? config.getMaxAcademicLoad() : 20;
    }

    public int getMinAcademicLoad() {
        SystemConfig config = systemConfigRepository.findActiveConfig()
                .orElseGet(this::createDefaultSystemConfig);
        return config.getMinAcademicLoad() != null ? config.getMinAcademicLoad() : 12;
    }
}