package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.Alert;
import eci.edu.dosw.proyecto.model.AlertType;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.model.User;
import eci.edu.dosw.proyecto.repository.AlertRepository;
import eci.edu.dosw.proyecto.repository.GroupRepository;
import eci.edu.dosw.proyecto.repository.UserRepository;
import eci.edu.dosw.proyecto.service.interfaces.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void checkAndCreateGroupCapacityAlert(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo", groupId));

        if (group.shouldTrigger90PercentAlert()) {
            createGroupCapacityAlertIfNotExists(group);
        }
    }

    @Override
    @Transactional
    public void resolveGroupCapacityAlert(String groupId) {
        Optional<Alert> existingAlert = alertRepository.findUnresolvedAlertByTypeAndGroup(
                AlertType.GROUP_CAPACITY_90, groupId
        );

        if (existingAlert.isPresent()) {
            Alert alert = existingAlert.get();
            alert.setResolved(true);
            alert.setResolvedAt(LocalDateTime.now());
            alert.setResolvedBy(getCurrentAdminUser());
            alertRepository.save(alert);
        }
    }

    @Override
    public List<Alert> getActiveAlerts() {
        return alertRepository.findByResolvedFalse();
    }

    @Override
    public List<Alert> getAlertsByType(AlertType type) {
        return alertRepository.findByTypeAndResolvedFalse(type);
    }

    @Override
    @Transactional
    public Alert resolveAlert(String alertId, String resolvedBy) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new NotFoundException("Alerta", alertId));

        User resolver = userRepository.findById(resolvedBy)
                .orElseThrow(() -> new NotFoundException("Usuario", resolvedBy));

        alert.setResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolvedBy(resolver);
        return alertRepository.save(alert);
    }

    // === MÉTODOS AUXILIARES ===

    private void createGroupCapacityAlertIfNotExists(Group group) {
        boolean alertExists = alertRepository.findUnresolvedAlertByTypeAndGroup(
                AlertType.GROUP_CAPACITY_90, group.getId()
        ).isPresent();

        if (!alertExists) {
            Alert alert = Alert.builder()
                    .type(AlertType.GROUP_CAPACITY_90)
                    .title("Grupo al 90% de capacidad")
                    .description("El grupo " + group.getGroupCode() + " de " +
                            group.getSubject().getName() + " ha alcanzado el 90% de su capacidad")
                    .severity("HIGH")
                    .group(group)
                    .resolved(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            alertRepository.save(alert);
        }
    }

    private User getCurrentAdminUser() {
        List<User> admins = userRepository.findByRoleName("ADMIN");
        return admins.isEmpty() ? null : admins.get(0);
    }
}