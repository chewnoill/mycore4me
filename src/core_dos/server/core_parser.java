package core_dos.server;

import java.awt.im.InputContext;
import java.util.ArrayList;

public class core_parser {
	private static final String BASE_SITE = "https://core.meditech.com"; 
	/**
	 * div 0 - 						empty
	 * div 1 - 					schedule,worklist,inquiries
	 * div 2 - 					left arrow, date, right arrow
	 * 			Day View			|		calendar view	|	event view
	 * div 3 - event table      	|		month picker	|   event name      
	 * div 4 - events/no events		|		calendar 		|   when/what/where
	 * div 5 - empty?               |       top				|   who
	 * div 6 - 		schedule,worklist,inquiries				|   blank
	 * div 7 - 					logoff						|   notes
	 * div 8 -												|   top
	 * div 9 -												| 	schedule,worklist,inquiries
	 * div 10 -												|	logoff
	 * @param input
	 * @return array list of div elements on the page
	 * 
	 */
	public static ArrayList<String> parseDayViewHTML(String input){
		ArrayList<String> ret = new ArrayList<String>();
		int start_form = input.indexOf("<form name=\"inputform\"");
		int end_form = input.indexOf("</form>",start_form+22);
		int x = 0;
		if(start_form!=-1){
			int next = input.indexOf("<div",start_form);
			while(next!=-1&&next<end_form){
				next = input.indexOf(">",next)+1;
				int end = input.indexOf("</div>", next);
				if(end!=-1&&next!=-1){
					ret.add(input.substring(next, end));
					System.out.println((x++)+": "+ret.get(x-1));
				}
				next = input.indexOf("<div",end);
			}
			
		}
		
		return ret;
	}
	/**
	 * expects div 2 element from day view
	 * @return remote link to the calendar page
	 */
	public static String getCalendarLink(String input){
		int date_view = input.indexOf("<td class=\"style6\"");
		if (date_view!=-1){
			int href_start = input.indexOf("<a href=\".",date_view)+10; 
			int href_end   = input.indexOf("\"", href_start);
			String cal_link = BASE_SITE + input.substring(href_start,href_end);
			return cal_link;
		}
		return null;
	}
	
	public static ArrayList<String> parseCalendarHTML(String input) {
		ArrayList<String> ret = new ArrayList<String>();
		int start_form = input.indexOf("<form name=\"inputform\"");
		int end_form = input.indexOf("</form>",start_form+22);
		int x = 0;
		if(start_form!=-1){
			int next = input.indexOf("<div",start_form);
			while(next!=-1&&next<end_form){
				next = input.indexOf(">",next)+1;
				int end = input.indexOf("</div>", next);
				if(end!=-1&&next!=-1){
					ret.add(input.substring(next, end));
					System.out.println((x++)+": "+ret.get(x-1));
				}
				next = input.indexOf("<div",end);
			}
			
		}
		
		return ret;
	}
	

}
