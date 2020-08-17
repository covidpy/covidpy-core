package py.gov.senatics.portal.persistence.covid19;

import java.util.Date;
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

import py.gov.senatics.portal.modelCovid19.CensoContacto;
import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;
import py.gov.senatics.portal.session.UserManager;

/**
 * @author cdelgado
 *
 */
@Stateless
public class ContactoDAO {

	@PersistenceContext(unitName = "covid19")
	private EntityManager em;

	@Inject
	private UserManager userManager;
	
	@Inject
	private PacienteDAO pacienteDAO;

	public CensoContacto save(CensoContacto censoContacto)
	{
		em.persist(censoContacto);
		em.flush();
		return censoContacto;
	}

	private CriteriaBuilder getCriteriaBuilder(){
		return em.getCriteriaBuilder();
	}

	public List<CensoContacto> getAllByUser() {
		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<Paciente> query = builder.createQuery(Paciente.class);
		Root<Paciente> root = query.from(Paciente.class);
		Join<Object, Object> userRoot =  root.join("usuario");
		query.where(builder.equal(userRoot.get("id"), getLoggedUser().getId()));
		Paciente paciente = em.createQuery(query).getSingleResult();

		String queryString = "SELECT c FROM CensoContacto c WHERE c.paciente.id =:paciente " +
				"ORDER BY c.fechaUltimoContacto DESC NULLS LAST, c.timestampCreacion DESC";
		TypedQuery<CensoContacto> q = em.createQuery(queryString, CensoContacto.class);
		q.setParameter("paciente", paciente.getId());

		return q.getResultList();
	}

	/*public CensoContacto crearContacto(CensoContacto censoContacto) {
		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<Paciente> query = builder.createQuery(Paciente.class);
		Root<Paciente> root = query.from(Paciente.class);
		Join<Object, Object> userRoot =  root.join("usuario");
		query.where(builder.equal(userRoot.get("id"), getLoggedUser().getId()));

		Paciente paciente = em.createQuery(query).getSingleResult();

		censoContacto.setPaciente(paciente);
		censoContacto.setTimestampCreacion(new Date());
		return save(censoContacto);
	}*/
	
	public CensoContacto crearContacto(CensoContacto censoContacto) {
		Boolean esPaciente = false;
		/*for(Rol rol : getLoggedUser().getRols()) {
			if(rol.getNombre().equalsIgnoreCase("Paciente")) {
				esPaciente = true;
				break;
			}
		}*/
		esPaciente = pacienteDAO.esPaciente(getLoggedUser());
		
		if(esPaciente) {
			CriteriaBuilder builder = getCriteriaBuilder();
			CriteriaQuery<Paciente> query = builder.createQuery(Paciente.class);
			Root<Paciente> root = query.from(Paciente.class);
			Join<Object, Object> userRoot =  root.join("usuario");
			query.where(builder.equal(userRoot.get("id"), getLoggedUser().getId()));
			
			Paciente paciente = em.createQuery(query).getSingleResult();
			censoContacto.setPaciente(paciente);
		}else {
			Paciente pacienteN = new Paciente(censoContacto.getPaciente().getId());
			censoContacto.setPaciente(pacienteN);
		}
		censoContacto.setCreadoPor(getLoggedUser());
		
		censoContacto.setTimestampCreacion(new Date());
		return save(censoContacto);
	}

	public CensoContacto editarContacto(Long id, CensoContacto censoContacto) {
		CensoContacto censoContactoDb = getById(id);

		censoContactoDb.setNombres(censoContacto.getNombres());
		censoContactoDb.setApellidos(censoContacto.getApellidos());
		censoContactoDb.setFechaUltimoContacto(censoContacto.getFechaUltimoContacto());
		censoContactoDb.setDomicilio(censoContacto.getDomicilio());
		censoContactoDb.setNroDocumento(censoContacto.getNroDocumento());
		censoContactoDb.setTelefono(censoContacto.getTelefono());
		censoContactoDb.setTipo(censoContacto.getTipo());
		/*****/
		censoContactoDb.setModificadoPor(getLoggedUser());
		censoContactoDb.setFechaModificacion(new Date());
		/*****/
		return save(censoContactoDb);
	}

	public CensoContacto getById(Long id) {
		return em.find(CensoContacto.class, id);
	}

	private Usuario getLoggedUser(){
		return userManager.getRequestUser();
	}
	
	public Boolean borrarContacto(Long idContacto) {
		Boolean borrado = false;
		CensoContacto contacto = em.find(CensoContacto.class, idContacto);
		em.remove(contacto);
		em.flush();
		borrado = true;
		return borrado;
	}

}
