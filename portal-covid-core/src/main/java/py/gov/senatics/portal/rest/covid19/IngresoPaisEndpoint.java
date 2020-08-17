package py.gov.senatics.portal.rest.covid19;

import java.util.Date;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.gov.senatics.portal.business.UsuarioBC;
import py.gov.senatics.portal.business.covid19.IngresoPaisBC;
import py.gov.senatics.portal.business.covid19.PacienteBC;
import py.gov.senatics.portal.modelCovid19.FormSeccionDatosBasicos;
import py.gov.senatics.portal.modelCovid19.FormSeccionDatosClinicos;
import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.modelCovid19.PacienteDatosPersonalesBasicos;
import py.gov.senatics.portal.modelCovid19.Registro;
import py.gov.senatics.portal.modelCovid19.RegistroFormulario;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;
import py.gov.senatics.portal.persistence.covid19.FormSeccionDatosBasicosDAO;
import py.gov.senatics.portal.persistence.covid19.FormSeccionDatosClinicosDAO;
import py.gov.senatics.portal.persistence.covid19.RegistroDAO;
import py.gov.senatics.portal.persistence.covid19.RegistroFormularioDAO;
import py.gov.senatics.portal.util.SmsSender;


/**
 * @author ricardo
 *
 */
@Path("/covid19api/ingresoPais")
@RequestScoped
public class IngresoPaisEndpoint {
	@Inject
	private RegistroDAO registroDAO;
	
	@Inject
	private RegistroFormularioDAO registroFormularioDAO;
	
	@Inject
	private FormSeccionDatosBasicosDAO formSeccionDatosBasicosDAO;
	
	@Inject
	private FormSeccionDatosClinicosDAO formSeccionDatosClinicosDAO;
	
	@Inject
	private SmsSender smsSender;
	
	@Inject
	private IngresoPaisBC ingresoPaisBC;
	
	@Inject
	private PacienteBC pacienteBC;
	
	@Inject
	private UsuarioBC usuarioBC;

	@Path("/datosBasicosViajero/{rcToken}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response datosBasicosViajero(@PathParam("rcToken") String rcToken, FormSeccionDatosBasicos formSeccionDatosBasicos) throws Exception
	{
		
		if(ingresoPaisBC.isCaptchaValid(rcToken)) {
			if(formSeccionDatosBasicos.getNumeroCelular().startsWith("09"))
			{
				formSeccionDatosBasicos.setNumeroCelular(formSeccionDatosBasicos.getNumeroCelular().replaceFirst("09", "5959"));
			}
			else if(!formSeccionDatosBasicos.getNumeroCelular().startsWith("595"))
			{
				return Response.status(400).entity("\"Verifique el número de celular\"").build();
			}
			
			Usuario usuario=usuarioBC.findByCedula(formSeccionDatosBasicos.getNumeroDocumento());
			if(usuario!=null)
			{
				return Response.status(400).entity("\"Usuario ya existente\"").build();
			}
			
			Registro registro=new Registro();
			registro.setFechaCreacion(new Date());
			registro.setResponsableRegistro("ciudadano");
			registro.setCodigoVerificacion(UUID.randomUUID().toString().substring(0, 8));
			registro.setEstado("registro");
			registro.setTipoRegistro("ingresopais");
			registroDAO.save(registro);
			RegistroFormulario registroFormulario=new RegistroFormulario();
			registroFormulario.setRegistro(registro);
			registroFormulario.setFechaCreacion(new Date());
			registroFormulario.setNombre("ingresopais");
			registroFormulario.setEstado("completo");
			registroFormulario.setRegistroFormularioAcompanante(false);
			registroFormularioDAO.save(registroFormulario);
			formSeccionDatosBasicos.setRegistroFormulario(registroFormulario);
			formSeccionDatosBasicos.setNumeroCelularVerificado("no verificado");
			//formSeccionDatosBasicos.setContrasenha(new String(Base64.getUrlEncoder().encode(MessageDigest.getInstance("SHA-256").digest(formSeccionDatosBasicos.getContrasenha().getBytes()))));
			formSeccionDatosBasicos.setContrasenha(usuarioBC.generarClave(formSeccionDatosBasicos.getContrasenha()));
			formSeccionDatosBasicosDAO.save(formSeccionDatosBasicos);
			//smsSender.sendSms(formSeccionDatosBasicos.getNumeroCelular(), "+15403169161", "Portal Covid19 Paraguay: Su código de verificación es: "+registro.getCodigoVerificacion());
			smsSender.sendSMSMenuMovil(formSeccionDatosBasicos.getNumeroCelular(), formSeccionDatosBasicos.getNombre()+" "+formSeccionDatosBasicos.getApellido()+", como medida de prevención usted debe realizar aislamiento domiciliario. Su código de verificación es:"+registro.getCodigoVerificacion()+".Vigilancia Sanitaria MSPBS.","ingresopais");
			return Response.ok("\""+registro.getId()+"\"").build();
		}else {
			return Response.status(400).entity("Por favor, vuelva a cargar la página. Validación automática (captcha) no ha sido detectada.").build();
		}
		
		
	}
	
	@Path("/validarTelefono/{idRegistro}/{codigoVerificacion}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response validarTelefono(@PathParam("idRegistro") Integer idRegistro, @PathParam("codigoVerificacion") String codigoVerificacion)
	{
		FormSeccionDatosBasicos formSeccionDatosBasicos=formSeccionDatosBasicosDAO.findByRegistroId(idRegistro);
		if(formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getCodigoVerificacion().equals(codigoVerificacion))
		{
			formSeccionDatosBasicos.setNumeroCelularVerificado("verificado");
			formSeccionDatosBasicosDAO.update(formSeccionDatosBasicos);
			return Response.ok().build();
		}
		else
		{
			return Response.status(400).build();
		}
	}
	
	@Path("/datosClinicos")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response datosClinicos(FormSeccionDatosClinicos formSeccionDatosClinicos) throws Exception
	{
		if(formSeccionDatosClinicos.getEvaluacionRiesgoViveSolo()==null)
		{
			return Response.status(400).entity("\"Debe marcar si vive sólo.\"").build();
		}
		if(formSeccionDatosClinicos.getEvaluacionRiesgoTieneHabitacionPropria()==null)
		{
			return Response.status(400).entity("\"Debe marcar si tiene habitación propia.\"").build();
		}
		if(formSeccionDatosClinicos.getEnfermedadBaseCardiopatiaCronica()==null)
		{
			return Response.status(400).entity("\"Debe marcar si tiene cardiopatía crónica.\"").build();
		}
		if(formSeccionDatosClinicos.getEnfermedadBasePulmonarCronico()==null)
		{
			return Response.status(400).entity("\"Debe marcar si tiene enfermedad pulmonar crónica.\"").build();
		}
		if(formSeccionDatosClinicos.getEnfermedadBaseAsma()==null)
		{
			return Response.status(400).entity("\"Debe marcar si tiene asma.\"").build();
		}
		if(formSeccionDatosClinicos.getEnfermedadBaseDiabetes()==null)
		{
			return Response.status(400).entity("\"Debe marcar si tiene diabetes.\"").build();
		}
		if(formSeccionDatosClinicos.getEnfermedadBaseRenalCronico()==null)
		{
			return Response.status(400).entity("\"Debe marcar si tiene enfermedad renal crónica.\"").build();
		}
		if(formSeccionDatosClinicos.getEnfermedadBaseHepaticaGrave()==null)
		{
			return Response.status(400).entity("\"Debe marcar si tiene enfermedad hepática grave.\"").build();
		}
		
		if(formSeccionDatosClinicos.getSintomasFiebre()==null)
		{
			return Response.status(400).entity("\"Debe marcar si tiene fiebre.\"").build();
		}
		else if(formSeccionDatosClinicos.getSintomasFiebre()&&(formSeccionDatosClinicos.getSintomasFiebreValor()==null||formSeccionDatosClinicos.getSintomasFiebreValor().trim().isEmpty()))
		{
			return Response.status(400).entity("\"Debe indicar su temperatura en caso de fiebre.\"").build();
		}
		
		if(formSeccionDatosClinicos.getSintomasDificultadRespirar()==null)
		{
			return Response.status(400).entity("\"Debe marcar si tiene dificultad para respirar.\"").build();
		}
		if(formSeccionDatosClinicos.getSintomasDiarrea()==null)
		{
			return Response.status(400).entity("\"Debe marcar si tiene diarrea.\"").build();
		}
		
		if(formSeccionDatosClinicos.getEvaluacionRiesgoUsomedicamento()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}
		else if(formSeccionDatosClinicos.getEvaluacionRiesgoUsomedicamento()&&(formSeccionDatosClinicos.getEvaluacionRiesgoMedicamento()==null||formSeccionDatosClinicos.getEvaluacionRiesgoMedicamento().trim().isEmpty()))
		{
			return Response.status(400).entity("\"Debe indicar los medicamentos que usa\"").build();
		}
		FormSeccionDatosBasicos formSeccionDatosBasicos=formSeccionDatosBasicosDAO.findByRegistroId(formSeccionDatosClinicos.getIdRegistro());
		if(formSeccionDatosBasicos==null)
		{
			return Response.status(404).build();
		}
		else
		{
			formSeccionDatosClinicos.setRegistroFormulario(formSeccionDatosBasicos.getRegistroFormulario());
			formSeccionDatosClinicosDAO.save(formSeccionDatosClinicos);
			return Response.ok().build();
		}	
		
	}
	
	@Path("/obtenerPersona/{numeroDocumento}/{codigoVerificacion}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFormSeccionDatosBasicos(@PathParam("numeroDocumento") String numeroDocumento, @PathParam("codigoVerificacion") String codigoVerificacion) throws Exception
	{
		FormSeccionDatosBasicos formSeccionDatosBasicos=formSeccionDatosBasicosDAO.findByNumeroDocumento(numeroDocumento);
		if(formSeccionDatosBasicos!=null)
		{
			if(formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getCodigoVerificacion().equals(codigoVerificacion))
			{
				return Response.ok(formSeccionDatosBasicos).build();
			}
			else
			{
				return Response.status(404).build();
			}
		}
		else
		{
			return Response.status(404).build();
		}
	}
	
	@Path("/confirmarPersona/{numeroDocumento}/{codigoVerificacion}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response confirmarIngresoPaisPersona(@PathParam("numeroDocumento") String numeroDocumento, @PathParam("codigoVerificacion") String codigoVerificacion) throws Exception
	{
		FormSeccionDatosBasicos formSeccionDatosBasicos=formSeccionDatosBasicosDAO.findByNumeroDocumento(numeroDocumento);
		if(formSeccionDatosBasicos!=null)
		{
			if(formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getCodigoVerificacion().equals(codigoVerificacion))
			{
				PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos=pacienteBC.getPacienteByNumeroDocumento(numeroDocumento);
				if(pacienteDatosPersonalesBasicos==null)
				{
					Paciente paciente=pacienteBC.createPacienteFromRegistro(formSeccionDatosBasicos.getRegistroFormulario().getRegistro());
					smsSender.sendSMSMenuMovil(formSeccionDatosBasicos.getNumeroCelular(), formSeccionDatosBasicos.getNombre()+" "+formSeccionDatosBasicos.getApellido()+", usted se ha registrado en el Sistema de Acompañamiento con éxito. Para acceder: https://appcoronavirus.mspbs.gov.py/. El sistema le pedirá su clave de seguridad. Vigilancia Sanitaria MSPBS.","ingresopais");
					return Response.ok().build();
				}
				else
				{
					return Response.status(400).entity("\"Ya fue generado el paciente\"").build();
				}
			}
			else
			{
				return Response.status(404).build();
			}
		}
		else
		{
			return Response.status(404).build();
		}
		
	}

}
