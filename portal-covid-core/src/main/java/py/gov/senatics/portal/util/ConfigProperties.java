package py.gov.senatics.portal.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfigProperties {

	public static final String COVID_URL_APP = "covid19.dominioapp";
	public static final String COVID_NOMBRE_APP = "covid19.nombre_app";
	public static final String COVID_PATH_CAMBIAR_CLAVE = "covid19.path_cambiar_clave";
	public static final String COVID_REPORTE_MINUTOS_NO_REPORTADOS = "covid19.reporte.minutos_sin_reporte";	

	private Properties properties;

	public String getPropValues(String property) throws IOException {
		try {
			InputStream input = new FileInputStream(Config.CONFIG_PROPERTIES.concat("config.properties"));
			properties = new Properties();
			properties.load(new InputStreamReader(input, "UTF-8"));

			return properties.getProperty(property);
		} catch (Exception e) {
			System.out.println("ERROR: "+e.getMessage()); 
		}
		return null;
	}
	
}
