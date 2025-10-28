package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.model.Alert;
import eci.edu.dosw.proyecto.model.AlertType;
import org.springframework.stereotype.Service;

import java.util.List;
public interface AlertService {
    void checkAndCreateGroupCapacityAlert(String groupId);
    void resolveGroupCapacityAlert(String groupId);
    List<Alert> getActiveAlerts();
    List<Alert> getAlertsByType(AlertType type);
    Alert resolveAlert(String alertId, String resolvedBy);
}