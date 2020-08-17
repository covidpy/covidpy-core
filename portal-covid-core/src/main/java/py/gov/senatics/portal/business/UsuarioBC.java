package py.gov.senatics.portal.business;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Result;
import py.gov.senatics.portal.cache.ConfiguracionCache;
import py.gov.senatics.portal.cache.SessionCache;
import py.gov.senatics.portal.dto.covid19.admin.CredentialsDto;
import py.gov.senatics.portal.dto.covid19.admin.ResponseDTO;
import py.gov.senatics.portal.dto.covid19.admin.Session;
import py.gov.senatics.portal.dto.covid19.admin.TableDTO;
import py.gov.senatics.portal.modelCovid19.admin.LoginAutomatico;
import py.gov.senatics.portal.modelCovid19.admin.Rol;
import py.gov.senatics.portal.persistence.covid19.admin.LoginAutomaticoDAO;
import py.gov.senatics.portal.persistence.covid19.admin.RolDao;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;
import py.gov.senatics.portal.persistence.covid19.admin.UsuarioDao;
import py.gov.senatics.portal.session.TokenManager;
import py.gov.senatics.portal.util.EmailSender;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequestScoped
public class UsuarioBC {

	@Inject
	private UsuarioDao usuarioDao;

	@Inject
	private RolDao rolDao;

	@Inject
	private TokenManager tokenManager;

	@Inject
	private EmailSender emailSender;

	@Inject
	private SessionCache sessionCache;

	@Inject
	private Logger logger;

	@Inject
	private LoginAutomaticoDAO loginAutomaticoDAO;

	@Inject
	private ConfiguracionCache conf;

	public Response filtrarUsuarios(int pageSize, int first, String sortField, boolean sortAsc, String filter) {

		TableDTO<Usuario> lista = new TableDTO<>();

		lista.setLista(this.usuarioDao.filtrarUsuarios(pageSize, first, sortField, sortAsc, filter));
		lista.setTotalRecords(this.usuarioDao.obtenerCantidadDeFilas(filter));

		return Response.status(Status.OK).entity(lista).build();
	}

	public Response doLogin(String username, String password, String oneTimeToken, String fcmRegistrationToken, String sistemaOperativo) {
		Usuario u;
		HttpServletRequest servletRequest = CDI.current().select(HttpServletRequest.class).get();
		HashMap<String, String> hashMap=new HashMap<>();
		if (oneTimeToken == null || oneTimeToken.trim().isEmpty()) {
			
			hashMap.put("sistema", "app");
			hashMap.put("ipUsuario", servletRequest.getRemoteAddr());
			hashMap.put("User-Agent", servletRequest.getHeader("User-Agent"));

			if (username == null || username.isEmpty())
			{
				hashMap.put("reason", "username null");
				logger.log(Level.INFO, hashMap.toString());
				return Response.status(Status.UNAUTHORIZED).build();
			}
			
			hashMap.put("username", username);
				

			if (password == null || password.isEmpty())
			{
				hashMap.put("reason", "pass null");
				logger.log(Level.INFO, hashMap.toString());
				return Response.status(Status.UNAUTHORIZED).build();
			}
			
			u = this.usuarioDao.obtenerUsuarioLogin(username);

			if (u == null)
			{
				hashMap.put("reason", "user no exist");
				logger.log(Level.INFO, hashMap.toString());
				return Response.status(Status.UNAUTHORIZED).build();
			}

			Boolean correctPass = this.verificarPassword(password, u.getPassword());

			if (!correctPass)
			{
				hashMap.put("reason", "incorrect pass");
				logger.log(Level.INFO, hashMap.toString());
				return Response.status(Status.UNAUTHORIZED).build();
			}
			else if(fcmRegistrationToken!=null&&!fcmRegistrationToken.isEmpty())
			{
				u.setFcmRegistrationToken(fcmRegistrationToken);
				if(sistemaOperativo!=null&&!sistemaOperativo.isEmpty())
				{
					u.setSistemaOperativo(sistemaOperativo);
				}
				usuarioDao.actualizar(u);
			}
				
		} else {
			hashMap.put("token", oneTimeToken);
			LoginAutomatico loginToken = this.loginAutomaticoDAO.getByToken(oneTimeToken);
			if (loginToken == null) {
				hashMap.put("reason", "token not exist");
				logger.log(Level.INFO, hashMap.toString());
				return Response.status(Status.UNAUTHORIZED).build();
			}
			long duration = Long.parseLong(this.conf.get("covid19.onetimetoken.seconds_duration"));
			long diff = new Date().getTime() - loginToken.getTimestampCreacion().getTime();
			if (diff > duration * 1000) {
				loginToken.setEstado("inactivo");
				hashMap.put("reason", "token expired");
				logger.log(Level.INFO, hashMap.toString());
				return Response.status(Status.UNAUTHORIZED).build();
			}

			u = loginToken.getUsuario();
			loginToken.setEstado("inactivo");
			this.loginAutomaticoDAO.update(loginToken);
		}

		hashMap.put("reason", "login succesful");
		logger.log(Level.INFO, hashMap.toString());
		
		u.setPassword(null);

		Session session = new Session();

		List<Rol> roles = this.usuarioDao.obtenerRoles(u.getId());

		if (roles != null && !roles.isEmpty()) {

			for (Rol r : roles)
				r.setPermisos(this.rolDao.obtenerPermisos(r.getId()));

		}
		u.setRols(roles);

		session.setUsuario(u);

		session.setToken(this.tokenManager.generateToken(u));

		this.sessionCache.putSession(session.getToken(), session);

		NewCookie cookie=new NewCookie("Authorization", session.getToken());

		return Response.ok().entity(session).cookie(cookie).build();

	}
	
	public Response doLoginGestion(String username, String password) {
		Usuario u;
		HttpServletRequest servletRequest = CDI.current().select(HttpServletRequest.class).get();
		HashMap<String, String> hashMap=new HashMap<>();

		hashMap.put("sistema", "gestion");
		hashMap.put("ipUsuario", servletRequest.getRemoteAddr());
		hashMap.put("User-Agent", servletRequest.getHeader("User-Agent"));

		if (username == null || username.isEmpty())
		{
			hashMap.put("reason", "username null");
			logger.log(Level.INFO, hashMap.toString());
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		hashMap.put("username", username);

		if (password == null || password.isEmpty())
		{
			hashMap.put("reason", "pass null");
			logger.log(Level.INFO, hashMap.toString());
			return Response.status(Status.UNAUTHORIZED).build();
		}

			u = this.usuarioDao.obtenerUsuarioLoginGestion(username);

			if (u == null)
			{
				hashMap.put("reason", "user no exist");
				logger.log(Level.INFO, hashMap.toString());
				return Response.status(Status.UNAUTHORIZED).build();
			}

			Boolean correctPass = this.verificarPassword(password, u.getPassword());

			if (!correctPass)
			{
				hashMap.put("reason", "incorrect pass");
				logger.log(Level.INFO, hashMap.toString());
				return Response.status(Status.UNAUTHORIZED).build();
			}
			
			hashMap.put("reason", "login succesful");
			logger.log(Level.INFO, hashMap.toString());

		u.setPassword(null);

		Session session = new Session();

		List<Rol> roles = this.usuarioDao.obtenerRoles(u.getId());

		if (roles != null && !roles.isEmpty()) {

			for (Rol r : roles)
				r.setPermisos(this.rolDao.obtenerPermisos(r.getId()));

			u.setRols(roles);
		}

		session.setUsuario(u);

		session.setToken(this.tokenManager.generateToken(u));

		this.sessionCache.putSession(session.getToken(), session);

		NewCookie cookie=new NewCookie("Authorization", session.getToken(),"/",null,null,3600,false);

		return Response.ok().entity(session).cookie(cookie).build();

	}

	public Response actualizar(Usuario req) {

		Usuario u = this.usuarioDao.obtenerUsuarioPorNombreUsuarioNotIn(req.getUsername(), req.getId());

		if (u != null)
			return Response.status(Status.CONFLICT)
					.entity(new ResponseDTO("Ya existe un usuario con el nombre de usuario ingresado", 409)).build();

		this.usuarioDao.actualizar(req);

		return Response.status(Status.OK).entity(new ResponseDTO("Usuario actualizado", 200)).build();

	}

	public Response crear(Usuario req) {

		Usuario u = this.usuarioDao.obtenerUsuarioPorNombreUsuarioOCedula(req.getUsername(), req.getCedula());

		if (u != null)
			return Response.status(Status.CONFLICT)
					.entity(new ResponseDTO("Ya existe un usuario con el nombre de usuario o cedula ingresada", 409))
					.build();

		String tokenReset = this.generarUUID() + req.getCedula();

		req.setTokenReset(tokenReset);

		this.usuarioDao.crear(req);

		this.emailSender.sendPasswordMessage(req.getUsername(), tokenReset, req.getNombre());

		return Response.status(Status.OK).entity(new ResponseDTO("Usuario creado", 200)).build();

	}

	public Response actualizarRoles(Usuario req) {
		this.usuarioDao.actualizarRoles(req);
		return Response.status(Status.OK).entity(new ResponseDTO("Roles actualizados, los cambios se verán reflejados en el próximo inicio de sesión", 200)).build();
	}

	public Response enviarCorreoParaCambiarClave(Usuario req) {

		Usuario u = this.usuarioDao.obtenerUsuarioPorNombreUsuario(req.getUsername());

		if (u == null) {
			logger.warning("Usuario no existe, por lo tanto no se enviará ningún correo");
			return Response.status(Status.OK)
					.entity(new ResponseDTO("Se enviará un correo electrónico, al usuario ingresado.", 200)).build();
		}

		if (!u.getActivo()) {
			logger.warning("El usuario no se encuentra activo, por lo tanto no se enviará ningún correo");
			return Response.status(Status.OK)
					.entity(new ResponseDTO("Se enviará un correo electrónico, al usuario ingresado.", 200)).build();
		}

		String tokenReset = this.generarUUID() + u.getCedula();

		this.usuarioDao.updateTokenReset(u.getId(), tokenReset);

		this.emailSender.sendPasswordMessage(u.getUsername(), tokenReset, u.getNombre());

		return Response.status(Status.OK)
				.entity(new ResponseDTO("Se enviará un correo electrónico, al usuario ingresado.", 200)).build();
	}

	public Response cambiarClave(Usuario u, CredentialsDto c) {

		if (u == null) {
			logger.warning("Usuario no existe.");
			return Response.status(Status.OK).entity(new ResponseDTO("Contraseña actualizada.", 200)).build();

		}

		if (!u.getActivo()) {
			logger.warning("El usuario no se encuentra activo. Por lo tanto no se cambiará la contraseña");
			return Response.status(Status.OK).entity(new ResponseDTO("Contraseña actualizada.", 200)).build();
		}

		if (c.getPassword() == null || c.getPassword().isEmpty() || c.getPassword2() == null
				|| c.getPassword2().isEmpty()) {
			logger.warning("Las claves no pueden ser vacías. Por lo tanto no se cambiará la contraseña");
			return Response.status(Status.OK).entity(new ResponseDTO("Contraseña actualizada.", 200)).build();
		}

		if (!c.getPassword().equalsIgnoreCase(c.getPassword2())) {
			logger.warning("Las contraseñas no coinciden. Por lo tanto no se cambiará la contraseña");
			return Response.status(Status.OK).entity(new ResponseDTO("Contraseña actualizada.", 200)).build();
		}

		u.setPassword(this.generarClave(c.getPassword()));

		this.usuarioDao.actualizarClave(u);

		return Response.status(Status.OK).entity(new ResponseDTO("Contraseña actualizada.", 200)).build();

	}

	private Boolean verificarPassword(String password, String hashBD) {

		Result result = BCrypt.verifyer().verify(password.toCharArray(), hashBD);

		return result.verified;

	}

	public String generarClave(String clave) {
		return BCrypt.withDefaults().hashToString(10, clave.toCharArray());
	}

	private String generarUUID() {
		return UUID.randomUUID().toString();
	}
	
	public Usuario findByCedula(String cedula)
	{
		return usuarioDao.getUsuarioBycedula(cedula);
	}
	
	public String tokenUnUso(Usuario usuario)
	{
		LoginAutomatico loginAutomatico=new LoginAutomatico();
		loginAutomatico.setUsuario(usuario);
		loginAutomatico.setTimestampCreacion(new Date());
		loginAutomatico.setEstado("activo");
		loginAutomatico.setToken(UUID.randomUUID().toString().substring(0, 8));
		loginAutomaticoDAO.save(loginAutomatico);
		return loginAutomatico.getToken();
	}
	
	public void actualizarClave(Usuario usuario)
	{
		usuarioDao.actualizarClave(usuario);
	}

}
