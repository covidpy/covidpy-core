package py.gov.senatics.portal.persistence.covid19;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import py.gov.senatics.portal.modelCovid19.RegistroFormulario;

/**
 * @author ricardo
 *
 */
@Stateless
public class RegistroFormularioDAO {

	@PersistenceContext(unitName = "covid19")
	private EntityManager em;

	public void save(RegistroFormulario registroFormulario)
	{
		em.persist(registroFormulario);
	}
	
	public void update(RegistroFormulario registroFormulario)
	{
		em.merge(registroFormulario);
		em.flush();
	}

}
