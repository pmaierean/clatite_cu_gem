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
package com.maiereni.authentication.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.auth.core.spi.AuthenticationHandler;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.apache.sling.auth.core.spi.DefaultAuthenticationFeedbackHandler;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.authentication.cache.CacheManagerProvider;

/**
 * @author Petre Maierean
 *
 */
@Component(name = "com.maiereni.authentication.handler.ApplicationAuthenticationHandler", 
		property = {AuthenticationHandler.TYPE_PROPERTY + "=" + HttpServletRequest.FORM_AUTH,
				AuthenticationHandler.PATH_PROPERTY + "=/" }, 
	service = AuthenticationHandler.class, 
	immediate = true)
public class ApplicationAuthenticationHandler extends DefaultAuthenticationFeedbackHandler
		implements AuthenticationHandler {
	public static final String COOKIE_NAME = "slingMe";
	public static final String FORM_PATTERN = ".*(\\x5Clogin)";
	public static final String USER_NAME = "user";
	public static final String PASSWORD = "password";
	private final Logger log = LoggerFactory.getLogger(ApplicationAuthenticationHandler.class);
	private FormParametersResolver formParametersResolver;
	private CookieResolver cookieResolver;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    @Reference
	private CacheManagerProvider cacheManagerProvider;
	
	/**
	 * Called by SCR to activate the authentication handler.
	 *
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalStateException
	 * @throws UnsupportedEncodingException
	 */
	@Activate
	protected void activate(ComponentContext componentContext)
		throws Exception {
		log.debug("Activate ApplicationAuthenticationHandler");
		formParametersResolver = new FormParametersResolver(FORM_PATTERN, USER_NAME, PASSWORD);
		cookieResolver = new CookieResolver(COOKIE_NAME);
	}
	
	@Override
	public AuthenticationInfo extractCredentials(final HttpServletRequest request, final HttpServletResponse response) {
		log.info("Extract credentials");
		AuthenticationInfo info = formParametersResolver.resolveAuthenticationInfo(request);
		if (info == null) {
			info = cookieResolver.resolveAuthenticationInfo(request);
		}
		
		return info;
	}

	@Override
	public boolean requestCredentials(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.info("Request credentials");
		return false;
	}

	@Override
	public void dropCredentials(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.info("Drop credentials");
	}

    /**
     * This default implementation does nothing.
     * <p>
     * Extensions of this class may overwrite to cleanup any internal state.
     */
	@Override
    public void authenticationFailed(HttpServletRequest request, HttpServletResponse response, AuthenticationInfo authInfo) {
		log.info("Authentication failed");
    }

    /**
     * This default implementation calls the
     * {@link #handleRedirect(HttpServletRequest, HttpServletResponse)} method
     * to optionally redirect the request after successful authentication.
     * <p>
     * Extensions of this class may overwrite this method to perform additional
     * cleanup etc.
     *
     * @return the result of calling the
     *            {@link #handleRedirect(HttpServletRequest, HttpServletResponse)}
     *            method.
     */
	@Override
    public boolean authenticationSucceeded(HttpServletRequest request,
        HttpServletResponse response, AuthenticationInfo authInfo) {
		log.info("Authentication succeeded");
        return handleRedirect(request, response);
    }
}
