package core_dos.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
@RemoteServiceRelativePath("reg")
public interface register_user extends RemoteService {
	String register(String user, 
			String password, 
			String access_token) throws IllegalArgumentException;
}
