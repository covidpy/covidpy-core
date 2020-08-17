package py.gov.senatics.portal.modelCovid19.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * The persistent class for the permiso database table.
 * 
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
@Table(name = "permiso", schema = "covid19admin")
public class Permiso implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "PERMISO_ID_GENERATOR", sequenceName = "PERMISO_ID_SEQ", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERMISO_ID_GENERATOR")
	private Long id;

	private String descripcion;

	private String nombre;

	// bi-directional many-to-many association to Rol
	@ManyToMany(mappedBy = "permisos")
	private List<Rol> rols;

	public Permiso() {
	}

	public Permiso(Long id, String nombre) {
		super();
		this.id = id;
		this.nombre = nombre;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<Rol> getRols() {
		return this.rols;
	}

	public void setRols(List<Rol> rols) {
		this.rols = rols;
	}

	public void addRol(Rol rol) {

		getRols().add(rol);
		rol.getPermisos().add(this);

	}

	public void removeRol(Rol rol) {

		getRols().remove(rol);

		rol.getPermisos().remove(this);

	}

}