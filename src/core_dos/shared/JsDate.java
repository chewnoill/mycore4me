package core_dos.shared;

import java.util.HashMap;

public class JsDate {
	JsDate(){}
	JsDate (String date, String dateTime){
		this.date = date;
		this.dateTime = dateTime;
	}
	public String dateTime="";
	public String date="";
	public String dateHuman = "";
	public void fixDates(){
		//remove colon from timezone
		int bad = date.indexOf(":", date.length()-4);
		if (bad!=-1){
			date = date.substring(0,bad)+date.substring(bad+1);
		}
		bad = dateTime.indexOf(":", dateTime.length()-4);
		if (bad!=-1){
			dateTime = dateTime.substring(0,bad)+dateTime.substring(bad+1);
		}
	}
	@Override
	public boolean equals(Object obj){
		JsDate that = (JsDate) obj;
		this.fixDates();
		that.fixDates();
		System.out.println("this: "+this.toJson()+"\nthat: "+that.toJson());
		return this.date.equals(that.date)&&
				this.dateTime.equals(that.dateTime);
		
	}
	public JsDate (String date, String dateTime, String dateHuman){
		this.date = date;
		this.dateTime = dateTime;
		this.dateHuman = dateHuman;
	}
	
	public HashMap<String,String> toJSONContent(){
		HashMap<String,String> ret = new HashMap<String,String>();
		if(date.length()>0){
			ret.put("date", date);
		} else if (dateTime.length()>0){
			ret.put("dateTime", dateTime);
		}	
		return ret;
	}
	public String toJson(){
		return "{\"date\": \""+date+"\","+
				"\"dateTime\": \""+dateTime+"\","+
				"\"dateHuman\": \""+dateHuman+"\"}";
	}

}
