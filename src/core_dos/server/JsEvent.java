package core_dos.server;

import java.util.HashMap;

public class JsEvent {
	public JsEvent(){
		this.start = new date();
		this.end = new date();
	}
	public JsEvent(String summary,
			String location,
			date start,
			date end){
		this.summary = summary;
		this.location = location;
		this.start = start;
		this.end = end;
	}
	
	public String summary;
	public String location;
	public date start;
	public date end;
	
	public HashMap<String,Object> toJSON(){
		HashMap<String,Object> ret = new HashMap<String,Object>();
		
		ret.put("summary", fix_string(summary));
		ret.put("location", fix_string(location));
		ret.put("start", start.toJSON());
		ret.put("end", end.toJSON());
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
	
	class date{
		date(){}
		date (String date, String dateTime){
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
		public String dateTime = "";
		public String date = "";
	}
}
