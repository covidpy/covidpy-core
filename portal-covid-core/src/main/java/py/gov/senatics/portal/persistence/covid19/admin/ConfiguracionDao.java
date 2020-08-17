package py.gov.senatics.portal.persistence.covid19.admin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

import py.gov.senatics.portal.modelCovid19.admin.Configuracion;

@Stateless
public class ConfiguracionDao {

	@PersistenceContext(unitName = "covid19")
	private EntityManager em;

	public List<Configuracion> obtenerConfiguraciones(List<String> conf) {
		TypedQuery<Configuracion> q = em.createQuery(
				"FROM Configuracion c WHERE c.nombreVariable IN :valorVariable",
				Configuracion.class);

		q.setParameter("valorVariable", conf);

		return q.getResultList();
	}

	public Map<String, String> obtenerConfiguracionesMap() {
		Session session = (Session) em.getDelegate();

		Criteria criteria = session.createCriteria(Configuracion.class, "configuracion");

		Map<String, String> map = new HashMap<>();

		ProjectionList projections = Projections.projectionList();

		projections.add(Projections.property("nombreVariable"), "nombreVariable");
		projections.add(Projections.property("valorVariable"), "valorVariable");

		criteria.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);

		Iterator<?> iter = criteria.list().iterator();

		while (iter.hasNext()) {
			Map<?, ?> itMap = (Map<?, ?>) iter.next();

			map.put(((Configuracion) itMap.get("configuracion")).getNombreVariable(),
					(String) ((Configuracion) itMap.get("configuracion")).getValorVariable());

		}
		return map;
	}
	
	public void update(Configuracion configuracion)
	{
		em.merge(configuracion);
		em.flush();
	}

}
