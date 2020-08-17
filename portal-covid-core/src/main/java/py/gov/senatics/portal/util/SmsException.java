package py.gov.senatics.portal.util;

public class SmsException extends RuntimeException {

	public SmsException(Throwable arg0) {
		super(arg0.getMessage(),arg0);
	}
	
}
