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
package com.maiereni.npm.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.UrlPathHelper;

/**
 * @author Petre Maierean
 *
 */
public class RegistryDispatcherServlet extends DispatcherServlet {
	private static final long serialVersionUID = 8810544494734596065L;
	private static final Logger logger = LoggerFactory.getLogger(RegistryDispatcherServlet.class);
	private UrlPathHelper urlPathHelper = new UrlPathHelper();
	
	@Override
	protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String requestUri = urlPathHelper.getRequestUri(request);
		logger.debug("No handler was found for: {}", requestUri);
		super.noHandlerFound(request, response);
	}
}
