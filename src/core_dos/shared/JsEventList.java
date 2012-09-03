package core_dos.shared;

import java.util.ArrayList;

public class JsEventList {
	public JsEventList(){}
	public String kind;
	public ArrayList<JsEvent> events;  
	public String summary;
	public JsEvent[] items;
	public String error_id="";
	public String error_message="";
	public JsEventList(String error_id,String error_message){
		this.error_id = error_id;
		this.error_message = error_message;
	}
	public JsEventList(ArrayList<JsEvent> events){
		this.events = events;
		
	}
	public String toJson(){
		//if no events and no other errors
		if((events==null||events.isEmpty())&&error_id.length()==0){
			error_id = "No Events";
			error_message = "No Events Found.";
		}
		
		String ret ="{\"error_id\": \""+error_id+"\","+
				"\"error_message\": \""+error_message+"\","; 
		
		ret += "\"events\": [ ";
		if (events != null) {
			for (JsEvent ev : events) {
				ret += ev.toJson() + ",";
			}
		}
		ret = ret.substring(0,ret.length()-1) + "]}";
		return ret;
		
	}
	
	public static String getLayout(){
		return "{{#events}}<br>"+JsEvent.getLayout()+"{{/events}}";
	}
}
