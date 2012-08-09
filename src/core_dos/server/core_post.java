package core_dos.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;

import com.google.api.server.spi.response.UnauthorizedException;


public class core_post {

	public static String SITE = "https://core.meditech.com/core-coreWebHH.desktop.mthh";
	public static String SITE2= "https://core.meditech.com/signon.mthz";

	private Map<String,String> data;
	private boolean is_lss = false;
	public static ClientConnectionManager CM=null;
	BasicHttpContext mHttpContext;

	private DefaultHttpClient mHttpClient;
	private CookieStore  mCookieStore;

	public core_post(boolean is_lss){

		this.data=new HashMap<String, String>();
		this.is_lss=is_lss;
		mHttpContext = new BasicHttpContext();
		mCookieStore = new BasicCookieStore();        
		mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);

		mHttpClient = new DefaultHttpClient();


	}

	public String doSubmit(String username,
			String password) throws UnauthorizedException {		

		try{
			HttpPost post;
			HttpGet httpget = new HttpGet(SITE);
			HttpResponse response = mHttpClient.execute(httpget,this.mHttpContext);
			HttpEntity entity = response.getEntity();

			System.out.println("Login form get: " + response.getStatusLine());
			if (entity != null) {
				entity.consumeContent();
			}
			System.out.println("Initial set of cookies:");
			List<Cookie> cookies = mCookieStore.getCookies();
			if (cookies.isEmpty()) {
				System.out.println("None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					System.out.println("- " + cookies.get(i).toString());
				}
			}


			System.out.println("==========second post");
			post = new HttpPost(SITE2);


			List<NameValuePair> nameValuePairs = 
					new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("userid", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			

			post.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			post.setHeader("Content-Type","application/x-www-form-urlencoded");

			post.setEntity(new  UrlEncodedFormEntity(nameValuePairs));

			response = mHttpClient.execute(post, mHttpContext);


			String file = "";
			String line = "";

			//webview.setHttpAuthUsernamePassword(SITE, "meditech.com", username, password);
			//webview.postUrl(SITE, EncodingUtils.getBytes(post_data, "BASE64"));

			BufferedReader in = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));
			


			while((line=in.readLine())!=null) {
				file += line;				
			}

			if(file.contains("Invalid username/password")||
					file.contains("Missing field(s)")){
				
			}

			in.close();
			System.out.println(file);
			System.out.println("==========done post");
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
			this.ignored_words = new ArrayList<String>();
			ignored_words.add("&nbsp;");
			ignored_words.add("<br>");
			this.replace_words = new HashMap<String,String>();
			replace_words.put("&lt;", "<");
			replace_words.put("&gt;", ">");
			no_event = false;
			this.event_date = fix(event_date);
			this.event_time = fix(event_time);
			this.event_info_link = fix(event_info_link);
			this.event_name = fix(event_name);
			this.event_place = fix(event_place);
			
					
		}
		private String fix(String input){
			for (int x = 0; x < ignored_words.size();x++){
				String ignore = ignored_words.get(x);
				int c = input.indexOf(ignore);
				while (c!=-1){
					input = input.substring(0, c) + input.substring(c+ignore.length());
					c = input.indexOf(ignore);
				}
				
			}
			for (String key : replace_words.keySet()) {
				int c = input.indexOf(key);
				while (c!=-1){
					input = input.substring(0, c) + 
							replace_words.get(key)+
							input.substring(c+key.length());
					c = input.indexOf(key);
				}
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

