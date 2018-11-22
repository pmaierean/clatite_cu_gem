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
package com.maiereni.host.web.jaxrs.service.cache;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.jcr.SimpleCredentials;

import javax.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.host.web.jaxrs.service.SessionTokenResolver;
import com.maiereni.host.web.jaxrs.service.exceptions.UserExpired;
import com.maiereni.host.web.jaxrs.service.exceptions.UserNotFound;

/**
 * An implementation of the Session Token Resolver that is based on the ehcache
 * @author Petre Maierean
 *
 */
public class SessionTokenResolverImpl implements SessionTokenResolver {
	private static Logger logger = LoggerFactory.getLogger(SessionTokenResolverImpl.class);
	
	private Cache<String, SimpleCredentials> cache;
	
	SessionTokenResolverImpl(@Nonnull Cache<String, SimpleCredentials> cache) {
		this.cache = cache;
		logger.debug("Create resolver");
	}
	
	/**
	 * Gets credentials from the cache
	 * @param sessionToken
	 * @return simple credentials
	 * @throws UserExpired, UserNotFound
	 */
	@Override
	public SimpleCredentials get(@Nonnull final String sessionToken) throws UserExpired, UserNotFound {
		SimpleCredentials ret = cache.get(sessionToken);
		if (ret == null) {
			throw new UserNotFound(sessionToken);
		}
		return ret;
	}

	/**
	 * Stores simple credentials
	 * @param credentials
	 * @return token
	 */
	@Override
	public String put(@Nonnull final SimpleCredentials credentials) {
		String token = UUID.randomUUID().toString().replaceAll("-", "");
		cache.put(token, credentials);
		logger.debug("Associate the credentials of user {} with token {}", new Object[] { credentials.getUserID(), token});
		return token;
	}

	/**
	 * Remove credentials
	 * @param sessionToken
	 */
	@Override
	public void remove(@Nonnull final String sessionToken) {
		cache.remove(sessionToken);
		logger.debug("Remove simple credentials for token {}", sessionToken);
	}

}
