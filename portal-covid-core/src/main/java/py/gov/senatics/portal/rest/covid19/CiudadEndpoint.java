package py.gov.senatics.portal.rest.covid19;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.gov.senatics.portal.persistence.covid19.CiudadDAO;

@Path("covid19/ciudades/{idDepto}")
public class CiudadEndpoint {

    @Inject
    private CiudadDAO ciudadDAO;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getCiudadesPorDepto(@PathParam("idDepto") Integer idDepto) {
       return Response.ok(this.ciudadDAO.getCiudadesPorDepto(idDepto)).build();
    }
}
