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

	public core_post(String username,String password){
		String core_html = "";
		try {
			core_html = doSubmit(username,password);
		} catch (UnauthorizedException e) {
			// unauthorized
			// alert user
			
		}
		
		
	}

	public String doSubmit(String username,
			String password) throws UnauthorizedException {		

		try{
			
			
			HttpRequest hr = Global.HRF.buildGetRequest(new GenericUrl(SITE));
			HttpResponse response = hr.execute();
			HttpHeaders headers = hr.getHeaders();
			//String sid = "xyz";
			//headers.set("cookie", "sid="+sid+"; path=/, sourl=%2fcore%2dcoreWebHH%2edesktop%2emthh; path=/");
			
			
			String s="";
			for (String key : response.getHeaders().keySet()) {
				 s+=key+": "+ response.getHeaders().get(key)+"<br>";
				 //+(String) response.getHeaders().get(key)+"\n";
			}
			
			
			String cookie = response.getHeaders().get("set-cookie").toString();
			System.out.println("cookies1: "+s+"\n"+cookie);
			int c_s = cookie.indexOf("[")+1;
			int c_e = cookie.indexOf("]",c_s);
			String c = cookie.substring(c_s, c_e);
			headers.put("cookie",c);
			
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
			System.out.println("cookies1.5\n "+s);
			response = hr.execute();
			
			cookie = response.getHeaders().get("set-cookie").toString();
			System.out.println("cookie1.6: "+cookie);
			c_s = cookie.indexOf("[")+1;
			c_e = cookie.indexOf("]",c_s);
			c = cookie.substring(c_s, c_e);
			cookie = headers.get("cookie")+";"+c;
			headers.put("cookie", cookie);
			
			System.out.println("cookies2: "+cookie);
			String file = "",next="";
			file = response.parseAsString();
			
			ArrayList<String> page = core_parser.parseDayViewHTML(file);
			String cal_link = core_parser.getCalendarLink(page.get(2));
			
			hr = Global.HRF.buildGetRequest(new GenericUrl(cal_link));
			hr.setHeaders(headers);
			response = hr.execute();
			file = response.parseAsString();
			System.out.println("-----------------------------------\n"+
					file);
			
			return file;
			
		} catch (IOException e) {
			System.out.println(":::"+e.getMessage());
			return "Network Error: "+e.getMessage();

		} 
		
	}

	protected ArrayList<Event> parseEvents(String input)  {
		int d;
		final ArrayList<Event> events = new ArrayList<Event>();
		String event_date;
		String event_time;
		String event_info_link;
		String event_name;
		String event_place;

		//parse file for event info:
		
		//check for errors
		int t = input.indexOf("Failed to connect with core");
		if(t!=-1&&t<10){
			//failed to connect
		}else {
			//connected

			//start parsing dates
			int c = input.indexOf("<td class=\"style9\">");
			int e = input.indexOf("LogOutBig.png",c);
			d = input.indexOf("</td>", c);
			c=c+"<td class=\"style9\">".length();
			d = input.indexOf("</td>", c);
			
			if(e<d||c>=input.length()){
				return null;
				
			} else {
				event_date = input.substring(c,d);

				while (d != -1){

					c = input.indexOf("<td class=\"style12\">",d);


					c=c+"<td class=\"style12\">".length();
					d = input.indexOf("</td>",c);
					event_time = input.substring(c,d);

					c = input.indexOf("<td class=\"style12\"><a href=\"",d);
					c=c+"<td class=\"style12\"><a href=\"".length();
					d = input.indexOf("\">",c);
					event_info_link = input.substring(c,d);

					c = d + "\">".length();
					d = input.indexOf("</a>",c);
					event_name = input.substring(c,d);

					c = input.indexOf("<td class=\"style12\">",d);
					c=c+"<td class=\"style12\">".length();
					d = input.indexOf("</td>",c);
					event_place = input.substring(c,d);

					//complete event should now have been read in
					events.add(new event(event_date,event_time,event_info_link,
							event_name,event_place).toEvent());


					int c1 = input.indexOf("<td class=\"style9\">",d);
					int c2 = input.indexOf("<td class=\"style12\">",d);
					if (c2<c1 || (c1 == -1)){
						//maybe read in more events on this date 
						d = c2;
					} else if (c1!=-1) {
						//next date
						c = c1;
						c=c+"<td class=\"style9\">".length();
						d = input.indexOf("</td>", c);
						event_date = input.substring(c,d);

					} else {
						//done
						d=-1;
					}
				}
				return events;
			}
		}
		return null;

	}

	class event{
		String event_date,event_time,event_info_link,event_name,event_place;
		boolean no_event;
		ArrayList<String> ignored_words;
		private HashMap<String, String> replace_words;
		public event(String event_date,
				String event_time,
				String event_info_link,
				String event_name,
				String event_place) {
			this.replace_words = new HashMap<String,String>();
			replace_words.put("&lt;", "<");
			replace_words.put("&gt;", ">");
			replace_words.put("&nbsp;", "");
			replace_words.put("<br>", "");//ignored
			replace_words.put("&amp;", "&");//ignored
			no_event = false;
			this.event_date = fix(event_date);
			this.event_time = fix(event_time);
			this.event_info_link = fix(event_info_link);
			this.event_name = fix(event_name);
			this.event_place = fix(event_place);
			
					
		}
		private String fix(String input){
			for (String key : replace_words.keySet()) {
				input.replaceAll(key, replace_words.get(key));
			}
			return input;
		}
		public event(String name) {
			this.event_name = name;
			this.no_event = true;
		}
		public Event toEvent(){
			Event event = new Event();
			event.setStart(new EventDateTime());
			return event;
		}
		public String toString(){
			System.out.println(event_date+"\n"+event_time+"\n"+event_name+"\n"+event_place);
			if (no_event){
				return event_name;
			} else if(event_place.length()>0) {
				return event_date+" "+event_time+"\n"+event_name+"\n"+event_place;
			} else {
				return event_date+" "+event_time+"\n"+event_name;
			}
		}
	}
}



