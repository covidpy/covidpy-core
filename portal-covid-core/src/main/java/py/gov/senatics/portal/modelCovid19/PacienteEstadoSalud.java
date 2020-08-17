package py.gov.senatics.portal.modelCovid19;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

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
@Table(name = "paciente_estado_salud", schema = "covid19")
public class PacienteEstadoSalud {

	private Integer id;
	private Paciente paciente;
	private HistoricoClinico historicoClinico;
	private String clasificacionPaciente;
	private Date ultimoReporteFecha;
	private String ultimoReporteTipo;
	private String ultimoRegistroTipo;
	private Boolean sintomasFiebre;
	private String sintomasFiebreUltimaMedicion;
	private Boolean sintomasTos;
	private Boolean sintomasDificultadRespirar;
	private Boolean sintomasDificultadRespirarDolorGarganta;
	private Boolean sintomasDificultadRespirarCansancioCaminar;
	private Boolean sintomasDificultadRespirarFaltaAire;
	private Boolean sintomasDificultadRespirarRinorrea;
	private Boolean sintomasDificultadRespirarCongestionNasal;
	private Boolean sintomasDiarrea;
	private String sintomasOtros;
	

	@Id
	/*@SequenceGenerator(name = "FormSeccionDatosBasicosIngresoPaisGenerator", sequenceName = "form_seccion_datos_basicos_ingresopais_id_seq", schema = "covid19", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FormSeccionDatosBasicosIngresoPaisGenerator")*/
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "id_paciente")
	@JsonIdentityInfo(
			generator = ObjectIdGenerators.PropertyGenerator.class,
			property = "id")
	@JsonIdentityReference(alwaysAsId=true)
	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	@ManyToOne
    @JoinColumn(name = "id_historico_clinico")
	public HistoricoClinico getHistoricoClinico() {
		return historicoClinico;
	}

	public void setHistoricoClinico(HistoricoClinico historicoClinico) {
		this.historicoClinico = historicoClinico;
	}

	@Column(name = "clasificacion_paciente")
	public String getClasificacionPaciente() {
		return clasificacionPaciente;
	}

	public void setClasificacionPaciente(String clasificacionPaciente) {
		this.clasificacionPaciente = clasificacionPaciente;
	}

	@Column(name = "ultimo_reporte_fecha")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getUltimoReporteFecha() {
		return ultimoReporteFecha;
	}

	public void setUltimoReporteFecha(Date ultimoReporteFecha) {
		this.ultimoReporteFecha = ultimoReporteFecha;
	}

	@Column(name = "ultimo_reporte_tipo")
	public String getUltimoReporteTipo() {
		return ultimoReporteTipo;
	}

	public void setUltimoReporteTipo(String ultimoReporteTipo) {
		this.ultimoReporteTipo = ultimoReporteTipo;
	}

	@Column(name = "ultimo_registro_tipo")
	public String getUltimoRegistroTipo() {
		return ultimoRegistroTipo;
	}

	public void setUltimoRegistroTipo(String ultimoRegistroTipo) {
		this.ultimoRegistroTipo = ultimoRegistroTipo;
	}

	@Column(name = "sintomas_fiebre")
	public Boolean getSintomasFiebre() {
		return sintomasFiebre;
	}

	public void setSintomasFiebre(Boolean sintomasFiebre) {
		this.sintomasFiebre = sintomasFiebre;
	}

	@Column(name = "sintomas_fiebre_ultima_medicion")
	public String getSintomasFiebreUltimaMedicion() {
		return sintomasFiebreUltimaMedicion;
	}

	public void setSintomasFiebreUltimaMedicion(String sintomasFiebreUltimaMedicion) {
		this.sintomasFiebreUltimaMedicion = sintomasFiebreUltimaMedicion;
	}

	@Column(name = "sintomas_tos")
	public Boolean getSintomasTos() {
		return sintomasTos;
	}

	public void setSintomasTos(Boolean sintomasTos) {
		this.sintomasTos = sintomasTos;
	}

	@Column(name = "sintomas_dificultad_respirar")
	public Boolean getSintomasDificultadRespirar() {
		return sintomasDificultadRespirar;
	}

	public void setSintomasDificultadRespirar(Boolean sintomasDificultadRespirar) {
		this.sintomasDificultadRespirar = sintomasDificultadRespirar;
	}

	@Column(name = "sintomas_dif_respirar_dolor_garganta")
	public Boolean getSintomasDificultadRespirarDolorGarganta() {
		return sintomasDificultadRespirarDolorGarganta;
	}

	public void setSintomasDificultadRespirarDolorGarganta(Boolean sintomasDificultadRespirarDolorGarganta) {
		this.sintomasDificultadRespirarDolorGarganta = sintomasDificultadRespirarDolorGarganta;
	}

	@Column(name = "sintomas_dif_respirar_cansancio_caminar")
	public Boolean getSintomasDificultadRespirarCansancioCaminar() {
		return sintomasDificultadRespirarCansancioCaminar;
	}

	public void setSintomasDificultadRespirarCansancioCaminar(Boolean sintomasDificultadRespirarCansancioCaminar) {
		this.sintomasDificultadRespirarCansancioCaminar = sintomasDificultadRespirarCansancioCaminar;
	}

	@Column(name = "sintomas_dif_respirar_falta_aire")
	public Boolean getSintomasDificultadRespirarFaltaAire() {
		return sintomasDificultadRespirarFaltaAire;
	}

	public void setSintomasDificultadRespirarFaltaAire(Boolean sintomasDificultadRespirarFaltaAire) {
		this.sintomasDificultadRespirarFaltaAire = sintomasDificultadRespirarFaltaAire;
	}

	@Column(name = "sintomas_dif_respirar_rinorrea")
	public Boolean getSintomasDificultadRespirarRinorrea() {
		return sintomasDificultadRespirarRinorrea;
	}

	public void setSintomasDificultadRespirarRinorrea(Boolean sintomasDificultadRespirarRinorrea) {
		this.sintomasDificultadRespirarRinorrea = sintomasDificultadRespirarRinorrea;
	}

	@Column(name = "sintomas_dif_respirar_congestion_nasal")
	public Boolean getSintomasDificultadRespirarCongestionNasal() {
		return sintomasDificultadRespirarCongestionNasal;
	}

	public void setSintomasDificultadRespirarCongestionNasal(Boolean sintomasDificultadRespirarCongestionNasal) {
		this.sintomasDificultadRespirarCongestionNasal = sintomasDificultadRespirarCongestionNasal;
	}

	@Column(name = "sintomas_diarrea")
	public Boolean getSintomasDiarrea() {
		return sintomasDiarrea;
	}

	public void setSintomasDiarrea(Boolean sintomasDiarrea) {
		this.sintomasDiarrea = sintomasDiarrea;
	}

	@Column(name = "sintomas_otros")
	public String getSintomasOtros() {
		return sintomasOtros;
	}

	public void setSintomasOtros(String sintomasOtros) {
		this.sintomasOtros = sintomasOtros;
	}

	
	
}