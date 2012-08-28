package core_dos.server;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.JsonNode;


import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.JsonParser;
import com.google.api.services.calendar.Calendar;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gwt.core.client.JsArray;

import core_dos.shared.FieldVerifier;
import core_dos.shared.secret;

public class google_post {
	static String CHECK_TOKEN = "https://www.googleapis.com/oauth2/v1/tokeninfo?";
	static String BASE_CAL = "https://www.googleapis.com/calendar/v3";
	static String CAL_NAME = "Core";
	//INSERT: POST https://www.googleapis.com/calendar/v3/calendars
	//REQUIRES SCOPE: https://www.googleapis.com/auth/calendar
	static String GET = "/calendars";
	static String LIST = "/users/me/calendarList";
	private HttpHeaders headers;
	private String cal_id;
	public google_post(){
		headers = new HttpHeaders();
	}
	
	public String post(String access_token, 
			ArrayList<JsEvent> events){
		String auth = checkToken(access_token);
		cal_id = findCalendar(CAL_NAME);
		//if calendar not found
		//create
		System.out.println("cal_id1: "+cal_id);
		if(cal_id==null){
			cal_id = createCal(CAL_NAME);
		}
		System.out.println("cal_id2: "+cal_id);
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
		
		String results = insertEvents(cal_id,events);
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
					new GenericUrl(BASE_CAL+LIST+
					"?key="+secret.API_KEY));
			hr.setHeaders(headers);
			response = hr.execute();
			output = response.parseAsString();
			//System.out.println(output);
			Gson gson = new Gson();
			JsCalendarList cl = gson.fromJson(output, JsCalendarList.class);
			for (JsCalendar cal: cl.items){
				
				if(cal.summary.equals(CAL_NAME)){
					return cal.id;
				}
			}
	        
			
		} catch (IOException e) {
			// 
			e.printStackTrace();
			return e.getMessage();
		} catch (Exception e) {
			System.out.println("ERROR======"+e.getMessage());
			e.printStackTrace();
		}
		
			
		return null;
	}
	
	private String createCal(String summary){
		//no calendar found, create one name it "core"
		
		HttpRequest hr;
		HttpResponse response;
		String output;
		HashMap<String,String> hm = new HashMap<String,String>();
		hm.put("summary", CAL_NAME);
		JsonHttpContent update = new JsonHttpContent(Global.JF, 
				hm);
		
		System.out.println("update to string:"+update+"\n\n---");
		try {
			hr = Global.HRF.buildPostRequest(new GenericUrl(BASE_CAL+GET+
					"?key="+secret.API_KEY),
					update);

			hr.setHeaders(headers);
			
			System.out.println("encoding: " + hr.getContent().getType());
			response = hr.execute();
			output = response.parseAsString();
			
			//System.out.println(output);
			Gson gson = new Gson();
			JsCalendar c = gson.fromJson(output, JsCalendar.class);
			return c.id;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	private String insertEvents(String calId,ArrayList<JsEvent> events){

		for(JsEvent ev : events ){
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
	private String insertEvent(String calId,JsEvent event){
		HttpRequest hr;
		HttpResponse response;
		String output;
		
		JsonHttpContent update = new JsonHttpContent(Global.JF, 
				event.toJSON());
		
		System.out.println("update to string:\n");
		try {
			update.writeTo(System.out);
		
			hr = Global.HRF.buildPostRequest(new GenericUrl(BASE_CAL+GET+"/"+calId+"/events"+
					"?key="+secret.API_KEY),
					update);
			hr.setHeaders(headers);
			response = hr.execute();
			output = response.parseAsString();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		System.out.println(output);
		return null;
	}
}

