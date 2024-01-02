package org.cswteams.ms3.control.controllerscheduler;

import org.cswteams.ms3.control.scheduler.ControllerScheduler;
import org.cswteams.ms3.dao.ScheduleDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

public abstract class ControllerSchedulerTestEnv extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    protected ControllerScheduler instance;

    @Autowired
    protected ScheduleDao scheduleDao;

    @Autowired
    protected UtenteDao utenteDao;

}
