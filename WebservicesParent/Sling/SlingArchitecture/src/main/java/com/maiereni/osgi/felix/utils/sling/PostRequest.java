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
package com.maiereni.osgi.felix.utils.sling;

import java.util.List;

import javax.servlet.http.Cookie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maiereni.osgi.felix.utils.bo.ValidationResponse;
import com.maiereni.utils.http.HttpClientException;
import com.maiereni.utils.http.bo.ResponseBean;

/**
 * @author Petre Maierean
 *
 */
public class PostRequest extends BaseAdminConsoleClient {  
 	private List<Cookie> cookies;
	private String urlTemplate;

	public PostRequest(String host,  ObjectMapper mapper, List<Cookie> cookies) {
		super(host, mapper);
		this.cookies = cookies;
		this.urlTemplate = "http://" + host;
	}

	public ValidationResponse validate(final String uri) {
		ValidationResponse ret = new ValidationResponse();
		try {
			ResponseBean bean = post(urlTemplate + uri, EMPTY_PARAMS, NO_HEADER, cookies);
			String s = bean.getBody();
			ret.setStatus(0);
			ret.setResponseBody(s);
		}
		catch(HttpClientException he) {
			ret.setResponseBody(he.getBody());
			ret.setStatus(he.getStatusCode());
		}
		catch(Exception e) {
			if (e.getMessage().contains("500")) {
				ret.setStatus(500);
			}
			else if (e.getMessage().contains("400")) {
				ret.setStatus(500);
			}
		}
		return ret;
	}
   
}
