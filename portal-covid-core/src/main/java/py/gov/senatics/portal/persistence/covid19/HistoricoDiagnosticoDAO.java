package py.gov.senatics.portal.persistence.covid19;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import py.gov.senatics.portal.modelCovid19.HistoricoDiagnostico;

@Stateless
public class HistoricoDiagnosticoDAO {

    @PersistenceContext(unitName = "covid19")
    private EntityManager em;

    public void save(HistoricoDiagnostico historicoDiagnostico)
    {
    	em.persist(historicoDiagnostico);
    }

}
