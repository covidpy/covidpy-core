package py.gov.senatics.portal.rest.covid19;

import py.gov.senatics.portal.annotation.Secured;
import py.gov.senatics.portal.persistence.covid19.NotificacionDAO;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("covid19/notificaciones")
@RequestScoped
@Secured
public class NotificacionEndpoint {

    @Inject
    private NotificacionDAO notificacionDAO;

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getAllNotificaciones() {
        return Response.ok(notificacionDAO.getAllByUser()).build();
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getById(@PathParam("id") String id) {
        return Response.ok(notificacionDAO.getById(Long.parseLong(id))).build();
    }

    @POST
    @Path("/{id}/visto")
    public Response guardarVisto(@PathParam("id") String id, @Context UriInfo uriInfo) {
        notificacionDAO.guardarVisto(Long.parseLong(id));
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        return Response.ok(builder.build()).build();
    }
}
