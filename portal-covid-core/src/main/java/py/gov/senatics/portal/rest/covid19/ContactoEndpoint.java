package py.gov.senatics.portal.rest.covid19;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import py.gov.senatics.portal.annotation.RolAllowed;
import py.gov.senatics.portal.annotation.Secured;
import py.gov.senatics.portal.business.covid19.CensoContactoBC;
import py.gov.senatics.portal.modelCovid19.CensoContacto;
import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.persistence.covid19.ContactoDAO;
import py.gov.senatics.portal.persistence.covid19.PacienteDAO;
import py.gov.senatics.portal.session.UserManager;

@Path("covid19/contactos")
@RequestScoped
@Secured
public class ContactoEndpoint {

    @Inject
    private ContactoDAO contactoDAO;

    @Inject
    private PacienteDAO pacienteDAO;
    
    @Inject
    private CensoContactoBC censoContactoBC;
    
    @Inject
    private UserManager userManager;

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getAllContactos() {
        return Response.ok(contactoDAO.getAllByUser()).build();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response crearContacto(CensoContacto censoContacto, @Context UriInfo uriInfo) {
    	
        Map<String, List<String>> error = validate(censoContacto);
        if(!error.isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
        if(censoContacto.getTipo() != null && censoContacto.getTipo().length()>100)
        {
        	censoContacto.setTipo(censoContacto.getTipo().substring(0, 100));
        }
        CensoContacto entity = contactoDAO.crearContacto(censoContacto);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(entity.getId().toString());
        return Response.created(builder.build()).entity(entity).build();
    }

    /*@POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response editarContacto(@PathParam("id") String id, CensoContacto censoContacto, @Context UriInfo uriInfo) {
        Paciente paciente = pacienteDAO.getPacienteAutenticado();
        CensoContacto contacto = contactoDAO.getById(Long.parseLong(id));
        if (!contacto.getPaciente().getId().equals(paciente.getId())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Map<String, List<String>> error = validate(censoContacto);
        if(!error.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
        CensoContacto entity = contactoDAO.editarContacto(Long.parseLong(id), censoContacto);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(entity.getId().toString());
        return Response.ok(builder.build()).entity(entity).build();
    }*/
    
    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response editarContacto(@PathParam("id") String id, CensoContacto censoContacto, @Context UriInfo uriInfo) {
    	Boolean esPaciente = false;
		
		CensoContacto contacto = contactoDAO.getById(Long.parseLong(id));
		
		esPaciente = pacienteDAO.esPaciente(userManager.getRequestUser());
		if(esPaciente) {
			Paciente paciente = pacienteDAO.getPacienteAutenticado();
	        if (!contacto.getPaciente().getId().equals(paciente.getId())) {
	            return Response.status(Response.Status.FORBIDDEN).build();
	        }
		}else {
			if (!contacto.getPaciente().getId().equals(censoContacto.getPaciente().getId())) {
	            return Response.status(Response.Status.FORBIDDEN).build();
	        }
		}

        Map<String, List<String>> error = validate(censoContacto);
        if(!error.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
        CensoContacto entity = contactoDAO.editarContacto(Long.parseLong(id), censoContacto);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(entity.getId().toString());
        return Response.ok(builder.build()).entity(entity).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getById(@PathParam("id") String id) {
        Paciente paciente = pacienteDAO.getPacienteAutenticado();
        CensoContacto contacto = contactoDAO.getById(Long.parseLong(id));
        if (!contacto.getPaciente().getId().equals(paciente.getId())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok(contacto).build();
    }

    private Map<String, List<String>> validate(CensoContacto censoContacto) {
        Map<String, List<String>> errorMap = new HashMap<String, List<String>>();
        if(censoContacto.getNombres() == null) {
            errorMap.put("nombres",  new ArrayList<String>());
            errorMap.get("nombres").add("El campo es requerido");
        }
        if(censoContacto.getApellidos() == null) {
            errorMap.put("apellidos",  new ArrayList<String>());
            errorMap.get("apellidos").add("El campo es requerido");
        }
        if (censoContacto.getFechaUltimoContacto() != null &&
                censoContacto.getFechaUltimoContacto().compareTo(new Date()) > 0) {
            errorMap.put("fechaUltimoContacto",  new ArrayList<String>());
            errorMap.get("fechaUltimoContacto").add("No puede ser una fecha futura");
        }
        return errorMap;
    }
    
    @GET
   	@Path("/getContactosPaciente")
   	@Produces(MediaType.APPLICATION_JSON)
    @Secured
	@RolAllowed("Operador")
   	public Response getContactosPaciente(@QueryParam("idPaciente") Long idPaciente, @QueryParam("pageSize") int pageSize, @QueryParam("start") int first,
   			@QueryParam("sortField") String sortField, @QueryParam("sortAsc") boolean sortAsc, @QueryParam("filter") String filter) {

       	return this.censoContactoBC.getContactosPaciente(idPaciente, pageSize, first, sortField, sortAsc, filter);
   	}
    
    @POST
    @Path("/borrar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response borrarContacto(Long idContacto) {
    	//CensoContacto contacto = contactoDAO.getById(idContacto);
    	Boolean borrado = contactoDAO.borrarContacto(idContacto);
    	if(borrado) {
    		return Response.ok().build();
    	}	
    	
    	return Response.status(404).build();
    	
    }

}
