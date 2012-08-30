package core_dos.shared;

import java.util.HashMap;

import com.google.api.client.http.json.JsonHttpContent;

import core_dos.server.Global;

public class JsEvent {
	public String getLayout(){
		return "<table class=\"box_view\"><tr><td colspan=\"2\">{{summary}}</td></tr>"+
				"<tr><td colspan=\"2\">location: {{location}}</td></tr>"+
				"<tr><td>start: {{start.date}} </td><td>end: {{end.date}}</td></tr>"+
				"<tr><td colspan=\"2\">buttons</td></tr>"+
				"</table>";
	}
			
	
	public JsEvent(){
		this.start = new date();
		this.end = new date();
	}
	public JsEvent(String summary,
			String location,
			date start,
			date end){
		this.summary = fix_string(summary);
		this.location = fix_string(location);
		this.start = start;
		this.end = end;
	}
	
	public String summary;
	public String location;
	public date start;
	public date end;
	public String description;
	public String participants;
	public HashMap<String,Object> toJsonContent(){
		HashMap<String,Object> ret = new HashMap<String,Object>();
		
		ret.put("summary", fix_string(summary));
		ret.put("location", fix_string(location));
		ret.put("start", start.toJSON());
		ret.put("end", end.toJSON());
		return ret;
	}
	public String toJson(){
		HashMap<String,Object> content = toJsonContent();
		String ret = "{";
		ret += "\"summary\": \""+summary+"\",";
		ret += "\"location\": \""+location+"\",";
		ret += "\"start\": { \"date\": \""+start.toString()+"\"},";
		ret += "\"end\": { \"date\": \""+end.toString()+"\"}}";
		System.out.println(ret);
		return ret;
	}
	
	@Override
	public boolean equals(Object comp){
		JsEvent that = (JsEvent) comp;
		System.out.println(this.summary.equals(that.summary));
		System.out.println(this.location.equals(that.location));
				
		return this.summary.equals(that.summary) &&
				this.location.equals(that.location) &&
				this.start.date.equals(that.start.date) &&
				this.start.dateTime.equals(that.start.dateTime) &&
				this.end.date.equals(that.end.date) &&
				this.end.dateTime.equals(that.end.dateTime);
				
	}
	static String fix_string(String input){
		
		String ret = input.replaceAll("<br>", "");
		ret = ret.replaceAll("&lt;", "<");
		ret = ret.replaceAll("&gt;", ">");
		ret = ret.replaceAll("&nbsp;", "");
		ret = ret.replaceAll("&amp;", "&");
		return ret;
	}
	
	public class date{
		date(){}
		public String toJson() {
			HashMap<String,Object> content = toJsonContent();
			String ret = "{";
			for(String key:content.keySet()){
				Object obj = content.get(key);
				if (obj instanceof String){
					ret+="\""+key+"\": \""+obj+"\",";
				}
				
			}
			return ret.substring(0,ret.length()-1)+"}";
		}
		public date (String date, String dateTime){
			this.date = date;
			this.dateTime = dateTime;
		}
		HashMap<String,String> toJSON(){
			HashMap<String,String> ret = new HashMap<String,String>();
			if(date.length()>0){
				ret.put("date", date);
			} else if (dateTime.length()>0){
				ret.put("dateTime", dateTime);
			}	
			return ret;
		}
		public String getDateType(){
			if(date.length()>0){
				return "date";
			} else if (dateTime.length()>0){
				return  "dateTime";
			}
			return "";
		}
		
		public String toString(){
			if(date.length()>0){
				return date;
			} else if (dateTime.length()>0){
				return  dateTime;
			}
			return "";
		}
		public String dateTime = "";
		public String date = "";
	}
}
