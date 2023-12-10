package org.cswteams.ms3.control.preferenze;

import java.util.Date;
import java.util.List;

import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.exception.CalendarServiceException;


/**
 * 
 * 
 Request:
	GET /api/v3/PublicHolidays/{Year}/{CountryCode}
	Year 		2023
	CountryCode AT
 Response:
	date 			The date of the holiday
	localName 		Local name
	name 			English name
	countryCode 	ISO 3166-1 alpha-2
	fixed 			Is this public holiday every year on the same date
	global 			Is this public holiday in every county (federal state)
	counties 		If it is not global you found here the Federal states (ISO-3166-2)
	launchYear 		The launch year of the public holiday
	types 			The types of the public holiday, several possible:
	    				- Public
	    				- Bank (Bank holiday, banks and offices are closed)
	    				- School (School holiday, schools are closed)
	    				- Authorities (Authorities are closed)
	    				- Optional (Majority of people take a day off)
	    				- Observance (Optional festivity, no paid day off)
  JSON Example:
  	{
		"date": "2017-01-01",
	    "localName": "Neujahr",
	    "name": "New Year's Day",
	    "countryCode": "AT",
	    "fixed": true,
	    "global": true,
	    "counties": null,
	    "launchYear": 1967,
	    "types": [
	    	"Public"
	    ]
	}
 *
 */
public interface ICalendarServiceManager {
	
	public void init(CalendarSetting setting);
	
	public List<Holiday> getHolidays() throws CalendarServiceException;


}