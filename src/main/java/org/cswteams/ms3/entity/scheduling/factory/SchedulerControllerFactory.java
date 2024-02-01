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
                throw new RuntimeException("Invalid scheduling algorithm");
        }
    }

    /**
     * same as before, but more handy for rest endpoints handling
     * @param algorithm
     * @return
     */
    public ISchedulerController createSchedulerController(int algorithm) {
        switch (algorithm) {
            case 1:
                return new SchedulerControllerUffaPoints();
            case 2:
                return new SchedulerControllerPriority();
            default:
                // we don't want any trouble
                return new SchedulerControllerPriority();
                //throw new RuntimeException("Invalid scheduling algorithm");
        }
    }
}