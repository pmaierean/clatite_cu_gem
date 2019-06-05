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

import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Petre Maierean
 *
 */
class CookieResolver {
	private final Logger log = LoggerFactory.getLogger(CookieResolver.class);
	private String cookieName;
	
	CookieResolver(final String cookieName) {
		this.cookieName = "";
		if (StringUtils.isNotBlank(cookieName)) {
			this.cookieName = cookieName;
		}
	}
	
	public AuthenticationInfo resolveAuthenticationInfo(final HttpServletRequest request) {
		AuthenticationInfo ret = null;
		String data = findCookieValue(request);
		if (StringUtils.isNotBlank(data)) {
			log.debug("Found cookie. Try to resolve it");
		}
		return ret;
	}
	
	private String findCookieValue(final HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookieName.equals(cookie.getName())) {
					String value = cookie.getValue();
					if (value.length() > 0) {
						try {
							return new String(Base64.decodeBase64(value), "UTF-8");
						} catch (UnsupportedEncodingException e1) {
							throw new RuntimeException(e1);
						}
					}
				}
			}
		}

		return null;
	}

}
