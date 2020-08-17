
package py.gov.senatics.portal.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@RequestScoped
public class SmsSender {
	public static final String ACCOUNT_SID = "AQUI_COLOQUE_ACCOUNT_SID";
	public static final String AUTH_TOKEN = "AQUI_AUTH_TOKEN";
	public static final String DEFAULT_FROM_NUMBER = "AQUI_DEFAULT_FROM_NUMBER";
	private static Pattern patToken = Pattern.compile("" //
			+ ".*\"access_token\"\\s*:\\s*\"([^\"]+)\".*" //
			+ "");
	private static String tokenUrl = "AQUI_EL_TOKEN_URL_MM";
	private static String clientId = "AQUI_EL_CLIENT_ID";// clientId
	private static String clientSecret = "AQUI_EL_CLIENT_SECRET";// client secret
	private static String auth = clientId + ":" + clientSecret;
	private static String authentication = Base64.getEncoder().encodeToString(auth.getBytes());
	private static String channel = "AQUI_EL_CHANNEL";
	private static String tag = "MITIC";
	private static String uid = "AQUI_UID";
	private static String smsUrl = "AQUI_SMS_URL_MM";
	private static Pattern patResponse = Pattern.compile("" //
			+ ".*\"code\"\\s*:\\s*([0-9]+)" //
			+ ".*\"message\"\\s*:\\s*\"([^\"]+)\"" //
			+ ".*\"singleMessageId\"\\s*:\\s*\"([^\"]+)\"" //
			+ ".*");
	private static String SMS_OK = "0";
	private static String SEMAPHORO = "sepahoro";
	
	@Inject
	private Logger logger;
	
	/**public void sendSmsTwilio(String phoneTo, String phoneFrom, String message) {
		try
		{
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

		if(!phoneTo.startsWith("+"))
		{
			phoneTo="+"+phoneTo;
		}
	    Message messageTwillio = Message.creator(new PhoneNumber(phoneTo),
	        new PhoneNumber(phoneFrom), 
	        message).create();
		}
		catch(ApiException e)
		{
			throw new SmsException(e);
		}
	}**/
	
	/**
	 * Recupera el token para el envio del sms
	 * 
	 * @return
	 * @throws Exception
	 */
	private String getToken() {
		String content = "grant_type=client_credentials";
		BufferedReader reader = null;
		HttpsURLConnection connection = null;
		String returnValue = "";
		try {
			URL url = new URL(tokenUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization", "Basic " + authentication);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Accept", "application/json");

			PrintStream os = new PrintStream(connection.getOutputStream());
			os.print(content);
			os.close();
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = null;
			StringWriter out = new StringWriter(
					connection.getContentLength() > 0 ? connection.getContentLength() : 2048);
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			String response = out.toString();
			logger.log(Level.INFO, "sendSms|token|"+response);
			Matcher matcher = patToken.matcher(response);
			if (matcher.matches() && matcher.groupCount() > 0) {
				returnValue = matcher.group(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SmsException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			connection.disconnect();
		}
		return returnValue;
	}
	
	/**
	 * Envía el SMS.
	 * 
	 * @param tel:
	 *            número de teléfono
	 * @param mensaje:
	 *            mensaje a enviar
	 * @param referencia:
	 *            se puede usar para asociar el sms al identificador del mensaje en
	 *            la BD
	 * @return Si pudo enviar, Retorna el singleMessageId asignado al sms.
	 * @throws Exception:
	 *             si no pudo enviar, la exception tiene el mensaje del error.
	 */
	public String sendSMSMenuMovil(String tel, String mensaje, String referencia) {
		synchronized(SEMAPHORO)
		{
			String mensajeOriginal=mensaje;
		String token = this.getToken();

		String part="";
		
		String returnValue = "";
		
		int partCount=(int)(Math.ceil(mensaje.length()/160.0));
		int i=1;
		do
		{
			if(partCount>1)
			{
				part=""+i+"/"+partCount+" ";
			}
			if(mensaje.length()>160)
			{
				int indexOf=mensaje.indexOf("http");
				if(indexOf>-1&&indexOf<156)
				{
					part+=mensaje.substring(0, indexOf);
					mensaje=mensaje.substring(indexOf);
				}
				else
				{
					part+=mensaje.substring(0, 156);
					mensaje=mensaje.substring(156);
				}
			}
			else
			{
				part+=mensaje;
				mensaje=null;
			}
			i++;
		
		String content = "{ " + //
				"\"channel\": \"" + channel + "\"," + //
				"\"content\": \"" + part + "\"," + //
				"\"dlr\": true," + //
				"\"reference\": \"" + referencia + "\"," + //
				"\"tag\": \"" + tag + "\"," + //
				"\"to\": \"" + tel + "\"," + //
				"\"uid\": \"" + uid + "\"" + //
				"}";

		logger.log(Level.INFO, "sendSms|request|"+content);
		BufferedReader reader = null;
		HttpsURLConnection connection = null;
		
		try {

			URL url = new URL(smsUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setRequestProperty("Authorization", "Bearer " + token);
			connection.setRequestProperty("Accept", "application/json");

			PrintStream os = new PrintStream(connection.getOutputStream());
			os.print(content);
			os.close();
			InputStream inputStream=null;
			try
			{
				inputStream=connection.getInputStream();
			}
			catch(IOException e)
			{
				e.printStackTrace();
				inputStream=connection.getErrorStream();
			}
			
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			StringWriter out = new StringWriter(
					connection.getContentLength() > 0 ? connection.getContentLength() : 2048);
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			String response = out.toString();
			logger.log(Level.INFO, "sendSms|response|"+response);
			Matcher matcher = patResponse.matcher(response);
			if (matcher.matches() && matcher.groupCount() > 0) {

				String code = matcher.group(1);
				String message = matcher.group(2);
				String singleMessageId = matcher.group(3);

				if (code.compareTo(SMS_OK) != 0&&code.compareTo("2")!= 0&&code.compareTo("7") != 0&&code.compareTo("1016") != 0) {
					//sendSmsTwilio(tel, DEFAULT_FROM_NUMBER, mensajeOriginal);
					logger.info("ERRORSMSMENUMOVIL;;code="+code+";;"+tel+";;"+mensajeOriginal);
					return null;
				} else {
					returnValue = singleMessageId;
				}
			}
			else
			{
				//sendSmsTwilio(tel, DEFAULT_FROM_NUMBER, mensajeOriginal);
				logger.info("ERRORSMSMENUMOVIL;;noMatcher;;"+tel+";;"+mensajeOriginal);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			//sendSmsTwilio(tel, DEFAULT_FROM_NUMBER, mensajeOriginal);
			logger.info("ERRORSMSMENUMOVIL;;exception="+e.getLocalizedMessage()+";;"+tel+";;"+mensajeOriginal);
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			connection.disconnect();
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		}
		while(mensaje!=null);
		return returnValue;
	}
	}


}
