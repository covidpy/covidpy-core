package py.gov.senatics.portal.persistence.covid19;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AgendadosDAO {

    @PersistenceContext(unitName = "mspbsCovid19")
    private EntityManager em;

    public List getAgendados(Integer lastId)
    {
    	return em.createNativeQuery("select id, cedula, telefono1, fecha_reserva, nombre, nombre_sitio from agendados where id>:lastId order by id").setParameter("lastId", lastId).getResultList();
    }
}
