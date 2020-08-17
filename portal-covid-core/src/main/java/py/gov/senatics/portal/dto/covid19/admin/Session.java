package py.gov.senatics.portal.dto.covid19.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import py.gov.senatics.portal.modelCovid19.admin.Rol;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class Session {

	private Usuario usuario;

	private String token;

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Boolean hasPermission(String... name) {
		return false;
	}

	public boolean hasRol(String... name) {

		for (int i = 0; i < name.length; i++) {
			for (Rol rol : this.usuario.getRols()) {
				if (name[i].equalsIgnoreCase(rol.getNombre()))
					return true;
			}
		}
		return false;
	}

}
