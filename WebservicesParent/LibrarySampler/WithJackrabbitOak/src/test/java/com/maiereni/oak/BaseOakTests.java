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
	private static final Logger _logger = LoggerFactory.getLogger(BaseOakTests.class);
	public final Logger logger = LoggerFactory.getLogger(getClass());
	
	public abstract ApplicationContext getApplicationContext();
	
	/**
	 * Get a bean by class
	 * @param clazz
	 * @return
	 */
	public <T> T getBean(final Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}
	
	/**
	 * Get a bean by class
	 * @param clazz
	 * @return
	 */
	public <T> T getBean(final String name, final Class<T> clazz) {
		return getApplicationContext().getBean(name, clazz);
	}
	
	public static ApplicationContext initialize(final String applicationPath) {
		try {
			final ApplicationContext ret = new ClassPathXmlApplicationContext(applicationPath);
			 _logger.debug("Configuration has been loaded from " + applicationPath);
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					_logger.debug("Clean up");
					ret.getBean(Cleaner.class).cleanup();
				}				
			});			 
			return ret;
		}
		catch(Exception e) {
			 _logger.error("Failed to load the test context from " + applicationPath);
			throw new java.lang.ExceptionInInitializerError(e);
		}
	}
}
