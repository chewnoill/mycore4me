package core_dos.server;

import java.io.IOException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import core_dos.shared.FieldVerifier;
import core_dos.shared.secret;

public class google_post {
	static String CHECK_TOKEN = "https://www.googleapis.com/oauth2/v1/tokeninfo?"+
			"access_token=";
	
	static String post(String access_token){
		
		HttpRequest hr;
		try {
			hr = Global.HRF.buildGetRequest(new GenericUrl(CHECK_TOKEN+access_token));
		
			HttpResponse response = hr.execute();
			HttpHeaders headers = hr.getHeaders();
			
			String file = response.parseAsString();
			
			return file;
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "fail";
	}

}
