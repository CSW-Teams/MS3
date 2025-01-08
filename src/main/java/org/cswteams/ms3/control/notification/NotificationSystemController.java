package org.cswteams.ms3.control.notification;

import org.cswteams.ms3.dao.NotificationDAO;
import org.cswteams.ms3.dao.SystemUserDAO;
import org.cswteams.ms3.dto.NotificationDTO;
import org.cswteams.ms3.entity.Notification;
import org.cswteams.ms3.entity.TenantUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
public class NotificationSystemController implements INotificationSystemController {

    @Autowired
    NotificationDAO notificationDAO;
    @Autowired
    SystemUserDAO userDAO;
    /*
    Metodo override per estendere l'observer si occupa infatti di ricevere
    aggiornamenti  entiti di cui lui é osservatore
    entity: parametro per capire quale Notificabile é prendere il suo notificatore e mettere lo stato come true e salvarlo
    */
    @Transactional
    @Override
    public void update(Notificable entity) {
        Notification newNotification=entity.getNotification();
        newNotification.setStatus(true);
        System.out.println("la notifica é per lui "+newNotification.getMessage()+" "+newNotification.getUser().getName()+newNotification.getUser().getId());
        System.out.println("salvo la notifica");
        notificationDAO.save(newNotification);
        //TODO: possibile aggiunta di una lista di utenti da notificare
    }

    @Transactional //forse non server
    @Override
    public Set<NotificationDTO> getAllNotificationByUser(long userId) {
        TenantUser user= userDAO.getOne(userId);
        if(user== null){
            return null;
        }
        Set<Notification> notifications=notificationDAO.findByUserAndStatus(user,true);
        Set<NotificationDTO> retSet=new HashSet<>();
        for(Notification entity:notifications){
            NotificationDTO n = new NotificationDTO(entity.getId(),entity.getUser().getId(),entity.getMessage());
            retSet.add(n);
        }
        return retSet;
    }

    //TODO:aggiungere una loggica di errore e lanciare a seguito un eccezzione
    @Transactional
    @Override
    public void changeStatus(NotificationDTO dto) throws Exception {
            Notification notification = notificationDAO.getOne(dto.getIdNotification());
            if(notification==null)
                throw new Exception("Selected notification does not exist");
            notification.setStatus(false);
            notificationDAO.save(notification);
    }
}
