package core_dos.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.http.entity.StringEntity;

import com.google.api.client.googleapis.json.JsonCContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.JsonEncoding;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.services.calendar.Calendar;
import com.google.gwt.json.client.JSONObject;

import core_dos.shared.FieldVerifier;
import core_dos.shared.secret;

public class google_post {
	static String CHECK_TOKEN = "https://www.googleapis.com/oauth2/v1/tokeninfo?";
	static String BASE_CAL = "https://www.googleapis.com/calendar/v3";
	//INSERT: POST https://www.googleapis.com/calendar/v3/calendars
	//REQUIRES SCOPE: https://www.googleapis.com/auth/calendar
	
	
	static String GET = "/calendars";
	
	static String post(String access_token){
		
		HttpRequest hr;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");                  
		headers.set("Authorization","Bearer "+access_token);
		//headers.set("ClientId",secret.CLIENT_ID);
		try {
			//check/accept token
			System.out.println("check auth");
			hr = Global.HRF.buildGetRequest(
					new GenericUrl(CHECK_TOKEN+
					"access_token="+access_token));
		
			HttpResponse response = hr.execute();
			
			String file;
			System.out.println("auth: "+response.parseAsString());
			try {
			//check if core calendar already exist
			//calendar id is not "core", will look like this:
			//  figcu3d6ao4kebhu148hat739o@group.calendar.google.com
				System.out.println("check calID");
			hr = Global.HRF.buildGetRequest(
					new GenericUrl(BASE_CAL+GET+"/abc"+
					"?key="+secret.API_KEY));
			hr.setHeaders(headers);
			
			response = hr.execute();
			
			file = response.parseAsString();
			
			System.out.println("calID result: "+file);
			} catch (com.google.api.client.http.HttpResponseException e) {
				System.out.println(e.getMessage());
				//no calendar found, create one name it "core"
				HashMap<String,String> hm = new HashMap<String,String>();
				hm.put("summary", "core");
				JsonHttpContent update = new JsonHttpContent(Global.JF, 
						hm);
				
				System.out.println("update to string:"+update+"\n\n---");
				hr = Global.HRF.buildPostRequest(new GenericUrl(BASE_CAL+GET+
						"?key="+secret.API_KEY),
						update);
				
				hr.setHeaders(headers);
				
				System.out.println("encoding: " + hr.getContent().getType());
				response = hr.execute();
				file = response.parseAsString();
				System.out.println(file);
				JsonObjectParser JOP = Global.JF.createJsonObjectParser();
				
			}
			
			
			return file;
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "fail";
	}

}
