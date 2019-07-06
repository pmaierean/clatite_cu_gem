/**
 * ================================================================
 *  Copyright (c) 2017-2018 Maiereni Software and Consulting Inc
 * ================================================================
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maiereni.host.web.jaxrs.service.exceptions;

/**
 * An exception class for the cases when the User session has expired
 * @author Petre Maierean
 *
 */
public class UserExpired extends Exception {
	private static final long serialVersionUID = 2515571273939194486L;
	private String sessionToken;
	
	public UserExpired(final String sessionToken) {
		this.sessionToken = sessionToken;
	}
	
	public String getSessionToken() {
		return sessionToken;
	}
}