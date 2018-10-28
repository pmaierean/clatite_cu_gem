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

import javax.jcr.Credentials;

/**
 * The API of a utility class that resolves the credentials to access the repository 
 * for a given user
 * @author Petre Maierean
 *
 */
public interface RepositoryUserResolver {
	/**
	 * Get user credentials
	 * @param repoUser
	 * @return
	 * @throws Exception
	 */
	Credentials getCredentials(String repoUser) throws Exception;
	/**
	 * Add user
	 * @param repoUser
	 * @param password
	 * @throws Exception
	 */
	void addUser(String repoUser, String password) throws Exception;
	/**
	 * Change the user password
	 * 
	 * @param repoUser
	 * @param repoPassword
	 * @throws Exception
	 */
	void setPassword(String repoUser, String repoPassword) throws Exception;
	
	/**
	 * Detects if the user exists
	 * @param repoUser
	 * @return
	 */
	boolean isUser(String repoUser);
}
