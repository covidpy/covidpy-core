package py.gov.senatics.portal.dto.covid19;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DatosClinicosDTO {

    private Boolean sintomasFiebre;

    private Float sintomasFiebreValor;

    private Boolean sintomasTos;

    private Boolean sintomasDificultadRespirar;
    private Boolean sintomasDifRespirarDolorGarganta;

    private Boolean sintomasDifRespirarCansancioCaminar;

    private Boolean sintomasDifRespirarFaltaAire;

    private Boolean sintomasDifRespirarRinorrea;
    private Boolean sintomasDifRespirarCongestionNasal;
    private String sintomasOtros;

    private Boolean fiebreGraduada;

    private Boolean sintomasDiarrea;

    public DatosClinicosDTO() {
    }

    public Boolean getSintomasFiebre() {
        return sintomasFiebre;
    }

    public void setSintomasFiebre(Boolean sintomasFiebre) {
        this.sintomasFiebre = sintomasFiebre;
    }

    public Float getSintomasFiebreValor() {
        return sintomasFiebreValor;
    }

    public void setSintomasFiebreValor(Float sintomasFiebreValor) {
        this.sintomasFiebreValor = sintomasFiebreValor;
    }

    public Boolean getSintomasTos() {
        return sintomasTos;
    }

    public void setSintomasTos(Boolean sintomasTos) {
        this.sintomasTos = sintomasTos;
    }

    public Boolean getSintomasDificultadRespirar() {
        return sintomasDificultadRespirar;
    }

    public void setSintomasDificultadRespirar(Boolean sintomasDificultadRespirar) {
        this.sintomasDificultadRespirar = sintomasDificultadRespirar;
    }

    public Boolean getSintomasDifRespirarDolorGarganta() {
        return sintomasDifRespirarDolorGarganta;
    }

    public void setSintomasDifRespirarDolorGarganta(Boolean sintomasDifRespirarDolorGarganta) {
        this.sintomasDifRespirarDolorGarganta = sintomasDifRespirarDolorGarganta;
    }

    public Boolean getSintomasDifRespirarCansancioCaminar() {
        return sintomasDifRespirarCansancioCaminar;
    }

    public void setSintomasDifRespirarCansancioCaminar(Boolean sintomasDifRespirarCansancioCaminar) {
        this.sintomasDifRespirarCansancioCaminar = sintomasDifRespirarCansancioCaminar;
    }

    public Boolean getSintomasDifRespirarFaltaAire() {
        return sintomasDifRespirarFaltaAire;
    }

    public void setSintomasDifRespirarFaltaAire(Boolean sintomasDifRespirarFaltaAire) {
        this.sintomasDifRespirarFaltaAire = sintomasDifRespirarFaltaAire;
    }

    public Boolean getSintomasDifRespirarRinorrea() {
        return sintomasDifRespirarRinorrea;
    }

    public void setSintomasDifRespirarRinorrea(Boolean sintomasDifRespirarRinorrea) {
        this.sintomasDifRespirarRinorrea = sintomasDifRespirarRinorrea;
    }

    public Boolean getSintomasDifRespirarCongestionNasal() {
        return sintomasDifRespirarCongestionNasal;
    }

    public void setSintomasDifRespirarCongestionNasal(Boolean sintomasDifRespirarCongestionNasal) {
        this.sintomasDifRespirarCongestionNasal = sintomasDifRespirarCongestionNasal;
    }

    public String getSintomasOtros() {
        return sintomasOtros;
    }

    public void setSintomasOtros(String sintomasOtros) {
        this.sintomasOtros = sintomasOtros;
    }

    public Boolean getFiebreGraduada() {
        return fiebreGraduada;
    }

    public void setFiebreGraduada(Boolean fiebreGraduada) {
        this.fiebreGraduada = fiebreGraduada;
    }

    public Boolean getSintomasDiarrea() {
        return sintomasDiarrea;
    }

    public void setSintomasDiarrea(Boolean sintomasDiarrea) {
        this.sintomasDiarrea = sintomasDiarrea;
    }
}
