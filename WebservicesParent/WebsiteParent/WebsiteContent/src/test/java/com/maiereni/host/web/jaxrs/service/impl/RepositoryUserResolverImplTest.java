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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import javax.jcr.Credentials;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.maiereni.host.web.util.ConfigurationProvider;
import com.maiereni.host.web.util.impl.TestEncryptorProvider;

/**
 * @author Petre Maierean
 *
 */
public class RepositoryUserResolverImplTest extends BaseRepositoryTest {
	private RepositoryUserResolverImpl service;
	private ConfigurationProvider config;
	
	@Before
	public void setUp() throws Exception {
		RepositoryImpl repository = getRepository();
		config = getConfigurationProvider();
		service = new RepositoryUserResolverImpl(repository, config, TestEncryptorProvider.load());
	}
	
	@After
	public void tearDown() throws Exception {
		super.cleanUp();
	}
	
	@Test
	public void testAdminChangePassword() {
		try {
			service.setPassword(RepositoryUserResolverImpl.ADMIN_USER, "p@ssw0rd");
			Credentials cred = service.getCredentials(RepositoryUserResolverImpl.ADMIN_USER);
			assertNotNull("Expected to find", cred);
			assertNull("Not expected to find", service.getCredentials("aaa"));
			assertEquals("The password", "p@ssw0rd", config.getProperty(RepositoryUserResolverImpl.ADMIN_USER_PASSWORD, null));
		}
		catch(Exception e) {
			logger.error("Failed to change admin password", e);
			fail("Not yet implemented");
		}
	}

	@Test
	public void testCreateUser() {
		try {
			service.addUser("aaa", "p@ssw0rd");
			Credentials cred = service.getCredentials("aaa");
			assertNotNull("Expected to find", cred);
			assertNotNull("Not expected to find", service.getCredentials("aaa"));
		}
		catch(Exception e) {
			logger.error("Failed to change admin password", e);
			fail("Not yet implemented");
		}
	}
}
