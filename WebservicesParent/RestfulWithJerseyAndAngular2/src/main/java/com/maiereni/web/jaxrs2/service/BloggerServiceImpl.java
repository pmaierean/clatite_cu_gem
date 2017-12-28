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
package com.maiereni.web.jaxrs2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Petre Maierean
 *
 */
@Component
public class BloggerServiceImpl implements BloggerService {
	private static final Logger logger = LoggerFactory.getLogger(BloggerServiceImpl.class);
	private static final String SERVICE_NAME = "sample blogger";
	
	@Override
	public String getServiceName() {
		logger.debug("Get the service name");
		return SERVICE_NAME;
	}

	@Override
	public String getPosting(String id) {
		logger.debug("Get a blog posting");
		return "This is my posting";
	}
	
	
}
