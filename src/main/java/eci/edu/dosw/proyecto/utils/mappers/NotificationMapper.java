package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.NotificationResponse;
import eci.edu.dosw.proyecto.model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, RequestMapper.class})
public interface NotificationMapper {
    NotificationResponse toNotificationResponse(Notification notification);
}