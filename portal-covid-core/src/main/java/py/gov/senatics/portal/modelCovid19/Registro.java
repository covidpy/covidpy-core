package py.gov.senatics.portal.modelCovid19;

import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;

@Entity
@Table(name = "registro", schema = "covid19")
public class Registro {
	public static final String
			TIPO_REPORTE_SALUD = "reporte_salud",
			ESTADO_REGISTRO = "registro",
			RESPONSABLE_PACIENTE = "paciente",
			RESPONSABLE_OPERADOR = "operador";

	private Integer id;
	private String codigoVerificacion;
	private String estado;
	private Date fechaCreacion;
	private Date fechaUltimaModificacion;
	private String responsableRegistro;
	private String tipoRegistro;
	private TipoRegistro tipoRegistroFk;
	private Usuario usuario;

	@Id
	/*@SequenceGenerator(name = "RegistroGenerator", sequenceName = "registro_id_seq", schema = "covid19", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RegistroGenerator")*/
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "codigo_verificacion")
	public String getCodigoVerificacion() {
		return codigoVerificacion;
	}

	public void setCodigoVerificacion(String codigoVerificacion) {
		this.codigoVerificacion = codigoVerificacion;
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

	@Column(name = "responsable_registro")
	public String getResponsableRegistro() {
		return responsableRegistro;
	}

	public void setResponsableRegistro(String responsableRegistro) {
		this.responsableRegistro = responsableRegistro;
	}

	@Column(name = "tipo_registro")
	public String getTipoRegistro() {
		return tipoRegistro;
	}

	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	@ManyToOne
	@JoinColumn(name = "id_usuario")
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}


	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tipo_registro", updatable = false, insertable = false)
	public TipoRegistro getTipoRegistroFk() {
		return tipoRegistroFk;
	}

	public void setTipoRegistroFk(TipoRegistro tipoRegistroFk) {
		this.tipoRegistroFk = tipoRegistroFk;
	}
}