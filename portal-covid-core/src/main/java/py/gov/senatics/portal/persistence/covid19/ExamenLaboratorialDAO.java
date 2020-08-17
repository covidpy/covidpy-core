package py.gov.senatics.portal.persistence.covid19;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import py.gov.senatics.portal.modelCovid19.ExamenLaboratorial;

@Stateless
public class ExamenLaboratorialDAO {

    @PersistenceContext(unitName = "covid19")
    private EntityManager em;

    public void save(ExamenLaboratorial examenLaboratorial)
    {
    	em.persist(examenLaboratorial);
    }
    
    public void update(ExamenLaboratorial examenLaboratorial)
    {
    	em.merge(examenLaboratorial);
    	em.flush();
    }
    
    public ExamenLaboratorial getMaxDateByPaciente(Long pacienteId)
    {
    	List<ExamenLaboratorial> result=em.createQuery("from ExamenLaboratorial where paciente.id=:pacienteId order by fechaPrevistaTomaMuestraLaboratorial desc").setParameter("pacienteId", pacienteId).setMaxResults(1).getResultList();
    	if(result.isEmpty())
    	{
    		return null;
    	}
    	else
    	{
    		return result.get(0);
    	}
    }

}
