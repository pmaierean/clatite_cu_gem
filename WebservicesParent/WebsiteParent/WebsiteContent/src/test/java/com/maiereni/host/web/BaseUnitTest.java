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
package com.maiereni.host.web;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.maiereni.host.web.util.impl.EncryptorFactory;

/**
 * Creates context that's necessary to test the website without the WebContainer
 * 
 * @author Petre Maierean
 *
 */
public abstract class BaseUnitTest {
	private static final Logger logger = LoggerFactory.getLogger(BaseUnitTest.class);
	private static final ApplicationContext applicationContext = loadTestContext();
	
	public <T> T getBean(@Nonnull Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}
	
	public <T> T getBean(@Nonnull String name, @Nonnull Class<T> clazz) {
		return applicationContext.getBean(name, clazz);
	}
	
	private static ApplicationContext loadTestContext() {
		try {
			System.setProperty(EncryptorFactory.KEY_STORE_PATH, "classpath:/testkeystore.jks");
			System.setProperty(EncryptorFactory.KEY_STORE_PASSWORD, "changeit");
			final ClassPathXmlApplicationContext ctxt = new ClassPathXmlApplicationContext("/test/testApplication.xml");
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						ctxt.getBean(Cleaner.class).cleanup();
					}
					catch(Exception e) {
						logger.error("Failed to cleanup", e);
					}
				}				
			});			 
			return ctxt;
		}
		catch(Exception e) {
			logger.error("Failed to initialize", e);
			throw new java.lang.ExceptionInInitializerError(e);
		}
	}
}
