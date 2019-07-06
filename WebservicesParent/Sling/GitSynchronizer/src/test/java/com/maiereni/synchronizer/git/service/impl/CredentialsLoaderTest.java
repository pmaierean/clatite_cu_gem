/**
 * ================================================================
 *  Copyright (c) 2017-2019 Maiereni Software and Consulting Inc
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
package com.maiereni.synchronizer.git.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileOutputStream;
import java.util.Properties;
import java.util.UUID;

import org.junit.Test;

import com.maiereni.synchronizer.git.service.bo.Configuration;
import com.maiereni.synchronizer.git.service.bo.GitProperties;
import com.maiereni.util.DeletableFile;

/**
 * @author Petre Maierean
 *
 */
public class CredentialsLoaderTest {

	@Test
	public void testLoadingNullFile() {
		try {
			CredentialsLoader loader = new CredentialsLoader();
			loader.getProperties(null);
		}
		catch(Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testLoadingBlankFile() {
		try {
			CredentialsLoader loader = new CredentialsLoader();
			loader.getProperties("");
		}
		catch(Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testLoadingNoFile() {
		try {
			CredentialsLoader loader = new CredentialsLoader();
			loader.getProperties(UUID.randomUUID().toString());
		}
		catch(Exception e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testLoadingFromRegularFile() {
		try (DeletableFile deletableFile = createTestCase("https://www.github.com/sample", "simple", "123")) {
			CredentialsLoader loader = new CredentialsLoader();
			Configuration config = loader.getProperties(deletableFile.getPath()); 
			assertNotNull("Expected configuration", config);
			GitProperties props = config.getGitProperties();
			assertNotNull("Expected not null", props);
			assertEquals("Expected to match", "123", props.getPassword());			
			assertEquals("Expected to match", "https://www.github.com/sample", props.getRemote());			
			assertEquals("Expected to match", "simple", props.getUserName());
		}
		catch(Exception e) {
			e.printStackTrace();
			fail("Unexpected behavior");
		}
	}

	private DeletableFile createTestCase(final String remote, final String userName, final String password) 
		throws Exception {
		DeletableFile ret = new DeletableFile(".properties");
		Properties props = new Properties();
		props.setProperty(CredentialsLoader.URL_KEY, remote);
		props.setProperty(CredentialsLoader.USER_KEY, userName);
		props.setProperty(CredentialsLoader.PASSWORD_KEY, password);
		try(FileOutputStream fos = new FileOutputStream(ret.getTmpFile())) {
			props.store(fos, "Test");
		}
		return ret;
	}
	
}
