package py.gov.senatics.portal.rest.covid19;

import py.gov.senatics.portal.annotation.Secured;
import py.gov.senatics.portal.dto.covid19.DatosClinicosDTO;
import py.gov.senatics.portal.modelCovid19.FormSeccionDatosClinicos;
import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.modelCovid19.Registro;
import py.gov.senatics.portal.modelCovid19.RegistroFormulario;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;
import py.gov.senatics.portal.persistence.covid19.FormSeccionDatosClinicosDAO;
import py.gov.senatics.portal.persistence.covid19.PacienteDAO;
import py.gov.senatics.portal.persistence.covid19.RegistroDAO;
import py.gov.senatics.portal.persistence.covid19.RegistroFormularioDAO;
import py.gov.senatics.portal.session.UserManager;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.UUID;

@Path("covid19/datos-clinicos")
@RequestScoped
@Secured
public class DatosClinicosEndpoint {

    @Inject
    private RegistroDAO registroDAO;

    @Inject
    private RegistroFormularioDAO registroFormularioDAO;

    @Inject
    private FormSeccionDatosClinicosDAO formSeccionDatosClinicosDAO;

    @Inject
    private PacienteDAO pacienteDAO;

    @Inject
    private UserManager userManager;

    @POST
    @Path("")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response postDatosClinicos(DatosClinicosDTO datosClinicosDTO) {
        Paciente paciente = pacienteDAO.getPacienteAutenticado();
        Usuario usuario = userManager.getRequestUser();

        Registro registro=new Registro();
        registro.setFechaCreacion(new Date());
        registro.setTipoRegistro("reporte_salud");
        registro.setEstado("registro");
        registro.setResponsableRegistro("paciente");
        registro.setCodigoVerificacion(UUID.randomUUID().toString().substring(0, 8));
        registro.setUsuario(usuario);
        registroDAO.save(registro);

        RegistroFormulario registroFormulario=new RegistroFormulario();
        registroFormulario.setPaciente(paciente);
        registroFormulario.setRegistro(registro);
        registroFormulario.setFechaCreacion(new Date());
        registroFormulario.setNombre("paciente");
        registroFormulario.setEstado("registro");
        registroFormulario.setRegistroFormularioAcompanante(false);
        registroFormularioDAO.save(registroFormulario);

        FormSeccionDatosClinicos formSeccionDatosClinicos = new FormSeccionDatosClinicos();
        formSeccionDatosClinicos.setRegistroFormulario(registroFormulario);


        formSeccionDatosClinicos.setDeclaracionAgreement(false);
        formSeccionDatosClinicos.setSintomasFiebre(datosClinicosDTO.getSintomasFiebre());
        formSeccionDatosClinicos.setSintomasFiebreValor(datosClinicosDTO.getSintomasFiebreValor() != null ? datosClinicosDTO.getSintomasFiebreValor().toString() : null);
        formSeccionDatosClinicos.setSintomasTos(datosClinicosDTO.getSintomasTos());
        formSeccionDatosClinicos.setSintomasDificultadRespirar(datosClinicosDTO.getSintomasDificultadRespirar());
        formSeccionDatosClinicos.setSintomasDificultadRespirarDolorGarganta(datosClinicosDTO.getSintomasDifRespirarDolorGarganta());
        formSeccionDatosClinicos.setSintomasDificultadRespirarCansancioCaminar(datosClinicosDTO.getSintomasDifRespirarCansancioCaminar());
        formSeccionDatosClinicos.setSintomasDificultadRespirarFaltaAire(datosClinicosDTO.getSintomasDifRespirarFaltaAire());
        formSeccionDatosClinicos.setSintomasDificultadRespirarRinorrea(datosClinicosDTO.getSintomasDifRespirarRinorrea());
        formSeccionDatosClinicos.setSintomasDificultadRespirarCongestionNasal(datosClinicosDTO.getSintomasDifRespirarCongestionNasal());
        formSeccionDatosClinicos.setSintomasOtro(datosClinicosDTO.getSintomasOtros());
        formSeccionDatosClinicos.setSintomasDiarrea(datosClinicosDTO.getSintomasDiarrea());
        formSeccionDatosClinicosDAO.save(formSeccionDatosClinicos);

        return Response.ok().build();
    }
}
