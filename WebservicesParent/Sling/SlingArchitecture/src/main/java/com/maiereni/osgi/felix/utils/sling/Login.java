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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maiereni.utils.http.bo.ResponseBean;

/**
 * A login command
 * @author Petre Maierean
 *
 */
public class Login extends BaseAdminConsoleClient {
	private static final String LOGIN_LINK = "/j_security_check";
	private static final String LOGIN_URL_TEMPLATE = "http://%s" + LOGIN_LINK;
	private static final String USER_NAME = "j_username";
	private static final String PASSWORD = "j_password";

	public Login(@Nonnull final String host, @Nonnull final ObjectMapper mapper) {
		super(host, mapper);
	}

	/**
	 * Login to the console
	 * @param user
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public ResponseBean login(final String user, final String password) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put(USER_NAME, user);
		params.put(PASSWORD, password);
		String url = String.format(LOGIN_URL_TEMPLATE, host);
		return post(url, params);
	}
}
