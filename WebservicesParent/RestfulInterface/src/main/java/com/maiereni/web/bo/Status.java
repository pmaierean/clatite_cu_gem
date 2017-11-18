/**
 * 
 */
package com.maiereni.web.bo;

/**
 * @author Petre Maierean
 *
 */
public enum Status {
	success("success"), failure("error");
	
	private String text;
	private Status(String text) {
		this.text = text;
	}
	public String toString() {
		return text;
	}
}
