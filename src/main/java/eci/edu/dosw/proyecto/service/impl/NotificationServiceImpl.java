package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.NotificationRequest;
import eci.edu.dosw.proyecto.dto.NotificationResponse;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.NotificationService;
import eci.edu.dosw.proyecto.utils.mappers.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final AlertRepository alertRepository;
    private final DeanRepository deanRepository;
    private final NotificationMapper notificationMapper;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;

    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationRequest notificationRequest) {
        User recipient = userRepository.findById(notificationRequest.getRecipientId())
                .orElseThrow(() -> new NotFoundException("Usuario", notificationRequest.getRecipientId()));

        Notification notification = Notification.builder()
                .type(notificationRequest.getType())
                .title(notificationRequest.getTitle())
                .message(notificationRequest.getMessage())
                .actionUrl(notificationRequest.getActionUrl())
                .read(false)
                .recipient(recipient)
                .createdAt(LocalDateTime.now())
                .build();

        // Asociar solicitud si existe
        if (notificationRequest.getRelatedRequestId() != null) {
            Request request = requestRepository.findById(notificationRequest.getRelatedRequestId())
                    .orElseThrow(() -> new NotFoundException("Solicitud", notificationRequest.getRelatedRequestId()));
            notification.setRelatedRequest(request);
        }

        // Asociar alerta si existe
        if (notificationRequest.getRelatedAlertId() != null) {
            Alert alert = alertRepository.findById(notificationRequest.getRelatedAlertId())
                    .orElseThrow(() -> new NotFoundException("Alerta", notificationRequest.getRelatedAlertId()));
            notification.setRelatedAlert(alert);
        }

        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toNotificationResponse(savedNotification);
    }

    @Override
    public List<NotificationResponse> getUserNotifications(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario", userId));
        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
        return notifications.stream()
                .map(notificationMapper::toNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getUnreadUserNotifications(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario", userId));
        List<Notification> notifications = notificationRepository.findByRecipientAndReadFalse(user);
        return notifications.stream()
                .map(notificationMapper::toNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationResponse markNotificationAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notificación", notificationId));
        notification.markAsRead();
        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toNotificationResponse(savedNotification);
    }

    @Override
    @Transactional
    public void markAllNotificationsAsRead(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario", userId));
        List<Notification> unreadNotifications = notificationRepository.findByRecipientAndReadFalse(user);
        unreadNotifications.forEach(Notification::markAsRead);
        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    public long getUnreadNotificationCount(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario", userId));
        return notificationRepository.countByRecipientAndReadFalse(user);
    }

    // === NOTIFICACIONES AUTOMÁTICAS ===

    @Override
    @Transactional
    public void notifyRequestStatusChange(String requestId, String newStatus) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud", requestId));

        String title = "Actualización de solicitud";
        String message = switch (newStatus) {
            case "APPROVED" -> "¡Tu solicitud ha sido aprobada! Ya estás inscrito en el grupo solicitado.";
            case "REJECTED" -> "Tu solicitud ha sido rechazada: " + request.getJustification();
            case "ADDITIONAL_INFO" -> "Se requiere información adicional para procesar tu solicitud.";
            case "CANCELLED" -> "Tu solicitud ha sido cancelada.";
            case "SPECIAL_APPROVAL" -> "¡Tu solicitud ha sido aprobada de forma especial!";
            default -> "Tu solicitud ha sido actualizada.";
        };

        Notification notification = Notification.builder()
                .type("REQUEST_UPDATE")
                .title(title)
                .message(message)
                .read(false)
                .recipient(request.getStudent().getUser())
                .relatedRequest(request)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void notifyWaitingListPromotion(String studentId, String groupId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante", studentId));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo", groupId));

        Notification notification = Notification.builder()
                .type("WAITING_LIST_PROMOTION")
                .title("¡Has sido promovido de la lista de espera!")
                .message("Ahora estás inscrito en el grupo " + group.getGroupCode() + " de " + group.getSubject().getName())
                .read(false)
                .recipient(student.getUser())
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void notifyAcademicAlert(String studentId, String alertType) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante", studentId));

        String title = "Alerta académica";
        String message = switch (alertType) {
            case "GROUP_CAPACITY_90" -> "El grupo al que perteneces está al 90% de su capacidad.";
            case "ACADEMIC_PROGRESS" -> "Tu progreso académico requiere atención.";
            default -> "Tienes una alerta académica pendiente.";
        };

        Notification notification = Notification.builder()
                .type("ACADEMIC_ALERT")
                .title(title)
                .message(message)
                .read(false)
                .recipient(student.getUser())
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void notifyPeriodReminder(String userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario", userId));

        Notification notification = Notification.builder()
                .type("DEADLINE_REMINDER")
                .title("Recordatorio de período")
                .message(message)
                .read(false)
                .recipient(user)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    // === NOTIFICACIONES MASIVAS ===

    @Override
    @Transactional
    public void notifyFacultyDeans(String facultyId, String title, String message) {
        List<Dean> deans = deanRepository.findByFacultyIdAndActiveTrue(facultyId);
        for (Dean dean : deans) {
            Notification notification = Notification.builder()
                    .type("FACULTY_NOTIFICATION")
                    .title(title)
                    .message(message)
                    .read(false)
                    .recipient(dean.getUser())
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional
    public void notifyAllAdmins(String title, String message) {
        List<User> admins = userRepository.findByRoleName("ADMIN");
        for (User admin : admins) {
            Notification notification = Notification.builder()
                    .type("ADMIN_NOTIFICATION")
                    .title(title)
                    .message(message)
                    .read(false)
                    .recipient(admin)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);
        }
    }
}