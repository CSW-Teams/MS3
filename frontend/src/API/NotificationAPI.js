
export  class NotificationAPI {

 async getNotification(id) {
      try {
        const response = await fetch('/api/notification/id='+id);
        if(response.status== 200){
          const body = await response.json();
          const notifications = [];
          for (let i = 0; i < body.length; i++) {
            const notification = {};
            notification.idNotification = body[i].idNotification;
            notification.idUser = body[i].idUser;
            notification.description = body[i].description;
            notifications[i] = notification;
          }
          return  notifications;
        }
        return [];
      } catch (error) {
        console.error('Errore durante il recupero delle notifiche:', error);
      }
  };
  async removeNotification(id,description,idUser) {
        try {
              const data = {}
              data.idNotification=Number(id);
              data.description=description;
              data.idUser=Number(idUser);
              // Esegui la chiamata POST con fetch
              const response = await fetch("/api/notification/updateStatus", {
                method: "PUT",
                headers: {
                  "Content-Type": "application/json",
                },
                body: JSON.stringify(data)  ,
              });
              // Verifica se la richiesta è stata eseguita con successo
              if (!response.ok) {
                throw new Error(`Error with the HTTP response: ${response.status}`);
              }
        } catch (error) {
          console.error('Errore durante il recupero delle notifiche:', error);
        }
    };
}
