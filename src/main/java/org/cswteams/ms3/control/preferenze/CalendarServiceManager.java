package org.cswteams.ms3.control.preferenze;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.exception.CalendarServiceException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CalendarServiceManager implements ICalendarServiceManager {

	@Autowired
	IHolidayController holidayController ;
	private CalendarSetting setting;
	
	public CalendarServiceManager() {
	}
	public void init(CalendarSetting setting) {
		this.setting = setting;
	}
	public List<Holiday> getHolidays() throws CalendarServiceException {
		HttpRequest request = HttpRequest.newBuilder(URI.create(this.setting.getURLHoliday())).header("accept", "application/json").build();
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response = null;
		try {
			response = client.send(request, BodyHandlers.ofString());
		} catch (IOException e) {
			throw new CalendarServiceException(e);
		} catch (InterruptedException e) {
			throw new CalendarServiceException(e);
		}
		if (response.body() != null && !response.body().isEmpty()) {
			List<Holiday> holidays = new ArrayList<Holiday>();
			try {
				JSONArray JSONData = (JSONArray) JSONValue.parse(response.body());
				for (Object item : JSONData) {
					JSONObject JSONItem = (JSONObject) item;
					holidays.add(new Holiday(
							JSONItem.get("localName").toString(),
							null, //HolidayCategory category, 
							LocalDate.parse(JSONItem.get("date").toString()).toEpochDay(), //long startDateEpochDay, Nel JSON le festività sono indicate giorno per giorno
							LocalDate.parse(JSONItem.get("date").toString()).toEpochDay(), //long endDateEpochDay, Non so se esistono festività che durono più di un giorno e per quanto ne so durono l'intera giornata
							JSONItem.get("countryCode").toString()
							));
				}
			} catch (Exception e) {
				throw new CalendarServiceException(e);
			}

			holidays.addAll(holidayController.readHolidays());
			return holidays;
		} else {
			throw new CalendarServiceException("Calendar data not found: data searched in '" + this.setting.getURLHoliday() + "'");
		}
	}
}
