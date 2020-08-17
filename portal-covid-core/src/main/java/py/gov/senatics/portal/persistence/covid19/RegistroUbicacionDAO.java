package py.gov.senatics.portal.persistence.covid19;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.modelCovid19.RegistroUbicacion;
import py.gov.senatics.portal.persistence.covid19.generic.BaseDAO;

@Stateless
public class RegistroUbicacionDAO extends BaseDAO<RegistroUbicacion> {


    public RegistroUbicacion crearRegistroUbicacion(RegistroUbicacion registroUbicacion) {

        RegistroUbicacion entity = new RegistroUbicacion();
        entity.setLatDispositivo(registroUbicacion.getLatDispositivo());
        entity.setLatReportado(registroUbicacion.getLatReportado());
        entity.setLongDispositivo(registroUbicacion.getLongDispositivo());
        entity.setLongReportado(registroUbicacion.getLongReportado());
        entity.setPaciente(registroUbicacion.getPaciente());
        entity.setAccuracy(registroUbicacion.getAccuracy());
        entity.setAltitude(registroUbicacion.getAltitude());
        entity.setAltitudeAccuracy(registroUbicacion.getAltitudeAccuracy());
        entity.setIpCliente(registroUbicacion.getIpCliente());
        entity.setSpeed(registroUbicacion.getSpeed());
        entity.setTipoRegistroUbicacion(registroUbicacion.getTipoRegistroUbicacion());
        entity.setUserAgent(registroUbicacion.getUserAgent());
        entity.setActividadIdentificada(registroUbicacion.getActividadIdentificada());
        entity.setTipoEvento(registroUbicacion.getTipoEvento());
        if(entity.getUserAgent().length()>100)
        {
        	entity.setUserAgent(entity.getUserAgent().substring(0, 100));
        }
        em.persist(entity);
        em.flush();
        return entity;
    }

    public RegistroUbicacion getLastRegistro(Paciente paciente) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<RegistroUbicacion> query = builder.createQuery(RegistroUbicacion.class);
        Root<RegistroUbicacion> root = query.from(RegistroUbicacion.class);
        Join<Object, Object> pacienteRoot = root.join("paciente");
        query.where(builder.equal(pacienteRoot.get("id"), paciente.getId()));
        query.orderBy(builder.desc(root.get("timestampCreacion")));

        try {
            return em.createQuery(query).setMaxResults(1).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
