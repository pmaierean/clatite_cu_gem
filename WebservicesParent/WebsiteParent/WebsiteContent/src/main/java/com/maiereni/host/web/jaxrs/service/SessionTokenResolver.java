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
package com.maiereni.host.web.jaxrs.service;

import javax.jcr.SimpleCredentials;

import com.maiereni.host.web.jaxrs.service.exceptions.UserExpired;
import com.maiereni.host.web.jaxrs.service.exceptions.UserNotFound;

/**
 * The API of a resolver utility
 * @author Petre Maierean
 *
 */
public interface SessionTokenResolver {
	/**
	 * Given a session token, the method resolves the simple credentials
	 * @param sessionToken
	 * @return
	 * @throws UserExpired, UserNotFound
	 */
	SimpleCredentials get(String sessionToken) throws UserExpired, UserNotFound;
	/**
	 * Stores credentials and returns a session token
	 * @param credentials
	 * @return session token
	 */
	String put(SimpleCredentials credentials);
	/**
	 * Removes session token
	 * @param sessionToken
	 */
	void remove(String sessionToken);
}
