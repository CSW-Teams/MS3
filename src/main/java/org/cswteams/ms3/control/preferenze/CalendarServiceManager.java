package org.cswteams.ms3.control.preferenze;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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


public class CalendarServiceManager implements ICalendarServiceManager {
	
	private String serviceURL;
	private String dateFormat;
	
	public CalendarServiceManager() {
	}
	
	public void init(CalendarSetting setting) {
		this.serviceURL = setting.getServiceURL();
		this.dateFormat = setting.getDateFormat();
	}
	
	public List<Holiday> getHolidays() throws CalendarServiceException {
		HttpRequest request = HttpRequest.newBuilder(URI.create(this.serviceURL)).header("accept", "application/json").build();
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
					
					//System.out.println(JSONItem);
					
					Calendar calendar = new GregorianCalendar();
					if (JSONItem.get("date") != null) {
						DateFormat dateFormat = new SimpleDateFormat(this.dateFormat);
						Date date = dateFormat.parse(JSONItem.get("date").toString());
						calendar.setTime(date);
					} else {
						throw new CalendarServiceException("Calendar date not found");
					}
					
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
							((JSONItem.get("localName") != null) ? JSONItem.get("localName").toString() : null), 
							null, //HolidayCategory category, 
							calendar.getTimeInMillis(), //long startDateEpochDay, Nel JSON le festività sono indicate giorno per giorno
							calendar.getTimeInMillis(), //long endDateEpochDay, Non so se esistono festività che durono più di un giorno e per quanto ne so durono l'intera giornata
							((JSONItem.get("countryCode") != null) ? JSONItem.get("countryCode").toString() : null)
							));
				}
			} catch (Exception e) {
				throw new CalendarServiceException(e);
			}
			return holidays;
		} else {
			throw new CalendarServiceException("Calendar data not found: data searched in '" + this.serviceURL + "'");
		}
	}
	
	public List<Date> getAllSundays(int year) {
	    Calendar calendar = new GregorianCalendar();
	    calendar.set(year, 0, 1, 0, 0, 0);
	    List<Date> sundays = new ArrayList<>();
	    while (calendar.get(Calendar.YEAR) == year) {
	    	sundays.add(calendar.getTime());
	    	calendar.add(Calendar.DAY_OF_MONTH, 7);
	    }
	    return sundays;
	}
}
