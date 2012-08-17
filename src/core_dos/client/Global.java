package core_dos.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

public class Global {	
	public static String THIS_HOST = "127.0.0.1:8888/";

	public static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
         
        return sb.toString();
    }
}
