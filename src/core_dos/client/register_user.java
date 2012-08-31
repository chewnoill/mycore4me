package core_dos.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
@RemoteServiceRelativePath("reg")
public interface register_user extends RemoteService {
	String register_coreauth(String user, 
			String password) throws IllegalArgumentException;
	String register_googleauth(String access_token);
	String post_to_google();
}
