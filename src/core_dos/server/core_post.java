package core_dos.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

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
import com.google.gwt.http.client.URL;

public class core_post {

	public static String SITE = "https://core.meditech.com/core-coreWebHH.desktop.mthh";
	public static String SITE2= "https://core.meditech.com/signon.mthz";

	private Map<String,String> data;
	private boolean is_lss = false;
	
	//private CookieStore  mCookieStore;

	public core_post(boolean is_lss){
		
		
		this.data=new HashMap<String, String>();
		this.is_lss=is_lss;
		

	}

	public String doSubmit(String username,
			String password) throws UnauthorizedException {		

		try{
			
			HttpTransport httpTransport = new NetHttpTransport();
			HttpRequestFactory hrf = httpTransport.createRequestFactory();

			HttpRequest hr = hrf.buildGetRequest(new GenericUrl(SITE));
			
			HttpResponse response = hr.execute();
			
			HashMap<String,String> hm = new HashMap<String,String>();
			hm.put("userid", username);
			hm.put("password",password);
			UrlEncodedContent content = new UrlEncodedContent(hm);
			hr = hrf.buildPostRequest(new GenericUrl(SITE2), content);
			response = hr.execute();
			
			
			String file = "",next="";
			file = response.parseAsString();
			
			System.out.println("Initial set of cookies:");
			
		return file;
		} catch (IOException e) {
			System.out.println(":::"+e.getMessage());
			return "Network Error: "+e.getMessage();

		}

		
	}

	protected void onPostExecute(String input)  {
		int d;
		final ArrayList<String> events = new ArrayList<String>();
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
				events.add("No Events");
				
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
							event_name,event_place).toString());


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
			}
		}

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
			replace_words.put("<br>", "");
			replace_words.put("&amp;", "");
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

