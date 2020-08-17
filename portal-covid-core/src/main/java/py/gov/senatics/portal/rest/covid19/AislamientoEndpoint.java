package py.gov.senatics.portal.rest.covid19;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.gov.senatics.portal.annotation.RolAllowed;
import py.gov.senatics.portal.annotation.Secured;
import py.gov.senatics.portal.business.UsuarioBC;
import py.gov.senatics.portal.business.covid19.PacienteBC;
import py.gov.senatics.portal.modelCovid19.ExamenLaboratorial;
import py.gov.senatics.portal.modelCovid19.FormSeccionDatosBasicos;
import py.gov.senatics.portal.modelCovid19.FormSeccionDatosClinicos;
import py.gov.senatics.portal.modelCovid19.Notificacion;
import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.modelCovid19.PacienteDatosPersonalesBasicos;
import py.gov.senatics.portal.modelCovid19.Registro;
import py.gov.senatics.portal.modelCovid19.RegistroFormulario;
import py.gov.senatics.portal.modelCovid19.RegistroUbicacion;
import py.gov.senatics.portal.modelCovid19.ReporteSalud;
import py.gov.senatics.portal.modelCovid19.admin.LoginAutomatico;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;
import py.gov.senatics.portal.persistence.covid19.ExamenLaboratorialDAO;
import py.gov.senatics.portal.persistence.covid19.FormSeccionDatosBasicosDAO;
import py.gov.senatics.portal.persistence.covid19.FormSeccionDatosClinicosDAO;
import py.gov.senatics.portal.persistence.covid19.NotificacionDAO;
import py.gov.senatics.portal.persistence.covid19.PacienteDatosPersonalesBasicosDAO;
import py.gov.senatics.portal.persistence.covid19.RegistroDAO;
import py.gov.senatics.portal.persistence.covid19.RegistroFormularioDAO;
import py.gov.senatics.portal.session.UserManager;
import py.gov.senatics.portal.util.Config;
import py.gov.senatics.portal.util.Drools;
import py.gov.senatics.portal.util.SmsException;
import py.gov.senatics.portal.util.SmsSender;


/**
 * @author ricardo
 *
 */
@Path("/covid19api/aislamiento")
@RequestScoped
public class AislamientoEndpoint {
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
	private PacienteBC pacienteBC;
	
	@Inject
	private UsuarioBC usuarioBC; 
	
	@Inject
	private UserManager userManager;
	
	@Inject
	private Config config;
	
	@Inject
	private NotificacionDAO notificacionDAO;
	
	@Inject
	private PacienteDatosPersonalesBasicosDAO pacienteDatosPersonalesBasicosDAO;
	
	@Inject
	private ExamenLaboratorialDAO examenLaboratorialDAO;
	
	@Path("/datosBasicos")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@RolAllowed("Operador")
	public Response datosBasicos(FormSeccionDatosBasicos formSeccionDatosBasicos) throws Exception
	{
		if(formSeccionDatosBasicos.getNumeroCelular()==null||formSeccionDatosBasicos.getNumeroCelular().trim().isEmpty())
		{
			return Response.status(400).entity("\"El número de celular es requerido\"").build();
		}
		else if(formSeccionDatosBasicos.getNumeroCelular().matches("09[9876]\\d{7}"))
		{
			formSeccionDatosBasicos.setNumeroCelular("595"+formSeccionDatosBasicos.getNumeroCelular().substring(1));
		}
		else if(formSeccionDatosBasicos.getNumeroCelular().matches("\\+5959[9876]\\d{7}"))
		{
			formSeccionDatosBasicos.setNumeroCelular(formSeccionDatosBasicos.getNumeroCelular().substring(1));
		}
		if(!formSeccionDatosBasicos.getNumeroCelular().matches("5959[9876]\\d{7}"))
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
		registro.setResponsableRegistro("operador");
		registro.setCodigoVerificacion(UUID.randomUUID().toString().substring(0, 8));
		registro.setEstado("registro");
		registro.setTipoRegistro(formSeccionDatosBasicos.getTipoRegistro());
		registro.setUsuario(userManager.getRequestUser());
		registroDAO.save(registro);
		RegistroFormulario registroFormulario=new RegistroFormulario();
		registroFormulario.setRegistro(registro);
		registroFormulario.setFechaCreacion(new Date());
		registroFormulario.setNombre("aislamiento");
		registroFormulario.setEstado("no completado");
		registroFormulario.setRegistroFormularioAcompanante(false);
		registroFormularioDAO.save(registroFormulario);
		formSeccionDatosBasicos.setRegistroFormulario(registroFormulario);
		formSeccionDatosBasicos.setNumeroCelularVerificado("no verificado");
		//formSeccionDatosBasicosIngresoPais.setContrasenha(new String(Base64.getUrlEncoder().encode(MessageDigest.getInstance("SHA-256").digest(formSeccionDatosBasicosIngresoPais.getContrasenha().getBytes()))));
		//smsSender.sendSms(formSeccionDatosBasicos.getNumeroCelular(), "+12058094778", "Portal Covid19 Paraguay: Su código de verificación es: "+registro.getCodigoVerificacion());
		try
		{
			formSeccionDatosBasicosDAO.save(formSeccionDatosBasicos);
			Paciente paciente=pacienteBC.createPacienteFromRegistro(formSeccionDatosBasicos.getRegistroFormulario().getRegistro());
			SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
			if("positivo".equals(paciente.getResultadoUltimoDiagnostico()))
			{
				Notificacion notificacion=new Notificacion();
				notificacion.setMensaje(config.getPropValues("covid19_diagnostico_positivo_instrucciones_recomendacion_limpieza_2"));
				notificacion.setPaciente(paciente);
				notificacion.setRemitente("Limpieza Domiciliaria 2");
				notificacion.setFechaNotificacion(new Date());
				notificacion.setVisto(false);
				notificacionDAO.save(notificacion);
				
				notificacion=new Notificacion();
				notificacion.setMensaje(config.getPropValues("covid19_diagnostico_positivo_instrucciones_recomendacion_limpieza_1"));
				notificacion.setPaciente(paciente);
				notificacion.setRemitente("Limpieza Domiciliaria 1");
				notificacion.setFechaNotificacion(new Date());
				notificacion.setVisto(false);
				notificacionDAO.save(notificacion);
				
				notificacion=new Notificacion();
				notificacion.setMensaje(config.getPropValues("covid19_diagnostico_positivo_instrucciones_recomendacion_cuidadores"));
				notificacion.setPaciente(paciente);
				notificacion.setRemitente("Recomendaciones para Cuidadores de Persona Aislada");
				notificacion.setFechaNotificacion(new Date());
				notificacion.setVisto(false);
				notificacionDAO.save(notificacion);
				
				notificacion=new Notificacion();
				notificacion.setMensaje(config.getPropValues("covid19_diagnostico_positivo_instrucciones_recomendacion_aislamiento_domiciliario"));
				notificacion.setPaciente(paciente);
				notificacion.setRemitente("Recomendaciones de Aislamiento Domiciliario");
				notificacion.setFechaNotificacion(new Date());
				notificacion.setVisto(false);
				notificacionDAO.save(notificacion);
				
				notificacion=new Notificacion();
				notificacion.setMensaje(config.getPropValues("covid19_diagnostico_positivo_instrucciones_recomendacion_seguimiento_estado_salud"));
				notificacion.setPaciente(paciente);
				notificacion.setRemitente("Seguimiento de su Estado de Salud");
				notificacion.setFechaNotificacion(new Date());
				notificacion.setVisto(false);
				notificacionDAO.save(notificacion);
				
				notificacion=new Notificacion();
				notificacion.setMensaje(config.getPropValues("covid19_diagnostico_positivo_instrucciones_introduccion"));
				notificacion.setPaciente(paciente);
				notificacion.setRemitente("Recomendaciones Generales");
				notificacion.setFechaNotificacion(new Date());
				notificacion.setVisto(false);
				notificacionDAO.save(notificacion);
			}
			else if("examen_laboratorio".equals(registro.getTipoRegistro())&&formSeccionDatosBasicos.getFechaPrevistaTomaMuestraLaboratorial()!=null)
			{
				ExamenLaboratorial examenLaboratorial=new ExamenLaboratorial();
				examenLaboratorial.setPaciente(paciente);
				examenLaboratorial.setUsuario(userManager.getRequestUser());
				examenLaboratorial.setFechaPrevistaTomaMuestraLaboratorial(formSeccionDatosBasicos.getFechaPrevistaTomaMuestraLaboratorial());
				examenLaboratorial.setFechaNotificacionTomaMuestraLaboratorial(new Date());
				examenLaboratorial.setEstado("agendado");
				
				examenLaboratorial.setLocalTomaMuestra(formSeccionDatosBasicos.getLocalTomaMuestra());
				
				examenLaboratorialDAO.save(examenLaboratorial);
				Notificacion notificacion=new Notificacion();
				notificacion.setMensaje(formSeccionDatosBasicos.getNombre()+", tiene examen laboratorial en fecha "+simpleDateFormat.format(formSeccionDatosBasicos.getFechaPrevistaTomaMuestraLaboratorial())+". Local: "+formSeccionDatosBasicos.getLocalTomaMuestra()+". Tiene autorizacion de circulacion para tal efecto. MSPBS.");
				notificacion.setPaciente(paciente);
				notificacion.setRemitente("Vigilancia Sanitaria-MSPBS");
				notificacion.setFechaNotificacion(new Date());
				notificacion.setVisto(false);
				notificacionDAO.save(notificacion);
				try
				{
					smsSender.sendSMSMenuMovil(formSeccionDatosBasicos.getNumeroCelular(), notificacion.getMensaje(),"laboratorial"+notificacion.getId());
				}
				catch(SmsException e)
				{
					return Response.status(400).entity("\"Se logró registrar al paciente exitosamente pero no se le pudo enviar el sms de laboratorio\"").build();
				}
			}
		}
		catch(EJBException e)
		{
			if(e.getCause().getCause().getCause().getMessage().contains("ERROR: duplicate key value violates unique constraint \"form_seccion_datos_basicos_numero_documento_idx\""))
			{
				return Response.status(400).entity("\"Número de cédula ya existente\"").build();
			}
			else
			{
				throw e;
			}
		}
		try
		{
			smsSender.sendSMSMenuMovil(formSeccionDatosBasicos.getNumeroCelular(), formSeccionDatosBasicos.getNombre()+". Por prevencion debe guardar cuarentena obligatoria. Debe reportarse aqui: https://f.mspbs.gov.py/#/i/"+registro.getId()+"/"+registro.getCodigoVerificacion() +". MSPBS.","registro"+registro.getId());
			formSeccionDatosBasicos.setNumeroCelularVerificado("enviado");
			formSeccionDatosBasicosDAO.update(formSeccionDatosBasicos);
		}
		catch(SmsException e)
		{
			return Response.status(400).entity("\"Se logró registrar al paciente exitosamente pero no se le pudo enviar el sms de registro\"").build();
		}
		return Response.ok("\""+registro.getCodigoVerificacion().substring(0, 4)+"\"").build();
	}
	
	@Path("/datosBasicosAislamiento/{idRegistro}/{codigoVerificacion}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFormSeccionDatosBasicos(@PathParam("idRegistro") Integer idRegistro, @PathParam("codigoVerificacion") String codigoVerificacion) throws Exception
	{
		FormSeccionDatosBasicos formSeccionDatosBasicos=formSeccionDatosBasicosDAO.findByRegistroId(idRegistro);
		if(formSeccionDatosBasicos==null)
		{
			return Response.status(404).build();
		}
		else
		{

			if(formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getCodigoVerificacion().equals(codigoVerificacion))
			{
				formSeccionDatosBasicos.setRegistroFormulario(null);
				return Response.ok(formSeccionDatosBasicos).build();
			}
			else
			{
				return Response.status(404).build();
			}
		}
		
	}
	
	@Path("/validarTelefono/{idRegistro}/{codigoVerificacion}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response validarTelefono(@PathParam("idRegistro") Integer idRegistro, @PathParam("codigoVerificacion") String codigoVerificacion,String contrasenha) throws Exception
	{
		FormSeccionDatosBasicos formSeccionDatosBasicos=formSeccionDatosBasicosDAO.findByRegistroId(idRegistro);
		if(formSeccionDatosBasicos==null)
		{
			return Response.status(404).build();
		}
		else
		{
			if(formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getCodigoVerificacion().equals(codigoVerificacion))
			{
				if(formSeccionDatosBasicos.getNumeroCelularVerificado().equals("no verificado")||formSeccionDatosBasicos.getNumeroCelularVerificado().equals("enviado"))
				{
					//formSeccionDatosBasicos.setNumeroCelularVerificado("verificado");
					//formSeccionDatosBasicos.setContrasenha(new String(Base64.getUrlEncoder().encode(MessageDigest.getInstance("SHA-256").digest(contrasenha.getBytes()))));
					/*formSeccionDatosBasicos.setContrasenha(usuarioBC.generarClave(contrasenha));
					formSeccionDatosBasicosDAO.update(formSeccionDatosBasicos);*/
					Usuario usuario=usuarioBC.findByCedula(formSeccionDatosBasicos.getNumeroDocumento());
					usuario.setPassword(usuarioBC.generarClave(contrasenha));
					usuarioBC.actualizarClave(usuario);
					return Response.ok().build();
				}
				else
				{
					return Response.status(400).entity("\"Disculpe, esta página está expirada. Si usted ya se registró en el sistema de seguimiento, no necesita hacerlo nuevamente.\"").build();
				}
			}
			else
			{
				return Response.status(404).build();
			}
		}
	}
	
	@Path("/datosClinicos")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response datosClinicos(FormSeccionDatosClinicos formSeccionDatosClinicos) throws Exception
	{
		if(formSeccionDatosClinicos.getEvaluacionRiesgoViveSolo()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}
		if(formSeccionDatosClinicos.getEvaluacionRiesgoTieneHabitacionPropria()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}
		if(formSeccionDatosClinicos.getEnfermedadBaseCardiopatiaCronica()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}
		if(formSeccionDatosClinicos.getEnfermedadBasePulmonarCronico()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}
		if(formSeccionDatosClinicos.getEnfermedadBaseAsma()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}
		if(formSeccionDatosClinicos.getEnfermedadBaseDiabetes()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}
		if(formSeccionDatosClinicos.getEnfermedadBaseRenalCronico()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}
		if(formSeccionDatosClinicos.getEnfermedadBaseHepaticaGrave()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}
		if(formSeccionDatosClinicos.getEvaluacionRiesgoUsomedicamento()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}
		else if(formSeccionDatosClinicos.getEvaluacionRiesgoUsomedicamento()&&(formSeccionDatosClinicos.getEvaluacionRiesgoMedicamento()==null||formSeccionDatosClinicos.getEvaluacionRiesgoMedicamento().trim().isEmpty()))
		{
			return Response.status(400).entity("\"Debe indicar los medicamentos que usa\"").build();
		}
		/*if(formSeccionDatosClinicos.getSintomasFiebre()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}
		if(formSeccionDatosClinicos.getSintomasDificultadRespirar()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}
		if(formSeccionDatosClinicos.getSintomasDiarrea()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}*/
		if(formSeccionDatosClinicos.getEnfermedadBaseHipertensionArterial()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}
		if(formSeccionDatosClinicos.getEnfermedadBaseAutoinmune()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}
		if(formSeccionDatosClinicos.getEnfermedadBaseNeoplasias()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}
		/*if(formSeccionDatosClinicos.getEnfermedadBaseEPOC()==null)
		{
			return Response.status(400).entity("\"Debe completar todos los campos\"").build();
		}*/
		FormSeccionDatosBasicos formSeccionDatosBasicos=formSeccionDatosBasicosDAO.findByRegistroId(formSeccionDatosClinicos.getIdRegistro());
		if(formSeccionDatosBasicos==null)
		{
			return Response.status(404).build();
		}
		else
		{
			if(formSeccionDatosBasicos.getNumeroCelularVerificado().equals("no verificado")||formSeccionDatosBasicos.getNumeroCelularVerificado().equals("enviado"))
			{
				formSeccionDatosBasicos.getRegistroFormulario().setEstado("completo");
				registroFormularioDAO.update(formSeccionDatosBasicos.getRegistroFormulario());
				formSeccionDatosBasicos.setNumeroCelularVerificado("verificado");
				formSeccionDatosBasicosDAO.update(formSeccionDatosBasicos);
				formSeccionDatosClinicos.setRegistroFormulario(formSeccionDatosBasicos.getRegistroFormulario());
				formSeccionDatosClinicos.setDeclaracionAgreement(true);
				formSeccionDatosClinicosDAO.save(formSeccionDatosClinicos);
				PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos=pacienteBC.getPacienteByNumeroDocumento(formSeccionDatosBasicos.getNumeroDocumento());
				pacienteDatosPersonalesBasicos.setNumeroCelularVerificado("verificado");
				pacienteDatosPersonalesBasicosDAO.update(pacienteDatosPersonalesBasicos);
				pacienteBC.createHistoricoClinicoFromFormSeccionDatosClinicos(formSeccionDatosClinicos, pacienteDatosPersonalesBasicos.getPaciente(), pacienteDatosPersonalesBasicos.getPaciente().getUsuario());
				LoginAutomatico loginAutomatico=pacienteBC.crearLoginAutomatico(pacienteDatosPersonalesBasicos.getPaciente());
				try {
					smsSender.sendSMSMenuMovil(formSeccionDatosBasicos.getNumeroCelular(), formSeccionDatosBasicos.getNombre()+", se registro a la App COVID19 con exito. Entre en https://a.mspbs.gov.py/. El sistema le pedira su clave de seguridad. MSPBS.","paciente"+pacienteDatosPersonalesBasicos.getPaciente().getId());
				}
				catch(SmsException e)
				{
					System.out.println("No se pudo enviar el mensaje final al paciente con documento"+formSeccionDatosBasicos.getNumeroDocumento());
				}
				return Response.ok("\""+loginAutomatico.getToken()+"\"").build();

			}
			else
			{
				return Response.status(400).build();
			}
		}
	}
	
	@Path("/drools")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFormSeccionDatosBasicos() throws Exception
	{
		List result=new ArrayList();
		ReporteSalud reporteSalud=new ReporteSalud();
		reporteSalud.setCongestionNasal("si");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setSecrecionNasal("si");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setDolorGarganta("si");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setTos("si");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setTomasteTemperatura("mas38");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setTomasteTemperatura("menos38");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setFiebreAyer("si");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setCongestionNasal("si");
		reporteSalud.setTomasteTemperatura("mas38");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setCongestionNasal("si");
		reporteSalud.setSecrecionNasal("si");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setSentisTristeDesanimado("4");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setSentisTristeDesanimado("5");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setSentisTristeDesanimado("1");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setSentisTristeDesanimado("3");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setSentisTristeDesanimado("2");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setSentisAngustia("4");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setSentisAngustia("5");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setSentisAngustia("1");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setSentisAngustia("3");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setSentisAngustia("2");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		reporteSalud.setComoTeSentis("bien");
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		reporteSalud=new ReporteSalud();
		Drools.clasificarPaciente(reporteSalud);
		result.add(reporteSalud);
		return Response.ok(result).build();
		
	}
	
	@Secured
	@Path("/obtenerPaciente/{numeroDocumento}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolAllowed({"Operador","Tecnico Salud"})
	public Response getPaciente(@PathParam("numeroDocumento") String numeroDocumento) throws Exception
	{
		FormSeccionDatosBasicos formSeccionDatosBasicos=formSeccionDatosBasicosDAO.findByNumeroDocumento(numeroDocumento);
		if(formSeccionDatosBasicos!=null)
		{
			HashMap<String, Object> result=new HashMap<>();

			SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
			result.put("nombre", formSeccionDatosBasicos.getNombre()+" "+formSeccionDatosBasicos.getApellido());
			result.put("fechaRegistro", simpleDateFormat.format(formSeccionDatosBasicos.getRegistroFormulario().getFechaCreacion()));
			if("0".equals(formSeccionDatosBasicos.getTipoDocumento()))
			{
				result.put("tipoDocumento", "Cédula");
			}
			else if("1".equals(formSeccionDatosBasicos.getTipoDocumento()))
			{
				result.put("tipoDocumento", "Pasaporte");
			}
			if(formSeccionDatosBasicos.getInicioAislamiento()!=null)
			{
				result.put("inicioAislamiento", simpleDateFormat.format(formSeccionDatosBasicos.getInicioAislamiento()));
			}

			PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos=pacienteBC.getPacienteByNumeroDocumento(formSeccionDatosBasicos.getNumeroDocumento());
			if(pacienteDatosPersonalesBasicos!=null)
			{
				result.put("nombre", pacienteDatosPersonalesBasicos.getNombre()+" "+pacienteDatosPersonalesBasicos.getApellido());
				result.put("id", pacienteDatosPersonalesBasicos.getId());
				//if(pacienteDatosPersonalesBasicos.getPaciente().getTieneSintomas()!=null){
				result.put("tieneSintomas", pacienteDatosPersonalesBasicos.getPaciente().getTieneSintomas());
				//}
				result.put("numeroCelularVerificado", pacienteDatosPersonalesBasicos.getNumeroCelularVerificado());
				
				ExamenLaboratorial examenLaboratorial=examenLaboratorialDAO.getMaxDateByPaciente(pacienteDatosPersonalesBasicos.getPaciente().getId());
				
				if(examenLaboratorial != null) {
					result.put("fechaPrevistaTomaMuestraLaboratorial", simpleDateFormat.format(examenLaboratorial.getFechaPrevistaTomaMuestraLaboratorial()));
					result.put("localTomaMuestra", examenLaboratorial.getLocalTomaMuestra());
				}
				
				if(pacienteDatosPersonalesBasicos.getPaciente().getResultadoUltimoDiagnostico()!=null)
				{
					result.put("resultadoUltimoDiagnostico", pacienteDatosPersonalesBasicos.getPaciente().getResultadoUltimoDiagnostico());
				}
				else
				{
					result.put("resultadoUltimoDiagnostico", "sospechoso");
				}
				
				if(pacienteDatosPersonalesBasicos.getPaciente().getFechaUltimoDiagnostico()!=null)
				{
					result.put("fechaUltimoDiagnostico", simpleDateFormat.format(pacienteDatosPersonalesBasicos.getPaciente().getFechaUltimoDiagnostico()));
				}
				else if(pacienteDatosPersonalesBasicos.getPaciente().getInicioAislamiento()!=null)
				{
					result.put("fechaUltimoDiagnostico", simpleDateFormat.format(pacienteDatosPersonalesBasicos.getPaciente().getInicioAislamiento()));
				}
				else
				{
					result.put("fechaUltimoDiagnostico", simpleDateFormat.format(new Date()));
				}
				
				if(pacienteDatosPersonalesBasicos.getPaciente().getFinPrevistoAislamiento()!=null)
				{
					result.put("fechaPrevistaFinAislamiento", simpleDateFormat.format(pacienteDatosPersonalesBasicos.getPaciente().getFinPrevistoAislamiento()));
				}
				
				ReporteSalud reporteSalud=pacienteBC.getLastReporteSalud(pacienteDatosPersonalesBasicos.getPaciente().getId());
				if(reporteSalud!=null)
				{
					result.put("fechaUltimoReporteSalud", simpleDateFormat.format(reporteSalud.getTimestampCreacion()));
				}
				RegistroUbicacion registroUbicacion=pacienteBC.getLastRegistro(pacienteDatosPersonalesBasicos.getPaciente());
				if(registroUbicacion!=null)
				{
					result.put("fechaUltimaUbicacion", simpleDateFormat.format(registroUbicacion.getTimestampCreacion()));
				}
				if(pacienteDatosPersonalesBasicos.getPaciente().getFechaInicioSintoma() != null)
				{
					result.put("fechaInicioSintoma", simpleDateFormat.format(pacienteDatosPersonalesBasicos.getPaciente().getFechaInicioSintoma()));
				}
				if(pacienteDatosPersonalesBasicos.getPaciente().getFechaExposicion() != null)
				{
					result.put("fechaExposicion", simpleDateFormat.format(pacienteDatosPersonalesBasicos.getPaciente().getFechaExposicion()));
				}
			}
			return Response.ok(result).build();
		}
		else
		{
			return Response.status(404).build();
		}
	}

}
