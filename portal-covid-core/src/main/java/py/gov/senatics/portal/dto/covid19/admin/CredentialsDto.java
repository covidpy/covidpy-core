package py.gov.senatics.portal.dto.covid19.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class CredentialsDto {

	private String username;

	private String password;

	private String password2;

	private String oneTimeToken;
	
	private String fcmRegistrationToken;
	
	private String so;

	public CredentialsDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public String getOneTimeToken() {
		return oneTimeToken;
	}

	public void setOneTimeToken(String oneTimeToken) {
		this.oneTimeToken = oneTimeToken;
	}

	public String getFcmRegistrationToken() {
		return fcmRegistrationToken;
	}

	public void setFcmRegistrationToken(String fcmRegistrationToken) {
		this.fcmRegistrationToken = fcmRegistrationToken;
	}

	public String getSo() {
		return so;
	}

	public void setSo(String so) {
		this.so = so;
	}
	
}
