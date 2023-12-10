package org.cswteams.ms3.control.preferenze;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.HolidayCategory;
import org.cswteams.ms3.exception.CalendarServiceException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CalendarServiceManager implements ICalendarServiceManager {
	private static final Logger log = Logger.getLogger(CalendarServiceManager.class);

	@Autowired
	IHolidayController holidayController ;
	private String serviceURL;

    public CalendarServiceManager() {
	}
	
	public void init(CalendarSetting setting) {
		this.serviceURL = setting.getURL();
    }
	
	public List<Holiday> getHolidays() throws CalendarServiceException {
		HttpRequest request = HttpRequest.newBuilder(URI.create(this.serviceURL)).header("accept", "application/json").build();
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response;

		try {
			response = client.send(request, BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			throw new CalendarServiceException(e);
		}

        if (response.body() != null && !response.body().isEmpty()) {
			List<Holiday> holidays = new ArrayList<Holiday>();
			try {
				JSONArray JSONData = (JSONArray) JSONValue.parse(response.body());
				for (Object item : JSONData) {
					JSONObject JSONItem = (JSONObject) item;

					/*holidays.add(new Holiday (
							calendar, 
							((JSONItem.get("name") != null) ? JSONItem.get("name").toString() : null), 
							((JSONItem.get("localName") != null) ? JSONItem.get("localName").toString() : null), 
							((JSONItem.get("types") != null) ? JSONItem.get("types").toString() : null),
							((JSONItem.get("launchYear") != null) ? Long.parseLong(JSONItem.get("launchYear").toString()) : null),
							((JSONItem.get("countryCode") != null) ? JSONItem.get("countryCode").toString() : null),
							((JSONItem.get("fixed") != null) ? Boolean.parseBoolean(JSONItem.get("fixed").toString()) : null),
							((JSONItem.get("global") != null) ? Boolean.parseBoolean(JSONItem.get("global").toString()): null)
							));*/
					holidays.add(new Holiday(
							JSONItem.get("localName").toString(),
							HolidayCategory.NAZIONALE, //Default value to be changed
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
			throw new CalendarServiceException("Calendar data not found: data searched in '" + this.serviceURL + "'");
		}
	}

	public List<LocalDate> getAllSundays(int year) {
		List<LocalDate> sundays = new ArrayList<>();
		LocalDate date = LocalDate.of(year, Month.JANUARY, 1);

		while (date.getYear() == year) {
			if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
				sundays.add(date);
			}
			date = date.plusDays(1);
		}
		return sundays;
	}
}
