package org.cswteams.ms3.control.preferenze;

import org.cswteams.ms3.control.preferenze.CalendarSetting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CalendarSettingTest {

    private CalendarSetting calendarSetting;

    String baseURL = "https://example.com/calendar?";

    @BeforeEach
    public void setUp() {
        calendarSetting = new CalendarSetting(baseURL);
    }

    @Test
    public void testAddURLParameter() {
        String key = "date";
        String value = "2023-11-19";
        String param = "prova";
        calendarSetting.addURLParameter(key, value);
        calendarSetting.addURLParameter(param);

        assertTrue(calendarSetting.getServiceURL().contains(key + "=" + value));
        assertTrue(calendarSetting.getServiceURL().contains(param));
        assertTrue(calendarSetting.getServiceURL().contains("&"));

        System.out.println(calendarSetting.getServiceURL());

        calendarSetting.reset();
        assertEquals(baseURL, calendarSetting.getServiceURL());
    }

    @Test
    public void testInvalidAddURLParameter() {

        calendarSetting.addURLParameter("", "");
        calendarSetting.addURLParameter("");

        System.out.println(calendarSetting.getServiceURL());
        assertFalse(calendarSetting.getServiceURL().contains("" + "=" + ""));
    }

    @Test
    public void testGetDateFormat() {
        assertEquals("yyyy-MM-dd", calendarSetting.getDateFormat());
    }

    @Test
    public void testInvalidConstructor() {

        calendarSetting = new CalendarSetting("");

        assertNotEquals("", calendarSetting.getServiceURL());

        calendarSetting = new CalendarSetting(null);

        assertNull(calendarSetting.getServiceURL());
    }
}
