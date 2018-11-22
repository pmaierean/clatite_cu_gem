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
package com.maiereni.host.web.jaxrs.service.impl;

import javax.annotation.Nonnull;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.SimpleCredentials;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.api.ContentRepository;
import org.apache.jackrabbit.oak.api.ContentSession;
import org.apache.jackrabbit.oak.api.QueryEngine;
import org.apache.jackrabbit.oak.api.Root;
import org.apache.jackrabbit.oak.namepath.NamePathMapper;
import org.apache.jackrabbit.oak.security.user.UserManagerImpl;
import org.apache.jackrabbit.oak.security.user.query.UserQueryManager;
import org.apache.jackrabbit.oak.spi.security.user.UserConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import com.maiereni.host.web.jaxrs.service.SessionTokenResolver;
import com.maiereni.host.web.jaxrs.service.bo.BaseRequest;
import com.maiereni.host.web.jaxrs.service.exceptions.UserExpired;
import com.maiereni.host.web.jaxrs.service.exceptions.UserNotFound;

/**
 * Utility class that resolves the user Session from the session token
 * 
 * @author Petre Maierean
 *
 */
public abstract class BaseService {

	@Autowired
	private ContentRepository contentRepository;
	
	@Autowired
	private SessionTokenResolver sessionTokenResolver;
	
	@Autowired
	private NamePathMapper namePathMapper;
	
	@Autowired
	private UserConfiguration userConfiguration;
	/**
	 * Open a session for the base request
	 * @param baseRequest
	 * @return
	 * @throws UserNotFound
	 * @throws UserExpired
	 * @throws NoSuchWorkspaceException 
	 * @throws LoginException 
	 * @throws Exception
	 */
	public ContentSession getContentSession(@Nonnull final BaseRequest baseRequest) throws UserNotFound, UserExpired, LoginException, NoSuchWorkspaceException {
		String sessionToken = baseRequest.getSessionToken();
		if (StringUtils.isEmpty(sessionToken)) {
			throw new UserNotFound("Missing authentication");
		}
		SimpleCredentials simpleCredentials = sessionTokenResolver.get(baseRequest.getSessionToken());
		return contentRepository.login(simpleCredentials, null);
	}  

	/**
	 * Get a Query manager for the current session
	 * @param session
	 * @return
	 */
	public UserQueryManager getUserQueryManager(@Nonnull final ContentSession session) {
		Root root = session.getLatestRoot();
		UserManagerImpl userManager = (UserManagerImpl)userConfiguration.getUserManager(root, namePathMapper);
		return new UserQueryManager(userManager, namePathMapper, userConfiguration.getParameters(), root);
	}
	
	/**
	 * Get the Query engine 
	 * @param session
	 * @return
	 */
	public QueryEngine getQueryEngine(@Nonnull final ContentSession session) { 
		Root root = session.getLatestRoot();
		return root.getQueryEngine();
	}
}
