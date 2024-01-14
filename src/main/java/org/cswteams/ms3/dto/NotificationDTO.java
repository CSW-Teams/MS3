package org.cswteams.ms3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NotificationDTO {
    Long idNotification;
    Long idUser;
    String description;
}
