package org.cswteams.ms3.control.preferenze;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalendarSetting {
	
	private String baseURL;
	private List<KeyValue<String, String>> URLParameters;
	
	private String dateFormat = "yyyy-MM-dd";
	
	public CalendarSetting(String baseURL) {
		this.baseURL = baseURL;
		this.URLParameters = new ArrayList<KeyValue<String, String>>();
	}
	
	public void addURLParameter(String value) {
		this.addURLParameter(null, value);
	}
	
	public void addURLParameter(String key, String value) {
		this.URLParameters.add(new KeyValue<>(key, value));
	}
	
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
