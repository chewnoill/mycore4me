package core_dos.server;

import java.awt.im.InputContext;
import java.util.ArrayList;

public class core_parser {
	private static final String BASE_SITE = "https://core.meditech.com"; 
	/**
	 * <table border="1">
	 * <tr><td>div 0 </td><td colspan=3>empty	</td></tr>
	 * <tr><td>div 1 </td><td colspan=3>schedule,worklist,inquiries</td></tr>
	 * <tr><td>div 2 </td><td colspan=3>left arrow, date, right arrow</td></tr>
	 * <tr><td></td><td>Day View</td><td>calendar view</td><td>event view</td></tr>
	 * <tr><td>div 3</td><td>event table</td><td>month picker</td><td>event name</td></tr>  
	 * <tr><td>div 4</td><td>events/no events</td><td>calendar</td><td>when/what/where</td></tr>
	 * <tr><td>div 5</td><td>empty?</td><td>top</td><td>who</td></tr>
	 * <tr><td>div 6</td><td colspan=2>schedule,worklist,inquiries</td><td>blank</td></tr>
	 * <tr><td>div 7</td><td colspan=2>logoff</td><td>notes</td></tr>
	 * <tr><td>div 8</td><td></td><td></td><td>top</td></tr>
	 * <tr><td>div 9</td><td></td><td></td><td>schedule,worklist,inquiries</td></tr>
	 * <tr><td>div 10</td><td></td><td></td><td>Logoff</td></tr>
	 * </table>
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
