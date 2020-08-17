package py.gov.senatics.portal.modelCovid19;

import com.fasterxml.jackson.annotation.*;
import com.opencsv.bean.CsvIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import py.gov.senatics.portal.annotation.formulario.ConditionalField;
import py.gov.senatics.portal.annotation.formulario.FieldOption;
import py.gov.senatics.portal.annotation.formulario.FormField;
import py.gov.senatics.portal.annotation.formulario.FormFieldType;
import py.gov.senatics.portal.annotation.formulario.validation.ConditionalNotNull;
import py.gov.senatics.portal.annotation.formulario.validation.ConditionalValidated;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;


@Entity
@Table(name = "reporte_salud_paciente", schema = "covid19")
@JsonIgnoreProperties(ignoreUnknown = true)
@ConditionalValidated
public class ReporteSalud {
    @CsvIgnore
    public static final String TEMPERATURA_MAS_38 = "mas38",
        TEMPERATURA_MENOS_38 = "menos38";
    @CsvIgnore
    public static final String FIEBRE_SI = "si";
    @CsvIgnore
    public static final String FIEBRE_NO = "no";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_registro_formulario")
    @JsonIgnore
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @JsonIdentityReference(alwaysAsId=true)
    private RegistroFormulario  registroFormulario;

    @Column(name = "como_te_sentis")
    @FormField(
            label = "¿Cómo te sentís?",
            page = 0,
            fieldType = FormFieldType.PRETTY_RADIO
    )
    @FieldOption(id = "mal", descripcion = "Mal")
    @FieldOption(id = "masomenos", descripcion = "Más o Menos")
    @FieldOption(id = "bien", descripcion = "Bien")
    @NotNull(message = "Es requerido")
    private String comoTeSentis;

    @Column(name = "signos_sintomas_descritos")
    @FormField(
            label = "Describa los signos y síntomas que tiene",
            page = 0,
            fieldType = FormFieldType.STRING
    )
    @ConditionalField(conditionField = "comoTeSentis", conditionValue = "mal")
    private String signosSintomasDescritos;

    @Transient
    @FormField(
            label = "Describa los signos y síntomas que tiene",
            page = 0,
            fieldType = FormFieldType.STRING,
            modelField = "signosSintomasDescritos"
    )
    @ConditionalField(conditionField = "comoTeSentis", conditionValue = "masomenos")
    private String signosSintomasDescritosB;

    @FormField(
            label = "¿Cómo te sentís hoy en relación a ayer?",
            page = 0,
            fieldType = FormFieldType.PRETTY_RADIO
    )
    @FieldOption(id = "peor", descripcion = "Peor")
    @FieldOption(id = "igual", descripcion = "Igual")
    @FieldOption(id = "mejor", descripcion = "Mejor")
    @Column(name = "como_te_sentis_con_relacion_ayer")
    @ConditionalField(conditionField = "esPrimeraVez", conditionValue = "true")
    @ConditionalNotNull
    private String comoTeSentisConRelacionAyer;

    @Transient
    private String esPrimeraVez;

    @Transient
    private String debeReportarFiebreAyer;

    @FormField(
            label = "Mencione los signos y síntomas que empeoraron",
            page = 0,
            fieldType = FormFieldType.STRING
    )
    @ConditionalField(conditionField = "comoTeSentisConRelacionAyer", conditionValue = "peor")
    @ConditionalNotNull
    @Column(name = "sintomas_empeoraron")
    private String sintomasEmpeoraron;

    @FormField(
            label = "Mencione los signos y síntomas que mejoraron",
            page = 0,
            fieldType = FormFieldType.STRING
    )
    @ConditionalField(conditionField = "comoTeSentisConRelacionAyer", conditionValue = "mejor")
    @ConditionalNotNull
    @Column(name = "sintomas_mejoraron")
    private String sintomasMejoraron;

    @FormField(
            label = "¿Tenés congestión nasal?",
            fieldType = FormFieldType.PRETTY_RADIO,
            page = 1
    )
    @FieldOption(id = "si", descripcion = "Sí")
    @FieldOption(id = "no", descripcion = "No")
    @Column(name = "congestion_nasal")
    @NotNull(message = "Es requerido")
    private String congestionNasal;

    @FormField(
            label = "¿Tenés secreción nasal?",
            page = 1,
            fieldType = FormFieldType.PRETTY_RADIO
    )
    @FieldOption(id = "si", descripcion = "Sí")
    @FieldOption(id = "no", descripcion = "No")
    @Column(name = "secrecion_nasal")
    @NotNull(message = "Es requerido")
    private String secrecionNasal;

    @FormField(
            label = "¿Tenés dolor de garganta?",
            page = 1,
            fieldType = FormFieldType.PRETTY_RADIO
    )
    @FieldOption(id = "si", descripcion = "Sí")
    @FieldOption(id = "no", descripcion = "No")
    @Column(name = "dolor_garganta")
    @NotNull(message = "Es requerido")
    private String dolorGarganta;

    @FormField(
            label = "¿Tenés tos?",
            page = 1,
            fieldType = FormFieldType.PRETTY_RADIO
    )
    @FieldOption(id = "si", descripcion = "Sí")
    @FieldOption(id = "no", descripcion = "No")
    @NotNull(message = "Es requerido")
    private String tos;

    @FormField(
            label = "¿Estás percibiendo los olores?",
            page = 2,
            fieldType = FormFieldType.PRETTY_RADIO
    )
    @FieldOption(id = "no", descripcion = "No")
    @FieldOption(id = "menos", descripcion = "Sí, pero menos")
    @FieldOption(id = "si", descripcion = "Sí")
    @Column(name = "percibe_olores")
    @NotNull(message = "Es requerido")
    private String percibeOlores;


    @FormField(
            label = "¿Desde cuándo?",
            page = 2,
            fieldType = FormFieldType.DATE_TIME
    )
    @ConditionalField(conditionField = "percibeOlores", conditionValue = "no")
    @Column(name = "desde_cuando_olores")
    @ConditionalField(conditionField = "esPrimeraVez", conditionValue = "true")
    @ConditionalNotNull
    private String desdeCuandoOlores;

    @FormField(
            label = "¿Desde cuándo?",
            page = 2,
            fieldType = FormFieldType.DATE_TIME,
            modelField = "desdeCuandoOlores"
    )
    @ConditionalField(conditionField = "percibeOlores", conditionValue = "menos")
    @Transient
    @ConditionalField(conditionField = "esPrimeraVez", conditionValue = "true")
    @ConditionalNotNull
    private String desdeCuandoOloresB;

    @FormField(
            label = "¿Estás percibiendo los sabores?",
            page = 2,
            fieldType = FormFieldType.PRETTY_RADIO
    )
    @FieldOption(id = "no", descripcion = "No")
    @FieldOption(id = "menos", descripcion = "Sí, pero menos")
    @FieldOption(id = "si", descripcion = "Sí")
    @Column(name = "percibe_sabores")
    @NotNull(message = "Es requerido")
    private String percibeSabores;

    @FormField(
            label = "¿Desde cuándo?",
            page = 2,
            fieldType = FormFieldType.DATE_TIME
    )
    @ConditionalField(conditionField = "percibeSabores", conditionValue = "no")
    @Column(name = "desde_cuando_sabores")
    @ConditionalField(conditionField = "esPrimeraVez", conditionValue = "true")
    @ConditionalNotNull
    private String desdeCuandoSabores;


    @FormField(
            label = "¿Desde cuándo?",
            fieldType = FormFieldType.DATE_TIME,
            page = 2,
            modelField = "desdeCuandoSabores"
    )
    @ConditionalField(conditionField = "percibeSabores", conditionValue = "menos")
    @Transient
    @ConditionalField(conditionField = "esPrimeraVez", conditionValue = "true")
    @ConditionalNotNull
    private String desdeCuandoSaboresB;

    @FormField(
            label = "¿Tenés dificultad para respirar?",
            page = 3,
            fieldType = FormFieldType.PRETTY_RADIO
    )
    @FieldOption(id = "si", descripcion = "Sí")
    @FieldOption(id = "no", descripcion = "No")
    @Column(name = "dificultad_respirar")
    @NotNull(message = "Es requerido")
    private String dificultadRespirar;


    @FormField(
            label = "¿Te sentís con fiebre? ¿Tenés fiebre?",
            page = 3,
            fieldType = FormFieldType.PRETTY_RADIO
    )
    @FieldOption(id = "si", descripcion = "Sí")
    @FieldOption(id = "no", descripcion = "No")
    @Column(name = "sentis_fiebre")
    @NotNull(message = "Es requerido")
    private String sentisFiebre;

    @FormField(
            label = "¿Desde cuándo?",
            page = 3,
            fieldType = FormFieldType.DATE_TIME
    )
    @ConditionalField(conditionField = "sentisFiebre", conditionValue = "si")
    @Column(name = "desde_cuando_fiebre")
    @ConditionalField(conditionField = "esPrimeraVez", conditionValue = "true")
    @ConditionalNotNull
    private String desdeCuandoFiebre;

    @Column(name = "tomaste_temperatura")
    private String tomasteTemperatura;


    @FormField(
            label = "¿Te tomaste la temperatura? ¿Cuánto marcó?",
            page = 4,
            fieldType = FormFieldType.TEMP
    )
    @ConditionalField(conditionField = "sentisFiebre", conditionValue = "si")
    @Column(name = "temperatura")
    @ConditionalNotNull
    private Float temperatura;


    @FormField(
            label = "¿Ayer te sentiste con fiebre? ¿Ayer tuviste fiebre?",
            page = 4,
            fieldType = FormFieldType.PRETTY_RADIO
    )
    @FieldOption(id = FIEBRE_SI, descripcion = "Sí")
    @FieldOption(id = "no", descripcion = "No")
    @ConditionalField(conditionField = "debeReportarFiebreAyer", conditionValue = "true")
    @ConditionalNotNull
    @Column(name = "fiebre_ayer")
    private String fiebreAyer;

    @FormField(
            label = "¿Te sentís triste, desanimado?",
            page = 6,
            fieldType = FormFieldType.SMILEY
    )
    @FieldOption(id = "1", descripcion = "mood")
    @FieldOption(id = "2", descripcion = "sentiment_satisfied_alt")
    @FieldOption(id = "3", descripcion = "sentiment_satisfied")
    @FieldOption(id = "4", descripcion = "sentiment_very_dissatisfied")
    @FieldOption(id = "5", descripcion = "mood_bad")
    @Column(name = "sentis_triste_desanimado")
    @NotNull(message = "Es requerido")
    private String sentisTristeDesanimado;

    @FormField(
            label = "¿Sentís angustia, inquietud, temor?",
            page = 6,
            fieldType = FormFieldType.SMILEY
    )
    @FieldOption(id = "1", descripcion = "mood")
    @FieldOption(id = "2", descripcion = "sentiment_satisfied_alt")
    @FieldOption(id = "3", descripcion = "sentiment_satisfied")
    @FieldOption(id = "4", descripcion = "sentiment_very_dissatisfied")
    @FieldOption(id = "5", descripcion = "mood_bad")
    @Column(name = "sentis_angustia")
    @NotNull(message = "Es requerido")
    private String sentisAngustia;


    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp_creacion")
    private Date timestampCreacion;

    @Transient
    private Set<String> resultadoGrupos=new LinkedHashSet<>();
    
    @Transient
    private Set<String> resultadoRecomendaciones=new LinkedHashSet<>();

    @Formula("(FLOOR(extract(epoch from (now()- timestamp_creacion))/(60*60)))")
    private Long horasRetraso;

    public ReporteSalud() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComoTeSentis() {
        return comoTeSentis;
    }

    public void setComoTeSentis(String comoTeSentis) {
        this.comoTeSentis = comoTeSentis;
    }

    public String getSignosSintomasDescritos() {
        return signosSintomasDescritos;
    }

    public void setSignosSintomasDescritos(String signosSintomasDescritos) {
        this.signosSintomasDescritos = signosSintomasDescritos;
    }

    public Date getTimestampCreacion() {
        return timestampCreacion;
    }

    public void setTimestampCreacion(Date timestampCreacion) {
        this.timestampCreacion = timestampCreacion;
    }

    public RegistroFormulario getRegistroFormulario() {
        return registroFormulario;
    }

    public void setRegistroFormulario(RegistroFormulario registroFormulario) {
        this.registroFormulario = registroFormulario;
    }

    public String getComoTeSentisConRelacionAyer() {
        return comoTeSentisConRelacionAyer;
    }

    public void setComoTeSentisConRelacionAyer(String comoTeSentisConRelacionAyer) {
        this.comoTeSentisConRelacionAyer = comoTeSentisConRelacionAyer;
    }

    public String getCongestionNasal() {
        return congestionNasal;
    }

    public void setCongestionNasal(String congestionNasal) {
        this.congestionNasal = congestionNasal;
    }

    public String getSecrecionNasal() {
        return secrecionNasal;
    }

    public void setSecrecionNasal(String secrecionNasal) {
        this.secrecionNasal = secrecionNasal;
    }

    public String getDolorGarganta() {
        return dolorGarganta;
    }

    public void setDolorGarganta(String dolorGarganta) {
        this.dolorGarganta = dolorGarganta;
    }

    public String getTos() {
        return tos;
    }

    public void setTos(String tos) {
        this.tos = tos;
    }

    public String getPercibeOlores() {
        return percibeOlores;
    }

    public void setPercibeOlores(String percibeOlores) {
        this.percibeOlores = percibeOlores;
    }

    public String getPercibeSabores() {
        return percibeSabores;
    }

    public void setPercibeSabores(String percibeSabores) {
        this.percibeSabores = percibeSabores;
    }

    public String getDificultadRespirar() {
        return dificultadRespirar;
    }

    public void setDificultadRespirar(String dificultadRespirar) {
        this.dificultadRespirar = dificultadRespirar;
    }

    public String getSentisFiebre() {
        return sentisFiebre;
    }

    public void setSentisFiebre(String setisFiebre) {
        this.sentisFiebre = setisFiebre;
    }

    public String getSignosSintomasDescritosB() {
        return signosSintomasDescritosB;
    }

    public void setSignosSintomasDescritosB(String signosSintomasDescritosB) {
        this.signosSintomasDescritosB = signosSintomasDescritosB;
    }

    public String getSintomasEmpeoraron() {
        return sintomasEmpeoraron;
    }

    public void setSintomasEmpeoraron(String sintomasEmpeoraron) {
        this.sintomasEmpeoraron = sintomasEmpeoraron;
    }

    public String getDesdeCuandoOlores() {
        return desdeCuandoOlores;
    }

    public void setDesdeCuandoOlores(String desdeCuandoOlores) {
        this.desdeCuandoOlores = desdeCuandoOlores;
    }

    public String getDesdeCuandoOloresB() {
        return desdeCuandoOloresB;
    }

    public void setDesdeCuandoOloresB(String desdeCuandoOloresB) {
        this.desdeCuandoOloresB = desdeCuandoOloresB;
    }

    public String getDesdeCuandoSabores() {
        return desdeCuandoSabores;
    }

    public void setDesdeCuandoSabores(String desdeCuandoSabores) {
        this.desdeCuandoSabores = desdeCuandoSabores;
    }

    public String getDesdeCuandoSaboresB() {
        return desdeCuandoSaboresB;
    }

    public void setDesdeCuandoSaboresB(String desdeCuandoSaboresB) {
        this.desdeCuandoSaboresB = desdeCuandoSaboresB;
    }

    public String getDesdeCuandoFiebre() {
        return desdeCuandoFiebre;
    }

    public void setDesdeCuandoFiebre(String desdeCuandoFiebre) {
        this.desdeCuandoFiebre = desdeCuandoFiebre;
    }

    public String getSintomasMejoraron() {
        return sintomasMejoraron;
    }

    public void setSintomasMejoraron(String sintomasMejoraron) {
        this.sintomasMejoraron = sintomasMejoraron;
    }

    public String getTomasteTemperatura() {
        return tomasteTemperatura;
    }

    public void setTomasteTemperatura(String tomasteTemperatura) {
        this.tomasteTemperatura = tomasteTemperatura;
    }

    public String getFiebreAyer() {
        return fiebreAyer;
    }

    public void setFiebreAyer(String fiebreAyer) {
        this.fiebreAyer = fiebreAyer;
    }

    public String getSentisTristeDesanimado() {
        return sentisTristeDesanimado;
    }

    public void setSentisTristeDesanimado(String sentisTristeDesanimado) {
        this.sentisTristeDesanimado = sentisTristeDesanimado;
    }

    public String getSentisAngustia() {
        return sentisAngustia;
    }

    public void setSentisAngustia(String sentisAngustia) {
        this.sentisAngustia = sentisAngustia;
    }
    
	public Set<String> getResultadoGrupos() {
		return resultadoGrupos;
	}

	public void setResultadoGrupos(Set<String> resultadoGrupos) {
		this.resultadoGrupos = resultadoGrupos;
	}

	public Set<String> getResultadoRecomendaciones() {
		return resultadoRecomendaciones;
	}

	public void setResultadoRecomendaciones(Set<String> resultadoRecomendaciones) {
		this.resultadoRecomendaciones = resultadoRecomendaciones;
	}

	public void addResultadoGrupo(String resultado)
    {
    	resultadoGrupos.add(resultado);
    }
	
	public void addResultadoRecomendacion(String resultado)
    {
    	resultadoRecomendaciones.add(resultado);
    }

    public String getEsPrimeraVez() {
        return esPrimeraVez;
    }

    public void setEsPrimeraVez(String esPrimeraVez) {
        this.esPrimeraVez = esPrimeraVez;
    }

    public Float getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Float temperatura) {
        this.temperatura = temperatura;
    }

    public String getDebeReportarFiebreAyer() {
        return debeReportarFiebreAyer;
    }

    public void setDebeReportarFiebreAyer(String debeReportarFiebreAyer) {
        this.debeReportarFiebreAyer = debeReportarFiebreAyer;
    }

    public Long getHorasRetraso() {
        return horasRetraso;
    }
}
