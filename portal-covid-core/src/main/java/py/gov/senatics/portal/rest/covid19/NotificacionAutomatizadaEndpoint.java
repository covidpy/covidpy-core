package py.gov.senatics.portal.rest.covid19;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import py.gov.senatics.portal.modelCovid19.DiagnosticoAccion;
import py.gov.senatics.portal.modelCovid19.FormSeccionDatosBasicos;
import py.gov.senatics.portal.modelCovid19.Notificacion;
import py.gov.senatics.portal.modelCovid19.PacienteDatosPersonalesBasicos;
import py.gov.senatics.portal.persistence.covid19.DiagnosticoAccionDAO;
import py.gov.senatics.portal.persistence.covid19.FormSeccionDatosBasicosDAO;
import py.gov.senatics.portal.persistence.covid19.NotificacionDAO;
import py.gov.senatics.portal.persistence.covid19.PacienteDatosPersonalesBasicosDAO;
import py.gov.senatics.portal.util.SmsException;
import py.gov.senatics.portal.util.SmsSender;

@Path("/covid19/notificacionAutomatizada")
@RequestScoped
public class NotificacionAutomatizadaEndpoint {
	@Inject
	private PacienteDatosPersonalesBasicosDAO pacienteDatosPersonalesBasicosDAO;
	
	@Inject
	private FormSeccionDatosBasicosDAO formSeccionDatosBasicosDAO;
	
	@Inject
	private DiagnosticoAccionDAO diagnosticoAccionDAO;
	
	@Inject
	private SmsSender smsSender;
	
	@Inject
	private NotificacionDAO notificacionDAO;
	
	private static final String semaforoPacientesSinRegistroSospechoso="semaforo";
	private static final String semaforoPacientesSinRegistroConfirmado12="semaforo";
	private static final String semaforoPacientesSinRegistroConfirmado16="semaforo";
	private static final String semaforoPacientesSinRegistroUbicacionSospechoso="semaforo";
	private static final String semaforoPacientesSinRegistroUbicacionConfirmado3="semaforo";
	private static final String semaforoPacientesSinRegistroUbicacionConfirmado6="semaforo";
	private static final String semaforoPacientesSinReporteSalud="semaforo";

	@Path("/pacientesSinRegistroSospechoso")
	@GET
	@Produces("application/json")
	public Response getPacientesSinRegistroSospechoso(@QueryParam("token") String token,@QueryParam("modoProduccionSMS") String modoProduccionSMS)
	{
		if(!"miticPacientesSinRegistroSospechosoProd".equals(token))
		{
			return Response.ok().build();
		}
		synchronized(semaforoPacientesSinRegistroSospechoso)
		{
		List<PacienteDatosPersonalesBasicos> resultPacienteDatosPersonalesBasicos=pacienteDatosPersonalesBasicosDAO.getPacientesSinRegistroSospechoso();
		for(PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos:resultPacienteDatosPersonalesBasicos)
		{
			pacienteDatosPersonalesBasicos.getPaciente().setUsuario(null);
			DiagnosticoAccion diagnosticoAccion=new DiagnosticoAccion();
			diagnosticoAccion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
			diagnosticoAccion.setTipoAccion("notificacion_registro_sospechoso");
			diagnosticoAccion.setEstadoEjecucion("ejecutado");
			diagnosticoAccion.setFechaHoraEjecucion(new Date());
			diagnosticoAccion.setResultadoEjecucion("exitoso");
			FormSeccionDatosBasicos formSeccionDatosBasicos=formSeccionDatosBasicosDAO.findByNumeroDocumento(pacienteDatosPersonalesBasicos.getNumeroDocumento());
			if("examen_laboratorio".equals(formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getTipoRegistro()))
			{
				diagnosticoAccion.setValor(formSeccionDatosBasicos.getNombre()+", para mejor seguimiento de su examen laboratorial, debe registrarse en https://f.mspbs.gov.py/#/i/"+formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getId()+"/"+formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getCodigoVerificacion()+". MSPBS.");
			}
			else
			{
				diagnosticoAccion.setValor(formSeccionDatosBasicos.getNombre()+", debe reportarse por su cuarentena obligatoria. Acceda a: https://f.mspbs.gov.py/#/i/"+formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getId()+"/"+formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getCodigoVerificacion()+". MSPBS.");
			}
			if("true".equals(modoProduccionSMS))
			{
				try
				{
					smsSender.sendSMSMenuMovil(pacienteDatosPersonalesBasicos.getNumeroCelular(), diagnosticoAccion.getValor(),"pacientesinregistrosospechoso"+pacienteDatosPersonalesBasicos.getPaciente().getId());
					diagnosticoAccionDAO.save(diagnosticoAccion);
				}
				catch(SmsException e)
				{
					System.out.println("pacientesSinRegistroSospechoso-"+pacienteDatosPersonalesBasicos.getNumeroDocumento()+"|"+pacienteDatosPersonalesBasicos.getNumeroCelular()+"|"+e.getMessage());
				}
			}
			else
			{
				diagnosticoAccionDAO.save(diagnosticoAccion);
			}
		}
		/*List<FormSeccionDatosBasicos> resultFormSeccionDatosBasicos=formSeccionDatosBasicosDAO.getPacientesSinRegistroSospechoso();
		for(FormSeccionDatosBasicos formSeccionDatosBasicos:resultFormSeccionDatosBasicos)
		{
			formSeccionDatosBasicos.getRegistroFormulario().getRegistro().setUsuario(null);;
		}
		List result=new ArrayList();
		result.addAll(resultFormSeccionDatosBasicos);
		result.addAll(resultPacienteDatosPersonalesBasicos);*/
		return Response.ok(resultPacienteDatosPersonalesBasicos).build();
		}
	}
	
	@Path("/pacientesSinRegistroConfirmado12")
	@GET
	@Produces("application/json")
	public Response getPacientesSinRegistroConfirmado12(@QueryParam("token") String token,@QueryParam("modoProduccionSMS") String modoProduccionSMS)
	{
		if(!"miticPacientesSinRegistroConfirmado12Prod".equals(token))
		{
			return Response.ok().build();
		}
		synchronized(semaforoPacientesSinRegistroConfirmado12)
		{
		List<PacienteDatosPersonalesBasicos> resultPacienteDatosPersonalesBasicos=pacienteDatosPersonalesBasicosDAO.getPacientesSinRegistroConfirmado12();
		for(PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos:resultPacienteDatosPersonalesBasicos)
		{
			pacienteDatosPersonalesBasicos.getPaciente().setUsuario(null);
			DiagnosticoAccion diagnosticoAccion=new DiagnosticoAccion();
			diagnosticoAccion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
			diagnosticoAccion.setTipoAccion("notificacion_registro_confirmado_12");
			diagnosticoAccion.setEstadoEjecucion("ejecutado");
			diagnosticoAccion.setFechaHoraEjecucion(new Date());
			diagnosticoAccion.setResultadoEjecucion("exitoso");
			FormSeccionDatosBasicos formSeccionDatosBasicos=formSeccionDatosBasicosDAO.findByNumeroDocumento(pacienteDatosPersonalesBasicos.getNumeroDocumento());
			diagnosticoAccion.setValor(formSeccionDatosBasicos.getNombre()+", debe reportarse por su cuarentena obligatoria. Acceda a: https://f.mspbs.gov.py/#/i/"+formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getId()+"/"+formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getCodigoVerificacion()+". MSPBS.");
			if("true".equals(modoProduccionSMS))
			{
				try
				{
					smsSender.sendSMSMenuMovil(pacienteDatosPersonalesBasicos.getNumeroCelular(), diagnosticoAccion.getValor(),"pacientesinregistroconfirmado12"+pacienteDatosPersonalesBasicos.getPaciente().getId());
					diagnosticoAccionDAO.save(diagnosticoAccion);
				}
				catch(SmsException e)
				{
					System.out.println("pacientesSinRegistroConfirmado12-"+pacienteDatosPersonalesBasicos.getNumeroDocumento()+"|"+pacienteDatosPersonalesBasicos.getNumeroCelular()+"|"+e.getMessage());
				}
			}
			else
			{
				diagnosticoAccionDAO.save(diagnosticoAccion);
			}
		}
		/*List<FormSeccionDatosBasicos> resultFormSeccionDatosBasicos=formSeccionDatosBasicosDAO.getPacientesSinRegistroSospechoso();
		for(FormSeccionDatosBasicos formSeccionDatosBasicos:resultFormSeccionDatosBasicos)
		{
			formSeccionDatosBasicos.getRegistroFormulario().getRegistro().setUsuario(null);;
		}
		List result=new ArrayList();
		result.addAll(resultFormSeccionDatosBasicos);
		result.addAll(resultPacienteDatosPersonalesBasicos);*/
		return Response.ok(resultPacienteDatosPersonalesBasicos).build();
		}
	}
	
	@Path("/pacientesSinRegistroConfirmado16")
	@GET
	@Produces("application/json")
	public Response getPacientesSinRegistroConfirmado16(@QueryParam("token") String token,@QueryParam("modoProduccionSMS") String modoProduccionSMS)
	{
		if(!"miticPacientesSinRegistroConfirmado16Prod".equals(token))
		{
			return Response.ok().build();
		}
		synchronized(semaforoPacientesSinRegistroConfirmado16)
		{
		List<PacienteDatosPersonalesBasicos> resultPacienteDatosPersonalesBasicos=pacienteDatosPersonalesBasicosDAO.getPacientesSinRegistroConfirmado16();
		for(PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos:resultPacienteDatosPersonalesBasicos)
		{
			pacienteDatosPersonalesBasicos.getPaciente().setUsuario(null);
			DiagnosticoAccion diagnosticoAccion=new DiagnosticoAccion();
			diagnosticoAccion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
			diagnosticoAccion.setTipoAccion("notificacion_registro_confirmado_16");
			diagnosticoAccion.setEstadoEjecucion("ejecutado");
			diagnosticoAccion.setFechaHoraEjecucion(new Date());
			diagnosticoAccion.setResultadoEjecucion("exitoso");
			FormSeccionDatosBasicos formSeccionDatosBasicos=formSeccionDatosBasicosDAO.findByNumeroDocumento(pacienteDatosPersonalesBasicos.getNumeroDocumento());
			diagnosticoAccion.setValor("RECORDATORIO: "+formSeccionDatosBasicos.getNombre()+", acceda a https://f.mspbs.gov.py/#/i/"+formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getId()+"/"+formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getCodigoVerificacion()+". Dificultad de acceder? Contactar de inmediato al: 0962320456. MSPBS.");
			if("true".equals(modoProduccionSMS))
			{
				try
				{
					smsSender.sendSMSMenuMovil(pacienteDatosPersonalesBasicos.getNumeroCelular(), diagnosticoAccion.getValor(),"pacientesinregistroconfirmado16"+pacienteDatosPersonalesBasicos.getPaciente().getId());
					diagnosticoAccionDAO.save(diagnosticoAccion);
				}
				catch(SmsException e)
				{
					System.out.println("pacientesSinRegistroConfirmado16-"+pacienteDatosPersonalesBasicos.getNumeroDocumento()+"|"+pacienteDatosPersonalesBasicos.getNumeroCelular()+"|"+e.getMessage());
				}
			}
			else
			{
				diagnosticoAccionDAO.save(diagnosticoAccion);
			}
		}
		/*List<FormSeccionDatosBasicos> resultFormSeccionDatosBasicos=formSeccionDatosBasicosDAO.getPacientesSinRegistroSospechoso();
		for(FormSeccionDatosBasicos formSeccionDatosBasicos:resultFormSeccionDatosBasicos)
		{
			formSeccionDatosBasicos.getRegistroFormulario().getRegistro().setUsuario(null);;
		}
		List result=new ArrayList();
		result.addAll(resultFormSeccionDatosBasicos);
		result.addAll(resultPacienteDatosPersonalesBasicos);*/
		return Response.ok(resultPacienteDatosPersonalesBasicos).build();
		}
	}
	
	@Path("/pacientesSinRegistroUbicacionSospechoso")
	@GET
	@Produces("application/json")
	public Response getPacientesSinRegistroUbicacionSospechoso(@QueryParam("token") String token,@QueryParam("modoProduccionSMS") String modoProduccionSMS)
	{
		if(!"miticPacientesSinRegistroUbicacionSospechoso".equals(token))
		{
			return Response.ok().build();
		}
		synchronized(semaforoPacientesSinRegistroUbicacionSospechoso)
		{
		List<PacienteDatosPersonalesBasicos> resultPacienteDatosPersonalesBasicos=pacienteDatosPersonalesBasicosDAO.getPacientesSinRegistroUbicacionSospechoso();
		for(PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos:resultPacienteDatosPersonalesBasicos)
		{
			pacienteDatosPersonalesBasicos.getPaciente().setUsuario(null);
			DiagnosticoAccion diagnosticoAccion=new DiagnosticoAccion();
			diagnosticoAccion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
			diagnosticoAccion.setTipoAccion("notificacion_ubicacion_sospechoso");
			diagnosticoAccion.setEstadoEjecucion("ejecutado");
			diagnosticoAccion.setFechaHoraEjecucion(new Date());
			diagnosticoAccion.setResultadoEjecucion("exitoso");
			diagnosticoAccion.setValor(pacienteDatosPersonalesBasicos.getNombre()+", debe acceder a la App COVID19-PY o al link http://a.mspbs.gov.py/ para reportar su ubicacion. MSPBS.");
			if("true".equals(modoProduccionSMS))
			{
				try
				{
					smsSender.sendSMSMenuMovil(pacienteDatosPersonalesBasicos.getNumeroCelular(), diagnosticoAccion.getValor(),"pacientesSinRegistroUbicacionSospechoso"+pacienteDatosPersonalesBasicos.getPaciente().getId());
					diagnosticoAccionDAO.save(diagnosticoAccion);
				}
				catch(SmsException e)
				{
					System.out.println("pacientesSinRegistroUbicacionSospechoso-"+pacienteDatosPersonalesBasicos.getNumeroDocumento()+"|"+pacienteDatosPersonalesBasicos.getNumeroCelular()+"|"+e.getMessage());
				}
			}
			else
			{
				diagnosticoAccionDAO.save(diagnosticoAccion);
			}
		}
		return Response.ok(resultPacienteDatosPersonalesBasicos).build();
		}
	}
	
	@Path("/pacientesSinRegistroUbicacionConfirmado3")
	@GET
	@Produces("application/json")
	public Response getPacientesSinRegistroUbicacionConfirmado3(@QueryParam("token") String token,@QueryParam("modoProduccionSMS") String modoProduccionSMS)
	{
		if(!"miticPacientesSinRegistroUbicacionConfirmado3".equals(token))
		{
			return Response.ok().build();
		}
		synchronized(semaforoPacientesSinRegistroUbicacionConfirmado3)
		{
		List<PacienteDatosPersonalesBasicos> resultPacienteDatosPersonalesBasicos=pacienteDatosPersonalesBasicosDAO.getPacientesSinRegistroUbicacionConfirmado3();
		for(PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos:resultPacienteDatosPersonalesBasicos)
		{
			pacienteDatosPersonalesBasicos.getPaciente().setUsuario(null);
			DiagnosticoAccion diagnosticoAccion=new DiagnosticoAccion();
			diagnosticoAccion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
			diagnosticoAccion.setTipoAccion("notificacion_ubicacion_confirmado_3");
			diagnosticoAccion.setEstadoEjecucion("ejecutado");
			diagnosticoAccion.setFechaHoraEjecucion(new Date());
			diagnosticoAccion.setResultadoEjecucion("exitoso");
			diagnosticoAccion.setValor(pacienteDatosPersonalesBasicos.getNombre()+", debe acceder a la App COVID19-PY o al link http://a.mspbs.gov.py/ para reportar su ubicacion. MSPBS.");
			if("true".equals(modoProduccionSMS))
			{
				try
				{
					smsSender.sendSMSMenuMovil(pacienteDatosPersonalesBasicos.getNumeroCelular(), diagnosticoAccion.getValor(),"pacientesSinRegistroUbicacionConfirmado3"+pacienteDatosPersonalesBasicos.getPaciente().getId());
					diagnosticoAccionDAO.save(diagnosticoAccion);
				}
				catch(SmsException e)
				{
					System.out.println("pacientesSinRegistroUbicacionConfirmado3-"+pacienteDatosPersonalesBasicos.getNumeroDocumento()+"|"+pacienteDatosPersonalesBasicos.getNumeroCelular()+"|"+e.getMessage());
				}
			}
			else
			{
				diagnosticoAccionDAO.save(diagnosticoAccion);
			}
		}
		return Response.ok(resultPacienteDatosPersonalesBasicos).build();
		}
	}
	
	@Path("/pacientesSinRegistroUbicacionConfirmado6")
	@GET
	@Produces("application/json")
	public Response getPacientesSinRegistroUbicacionConfirmado6(@QueryParam("token") String token,@QueryParam("modoProduccionSMS") String modoProduccionSMS)
	{
		if(!"miticPacientesSinRegistroUbicacionConfirmado6".equals(token))
		{
			return Response.ok().build();
		}
		synchronized(semaforoPacientesSinRegistroUbicacionConfirmado6)
		{
		List<PacienteDatosPersonalesBasicos> resultPacienteDatosPersonalesBasicos=pacienteDatosPersonalesBasicosDAO.getPacientesSinRegistroUbicacionConfirmado6();
		for(PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos:resultPacienteDatosPersonalesBasicos)
		{
			pacienteDatosPersonalesBasicos.getPaciente().setUsuario(null);
			DiagnosticoAccion diagnosticoAccion=new DiagnosticoAccion();
			diagnosticoAccion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
			diagnosticoAccion.setTipoAccion("notificacion_ubicacion_confirmado_6");
			diagnosticoAccion.setEstadoEjecucion("ejecutado");
			diagnosticoAccion.setFechaHoraEjecucion(new Date());
			diagnosticoAccion.setResultadoEjecucion("exitoso");
			diagnosticoAccion.setValor(pacienteDatosPersonalesBasicos.getNombre()+" debido a su cuarentena debe actualizar su ubicacion. Acceda a http://a.mspbs.gov.py/ . Dificultad de acceder? Llame al 0962320456. MSPBS.");
			if("true".equals(modoProduccionSMS))
			{
				try
				{
					smsSender.sendSMSMenuMovil(pacienteDatosPersonalesBasicos.getNumeroCelular(), diagnosticoAccion.getValor(),"pacientesSinRegistroUbicacionConfirmado6"+pacienteDatosPersonalesBasicos.getPaciente().getId());
					Notificacion notificacion=new Notificacion();
					notificacion.setMensaje("Estimado/a "+pacienteDatosPersonalesBasicos.getNombre()+" en la fecha, aún no hemos recibido la actualización de su ubicación. Durante este periodo de aislamiento obligatorio es de caracter obligatorio que reporte su ubicación. Por favor acceda a la opción \"Reportar Ubicación\" del Menú Principal.");
					notificacion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
					notificacion.setRemitente("MSPBS");
					notificacion.setFechaNotificacion(new Date());
					notificacion.setVisto(false);
					notificacionDAO.save(notificacion);
					diagnosticoAccionDAO.save(diagnosticoAccion);
				}
				catch(SmsException e)
				{
					System.out.println("pacientesSinRegistroUbicacionConfirmado6-"+pacienteDatosPersonalesBasicos.getNumeroDocumento()+"|"+pacienteDatosPersonalesBasicos.getNumeroCelular()+"|"+e.getMessage());
				}
			}
			else
			{
				diagnosticoAccionDAO.save(diagnosticoAccion);
			}
		}
		return Response.ok(resultPacienteDatosPersonalesBasicos).build();
		}
	}
	
	@Path("/pacientesSinReporteSalud")
	@GET
	@Produces("application/json")
	public Response getPacientesSinReporteSalud(@QueryParam("token") String token,@QueryParam("modoProduccionSMS") String modoProduccionSMS, @QueryParam("sospechosos") String sospechosos)
	{
		if(!"pacientesSinReporteSalud".equals(token))
		{
			return Response.ok().build();
		}
		synchronized(semaforoPacientesSinReporteSalud)
		{
		List<PacienteDatosPersonalesBasicos> resultPacienteDatosPersonalesBasicos=pacienteDatosPersonalesBasicosDAO.getPacientesSinReporteSalud(sospechosos);
		for(PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos:resultPacienteDatosPersonalesBasicos)
		{
			pacienteDatosPersonalesBasicos.getPaciente().setUsuario(null);
			DiagnosticoAccion diagnosticoAccion=new DiagnosticoAccion();
			diagnosticoAccion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
			diagnosticoAccion.setTipoAccion("notificacion_reporte_salud");
			diagnosticoAccion.setEstadoEjecucion("ejecutado");
			diagnosticoAccion.setFechaHoraEjecucion(new Date());
			diagnosticoAccion.setResultadoEjecucion("exitoso");
			diagnosticoAccion.setValor("Estimado/a "+pacienteDatosPersonalesBasicos.getNombre()+" debe acceder a la App COVID19-PY para reportar su estado de salud o aqui: http://a.mspbs.gov.py/. MSPBS.");
			if("true".equals(modoProduccionSMS))
			{
				try
				{
					smsSender.sendSMSMenuMovil(pacienteDatosPersonalesBasicos.getNumeroCelular(), diagnosticoAccion.getValor(),"pacientesSinReporteSalud"+pacienteDatosPersonalesBasicos.getPaciente().getId());
					diagnosticoAccionDAO.save(diagnosticoAccion);
				}
				catch(SmsException e)
				{
					System.out.println("pacientesSinReporteSalud-"+pacienteDatosPersonalesBasicos.getNumeroDocumento()+"|"+pacienteDatosPersonalesBasicos.getNumeroCelular()+"|"+e.getMessage());
				}
			}
			else
			{
				diagnosticoAccionDAO.save(diagnosticoAccion);
			}
		}
		return Response.ok(resultPacienteDatosPersonalesBasicos).build();
		}
	}

}