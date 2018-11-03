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
import static org.junit.Assert.fail;

import java.util.Locale;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.maiereni.host.web.jaxrs.service.RepositoryUserResolver;
import com.maiereni.host.web.jaxrs.service.bo.RepositoryAddNodeRequest;
import com.maiereni.host.web.jaxrs.service.bo.RepositoryInsertRequest;
import com.maiereni.host.web.jcr.TextMessage;
import com.maiereni.host.web.util.ConfigurationProvider;
import com.maiereni.host.web.util.impl.TestEncryptorProvider;

/**
 * Unit test for the repository service
 * @author Petre Maierean
 *
 */
public class RepositoryServiceImplTest extends BaseRepositoryTest {
	private RepositoryServiceImpl repositoryService;
	private RepositoryUserResolver repositoryUserResolver;
	@Before
	public void setUp() throws Exception {
		RepositoryImpl repository = getRepository();
		ConfigurationProvider config = getConfigurationProvider();
		repositoryUserResolver = new RepositoryUserResolverImpl(repository, config, TestEncryptorProvider.load());
		repositoryService = new RepositoryServiceImpl(repository, repositoryUserResolver);
	}

	@After
	public void tearDown() throws Exception {
		super.cleanUp();
	}

	@Test
	public void testAddObjectNoPath() {
		try {
			RepositoryAddNodeRequest request = new RepositoryAddNodeRequest();
			repositoryService.addNode(request);
			fail("Unexpected request to add node without credentials");			
		}
		catch(Exception e) {
			assertEquals("Expected error", e.getMessage(), "Either path or name is null");
		}
	}

	@Test
	public void testAddObjectNoCredentials() {
		try {
			RepositoryAddNodeRequest request = new RepositoryAddNodeRequest();
			request.setName("abc");
			request.setPath("/simple/test");
			request.setRepoUser("");
			repositoryService.addNode(request);
			fail("Unexpected request to add node without credentials");			
		}
		catch(Exception e) {
			assertEquals("Expected error", e.getMessage(), "Invalid authorizable name ''");
		}
	}
	@Test
	public void testAddObjectWithBadCredential() {
		try {
			TextMessage textMessage = new TextMessage();
			textMessage.setKey("description");
			textMessage.setLocale(Locale.ENGLISH);
			textMessage.setMessage("This is the simple message");
			RepositoryInsertRequest request = new RepositoryInsertRequest();
			request.setName("abc");
			request.setParentPath("/simple/test");
			request.setRepoUser("test");
			repositoryService.addResource(request, textMessage);
			fail("Unexpected request to add node without credentials");			
		}
		catch(Exception e) {
			assertEquals("Expected error", e.getMessage(), "User not found");
		}		
	}
	@Test
	public void testAddObjectWithCredential() {
		try {
			repositoryUserResolver.addUser("test123", "sample");
			TextMessage textMessage = new TextMessage();
			textMessage.setKey("description");
			textMessage.setLocale(Locale.ENGLISH);
			textMessage.setMessage("This is the simple message");
			RepositoryAddNodeRequest addReq = new RepositoryAddNodeRequest();
			addReq.setRepoUser("test123");
			addReq.setLocale(Locale.ENGLISH);
			addReq.setName("simple");
			addReq.setPath("/");
			addReq.setType(JcrConstants.NT_FOLDER);
			repositoryService.addNode(addReq);

			addReq.setName("test");
			addReq.setPath("/simple");
			addReq.setType(JcrConstants.NT_FOLDER);
			repositoryService.addNode(addReq);
			
			RepositoryInsertRequest request = new RepositoryInsertRequest();
			request.setName("abc");
			request.setParentPath("/simple/test");
			request.setRepoUser("test123");
			repositoryService.addResource(request, textMessage);
			fail("Unexpected request to add node without credentials");			
		}
		catch(Exception e) {
			logger.error("The error", e);
			assertEquals("Expected error", e.getMessage(), "User not found");
		}		
	}
	
}
