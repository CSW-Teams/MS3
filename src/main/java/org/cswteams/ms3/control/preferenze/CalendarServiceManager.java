package org.cswteams.ms3.control.preferenze;

import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.enums.HolidayCategory;
import org.cswteams.ms3.exception.CalendarServiceException;
import org.jboss.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class CalendarServiceManager implements ICalendarServiceManager {
	private static final Logger log = Logger.getLogger(CalendarServiceManager.class);

	@Autowired
	IHolidayController holidayController ;
	private CalendarSetting setting;

	public CalendarServiceManager() {
	}
	public void init(CalendarSetting setting) {
		this.setting = setting;
	}
	public List<HolidayDTO> getHolidays() throws CalendarServiceException {
		HttpRequest request = HttpRequest.newBuilder(URI.create(this.setting.getURLHoliday())).header("accept", "application/json").build();
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response;
		try {
			response = client.send(request, BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			throw new CalendarServiceException(e);
		}
		if (response.body() != null && !response.body().isEmpty()) {
			List<HolidayDTO> holidays = new ArrayList<HolidayDTO>();
			try {
				JSONArray JSONData = (JSONArray) JSONValue.parse(response.body());
				for (Object item : JSONData) {
					JSONObject JSONItem = (JSONObject) item;
					holidays.add(new HolidayDTO(
							JSONItem.get("localName").toString(),
							HolidayCategory.NATIONAL, //Default value to be changed
							LocalDate.parse(JSONItem.get("date").toString()).toEpochDay(), //long startDateEpochDay, Nel JSON le festività sono indicate giorno per giorno
							LocalDate.parse(JSONItem.get("date").toString()).toEpochDay(), //long endDateEpochDay, Non so se esistono festività che durono più di un giorno
							JSONItem.get("countryCode").toString()
							));
				}
			} catch (Exception e) {
				throw new CalendarServiceException(e);
			}
			holidayController.registerHoliday(holidays);
			return holidays;
		} else {
			throw new CalendarServiceException("Calendar data not found: data searched in '" + this.setting.getURLHoliday() + "'");
		}
	}

}
