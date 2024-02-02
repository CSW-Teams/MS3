package org.cswteams.ms3.control.scheduler;

import org.cswteams.ms3.entity.Schedule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import javax.transaction.Transactional;
import java.time.LocalDate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class ControllerSchedulerTest {

    @Before
    public abstract void populateDB() ;

    protected boolean isPossible ;
    protected LocalDate start ;

    protected LocalDate end ;

    @Autowired
    private ISchedulerController controller ;

    @Test
    public void testScheduler() {

        Schedule schedule = controller.createSchedule(start, end) ;

        if(isPossible) {
            assertNotNull(schedule) ;
        } else {
            assertNull(schedule);
        }
    }
}
