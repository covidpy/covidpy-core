package py.gov.senatics.portal.rest.covid19.interceptor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;

import com.auth0.jwt.interfaces.DecodedJWT;

import py.gov.senatics.portal.annotation.RolAllowed;
import py.gov.senatics.portal.annotation.Secured;
import py.gov.senatics.portal.cache.SessionCache;
import py.gov.senatics.portal.dto.covid19.admin.ResponseDTO;
import py.gov.senatics.portal.dto.covid19.admin.Session;
import py.gov.senatics.portal.session.TokenManager;

@Provider
@ServerInterceptor
public class RESTSecurityInterceptor implements ContainerRequestFilter {

	@Inject
	private TokenManager tokenManager;

	@Inject
	private SessionCache sessionCache;

	public static final String AUTHORIZATION_HEADER = "Authorization";

	public static final Pattern PATTERN = Pattern.compile("^Bearer\\s");

	private static final ServerResponse UNAUTHORIZED = new ServerResponse(
			new ResponseDTO("Debe iniciar sesión con usuario válido", 401), 401, new Headers<Object>());

	private static final ServerResponse FORBIDDEN = new ServerResponse(
			new ResponseDTO("El usuario no tiene los permisos suficientes para realizar esta operación", 403), 403,
			new Headers<Object>());

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// TODO Auto-generated method stub

		final ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext
				.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");

		final MultivaluedMap<String, String> headers = requestContext.getHeaders();

		final Method method = methodInvoker.getMethod();

		if (method.isAnnotationPresent(Secured.class) || method.getDeclaringClass().isAnnotationPresent(Secured.class)) {

			String authHeader = headers.getFirst(AUTHORIZATION_HEADER);
			
			if(authHeader==null)
			{
				Cookie cookie=requestContext.getCookies().get("Authorization");
				if(cookie!=null)
				{
					authHeader = "Bearer "+cookie.getValue();
				}
			}

			if (authHeader == null || authHeader.isEmpty()) {
				//System.out.println("UNAUTHORIZED Empty");
				requestContext.abortWith(UNAUTHORIZED);
				return;
			}

			final DecodedJWT jwtToken = this.verifyAndDecodeToken(authHeader);

			if (jwtToken == null) {
				//System.out.println("UNAUTHORIZED decode");
				requestContext.abortWith(UNAUTHORIZED);
				return;
			}

			final Session s = this.sessionCache.getSession(authHeader.split("\\s")[1]);
			if (s == null) {
				requestContext.abortWith(UNAUTHORIZED);
				return;
			}

			if (method.isAnnotationPresent(RolAllowed.class)) {

				Boolean hasRol = s.hasRol(method.getAnnotation(RolAllowed.class).value());

				if (!hasRol) {
					//System.out.println("FORBIDDEN");
					requestContext.abortWith(FORBIDDEN);
					return;
				}
			}

			requestContext.getHeaders().add("sessionId", jwtToken.getSubject());

		}

	}

	private DecodedJWT verifyAndDecodeToken(String authHeader) {

		Matcher m = PATTERN.matcher(authHeader);

		if (!m.find())
			return null;

		return this.tokenManager.verify(authHeader.split("\\s")[1]);
	}

}
