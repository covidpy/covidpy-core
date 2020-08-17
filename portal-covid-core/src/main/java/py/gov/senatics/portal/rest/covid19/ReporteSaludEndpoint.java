package py.gov.senatics.portal.rest.covid19;

import org.apache.commons.beanutils.BeanUtils;
import py.gov.senatics.portal.annotation.RolAllowed;
import py.gov.senatics.portal.annotation.Secured;
import py.gov.senatics.portal.annotation.formulario.FormField;
import py.gov.senatics.portal.business.covid19.PacienteBC;
import py.gov.senatics.portal.business.covid19.ReporteSaludBC;
import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.modelCovid19.RegistroFormulario;
import py.gov.senatics.portal.modelCovid19.ReporteSalud;
import py.gov.senatics.portal.modelCovid19.admin.Rol;
import py.gov.senatics.portal.persistence.covid19.PacienteDAO;
import py.gov.senatics.portal.persistence.covid19.ReporteSaludDAO;
import py.gov.senatics.portal.rest.covid19.generic.GenericEndpoint;
import py.gov.senatics.portal.session.UserManager;
import py.gov.senatics.portal.util.Drools;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.apache.commons.beanutils.BeanUtils.getProperty;

@Path("covid19/reporte-salud")
@Secured
@RequestScoped
public class ReporteSaludEndpoint extends GenericEndpoint<ReporteSalud, ReporteSaludDAO, ReporteSalud> {

    @Inject
    private PacienteDAO pacienteDAO;

    @Inject
    private PacienteBC pacienteBC;

    @Inject
    private ReporteSaludBC reporteSaludBC;

    @Inject
    private UserManager userManager;

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response postReporteSalud(@Valid ReporteSalud reporteDto) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Paciente paciente = pacienteDAO.getPacienteAutenticado();
        RegistroFormulario registroFormulario = pacienteBC.crearRegistroFormularioAutoreporte(paciente);
        ReporteSalud entity = saveReporteSalud(paciente, reporteDto, registroFormulario);
        return Response.ok(entity).build();
    }

    private ReporteSalud saveReporteSalud(Paciente paciente, ReporteSalud reporteDto, RegistroFormulario registroFormulario) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ReporteSalud entity = this.setEntityFromDto(new ReporteSalud(), reporteDto);

        entity.setRegistroFormulario(registroFormulario);

        if (entity.getTemperatura() != null) {
            entity.setTomasteTemperatura(
                    entity.getTemperatura() > 38 ?
                            ReporteSalud.TEMPERATURA_MAS_38 : ReporteSalud.TEMPERATURA_MENOS_38
            );
        }

        if (!reporteSaludBC.debeReportarTuvoFiebreAyer(paciente)) {
            // Si no debe reportar, se calcula de reportes anteriores si tuvo fiebre ayer
            entity.setFiebreAyer(this.reporteSaludBC.tuvoFiebreAyer(paciente) ? ReporteSalud.FIEBRE_SI: ReporteSalud.FIEBRE_NO);
        }

        Drools.clasificarPaciente(entity);
        this.dao.save(entity);
        pacienteBC.createHistoricoClinicoFromReporteSalud(entity, paciente, paciente.getUsuario());
        return entity;
    }


    @POST
    @Path("cedula/{cedula:\\d+}")
    @RolAllowed(Rol.OPERADOR)
    public Response postReporteSaludOperador(
            @PathParam("cedula") String cedula,
            @Valid ReporteSalud reporteDto
    ) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Paciente paciente = pacienteBC.getPacienteFromCedula(cedula);
        if (paciente == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        RegistroFormulario registroFormulario = pacienteBC.crearRegistroFormularioOperador(paciente, userManager.getRequestUser());
        ReporteSalud entity = saveReporteSalud(paciente, reporteDto, registroFormulario);
        return Response.ok(entity).build();
    }


    @GET
    @Path("primera-vez")
    @Produces(MediaType.APPLICATION_JSON)
    public Response esPrimeraVez() {
        Paciente paciente = pacienteDAO.getPacienteAutenticado();
        return esPrimeraVez(paciente);
    }

    private Response esPrimeraVez(Paciente paciente) {
        boolean firstTime = reporteSaludBC.esPrimerReporte(paciente);
        Map<String, Boolean> dto = new HashMap<>();
        dto.put("firstTime", firstTime);
        dto.put("debeReportarFiebreAyer", reporteSaludBC.debeReportarTuvoFiebreAyer(paciente));
        return Response.ok(dto).build();
    }

    @GET
    @Path("primera-vez/{cedula:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolAllowed(Rol.OPERADOR)
    public Response esPrimeraVez(@PathParam("cedula") String cedula) {
        Paciente paciente = pacienteBC.getPacienteFromCedula(cedula);
        if (paciente == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return esPrimeraVez(paciente);
    }

    @Override
    @RolAllowed(Rol.OPERADOR)
    public Response list(Integer page, Integer pageSize, List<String> filters, String orderBy, Boolean orderDesc, String search) {
        return super.list(page, pageSize, filters, orderBy, orderDesc, search);
    }

    @Override
    @RolAllowed(Rol.OPERADOR)
    public void csvList(HttpServletResponse response, HttpServletRequest request, List<String> filters, String orderBy, boolean orderDesc, String search) {
        super.csvList(response, request, filters, orderBy, orderDesc, search);
    }

    @RolAllowed(Rol.OPERADOR)
    @GET
    @Path("ultimo-reporte/{cedula:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ultimoReporte(@PathParam("cedula") String cedula) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Paciente paciente = pacienteBC.getPacienteFromCedula(cedula);
        if (paciente == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ReporteSalud ultimoReporte = dao.getLastReporteSalud(paciente.getId());
        if (ultimoReporte == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ultimoReporte.setEsPrimeraVez(Boolean.toString(reporteSaludBC.esPrimerReporte(paciente)));
        ultimoReporte.setDebeReportarFiebreAyer(Boolean.toString(reporteSaludBC.debeReportarTuvoFiebreAyer(paciente)));

        // Seteamos los campos transient
        List<Field> fields = Arrays.stream(ReporteSalud.class.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(FormField.class) && f.isAnnotationPresent(Transient.class))
                .collect(Collectors.toList());

        for (Field field: fields) {
            FormField fieldInfo = field.getAnnotation(FormField.class);
            // Seteamos según modelField o por el nombre del campo
            BeanUtils.setProperty(
                    ultimoReporte,
                    fieldInfo.modelField().isEmpty() ?
                            field.getName() :
                            fieldInfo.modelField(),
                    getProperty(
                            ultimoReporte,
                            field.getName()
                    )
            );
        }
        return Response.ok(ultimoReporte).build();
    }
}
