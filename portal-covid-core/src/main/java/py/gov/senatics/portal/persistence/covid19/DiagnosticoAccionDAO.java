package py.gov.senatics.portal.persistence.covid19;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import py.gov.senatics.portal.modelCovid19.DiagnosticoAccion;

@Stateless
public class DiagnosticoAccionDAO {

    @PersistenceContext(unitName = "covid19")
    private EntityManager em;

    public void save(DiagnosticoAccion diagnosticoAccion)
    {
    	em.persist(diagnosticoAccion);
    }

}
