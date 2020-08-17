package py.gov.senatics.portal.modelCovid19;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

import py.gov.senatics.portal.modelCovid19.admin.Usuario;

@Entity
@Table(name = "form_seccion_datos_clinicos", schema = "covid19")
public class FormSeccionDatosClinicos {
	private Integer id;
	private RegistroFormulario registroFormulario;
	/*private String medioTransporte;
	private String tipoEmpresaTransporte;
	private String transporteNoAsiento;
	private Date fechaPartida;
	private Date fechaLlegada;
	private String paisOrigen;
	private String ciudadOrigen;*/
	private String ocupacion;
	//private String paisesCirculacion;
	private Boolean sintomasFiebre;
	private String sintomasFiebreValor;
	private Boolean sintomasTos;
	private Boolean sintomasDificultadRespirar;
	private Boolean sintomasDificultadRespirarDolorGarganta;
	private Boolean sintomasDificultadRespirarCansancioCaminar;
	private Boolean sintomasDificultadRespirarFaltaAire;
	private Boolean sintomasDificultadRespirarRinorrea;
	private Boolean sintomasDificultadRespirarCongestionNasal;
	private Boolean sintomasDiarrea;
	//private Boolean sintomasDolorGarganta;
	private String sintomasOtro;
	private Boolean declaracionAgreement;
	private Usuario usuario;
	private Boolean evaluacionRiesgoViveSolo;
	private Boolean evaluacionRiesgoTieneHabitacionPropria;
	private Boolean enfermedadBaseCardiopatiaCronica;
	private Boolean enfermedadBasePulmonarCronico;
	private Boolean enfermedadBaseAsma;
	private Boolean enfermedadBaseDiabetes;
	private Boolean enfermedadBaseRenalCronico;
	private Boolean enfermedadBaseInmunodeprimido; 
	private Boolean enfermedadBaseNeurologica;
	private Boolean enfermedadBaseSindromedown;
	private Boolean enfermedadBaseObesidad;
	private Integer idRegistro;
	private Boolean evaluacionRiesgoUsomedicamento;
    private String evaluacionRiesgoMedicamento;
    private Boolean enfermedadBaseHepaticaGrave;
    private Boolean enfermedadBaseHipertensionArterial;
    private Boolean enfermedadBaseAutoinmune;
    private Boolean enfermedadBaseNeoplasias;
    private Boolean enfermedadBaseEPOC;
    private List<String> resultados=new ArrayList<String>();

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
	@JoinColumn(name = "id_registro_formulario")
	public RegistroFormulario getRegistroFormulario() {
		return registroFormulario;
	}

	public void setRegistroFormulario(RegistroFormulario registroFormulario) {
		this.registroFormulario = registroFormulario;
	}

	/*@Column(name = "medio_transporte")
	public String getMedioTransporte() {
		return medioTransporte;
	}

	public void setMedioTransporte(String medioTransporte) {
		this.medioTransporte = medioTransporte;
	}

	@Column(name = "tipo_empresa_transporte")
	public String getTipoEmpresaTransporte() {
		return tipoEmpresaTransporte;
	}

	public void setTipoEmpresaTransporte(String tipoEmpresaTransporte) {
		this.tipoEmpresaTransporte = tipoEmpresaTransporte;
	}

	@Column(name = "transporte_no_asiento")
	public String getTransporteNoAsiento() {
		return transporteNoAsiento;
	}

	public void setTransporteNoAsiento(String transporteNoAsiento) {
		this.transporteNoAsiento = transporteNoAsiento;
	}

	@Column(name = "fecha_partida")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public Date getFechaPartida() {
		return fechaPartida;
	}

	public void setFechaPartida(Date fechaPartida) {
		this.fechaPartida = fechaPartida;
	}

	@Column(name = "fecha_llegada")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
	public Date getFechaLlegada() {
		return fechaLlegada;
	}

	public void setFechaLlegada(Date fechaLlegada) {
		this.fechaLlegada = fechaLlegada;
	}

	@Column(name = "pais_origen")
	public String getPaisOrigen() {
		return paisOrigen;
	}

	public void setPaisOrigen(String paisOrigen) {
		this.paisOrigen = paisOrigen;
	}

	@Column(name = "ciudad_origen")
	public String getCiudadOrigen() {
		return ciudadOrigen;
	}

	public void setCiudadOrigen(String ciudadOrigen) {
		this.ciudadOrigen = ciudadOrigen;
	}*/

	public String getOcupacion() {
		return ocupacion;
	}

	public void setOcupacion(String ocupacion) {
		this.ocupacion = ocupacion;
	}

	/*@Column(name = "paises_circulacion")
	public String getPaisesCirculacion() {
		return paisesCirculacion;
	}

	public void setPaisesCirculacion(String paisesCirculacion) {
		this.paisesCirculacion = paisesCirculacion;
	}*/

	@Column(name = "sintomas_fiebre")
	public Boolean getSintomasFiebre() {
		return sintomasFiebre;
	}

	public void setSintomasFiebre(Boolean sintomasFiebre) {
		this.sintomasFiebre = sintomasFiebre;
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

	/*@Column(name = "sintomas_dolor_garganta")
	public Boolean getSintomasDolorGarganta() {
		return sintomasDolorGarganta;
	}

	public void setSintomasDolorGarganta(Boolean sintomasDolorGarganta) {
		this.sintomasDolorGarganta = sintomasDolorGarganta;
	}*/

	@Column(name = "sintomas_otros")
	public String getSintomasOtro() {
		return sintomasOtro;
	}

	public void setSintomasOtro(String sintomasOtro) {
		this.sintomasOtro = sintomasOtro;
	}

	@Column(name = "declaracion_agreement")
	public Boolean getDeclaracionAgreement() {
		return declaracionAgreement;
	}

	public void setDeclaracionAgreement(Boolean declaracionAgreement) {
		this.declaracionAgreement = declaracionAgreement;
	}

	@Transient
	public Integer getIdRegistro() {
		return idRegistro;
	}

	public void setIdRegistro(Integer idRegistro) {
		this.idRegistro = idRegistro;
	}

	@Column(name = "sintomas_fiebre_valor")
	public String getSintomasFiebreValor() {
		return sintomasFiebreValor;
	}

	public void setSintomasFiebreValor(String sintomasFiebreValor) {
		this.sintomasFiebreValor = sintomasFiebreValor;
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

	@ManyToOne
	@JoinColumn(name = "id_usuario")
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	@Column(name = "evaluacion_riesgo_vive_solo")
	public Boolean getEvaluacionRiesgoViveSolo() {
		return evaluacionRiesgoViveSolo;
	}

	public void setEvaluacionRiesgoViveSolo(Boolean evaluacionRiesgoViveSolo) {
		this.evaluacionRiesgoViveSolo = evaluacionRiesgoViveSolo;
	}

	@Column(name = "evaluacion_riesgo_tiene_habitacion_propria")
	public Boolean getEvaluacionRiesgoTieneHabitacionPropria() {
		return evaluacionRiesgoTieneHabitacionPropria;
	}

	public void setEvaluacionRiesgoTieneHabitacionPropria(Boolean evaluacionRiesgoTieneHabitacionPropria) {
		this.evaluacionRiesgoTieneHabitacionPropria = evaluacionRiesgoTieneHabitacionPropria;
	}

	@Column(name = "enfermedad_base_cardiopatia_cronica")
	public Boolean getEnfermedadBaseCardiopatiaCronica() {
		return enfermedadBaseCardiopatiaCronica;
	}

	public void setEnfermedadBaseCardiopatiaCronica(Boolean enfermedadBaseCardiopatiaCronica) {
		this.enfermedadBaseCardiopatiaCronica = enfermedadBaseCardiopatiaCronica;
	}

	@Column(name = "enfermedad_base_pulmonar_cronico")
	public Boolean getEnfermedadBasePulmonarCronico() {
		return enfermedadBasePulmonarCronico;
	}

	public void setEnfermedadBasePulmonarCronico(Boolean enfermedadBasePulmonarCronico) {
		this.enfermedadBasePulmonarCronico = enfermedadBasePulmonarCronico;
	}
	
	@Column(name = "enfermedad_base_hepatica_grave")
	public Boolean getEnfermedadBaseHepaticaGrave() {
		return enfermedadBaseHepaticaGrave;
	}

	public void setEnfermedadBaseHepaticaGrave(Boolean enfermedadBaseHepaticaGrave) {
		this.enfermedadBaseHepaticaGrave = enfermedadBaseHepaticaGrave;
	}

	@Column(name = "enfermedad_base_asma")
	public Boolean getEnfermedadBaseAsma() {
		return enfermedadBaseAsma;
	}

	public void setEnfermedadBaseAsma(Boolean enfermedadBaseAsma) {
		this.enfermedadBaseAsma = enfermedadBaseAsma;
	}

	@Column(name = "enfermedad_base_diabetes")
	public Boolean getEnfermedadBaseDiabetes() {
		return enfermedadBaseDiabetes;
	}

	public void setEnfermedadBaseDiabetes(Boolean enfermedadBaseDiabetes) {
		this.enfermedadBaseDiabetes = enfermedadBaseDiabetes;
	}

	@Column(name = "enfermedad_base_renal_cronico")
	public Boolean getEnfermedadBaseRenalCronico() {
		return enfermedadBaseRenalCronico;
	}

	public void setEnfermedadBaseRenalCronico(Boolean enfermedadBaseRenalCronico) {
		this.enfermedadBaseRenalCronico = enfermedadBaseRenalCronico;
	}

	@Column(name = "enfermedad_base_inmunodeprimido")
	public Boolean getEnfermedadBaseInmunodeprimido() {
		return enfermedadBaseInmunodeprimido;
	}

	public void setEnfermedadBaseInmunodeprimido(Boolean enfermedadBaseInmunodeprimido) {
		this.enfermedadBaseInmunodeprimido = enfermedadBaseInmunodeprimido;
	}

	@Column(name = "enfermedad_base_neurologica")
	public Boolean getEnfermedadBaseNeurologica() {
		return enfermedadBaseNeurologica;
	}

	public void setEnfermedadBaseNeurologica(Boolean enfermedadBaseNeurologica) {
		this.enfermedadBaseNeurologica = enfermedadBaseNeurologica;
	}

	@Column(name = "enfermedad_base_sindromedown")
	public Boolean getEnfermedadBaseSindromedown() {
		return enfermedadBaseSindromedown;
	}

	public void setEnfermedadBaseSindromedown(Boolean enfermedadBaseSindromedown) {
		this.enfermedadBaseSindromedown = enfermedadBaseSindromedown;
	}

	@Column(name = "enfermedad_base_obesidad")
	public Boolean getEnfermedadBaseObesidad() {
		return enfermedadBaseObesidad;
	}

	public void setEnfermedadBaseObesidad(Boolean enfermedadBaseObesidad) {
		this.enfermedadBaseObesidad = enfermedadBaseObesidad;
	}
@Column(name = "evaluacion_riesgo_usomedicamento")
	public Boolean getEvaluacionRiesgoUsomedicamento() {
		return evaluacionRiesgoUsomedicamento;
	}

	public void setEvaluacionRiesgoUsomedicamento(Boolean evaluacionRiesgoUsomedicamento) {
		this.evaluacionRiesgoUsomedicamento = evaluacionRiesgoUsomedicamento;
	}

	@Column(name = "evaluacion_riesgo_medicamento")
	public String getEvaluacionRiesgoMedicamento() {
		return evaluacionRiesgoMedicamento;
	}

	public void setEvaluacionRiesgoMedicamento(String evaluacionRiesgoMedicamento) {
		this.evaluacionRiesgoMedicamento = evaluacionRiesgoMedicamento;
}
	@Column(name = "sintomas_diarrea")
	public Boolean getSintomasDiarrea() {
		return sintomasDiarrea;
	}

	public void setSintomasDiarrea(Boolean sintomasDiarrea) {
		this.sintomasDiarrea = sintomasDiarrea;
	}

	@Column(name = "enfermedad_base_hipertension_arterial")
	public Boolean getEnfermedadBaseHipertensionArterial() {
		return enfermedadBaseHipertensionArterial;
	}

	public void setEnfermedadBaseHipertensionArterial(Boolean enfermedadBaseHipertensionArterial) {
		this.enfermedadBaseHipertensionArterial = enfermedadBaseHipertensionArterial;
	}

	@Column(name = "enfermedad_base_autoinmune")
	public Boolean getEnfermedadBaseAutoinmune() {
		return enfermedadBaseAutoinmune;
	}

	public void setEnfermedadBaseAutoinmune(Boolean enfermedadBaseAutoinmune) {
		this.enfermedadBaseAutoinmune = enfermedadBaseAutoinmune;
	}

	@Column(name = "enfermedad_base_neoplasias")
	public Boolean getEnfermedadBaseNeoplasias() {
		return enfermedadBaseNeoplasias;
	}

	public void setEnfermedadBaseNeoplasias(Boolean enfermedadBaseNeoplasias) {
		this.enfermedadBaseNeoplasias = enfermedadBaseNeoplasias;
	}

	@Column(name = "enfermedad_base_epoc")
	public Boolean getEnfermedadBaseEPOC() {
		return enfermedadBaseEPOC;
	}

	public void setEnfermedadBaseEPOC(Boolean enfermedadBaseEPOC) {
		this.enfermedadBaseEPOC = enfermedadBaseEPOC;
	}

	@Transient
	public List<String> getResultados() {
		return resultados;
	}

	public void setResultados(List resultados) {
		this.resultados = resultados;
	}
	
	public void addResultado(String resultado)
	{
		resultados.add(resultado);
	}
	
}
