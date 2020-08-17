package py.gov.senatics.portal.modelCovid19.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The persistent class for the rol database table.
 * 
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
@Table(name = "rol", schema = "covid19admin")
public class Rol implements Serializable {
	public static final String REPORTES = "Consulta";
	public static final String OPERADOR = "Operador";
    private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "ROL_ID_GENERATOR", sequenceName = "ROL_ID_SEQ", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROL_ID_GENERATOR")
	private Long id;

	private Boolean activo;

	private String descripcion;

	private String nombre;

	// bi-directional many-to-many association to Permiso
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	@JoinTable(name = "rol_permiso", schema = "covid19admin", joinColumns = { @JoinColumn(name = "rol_id") }, inverseJoinColumns = {
			@JoinColumn(name = "permiso_id") })
	private List<Permiso> permisos;

	// bi-directional many-to-many association to Usuario
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.EAGER)
	@JoinTable(name = "rol_usuario", schema = "covid19admin", joinColumns = { @JoinColumn(name = "rol_id") }, inverseJoinColumns = {
			@JoinColumn(name = "usuario_id") })
	private List<Usuario> usuarios;

	public Rol() {
	}

	public Rol(Long id, String nombre) {
		super();
		this.id = id;
		this.nombre = nombre;
	}

	public Rol(String nombre) {
		super();
		this.nombre = nombre;
	}
	
	public Rol(Long id, String descripcion, String nombre) {
		super();
		this.id = id;
		this.descripcion = descripcion;
		this.nombre = nombre;
	}
	
	public Rol(Long id, String descripcion, String nombre, Boolean activo) {
		super();
		this.id = id;
		this.descripcion = descripcion;
		this.nombre = nombre;
		this.activo = activo;
	}
	
	public Rol(Long id, Boolean activo, String descripcion) {
		super();
		this.id = id;
		this.descripcion = descripcion;
		this.activo = activo;
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

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Permiso> getPermisos() {
		return this.permisos;
	}

	public void setPermisos(List<Permiso> permisos) {
		this.permisos = permisos;
	}

	public List<Usuario> getUsuarios() {
		if (this.usuarios == null)
			this.usuarios = new ArrayList<Usuario>();
		return this.usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public void addUsuario(Usuario usuario) {

		getUsuarios().add(usuario);
		usuario.getRols().add(this);
	}

	public void removeUsuario(Usuario usuario) {

		getUsuarios().remove(usuario);

		usuario.getRols().remove(this);

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Rol other = (Rol) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}
//
//	@Override
//	public String toString() {
//		StringBuilder builder = new StringBuilder();
//		builder.append("Rol [");
//		if (id != null) {
//			builder.append("id=");
//			builder.append(id);
//			builder.append(", ");
//		}
//		if (nombre != null) {
//			builder.append("nombre=");
//			builder.append(nombre);
//		}
//		builder.append("]");
//		return builder.toString();
//	}

}