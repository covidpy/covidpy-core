package py.gov.senatics.portal.modelCovid19.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import py.gov.senatics.portal.modelCovid19.Paciente;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The persistent class for the usuario database table.
 * 
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
@Table(name = "usuario", schema = "covid19admin")
public class Usuario implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	/*@SequenceGenerator(name = "USUARIO_ID_GENERATOR", sequenceName = "USUARIO_ID_SEQ", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USUARIO_ID_GENERATOR")*/
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Boolean activo;

	private String apellido;

	private String cedula;

	private String celular;

	private String email;

	@Column(name = "estado_contacto")
	private Boolean estadoContacto;

	private String nombre;

	private String password;

	private String telefono;

	@Column(name = "token_reset")
	private String tokenReset;

	private String username;
	
	@Column(name = "fcm_registration_token")
	private String fcmRegistrationToken;
	
	@Column(name = "sistema_operativo")
	private String sistemaOperativo;

	@OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
	@JsonIgnore
	private Paciente paciente;

	// bi-directional many-to-many association to Rol
	@ManyToMany(mappedBy = "usuarios", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("usuarios")
	private List<Rol> rols;

	public Usuario() {
	}

	public Usuario(Long id, Boolean activo, String username) {
		super();
		this.id = id;
		this.activo = activo;
		this.username = username;
	}

	public Usuario(Long id, Boolean activo, String nombre, String username) {
		super();
		this.id = id;
		this.activo = activo;
		this.nombre = nombre;
		this.username = username;
	}

	public Usuario(Long id, Boolean activo, String cedula, String nombre, String username) {
		super();
		this.id = id;
		this.activo = activo;
		this.cedula = cedula;
		this.nombre = nombre;
		this.username = username;
	}

	public Usuario(Long id, Boolean activo, String apellido, String nombre, String password, String username) {
		super();
		this.id = id;
		this.activo = activo;
		this.apellido = apellido;
		this.nombre = nombre;
		this.password = password;
		this.username = username;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getActivo() {
		return this.activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public String getApellido() {
		return this.apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getCedula() {
		return this.cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getCelular() {
		return this.celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getEstadoContacto() {
		return this.estadoContacto;
	}

	public void setEstadoContacto(Boolean estadoContacto) {
		this.estadoContacto = estadoContacto;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getTokenReset() {
		return this.tokenReset;
	}

	public void setTokenReset(String tokenReset) {
		this.tokenReset = tokenReset;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFcmRegistrationToken() {
		return fcmRegistrationToken;
	}

	public void setFcmRegistrationToken(String fcmRegistrationToken) {
		this.fcmRegistrationToken = fcmRegistrationToken;
	}

	public String getSistemaOperativo() {
		return sistemaOperativo;
	}

	public void setSistemaOperativo(String sistemaOperativo) {
		this.sistemaOperativo = sistemaOperativo;
	}

	public List<Rol> getRols() {
		if (this.rols == null)
			this.rols = new ArrayList<Rol>();
		return this.rols;
	}

	public void setRols(List<Rol> rols) {
		this.rols = rols;
	}

	public void addRol(Rol rol) {

		getRols().add(rol);
		rol.getUsuarios().add(this);

	}

	public void removeRol(Rol rol) {

		getRols().remove(rol);

		rol.getUsuarios().remove(this);

	}

	public void removeAll() {

		for (Rol rol : getRols()) {
			rol.getUsuarios().remove(this);
		}
		getRols().clear();
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Usuario other = (Usuario) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}

}