package py.gov.senatics.portal.rest.covid19;

import py.gov.senatics.portal.annotation.Secured;
import py.gov.senatics.portal.persistence.covid19.PaisDAO;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("covid19/paises")
@Secured
public class PaisEndpoint {

    @Inject
    private PaisDAO paisDAO;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getAll() {
       return Response.ok(this.paisDAO.getAll()).build();
    }
}
