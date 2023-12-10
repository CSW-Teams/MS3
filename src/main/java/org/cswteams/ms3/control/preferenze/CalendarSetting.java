package org.cswteams.ms3.control.preferenze;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalendarSetting {
	public String getURLHoliday() {
		return URLHoliday;
	}
	public String getDateFormat() {
		return dateFormat;
	}
	private String URLHoliday;
	private String dateFormat = "yyyy-MM-dd";

	public CalendarSetting(String url1) {
		this.URLHoliday=url1;
	}
}
