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
package com.maiereni.host.web.servlet;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * A Dispatcher servlet context
 * @author Petre Maierean
 *
 */
public class MvcDispatcherServlet extends DispatcherServlet {
	private static final long serialVersionUID = -4217703434671031535L;

	@Override
	protected WebApplicationContext initWebApplicationContext() {
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		if (context != null) {
			super.setApplicationContext(context);
		}
		return super.initWebApplicationContext();
	}
}
