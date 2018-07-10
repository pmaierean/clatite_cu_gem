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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.host.web.jaxrs.service.RepositoryConsumer;
import com.maiereni.host.web.jaxrs.service.RepositoryConsumerFactory;

/**
 * Implementation of the RepositoryConsumerFactory 
 * @author Petre Maierean
 *
 */
public class RepositoryConsumerFactoryImpl implements RepositoryConsumerFactory {
	private static final Logger logger = LoggerFactory.getLogger(RepositoryConsumerFactoryImpl.class);
	
	/**
	 * Creates a repository consumer object to marshal for an expected class 
	 * @param expectedType the expected bean class
	 * @return repository consumer
	 * @throws exception if the expectedType in not recognized
	 */
	@Override
	public RepositoryConsumer getConsumer(@Nonnull final Class<?> expectedType) throws Exception {
		logger.debug("Consumer");
		return null;
	}


}
