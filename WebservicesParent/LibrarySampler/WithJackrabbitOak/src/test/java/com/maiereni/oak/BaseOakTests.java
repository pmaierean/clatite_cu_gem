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
package com.maiereni.oak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Petre Maierean
 *
 */
public abstract class BaseOakTests {
	private static final Logger logger = LoggerFactory.getLogger(BaseOakTests.class);
	protected static final ApplicationContext APPLICATION_CONTEXT;
	
	/**
	 * Get a bean by class
	 * @param clazz
	 * @return
	 */
	public <T> T getBean(final Class<T> clazz) {
		return APPLICATION_CONTEXT.getBean(clazz);
	}
	
	/**
	 * Get a bean by class
	 * @param clazz
	 * @return
	 */
	public <T> T getBean(final String name, final Class<T> clazz) {
		return APPLICATION_CONTEXT.getBean(name, clazz);
	}

	static {
		try {
			APPLICATION_CONTEXT = new ClassPathXmlApplicationContext("/com/maiereni/sample/oak/test/application.xml");
		}
		catch(Exception e) {
			logger.error("Failed to load the test context", e);
			throw new java.lang.ExceptionInInitializerError(e);
		}
	}
}
