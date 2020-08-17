package py.gov.senatics.portal.modelCovid19;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "registro_formulario", schema = "covid19")
public class RegistroFormulario {
	public static final String
			NOMBRE_PACIENTE = "paciente",
			ESTADO_COMPLETO = "completo";

	private Integer id;
	private Registro registro;
	private Boolean registroFormularioAcompanante;
	private String nombre;
	private String estado;
	private Date fechaCreacion;
	private Date fechaUltimaModificacion;
	private List<ReporteSalud> reportes;

	private Paciente paciente;

	@Id
	/*@SequenceGenerator(name = "RegistroFormularioGenerator", sequenceName = "registro_formulario_id_seq", schema = "covid19", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RegistroFormularioGenerator")*/
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "id_registro")
	public Registro getRegistro() {
		return registro;
	}

	public void setRegistro(Registro registro) {
		this.registro = registro;
	}

	@Column(name = "registro_formulario_acompanante")
	public Boolean getRegistroFormularioAcompanante() {
		return registroFormularioAcompanante;
	}

	public void setRegistroFormularioAcompanante(Boolean registroFormularioAcompanante) {
		this.registroFormularioAcompanante = registroFormularioAcompanante;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Column(name = "fecha_creacion")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	@Column(name = "fecha_ultima_modificacion")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaUltimaModificacion() {
		return fechaUltimaModificacion;
	}

	public void setFechaUltimaModificacion(Date fechaUltimaModificacion) {
		this.fechaUltimaModificacion = fechaUltimaModificacion;
	}

	@ManyToOne
	@JoinColumn(name = "id_paciente")
	@JsonIdentityInfo(
			generator = ObjectIdGenerators.PropertyGenerator.class,
			property = "id")
	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	@OneToMany(mappedBy = "registroFormulario")
	@OrderBy("timestampCreacion DESC")
	@JsonIgnore
	public List<ReporteSalud> getReportes() {
		return reportes;
	}

	public void setReportes(List<ReporteSalud> reportes) {
		this.reportes = reportes;
	}
}