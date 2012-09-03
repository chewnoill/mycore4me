package core_dos.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
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

import com.google.api.client.http.HttpMethod;
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




import core_dos.shared.FieldVerifier;
import core_dos.shared.JsEvent;
import core_dos.shared.JsEventList;
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
	public static String BASE_SITE = "https://core.meditech.com";
	private HttpHeaders headers;
	private JsEventList events;
	private String username;
	private String password;
	private cookie_monster cm = new cookie_monster();
	
	
	public core_post(String username,String password){
		String core_html = "";
		this.username = username;
		this.password = password;
		
		
		
	}


	public JsEventList build_events() throws UnauthorizedException {		
		String file = "";
		try{
			HttpRequest hr;
			HttpResponse response;
			String cookie;
			this.headers = new HttpHeaders();
			ArrayList<JsEvent> events;
			
			System.out.println("get new auth");
			hr = Global.HRF.buildGetRequest(new GenericUrl(SITE));
			hr.setHeaders(this.headers);
			response = hr.execute();
			
			file = response.parseAsString();
			
			System.out.println(file);
			
			if(file.contains("<title>MEDITECH HCIS Signon</title>")){
				
				HttpHeaders headers = response.getHeaders();
				String cookie1 = headers.get("cookie")!=null?headers.get("cookie").toString():"";
				cookie1 = cm.StoS(cookie1);
				if(response.getHeaders().get("set-cookie")!=null){
					String cookie2 = response.getHeaders().get("set-cookie").toString();
		
					cookie1 = cm.StoS(cookie2);
						
				}
				cookie = cookie1;
				
				headers.put("cookie",cookie);
				//file = "cookies1: "+cookie;
				//System.out.println(file);
				//headers.put("sid", sid);
				System.out.println("cookies: "+cookie);
				
				
				HashMap<String,String> hm = new HashMap<String,String>();
				hm.put("userid", username);
				hm.put("password",password);
				UrlEncodedContent content = new UrlEncodedContent(hm);
				
				//hr = Global.HRF.buildPostRequest(new GenericUrl(SITE2), content);
				hr.setMethod(HttpMethod.POST);
				hr.setContent(content);
				hr.setUrl(new GenericUrl(SITE2));
				hr.setHeaders(headers);
				
				cookie1+="\n::::request::::\n";
				
				
				file += cookie1;
				System.out.println("-----------------------------------1\n"+file);
				response = hr.execute();
				
				if(response.getHeaders().get("set-cookie")!=null){
					String cookie2 = response.getHeaders().get("set-cookie").toString();
		
					cookie1 = cm.StoS(cookie2);
					
				}
				cookie = cookie1;
				headers.put("cookie",cookie);
				System.out.println("-----------------------------------2\n");
				file = response.parseAsString();
				
				if(file.contains("Invalid username/password, try again.")){
					throw new UnauthorizedException("username/password rejected");
				}
				
				this.headers=headers;
			
				
			}
			
			ArrayList<String> landing_page = core_parser.parseCoreViewHTML(file,"day");
			if(landing_page==null){return null;}
			String cal_link = core_parser.getCalendarLink(landing_page.get(2));
			
			hr = Global.HRF.buildGetRequest(new GenericUrl(cal_link));
			hr.setHeaders(headers);
			response = hr.execute();
			System.out.println("-----------------------------------3\n");
			file = response.parseAsString();
			
			ArrayList<String> calendar_page = core_parser.parseCoreViewHTML(file,"cal");
			
			this.events = getEventsFromCal(calendar_page.get(4));
			System.out.println("-----------------------------------4\n");
			
			return this.events;
			
			//return file;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(":::"+e.getMessage());
			this.events = new JsEventList("Error",e.getMessage());
			return  this.events;

		} 
		
	}
	
	JsEventList getEvents() throws UnauthorizedException{
		
		events = build_events();
		
		return events; 
	}
	/**
	 * 
	 * @param cal_table
	 * @throws IOException 
	 */
	private JsEventList getEventsFromCal(String cal_table) throws IOException{
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
		
		return new JsEventList(ret);
		
	}
	
	
}

class cookie_monster{
	HashMap<String,String> cookies = new HashMap<String,String>();
	void parseFromString(String cookies){
		String seg_token = "[;,\\[\\]]";
		String value_token = "=";
		String[] expressions = cookies.split(seg_token);
		for(String ex: expressions){
			String[] value = ex.split(value_token);
			if(value.length>1){
				this.cookies.put(value[0],value[1]);
			}
		}
		
	}
	public String toString(){
		String ret = "";
		for(String key: cookies.keySet()){
			ret += key + "=" + cookies.get(key) + ";";
		}
		return ret;
	}
	public String StoS(String values){
		this.parseFromString(values);
		return this.toString();
	}
	
}



