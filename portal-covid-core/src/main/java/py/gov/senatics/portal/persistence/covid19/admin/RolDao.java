package py.gov.senatics.portal.persistence.covid19.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import py.gov.senatics.portal.modelCovid19.admin.Permiso;
import py.gov.senatics.portal.modelCovid19.admin.Rol;

@Stateless
public class RolDao {

	@PersistenceContext(unitName = "covid19")
	private EntityManager em;

	public List<Rol> getAll() {

		TypedQuery<Rol> query = em.createQuery("SELECT new Rol(r.id, r.nombre) FROM Rol r", Rol.class);
		
		return query.getResultList();

	}
	
	public List<Rol> getAllActivo() {

		TypedQuery<Rol> query = em.createQuery("SELECT new Rol(r.id, r.nombre) FROM Rol r where r.activo is true", Rol.class);
		return query.getResultList();

	}
	
	public Rol obtenerRolPorNombre(String nombre) {
		TypedQuery<Rol> q = em.createQuery(
				"SELECT new Rol(r.nombre) FROM Rol r WHERE r.nombre =:nombre",
				Rol.class);

		q.setParameter("nombre", nombre);

		try {

			return q.getSingleResult();

		} catch (NoResultException e) {
			// TODO: handle exception

			return null;
		}
	}
	
	public void crear(Rol rol) {
		Rol entity = new Rol();
		
		entity.setNombre(rol.getNombre());
		entity.setDescripcion(rol.getDescripcion());
		entity.setActivo(true);

		em.persist(entity);

	}
	
	public void actualizar(Rol rol) {
		Rol entity = em.getReference(Rol.class, rol.getId());
		entity.setNombre(rol.getNombre());
		entity.setDescripcion(rol.getDescripcion());
		entity.setActivo(rol.getActivo());
	}
	
	public Rol obtenerRolPorNombreNotIn(String nombre, Long id) {
		TypedQuery<Rol> q = em.createQuery(
				"SELECT new Rol(r.id, r.activo, r.nombre) FROM Rol r WHERE r.nombre =:nombre AND id NOT IN :id",
				Rol.class);

		q.setParameter("nombre", nombre);
		q.setParameter("id", id);

		try {

			return q.getSingleResult();

		} catch (NoResultException e) {
			// TODO: handle exception

			return null;
		}
	}
	
	public List<Rol> getAll(Long idUsuario) {

		TypedQuery<Rol> query = em.createQuery(
				"SELECT new Rol(r.id, r.nombre) FROM Rol r INNER JOIN r.usuarios u WHERE u.id = :idUsuario", Rol.class);
		query.setParameter("idUsuario", idUsuario);

		return query.getResultList();

	}

	public List<Permiso> obtenerPermisos(Long idRol) {

		TypedQuery<Permiso> q = em.createQuery(
				"SELECT new Permiso(p.id, p.nombre) FROM Permiso p INNER JOIN p.rols r WHERE r.id =:idRol",
				Permiso.class);

		q.setParameter("idRol", idRol);

		return q.getResultList();

	}

	public List<Rol> filtrarRoles(int pageSize, int first, String sortField, boolean sortAsc, String filter) {
		Session session = (Session) em.getDelegate();
		List<Rol> listaRetorno = new ArrayList<Rol>();

		Criteria criteria = session.createCriteria(Rol.class, "rol");

		ProjectionList projections = Projections.projectionList();

		projections.add(Projections.property("id"), "rolId");
		projections.add(Projections.property("descripcion"), "descripcion");
		projections.add(Projections.property("nombre"), "nombre");
		projections.add(Projections.property("activo"), "activo");

		criteria.setProjection(projections);

		if (sortField != null && !sortField.isEmpty() && !sortField.equalsIgnoreCase("null")) {
			Order order = Order.asc(sortField);

			if (!sortAsc)
				order = Order.desc(sortField);

			criteria.addOrder(order);
		} else {
			Order order = Order.asc("rolId");

			if (!sortAsc)
				order = Order.desc("rolId");

			criteria.addOrder(order);
		}

		Disjunction clausulaOR = Restrictions.disjunction();

		if (filter != null && !filter.isEmpty()) {

			clausulaOR.add(Restrictions.ilike("nombre", filter, MatchMode.ANYWHERE));
			clausulaOR.add(Restrictions.ilike("descripcion", filter, MatchMode.ANYWHERE));
			criteria.add(clausulaOR);
		}

		criteria.setFirstResult(first);
		criteria.setMaxResults(pageSize);

		criteria.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);

		Iterator<?> iter = criteria.list().iterator();

		while (iter.hasNext()) {

			Map<?, ?> map = (Map<?, ?>) iter.next();

			Rol rol = new Rol((Long) map.get("rolId"), (String) map.get("descripcion"), (String) map.get("nombre"), (Boolean) map.get("activo"));
			listaRetorno.add(rol);

		}

		return listaRetorno;

	}

	public int obtenerCantidadDeFilas(String filter) {

		Session session = (Session) em.getDelegate();

		Criteria criteria = session.createCriteria(Rol.class, "rol");

		Disjunction clausulaOR = Restrictions.disjunction();

		if (filter != null && !filter.isEmpty()) {

			clausulaOR.add(Restrictions.ilike("nombre", filter, MatchMode.ANYWHERE));
			clausulaOR.add(Restrictions.ilike("descripcion", filter, MatchMode.ANYWHERE));

			criteria.add(clausulaOR);
		}

		return ((Number) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

}