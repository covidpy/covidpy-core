
package py.gov.senatics.portal.util;


import py.gov.senatics.portal.dto.covid19.admin.ResponseDTO;
import py.gov.senatics.portal.persistence.covid19.admin.ConfiguracionDao;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

@RequestScoped
public class EmailSender {

	private String host;
	private String port;
	private String subject;
	private String cc;
	private String username;
	private String password;
	private String adminPyUrl;

	@Inject
	private ConfiguracionDao confDao;

	@Inject
	private Logger logger;

	@PostConstruct
	public void initBean() {

		Map<String, String> map = this.confDao.obtenerConfiguracionesMap();

		this.host = map.get("mail.smtp.host");
		this.port = map.get("mail.smtp.port");
		this.username = map.get("mail.smtp.user");
		this.password = map.get("mail.smtp.pass");
		this.adminPyUrl = map.get("adminportalpy.url");
		this.subject = "Sistema de administración del Portal Paraguay";

	}

	public ResponseDTO sendPasswordMessage(String to, String tokenReset, String nombre) {

		// Create Message Body

		StringBuilder bodyMessage = new StringBuilder();

		bodyMessage.append("Hola ").append(nombre).append(",<br>").append(
				"Haga clic en el enlace de abajo para restablecer la contrase&ntilde;a o copie y pegue el enlace en <br>")
				.append("la barra de direcciones de su navegador: <a href='").append(this.adminPyUrl)
				.append("cambiarClave/").append(tokenReset).append("'>").append(this.adminPyUrl).append("cambiarClave/")
				.append(tokenReset).append("</a><br><br>")
				.append("Sistema de administraci&oacute;n del Portal Paraguay - MITIC");

		logger.info("Enviando correo a: ".concat(to));

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		// properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);

		Authenticator auth = new SMTPAuthenticator(this.username, this.password);
		Session session = Session.getInstance(properties, auth);
		ResponseDTO res = new ResponseDTO();

		try {
			// Create a default MimeMessage object.
			// session.setDebug(true);

			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(username, "Portal Paraguay"));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set To: header field of the header.
			if (cc != null) {
				message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
			}
			// Set Subject: header field
			message.setSubject(subject);

			// Codigo para poder insertar una imagen al cuerpo del Email
			MimeMultipart multipart = new MimeMultipart("related");
			// Se setea el cuerpo del Html
			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setContent(bodyMessage.toString(), "text/html");
			// Se agrega al Mime Multipart
			multipart.addBodyPart(messageBodyPart);

			message.setContent(multipart);

			Transport.send(message);

			res.setStatus(200);
			res.setMessage("Correo electrónico enviado correctamente.");

			return res;

		} catch (MessagingException mex) {
			mex.printStackTrace();
			res.setMessage(mex.getMessage());
			res.setStatus(503);

			return res;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			res.setMessage(e.getMessage());
			res.setStatus(503);

			return res;
		}
	}

	private class SMTPAuthenticator extends Authenticator {

		private String username;
		private String password;

		public SMTPAuthenticator(String username, String password) {
			super();
			this.username = username;
			this.password = password;
		}

		public PasswordAuthentication getPasswordAuthentication() {

			return new PasswordAuthentication(this.username, this.password);
		}
	}

}
