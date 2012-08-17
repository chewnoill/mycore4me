package core_dos.server;

import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.api.services.calendar.Calendar;
import core_dos.client.GreetingService;
import core_dos.client.register_user;
import core_dos.shared.FieldVerifier;

@SuppressWarnings("serial")
public class registry_server extends RemoteServiceServlet implements register_user {
	/**
	* Escape an html string. Escaping data received from the client helps to
	* prevent cross-site script vulnerabilities.
	* 
	* @param html the html string to escape
	* @return the escaped string
	*/
	private String escapeHtml(String html) {
	if (html == null) {
		return null;
	}
	
	return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
			.replaceAll(">", "&gt;");
	}
	
	@Override
	public String register(String user, 
			String password,
			String access_token)
			throws IllegalArgumentException {
		
			
			// Verify that the input is valid. 
			if (!FieldVerifier.isValidName(user)) {
				// If the input is not valid, throw an IllegalArgumentException back to
				// the client.
				
				throw new IllegalArgumentException(
						"Name must be at least 4 characters long");
			}
			
			String serverInfo = getServletContext().getServerInfo();
			String userAgent = getThreadLocalRequest().getHeader("User-Agent");
		
			// Escape data from the client to avoid cross-site script vulnerabilities.
			user = escapeHtml(user);
			userAgent = escapeHtml(userAgent);
			core_post post = new core_post(user,password);
			String content = access_token;
			
			content += "\n"+google_post.post(access_token);
		
			return "Hello, " + user + "!<br><br>I am running " + serverInfo
					+ ":"+content+":";
	}

}
