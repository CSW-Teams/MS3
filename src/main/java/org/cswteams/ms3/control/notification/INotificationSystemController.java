package org.cswteams.ms3.control.notification;

import org.cswteams.ms3.dto.NotificationDTO;

import java.util.Set;

public interface INotificationSystemController extends Observer {
    Set<NotificationDTO> getAllNotificationByUser(long userId);
    void changeStatus(NotificationDTO n) throws Exception;
}
