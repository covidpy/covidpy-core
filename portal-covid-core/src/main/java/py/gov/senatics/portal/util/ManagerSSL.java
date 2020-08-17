package py.gov.senatics.portal.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ManagerSSL {	

	public static SSLContext getSslContext() throws NoSuchAlgorithmException, KeyManagementException {
		
		SSLContext sslContext = SSLContext.getInstance("TLSv1");
		
		KeyManager[] keyManagers = null;
		
		TrustManager[] trustManager = {new NoOpTrustManager()};
		
		SecureRandom secureRandom = new SecureRandom();		
		
		sslContext.init(keyManagers, trustManager, secureRandom);		
		
		return sslContext;
	}
	

	public static HostnameVerifier getHostNameVerifier() {
		return new NoOpHostnameVerifier();

	}

	public static class NoOpHostnameVerifier implements HostnameVerifier {
	    @Override
	    public boolean verify(String s, SSLSession sslSession) {
	        return true;
	    }
	}
	
	public static class NoOpTrustManager implements X509TrustManager {
	    @Override
	    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
	    }

	    @Override
	    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
	    }

	    @Override
	    public X509Certificate[] getAcceptedIssuers() {
	        return new X509Certificate[0];
	    }
	}

}
