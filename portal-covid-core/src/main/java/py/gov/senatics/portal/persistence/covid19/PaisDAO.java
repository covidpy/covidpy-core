package py.gov.senatics.portal.persistence.covid19;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import py.gov.senatics.portal.modelCovid19.Pais;

@Stateless
public class PaisDAO {

    @PersistenceContext(unitName = "covid19")
    private EntityManager em;

    public List<Pais> getAll() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Pais> query = builder.createQuery(Pais.class);
        query.from(Pais.class);
        return em.createQuery(query).getResultList();
    }
}
