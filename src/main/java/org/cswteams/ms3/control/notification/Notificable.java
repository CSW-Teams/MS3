package org.cswteams.ms3.control.notification;

import org.cswteams.ms3.entity.Notification;

public abstract class Notificable {
    protected Observer observer = null;

    protected void Notify(){
        observer.update(this);
    }
    public void attach(Observer observer){
        this.observer=observer;
    }

    private Notificable() {};

    protected Notificable(Observer observer){
       attach(observer);
    }
    public abstract Notification getNotification();
}
