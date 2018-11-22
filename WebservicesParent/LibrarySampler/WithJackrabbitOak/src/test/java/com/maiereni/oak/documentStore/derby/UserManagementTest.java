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
package com.maiereni.oak.documentStore.derby;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.UUID;

import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.security.auth.login.Configuration;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.api.ContentRepository;
import org.apache.jackrabbit.oak.api.ContentSession;
import org.apache.jackrabbit.oak.api.Root;
import org.apache.jackrabbit.oak.namepath.NamePathMapper;
import org.apache.jackrabbit.oak.plugins.value.jcr.ValueFactoryImpl;
import org.apache.jackrabbit.oak.spi.security.user.UserConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Petre Maierean
 *
 */
public class UserManagementTest extends BaseDocumentStoreTest {
	private static final String TEST_GROUP_NAME = "testGroup" + UUID.randomUUID().toString();
	private static final String TEST_USER_NAME = "testUser" + UUID.randomUUID().toString();
	private ContentSession adminSession;
	private UserManager userManager;
	private Root root;
	private ValueFactoryImpl valueFactory;
	
	@Before
	public void setUp() throws Exception {
		ContentRepository contentRepository = getBean(ContentRepository.class);
		SimpleCredentials adminCredentials = getBean("adminUser", SimpleCredentials.class);
        adminSession = contentRepository.login(adminCredentials, null);
        root = adminSession.getLatestRoot();
        UserConfiguration userConfiguration = getBean(UserConfiguration.class);
        NamePathMapper namePathMapper = getBean("namePathMapper", NamePathMapper.class);
		userManager = userConfiguration.getUserManager(root, namePathMapper);
        valueFactory = new ValueFactoryImpl(root, namePathMapper);
		Configuration config = getBean("securityConfiguration", Configuration.class);
		Configuration.setConfiguration(config);
	}

	@After
	public void tearDown() throws Exception {
        if (adminSession != null) {
            adminSession.close();
        }
		Configuration.setConfiguration(null);
	}
		
	@Test
	public void testAddGroup() {
		Authorizable testGroup = null;
		try {
			userManager.createGroup(TEST_GROUP_NAME);
			root.commit();
			
			root.refresh();
			Authorizable grp = userManager.getAuthorizable(TEST_GROUP_NAME);
			assertNotNull("Expected to find authorizable", grp);
			testGroup = grp;
			assertTrue("Expected to be a group", grp.isGroup());
			assertEquals("Expected name", TEST_GROUP_NAME, ((Group)grp).getID());
			logger.debug("Tested adding a group successfully");
		}
		catch(Exception e) {
			logger.error("Failed to create group", e);
			fail("Failed to create user");
		}
		finally {
			if (testGroup != null) {
				try {
					testGroup.remove();
					root.commit();
				}
				catch(Exception e) {
					logger.error("Failed to remove authorizable", e);
				}
				root.refresh();
			}
		}
	}
	
	@Test
	public void testGroupNotFound() {
		Authorizable testGroup = null;
		try {
			userManager.createGroup(TEST_GROUP_NAME);
			root.commit();
			
			root.refresh();
			Authorizable grp = userManager.getAuthorizable(TEST_GROUP_NAME);
			assertNotNull("Expected to find authorizable", grp);
			testGroup = grp;
			Authorizable grp1 = userManager.getAuthorizable(TEST_GROUP_NAME + "1");
			assertNull("Not expected to find", grp1);
			logger.debug("Tested group not found successfully");
		}
		catch(Exception e) {
			logger.error("Failed to create user", e);
			fail("Failed to create user");
		}
		finally {
			if (testGroup != null) {
				try {
					testGroup.remove();
					root.commit();
				}
				catch(Exception e) {
					logger.error("Failed to remove authorizable", e);
				}
				root.refresh();
			}
		}
	}
	
	@Test
	public void testAddUser() {
		Authorizable testGroup = null, testUser = null;
		try {
			userManager.createGroup(TEST_GROUP_NAME);
			root.commit();
			
			root.refresh();
			Authorizable grp = userManager.getAuthorizable(TEST_GROUP_NAME);
			assertNotNull("Expected to find authorizable", grp);
			testGroup = grp;
			assertTrue("Expected to be a group", grp.isGroup());
			
			User usr = userManager.createUser(TEST_USER_NAME, "password");
			usr.setProperty("description", valueFactory.createValue("This is my test user"));
			root.commit();
			root.refresh();
			Group actualGroup = (Group) userManager.getAuthorizable(TEST_GROUP_NAME);
			boolean isAdded = actualGroup.addMember(usr);
			assertTrue("Add a member to the group",isAdded);

			root.commit();
			root.refresh();
			
			testUser = userManager.getAuthorizable(TEST_USER_NAME);
			assertNotNull("Expected test user", testUser);
			assertTrue("The authorizable is a user", !testUser.isGroup());
			usr = (User)testUser;
			assertEquals("The name should match", TEST_USER_NAME, usr.getID());
			Value[] props = usr.getProperty("description");
			assertNotNull("Expected description", props);
			assertEquals("Expected description", 1, props.length);
			assertEquals("Expected value of description", "This is my test user", props[0].toString());
			
			actualGroup = (Group) userManager.getAuthorizable(TEST_GROUP_NAME);
			Iterator<Authorizable> iter = actualGroup.getMembers();
			int i = 0;
			for(;iter.hasNext();) {
				i++;
				Authorizable a = iter.next();
				assertTrue(!a.isGroup());
				assertEquals("The name should match", TEST_USER_NAME, ((User)a).getID());
			}
			assertEquals("Expected number of members of the group", 1, i);
			logger.debug("Tested adding a user successfully");
		}
		catch(Exception e) {
			logger.error("Failed to create user", e);
			fail("Failed to create user");
		}
		finally {
			if (testUser != null) {
				try {
					testUser.remove();
					root.commit();
				}
				catch(Exception e) {
					logger.error("Failed to remove authorizable", e);
				}
				root.refresh();
			}
			
			if (testGroup != null) {
				try {
					testGroup.remove();
					root.commit();
				}
				catch(Exception e) {
					logger.error("Failed to remove authorizable", e);
				}
				root.refresh();
			}
		}
	}
}
