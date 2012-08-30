package core_dos.shared;

import java.util.ArrayList;

public class JsEventList {
	public ArrayList<JsEvent> events = new ArrayList<JsEvent>();  
	public String toJson(){
		String ret = "[";
		for (JsEvent ev : events) {
			ret += ev.toJson() + ",";
		}
		ret = ret.substring(0,ret.length()-1)+"]";
		return ret;
		
	}
}
