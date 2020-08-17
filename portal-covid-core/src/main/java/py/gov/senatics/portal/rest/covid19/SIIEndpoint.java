
package py.gov.senatics.portal.rest.covid19;


import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.gov.senatics.portal.annotation.RolAllowed;
import py.gov.senatics.portal.annotation.Secured;
import py.gov.senatics.portal.util.AuthDAO;
import py.gov.senatics.portal.util.Config;

@RequestScoped
@Path("/covid19/aquipath")
public class SIIEndpoint {

	@Inject
	private AuthDAO authDAO;
	
	@Path("/identificaciones/{cedula}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@RolAllowed("Operador")
	public Response getIdentificacionesCedula(@PathParam("cedula") String cedula) throws Exception {
		return Response.ok(authDAO.obtenerDatos(Config.URL_API_IDENTIFICACIONES, cedula)).build();
	}

}
