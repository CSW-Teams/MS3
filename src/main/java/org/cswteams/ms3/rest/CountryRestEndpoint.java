package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.utils.Country;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/country")
public class CountryRestEndpoint {
	@RequestMapping(method = RequestMethod.GET, path = "/country={countryName}")
    public ResponseEntity<String> getCountryCode(@PathVariable String country){
		String code = Country.nameToCode(country);
		if (code != null) {
			return ResponseEntity.status(HttpStatus.FOUND).body(code);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Country not found");
		}
    }

}
