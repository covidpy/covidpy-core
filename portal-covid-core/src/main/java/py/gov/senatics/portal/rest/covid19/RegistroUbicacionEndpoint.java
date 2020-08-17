package py.gov.senatics.portal.rest.covid19;

import py.gov.senatics.portal.annotation.Secured;
import py.gov.senatics.portal.dto.covid19.DatosUbicacionDTO;
import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.modelCovid19.RegistroUbicacion;
import py.gov.senatics.portal.persistence.covid19.PacienteDAO;
import py.gov.senatics.portal.persistence.covid19.RegistroUbicacionDAO;
import py.gov.senatics.portal.rest.covid19.generic.GenericEndpoint;
import py.gov.senatics.portal.util.Config;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("covid19/registro-ubicacion")
@RequestScoped
@Secured
public class RegistroUbicacionEndpoint extends GenericEndpoint<RegistroUbicacion, RegistroUbicacionDAO, RegistroUbicacion> {

    @Inject
    private PacienteDAO pacienteDAO;

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response postRegistroUbicacion(RegistroUbicacion registroUbicacion, @Context UriInfo uriInfo){
        Paciente paciente = pacienteDAO.getPacienteAutenticado();
        registroUbicacion.setPaciente(paciente);
        if(registroUbicacion.getAccuracy()!=null||registroUbicacion.getAltitude()!=null||registroUbicacion.getAltitudeAccuracy()!=null||registroUbicacion.getSpeed()!=null)
        {
        	registroUbicacion.setTipoRegistroUbicacion("automatico");
        }
        else
        {
        	registroUbicacion.setTipoRegistroUbicacion("manual");
        }
        HttpServletRequest servletRequest = CDI.current().select(HttpServletRequest.class).get();
        registroUbicacion.setIpCliente(servletRequest.getRemoteAddr());
        registroUbicacion.setUserAgent(servletRequest.getHeader("User-Agent"));
        RegistroUbicacion entity = dao.crearRegistroUbicacion(registroUbicacion);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(entity.getId().toString());
        return Response.created(builder.build()).entity(entity).build();
    }

    @GET
    @Path("/last")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLast() {
        Paciente paciente = pacienteDAO.getPacienteAutenticado();
        RegistroUbicacion ultimoRegistro = dao.getLastRegistro(paciente);
        if (ultimoRegistro == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(ultimoRegistro).build();
    }

    @POST
    @Path("/actualizarUbicacionPaciente")
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarUbicacionPaciente(DatosUbicacionDTO datos) {
    	Paciente paciente = pacienteDAO.getPacienteAutenticado();
    	RegistroUbicacion registroUbicacion=new RegistroUbicacion();
        registroUbicacion.setPaciente(paciente);
        registroUbicacion.setTipoRegistroUbicacion("automatico");
        registroUbicacion.setAccuracy(datos.getAccuracy());
        registroUbicacion.setAltitude(datos.getAltitude());
        registroUbicacion.setAltitudeAccuracy(datos.getAltitudeAccuracy());
        registroUbicacion.setLatReportado(datos.getLatitude().floatValue());
        registroUbicacion.setLongReportado(datos.getLongitude().floatValue());
        registroUbicacion.setLatDispositivo(datos.getLatitude().floatValue());
        registroUbicacion.setLongDispositivo(datos.getLongitude().floatValue());
        registroUbicacion.setSpeed(datos.getSpeed());
        registroUbicacion.setActividadIdentificada(datos.getActivity());
        registroUbicacion.setTipoEvento(datos.getEvent());
        HttpServletRequest servletRequest = CDI.current().select(HttpServletRequest.class).get();
        registroUbicacion.setIpCliente(servletRequest.getRemoteAddr());
        registroUbicacion.setUserAgent(servletRequest.getHeader("User-Agent"));
        RegistroUbicacion entity = dao.crearRegistroUbicacion(registroUbicacion);
        boolean status = false;
        if(datos != null) {
        	Config config = new Config();
        	status = config.insertLocation(datos);
        }
        return Response.ok(status).build();
    }

}
