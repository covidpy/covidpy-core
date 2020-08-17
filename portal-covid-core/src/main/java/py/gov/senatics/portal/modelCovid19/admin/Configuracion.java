package py.gov.senatics.portal.modelCovid19.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The persistent class for the configuracion database table.
 * 
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
@Table(name = "configuracion", schema = "covid19admin")
public class Configuracion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "CONFIGURACION_IDCONFIGURACION_GENERATOR", sequenceName = "CONFIGURACION_ID_CONFIGURACION_SEQ", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIGURACION_IDCONFIGURACION_GENERATOR")
	@Column(name = "id_configuracion")
	private Long idConfiguracion;

	private String estado;

	@Column(name = "nombre_variable")
	private String nombreVariable;

	@Column(name = "valor_variable")
	private String valorVariable;

	public Configuracion() {
	}

	public Long getIdConfiguracion() {
		return this.idConfiguracion;
	}

	public void setIdConfiguracion(Long idConfiguracion) {
		this.idConfiguracion = idConfiguracion;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getNombreVariable() {
		return this.nombreVariable;
	}

	public void setNombreVariable(String nombreVariable) {
		this.nombreVariable = nombreVariable;
	}

	public String getValorVariable() {
		return this.valorVariable;
	}

	public void setValorVariable(String valorVariable) {
		this.valorVariable = valorVariable;
	}

}