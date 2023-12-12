package org.cswteams.ms3.control.preferenze;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalendarSetting {
	
	private final String baseURL;
	private List<KeyValue<String, String>> URLParameters;
	private String year;
	private String country;

	private final String dateFormat = "yyyy-MM-dd";
	
	public CalendarSetting(String baseURL) {
		this.baseURL = baseURL;
		this.URLParameters = new ArrayList<KeyValue<String, String>>();
	}

	public void reset(){
		this.URLParameters = new ArrayList<KeyValue<String, String>>();
	}
	
	public void addURLParameter(String value) {
		this.addURLParameter(null, value);
	}
	
	public void addURLParameter(String key, String value) {
		this.URLParameters.add(new KeyValue<>(key, value));
	}

	public String getURL(){
		String url = this.baseURL;

		url = url + "/" + year + "/" + country;


		return url;
	}


	@Deprecated
	public String getServiceURL() {
		String serviceURL = this.baseURL;
		for (KeyValue<String, String> parameter : this.URLParameters) {
			if (parameter.getKey() != null) {
				serviceURL = serviceURL.concat(parameter.getKey() + "=" + parameter.getValue());
			} else {
				serviceURL = serviceURL.concat(parameter.getValue());
			}
		}
		return serviceURL;
	}
	
	public String getDateFormat() {
		return this.dateFormat;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	private class KeyValue<K, V> implements Map.Entry<K, V> {

		K key;
		V value;
		
		public KeyValue(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		@Override
		public K getKey() {
			// TODO Auto-generated method stub
			return this.key;
		}

		@Override
		public V getValue() {
			// TODO Auto-generated method stub
			return this.value;
		}

		@Override
		public V setValue(V value) {
			// TODO Auto-generated method stub
			return this.value = value;
		}
	}	
}
