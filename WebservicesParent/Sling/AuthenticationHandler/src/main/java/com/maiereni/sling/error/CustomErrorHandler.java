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
package com.maiereni.sling.error;

import java.io.IOException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.engine.servlets.ErrorHandler;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

/**
 * @author Petre Maierean
 *
 */
@Component(service = { ErrorHandler.class },
property = {
        Constants.SERVICE_DESCRIPTION + "=Maiereni Error Handler",
        Constants.SERVICE_VENDOR + "=Maiereni Software and Consulting Inc"
})
public class CustomErrorHandler implements ErrorHandler {

	@Override
	public void handleError(int status, String message, SlingHttpServletRequest request,
		SlingHttpServletResponse response) throws IOException {
	}

	@Override
	public void handleError(Throwable throwable, SlingHttpServletRequest request, SlingHttpServletResponse response)
		throws IOException {
	}

}
