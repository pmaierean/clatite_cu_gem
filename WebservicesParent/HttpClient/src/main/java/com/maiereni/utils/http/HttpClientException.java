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
package com.maiereni.utils.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * @author Petre Maierean
 *
 */
public class HttpClientException extends Exception {
    private static final String ERROR_MESSAGE = "%s: %s";
    private static final long serialVersionUID = -5917028996005802319L;
	private String body, errorReason;
	private int statusCode;
	
	public HttpClientException(final HttpResponse response) {
		super(String.format(ERROR_MESSAGE, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
		statusCode = response.getStatusLine().getStatusCode();
		errorReason = response.getStatusLine().getReasonPhrase();
		try {
			HttpEntity entity = response.getEntity();
	        body = EntityUtils.toString(entity, "UTF-8");
		}
		catch(Exception e) {
			
		}
	}
	
	public String getBody() {
		return body;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public int getStatusCode() {
		return statusCode;
	}
}
