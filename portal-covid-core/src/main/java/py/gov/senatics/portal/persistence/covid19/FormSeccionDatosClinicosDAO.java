package py.gov.senatics.portal.persistence.covid19;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import py.gov.senatics.portal.modelCovid19.FormSeccionDatosClinicos;

/**
 * @author ricardo
 *
 */
@Stateless
public class FormSeccionDatosClinicosDAO {

	@PersistenceContext(unitName = "covid19")
	private EntityManager em;

	public void save(FormSeccionDatosClinicos formSeccionDatosClinicos)
	{
		em.persist(formSeccionDatosClinicos);
	}

}
