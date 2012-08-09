package core_dos.client;

import com.google.gwt.user.client.rpc.RemoteService;

public interface register_user extends RemoteService {
	String register(String user,String password) throws IllegalArgumentException;
}
