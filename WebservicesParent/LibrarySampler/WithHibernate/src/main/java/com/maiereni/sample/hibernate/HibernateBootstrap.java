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
package com.maiereni.sample.hibernate;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.maiereni.sample.hibernate.model.User;

/**
 * Java application to bootstrap hibernate
 * 
 * @author Petre Maierean
 *
 */
@Component
public class HibernateBootstrap {
	private static final Logger logger = LoggerFactory.getLogger(HibernateBootstrap.class);
	private static final ClassPathXmlApplicationContext APPLICATION_CONTEXT = init();
	
	@Autowired
	private SampleDao sampleDao;
	
	public void listAllUsers() throws Exception {
		List<User> users = sampleDao.getUsers();
		users.forEach((u) -> {
			logger.debug("The name of the user {} is {} {}", new Object[] {u.getUserName(), u.getFirstName(), u.getLastName()}); 
		});
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			APPLICATION_CONTEXT.getBean(HibernateBootstrap.class).listAllUsers();
		}
		catch(Exception e) {
			logger.error("Failed to bootstrap hiberante", e);
		}
	}

	
	private static final ClassPathXmlApplicationContext init() {
		ClassPathXmlApplicationContext ret = null;
		try {
			ret = new ClassPathXmlApplicationContext("/SampleHibernate.xml");
			Runtime.getRuntime().addShutdownHook(new Thread() {
			    public void run() {
			    	try {
			    		APPLICATION_CONTEXT.stop();
			    		logger.debug("Tje context has been stopped");
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
