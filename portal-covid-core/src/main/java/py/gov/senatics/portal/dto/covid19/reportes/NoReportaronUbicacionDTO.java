package py.gov.senatics.portal.dto.covid19.reportes;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.opencsv.bean.CsvBindByName;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NoReportaronUbicacionDTO {

    @CsvBindByName(column = "Nombre")
    private String nombreCompleto;

    @CsvBindByName(column = "Cédula")
    private String cedula;

    @CsvBindByName(column = "Fecha Último Reporte")
    private Date fechaUltimoReporte;

    @CsvBindByName(column = "Horas de Retraso")
    private Long horasRetraso;

    @CsvBindByName(column = "Teléfono")
    private String telefono;

    @CsvBindByName(column = "Motivo de Ingreso")
    private String tipoIngreso;

    @CsvBindByName(column = "Tipo de Paciente")
    private String tipoPaciente;

    public NoReportaronUbicacionDTO() {
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Date getFechaUltimoReporte() {
        return fechaUltimoReporte;
    }

    public void setFechaUltimoReporte(Date fechaUltimoReporte) {
        this.fechaUltimoReporte = fechaUltimoReporte;
    }

    public Long getHorasRetraso() {
        return horasRetraso;
    }

    public void setHorasRetraso(Long horasRetraso) {
        this.horasRetraso = horasRetraso;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipoIngreso() {
        return tipoIngreso;
    }

    public void setTipoIngreso(String tipoIngreso) {
        this.tipoIngreso = tipoIngreso;
    }

    public String getTipoPaciente() {
        return tipoPaciente;
    }

    public void setTipoPaciente(String tipoPaciente) {
        this.tipoPaciente = tipoPaciente;
    }
}
