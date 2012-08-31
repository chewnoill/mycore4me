package core_dos.shared;

import java.util.HashMap;

import com.google.api.client.http.json.JsonHttpContent;

import core_dos.server.Global;

public class JsEvent {
	public JsEvent(){}
	public String kind;
	public String id;
	public String summary;
	public String location;
	public String htmlLink;
	public JsDate start;
	public JsDate end;
	public String description;
	public String participants;
	
	
	public JsEvent(String summary,
			String location,
			String startDate,
			String startDateTime,
			String startDateHuman,
			String endDate,
			String endDateTime,
			String endDateHuman){
		this.summary = fix_string(summary);
		this.location = fix_string(location);
		this.start = new JsDate(startDate,startDateTime,startDateHuman);
		this.end = new JsDate(endDate,endDateTime,endDateHuman);
	}
	
	
	public HashMap<String,Object> toJsonContent(){
		HashMap<String,Object> ret = new HashMap<String,Object>();
		
		ret.put("summary", fix_string(summary));
		ret.put("location", fix_string(location));
		ret.put("start", start.toJSONContent());
		ret.put("end", end.toJSONContent());
		return ret;
	}
	public String toJson(){
		HashMap<String,Object> content = toJsonContent();
		String ret = "{"+
				"\"summary\": \""+summary+"\","+
				"\"location\": \""+location+"\","+
				"\"start\": "+start.toJson()+","+
				"\"end\": "+end.toJson()+"}";
		System.out.println(ret);
		return ret;
	}
	
	@Override
	public boolean equals(Object comp){
		JsEvent that = (JsEvent) comp;
		
		System.out.println(this.location.equals(that.location));
		
		boolean ret = this.summary.equals(that.summary) &&
				this.start.equals(that.start) &&
				this.end.equals(that.end);
		//events do not require a location
		//this.location.equals(that.location) &&
		System.out.println(this.summary+"="+that.summary+":"+ret);
		System.out.println("----"+this.toJson()+":"+that.toJson()+"----");
		return ret;
	}
	static String fix_string(String input){
		if(input==null){return "";}
		String ret = input.replaceAll("<br>", "");
		ret = ret.replaceAll("&lt;", "<");
		ret = ret.replaceAll("&gt;", ">");
		ret = ret.replaceAll("&nbsp;", "");
		ret = ret.replaceAll("&amp;", "&");
		return ret;
	}
	public static String getLayout(){
		return "<table class=\"box_view\"><tr><td colspan=\"2\">{{summary}}</td></tr>"+
				"<tr><td colspan=\"2\">location: {{location}}</td></tr>"+
				"<tr><td>start: {{start.dateHuman}} </td><td>end: {{end.dateHuman}}</td></tr>"+
				"<tr><td colspan=\"2\">buttons</td></tr>"+
				"</table>";
	}
			
	
	
}
