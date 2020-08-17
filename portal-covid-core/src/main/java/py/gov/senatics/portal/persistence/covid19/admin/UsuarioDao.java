package py.gov.senatics.portal.persistence.covid19.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

import py.gov.senatics.portal.modelCovid19.admin.Rol;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;

@Stateless
public class UsuarioDao {

	@PersistenceContext(unitName = "covid19")
	private EntityManager em;

	public List<Usuario> filtrarUsuarios(int pageSize, int first, String sortField, boolean sortAsc, String filter) {

		Session session = (Session) em.getDelegate();
		List<Usuario> listaRetorno = new ArrayList<Usuario>();

		Criteria criteria = session.createCriteria(Usuario.class, "usuario");


		ProjectionList projections = Projections.projectionList();

		projections.add(Projections.property("id"), "usuarioId");
		projections.add(Projections.property("nombre"), "nombre");
		projections.add(Projections.property("apellido"), "apellido");
		projections.add(Projections.property("username"), "username");
		projections.add(Projections.property("cedula"), "cedula");
		projections.add(Projections.property("activo"), "activo");

		criteria.setProjection(projections);

		if (sortField != null && !sortField.isEmpty() && !sortField.equalsIgnoreCase("null")) {
			Order order = Order.asc(sortField);

			if (!sortAsc)
				order = Order.desc(sortField);

			criteria.addOrder(order);
		} else {
			Order order = Order.asc("usuarioId");

			if (!sortAsc)
				order = Order.desc("usuarioId");

			criteria.addOrder(order);
		}

		Disjunction clausulaOR = Restrictions.disjunction();

		if (filter != null && !filter.isEmpty()) {

			clausulaOR.add(Restrictions.ilike("nombre", filter, MatchMode.ANYWHERE));
			clausulaOR.add(Restrictions.ilike("apellido", filter, MatchMode.ANYWHERE));
			clausulaOR.add(Restrictions.ilike("username", filter, MatchMode.ANYWHERE));
			criteria.add(clausulaOR);
		}

		criteria.setFirstResult(first);
		criteria.setMaxResults(pageSize);

		criteria.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);

		Iterator<?> iter = criteria.list().iterator();

		while (iter.hasNext()) {
			Map<?, ?> map = (Map<?, ?>) iter.next();

			Usuario usuario = new Usuario((Long) map.get("usuarioId"), (Boolean) map.get("activo"),
					(String) map.get("apellido"), (String) map.get("nombre"), (String) map.get("username"));

			listaRetorno.add(usuario);

		}

		return listaRetorno;

	}

	public int obtenerCantidadDeFilas(String filter) {

		Session session = (Session) em.getDelegate();

		Criteria criteria = session.createCriteria(Usuario.class, "usuario");

		Disjunction clausulaOR = Restrictions.disjunction();

		if (filter != null && !filter.isEmpty()) {

			clausulaOR.add(Restrictions.ilike("nombre", filter, MatchMode.ANYWHERE));
			clausulaOR.add(Restrictions.ilike("apellido", filter, MatchMode.ANYWHERE));
			clausulaOR.add(Restrictions.ilike("username", filter, MatchMode.ANYWHERE));

			criteria.add(clausulaOR);
		}

		return ((Number) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	public Usuario obtenerUsuarioLogin(String username) {
		TypedQuery<Usuario> q = em.createQuery(
				"SELECT new Usuario(u.id, u.activo, u.apellido, u.nombre, u.password, u.username) FROM Usuario u "
						+ " LEFT JOIN u.paciente p"
						+ " WHERE u.username =:username AND u.activo =:activo and p is not null",
				Usuario.class);

		q.setParameter("username", username);
		q.setParameter("activo", true);

		try {
			Usuario usuario = q.getSingleResult();
			usuario.getRols();
			return usuario;

		} catch (NoResultException e) {
			// TODO: handle exception

			return null;
		}
	}
	
	public Usuario obtenerUsuarioLoginGestion(String username) {
		TypedQuery<Usuario> q = em.createQuery(
				"SELECT new Usuario(u.id, u.activo, u.apellido, u.nombre, u.password, u.username) FROM Usuario u "
						+ " LEFT JOIN u.paciente p"
						+ " WHERE u.username =:username AND u.activo =:activo and p is null",
				Usuario.class);

		q.setParameter("username", username);
		q.setParameter("activo", true);

		try {
			Usuario usuario = q.getSingleResult();
			usuario.getRols();
			return usuario;
		} catch (NoResultException e) {
			// TODO: handle exception

			return null;
		}
	}

	public Usuario obtenerUsuarioPorNombreUsuario(String username) {
		TypedQuery<Usuario> q = em.createQuery(
				"SELECT new Usuario(u.id, u.activo, u.cedula, u.nombre, u.username) FROM Usuario u WHERE u.username =:username",
				Usuario.class);

		q.setParameter("username", username);

		try {

			return q.getSingleResult();

		} catch (NoResultException e) {
			// TODO: handle exception

			return null;
		}
	}

	public Usuario obtenerUsuariosPorToken(String token) {
		TypedQuery<Usuario> q = em.createQuery(
				"SELECT new Usuario(u.id, u.activo, u.username) FROM Usuario u WHERE u.tokenReset =:tokenReset  ",
				Usuario.class);

		q.setParameter("tokenReset", token);

		try {

			return q.getSingleResult();

		} catch (NoResultException e) {
			// TODO: handle exception

			return null;
		}

	}

	public List<Rol> obtenerRoles(Long idUsuario) {

		TypedQuery<Rol> q = em.createQuery(
				"SELECT new Rol(r.id, r.nombre) FROM Rol r INNER JOIN r.usuarios u WHERE u.id =:idUsuario ", Rol.class);

		q.setParameter("idUsuario", idUsuario);

		return q.getResultList();

	}

	public Usuario obtenerUsuarioPorNombreUsuarioOCedula(String username, String cedula) {
		TypedQuery<Usuario> q = em.createQuery(
				"SELECT new Usuario(u.id, u.activo, u.username) FROM Usuario u WHERE u.username =:username OR u.cedula =:cedula",
				Usuario.class);

		q.setParameter("username", username);
		q.setParameter("cedula", cedula);

		try {

			return q.getSingleResult();

		} catch (NoResultException e) {
			// TODO: handle exception

			return null;
		}
	}

	public Usuario obtenerUsuarioPorNombreUsuarioNotIn(String username, Long id) {
		TypedQuery<Usuario> q = em.createQuery(
				"SELECT new Usuario(u.id, u.activo, u.username) FROM Usuario u WHERE u.username =:username AND id NOT IN :id",
				Usuario.class);

		q.setParameter("username", username);
		q.setParameter("id", id);

		try {

			return q.getSingleResult();

		} catch (NoResultException e) {
			// TODO: handle exception

			return null;
		}
	}

	public void updateTokenReset(Long idUsuario, String token) {

		Usuario u = em.getReference(Usuario.class, idUsuario);

		u.setTokenReset(token);

	}

	public void actualizar(Usuario usuario) {

		Usuario entity = em.getReference(Usuario.class, usuario.getId());


		entity.setNombre(usuario.getNombre());
		entity.setApellido(usuario.getApellido());
		entity.setUsername(usuario.getUsername());
		entity.setActivo(usuario.getActivo());
		entity.setFcmRegistrationToken(usuario.getFcmRegistrationToken());
		entity.setSistemaOperativo(usuario.getSistemaOperativo());
		em.flush();
		
	}

	public void actualizarClave(Usuario u) {
		Usuario e = em.getReference(Usuario.class, u.getId());
		e.setPassword(u.getPassword());

		e.setTokenReset(null);
		em.flush();
	}

	public Usuario crear(Usuario usuario) {

		Usuario entity = new Usuario();

		entity.setNombre(usuario.getNombre());
		entity.setApellido(usuario.getApellido());
		entity.setUsername(usuario.getUsername());
		entity.setTelefono(usuario.getTelefono());
		entity.setCedula(usuario.getCedula());
		entity.setActivo(true);
		entity.setTokenReset(usuario.getTokenReset());
		entity.setPassword(usuario.getPassword());


		for (Rol rol : usuario.getRols()) {
			Rol r = em.getReference(Rol.class, rol.getId());
			entity.addRol(r);

		}

		em.persist(entity);
		
		return entity;

	}

	public void actualizarRoles(Usuario req) {

		Usuario e = em.getReference(Usuario.class, req.getId());

		List<Rol> userRoles = new ArrayList<>(e.getRols());

		for (Rol tmp : userRoles) {

			Rol r = em.getReference(Rol.class, tmp.getId());
			r.getUsuarios().remove(e);

			e.removeRol(r);
			em.merge(e);
		}

		for (Rol tmp : req.getRols()) {

			Rol r = em.getReference(Rol.class, tmp.getId());
			e.addRol(r);

		}
		em.flush();

	}

	public Usuario byId(long id) {
		return em.find(Usuario.class, id);
	}
	
	public Usuario getUsuarioBycedula(String cedula) {
		Query q = em.createQuery("FROM Usuario WHERE cedula=:cedula");

		q.setParameter("cedula", cedula);
		
		List result=q.getResultList();

		if(result.isEmpty())
		{
			return null;

		}
		else
		{
			return (Usuario) result.get(0);
		}
	}

}
