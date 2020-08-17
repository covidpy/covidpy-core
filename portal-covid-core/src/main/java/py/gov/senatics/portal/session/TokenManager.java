package py.gov.senatics.portal.session;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import py.gov.senatics.portal.modelCovid19.admin.Rol;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.logging.Logger;

@ApplicationScoped
public class TokenManager {

	@Inject
	private Logger logger;

	private Algorithm algorithm;
	private JWTVerifier verifier;

	@PostConstruct
	public void initSessionManager() {

		algorithm = Algorithm.HMAC256("AQUI_EL_SECRET");
		verifier = JWT.require(algorithm).build();
		logger.info("Starting Token manager");

	}

	@PreDestroy
	public void destroySessionManager() {
		logger.info("Token manager destroyed");
	}

	public void start() {

		logger.info("Token manager started");
	}

	public DecodedJWT verify(String token) {
		try {

			return this.verifier.verify(token);

		} catch (JWTVerificationException e) {
			// TODO: handle exception
			logger.warning(e.getMessage());
			return null;
		}
	}

	public String generateToken(Usuario usuario) {

		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.DAY_OF_YEAR, 15);

		return JWT.create().withExpiresAt(calendar.getTime()).withIssuer("AQUI_ISSUER")
				.withSubject(usuario.getId().toString()).sign(algorithm);
	}

	public String generateToken(Usuario usuario, Rol[] rols) {

		if (rols.length == 0)
			return null;

		Calendar calendar = Calendar.getInstance();
		
		calendar.add(Calendar.MINUTE, 60);

		final String[] items = new String[rols.length];

		for (int i = 0; i < rols.length; i++)
			items[i] = rols[i].getNombre();

		return JWT.create().withExpiresAt(calendar.getTime()).withIssuer("AQUI_ISSUER")
				.withSubject(usuario.getId().toString()).withArrayClaim("rols", items).sign(algorithm);
	}

}
