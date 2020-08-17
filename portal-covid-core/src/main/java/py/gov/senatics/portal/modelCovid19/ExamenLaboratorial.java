package py.gov.senatics.portal.modelCovid19;

import py.gov.senatics.portal.modelCovid19.admin.Usuario;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "examen_laboratorial", schema = "covid19")
public class ExamenLaboratorial {

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
    
    @Column(name = "fecha_resultado_diagnostico")
    @Temporal(TemporalType.DATE)
    private Date fechaResultadoDiagnostico;
    
    @Column(name = "fecha_prevista_toma_muestra_laboratorial")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPrevistaTomaMuestraLaboratorial;
    
    @Column(name = "identificador_externo")
    private Integer identificadorExterno;
    
    private String estado;
    
    @Column(name = "fecha_notificacion_toma_muestra_laboratorial")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaNotificacionTomaMuestraLaboratorial;
    
    @Column(name = "fecha_notificacion_resultado")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaNotificacionResultado;
    
    @Column(name = "local_toma_muestra")
    private String localTomaMuestra;

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

	public Date getFechaResultadoDiagnostico() {
		return fechaResultadoDiagnostico;
	}

	public void setFechaResultadoDiagnostico(Date fechaResultadoDiagnostico) {
		this.fechaResultadoDiagnostico = fechaResultadoDiagnostico;
	}

	public Date getFechaPrevistaTomaMuestraLaboratorial() {
		return fechaPrevistaTomaMuestraLaboratorial;
	}

	public void setFechaPrevistaTomaMuestraLaboratorial(Date fechaPrevistaTomaMuestraLaboratorial) {
		this.fechaPrevistaTomaMuestraLaboratorial = fechaPrevistaTomaMuestraLaboratorial;
	}

	public Integer getIdentificadorExterno() {
		return identificadorExterno;
	}

	public void setIdentificadorExterno(Integer identificadorExterno) {
		this.identificadorExterno = identificadorExterno;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Date getFechaNotificacionTomaMuestraLaboratorial() {
		return fechaNotificacionTomaMuestraLaboratorial;
	}

	public void setFechaNotificacionTomaMuestraLaboratorial(Date fechaNotificacionTomaMuestraLaboratorial) {
		this.fechaNotificacionTomaMuestraLaboratorial = fechaNotificacionTomaMuestraLaboratorial;
	}

	public Date getFechaNotificacionResultado() {
		return fechaNotificacionResultado;
	}

	public void setFechaNotificacionResultado(Date fechaNotificacionResultado) {
		this.fechaNotificacionResultado = fechaNotificacionResultado;
	}
	
	public String getLocalTomaMuestra() {
		return localTomaMuestra;
	}

	public void setLocalTomaMuestra(String localTomaMuestra) {
		this.localTomaMuestra = localTomaMuestra;
	}
    
}
