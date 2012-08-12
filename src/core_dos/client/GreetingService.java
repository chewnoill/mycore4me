package core_dos.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greetings")
public interface GreetingService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException;
}
