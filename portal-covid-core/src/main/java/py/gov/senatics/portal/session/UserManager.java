package py.gov.senatics.portal.session;


import org.jboss.resteasy.spi.ResteasyProviderFactory;
import py.gov.senatics.portal.cache.SessionCache;
import py.gov.senatics.portal.dto.covid19.admin.Session;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import java.util.Arrays;

import static py.gov.senatics.portal.rest.covid19.interceptor.RESTSecurityInterceptor.AUTHORIZATION_HEADER;


@RequestScoped
public class UserManager {

    private static final String AUTH_COOKIE_NAME = "Authorization";

    @Inject
    private SessionCache sessionCache;

    public Usuario getRequestUser() {
        final Session s = this.getCurrentSession();
        return s != null ? s.getUsuario() : null;
    }

    private Session getCurrentSession() {
        HttpServletRequest request = ResteasyProviderFactory.getContextData(HttpServletRequest.class);
        HttpHeaders httpHeaders = ResteasyProviderFactory.getContextData(HttpHeaders.class);

        String authHeader = httpHeaders.getHeaderString(AUTHORIZATION_HEADER);

        if (authHeader == null)
        {
        	if(request.getCookies()==null)
        	{
        		return null;
        	}
            Cookie cookieAuth = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals(AUTH_COOKIE_NAME)).findFirst().orElse(null);
            if (cookieAuth != null) {
                authHeader = "Bearer " + cookieAuth.getValue();
            } else {
                return null;
            }
        }

        return this.sessionCache.getSession(authHeader.split("\\s")[1]);
    }

}
