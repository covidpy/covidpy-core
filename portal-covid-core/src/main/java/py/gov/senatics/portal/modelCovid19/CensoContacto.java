package py.gov.senatics.portal.modelCovid19;

import com.fasterxml.jackson.annotation.*;

import py.gov.senatics.portal.modelCovid19.admin.Usuario;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "censo_contacto", schema = "covid19")
public class CensoContacto {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String nombres;

	@NotNull
	private String apellidos;

	@Column(name = "nro_documento")
	private String nroDocumento;

	private String telefono;

	private String domicilio;

	@Column(name = "fecha_ultimo_contacto")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date fechaUltimoContacto;

	private String tipo;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "timestamp_creacion")
	//@Column(name = "fecha_creacion")
	private Date timestampCreacion;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_modificacion")
	private Date fechaModificacion;
	
	@ManyToOne
	@NotNull
	@JoinColumn(name = "creado_por")
	@JsonIgnore
	private Usuario creadoPor;
	
	@ManyToOne
	@JoinColumn(name = "modificado_por")
	@JsonIgnore
	private Usuario modificadoPor;

	@ManyToOne
	@NotNull
	@JoinColumn(name = "id_paciente")
	@JsonIdentityInfo(
			generator = ObjectIdGenerators.PropertyGenerator.class,
			property = "id")
	@JsonIdentityReference(alwaysAsId=true)
	private Paciente paciente;

	public CensoContacto() {
	}

	public CensoContacto(Long id, String nombres, String apellidos, String nroDocumento, String telefono, String domicilio,
						 Date fechaUltimoContacto, String tipo, Date timestampCreacion, Paciente paciente) {
		this.id = id;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.nroDocumento = nroDocumento;
		this.telefono = telefono;
		this.domicilio = domicilio;
		this.fechaUltimoContacto = fechaUltimoContacto;
		this.tipo = tipo;
		this.timestampCreacion = timestampCreacion;
		this.paciente = paciente;
	}
	
	public CensoContacto(Long id, String nombres, String apellidos, String nroDocumento, String telefono, String domicilio,
			 Date fechaUltimoContacto, String tipo, Date timestampCreacion, Long idPaciente) {
		this.id = id;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.nroDocumento = nroDocumento;
		this.telefono = telefono;
		this.domicilio = domicilio;
		this.fechaUltimoContacto = fechaUltimoContacto;
		this.tipo = tipo;
		this.timestampCreacion = timestampCreacion;
		this.paciente = new Paciente(id);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getNroDocumento() {
		return nroDocumento;
	}

	public void setNroDocumento(String nroDocumento) {
		this.nroDocumento = nroDocumento;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}

	public Date getFechaUltimoContacto() {
		return fechaUltimoContacto;
	}

	public void setFechaUltimoContacto(Date fechaUltimoContacto) {
		this.fechaUltimoContacto = fechaUltimoContacto;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
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
	
	public Date getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}
	
	public Usuario getCreadoPor() {
		return creadoPor;
	}

	public void setCreadoPor(Usuario creadoPor) {
		this.creadoPor = creadoPor;
	}
	
	public Usuario getModificadoPor() {
		return modificadoPor;
	}

	public void setModificadoPor(Usuario modificadoPor) {
		this.modificadoPor = modificadoPor;
	}
}