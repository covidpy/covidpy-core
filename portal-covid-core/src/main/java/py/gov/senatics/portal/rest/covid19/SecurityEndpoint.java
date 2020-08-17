package py.gov.senatics.portal.rest.covid19;

import py.gov.senatics.portal.annotation.RolAllowed;
import py.gov.senatics.portal.annotation.Secured;
import py.gov.senatics.portal.business.UsuarioBC;
import py.gov.senatics.portal.business.covid19.PacienteBC;
import py.gov.senatics.portal.dto.covid19.admin.CambiarClaveDto;
import py.gov.senatics.portal.dto.covid19.admin.CredentialsDto;
import py.gov.senatics.portal.dto.covid19.admin.RecuperarClaveDTO;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;
import py.gov.senatics.portal.session.UserManager;
import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/covid19/seguridad")
public class SecurityEndpoint {

	@Inject
	private UsuarioBC usuarioBC;

	@Inject
	private PacienteBC pacienteBC;

	@Inject
	private UserManager userManager;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response doLogin(CredentialsDto credentials) {
		return this.usuarioBC.doLogin(credentials.getUsername(), credentials.getPassword(), credentials.getOneTimeToken(), credentials.getFcmRegistrationToken(),credentials.getSo());
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	@Path("/gestion")
	public Response doLoginGestion(CredentialsDto credentials) {
		return this.usuarioBC.doLoginGestion(credentials.getUsername(), credentials.getPassword());
	}

	@Path("recuperar-clave")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response recuperarClave(@Valid RecuperarClaveDTO recuperarClaveDTO) {
		return this.pacienteBC.recuperarClave(
				recuperarClaveDTO.getNroDocumento(),
				recuperarClaveDTO.getCelular()
		);
	}
	@Path("cambiar-clave")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	@Secured
	public Response recuperarClave(@Valid CambiarClaveDto cambiarClaveDto) {
		Usuario usuario = this.userManager.getRequestUser();
		CredentialsDto credentialsDto = new CredentialsDto();
		credentialsDto.setPassword(cambiarClaveDto.getPassword());
		credentialsDto.setPassword2(cambiarClaveDto.getPassword2());
		return this.usuarioBC.cambiarClave(
		        usuario,
                credentialsDto
		);
	}
	
	@Path("/tokenUnUso")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@RolAllowed("Paciente")
	public Response getTokenUnUso() {
		return Response.ok(" \""+usuarioBC.tokenUnUso(userManager.getRequestUser())+"\"").build();
	}

}
