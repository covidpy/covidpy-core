package py.gov.senatics.portal.persistence.covid19;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.criteria.Root;

import py.gov.senatics.portal.modelCovid19.TipoPacienteDiagnostico;
import py.gov.senatics.portal.modelCovid19.TipoPacienteDiagnostico_;
import py.gov.senatics.portal.persistence.covid19.generic.BaseDAO;
import py.gov.senatics.portal.persistence.covid19.generic.CustomFilter;

@Stateless
public class TipoPacienteDiagnosticoDAO extends BaseDAO<TipoPacienteDiagnostico> {


    @Override
    protected Map<String, CustomFilter> getCustomFilters() {
        Map<String, CustomFilter> filters = new HashMap<>();
        filters.put("debeReportarUbicacion", (builder, query, paramExpr, value) -> {
            Object[] roots = query.getRoots().toArray();
            Root<TipoPacienteDiagnostico> root = (Root<TipoPacienteDiagnostico>) roots[0];

            return builder.or(
                    builder.notEqual(paramExpr, paramExpr), // Se debe incluir la expresion para que no dé NullPointerException
                    builder.equal(root.get(TipoPacienteDiagnostico_.debeReportarUbicacion), Boolean.valueOf(value))
            );
        });
        filters.put("debeReportarEstadoSalud", (builder, query, paramExpr, value) -> {
            Object[] roots = query.getRoots().toArray();
            Root<TipoPacienteDiagnostico> root = (Root<TipoPacienteDiagnostico>) roots[0];

            return builder.or(
                    builder.notEqual(paramExpr, paramExpr), // Se debe incluir la expresion para que no dé NullPointerException
                    builder.equal(root.get(TipoPacienteDiagnostico_.debeReportarEstadoSalud), Boolean.valueOf(value))
            );
        });

        return filters;
    }

}
