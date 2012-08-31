package core_dos.shared;

import java.util.ArrayList;

public class JsEventList {
	public JsEventList(){}
	public String kind;
	public ArrayList<JsEvent> events;  
	public String summary;
	public JsEvent[] items;
	
	public String toJson(){
		String ret = "{\"events\": [ ";
		if (events != null) {
			for (JsEvent ev : events) {
				ret += ev.toJson() + ",";
			}
		}
		ret = ret.substring(0,ret.length()-1) + "]}";
		return ret;
		
	}
	
	public static String getLayout(){
		return "{{#events}}"+JsEvent.getLayout()+"<br>{{/events}}";
	}
}
