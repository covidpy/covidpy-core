package py.gov.senatics.portal.persistence.covid19;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import py.gov.senatics.portal.modelCovid19.Ciudad;

@Stateless
public class CiudadDAO {

    @PersistenceContext(unitName = "covid19")
    private EntityManager em;

    public List<Ciudad> getCiudadesPorDepto(Integer idDepto) {
    	
    	List result=em.createQuery("Select new Ciudad(idCiudad, descripcion) from Ciudad c where c.departamento.idDepartamento=:id").setParameter("id", idDepto).getResultList();
        
        return result;
    }
}
