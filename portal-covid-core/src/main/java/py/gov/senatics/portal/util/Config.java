package py.gov.senatics.portal.util;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import py.gov.senatics.portal.dto.covid19.DatosUbicacionDTO;

public class Config {
	
	public static final String CONFIG_PROPERTIES = "COLOQUE_AQUI_PATH_DIRECTORIO_CONFIG_PROPERTIES";
	public static final String URL_RECAPTCHA = "https://www.google.com/recaptcha/api/siteverify";
	public static final String URL_SECURITY = "COLOQUE_AQUI_PATH";
	public static final String URL_API_IDENTIFICACIONES = "COLOQUE_AQUI_PATH";

	public String getPropValues(String property) throws IOException {
		try {

			InputStream input = new FileInputStream(CONFIG_PROPERTIES.concat("config.properties"));

			Properties properties = new Properties();

			properties.load(input);

			return properties.getProperty(property);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public boolean insertLocation(DatosUbicacionDTO datos) {
		boolean resp = false;
		FileWriter flwriter = null;
		try {
			flwriter = new FileWriter("PATH_ARCHIVO", true);
			BufferedWriter bfwriter = new BufferedWriter(flwriter);
			bfwriter.write(datos.toString());
			bfwriter.close();
			resp = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (flwriter != null) {
				try {
					flwriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return resp;
	}

}
