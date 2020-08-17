package py.gov.senatics.portal.persistence.covid19;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import py.gov.senatics.portal.modelCovid19.PacienteEstadoSalud;

@Stateless
public class PacienteEstadoSaludDAO {

    @PersistenceContext(unitName = "covid19")
    private EntityManager em;
    
    public void save(PacienteEstadoSalud pacienteEstadoSalud)
    {
    	em.persist(pacienteEstadoSalud);
    }

    public void update(PacienteEstadoSalud pacienteEstadoSalud)
    {
        em.merge(pacienteEstadoSalud);
        em.flush();
    }

    public PacienteEstadoSalud getByPaciente(Long idPaciente) {
        List<PacienteEstadoSalud> list=em.createQuery("from PacienteEstadoSalud where paciente.id=:idPaciente").setParameter("idPaciente", idPaciente).getResultList();
        if(list.isEmpty())
        {
        	return null;
        }
        else
        {
        	return list.get(0);
        }
    }

}
