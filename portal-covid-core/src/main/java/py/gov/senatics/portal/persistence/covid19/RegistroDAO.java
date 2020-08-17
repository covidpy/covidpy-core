package py.gov.senatics.portal.persistence.covid19;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import py.gov.senatics.portal.modelCovid19.Registro;
import py.gov.senatics.portal.modelCovid19.Registro_;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;

/**
 * @author ricardo
 *
 */
@Stateless
public class RegistroDAO {

	@PersistenceContext(unitName = "covid19")
	private EntityManager em;

	public void save(Registro registro)
	{
		em.persist(registro);
	}
	
	public Registro findById(Integer id)
	{
		List result=em.createQuery("from Registro where id=:id").setParameter("id", id).getResultList();
		if(result.isEmpty())
		{
			return null;
		}
		else
		{
			return (Registro) result.get(0);
		}
	}

	public Registro getRegistroPacienteByUsuario(Usuario usuario) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Registro> query = criteriaBuilder.createQuery(Registro.class);
		Root<Registro> from = query.from(Registro.class);
		ParameterExpression<Usuario> parameter = criteriaBuilder.parameter(Usuario.class);
		query.where(criteriaBuilder.equal(from.get(Registro_.usuario), parameter));

		try {
			return em.createQuery(query)
					.setParameter(parameter, usuario)
					.setMaxResults(1)
					.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}
}
