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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.auth.core.AuthConstants;
import org.apache.sling.auth.core.AuthUtil;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolves the login parameters from the request
 * @author Petre Maierean
 *
 */
class FormParametersResolver {
	private final Logger log = LoggerFactory.getLogger(FormParametersResolver.class);
	private static final String METHOD_POST = "POST";
	private String uriPattern, userNameKey, userPasswordKey;
	
	FormParametersResolver() {
		this(null, null,null);
	}
	
	FormParametersResolver(final String uriPattern, final String userNameKey, final String userPasswordKey) {
		this.uriPattern = ".*(\\x5Clogin)";
		if (StringUtils.isNotBlank(uriPattern)) {
			this.uriPattern = uriPattern;
		}
		this.userNameKey = "user";
		if (StringUtils.isNotBlank(userNameKey)) {
			this.userNameKey = userNameKey;
		}
		this.userPasswordKey = "password";
		if (StringUtils.isNotBlank(userPasswordKey)) {
			this.userPasswordKey = userPasswordKey;
		}
	}
	
	/**
	 * Resolves the credentials from a form
	 * @param request
	 * @return
	 */
	public AuthenticationInfo resolveAuthenticationInfo(final HttpServletRequest request) {
		AuthenticationInfo ret = null;
		String uri = request.getRequestURI();
		String method = request.getMethod();
		if (StringUtils.isNoneBlank(method, uri) && uri.matches(uriPattern) && method.trim().toUpperCase().equals(METHOD_POST)) {
			String userName = request.getParameter(userNameKey);
			String password = request.getParameter(userPasswordKey);
			if (StringUtils.isNoneBlank(userName, password)) {
				ret = new AuthenticationInfo(HttpServletRequest.FORM_AUTH, userName, password.toCharArray());
				ret.put(AuthConstants.AUTH_INFO_LOGIN, new Object());
				if (!AuthUtil.isValidateRequest(request)) {
					AuthUtil.setLoginResourceAttribute(request, request.getContextPath());
				}
				log.debug("Found credentials in the request parameters");
			}
		}
		
		return ret;
	}
}
