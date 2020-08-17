package py.gov.senatics.portal.persistence.covid19;

import py.gov.senatics.portal.modelCovid19.*;
import py.gov.senatics.portal.persistence.covid19.generic.BaseDAO;
import py.gov.senatics.portal.persistence.covid19.generic.CustomFilter;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ReporteSaludDAO extends BaseDAO<ReporteSalud> {

    public boolean tieneReportes(Paciente paciente) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<ReporteSalud> root = query.from(ReporteSalud.class);
        Join<Object, Object> registroRoot =  root.join("registroFormulario");
        Join<Object, Object> pacienteRoot =  registroRoot.join("paciente");
        query.where(builder.equal(pacienteRoot.get("id"), paciente.getId()));
        query.select(builder.count(root));
        try {
            return em.createQuery(query).getSingleResult() > 0;
        } catch (NoResultException ex) {
            return false;
        }
    }

    public ReporteSalud getLastReporteSalud(Long idPaciente)
    {
    	List<ReporteSalud> result=em.createQuery("from ReporteSalud where registroFormulario.paciente.id=:idPaciente order by id desc").setParameter("idPaciente", idPaciente).setMaxResults(1).getResultList();
    	if(result.isEmpty())
    	{
    		return null;
    	}
    	else
    	{
    		return result.get(0);
    	}
    }

    public List<ReporteSalud> getReportesAyer(Paciente paciente) {
        Date hoy = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date ayer = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        CriteriaQuery<ReporteSalud> queryReporteAyer = queryReportesFechaDesdeHasta(paciente, ayer, hoy);
        return em.createQuery(queryReporteAyer).getResultList();
    }

    private CriteriaQuery<ReporteSalud> queryReportesFechaDesdeHasta(Paciente paciente, Date desde, Date hasta) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ReporteSalud> query = builder.createQuery(ReporteSalud.class);
        Root<ReporteSalud> root = query.from(ReporteSalud.class);
        Join<ReporteSalud, RegistroFormulario> registroRoot = root.join(ReporteSalud_.registroFormulario);
        Join<RegistroFormulario, Paciente> pacienteRoot = registroRoot.join(RegistroFormulario_.paciente);
        query.where(
                builder.equal(pacienteRoot.get(Paciente_.id), paciente.getId()),
                builder.greaterThanOrEqualTo(root.get(ReporteSalud_.timestampCreacion), desde),
                builder.lessThan(root.get(ReporteSalud_.timestampCreacion), hasta)
        );
        query.select(root);
        return query;
    }

    public ReporteSalud getUltimoReporteHoy(Paciente paciente) {
        Date hoy = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date manhana = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        CriteriaQuery<ReporteSalud> queryUltimoReporteFiebreAyer = queryReportesFechaDesdeHasta(paciente, hoy, manhana);
        CriteriaBuilder builder = em.getCriteriaBuilder();
        Root<ReporteSalud> root = (Root<ReporteSalud>) queryUltimoReporteFiebreAyer.getRoots().toArray()[0];
        queryUltimoReporteFiebreAyer.orderBy(builder.desc(root.get(ReporteSalud_.timestampCreacion)));
        try {
            return em.createQuery(queryUltimoReporteFiebreAyer).setMaxResults(1).getSingleResult();
        } catch (NoResultException ex) {
            // No hay reportes ni ayer ni hoy, entonces no tuvo fiebre ayer
            return null;
        }
    }

    @Override
    protected Map<String, CustomFilter> getCustomFilters() {
        Map<String, CustomFilter> filters = new HashMap<>();
        filters.put(
                "cedula", (builder, query, paramExpr, value) -> {
                    Root<ReporteSalud> root = (Root<ReporteSalud>) query.getRoots().toArray()[0];
                    Join<ReporteSalud, RegistroFormulario> registroFormularioJoin = root.join(ReporteSalud_.registroFormulario);
                    Join<RegistroFormulario, Paciente> pacienteJoin = registroFormularioJoin.join(RegistroFormulario_.paciente);
                    Join<Paciente, PacienteDatosPersonalesBasicos> pacienteDatosPersonalesBasicosJoin = pacienteJoin.join(Paciente_.datosPersonalesBasicos);
                    return builder.equal(pacienteDatosPersonalesBasicosJoin.get(PacienteDatosPersonalesBasicos_.numeroDocumento), paramExpr);
                }
        );
        return filters;
    }
}
