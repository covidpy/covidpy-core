package py.gov.senatics.portal.dto.covid19;

public class DatosUbicacionDTO {
	
	private String activity;
	
	private String event;
	
	private String fechaHora;
	
	private Double latitude;
	
	private Double longitude;
	
	private Double accuracy;
	
	private Double altitude;
	
	private Double speed;
	
	private Double altitudeAccuracy;
	
	private Integer idUsuario;

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getFechaHora() {
		return fechaHora;
	}

	public void setFechaHora(String fechaHora) {
		this.fechaHora = fechaHora;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Double getAltitudeAccuracy() {
		return altitudeAccuracy;
	}

	public void setAltitudeAccuracy(Double altitudeAccuracy) {
		this.altitudeAccuracy = altitudeAccuracy;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	@Override
	public String toString() {
		return "[LOG] fechaHora=" + isNull(fechaHora) + ", idUsuario="+isNull(idUsuario)+", activity="+isNull(activity) + ", event=" + isNull(event) 
				+ ", latitude=" + isNull(latitude) + ", longitude=" + isNull(longitude) + ", accuracy=" + isNull(accuracy) + ", altitude="
				+ isNull(altitude) + ", speed=" + isNull(speed) + ", altitudeAccuracy=" + isNull(altitudeAccuracy) + "\n";
	}
	
	private String isNull(Object data) {
		if(data != null) {
			return data.toString();
		} 
		return "-";
	}
	
	
	
}