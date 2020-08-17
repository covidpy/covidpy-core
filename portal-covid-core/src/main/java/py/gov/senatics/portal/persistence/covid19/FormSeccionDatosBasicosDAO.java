package py.gov.senatics.portal.persistence.covid19;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import py.gov.senatics.portal.modelCovid19.FormSeccionDatosBasicos;

/**
 * @author ricardo
 *
 */
@Stateless
public class FormSeccionDatosBasicosDAO {

	@PersistenceContext(unitName = "covid19")
	private EntityManager em;

	public void save(FormSeccionDatosBasicos formSeccionDatosBasicos)
	{
		em.persist(formSeccionDatosBasicos);
	}
	
	public FormSeccionDatosBasicos findByRegistroId(Integer registroId)
	{
		List result=em.createQuery("from FormSeccionDatosBasicos where registroFormulario.registro.id=:id").setParameter("id", registroId).getResultList();
		if(result.isEmpty())
		{
			return null;
		}
		else
		{
			return (FormSeccionDatosBasicos) result.get(0);
		}
	}
	
	public void update(FormSeccionDatosBasicos formSeccionDatosBasicosIngresoPais)
	{
		em.merge(formSeccionDatosBasicosIngresoPais);
		em.flush();
	}
	
	public FormSeccionDatosBasicos findByNumeroDocumento(String numeroDocumento)
	{
		List result=em.createQuery("from FormSeccionDatosBasicos where numeroDocumento=:numeroDocumento").setParameter("numeroDocumento", numeroDocumento).getResultList();
		if(result.isEmpty())
		{
			return null;
		}
		else
		{
			return (FormSeccionDatosBasicos) result.get(0);
		}
	}
	
	public List<FormSeccionDatosBasicos> getPacientesSinRegistroSospechoso()
    {
    	Calendar calendar=Calendar.getInstance();
    	calendar.add(Calendar.DAY_OF_YEAR, -1);
    	return em.createQuery("from FormSeccionDatosBasicos where registroFormulario.registro.tipoRegistro not in ('aislamiento_confirmado') and numeroDocumento not in (select numeroDocumento from PacienteDatosPersonalesBasicos) and registroFormulario.fechaCreacion<:yesterday order by id").setParameter("yesterday", calendar.getTime()).getResultList();
    }

}
