package py.gov.senatics.portal.modelCovid19;

import javax.persistence.*;

@Entity
@Table(name = "tipo_paciente_diagnostico", schema = "covid19")
public class TipoPacienteDiagnostico {

    @Id
    private String id;

    private String descripcion;

    @Column(name = "frecuencia_reporte_ubicacion_horas")
    private Integer frecuenciaReporteUbicacionHoras;

    @Column(name = "debe_reportar_ubicacion")
    private Boolean debeReportarUbicacion;

    @Column(name = "frecuencia_reporte_estado_salud_horas")
    private Integer frecuenciaReporteSaludHoras;

    @Column(name = "debe_reportar_estado_salud")
    private Boolean debeReportarEstadoSalud;

    public TipoPacienteDiagnostico() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getFrecuenciaReporteUbicacionHoras() {
        return frecuenciaReporteUbicacionHoras;
    }

    public void setFrecuenciaReporteUbicacionHoras(Integer frecuenciaReporteUbicacionHoras) {
        this.frecuenciaReporteUbicacionHoras = frecuenciaReporteUbicacionHoras;
    }

    public Boolean getDebeReportarUbicacion() {
        return debeReportarUbicacion;
    }

    public void setDebeReportarUbicacion(Boolean debeReportarUbicacion) {
        this.debeReportarUbicacion = debeReportarUbicacion;
    }

    public Integer getFrecuenciaReporteSaludHoras() {
        return frecuenciaReporteSaludHoras;
    }

    public void setFrecuenciaReporteSaludHoras(Integer frecuenciaReporteSaludHoras) {
        this.frecuenciaReporteSaludHoras = frecuenciaReporteSaludHoras;
    }

    public Boolean getDebeReportarEstadoSalud() {
        return debeReportarEstadoSalud;
    }

    public void setDebeReportarEstadoSalud(Boolean debeReportarEstadoSalud) {
        this.debeReportarEstadoSalud = debeReportarEstadoSalud;
    }
}
