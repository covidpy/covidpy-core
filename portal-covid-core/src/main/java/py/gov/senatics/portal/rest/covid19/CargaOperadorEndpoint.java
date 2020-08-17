package py.gov.senatics.portal.rest.covid19;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
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
import py.gov.senatics.portal.business.covid19.IngresoPaisBC;
import py.gov.senatics.portal.business.covid19.PacienteBC;
import py.gov.senatics.portal.modelCovid19.ExamenLaboratorial;
import py.gov.senatics.portal.modelCovid19.FormSeccionDatosBasicos;
import py.gov.senatics.portal.modelCovid19.FormSeccionDatosClinicos;
import py.gov.senatics.portal.modelCovid19.HistoricoDiagnostico;
import py.gov.senatics.portal.modelCovid19.Notificacion;
import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.modelCovid19.PacienteDatosPersonalesBasicos;
import py.gov.senatics.portal.modelCovid19.Registro;
import py.gov.senatics.portal.modelCovid19.RegistroFormulario;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;
import py.gov.senatics.portal.persistence.covid19.ExamenLaboratorialDAO;
import py.gov.senatics.portal.persistence.covid19.FormSeccionDatosBasicosDAO;
import py.gov.senatics.portal.persistence.covid19.FormSeccionDatosClinicosDAO;
import py.gov.senatics.portal.persistence.covid19.HistoricoDiagnosticoDAO;
import py.gov.senatics.portal.persistence.covid19.NotificacionDAO;
import py.gov.senatics.portal.persistence.covid19.PacienteDatosPersonalesBasicosDAO;
import py.gov.senatics.portal.persistence.covid19.RegistroDAO;
import py.gov.senatics.portal.persistence.covid19.RegistroFormularioDAO;
import py.gov.senatics.portal.session.UserManager;
import py.gov.senatics.portal.util.Config;
import py.gov.senatics.portal.util.SmsException;
import py.gov.senatics.portal.util.SmsSender;


/**
 * @author
 *
 */
@Path("/covid19api/cargaOperador")
@RequestScoped
public class CargaOperadorEndpoint {
	@Inject
	private RegistroDAO registroDAO;
	
	@Inject
	private RegistroFormularioDAO registroFormularioDAO;
	
	@Inject
	private FormSeccionDatosBasicosDAO formSeccionDatosBasicosDAO;
	
	@Inject
	private SmsSender smsSender;
	
	@Inject
	private IngresoPaisBC ingresoPaisBC;
	
	@Inject
	private PacienteBC pacienteBC;
	
	@Inject
	private UsuarioBC usuarioBC;
	
	@Inject
	private FormSeccionDatosClinicosDAO formSeccionDatosClinicosDAO;
	
	@Inject
	private Config config;
	
	@Inject
	private NotificacionDAO notificacionDAO;
	
	@Inject
	private HistoricoDiagnosticoDAO historicoDiagnosticoDAO;
	
	@Inject
	private UserManager userManager;
	
	@Inject
	private ExamenLaboratorialDAO examenLaboratorialDAO;
	
	@Inject
	private PacienteDatosPersonalesBasicosDAO pacienteDatosPersonalesBasicosDAO;

	@Path("/datosBasicos")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response datosBasicos(FormSeccionDatosBasicos formSeccionDatosBasicos) throws Exception
	{
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
		registro.setResponsableRegistro("operador");
		registro.setCodigoVerificacion(UUID.randomUUID().toString().substring(0, 8));
		registro.setEstado("registro");
		registro.setTipoRegistro(formSeccionDatosBasicos.getTipoRegistro());
		registroDAO.save(registro);
		RegistroFormulario registroFormulario=new RegistroFormulario();
		registroFormulario.setRegistro(registro);
		registroFormulario.setFechaCreacion(new Date());
		registroFormulario.setNombre("operador");
		registroFormulario.setEstado("completo");
		registroFormulario.setRegistroFormularioAcompanante(false);
		registroFormularioDAO.save(registroFormulario);
		formSeccionDatosBasicos.setRegistroFormulario(registroFormulario);
		formSeccionDatosBasicos.setNumeroCelularVerificado("no verificado");
		//formSeccionDatosBasicos.setContrasenha(new String(Base64.getUrlEncoder().encode(MessageDigest.getInstance("SHA-256").digest(registro.getCodigoVerificacion().getBytes()))));
		formSeccionDatosBasicosDAO.save(formSeccionDatosBasicos);
			
		//smsSender.sendSms(formSeccionDatosBasicos.getNumeroCelular(), "+12058094778", formSeccionDatosBasicos.getNombre()+" "+formSeccionDatosBasicos.getApellido()+", como medida de prevención usted debe realizar aislamiento domiciliario. Para tal efecto, entre en http://form.coronavirus.gov.py/#/i/"+registro.getId()+"/"+registro.getCodigoVerificacion() +". Vigilancia Sanitaria MSPBS. Código Confirmación:"+registro.getCodigoVerificacion().substring(0,4));
		return Response.ok("\""+registro.getId()+"\"").build();
		
	}
	
	@Path("/datosClinicos")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response datosClinicos(FormSeccionDatosClinicos formSeccionDatosClinicos) throws Exception
	{

		FormSeccionDatosBasicos formSeccionDatosBasicos=formSeccionDatosBasicosDAO.findByRegistroId(formSeccionDatosClinicos.getIdRegistro());
		formSeccionDatosClinicos.setRegistroFormulario(formSeccionDatosBasicos.getRegistroFormulario());
		
		formSeccionDatosClinicos.setDeclaracionAgreement(true);

		formSeccionDatosClinicosDAO.save(formSeccionDatosClinicos);
		Paciente paciente=pacienteBC.createPacienteFromRegistro(formSeccionDatosBasicos.getRegistroFormulario().getRegistro());
		//smsSender.sendSms(formSeccionDatosBasicos.getNumeroCelular(), "+12058094778", formSeccionDatosBasicos.getNombre()+" "+formSeccionDatosBasicos.getApellido()+
		//", usted ha sido registrado en el Sistema de Acompañamiento con éxito. Para acceder: https://appcoronavirus.mspbs.gov.py/. El sistema le pedirá su clave de seguridad. Vigilancia Sanitaria MSPBS. Utilice esta contraseña provisoria: "+formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getCodigoVerificacion());
		smsSender.sendSMSMenuMovil(formSeccionDatosBasicos.getNumeroCelular(), formSeccionDatosBasicos.getNombre()+
		", se registró a la App COVID19 con éxito. Primero debe crear una clave de seguridad aquí: http://localhost:4200/#/covid19/carga-operador/clave-seguridad/"+formSeccionDatosClinicos.getIdRegistro(),"cargaoperador"+paciente.getId());
		
		return Response.ok("\""+paciente.getToken()+"\"").build();
	}
	
	@Path("/claveSeguridad/{idRegistro}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response setearClave(@PathParam("idRegistro") Integer idRegistro, String clave) throws Exception{
		FormSeccionDatosBasicos formSeccionDatosBasicos=formSeccionDatosBasicosDAO.findByRegistroId(idRegistro);
		//formSeccionDatosBasicos.setContrasenha(new String(Base64.getUrlEncoder().encode(MessageDigest.getInstance("SHA-256").digest(clave.getBytes()))));
		formSeccionDatosBasicos.setContrasenha(usuarioBC.generarClave(clave));
		
		
		smsSender.sendSMSMenuMovil(formSeccionDatosBasicos.getNumeroCelular(), formSeccionDatosBasicos.getNombre()+
		", su clave de seguridad ha sido creada con éxito. Para acceder: https://a.mspbs.gov.py/. El sistema le pedirá su clave de seguridad. Vigilancia Sanitaria MSPBS.","cargaoperador"+idRegistro);
		
		return Response.ok().build();
		
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
					smsSender.sendSMSMenuMovil(formSeccionDatosBasicos.getNumeroCelular(), formSeccionDatosBasicos.getNombre()+", se registró a la App COVID19 con éxito. Para acceder: https://a.mspbs.gov.py/. El sistema le pedirá su clave de seguridad. Vigilancia Sanitaria MSPBS.","cargaoperador");
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
	
	@Path("/paciente/crearExamenLaboratorial")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@RolAllowed("Operador")
	public Response crearExamenLaboratorial(Map<String,String> examenLab) throws Exception
	{
		//if(actualizarDiagnostico.get("fechaPrevistaTomaMuestraLaboratorial") != null && actualizarDiagnostico.get("localTomaMuestra") != null) {
			PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos=pacienteBC.getPacienteByNumeroDocumento(examenLab.get("numeroDocumento"));
			ExamenLaboratorial examenLaboratorial=new ExamenLaboratorial();
			SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
			examenLaboratorial.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
			examenLaboratorial.setUsuario(userManager.getRequestUser());
			
			examenLaboratorial.setFechaPrevistaTomaMuestraLaboratorial(simpleDateFormat.parse(examenLab.get("fechaPrevistaTomaMuestraLaboratorial")));
			
			examenLaboratorial.setLocalTomaMuestra(examenLab.get("localTomaMuestra"));
			
			examenLaboratorial.setFechaNotificacionTomaMuestraLaboratorial(new Date());
			examenLaboratorial.setEstado("agendado");
			examenLaboratorialDAO.save(examenLaboratorial);
			
			Notificacion notificacion=new Notificacion();
			notificacion.setMensaje(pacienteDatosPersonalesBasicos.getNombre()+", tiene examen laboratorial en fecha "+examenLab.get("fechaPrevistaTomaMuestraLaboratorial")+". Local: "+examenLab.get("localTomaMuestra")+". Tiene autorizacion de circulacion para tal efecto. MSPBS.");
			notificacion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
			notificacion.setRemitente("Vigilancia Sanitaria-MSPBS");
			notificacion.setFechaNotificacion(new Date());
			notificacion.setVisto(false);
			notificacionDAO.save(notificacion);
			try
			{
				smsSender.sendSMSMenuMovil(pacienteDatosPersonalesBasicos.getNumeroCelular(), notificacion.getMensaje(),"laboratorial"+notificacion.getId());
			}
			catch(SmsException e)
			{
				return Response.status(400).entity("\"Se logró registrar el examen laboratorial del paciente exitosamente pero no se le pudo enviar el sms de laboratorio\"").build();
			}
			return Response.ok().build();		
		//}
	}
	
	@Path("/paciente/actualizarDiagnostico")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@RolAllowed("Operador")
	public Response actualizarDiagnosticoPaciente(Map<String,String> actualizarDiagnostico) throws Exception
	{
		PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos=pacienteBC.getPacienteByNumeroDocumento(actualizarDiagnostico.get("numeroDocumento"));
		if(pacienteDatosPersonalesBasicos!=null)
		{
			pacienteDatosPersonalesBasicos.getPaciente().setResultadoUltimoDiagnostico((String) actualizarDiagnostico.get("resultadoUltimoDiagnostico"));
			SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
			pacienteDatosPersonalesBasicos.getPaciente().setFechaUltimoDiagnostico(simpleDateFormat.parse(actualizarDiagnostico.get("fechaUltimoDiagnostico")));
			if(actualizarDiagnostico.get("fechaPrevistaFinAislamiento")==null)
			{
				pacienteDatosPersonalesBasicos.getPaciente().setFinPrevistoAislamiento(null);
			}
			else
			{
				pacienteDatosPersonalesBasicos.getPaciente().setFinPrevistoAislamiento(simpleDateFormat.parse(actualizarDiagnostico.get("fechaPrevistaFinAislamiento")));
			}
			
			pacienteDatosPersonalesBasicos.getPaciente().setTieneSintomas(actualizarDiagnostico.get("tieneSintomas"));
			
			if(pacienteDatosPersonalesBasicos.getPaciente().getTieneSintomas() != null) {
				if(pacienteDatosPersonalesBasicos.getPaciente().getTieneSintomas().equals("Si")) {
					pacienteDatosPersonalesBasicos.getPaciente().setFechaInicioSintoma(actualizarDiagnostico.get("fechaInicioSintoma") != null ?
							simpleDateFormat.parse(actualizarDiagnostico.get("fechaInicioSintoma")) : null);
				} else {
					pacienteDatosPersonalesBasicos.getPaciente().setFechaInicioSintoma(null);
				}
			}
			
			pacienteDatosPersonalesBasicos.getPaciente().setFechaExposicion(actualizarDiagnostico.get("fechaExposicion") != null ?
					simpleDateFormat.parse(actualizarDiagnostico.get("fechaExposicion")) : null);
			
			pacienteBC.updatePaciente(pacienteDatosPersonalesBasicos.getPaciente());
			HistoricoDiagnostico historicoDiagnostico=new HistoricoDiagnostico();
			historicoDiagnostico.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
			historicoDiagnostico.setResultadoDiagnostico(pacienteDatosPersonalesBasicos.getPaciente().getResultadoUltimoDiagnostico());
			historicoDiagnostico.setFechaDiagnostico(pacienteDatosPersonalesBasicos.getPaciente().getFechaUltimoDiagnostico());
			historicoDiagnostico.setFinPrevistoAislamiento(pacienteDatosPersonalesBasicos.getPaciente().getFinPrevistoAislamiento());
			historicoDiagnostico.setUsuario(userManager.getRequestUser());
			historicoDiagnostico.setFechaModificacion(new Date());
			historicoDiagnosticoDAO.save(historicoDiagnostico);
			
			/*ExamenLaboratorial examenLaboratorialBD = examenLaboratorialDAO.getMaxDateByPaciente(pacienteDatosPersonalesBasicos.getPaciente().getId());
			if(examenLaboratorialBD != null) {
				if(actualizarDiagnostico.get("fechaPrevistaTomaMuestraLaboratorial") != null) {
					examenLaboratorialBD.setFechaPrevistaTomaMuestraLaboratorial(simpleDateFormat.parse(actualizarDiagnostico.get("fechaPrevistaTomaMuestraLaboratorial")));
				}
				if(actualizarDiagnostico.get("localTomaMuestra") != null) {
					examenLaboratorialBD.setLocalTomaMuestra(actualizarDiagnostico.get("localTomaMuestra"));
				}
				examenLaboratorialDAO.update(examenLaboratorialBD);
			}*/
			
			/*if(actualizarDiagnostico.get("fechaPrevistaTomaMuestraLaboratorial") != null && actualizarDiagnostico.get("localTomaMuestra") != null) {
				ExamenLaboratorial examenLaboratorial=new ExamenLaboratorial();
				examenLaboratorial.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
				examenLaboratorial.setUsuario(userManager.getRequestUser());
				
				examenLaboratorial.setFechaPrevistaTomaMuestraLaboratorial(simpleDateFormat.parse(actualizarDiagnostico.get("fechaPrevistaTomaMuestraLaboratorial")));
				
				examenLaboratorial.setLocalTomaMuestra(actualizarDiagnostico.get("localTomaMuestra"));
				
				examenLaboratorial.setFechaNotificacionTomaMuestraLaboratorial(new Date());
				examenLaboratorial.setEstado("agendado");
				examenLaboratorialDAO.save(examenLaboratorial);
				
				Notificacion notificacion=new Notificacion();
				notificacion.setMensaje("Estimado/a "+pacienteDatosPersonalesBasicos.getNombre()+", conforme indicaciones recibidas tiene examen laboratorial en fecha "+actualizarDiagnostico.get("fechaPrevistaTomaMuestraLaboratorial")+". Local: "+actualizarDiagnostico.get("localTomaMuestra")+". Tiene autorización de circulación para tal efecto. MSPBS.");
				notificacion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
				notificacion.setRemitente("Vigilancia Sanitaria-MSPBS");
				notificacion.setFechaNotificacion(new Date());
				notificacion.setVisto(false);
				notificacionDAO.save(notificacion);
				try
				{
					smsSender.sendSMSMenuMovil(pacienteDatosPersonalesBasicos.getNumeroCelular(), notificacion.getMensaje(),"laboratorial"+notificacion.getId());
				}
				catch(SmsException e)
				{
					return Response.status(400).entity("\"Se logró actualizar el diagnóstico del paciente exitosamente pero no se le pudo enviar el sms de laboratorio\"").build();
				}
			}*/
			
			if("positivo".equals(pacienteDatosPersonalesBasicos.getPaciente().getResultadoUltimoDiagnostico()))
			{
				Notificacion notificacion=new Notificacion();
				notificacion.setMensaje(config.getPropValues("covid19_diagnostico_positivo_instrucciones_recomendacion_limpieza_2"));
				notificacion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
				notificacion.setRemitente("Limpieza Domiciliaria 2");
				notificacion.setFechaNotificacion(new Date());
				notificacion.setVisto(false);
				notificacionDAO.save(notificacion);
				
				notificacion=new Notificacion();
				notificacion.setMensaje(config.getPropValues("covid19_diagnostico_positivo_instrucciones_recomendacion_limpieza_1"));
				notificacion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
				notificacion.setRemitente("Limpieza Domiciliaria 1");
				notificacion.setFechaNotificacion(new Date());
				notificacion.setVisto(false);
				notificacionDAO.save(notificacion);
				
				notificacion=new Notificacion();
				notificacion.setMensaje(config.getPropValues("covid19_diagnostico_positivo_instrucciones_recomendacion_cuidadores"));
				notificacion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
				notificacion.setRemitente("Recomendaciones para Cuidadores de Persona Aislada");
				notificacion.setFechaNotificacion(new Date());
				notificacion.setVisto(false);
				notificacionDAO.save(notificacion);
				
				notificacion=new Notificacion();
				notificacion.setMensaje(config.getPropValues("covid19_diagnostico_positivo_instrucciones_recomendacion_aislamiento_domiciliario"));
				notificacion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
				notificacion.setRemitente("Recomendaciones de Aislamiento Domiciliario");
				notificacion.setFechaNotificacion(new Date());
				notificacion.setVisto(false);
				notificacionDAO.save(notificacion);
				
				notificacion=new Notificacion();
				notificacion.setMensaje(config.getPropValues("covid19_diagnostico_positivo_instrucciones_recomendacion_seguimiento_estado_salud"));
				notificacion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
				notificacion.setRemitente("Seguimiento de su Estado de Salud");
				notificacion.setFechaNotificacion(new Date());
				notificacion.setVisto(false);
				notificacionDAO.save(notificacion);
				
				notificacion=new Notificacion();
				notificacion.setMensaje(config.getPropValues("covid19_diagnostico_positivo_instrucciones_introduccion"));
				notificacion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
				notificacion.setRemitente("Recomendaciones Generales");
				notificacion.setFechaNotificacion(new Date());
				notificacion.setVisto(false);
				notificacionDAO.save(notificacion);
			}
			return Response.ok().build();

		}
		else
		{
			return Response.status(404).build();
		}
		
	}
	
	@Path("/paciente/reenviarSms")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@RolAllowed("Operador")
	public Response reenviarSms(String numeroDocumento) throws Exception
	{
		PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos=pacienteBC.getPacienteByNumeroDocumento(numeroDocumento);
		
		if(pacienteDatosPersonalesBasicos != null) {
			Registro registro = pacienteDatosPersonalesBasicos.getFormSeccionDatosBasicos().getRegistroFormulario().getRegistro();
			try
			{
				smsSender.sendSMSMenuMovil(pacienteDatosPersonalesBasicos.getNumeroCelular(), pacienteDatosPersonalesBasicos.getNombre()+". Por prevencion debe guardar cuarentena obligatoria. Debe reportarse aqui: https://f.mspbs.gov.py/#/i/"+registro.getId()+"/"+registro.getCodigoVerificacion() +". MSPBS.","reenviarsms"+registro.getId());
			}
			catch(SmsException e)
			{
				return Response.status(400).entity("\"No se le pudo re-enviar el sms al paciente\"").build();
			}
			return Response.ok().build();
		}else{
			return Response.status(404).build();
		}
		
	}
	
	@Path("/paciente/cambiarNroCelular")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@RolAllowed("Operador")
	public Response cambiarNroCelular(Map<String,String> datosPaciente) throws Exception{
		String  numeroCelular = datosPaciente.get("numeroCelular");
		
		if(numeroCelular==null||numeroCelular.trim().isEmpty()){
			return Response.status(400).entity("\"El número de celular es requerido\"").build();
		}
		else if(numeroCelular.matches("09[9876]\\d{7}")){
			numeroCelular = "595"+numeroCelular.substring(1);
		}
		else if(numeroCelular.matches("\\+5959[9876]\\d{7}")){
			numeroCelular = numeroCelular.substring(1);
		}
		if(!numeroCelular.matches("5959[9876]\\d{7}")){
			return Response.status(400).entity("\"Verifique el número de celular\"").build();
		}
		
		PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos=pacienteBC.getPacienteByNumeroDocumento(datosPaciente.get("numeroDocumento"));
		if(pacienteDatosPersonalesBasicos != null) {
			pacienteDatosPersonalesBasicos.setNumeroCelular(datosPaciente.get("numeroCelular"));
			pacienteDatosPersonalesBasicosDAO.update(pacienteDatosPersonalesBasicos);
			if(datosPaciente.get("numeroCelularVerificado").equalsIgnoreCase("no verificado")) {
				Registro registro = pacienteDatosPersonalesBasicos.getFormSeccionDatosBasicos().getRegistroFormulario().getRegistro();
				try
				{
					smsSender.sendSMSMenuMovil(pacienteDatosPersonalesBasicos.getNumeroCelular(), pacienteDatosPersonalesBasicos.getNombre()+". Por prevencion debe guardar cuarentena obligatoria. Debe reportarse aqui: https://f.mspbs.gov.py/#/i/"+registro.getId()+"/"+registro.getCodigoVerificacion() +". MSPBS.","cambiarsms"+registro.getId());
				}
				catch(SmsException e)
				{
					return Response.status(400).entity("\"No se le pudo enviar el sms al paciente\"").build();
				}
			}
			
			return Response.ok().build();
		}else {
			return Response.status(404).build();
		}
		
	}

}
