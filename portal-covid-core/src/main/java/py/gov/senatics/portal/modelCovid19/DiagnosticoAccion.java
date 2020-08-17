package py.gov.senatics.portal.modelCovid19;


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

@Entity
@Table(name = "diagnostico_accion", schema = "covid19")
public class DiagnosticoAccion {

    
    private Long id;
    private HistoricoClinico historicoClinico;
    private DiagnosticoRecomendacion diagnosticoRecomendacion;
    private String tipoAccion;
    private String valor;
    private String estadoEjecucion;
    private String resultadoEjecucion;
    private Date fechaHoraEjecucion;
    private Paciente paciente;  
    

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

	@ManyToOne
    @JoinColumn(name = "id_diagnostico_recomendacion")
	public DiagnosticoRecomendacion getDiagnosticoRecomendacion() {
		return diagnosticoRecomendacion;
	}

	public void setDiagnosticoRecomendacion(DiagnosticoRecomendacion diagnosticoRecomendacion) {
		this.diagnosticoRecomendacion = diagnosticoRecomendacion;
	}

	@Column(name = "tipo_accion")
	public String getTipoAccion() {
		return tipoAccion;
	}

	public void setTipoAccion(String tipoAccion) {
		this.tipoAccion = tipoAccion;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	@Column(name = "estado_ejecucion")
	public String getEstadoEjecucion() {
		return estadoEjecucion;
	}

	public void setEstadoEjecucion(String estadoEjecucion) {
		this.estadoEjecucion = estadoEjecucion;
	}

	@Column(name = "resultado_ejecucion")
	public String getResultadoEjecucion() {
		return resultadoEjecucion;
	}

	public void setResultadoEjecucion(String resultadoEjecucion) {
		this.resultadoEjecucion = resultadoEjecucion;
	}

	@Column(name = "fechahora_ejecucion")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaHoraEjecucion() {
		return fechaHoraEjecucion;
	}

	public void setFechaHoraEjecucion(Date fechaHoraEjecucion) {
		this.fechaHoraEjecucion = fechaHoraEjecucion;
	}

	@ManyToOne
    @JoinColumn(name = "id_paciente")
	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}
   
}