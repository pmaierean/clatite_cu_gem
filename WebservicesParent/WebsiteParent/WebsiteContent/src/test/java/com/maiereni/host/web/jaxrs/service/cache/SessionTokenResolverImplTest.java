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
package com.maiereni.host.web.jaxrs.service.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.jcr.SimpleCredentials;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.host.web.BaseUnitTest;
import com.maiereni.host.web.jaxrs.service.exceptions.UserNotFound;

/**
 * Unit test class of the SessionTokenResolverImpl class
 * @author Petre Maierean
 *
 */
public class SessionTokenResolverImplTest extends BaseUnitTest {
	private static final Logger logger = LoggerFactory.getLogger(SessionTokenResolverImplTest.class);
	private SessionTokenResolverImpl resolver;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		resolver = getBean(SessionTokenResolverImpl.class);
	}


	@Test
	public void testGettingSessions() {
		try {
			SimpleCredentials credentials = new SimpleCredentials("simple", "1234".toCharArray());
			String token = resolver.put(credentials);
			assertNotNull("The token is not expected to be null", token);
			Thread.sleep(1000);
			SimpleCredentials simple = resolver.get(token);
			assertNotNull("Non null", simple);
			assertEquals("Expected user id", credentials.getUserID(), simple.getUserID());
			assertEquals("Expected password", credentials.getPassword(), simple.getPassword());
			resolver.remove(token);
			try {
				resolver.get(token);
				fail("Unexpected result");
			}
			catch(UserNotFound g) {
				
			}
		}
		catch(Exception e) {
			logger.error("Failed to test the resolver", e);
			fail("There was an error");
		}
	}

}
