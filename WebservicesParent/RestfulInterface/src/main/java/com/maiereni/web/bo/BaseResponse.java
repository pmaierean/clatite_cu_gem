/**
 * 
 */
package com.maiereni.web.bo;

import java.io.Serializable;

/**
 * @author Petre Maierean
 *
 */
public abstract class BaseResponse implements Serializable {
	private static final long serialVersionUID = 7838266282061160942L;
	private String statusMessage;
	private Status status = Status.success;
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
}
