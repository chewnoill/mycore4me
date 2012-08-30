package core_dos.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import com.google.api.server.spi.response.UnauthorizedException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.HttpHeaders;


import com.google.gwt.http.client.URL;

import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;



import core_dos.shared.FieldVerifier;
import core_dos.shared.JsEvent;
import core_dos.shared.secret;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.IdentityType;
public class core_post {

	public static String SITE = "https://core.meditech.com/core-coreWebHH.desktop.mthh";
	public static String SITE2= "https://core.meditech.com/signon.mthz";
	private HttpHeaders headers;
	private ArrayList<JsEvent> events;
	private String username;
	private String password;
	
	
	public core_post(String username,String password){
		String core_html = "";
		this.username = username;
		this.password = password;
		
		
		
	}


	public ArrayList<JsEvent> build_events() throws UnauthorizedException {		

		try{
			
			
			HttpRequest hr = Global.HRF.buildGetRequest(new GenericUrl(SITE));
			HttpResponse response = hr.execute();
			headers = hr.getHeaders();
			//String sid = "xyz";
			//headers.set("cookie", "sid="+sid+"; path=/, sourl=%2fcore%2dcoreWebHH%2edesktop%2emthh; path=/");
			
			
			String s="";
			for (String key : response.getHeaders().keySet()) {
				 s+=key+": "+ response.getHeaders().get(key)+"<br>";
				 //+(String) response.getHeaders().get(key)+"\n";
			}
			
			String cookie = headers.get("cookie")!=null?headers.get("cookie").toString():"";
			
			if(response.getHeaders().get("set-cookie")!=null){
				cookie = response.getHeaders().get("set-cookie").toString();
				int c_s = cookie.indexOf("[")+1;
				int c_e = cookie.indexOf("]",c_s);
				String c = cookie.substring(c_s, c_e);
				cookie += ";"+c;
					
			}
			
			headers.put("cookie",cookie);
			
			//headers.put("sid", sid);
			//System.out.println("cookies: "+s);
			
			
			HashMap<String,String> hm = new HashMap<String,String>();
			hm.put("userid", username);
			hm.put("password",password);
			UrlEncodedContent content = new UrlEncodedContent(hm);
			
			hr = Global.HRF.buildPostRequest(new GenericUrl(SITE2), content);
			hr.setHeaders(headers);
			s="";
			for (String key : hr.getHeaders().keySet()) {
				 s+=key+": "+ hr.getHeaders().get(key)+"\n";
				 //+(String) response.getHeaders().get(key)+"\n";
			}
			response = hr.execute();
			cookie = headers.get("cookie")!=null?headers.get("cookie").toString():"";
			
			if(response.getHeaders().get("set-cookie")!=null){
				cookie = response.getHeaders().get("set-cookie").toString();
				int c_s = cookie.indexOf("[")+1;
				int c_e = cookie.indexOf("]",c_s);
				String c = cookie.substring(c_s, c_e);
				cookie += ";"+c;
					
			}
			
			headers.put("cookie",cookie);
			
			String file = "",next="";
			file = response.parseAsString();
			
			ArrayList<String> landing_page = core_parser.parseCoreViewHTML(file,"day");
			if(landing_page==null){return null;}
			String cal_link = core_parser.getCalendarLink(landing_page.get(2));
			
			hr = Global.HRF.buildGetRequest(new GenericUrl(cal_link));
			hr.setHeaders(headers);
			response = hr.execute();
			
			file = response.parseAsString();
			
			ArrayList<String> calendar_page = core_parser.parseCoreViewHTML(file,"cal");
			
			this.events = getEventsFromCal(calendar_page.get(4));
			System.out.println("-----------------------------------\n");
			
			return this.events;
			
		} catch (IOException e) {
			System.out.println(":::"+e.getMessage());
			return null;

		} 
		
	}
	
	ArrayList<JsEvent> getEvents(){
		return this.events;
	}
	/**
	 * 
	 * @param cal_table
	 * @throws IOException 
	 */
	private ArrayList<JsEvent> getEventsFromCal(String cal_table) throws IOException{
		ArrayList<String> day_view_links = core_parser.parseEventFromCal(cal_table);
		ArrayList<String> event_view_links = new ArrayList<String>();
		ArrayList<JsEvent> ret = new ArrayList<JsEvent>();
		int x = 0;
		System.out.println("event links--------------------------------\n");
		for(String ev: day_view_links){
			System.out.println("loading: "+ev);
			HttpRequest hr = Global.HRF.buildGetRequest(new GenericUrl(ev));
			hr.setHeaders(headers);
			HttpResponse response = hr.execute();

			
			String day_view = response.parseAsString();
			ArrayList<String> day = core_parser.parseCoreViewHTML(day_view,"day");
			if(day.get(4).indexOf("No Events")!=-1){
				//nothing found, I should never get here
				System.out.println((++x)+": "+ev+": No Events");
		
			}else {
				
				event_view_links = core_parser.parseEventFromDay(day.get(3));
				for(String ev1: event_view_links){
					System.out.println("loading event view: "+ev1);
					hr = Global.HRF.buildGetRequest(new GenericUrl(ev1));
					hr.setHeaders(headers);
					response = hr.execute();
					String event_view = response.parseAsString();
					
					ArrayList<String> event_body = core_parser.parseCoreViewHTML(event_view, "day");
					System.out.println("event: "+ev1+":"+event_view.length()+":"+event_body.size());
					
					ArrayList<JsEvent> events = core_parser.parseEventFromEventView(event_body);
					for (JsEvent event : events){
						
						if (!ret.contains(event)){
							System.out.println("adding:: "+event);
							ret.add(event);
						}else{
							System.out.println("skipping");
						}
					}
					
					
				}
			}
			
		}
		return ret;
		
	}
	
	
}



