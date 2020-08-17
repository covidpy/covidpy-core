package py.gov.senatics.portal.modelCovid19;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "registro_ubicacion", schema = "covid19")
public class RegistroUbicacion {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "long_reportado")
    private Float longReportado;

    @Column(name = "lat_reportado")
    private Float latReportado;

    @Column(name = "lat_dispositivo")
    private Float latDispositivo;

    @Column(name = "long_dispositivo")
    private Float longDispositivo;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp_creacion")
    private Date timestampCreacion;

    @Formula("(FLOOR(extract(epoch from (now()- timestamp_creacion))/(60*60)))")
    private Long horasRetraso;

    @ManyToOne
    @JoinColumn(name = "id_paciente")
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @JsonIdentityReference(alwaysAsId=true)
    private Paciente paciente;
    
    private Double accuracy;
	private Double altitude;
	private Double speed;
	
	@Column(name = "altitude_accuracy")
	private Double altitudeAccuracy;
	
	@Column(name = "tipo_registro_ubicacion")
	private String tipoRegistroUbicacion;
	
	@Column(name = "ip_cliente")
	private String ipCliente;
	
	@Column(name = "user_agent")
	private String userAgent;
	
	@Column(name = "actividad_identificada")
	private String actividadIdentificada;
	
	@Column(name = "tipo_evento")
	private String tipoEvento;

    public RegistroUbicacion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getLongReportado() {
        return longReportado;
    }

    public void setLongReportado(Float longReportado) {
        this.longReportado = longReportado;
    }

    public Float getLatReportado() {
        return latReportado;
    }

    public void setLatReportado(Float latReportado) {
        this.latReportado = latReportado;
    }

    public Float getLatDispositivo() {
        return latDispositivo;
    }

    public void setLatDispositivo(Float latDispositivo) {
        this.latDispositivo = latDispositivo;
    }

    public Float getLongDispositivo() {
        return longDispositivo;
    }

    public void setLongDispositivo(Float longDispositivo) {
        this.longDispositivo = longDispositivo;
    }

    public Date getTimestampCreacion() {
        return timestampCreacion;
    }

    public void setTimestampCreacion(Date timestampCreacion) {
        this.timestampCreacion = timestampCreacion;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getTipoRegistroUbicacion() {
		return tipoRegistroUbicacion;
	}

	public void setTipoRegistroUbicacion(String tipoRegistroUbicacion) {
		this.tipoRegistroUbicacion = tipoRegistroUbicacion;
	}

	public String getIpCliente() {
		return ipCliente;
	}

	public void setIpCliente(String ipCliente) {
		this.ipCliente = ipCliente;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getActividadIdentificada() {
		return actividadIdentificada;
	}

	public void setActividadIdentificada(String actividadIdentificada) {
		this.actividadIdentificada = actividadIdentificada;
	}

	public String getTipoEvento() {
		return tipoEvento;
	}

	public void setTipoEvento(String tipoEvento) {
		this.tipoEvento = tipoEvento;
	}

	public Long getHorasRetraso() {
		return horasRetraso;
	}
}
