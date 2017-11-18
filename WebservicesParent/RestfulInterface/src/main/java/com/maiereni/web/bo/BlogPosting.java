/**
 * 
 */
package com.maiereni.web.bo;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author Petre Maierean
 *
 */
@XmlRootElement
public class BlogPosting extends BaseResponse {
	private static final long serialVersionUID = 8445123778151979823L;
	private String id, message="";
	private Calendar calendar;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Calendar getCalendar() {
		return calendar;
	}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	
}
