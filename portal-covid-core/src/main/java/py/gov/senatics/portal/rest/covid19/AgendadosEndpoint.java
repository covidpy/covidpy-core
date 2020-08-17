package py.gov.senatics.portal.rest.covid19;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.exception.ApiException;

import py.gov.senatics.portal.annotation.RolAllowed;
import py.gov.senatics.portal.annotation.Secured;
import py.gov.senatics.portal.business.covid19.PacienteBC;
import py.gov.senatics.portal.modelCovid19.ExamenLaboratorial;
import py.gov.senatics.portal.modelCovid19.FormSeccionDatosBasicos;
import py.gov.senatics.portal.modelCovid19.Notificacion;
import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.modelCovid19.PacienteDatosPersonalesBasicos;
import py.gov.senatics.portal.modelCovid19.Registro;
import py.gov.senatics.portal.modelCovid19.RegistroFormulario;
import py.gov.senatics.portal.modelCovid19.admin.Configuracion;
import py.gov.senatics.portal.persistence.covid19.AgendadosDAO;
import py.gov.senatics.portal.persistence.covid19.ExamenLaboratorialDAO;
import py.gov.senatics.portal.persistence.covid19.FormSeccionDatosBasicosDAO;
import py.gov.senatics.portal.persistence.covid19.NotificacionDAO;
import py.gov.senatics.portal.persistence.covid19.PacienteDatosPersonalesBasicosDAO;
import py.gov.senatics.portal.persistence.covid19.RegistroDAO;
import py.gov.senatics.portal.persistence.covid19.RegistroFormularioDAO;
import py.gov.senatics.portal.persistence.covid19.admin.ConfiguracionDao;
import py.gov.senatics.portal.session.UserManager;
import py.gov.senatics.portal.util.AuthDAO;
import py.gov.senatics.portal.util.Config;
import py.gov.senatics.portal.util.SmsException;
import py.gov.senatics.portal.util.SmsSender;

@Path("/covid19/agendados")
@RequestScoped
public class AgendadosEndpoint {
	@Inject
	private AgendadosDAO agendadosDAO;
	
	@Inject
	private FormSeccionDatosBasicosDAO formSeccionDatosBasicosDAO;
	
	@Inject
	private RegistroDAO registroDAO;
	
	@Inject
	private RegistroFormularioDAO registroFormularioDAO;
	
	@Inject
	private UserManager userManager;
	
	@Inject
	private AuthDAO authDAO;
	
	@Inject
	private PacienteBC pacienteBC;
	
	@Inject
	private NotificacionDAO notificacionDAO;
	
	@Inject
	private SmsSender smsSender;
	
	@Inject
	private ConfiguracionDao configuracionDao;
	
	@Inject
	private PacienteDatosPersonalesBasicosDAO pacienteDatosPersonalesBasicosDAO;
	
	@Inject
	private ExamenLaboratorialDAO examenLaboratorialDAO;
	
	private static final String SEMAFORO="semaforo";

	@Path("/proceso")
	@GET
	@Produces("application/json")
	@Secured
	@RolAllowed("Registro Automatico Agendamiento")
	public Response importar(@QueryParam("modoProduccionSMS") String modoProduccionSMS,@QueryParam("token") String token)
	{
		if(!"miticAgendadosProd".equals(token))
		{
			return Response.ok().build();
		}
		synchronized(SEMAFORO)
		{
		Configuracion configuracion=configuracionDao.obtenerConfiguraciones(Arrays.asList("covid19.agendados.lastId")).get(0);
		
		Integer lastId=Integer.valueOf(configuracion.getValorVariable());
		List<Object[]> result=agendadosDAO.getAgendados(lastId);
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
		for(Object[] objectArray:result)
		{
			String numeroDocumento=(String) objectArray[1];
			String numeroCelular=(String) objectArray[2];
			
			if(numeroDocumento!=null&&!numeroDocumento.isEmpty())
			{
				if(numeroCelular!=null&&!numeroCelular.isEmpty())
				{
					if(numeroCelular.matches("9[9876]\\d{7}"))
					{
						numeroCelular="595"+numeroCelular;
					}
					else if(numeroCelular.matches("09[9876]\\d{7}"))
					{
						numeroCelular="595"+numeroCelular.substring(1);
					}
					else if(numeroCelular.matches("\\+5959[9876]\\d{7}"))
					{
						numeroCelular=numeroCelular.substring(1);
					}
					if(!numeroCelular.matches("5959[9876]\\d{7}"))
					{
						System.out.println("agendados-"+objectArray[0]+"|"+numeroDocumento+"|"+numeroCelular+"|"+objectArray[3]+"|"+objectArray[4]+"|celular inválido");
						continue;
					}
					FormSeccionDatosBasicos formSeccionDatosBasicos=formSeccionDatosBasicosDAO.findByNumeroDocumento(numeroDocumento);
					if(formSeccionDatosBasicos==null)
					{
						ObjectMapper objectMapper=new ObjectMapper();
						try
						{
						JsonNode jsonNode=objectMapper.readTree(authDAO.obtenerDatos(Config.URL_API_IDENTIFICACIONES, numeroDocumento));
						if(jsonNode.get("obtenerPersonaPorNroCedulaResponse").get("return").get("error")==null)
						{
							
							Registro registro=new Registro();
							registro.setFechaCreacion(new Date());
							registro.setResponsableRegistro("operador");
							registro.setCodigoVerificacion(UUID.randomUUID().toString().substring(0, 8));
							registro.setEstado("registro");
							registro.setTipoRegistro("examen_laboratorio");
							registro.setUsuario(userManager.getRequestUser());
							registroDAO.save(registro);
							RegistroFormulario registroFormulario=new RegistroFormulario();
							registroFormulario.setRegistro(registro);
							registroFormulario.setFechaCreacion(new Date());
							registroFormulario.setNombre("aislamiento");
							registroFormulario.setEstado("no completado");
							registroFormulario.setRegistroFormularioAcompanante(false);
							registroFormularioDAO.save(registroFormulario);
							formSeccionDatosBasicos=new FormSeccionDatosBasicos();
							formSeccionDatosBasicos.setNumeroDocumento(numeroDocumento);
							formSeccionDatosBasicos.setNombre(jsonNode.get("obtenerPersonaPorNroCedulaResponse").get("return").get("nombres").asText());
							formSeccionDatosBasicos.setApellido(jsonNode.get("obtenerPersonaPorNroCedulaResponse").get("return").get("apellido").asText());
							formSeccionDatosBasicos.setFechaNacimiento(LocalDate.parse(jsonNode.get("obtenerPersonaPorNroCedulaResponse").get("return").get("fechNacim").asText().substring(0,10)));
							formSeccionDatosBasicos.setSexo(jsonNode.get("obtenerPersonaPorNroCedulaResponse").get("return").get("sexo").asText());
							formSeccionDatosBasicos.setFechaPrevistaTomaMuestraLaboratorial((Date) objectArray[3]);
							formSeccionDatosBasicos.setNumeroCelular(numeroCelular);
							formSeccionDatosBasicos.setTipoDocumento("0");
							formSeccionDatosBasicos.setRegistroFormulario(registroFormulario);
							formSeccionDatosBasicos.setNumeroCelularVerificado("no verificado");
							formSeccionDatosBasicosDAO.save(formSeccionDatosBasicos);
							Paciente paciente=pacienteBC.createPacienteFromRegistro(formSeccionDatosBasicos.getRegistroFormulario().getRegistro());
							ExamenLaboratorial examenLaboratorial=new ExamenLaboratorial();
							examenLaboratorial.setPaciente(paciente);
							examenLaboratorial.setUsuario(userManager.getRequestUser());
							examenLaboratorial.setFechaPrevistaTomaMuestraLaboratorial((Date) objectArray[3]);
							examenLaboratorial.setIdentificadorExterno((Integer) objectArray[0]);
							examenLaboratorial.setFechaNotificacionTomaMuestraLaboratorial(new Date());
							examenLaboratorial.setEstado("agendado");
							
							examenLaboratorial.setLocalTomaMuestra((String) objectArray[5]);
							
							examenLaboratorialDAO.save(examenLaboratorial);
							Notificacion notificacion=new Notificacion();
							notificacion.setMensaje(formSeccionDatosBasicos.getNombre()+", tiene examen laboratorial en fecha "+simpleDateFormat.format(formSeccionDatosBasicos.getFechaPrevistaTomaMuestraLaboratorial())+". Local: "+(String) objectArray[5]+". Tiene autorizacion de circulacion para tal efecto. MSPBS.");
							notificacion.setPaciente(paciente);
							notificacion.setRemitente("Vigilancia Sanitaria-MSPBS");
							notificacion.setFechaNotificacion(new Date());
							notificacion.setVisto(false);
							notificacionDAO.save(notificacion);
							if("true".equals(modoProduccionSMS))
							{
								try
								{
									smsSender.sendSMSMenuMovil(formSeccionDatosBasicos.getNumeroCelular(), formSeccionDatosBasicos.getNombre()+". Por prevencion debe guardar cuarentena obligatoria. Debe reportarse aqui: https://f.mspbs.gov.py/#/i/"+registro.getId()+"/"+registro.getCodigoVerificacion() +". MSPBS.","registro"+registro.getId());
									formSeccionDatosBasicos.setNumeroCelularVerificado("enviado");
									formSeccionDatosBasicosDAO.update(formSeccionDatosBasicos);
								}
								catch(SmsException e)
								{
									System.out.println("agendados-"+objectArray[0]+"|"+numeroDocumento+"|"+numeroCelular+"|"+objectArray[3]+"|"+objectArray[4]+"|Mensaje registro: "+e.getMessage());
								}
								try
								{
									smsSender.sendSMSMenuMovil(formSeccionDatosBasicos.getNumeroCelular(), notificacion.getMensaje(),"laboratorial"+notificacion.getId());
								}
								catch(SmsException e)
								{
									System.out.println("agendados-"+objectArray[0]+"|"+numeroDocumento+"|"+numeroCelular+"|"+objectArray[3]+"|"+objectArray[4]+"|Mensaje examen laboratorial: "+e.getMessage());
								}
							}
						}
						else
						{
							System.out.println("agendados-"+objectArray[0]+"|"+numeroDocumento+"|"+numeroCelular+"|"+objectArray[3]+"|"+objectArray[4]+"|no se encontró la persona en identificaciones");
						}
						}
						catch(Exception e)
						{
							System.out.println("agendados-"+objectArray[0]+"|"+numeroDocumento+"|"+numeroCelular+"|"+objectArray[3]+"|"+objectArray[4]+"|"+e.getMessage());
						}
					}
					else// if(formSeccionDatosBasicos.getFechaPrevistaTomaMuestraLaboratorial()==null||((Date) objectArray[3]).after(formSeccionDatosBasicos.getFechaPrevistaTomaMuestraLaboratorial()))
					{
						/*formSeccionDatosBasicos.setFechaPrevistaTomaMuestraLaboratorial((Date) objectArray[3]);
						formSeccionDatosBasicosDAO.update(formSeccionDatosBasicos);*/
						PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos=pacienteDatosPersonalesBasicosDAO.getByNroDocumento(numeroDocumento);						
						Notificacion notificacion=new Notificacion();
						notificacion.setMensaje(formSeccionDatosBasicos.getNombre()+", tiene examen laboratorial en fecha "+simpleDateFormat.format((Date)objectArray[3])+". Local: "+(String) objectArray[5]+". Tiene autorizacion de circulacion para tal efecto. MSPBS.");
						notificacion.setRemitente("Vigilancia Sanitaria-MSPBS");
						notificacion.setFechaNotificacion(new Date());
						notificacion.setVisto(false);
						String numeroCelularMensaje=null;
						if(pacienteDatosPersonalesBasicos!=null)
						{
							ExamenLaboratorial examenLaboratorial=new ExamenLaboratorial();
							examenLaboratorial.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
							examenLaboratorial.setUsuario(userManager.getRequestUser());
							examenLaboratorial.setFechaPrevistaTomaMuestraLaboratorial((Date) objectArray[3]);
							examenLaboratorial.setIdentificadorExterno((Integer) objectArray[0]);
							examenLaboratorial.setFechaNotificacionTomaMuestraLaboratorial(new Date());
							examenLaboratorial.setEstado("agendado");
							
							examenLaboratorial.setLocalTomaMuestra((String) objectArray[5]);
							
							examenLaboratorialDAO.save(examenLaboratorial);
							notificacion.setPaciente(pacienteDatosPersonalesBasicos.getPaciente());
							notificacionDAO.save(notificacion);
							numeroCelularMensaje=pacienteDatosPersonalesBasicos.getNumeroCelular();
						}
						else
						{
							numeroCelularMensaje=formSeccionDatosBasicos.getNumeroCelular();
						}
						if("true".equals(modoProduccionSMS))
						{
							try
							{
								smsSender.sendSMSMenuMovil(numeroCelular, notificacion.getMensaje(),"laboratorial"+notificacion.getId());
							}
							catch(SmsException e)
							{
								System.out.println("agendados-"+objectArray[0]+"|"+numeroDocumento+"|"+numeroCelular+"|"+objectArray[3]+"|"+objectArray[4]+"|"+e.getMessage());
							}
						}
					}
					/*else
					{
						System.out.println("agendados-"+objectArray[0]+"|"+numeroDocumento+"|"+numeroCelular+"|"+objectArray[3]+"|"+objectArray[4]+"|fecha encontrada menor registro actual");
					}*/
				}
				else
				{
					System.out.println("agendados-"+objectArray[0]+"|"+numeroDocumento+"|"+numeroCelular+"|"+objectArray[3]+"|"+objectArray[4]+"|sin celular");
				}
			}
			else
			{
				System.out.println("agendados-"+objectArray[0]+"|"+numeroDocumento+"|"+numeroCelular+"|"+objectArray[3]+"|"+objectArray[4]+"|sin cédula");
			}
			if(lastId.compareTo((Integer) objectArray[0])<0)
			{
				lastId=(Integer) objectArray[0];
			}
		}
		configuracion.setValorVariable(lastId.toString());
		configuracionDao.update(configuracion);
		return Response.ok().build();
		}
	}

}