package py.gov.senatics.portal.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequestScoped
public class AuthDAO {
	
	@Inject
	private Config config;

	public String obtenerToken() throws IOException {

		Client client = ClientBuilder.newClient().register(new HashMap<String, Object>());

		Map<String, String> params = new HashMap<>();
		
		params.put("username", this.config.getPropValues("SII_USERNAME"));
		params.put("password", this.config.getPropValues("SII_PASSWORD"));
		
		String security = client
				.target(this.config.getPropValues("URL_SERVER") + Config.URL_SECURITY)
				.request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(new ObjectMapper().writeValueAsString(params), MediaType.APPLICATION_JSON), String.class);

		return security;
	}

	public String obtenerDatos(String url, String... params) throws Exception {
		
		String security = obtenerToken();

		String retorno = "";
		
		ObjectMapper mapper = new ObjectMapper();
		
		Map<String, Object> map = mapper.readValue(security, Map.class);

		Boolean success = (Boolean) map.get("success");
		
		if (success) {
			
			String authorization = "Bearer " + map.get("token").toString();
			
			SSLContext sslContext = ManagerSSL.getSslContext();
			 
		    HostnameVerifier allHostsValid = ManagerSSL.getHostNameVerifier();
		    
		    final Client client = ClientBuilder.newBuilder()
	                .sslContext(sslContext)
	                .hostnameVerifier(allHostsValid)
	                .register(new HashMap<String, Object>())
	                .build();
			
			String dataParams = "";
			
			if(params != null && params.length > 0) {
				
				int c = 0;
				
				for(String param : params) {
					
					if(c > 0) {
						dataParams+= "/";
					}
					
					dataParams+= param;
					c++;
				}
			}
						
			WebTarget resource = client.target(this.config.getPropValues("URL_SERVER") + url + dataParams);
			retorno = resource.request()
					.header("Accept", MediaType.APPLICATION_JSON)
					.header("Content-Type", MediaType.APPLICATION_JSON)
					.header("Authorization", authorization)
					.get(String.class);
		}
		
		return retorno;
	}
	

}


