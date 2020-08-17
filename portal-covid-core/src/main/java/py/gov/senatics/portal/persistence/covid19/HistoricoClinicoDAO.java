package py.gov.senatics.portal.persistence.covid19;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import py.gov.senatics.portal.modelCovid19.HistoricoClinico;

@Stateless
public class HistoricoClinicoDAO {

    @PersistenceContext(unitName = "covid19")
    private EntityManager em;

    public void save(HistoricoClinico historicoClinico)
    {
    	em.persist(historicoClinico);
    }

}
