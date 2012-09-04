package core_dos.shared;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class JsDate {
	JsDate(){}
	//private static SimpleDateFormat rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",Locale.US);
	JsDate (String date, String dateTime){
		this.date = date;
		this.dateTime = dateTime;
	}
	public String dateTime="";
	public String date="";
	public String dateHuman = "";
	
	@Override
	public boolean equals(Object obj){
		JsDate that = (JsDate) obj;
		
		System.out.println("this: "+this.toJson()+"\nthat: "+that.toJson());
		
		return this.dateTime.equals(that.dateTime) && this.date.equals(that.date);
		
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
