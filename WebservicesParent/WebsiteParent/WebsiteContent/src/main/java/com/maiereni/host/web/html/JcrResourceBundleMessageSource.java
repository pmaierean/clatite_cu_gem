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
package com.maiereni.host.web.html;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.maiereni.host.web.jaxrs.service.RepositoryService;
import com.maiereni.host.web.jaxrs.service.bo.RepositoryQueryRequest;
import com.maiereni.host.web.jcr.TextMessage;

/**
 * A resource bundle message source that retrieves messages from the JCR
 * 
 * @author Petre Maierean
 *
 */
@Component("messageSource")
public class JcrResourceBundleMessageSource extends ResourceBundleMessageSource {
	private static final Logger logger = LoggerFactory.getLogger(JcrResourceBundleMessageSource.class);
	@Autowired
	private RepositoryService repositoryService;
	
	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		logger.debug("Resolve code without arguments for {} and locale {}", new Object[] {code, locale});
		return super.resolveCodeWithoutArguments(code, locale);
	}
	
	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		logger.debug("Resolve code for {} and locale {}", new Object[] {code, locale});		
		return super.resolveCode(code, locale);
	}
	
	@Override
	protected ResourceBundle getResourceBundle(String basename, Locale locale) {
		logger.debug("Get resource bundle for {} and locale {}", new Object[] {basename, locale});
		try {
			RepositoryQueryRequest request = null;
			List<TextMessage> o = (List<TextMessage>)repositoryService.getResources(request, TextMessage.class);
		}
		catch(Exception e) {
			logger.error("Failed to retrieve messages from JCR due to an exception", e);
		}
		return super.getResourceBundle(basename, locale);
	}
	

}
