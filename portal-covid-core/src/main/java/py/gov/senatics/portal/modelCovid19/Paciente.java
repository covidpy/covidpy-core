package py.gov.senatics.portal.modelCovid19;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.Formula;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "paciente", schema = "covid19")
public class Paciente {
    public static class JsonViews {
        public static class ReporteUbicacion {}
        public static class Todo {}
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    @JsonIgnore
    private Usuario usuario;
    
    @Column(name = "inicio_seguimiento")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView({JsonViews.Todo.class})
    private Date inicioSeguimiento;
    
    @Column(name = "fin_seguimiento")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView({JsonViews.Todo.class})
    private Date finSeguimiento;
    
    @Column(name = "inicio_aislamiento")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView({JsonViews.Todo.class})
    private Date inicioAislamiento;
    
    @Column(name = "clasificacion_paciente")
    @JsonView({JsonViews.Todo.class})
    private String clasificacionPaciente;
    
    @Column(name = "fin_aislamiento")
    @Temporal(TemporalType.DATE)
    @JsonView({JsonViews.Todo.class})
    private Date finAislamiento;
    
    @Column(name = "fin_previsto_aislamiento")
    @Temporal(TemporalType.DATE)
    @JsonView({JsonViews.Todo.class})
    private Date finPrevistoAislamiento;

    @Column(name = "resultado_ultimo_diagnostico")
    private String resultadoUltimoDiagnostico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resultado_ultimo_diagnostico", updatable = false, insertable = false)
    @JsonIgnore
    private TipoPacienteDiagnostico tipoPaciente;


    @Column(name = "fecha_ultimo_diagnostico")
    @Temporal(TemporalType.DATE)
    private Date fechaUltimoDiagnostico;


    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY)
    @OrderBy("timestampCreacion DESC")
    @JsonIgnore
    private List<RegistroUbicacion> ubicaciones;

    @Transient
    @JsonView({JsonViews.Todo.class})
    private String token;

    @JsonIgnore
    @Formula("(select max(u.timestamp_creacion) from covid19.registro_ubicacion u where u.id_paciente = id)")
    private Date fechaUltimoReporteUbicacion;

    @JsonIgnore
    @OneToOne(mappedBy = "paciente", fetch = FetchType.LAZY)
    private PacienteDatosPersonalesBasicos datosPersonalesBasicos;
    
    @Column(name = "tiene_sintomas")
    private String tieneSintomas;

    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY)
    @OrderBy("fechaCreacion DESC")
    @JsonIgnore
    private List<RegistroFormulario> formularios;

    @JsonIgnore
    @Formula("(select max(r.timestamp_creacion) from covid19.reporte_salud_paciente r join covid19.registro_formulario rf on r.id_registro_formulario = rf.id where rf.id_paciente = id)")
    private Date fechaUltimoReporteEstadoSalud;

    @Column(name = "fecha_inicio_sintoma")
    @Temporal(TemporalType.DATE)
    private Date fechaInicioSintoma;

    @Column(name = "fecha_exposicion")
    @Temporal(TemporalType.DATE)
    private Date fechaExposicion;

    public Paciente() {
    }
    
    public Paciente(Long id) {
    	this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

	public Date getInicioSeguimiento() {
		return inicioSeguimiento;
	}

	public void setInicioSeguimiento(Date inicioSeguimiento) {
		this.inicioSeguimiento = inicioSeguimiento;
	}

	public Date getFinSeguimiento() {
		return finSeguimiento;
	}

	public void setFinSeguimiento(Date finSeguimiento) {
		this.finSeguimiento = finSeguimiento;
	}

	public Date getInicioAislamiento() {
		return inicioAislamiento;
	}

	public void setInicioAislamiento(Date inicioAislamiento) {
		this.inicioAislamiento = inicioAislamiento;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getClasificacionPaciente() {
		return clasificacionPaciente;
	}

	public void setClasificacionPaciente(String clasificacionPaciente) {
		this.clasificacionPaciente = clasificacionPaciente;
	}

	public Date getFinAislamiento() {
		return finAislamiento;
	}

	public void setFinAislamiento(Date finAislamiento) {
		this.finAislamiento = finAislamiento;
	}

	public Date getFinPrevistoAislamiento() {
		return finPrevistoAislamiento;
	}

	public void setFinPrevistoAislamiento(Date finPrevistoAislamiento) {
		this.finPrevistoAislamiento = finPrevistoAislamiento;
	}

	public String getResultadoUltimoDiagnostico() {
		return resultadoUltimoDiagnostico;
	}

	public void setResultadoUltimoDiagnostico(String resultadoUltimoDiagnostico) {
		this.resultadoUltimoDiagnostico = resultadoUltimoDiagnostico;
	}

	public Date getFechaUltimoDiagnostico() {
		return fechaUltimoDiagnostico;
	}

	public void setFechaUltimoDiagnostico(Date fechaUltimoDiagnostico) {
		this.fechaUltimoDiagnostico = fechaUltimoDiagnostico;
	}


    public List<RegistroUbicacion> getUbicaciones() {
        return ubicaciones;
    }

    public void setUbicaciones(List<RegistroUbicacion> ubicaciones) {
        this.ubicaciones = ubicaciones;
    }

    @Transient
    @JsonView({JsonViews.ReporteUbicacion.class})
    public String getNombreCompleto() {
        return this.usuario != null ? this.usuario.getNombre() + " " + this.usuario.getApellido(): null;
    }

    @Transient
    @JsonView({JsonViews.ReporteUbicacion.class})
    public String getCedula() {
        return this.usuario != null ? this.usuario.getCedula() : null;
    }

    public Date getFechaUltimoReporteUbicacion() {
        return fechaUltimoReporteUbicacion;
    }

    public void setFechaUltimoReporteUbicacion(Date fechaUltimoReporteUbicacion) {
        this.fechaUltimoReporteUbicacion = fechaUltimoReporteUbicacion;
    }

    public TipoPacienteDiagnostico getTipoPaciente() {
        return tipoPaciente;
    }

    public void setTipoPaciente(TipoPacienteDiagnostico tipoPaciente) {
        this.tipoPaciente = tipoPaciente;
    }

    public PacienteDatosPersonalesBasicos getDatosPersonalesBasicos() {
        return datosPersonalesBasicos;
    }

    public void setDatosPersonalesBasicos(PacienteDatosPersonalesBasicos datosPersonalesBasicos) {
        this.datosPersonalesBasicos = datosPersonalesBasicos;
    }
    
    public String getTieneSintomas() {
		return tieneSintomas;
	}

	public void setTieneSintomas(String tieneSintomas) {
		this.tieneSintomas = tieneSintomas;
	}

    public List<RegistroFormulario> getFormularios() {
        return formularios;
    }

    public void setFormularios(List<RegistroFormulario> formularios) {
        this.formularios = formularios;
    }

    public Date getFechaUltimoReporteEstadoSalud() {
        return fechaUltimoReporteEstadoSalud;
    }

    public void setFechaUltimoReporteEstadoSalud(Date fechaUltimoReporteEstadoSalud) {
        this.fechaUltimoReporteEstadoSalud = fechaUltimoReporteEstadoSalud;
    }

    public Date getFechaInicioSintoma() {
        return fechaInicioSintoma;
    }

    public void setFechaInicioSintoma(Date fechaInicioSintoma) {
        this.fechaInicioSintoma = fechaInicioSintoma;
    }

    public Date getFechaExposicion() {
        return fechaExposicion;
    }

    public void setFechaExposicion(Date fechaExposicion) {
        this.fechaExposicion = fechaExposicion;
    }
}
