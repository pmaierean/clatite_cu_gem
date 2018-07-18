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

import static org.junit.Assert.fail;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.maiereni.host.web.jaxrs.service.RepositoryUserResolver;
import com.maiereni.host.web.util.ConfigurationProvider;

/**
 * Unit test for the repository service
 * @author Petre Maierean
 *
 */
public class RepositoryServiceImplTest extends BaseRepositoryTest {
	private RepositoryServiceImpl repositoryService;
	
	@Before
	public void setUp() throws Exception {
		RepositoryImpl repository = getRepository();
		ConfigurationProvider config = getConfigurationProvider();
		RepositoryUserResolver repositoryUserResolver = new RepositoryUserResolverImpl(repository, config);
		repositoryService = new RepositoryServiceImpl(repository, repositoryUserResolver);
	}

	@After
	public void tearDown() throws Exception {
		super.cleanUp();
	}

	@Test
	public void testAddObject() {
		try {
			
		}
		catch(Exception e) {
			logger.error("Failed to add object", e);
			fail("Not yet implemented");
		}
	}

}
