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
package com.maiereni.sample.oak;

import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.api.ContentRepository;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.segment.file.FileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * The tester application
 * @author Petre Maierean
 *
 */
@Component
public class Application {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	private static final ClassPathXmlApplicationContext APPLICATION_CONTEXT = init();

	/**
	 * Get the Oak class
	 * @return
	 * @throws Exception
	 */
	public static Oak getOak() throws Exception {
		return APPLICATION_CONTEXT.getBean(Oak.class);
	}
	
	/**
	 * Get the context repository
	 * @return
	 * @throws Exception
	 */
	public static ContentRepository getContentRepository() throws Exception {
		return APPLICATION_CONTEXT.getBean(ContentRepository.class);
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Jcr getJcr() throws Exception {
		return APPLICATION_CONTEXT.getBean(Jcr.class);
	}
	
	/**
	 * Get the super user
	 * @return
	 * @throws Exception
	 */
	public static SimpleCredentials getSuperUser() throws Exception {
		return APPLICATION_CONTEXT.getBean("superUser", SimpleCredentials.class);
	}
	
	private static final ClassPathXmlApplicationContext init() {
		ClassPathXmlApplicationContext ret = null;
		try {
			ret = new ClassPathXmlApplicationContext("/SampleWithJackrabbit.xml");
			Runtime.getRuntime().addShutdownHook(new Thread() {
			    public void run() {
			    	try {
			    		FileStore store = APPLICATION_CONTEXT.getBean(FileStore.class);
			    		store.close();
			    	}
			    	catch(Exception e) {
			    		logger.error("Failed to close the file store", e);
			    	}
			    	try {
			    		APPLICATION_CONTEXT.stop();
			    		logger.debug("The context has been stopped");
			    	}
			    	catch(Exception e) {
			    		logger.error("Fail to stop the context", e);
			    	}
			    }
			});
		}
		catch(Exception e) {
			logger.error("Failed to load the context", e);
			throw new ExceptionInInitializerError("Cannot load the context");
		}
		return ret;
	}
}
