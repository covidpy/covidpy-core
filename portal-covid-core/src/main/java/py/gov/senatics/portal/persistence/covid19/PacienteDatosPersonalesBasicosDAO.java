package py.gov.senatics.portal.persistence.covid19;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.modelCovid19.PacienteDatosPersonalesBasicos;

@Stateless
public class PacienteDatosPersonalesBasicosDAO {

    @PersistenceContext(unitName = "covid19")
    private EntityManager em;
    
    public void save(PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos)
    {
    	em.persist(pacienteDatosPersonalesBasicos);
    }

    public void update(PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos)
    {
        em.merge(pacienteDatosPersonalesBasicos);
        em.flush();
    }

    public Paciente getPaciente()
    {
    	return null;
    }

    public PacienteDatosPersonalesBasicos getByPaciente(Paciente paciente) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<PacienteDatosPersonalesBasicos> query = builder.createQuery(PacienteDatosPersonalesBasicos.class);
        Root<PacienteDatosPersonalesBasicos> root = query.from(PacienteDatosPersonalesBasicos.class);
        Join<Object, Object> pacienteRoot =  root.join("paciente");
        query.where(builder.equal(pacienteRoot.get("id"), paciente.getId()));

        try {
        	PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos=em.createQuery(query).getSingleResult();
        	pacienteDatosPersonalesBasicos.getPaciente().getUsuario();
            return pacienteDatosPersonalesBasicos;
            
        } catch (NoResultException ex) {
            return null;
        }
    }

    public PacienteDatosPersonalesBasicos getByNroDocumento(String nroDocumento) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<PacienteDatosPersonalesBasicos> query = builder.createQuery(PacienteDatosPersonalesBasicos.class);
        Root<PacienteDatosPersonalesBasicos> root = query.from(PacienteDatosPersonalesBasicos.class);
        query.where(builder.equal(root.get("numeroDocumento"), nroDocumento));
        try {
            return em.createQuery(query).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
    
    public List<PacienteDatosPersonalesBasicos> getPacientesSinRegistroSospechoso()
    {
    	Calendar calendar=Calendar.getInstance();
    	calendar.add(Calendar.DAY_OF_YEAR, -1);
    	return em.createQuery("from PacienteDatosPersonalesBasicos where paciente.resultadoUltimoDiagnostico not in ('fallecido','alta_aislamiento','alta_confirmado','positivo') and paciente.usuario.password is null and paciente.inicioSeguimiento<:yesterday and paciente not in (select da.paciente.id from DiagnosticoAccion da where da.tipoAccion='notificacion_registro_sospechoso') order by id").setParameter("yesterday", calendar.getTime()).getResultList();
    }
    
    public List<PacienteDatosPersonalesBasicos> getPacientesSinRegistroConfirmado12()
    {
    	Calendar calendar=Calendar.getInstance();
    	calendar.add(Calendar.HOUR_OF_DAY, -12);
    	return em.createQuery("from PacienteDatosPersonalesBasicos where paciente.resultadoUltimoDiagnostico in ('positivo') and paciente.usuario.password is null and paciente.inicioSeguimiento<:limite and paciente not in (select da.paciente.id from DiagnosticoAccion da where da.tipoAccion='notificacion_registro_confirmado_12') order by id").setParameter("limite", calendar.getTime()).getResultList();
    }
    
    public List<PacienteDatosPersonalesBasicos> getPacientesSinRegistroConfirmado16()
    {
    	Calendar calendar=Calendar.getInstance();
    	calendar.add(Calendar.HOUR_OF_DAY, -16);
    	return em.createQuery("from PacienteDatosPersonalesBasicos where paciente.resultadoUltimoDiagnostico in ('positivo') and paciente.usuario.password is null and paciente.inicioSeguimiento<:limite and paciente not in (select da.paciente.id from DiagnosticoAccion da where da.tipoAccion='notificacion_registro_confirmado_16') order by id").setParameter("limite", calendar.getTime()).getResultList();
    }
    
    public List<PacienteDatosPersonalesBasicos> getPacientesSinRegistroUbicacionSospechoso()
    {
    	Calendar calendar=Calendar.getInstance();
    	calendar.add(Calendar.DAY_OF_YEAR, -2);
    	Calendar todayFirst=Calendar.getInstance();
    	todayFirst.set(Calendar.HOUR_OF_DAY,0);
    	todayFirst.set(Calendar.MINUTE,0);
    	todayFirst.set(Calendar.SECOND,0);
    	todayFirst.set(Calendar.MILLISECOND,0);
    	Calendar todayLast=Calendar.getInstance();
    	todayLast.set(Calendar.HOUR_OF_DAY,23);
    	todayLast.set(Calendar.MINUTE,59);
    	todayLast.set(Calendar.SECOND,59);
    	return em.createQuery("from PacienteDatosPersonalesBasicos where paciente.resultadoUltimoDiagnostico not in ('fallecido','alta_aislamiento','alta_confirmado','positivo') and paciente.usuario.password is not null and paciente not in (select ru.paciente.id from RegistroUbicacion ru where ru.timestampCreacion>:limit) and paciente not in (select da.paciente.id from DiagnosticoAccion da where tipoAccion='notificacion_ubicacion_sospechoso' and da.fechaHoraEjecucion between :todayFirst and :todayLast) order by id").setParameter("limit", calendar.getTime()).setParameter("todayFirst", todayFirst.getTime()).setParameter("todayLast", todayLast.getTime()).getResultList();
    }
    
    public List<PacienteDatosPersonalesBasicos> getPacientesSinRegistroUbicacionConfirmado3()
    {
    	Calendar calendar=Calendar.getInstance();
    	calendar.add(Calendar.HOUR_OF_DAY, -15);
    	Calendar todayFirst=Calendar.getInstance();
    	todayFirst.set(Calendar.HOUR_OF_DAY,0);
    	todayFirst.set(Calendar.MINUTE,0);
    	todayFirst.set(Calendar.SECOND,0);
    	todayFirst.set(Calendar.MILLISECOND,0);
    	Calendar todayLast=Calendar.getInstance();
    	todayLast.set(Calendar.HOUR_OF_DAY,23);
    	todayLast.set(Calendar.MINUTE,59);
    	todayLast.set(Calendar.SECOND,59);
    	return em.createQuery("from PacienteDatosPersonalesBasicos where paciente.resultadoUltimoDiagnostico in ('positivo') and paciente.usuario.password is not null and paciente not in (select ru.paciente.id from RegistroUbicacion ru where ru.timestampCreacion>:limite) and paciente not in (select da.paciente.id from DiagnosticoAccion da where da.tipoAccion='notificacion_ubicacion_confirmado_3' and da.fechaHoraEjecucion between :todayFirst and :todayLast) order by id").setParameter("limite", calendar.getTime()).setParameter("todayFirst", todayFirst.getTime()).setParameter("todayLast", todayLast.getTime()).getResultList();
    }
    
    public List<PacienteDatosPersonalesBasicos> getPacientesSinRegistroUbicacionConfirmado6()
    {
    	Calendar calendar=Calendar.getInstance();
    	calendar.add(Calendar.HOUR_OF_DAY, -18);
    	Calendar todayFirst=Calendar.getInstance();
    	todayFirst.set(Calendar.HOUR_OF_DAY,0);
    	todayFirst.set(Calendar.MINUTE,0);
    	todayFirst.set(Calendar.SECOND,0);
    	todayFirst.set(Calendar.MILLISECOND,0);
    	Calendar todayLast=Calendar.getInstance();
    	todayLast.set(Calendar.HOUR_OF_DAY,23);
    	todayLast.set(Calendar.MINUTE,59);
    	todayLast.set(Calendar.SECOND,59);
    	return em.createQuery("from PacienteDatosPersonalesBasicos where paciente.resultadoUltimoDiagnostico in ('positivo') and paciente.usuario.password is not null and paciente not in (select ru.paciente.id from RegistroUbicacion ru where ru.timestampCreacion>:limite) and paciente not in (select da.paciente.id from DiagnosticoAccion da where da.tipoAccion='notificacion_ubicacion_confirmado_6' and da.fechaHoraEjecucion between :todayFirst and :todayLast) order by id").setParameter("limite", calendar.getTime()).setParameter("todayFirst", todayFirst.getTime()).setParameter("todayLast", todayLast.getTime()).getResultList();
    }
    
    public List<PacienteDatosPersonalesBasicos> getPacientesSinReporteSalud(String sospechosos)
    {
    	Calendar calendar=Calendar.getInstance();
    	calendar.add(Calendar.HOUR_OF_DAY, -36);
    	Calendar todayFirst=Calendar.getInstance();
    	todayFirst.set(Calendar.HOUR_OF_DAY,0);
    	todayFirst.set(Calendar.MINUTE,0);
    	todayFirst.set(Calendar.SECOND,0);
    	todayFirst.set(Calendar.MILLISECOND,0);
    	Calendar todayLast=Calendar.getInstance();
    	todayLast.set(Calendar.HOUR_OF_DAY,23);
    	todayLast.set(Calendar.MINUTE,59);
    	todayLast.set(Calendar.SECOND,59);
    	String query="from PacienteDatosPersonalesBasicos where paciente.resultadoUltimoDiagnostico ";
    	if("true".equals(sospechosos))
    	{
    		query+="not in ('fallecido','alta_aislamiento','alta_confirmado')";
    	}
    	else
    	{
    		query+="in ('positivo')";
    	}
    	query+=" and paciente.usuario.password is not null and paciente not in (select rs.registroFormulario.paciente.id from ReporteSalud rs where rs.timestampCreacion>:limit) and paciente not in (select da.paciente.id from DiagnosticoAccion da where tipoAccion='notificacion_reporte_salud' and da.fechaHoraEjecucion between :todayFirst and :todayLast) order by id";
    	return em.createQuery(query).setParameter("limit", calendar.getTime()).setParameter("todayFirst", todayFirst.getTime()).setParameter("todayLast", todayLast.getTime()).getResultList();
    }
}
