package py.gov.senatics.portal.modelCovid19;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "historico_clinico_datos", schema = "covid19")
public class HistoricoClinicoDatos {

    
    private Long id;
    private HistoricoClinico historicoClinico;
    private String nombreDatos;
    private String valorDatos;
    private String tipoClinicoDato;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "id_historico_clinico")
	public HistoricoClinico getHistoricoClinico() {
		return historicoClinico;
	}

	public void setHistoricoClinico(HistoricoClinico historicoClinico) {
		this.historicoClinico = historicoClinico;
	}

	@Column(name = "nombre_dato")
	public String getNombreDatos() {
		return nombreDatos;
	}

	public void setNombreDatos(String nombreDatos) {
		this.nombreDatos = nombreDatos;
	}

	@Column(name = "valor_dato")
	public String getValorDatos() {
		return valorDatos;
	}

	public void setValorDatos(String valorDatos) {
		this.valorDatos = valorDatos;
	}

	@Column(name = "tipo_clinico_dato")
	public String getTipoClinicoDato() {
		return tipoClinicoDato;
	}

	public void setTipoClinicoDato(String tipoClinicoDato) {
		this.tipoClinicoDato = tipoClinicoDato;
	}

    
}