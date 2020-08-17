package py.gov.senatics.portal.persistence.covid19;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import py.gov.senatics.portal.modelCovid19.CensoContacto;

/**
 * @author
 *
 */
@Stateless
public class CensoContactoDAO {

	@PersistenceContext(unitName = "covid19")
	private EntityManager em;

	public CensoContacto save(CensoContacto censoContacto)
	{
		em.persist(censoContacto);
		em.flush();
		return censoContacto;
	}

	private CriteriaBuilder getCriteriaBuilder(){
		return em.getCriteriaBuilder();
	}
	
	public List<CensoContacto> getContactosPaciente(Long idPaciente, int pageSize, int first, String sortField, boolean sortAsc, String filter) {

		Session session = (Session) em.getDelegate();
		List<CensoContacto> listaRetorno = new ArrayList<CensoContacto>();

		Criteria criteria = session.createCriteria(CensoContacto.class, "contacto");
		
		criteria.createCriteria("contacto.paciente", "paciente");
		
		criteria.add(Restrictions.eq("paciente.id", idPaciente));

		ProjectionList projections = Projections.projectionList();

		projections.add(Projections.property("id"), "id");
		projections.add(Projections.property("nombres"), "nombres");
		projections.add(Projections.property("apellidos"), "apellidos");
		projections.add(Projections.property("nroDocumento"), "nroDocumento");
		projections.add(Projections.property("telefono"), "telefono");
		projections.add(Projections.property("domicilio"), "domicilio");
		projections.add(Projections.property("fechaUltimoContacto"), "fechaUltimoContacto");
		projections.add(Projections.property("tipo"), "tipo");
		
		projections.add(Projections.property("timestampCreacion"), "timestampCreacion");
		projections.add(Projections.property("paciente.id"), "idPaciente");

		criteria.setProjection(projections);

		if (sortField != null && !sortField.isEmpty() && !sortField.equalsIgnoreCase("null")) {
			Order order = Order.asc(sortField);

			if (!sortAsc)
				order = Order.desc(sortField);

			criteria.addOrder(order);
		} else {
			Order order = Order.asc("id");

			if (!sortAsc)
				order = Order.desc("id");

			criteria.addOrder(order);
		}

		Disjunction clausulaOR = Restrictions.disjunction();

		if (filter != null && !filter.isEmpty()) {
			clausulaOR.add(Restrictions.ilike("nombres", filter, MatchMode.ANYWHERE));
			clausulaOR.add(Restrictions.ilike("apellidos", filter, MatchMode.ANYWHERE));
			clausulaOR.add(Restrictions.ilike("nroDocumento", filter, MatchMode.ANYWHERE));
			
			criteria.add(clausulaOR);
			
		}

		criteria.setFirstResult(first);
		if(pageSize != 0) {
			criteria.setMaxResults(pageSize);
		}

		criteria.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);

		Iterator<?> iter = criteria.list().iterator();

		while (iter.hasNext()) {
			Map<?, ?> map = (Map<?, ?>) iter.next();

			CensoContacto contacto = new CensoContacto((Long) map.get("id"),
					(String) map.get("nombres"), (String) map.get("apellidos"), (String) map.get("nroDocumento"),
					(String) map.get("telefono"), (String) map.get("domicilio"), (Date) map.get("fechaUltimoContacto"), (String) map.get("tipo"),
					(Date) map.get("timestampCreacion"), (Long) map.get("idPaciente"));

			listaRetorno.add(contacto);

		}

		return listaRetorno;

	}

	public int obtenerCantidadDeFilas(Long idPaciente, String filter) {

		Session session = (Session) em.getDelegate();

		Criteria criteria = session.createCriteria(CensoContacto.class, "contacto");
		
		criteria.add(Restrictions.eq("paciente.id", idPaciente));

		Disjunction clausulaOR = Restrictions.disjunction();

		if (filter != null && !filter.isEmpty()) {
			
			clausulaOR.add(Restrictions.ilike("nombres", filter, MatchMode.ANYWHERE));
			clausulaOR.add(Restrictions.ilike("apellidos", filter, MatchMode.ANYWHERE));
			clausulaOR.add(Restrictions.ilike("nroDocumento", filter, MatchMode.ANYWHERE));
			
			criteria.add(clausulaOR);
		}

		return ((Number) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

}
