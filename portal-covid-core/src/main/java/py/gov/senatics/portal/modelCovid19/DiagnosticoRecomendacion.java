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
@Table(name = "diagnostico_recomendacion", schema = "covid19")
public class DiagnosticoRecomendacion {

    
    private Long id;
    private HistoricoClinico historicoClinico;
    private String recomendacionTipo;
    private String recomendacionValor;
    

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

	@Column(name = "recomendacion_tipo")
	public String getRecomendacionTipo() {
		return recomendacionTipo;
	}

	public void setRecomendacionTipo(String recomendacionTipo) {
		this.recomendacionTipo = recomendacionTipo;
	}

	@Column(name = "recomendacion_valor")
	public String getRecomendacionValor() {
		return recomendacionValor;
	}

	public void setRecomendacionValor(String recomendacionValor) {
		this.recomendacionValor = recomendacionValor;
	}
   
}