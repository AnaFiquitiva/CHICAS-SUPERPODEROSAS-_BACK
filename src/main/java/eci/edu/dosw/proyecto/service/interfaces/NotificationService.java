package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.NotificationRequest;
import eci.edu.dosw.proyecto.dto.NotificationResponse;
import org.springframework.stereotype.Service;

import java.util.List;
public interface NotificationService {
    NotificationResponse createNotification(NotificationRequest notificationRequest);
    List<NotificationResponse> getUserNotifications(String userId);
    List<NotificationResponse> getUnreadUserNotifications(String userId);
    NotificationResponse markNotificationAsRead(String notificationId);
    void markAllNotificationsAsRead(String userId);
    long getUnreadNotificationCount(String userId);

    // Automated notifications
    void notifyRequestStatusChange(String requestId, String newStatus);
    void notifyWaitingListPromotion(String studentId, String groupId);
    void notifyAcademicAlert(String studentId, String alertType);
    void notifyPeriodReminder(String userId, String message);

    // Bulk operations
    void notifyFacultyDeans(String facultyId, String title, String message);
    void notifyAllAdmins(String title, String message);
}