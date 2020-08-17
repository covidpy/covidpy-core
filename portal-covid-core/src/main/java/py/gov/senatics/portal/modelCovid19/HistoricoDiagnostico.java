package py.gov.senatics.portal.modelCovid19;


import com.fasterxml.jackson.annotation.JsonValue;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "historico_diagnostico", schema = "covid19")
public class HistoricoDiagnostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    @Column(name = "resultado_diagnostico")
    private String resultadoDiagnostico;
    
    @Column(name = "fecha_diagnostico")
    @Temporal(TemporalType.DATE)
    private Date fechaDiagnostico;
    
    @Column(name = "fin_previsto_aislamiento")
    @Temporal(TemporalType.DATE)
    private Date finPrevistoAislamiento;
    
    @Column(name = "fecha_modificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getResultadoDiagnostico() {
		return resultadoDiagnostico;
	}

	public void setResultadoDiagnostico(String resultadoDiagnostico) {
		this.resultadoDiagnostico = resultadoDiagnostico;
	}

	public Date getFechaDiagnostico() {
		return fechaDiagnostico;
	}

	public void setFechaDiagnostico(Date fechaDiagnostico) {
		this.fechaDiagnostico = fechaDiagnostico;
	}

	public Date getFinPrevistoAislamiento() {
		return finPrevistoAislamiento;
	}

	public void setFinPrevistoAislamiento(Date finPrevistoAislamiento) {
		this.finPrevistoAislamiento = finPrevistoAislamiento;
	}

	public Date getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}
    
}
