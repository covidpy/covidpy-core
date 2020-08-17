package py.gov.senatics.portal.persistence.covid19;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import py.gov.senatics.portal.modelCovid19.Notificacion;
import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;
import py.gov.senatics.portal.session.UserManager;

@Stateless
public class NotificacionDAO {

	@PersistenceContext(unitName = "covid19")
	private EntityManager em;

	@Inject
	private UserManager userManager;

	private CriteriaBuilder getCriteriaBuilder(){
		return em.getCriteriaBuilder();
	}

	public List<Notificacion> getAllByUser() {
		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<Paciente> query = builder.createQuery(Paciente.class);
		Root<Paciente> root = query.from(Paciente.class);
		Join<Object, Object> userRoot =  root.join("usuario");
		query.where(builder.equal(userRoot.get("id"), getLoggedUser().getId()));
		Paciente paciente = em.createQuery(query).getSingleResult();

		String queryString = "SELECT n FROM Notificacion n WHERE n.paciente.id =:paciente ORDER BY n.fechaNotificacion DESC";
		TypedQuery<Notificacion> q = em.createQuery(queryString, Notificacion.class);
		q.setParameter("paciente", paciente.getId());

		return q.getResultList();
	}

	public Notificacion getById(Long id) {
		return em.find(Notificacion.class, id);
	}

	public void guardarVisto(Long id) {
		Notificacion notificacion = em.find(Notificacion.class, id);
		notificacion.setVisto(true);
		em.persist(notificacion);
		em.flush();
	}

	private Usuario getLoggedUser(){
		return userManager.getRequestUser();
	}
	
	public void save(Notificacion notificacion)
	{
		em.persist(notificacion);
	}

}
