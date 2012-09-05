package core_dos.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.gson.Gson;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import core_dos.client.GreetingService;
import core_dos.client.register_user;
import core_dos.shared.FieldVerifier;
import core_dos.shared.JsEventList;

@SuppressWarnings("serial")
public class registry_server extends RemoteServiceServlet implements register_user {
	/**
	* Escape an html string. Escaping data received from the client helps to
	* prevent cross-site script vulnerabilities.
	* 
	* @param html the html string to escape
	* @return the escaped string
	*/
	
	private String access_token;
	private core_post c_post = null;
	private google_post g_post = new google_post();
	
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
	
	@Override
	public String register_coreauth(String user, 
			String password)
			throws IllegalArgumentException {
		

			
			String serverInfo = getServletContext().getServerInfo();
			String userAgent = getThreadLocalRequest().getHeader("User-Agent");
			// Escape data from the client to avoid cross-site script vulnerabilities.
			user = escapeHtml(user);
			userAgent = escapeHtml(userAgent);
			//SimpleDateFormat rfc = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ssZ");
			
			
			c_post = new core_post(user,password);
			
			try {
				JsEventList el = c_post.getEvents();
				return el.toJson();
				//return c_post.build_events();
			} catch (UnauthorizedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new JsEventList("Unauthorized","please login again").toJson();
			}
			
			
			
	}

	@Override
	public String register_googleauth(String access_token) {
		this.access_token = access_token;
		return g_post.checkToken(access_token);
	}
	
	@Override
	public String post_to_google(){
		if(g_post == null){
			g_post = new google_post();
		}
		if(c_post==null){
			return null;
		}
		try {
			
			return g_post.post(c_post.getEvents());
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
