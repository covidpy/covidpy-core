package py.gov.senatics.portal.rest.covid19;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.gov.senatics.portal.annotation.Secured;
import py.gov.senatics.portal.dto.covid19.DatosBasicosDTO;
import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.modelCovid19.PacienteDatosPersonalesBasicos;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;
import py.gov.senatics.portal.persistence.covid19.PacienteDAO;
import py.gov.senatics.portal.persistence.covid19.PacienteDatosPersonalesBasicosDAO;
import py.gov.senatics.portal.persistence.covid19.admin.UsuarioDao;
import py.gov.senatics.portal.session.UserManager;

@RequestScoped
@Path("/covid19/datos-basicos")
@Secured
public class DatosBasicosEndpoint {

    @Inject
    private PacienteDAO pacienteDAO;

    @Inject
    private PacienteDatosPersonalesBasicosDAO datosPersonalesBasicosDAO;

    @Inject
    private UsuarioDao usuarioDao;

    @Inject
    private UserManager userManager;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getDatosBasicos() {
        Paciente paciente = pacienteDAO.getPacienteAutenticado();
        PacienteDatosPersonalesBasicos datos = datosPersonalesBasicosDAO.getByPaciente(paciente);
        return Response.ok(datos != null ? datos : new PacienteDatosPersonalesBasicos()).build();
    }

    @PUT
    @Path("")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response putDatosBasicos(@Valid DatosBasicosDTO datos) {

        Paciente paciente = pacienteDAO.getPacienteAutenticado();
        PacienteDatosPersonalesBasicos datosActuales = datosPersonalesBasicosDAO.getByPaciente(paciente);
        datosActuales.setNombre(datos.getNombre());
        datosActuales.setApellido(datos.getApellido());
        datosActuales.setPaisNacionalidad(datos.getPaisNacionalidad());
        datosActuales.setCiudadNacimiento(datos.getCiudadNacimiento());
        datosActuales.setFechaNacimiento(datos.getFechaNacimiento());
        datosActuales.setSexo(datos.getSexo());
        datosActuales.setNumeroTelefono(datos.getNumeroTelefono());
        datosActuales.setDireccionDomicilio(datos.getDireccionDomicilio());
        datosActuales.setResidenteParaguay(datos.getResidenteParaguay());
        datosActuales.setPaisEmisorDocumento(datos.getPaisEmisorDocumento());
        datosActuales.setCiudadDomicilio(datos.getCiudadDomicilio());
        datosActuales.setDepartamentoDomicilio(datos.getDepartamentoDomicilio());
        datosPersonalesBasicosDAO.update(datosActuales);

        Usuario usuario = userManager.getRequestUser();
        usuario.setNombre(datos.getNombre());
        usuario.setApellido(datos.getApellido());
        usuarioDao.actualizar(usuario);

        return Response.ok().build();
    }

    @GET
    @Path("dias-cuarentena")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiasCuarentena() {
        Paciente paciente = pacienteDAO.getPacienteAutenticado();
        long cantidadDias;
        if (paciente.getInicioAislamiento() == null) {
            cantidadDias = 0;
        } else {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(paciente.getInicioAislamiento());
            cantidadDias = ChronoUnit.DAYS.between(
                    calendar.toInstant(),
                    Calendar.getInstance().toInstant()
            );
        }
        Map<String, Long> dto = new HashMap<>();
        dto.put("dias", cantidadDias);
        return Response.ok(dto).build();
    }



}
