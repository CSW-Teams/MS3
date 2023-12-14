package org.cswteams.ms3.control.preferenze;

import org.cswteams.ms3.dao.HolidayDao;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.HolidayCategory;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles(value = "test")
public class TestHoliday {

    @ClassRule
    public static final SpringClassRule scr = new SpringClassRule();

    @Rule
    public final SpringMethodRule smr = new SpringMethodRule();

    @Autowired
    private HolidayDao dao ;

    private final Holiday holiday;
    private final boolean result ;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        LocalDate today = LocalDate.now() ;

        ArrayList<Object[]> retVal = new ArrayList<>() ;

        ArrayList<String> names = new ArrayList<>() ;
        names.add("A"); names.add(null) ;

        List<LocalDate> befores = new ArrayList<>(List.of(
                today.minusYears(3),
                today.plusYears(3)
        ));

        befores.add(null) ;

        ArrayList<HolidayCategory> categories = new ArrayList<>(List.of(HolidayCategory.values()));
        categories.add(null) ;
        
        ArrayList<String> locations = new ArrayList<>() ;
        locations.add("A") ; locations.add(null) ;

        for (String name : names) {
            for(LocalDate before : befores) {
                for (HolidayCategory category : categories) {
                    for (String location : locations) {
                        
                        Holiday holiday1;
                        Holiday holiday2, holiday3 = null, holiday4 = null;

                        if (before != null) {

                            if (before.isAfter(today)) {
                                holiday1 = new Holiday(name, category, before.toEpochDay(), today.minusMonths(4).toEpochDay(), location);
                            } else {
                                holiday1 = new Holiday(name, category, before.toEpochDay(), today.plusMonths(4).toEpochDay(), location);
                            }
                            holiday2 = new Holiday(name, category, before.toEpochDay(), before.minusMonths(4).toEpochDay(), location);
                            holiday3 = new Holiday(name, category, before.toEpochDay(), before.plusMonths(4).toEpochDay(), location);
                            holiday4 = new Holiday(name, category, before.toEpochDay(), 0, location);

                        } else {
                            holiday1 = new Holiday(name, category, 0, -10, location);
                            holiday2 = new Holiday(name, category, 0, -1, location);
                        }

                        

                        if (name != null && category != null && before != null) {

                            if (before.isAfter(today))
                                retVal.add(new Object[]{
                                        holiday1, false
                                });
                            else
                                retVal.add(new Object[]{
                                        holiday1, true
                                });

                            retVal.add(new Object[]{
                                    holiday3, true
                            });

                            retVal.add(new Object[]{
                                    holiday4, false
                            });
                        } else {
                            retVal.add(new Object[]{
                                    holiday1, false
                            });

                            if (before != null){
                                retVal.add(new Object[]{
                                        holiday3, false
                                });

                                retVal.add(new Object[]{
                                        holiday4, false
                                });
                            }
                        }

                        retVal.add(new Object[]{
                                holiday2, false
                        });
                    }
                }
            }
        }

        retVal.add(new Object[]{null, false});

        return retVal ;
    }

    public TestHoliday(Holiday holiday, boolean result) {
        this.holiday = holiday ;
        this.result = result ;
    }

    @Test
    public void testHolidayInsertion() {
        if(result) {
            try {
                dao.save(holiday) ;
            } catch (Exception e) {
                fail() ;
            }
        } else
        {
            try {
                dao.save(holiday) ;
            } catch (Exception e) {
                return;
            }

            fail() ;
        }
    }
}
