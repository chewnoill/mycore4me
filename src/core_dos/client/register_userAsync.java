package core_dos.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

public interface register_userAsync {
	void register_coreauth(String username, 
			String password,
			AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void register_googleauth(String access_token,
			AsyncCallback<String> callback);
}
