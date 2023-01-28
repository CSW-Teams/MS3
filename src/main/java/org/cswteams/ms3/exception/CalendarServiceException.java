package org.cswteams.ms3.exception;
public class CalendarServiceException extends Exception {

	private transient static final long serialVersionUID = 1L;

	public CalendarServiceException(String cause) {
		super(cause);
	}

	public CalendarServiceException(Exception e) {
		super(e);
	}
}
