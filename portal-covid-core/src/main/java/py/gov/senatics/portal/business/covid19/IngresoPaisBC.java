package py.gov.senatics.portal.business.covid19;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RequestScoped
public class IngresoPaisBC {
	
	public boolean isCaptchaValid(String response) {
		//System.out.println(response);
		boolean valid= false;
		try {
			String url = "https://www.google.com/recaptcha/api/siteverify",
	               params = "secret=AQUI_SECRET" + "&response=" + response;

	        HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
	        
	        http.setDoOutput(true);
	        http.setRequestMethod("POST");
	        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	        
	        OutputStream out = http.getOutputStream();
	        out.write(params.getBytes("UTF-8"));
	        out.flush();
	        out.close();

	        InputStream res = http.getInputStream();
	        BufferedReader rd = new BufferedReader(new InputStreamReader(res, "UTF-8"));
	        
	        StringBuilder sb = new StringBuilder();
	        int cp;
	        while ((cp = rd.read()) != -1) {
	            sb.append((char) cp);
	        }
	        JsonParser parser = new JsonParser();
	        JsonObject json = (JsonObject) parser.parse(sb.toString());
	        
	        res.close();
	        
	        if(json.get("success").getAsBoolean() && json.get("score").getAsFloat() > 0.5) {
	        	valid = true;
	        }
	        
		} catch (Exception e) {
	        //e.printStackTrace();
	    }
		return valid;
	}

	public Boolean chequearCaptcha(String token) throws ClientProtocolException, IOException {
		
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://google.com/recaptcha/api/");
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("secret", "AQUI_SECRET"));
		params.add(new BasicNameValuePair("response", token));
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		
		HttpResponse response = httpclient.execute(httppost);
		
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			System.out.println(entity.getContent());
		    /*try (InputStream instream = entity.getContent()) {
		    	
		    }*/
		}
		
		return true;
	}

}