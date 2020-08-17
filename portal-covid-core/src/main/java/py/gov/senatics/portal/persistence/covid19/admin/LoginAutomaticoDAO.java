package py.gov.senatics.portal.persistence.covid19.admin;

import py.gov.senatics.portal.modelCovid19.admin.LoginAutomatico;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Stateless
public class LoginAutomaticoDAO {


    @PersistenceContext(unitName = "covid19")
    private EntityManager em;

    public LoginAutomatico getByToken(String token) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<LoginAutomatico> query = builder.createQuery(LoginAutomatico.class);
        Root<LoginAutomatico> root = query.from(LoginAutomatico.class);
        query.where(
                builder.equal(root.get("token"), token),
                builder.equal(root.get("estado"), LoginAutomatico.ESTADO_ACTIVO)
        );

        query.orderBy(builder.desc(root.get("timestampCreacion")));

        try {
            return em.createQuery(query).setMaxResults(1).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public void update(LoginAutomatico loginAutomatico) {
        em.merge(loginAutomatico);
    }
    
    public void save(LoginAutomatico loginAutomatico)
    {
    	em.persist(loginAutomatico);
    }
}
