package org.cswteams.ms3.control.scheduler.constraint_tests;

import org.junit.Test;

import javax.transaction.Transactional;

public abstract class ControllerSchedulerExtraTest extends ControllerSchedulerTest {

    public abstract void extraChecks() ;

    @Override
    @Test
    @Transactional
    public void testScheduler() {
        super.testScheduler() ;
        extraChecks() ;
    }
}
