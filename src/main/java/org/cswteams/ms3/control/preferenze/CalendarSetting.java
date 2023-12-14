package org.cswteams.ms3.control.preferenze;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalendarSetting {
	public String getURLHoliday() {
		return URLHoliday;
	}
	private String URLHoliday;
	public CalendarSetting(String url1) {
		this.URLHoliday=url1;
	}
}
