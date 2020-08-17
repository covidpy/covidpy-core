package py.gov.senatics.portal.business.covid19;

import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.modelCovid19.ReporteSalud;
import py.gov.senatics.portal.persistence.covid19.ReporteSaludDAO;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;

@RequestScoped
public class ReporteSaludBC {

    @Inject
    private ReporteSaludDAO dao;

    public boolean esPrimerReporte(Paciente paciente) {
        return !dao.tieneReportes(paciente);
    }

    public boolean tuvoFiebreAyer(Paciente paciente) {
        List<ReporteSalud> reportesAyer = dao.getReportesAyer(paciente);
        if (reportesAyer.size() > 0) {
            return reportesAyer.stream().anyMatch(r -> r.getSentisFiebre().equals(ReporteSalud.FIEBRE_SI));
        } else {
            // Si no hay reportes de ayer, se ve si hay reportes hoy
            ReporteSalud ultimoReporteHoy = dao.getUltimoReporteHoy(paciente);
            return ultimoReporteHoy != null && ultimoReporteHoy.getFiebreAyer().equals(ReporteSalud.FIEBRE_SI);
        }
    }

    /**
     * Devuelve si el paciente debe reportar específicamente si tuvo fiebre el día anterior
     * @param paciente paciente que reportará su salud
     * @return true si debe aparecer el campo de "Tuvo fiebre ayer"
     */
    public boolean debeReportarTuvoFiebreAyer(Paciente paciente) {
        return dao.getReportesAyer(paciente).isEmpty() && dao.getUltimoReporteHoy(paciente) == null;
    }
}
