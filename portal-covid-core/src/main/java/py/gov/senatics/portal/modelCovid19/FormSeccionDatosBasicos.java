package py.gov.senatics.portal.modelCovid19;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "form_seccion_datos_basicos", schema = "covid19")
public class FormSeccionDatosBasicos implements Serializable {
	private Integer id;
	private String nombre;
	private String apellido;
	private String paisNacionalidad;
	private String ciudadNacimiento;
	private String tipoDocumento;
	private String numeroDocumento;
	private LocalDate fechaNacimiento;
	private String sexo;
	private String numeroCelular;
	private String numeroCelularVerificado;
	private String numeroTelefono;
	private String correoElectronico;
	private String direccionDomicilio;
	private Boolean residenteParaguay;
	private String paisEmisorDocumento;
	private RegistroFormulario registroFormulario;
	private String contrasenha;
	private String ciudadDomicilio;
	private String departamentoDomicilio;
	private String tipoRegistro;
	private Date inicioAislamiento;
	private Date fechaPrevistaTomaMuestraLaboratorial;
	
	private String localTomaMuestra;
	
	//private String rcToken;

	@Id
	/*@SequenceGenerator(name = "FormSeccionDatosBasicosIngresoPaisGenerator", sequenceName = "form_seccion_datos_basicos_ingresopais_id_seq", schema = "covid19", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FormSeccionDatosBasicosIngresoPaisGenerator")*/
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	/*public String getRcToken() {
		return rcToken;
	}

	public void setRcToken(String rcToken) {
		this.rcToken = rcToken;
	}*/

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	@Column(name = "pais_nacionalidad")
	public String getPaisNacionalidad() {
		return paisNacionalidad;
	}

	public void setPaisNacionalidad(String paisNacionalidad) {
		this.paisNacionalidad = paisNacionalidad;
	}

	@Column(name = "ciudad_nacimiento")
	public String getCiudadNacimiento() {
		return ciudadNacimiento;
	}

	public void setCiudadNacimiento(String ciudadNacimiento) {
		this.ciudadNacimiento = ciudadNacimiento;
	}

	@Column(name = "tipo_documento")
	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	@Column(name = "numero_documento")
	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	@Column(name = "fecha_nacimiento")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	@Column(name = "numero_celular")
	public String getNumeroCelular() {
		return numeroCelular;
	}

	public void setNumeroCelular(String numeroCelular) {
		this.numeroCelular = numeroCelular;
	}

	@Column(name = "numero_celular_verificado")
	public String getNumeroCelularVerificado() {
		return numeroCelularVerificado;
	}

	public void setNumeroCelularVerificado(String numeroCelularVerificado) {
		this.numeroCelularVerificado = numeroCelularVerificado;
	}

	@Column(name = "numero_telefono")
	public String getNumeroTelefono() {
		return numeroTelefono;
	}

	public void setNumeroTelefono(String numeroTelefono) {
		this.numeroTelefono = numeroTelefono;
	}

	@Column(name = "correo_electronico")
	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	@Column(name = "direccion_domicilio")
	public String getDireccionDomicilio() {
		return direccionDomicilio;
	}

	public void setDireccionDomicilio(String direccionDomicilio) {
		this.direccionDomicilio = direccionDomicilio;
	}

	@Column(name = "residente_paraguay")
	public Boolean getResidenteParaguay() {
		return residenteParaguay;
	}

	public void setResidenteParaguay(Boolean residenteParaguay) {
		this.residenteParaguay = residenteParaguay;
	}

	@Column(name = "pais_emisor_documento")
	public String getPaisEmisorDocumento() {
		return paisEmisorDocumento;
	}

	public void setPaisEmisorDocumento(String paisEmisorDocumento) {
		this.paisEmisorDocumento = paisEmisorDocumento;
	}

	@ManyToOne
	@JoinColumn(name = "id_registro_formulario")
	public RegistroFormulario getRegistroFormulario() {
		return registroFormulario;
	}

	public void setRegistroFormulario(RegistroFormulario registroFormulario) {
		this.registroFormulario = registroFormulario;
	}

	public String getContrasenha() {
		return contrasenha;
	}

	public void setContrasenha(String contrasenha) {
		this.contrasenha = contrasenha;
	}

	@Column(name = "ciudad_domicilio")
	public String getCiudadDomicilio() {
		return ciudadDomicilio;
	}

	public void setCiudadDomicilio(String ciudadDomicilio) {
		this.ciudadDomicilio = ciudadDomicilio;
	}

	@Column(name = "departamento_domicilio")
	public String getDepartamentoDomicilio() {
		return departamentoDomicilio;
	}

	public void setDepartamentoDomicilio(String departamentoDomicilio) {
		this.departamentoDomicilio = departamentoDomicilio;
	}

	@Transient
	public String getTipoRegistro() {
		return tipoRegistro;
	}

	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	@Column(name = "inicio_aislamiento")
	@Temporal(TemporalType.DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public Date getInicioAislamiento() {
		return inicioAislamiento;
	}

	public void setInicioAislamiento(Date inicioAislamiento) {
		this.inicioAislamiento = inicioAislamiento;
	}
	
	@Column(name = "fecha_prevista_toma_muestra_laboratorial")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public Date getFechaPrevistaTomaMuestraLaboratorial() {
		return fechaPrevistaTomaMuestraLaboratorial;
	}

	public void setFechaPrevistaTomaMuestraLaboratorial(Date fechaPrevistaTomaMuestraLaboratorial) {
		this.fechaPrevistaTomaMuestraLaboratorial = fechaPrevistaTomaMuestraLaboratorial;
	}
	
	@Transient
	public String getLocalTomaMuestra() {
		return localTomaMuestra;
	}

	public void setLocalTomaMuestra(String localTomaMuestra) {
		this.localTomaMuestra = localTomaMuestra;
	}
	

}