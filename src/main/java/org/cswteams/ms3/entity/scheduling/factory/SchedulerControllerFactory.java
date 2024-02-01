package org.cswteams.ms3.entity.scheduling.factory;

import org.cswteams.ms3.control.scheduler.ISchedulerController;
import org.cswteams.ms3.control.scheduler.SchedulerControllerPriority;
import org.cswteams.ms3.control.scheduler.SchedulerControllerUffaPoints;

public class SchedulerControllerFactory {

    public ISchedulerController createSchedulerController(SchedulerType schedulerType) {
        switch (schedulerType) {
            case SCHEDULER_UFFAPOINTS:
                return new SchedulerControllerUffaPoints();
            case SCHEDULER_UFFAPRIORITY:
                return new SchedulerControllerPriority();
            default:
                throw new RuntimeException("wut?");
        }
    }

}