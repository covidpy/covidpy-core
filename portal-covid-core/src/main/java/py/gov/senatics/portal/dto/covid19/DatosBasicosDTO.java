package py.gov.senatics.portal.dto.covid19;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DatosBasicosDTO {

	@NotNull(message = "Es requerido")
    @Size(min = 1, message = "Es requerido")
	private String nombre;

	@NotNull(message = "Es requerido")
	@Size(min = 1, message = "Es requerido")
	private String apellido;
	private String paisNacionalidad;
	private String ciudadNacimiento;
	private LocalDate fechaNacimiento;
	private String sexo;
	private String numeroDocumento;
	private String numeroCelular;
	private String numeroTelefono;

	private String direccionDomicilio;
	private Boolean residenteParaguay;
	private String paisEmisorDocumento;
	private String ciudadDomicilio;
	private String departamentoDomicilio;

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

	@Column(name = "fecha_nacimiento")
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

	@Column(name = "numero_telefono")
	public String getNumeroTelefono() {
		return numeroTelefono;
	}

	public void setNumeroTelefono(String numeroTelefono) {
		this.numeroTelefono = numeroTelefono;
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

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public String getNumeroCelular() {
		return numeroCelular;
	}

	public void setNumeroCelular(String numeroCelular) {
		this.numeroCelular = numeroCelular;
	}
}