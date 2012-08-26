package core_dos.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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
	static String CAL_NAME = "Core";
	//INSERT: POST https://www.googleapis.com/calendar/v3/calendars
	//REQUIRES SCOPE: https://www.googleapis.com/auth/calendar
	static String GET = "/calendars";
	private HttpHeaders headers;
	private String cal_id;
	public google_post(){
		headers = new HttpHeaders();
	}
	
	public String post(String access_token, 
			ArrayList<HashMap<String,Object>> events){
		String auth = checkToken(access_token);
		String calendar = findCalendar(CAL_NAME);
		//if calendar not found
		//create
		calendar = createCal(CAL_NAME);
		//get calId from calObject
		/*
		 * encoding: application/json; charset=UTF-8
		 *	{
		 *	 "kind": "calendar#calendar",
		 *	 "etag": "\"y3Ec45PWr056tgI9oyp5WeT3BzE/yM6-13YtUCFGK9GiFAQQXaZInn0\"",
		 *	 "id": "0moigj30b8euplcjun8lef24fk@group.calendar.google.com",
		 *	 "summary": "core"
		 *	}
		 */
		String pat_cal_id_start = "\"id\": \"";
		String pat_cal_id_end = "\",";
		int cal_id_start = calendar.indexOf(pat_cal_id_start)+pat_cal_id_start.length();
		int cal_id_end = calendar.indexOf(pat_cal_id_end,cal_id_start);
		String calId = calendar.substring(cal_id_start,cal_id_end);
		
		String results = insertEvents(calId,events);
		return results;
	}
	private String checkToken(String access_token){
		headers.set("Content-Type", "application/json");                  
		headers.set("Authorization","Bearer "+access_token);
		
		HttpRequest hr;
		HttpResponse response;
		String output;
		//headers.set("ClientId",secret.CLIENT_ID);
		
		//check/accept token
		System.out.println("check auth");
		try {
			hr = Global.HRF.buildGetRequest(
					new GenericUrl(CHECK_TOKEN+
					"access_token="+access_token));
			response = hr.execute();
			System.out.println("auth: "+response.parseAsString());
			return response.parseAsString();
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		
	}

	/**TODO - does not work
	 * 
	 * @param summary
	 * @return
	 */
	private String findCalendar(String summary){
		//check if core calendar already exist
		//calendar id is not "core", will look like this:
		//  figcu3d6ao4kebhu148hat739o@group.calendar.google.com
		HttpRequest hr;
		HttpResponse response;
		String output;
		System.out.println("check calID");
		try {
			hr = Global.HRF.buildGetRequest(
					new GenericUrl(BASE_CAL+GET+"/abc"+
					"?key="+secret.API_KEY));
			hr.setHeaders(headers);
			response = hr.execute();
			output = response.parseAsString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		
		System.out.println("calID result: "+output);
		return output;
	}
	
	private String createCal(String summary){
		//no calendar found, create one name it "core"
		
		HttpRequest hr;
		HttpResponse response;
		String output;
		HashMap<String,String> hm = new HashMap<String,String>();
		hm.put("summary", "core");
		JsonHttpContent update = new JsonHttpContent(Global.JF, 
				hm);
		//{"summary":"core"}
		
		System.out.println("update to string:"+update+"\n\n---");
		try {
			hr = Global.HRF.buildPostRequest(new GenericUrl(BASE_CAL+GET+
					"?key="+secret.API_KEY),
					update);

			hr.setHeaders(headers);
			
			System.out.println("encoding: " + hr.getContent().getType());
			response = hr.execute();
			
			output = response.parseAsString();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		System.out.println(output);
		
		return output;
	}
	
	
	private String insertEvents(String calId,ArrayList<HashMap<String,Object>> events){

		for(HashMap<String,Object> ev : events ){
			insertEvent(calId,ev);
		}
		return null;
	}
	/**
	 * TODO
	 * @param calId
	 * @param event
	 * @return
	 */
	private String insertEvent(String calId,Object event){
		HttpRequest hr;
		HttpResponse response;
		String output;
		
		JsonHttpContent update = new JsonHttpContent(Global.JF, 
				event);
		
		System.out.println("update to string:\n");
		try {
			update.writeTo(System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("\n\n---");
		/*
		try {
			hr = Global.HRF.buildPostRequest(new GenericUrl(BASE_CAL+GET+
					"?key="+secret.API_KEY),
					update);

			hr.setHeaders(headers);
			
			System.out.println("encoding: " + hr.getContent().getType());
			response = hr.execute();
			
			output = response.parseAsString();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		System.out.println(output);
		*/
		return null;
	}
}
