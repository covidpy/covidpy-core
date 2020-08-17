package py.gov.senatics.portal.business.covid19;

import java.util.*;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import py.gov.senatics.portal.cache.ConfiguracionCache;
import py.gov.senatics.portal.modelCovid19.*;
import py.gov.senatics.portal.modelCovid19.admin.LoginAutomatico;
import py.gov.senatics.portal.modelCovid19.admin.Rol;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;
import py.gov.senatics.portal.persistence.covid19.*;
import py.gov.senatics.portal.persistence.covid19.admin.LoginAutomaticoDAO;
import py.gov.senatics.portal.persistence.covid19.admin.UsuarioDao;
import py.gov.senatics.portal.util.ConfigProperties;
import py.gov.senatics.portal.util.SmsSender;

@RequestScoped
public class PacienteBC {
	
	@Inject
	private RegistroDAO registroDAO;
	
	@Inject
	private PacienteDAO pacienteDAO;
	
	@Inject
	private FormSeccionDatosBasicosDAO formSeccionDatosBasicosDAO;
	
	@Inject
	private PacienteDatosPersonalesBasicosDAO pacienteDatosPersonalesBasicosDAO;
	
	@Inject
	private UsuarioDao usuarioDao;
	
	@Inject
	private LoginAutomaticoDAO loginAutomaticoDAO;

	@Inject
	private SmsSender smsSender;

	@Inject
	private ConfiguracionCache conf;

	@Inject
	private RegistroFormularioDAO registroFormularioDAO;
	
	@Inject
	private HistoricoClinicoDAO historicoClinicoDAO;
	
	@Inject
	private HistoricoClinicoDatosDAO historicoClinicoDatosDAO;
	
	@Inject
	private ReporteSaludDAO reporteSaludDAO;
	
	@Inject
	private RegistroUbicacionDAO registroUbicacionDAO;
	
	@Inject
	private PacienteEstadoSaludDAO pacienteEstadoSaludDAO;
	
	@Inject
	private DiagnosticoRecomendacionDAO diagnosticoRecomendacionDAO;
	
	@Inject
	private DiagnosticoAccionDAO diagnosticoAccionDAO;

	@Inject
	private TipoRegistroDAO tipoRegistroDAO;

	public Paciente createPacienteFromRegistro(Registro registro)
	{
		if("reporte_salud".equals(registro.getTipoRegistro()))
		{
			return null;
		}
		FormSeccionDatosBasicos formSeccionDatosBasicos=formSeccionDatosBasicosDAO.findByRegistroId(registro.getId());
		Paciente paciente=new Paciente();
		paciente.setInicioSeguimiento(new Date());
		if(formSeccionDatosBasicos.getInicioAislamiento()!=null)
		{
			paciente.setInicioAislamiento(formSeccionDatosBasicos.getInicioAislamiento());
		}
		else
		{
			paciente.setInicioAislamiento(paciente.getInicioSeguimiento());
		}
		paciente.setClasificacionPaciente("A");
		if("aislamiento_confirmado".contentEquals(formSeccionDatosBasicos.getRegistroFormulario().getRegistro().getTipoRegistro())) {
			paciente.setResultadoUltimoDiagnostico("positivo");
		}
		else
		{
			paciente.setResultadoUltimoDiagnostico("sospechoso");
		}
		paciente.setFechaUltimoDiagnostico(paciente.getInicioAislamiento());
		//if("aislamiento".equals(registro.getTipoRegistro()))
		{
			Usuario usuario=new Usuario();
			usuario.setNombre(formSeccionDatosBasicos.getNombre());
			usuario.setApellido(formSeccionDatosBasicos.getApellido());
			if("0".equals(formSeccionDatosBasicos.getTipoDocumento()))
			{
				usuario.setUsername(formSeccionDatosBasicos.getNumeroDocumento());
			}
			else if("1".equals(formSeccionDatosBasicos.getTipoDocumento()))
			{
				usuario.setUsername(formSeccionDatosBasicos.getNumeroDocumento());
			}
			usuario.setTelefono(formSeccionDatosBasicos.getNumeroCelular());
			usuario.setCedula(formSeccionDatosBasicos.getNumeroDocumento());
			usuario.setCedula(formSeccionDatosBasicos.getNumeroDocumento());
			//usuario.setPassword(formSeccionDatosBasicos.getContrasenha());
			usuario.setActivo(true);
			Rol rol=new Rol();
			rol.setId(3L);
			usuario.addRol(rol);
			usuario=usuarioDao.crear(usuario);			
			paciente.setUsuario(usuario);
			/*LoginAutomatico loginAutomatico=new LoginAutomatico();
			loginAutomatico.setUsuario(usuario);
			loginAutomatico.setTimestampCreacion(new Date());
			loginAutomatico.setEstado("activo");
			loginAutomatico.setToken(UUID.randomUUID().toString().substring(0, 8));
			paciente.setToken(loginAutomatico.getToken());
			loginAutomaticoDAO.save(loginAutomatico);*/
		}
		/*else if("reporte_salud".equals(registro.getTipoRegistro()))
		{
			paciente.setUsuario(registro.getUsuario());
		}*/
		pacienteDAO.save(paciente);
		PacienteDatosPersonalesBasicos pacienteDatosPersonalesBasicos=new PacienteDatosPersonalesBasicos();
		pacienteDatosPersonalesBasicos.setPaciente(paciente);
		pacienteDatosPersonalesBasicos.setApellido(formSeccionDatosBasicos.getApellido());
		pacienteDatosPersonalesBasicos.setCiudadDomicilio(formSeccionDatosBasicos.getCiudadDomicilio());
		pacienteDatosPersonalesBasicos.setCiudadNacimiento(formSeccionDatosBasicos.getCiudadNacimiento());
		pacienteDatosPersonalesBasicos.setCorreoElectronico(formSeccionDatosBasicos.getCorreoElectronico());
		pacienteDatosPersonalesBasicos.setDepartamentoDomicilio(formSeccionDatosBasicos.getDepartamentoDomicilio());
		pacienteDatosPersonalesBasicos.setDireccionDomicilio(formSeccionDatosBasicos.getDireccionDomicilio());
		pacienteDatosPersonalesBasicos.setFechaNacimiento(formSeccionDatosBasicos.getFechaNacimiento());
		pacienteDatosPersonalesBasicos.setNombre(formSeccionDatosBasicos.getNombre());
		pacienteDatosPersonalesBasicos.setNumeroCelular(formSeccionDatosBasicos.getNumeroCelular());
		pacienteDatosPersonalesBasicos.setNumeroCelularVerificado(formSeccionDatosBasicos.getNumeroCelularVerificado());
		pacienteDatosPersonalesBasicos.setNumeroDocumento(formSeccionDatosBasicos.getNumeroDocumento());
		pacienteDatosPersonalesBasicos.setNumeroTelefono(formSeccionDatosBasicos.getNumeroTelefono());
		pacienteDatosPersonalesBasicos.setPaisEmisorDocumento(formSeccionDatosBasicos.getPaisEmisorDocumento());
		pacienteDatosPersonalesBasicos.setPaisNacionalidad(formSeccionDatosBasicos.getPaisNacionalidad());
		pacienteDatosPersonalesBasicos.setResidenteParaguay(formSeccionDatosBasicos.getResidenteParaguay());
		pacienteDatosPersonalesBasicos.setSexo(formSeccionDatosBasicos.getSexo());
		pacienteDatosPersonalesBasicos.setTipoDocumento(formSeccionDatosBasicos.getTipoDocumento());
		pacienteDatosPersonalesBasicosDAO.save(pacienteDatosPersonalesBasicos);
		return paciente;
	}

	public Response recuperarClave(String nroDocumento, String celular) {
		// Preformateo
	    nroDocumento = nroDocumento.trim();
		StringBuilder sb = new StringBuilder();
		if (celular.startsWith("0")) {
			sb.append("595")
					.append(celular.substring(1).trim());
		} else if (celular.startsWith("+")) {
			sb.append(celular.substring(1).trim());
		}
		celular = sb.toString();

		PacienteDatosPersonalesBasicos datosPaciente = this.pacienteDatosPersonalesBasicosDAO.getByNroDocumento(nroDocumento);
		if (datosPaciente == null) {
			Map<String, List<String>> validation = new HashMap<>();
			validation.put("nroDocumento", new ArrayList<>());
			validation.get("nroDocumento").add("Este documento no se encuentra registrado");
			return Response.status(Response.Status.BAD_REQUEST).entity(validation).build();
		}
		if (!datosPaciente.getNumeroCelular().equals(celular)
				|| !datosPaciente.getNumeroCelularVerificado().equals(PacienteDatosPersonalesBasicos.ESTADO_CELULAR_VERIFICADO)) {
			Map<String, List<String>> validation = new HashMap<>();
			validation.put("celular", new ArrayList<>());
			validation.get("celular").add("Este número no fue verificado");
		    return Response.status(Response.Status.BAD_REQUEST).entity(validation).build();
		}

		LoginAutomatico loginAutomatico = crearLoginAutomatico(datosPaciente.getPaciente());

		String message = "Parar recuperar su contrasena en la APP "+this.conf.get(ConfigProperties.COVID_NOMBRE_APP)+
				", Ingrese al link: "+this.conf.get(ConfigProperties.COVID_URL_APP)+"login?redirect="+this.conf.get(ConfigProperties.COVID_PATH_CAMBIAR_CLAVE)+"&token="+
				loginAutomatico.getToken();
		smsSender.sendSMSMenuMovil(datosPaciente.getNumeroCelular(),
				message,"reseteo"+loginAutomatico.getId());

		return Response.ok().build();
	}

	public LoginAutomatico crearLoginAutomatico(Paciente p) {
		LoginAutomatico loginAutomatico=new LoginAutomatico();
		loginAutomatico.setUsuario(p.getUsuario());
		loginAutomatico.setEstado(LoginAutomatico.ESTADO_ACTIVO);
		loginAutomatico.setToken(UUID.randomUUID().toString().substring(0, 24));
		loginAutomaticoDAO.save(loginAutomatico);
		return loginAutomatico;
	}
	
	public PacienteDatosPersonalesBasicos getPacienteByNumeroDocumento(String numeroDocumento)
	{
		return pacienteDatosPersonalesBasicosDAO.getByNroDocumento(numeroDocumento);
	}
	public RegistroFormulario crearRegistroFormularioOperador(Paciente paciente, Usuario usuario) {
		return crearRegistroFormulario(paciente, Registro.RESPONSABLE_OPERADOR, usuario);
	}

	private RegistroFormulario crearRegistroFormulario(Paciente paciente, String responsableRegistro, Usuario usuarioRegistro) {
		Registro registro=new Registro();
		registro.setFechaCreacion(new Date());
		registro.setResponsableRegistro(responsableRegistro);
		registro.setCodigoVerificacion(UUID.randomUUID().toString().substring(0, 8));
		registro.setEstado(Registro.ESTADO_REGISTRO);
		registro.setTipoRegistro(Registro.TIPO_REPORTE_SALUD);
		registro.setUsuario(usuarioRegistro);
		registroDAO.save(registro);
		RegistroFormulario registroFormulario=new RegistroFormulario();
		registroFormulario.setRegistro(registro);
		registroFormulario.setFechaCreacion(new Date());
		registroFormulario.setNombre(RegistroFormulario.NOMBRE_PACIENTE);
		registroFormulario.setEstado(RegistroFormulario.ESTADO_COMPLETO);
		registroFormulario.setRegistroFormularioAcompanante(false);
		registroFormulario.setPaciente(paciente);
		registroFormularioDAO.save(registroFormulario);
		return registroFormulario;
	}

	public RegistroFormulario crearRegistroFormularioAutoreporte(Paciente paciente) {
		return crearRegistroFormulario(paciente, Registro.RESPONSABLE_PACIENTE, paciente.getUsuario());
	}
	
	public HistoricoClinico createHistoricoClinicoFromFormSeccionDatosClinicos(FormSeccionDatosClinicos formSeccionDatosClinicos, Paciente paciente, Usuario usuario)
	{
		HistoricoClinico historicoClinico=new HistoricoClinico();
		historicoClinico.setRegistro(formSeccionDatosClinicos.getRegistroFormulario().getRegistro());
		historicoClinico.setPaciente(paciente);
		historicoClinico.setFechaRegistro(new Date());
		historicoClinico.setUsuario(usuario);
		historicoClinicoDAO.save(historicoClinico);
		
		HistoricoClinicoDatos historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("viveSolo");
		historicoClinicoDatos.setValorDatos(formSeccionDatosClinicos.getEvaluacionRiesgoViveSolo().toString());
		historicoClinicoDatos.setTipoClinicoDato("factor_riesgo");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("tieneHabitacionPropria");
		historicoClinicoDatos.setValorDatos(formSeccionDatosClinicos.getEvaluacionRiesgoTieneHabitacionPropria().toString());
		historicoClinicoDatos.setTipoClinicoDato("factor_riesgo");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("cardiopatiaCronica");
		historicoClinicoDatos.setValorDatos(formSeccionDatosClinicos.getEnfermedadBaseCardiopatiaCronica().toString());
		historicoClinicoDatos.setTipoClinicoDato("factor_riesgo");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("pulmonarCronico");
		historicoClinicoDatos.setValorDatos(formSeccionDatosClinicos.getEnfermedadBasePulmonarCronico().toString());
		historicoClinicoDatos.setTipoClinicoDato("factor_riesgo");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("asma");
		historicoClinicoDatos.setValorDatos(formSeccionDatosClinicos.getEnfermedadBaseAsma().toString());
		historicoClinicoDatos.setTipoClinicoDato("factor_riesgo");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("diabetes");
		historicoClinicoDatos.setValorDatos(formSeccionDatosClinicos.getEnfermedadBaseDiabetes().toString());
		historicoClinicoDatos.setTipoClinicoDato("factor_riesgo");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("renalCronico");
		historicoClinicoDatos.setValorDatos(formSeccionDatosClinicos.getEnfermedadBaseRenalCronico().toString());
		historicoClinicoDatos.setTipoClinicoDato("factor_riesgo");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("hepaticaGrave");
		historicoClinicoDatos.setValorDatos(formSeccionDatosClinicos.getEnfermedadBaseHepaticaGrave().toString());
		historicoClinicoDatos.setTipoClinicoDato("factor_riesgo");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("usomedicamento");
		historicoClinicoDatos.setValorDatos(formSeccionDatosClinicos.getEvaluacionRiesgoUsomedicamento().toString());
		historicoClinicoDatos.setTipoClinicoDato("factor_riesgo");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("medicamento");
		historicoClinicoDatos.setValorDatos(formSeccionDatosClinicos.getEvaluacionRiesgoMedicamento());
		historicoClinicoDatos.setTipoClinicoDato("factor_riesgo");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("hipertensionArterial");
		historicoClinicoDatos.setValorDatos(formSeccionDatosClinicos.getEnfermedadBaseHipertensionArterial().toString());
		historicoClinicoDatos.setTipoClinicoDato("factor_riesgo");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("autoinmune");
		historicoClinicoDatos.setValorDatos(formSeccionDatosClinicos.getEnfermedadBaseAutoinmune().toString());
		historicoClinicoDatos.setTipoClinicoDato("factor_riesgo");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("neoplasias");
		historicoClinicoDatos.setValorDatos(formSeccionDatosClinicos.getEnfermedadBaseNeoplasias().toString());
		historicoClinicoDatos.setTipoClinicoDato("factor_riesgo");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		return historicoClinico;
	}
	
	public HistoricoClinico createHistoricoClinicoFromReporteSalud(ReporteSalud reporteSalud, Paciente paciente, Usuario usuario)
	{
		HistoricoClinico historicoClinico=new HistoricoClinico();
		historicoClinico.setRegistro(reporteSalud.getRegistroFormulario().getRegistro());
		historicoClinico.setPaciente(paciente);
		historicoClinico.setFechaRegistro(new Date());
		historicoClinico.setUsuario(usuario);
		historicoClinicoDAO.save(historicoClinico);
		
		PacienteEstadoSalud pacienteEstadoSalud=pacienteEstadoSaludDAO.getByPaciente(paciente.getId());
		if(pacienteEstadoSalud==null)
		{
			pacienteEstadoSalud=new PacienteEstadoSalud();
			pacienteEstadoSalud.setPaciente(paciente);
		}
		
		pacienteEstadoSalud.setHistoricoClinico(historicoClinico);
		pacienteEstadoSalud.setUltimoRegistroTipo(reporteSalud.getRegistroFormulario().getRegistro().getTipoRegistro());
		pacienteEstadoSalud.setUltimoReporteFecha(reporteSalud.getRegistroFormulario().getFechaCreacion());
		pacienteEstadoSalud.setUltimoReporteTipo("auto_reporte");
		
		if(reporteSalud.getResultadoGrupos().contains("C"))
		{
			pacienteEstadoSalud.setClasificacionPaciente("C");
		}
		else if(reporteSalud.getResultadoGrupos().contains("B"))
		{
			pacienteEstadoSalud.setClasificacionPaciente("B");
		}
		else if(reporteSalud.getResultadoGrupos().contains("A"))
		{
			pacienteEstadoSalud.setClasificacionPaciente("A");
		}
		
		if(pacienteEstadoSalud.getClasificacionPaciente()!=null)
		{
			paciente.setClasificacionPaciente(pacienteEstadoSalud.getClasificacionPaciente());
			pacienteDAO.update(paciente);
		}
		
		if("si".equals(reporteSalud.getDificultadRespirar()))
		{
			pacienteEstadoSalud.setSintomasDificultadRespirar(true);
		}
		else if("no".equals(reporteSalud.getDificultadRespirar()))
		{
			pacienteEstadoSalud.setSintomasDificultadRespirar(false);
		}
		
		if("si".equals(reporteSalud.getCongestionNasal()))
		{
			pacienteEstadoSalud.setSintomasDificultadRespirarCongestionNasal(true);
		}
		else if("no".equals(reporteSalud.getCongestionNasal()))
		{
			pacienteEstadoSalud.setSintomasDificultadRespirarCongestionNasal(false);
		}
		
		if("si".equals(reporteSalud.getSecrecionNasal()))
		{
			pacienteEstadoSalud.setSintomasDificultadRespirarRinorrea(true);
		}
		else if("no".equals(reporteSalud.getSecrecionNasal()))
		{
			pacienteEstadoSalud.setSintomasDificultadRespirarRinorrea(false);
		}
		
		if("si".equals(reporteSalud.getDolorGarganta()))
		{
			pacienteEstadoSalud.setSintomasDificultadRespirarDolorGarganta(true);
		}
		else if("no".equals(reporteSalud.getDolorGarganta()))
		{
			pacienteEstadoSalud.setSintomasDificultadRespirarDolorGarganta(false);
		}
		
		if("si".equals(reporteSalud.getSentisFiebre()))
		{
			pacienteEstadoSalud.setSintomasFiebre(true);
		}
		else if("no".equals(reporteSalud.getSentisFiebre()))
		{
			pacienteEstadoSalud.setSintomasFiebre(false);
		}
		
		if(reporteSalud.getTemperatura()!=null)
		{
			pacienteEstadoSalud.setSintomasFiebreUltimaMedicion(reporteSalud.getTemperatura().toString());
		}
		else
		{
			pacienteEstadoSalud.setSintomasFiebreUltimaMedicion(null);
		}
		
		String sintomasOtros="";
		if(reporteSalud.getSignosSintomasDescritos()!=null)
		{
			sintomasOtros+=" "+reporteSalud.getSignosSintomasDescritos();
		}
		if(reporteSalud.getSignosSintomasDescritosB()!=null)
		{
			sintomasOtros+=" "+reporteSalud.getSignosSintomasDescritosB();
		}
		pacienteEstadoSalud.setSintomasOtros(sintomasOtros);
		
		
		if("si".equals(reporteSalud.getTos()))
		{
			pacienteEstadoSalud.setSintomasTos(true);
		}
		else if("no".equals(reporteSalud.getTos()))
		{
			pacienteEstadoSalud.setSintomasTos(false);
		}
		
		if(pacienteEstadoSalud.getId()==null)
		{
			pacienteEstadoSaludDAO.save(pacienteEstadoSalud);
		}
		else
		{
			pacienteEstadoSaludDAO.update(pacienteEstadoSalud);
		}
		
		HistoricoClinicoDatos historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("comoTeSentis");
		historicoClinicoDatos.setValorDatos(reporteSalud.getComoTeSentis());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("comoTeSentisConRelacionAyer");
		historicoClinicoDatos.setValorDatos(reporteSalud.getComoTeSentisConRelacionAyer());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("congestionNasal");
		historicoClinicoDatos.setValorDatos(reporteSalud.getCongestionNasal());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("desdeCuandoFiebre");
		historicoClinicoDatos.setValorDatos(reporteSalud.getDesdeCuandoFiebre());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("desdeCuandoOlores");
		historicoClinicoDatos.setValorDatos(reporteSalud.getDesdeCuandoOlores());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("desdeCuandoOloresB");
		historicoClinicoDatos.setValorDatos(reporteSalud.getDesdeCuandoOloresB());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("desdeCuandoSabores");
		historicoClinicoDatos.setValorDatos(reporteSalud.getDesdeCuandoSabores());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("desdeCuandoSaboresB");
		historicoClinicoDatos.setValorDatos(reporteSalud.getDesdeCuandoSaboresB());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("dificultadRespirar");
		historicoClinicoDatos.setValorDatos(reporteSalud.getDificultadRespirar());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("dolorGarganta");
		historicoClinicoDatos.setValorDatos(reporteSalud.getDolorGarganta());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("esPrimeraVez");
		historicoClinicoDatos.setValorDatos(reporteSalud.getEsPrimeraVez());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("fiebreAyer");
		historicoClinicoDatos.setValorDatos(reporteSalud.getFiebreAyer());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("percibeOlores");
		historicoClinicoDatos.setValorDatos(reporteSalud.getPercibeOlores());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("percibeSabores");
		historicoClinicoDatos.setValorDatos(reporteSalud.getPercibeSabores());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("secrecionNasal");
		historicoClinicoDatos.setValorDatos(reporteSalud.getSecrecionNasal());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("sentisAngustia");
		historicoClinicoDatos.setValorDatos(reporteSalud.getSentisAngustia());
		historicoClinicoDatos.setTipoClinicoDato("salud_mental");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("sentisFiebre");
		historicoClinicoDatos.setValorDatos(reporteSalud.getSentisFiebre());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("sentisTristeDesanimado");
		historicoClinicoDatos.setValorDatos(reporteSalud.getSentisTristeDesanimado());
		historicoClinicoDatos.setTipoClinicoDato("salud_mental");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("signosSintomasDescritos");
		historicoClinicoDatos.setValorDatos(reporteSalud.getSignosSintomasDescritos());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("signosSintomasDescritosB");
		historicoClinicoDatos.setValorDatos(reporteSalud.getSignosSintomasDescritosB());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("sintomasEmpeoraron");
		historicoClinicoDatos.setValorDatos(reporteSalud.getSintomasEmpeoraron());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("sintomasMejoraron");
		historicoClinicoDatos.setValorDatos(reporteSalud.getSintomasMejoraron());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("tomasteTemperatura");
		historicoClinicoDatos.setValorDatos(reporteSalud.getTomasteTemperatura());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("tos");
		historicoClinicoDatos.setValorDatos(reporteSalud.getTos());
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		historicoClinicoDatos=new HistoricoClinicoDatos();
		historicoClinicoDatos.setHistoricoClinico(historicoClinico);
		historicoClinicoDatos.setNombreDatos("temperatura");
		if(reporteSalud.getTemperatura()!=null)
		{
			historicoClinicoDatos.setValorDatos(reporteSalud.getTemperatura().toString());
		}
		
		historicoClinicoDatos.setTipoClinicoDato("sintomas");
		historicoClinicoDatosDAO.save(historicoClinicoDatos);
		
		Iterator<String> recomendacionesIterator=reporteSalud.getResultadoRecomendaciones().iterator();
		
		DiagnosticoRecomendacion diagnosticoRecomendacion=new DiagnosticoRecomendacion();
		diagnosticoRecomendacion.setHistoricoClinico(historicoClinico);
		diagnosticoRecomendacion.setRecomendacionTipo("sintomas");
		diagnosticoRecomendacion.setRecomendacionValor(recomendacionesIterator.next());
		diagnosticoRecomendacionDAO.save(diagnosticoRecomendacion);
		
		DiagnosticoAccion diagnosticoAccion=new DiagnosticoAccion();
		diagnosticoAccion.setHistoricoClinico(historicoClinico);
		diagnosticoAccion.setDiagnosticoRecomendacion(diagnosticoRecomendacion);
		diagnosticoAccion.setTipoAccion("clasificacion_paciente");
		diagnosticoAccion.setValor(pacienteEstadoSalud.getClasificacionPaciente());
		diagnosticoAccion.setFechaHoraEjecucion(new Date());
		diagnosticoAccion.setEstadoEjecucion("ejecutado");
		diagnosticoAccion.setResultadoEjecucion("exitoso");
		diagnosticoAccionDAO.save(diagnosticoAccion);
		
		diagnosticoRecomendacion=new DiagnosticoRecomendacion();
		diagnosticoRecomendacion.setHistoricoClinico(historicoClinico);
		diagnosticoRecomendacion.setRecomendacionTipo("salud_mental");
		diagnosticoRecomendacion.setRecomendacionValor(recomendacionesIterator.next());
		diagnosticoRecomendacionDAO.save(diagnosticoRecomendacion);
		
		diagnosticoRecomendacion=new DiagnosticoRecomendacion();
		diagnosticoRecomendacion.setHistoricoClinico(historicoClinico);
		diagnosticoRecomendacion.setRecomendacionTipo("motivacional");
		diagnosticoRecomendacion.setRecomendacionValor(recomendacionesIterator.next());
		diagnosticoRecomendacionDAO.save(diagnosticoRecomendacion);
		
		return historicoClinico;
	}
	
	public ReporteSalud getLastReporteSalud(Long idPaciente)
    {
		return reporteSaludDAO.getLastReporteSalud(idPaciente);
    }
	
	public RegistroUbicacion getLastRegistro(Paciente paciente) {
		return registroUbicacionDAO.getLastRegistro(paciente);
	}
	
	public void updatePaciente(Paciente paciente)
	{
		pacienteDAO.update(paciente);
	}

	public String getMotivoIngreso(Paciente p) {
		TipoRegistro motivoIngreso = tipoRegistroDAO.getMotivoIngreso(p);
		return motivoIngreso != null ? motivoIngreso.getDescripcion() : null;
	}

	public Paciente getPacienteFromCedula(String cedula) {
		List<String> filters = new ArrayList<>();
		filters.add("cedula:" + cedula);
		List<Paciente> pacientes = pacienteDAO.getList(0, 1, filters, null, false, null);
		if (pacientes.isEmpty()) {
			return null;
		}
		return pacientes.get(0);

	}
}