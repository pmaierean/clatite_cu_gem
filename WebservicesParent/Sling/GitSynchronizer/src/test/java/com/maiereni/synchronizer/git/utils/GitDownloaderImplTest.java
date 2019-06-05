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
package com.maiereni.synchronizer.git.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.synchronizer.git.service.bo.GitProperties;
import com.maiereni.synchronizer.git.service.bo.GitResults;
import com.maiereni.synchronizer.git.service.bo.LayoutRule;
import com.maiereni.util.DeletableDirectory;
import com.maiereni.util.DeletableFile;

/**
 * @author Petre Maierean
 *
 */
public class GitDownloaderImplTest {
	private static final String SLING_ENGINE_GIT_URL = "https://gitbox.apache.org/repos/asf/sling-org-apache-sling-engine.git";
	private static final String FRONTEND_GIT1_URL = "https://github.com/eirslett/frontend-maven-plugin";
	private static final String TAG_2_4_6 = "frontend-plugins-1.7.5";
	private static final Logger logger = LoggerFactory.getLogger(GitDownloaderImplTest.class);
	private GitDownloaderImpl downloader = new GitDownloaderImpl();
	
	@Test
	public void testNullArgument() {
		try {
			downloader.download(null);
			fail("Expected to throw an exception");
		}
		catch(Exception e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testNullLocalRepo() {
		try {
			GitProperties props = new GitProperties();
			downloader.download(props);
			fail("Expected to throw an exception");
		}
		catch(Exception e) {
			assertTrue(true);
			assertEquals("Expected message", "The local repository cannot be null", e.getMessage());
		}
	}	

	@Test
	public void testNullRemote() {
		try (DeletableDirectory dir = new DeletableDirectory()) {
			GitProperties props = new GitProperties();
			props.setLocalRepo(dir.getPath());

			downloader.download(props);
			fail("Expected to throw an exception");
		}
		catch(Exception e) {
			assertTrue(true);
			assertEquals("Expected message", "The remote URL of Git cannot be null", e.getMessage());
		}		
	}
	
	@Test
	public void testInvalidRemote() {
		try (DeletableDirectory dir = new DeletableDirectory()) {
			GitProperties props = new GitProperties();
			props.setRemote("http://www." + UUID.randomUUID().toString().replaceAll("-", "") + ".com");
			props.setLocalRepo(dir.getPath());
			
			downloader.download(props);
			fail("Expected to throw an exception");
		}
		catch(Exception e) {
			assertTrue(true);
			assertEquals("Expected message", "Exception caught during execution of fetch command", e.getMessage());
		}				
	}
		
	@Test
	public void testCreateNewMasterBranch() {
		try (DeletableDirectory dir = new DeletableDirectory()) {
			GitProperties props = new GitProperties();
			props.setRemote(SLING_ENGINE_GIT_URL);
			props.setLocalRepo(dir.getPath());
			props.setContentPath("");
			GitResults results = downloader.download(props);
			assertNotNull(results);
			assertTrue("The repository has been created", results.isCreated());
		}
		catch(Exception e) {
			logger.error("Failed to create local repository", e);
			fail("Expected to create a local repository");
		}
	}
	
	@Test
	public void testCreateNewMasterBranchFromTag() {
		try (DeletableDirectory dir = new DeletableDirectory()) {
			GitProperties props = new GitProperties();
			props.setRemote(FRONTEND_GIT1_URL);
			props.setTagName(TAG_2_4_6);
			props.setLocalRepo(dir.getPath());
			props.setContentPath("");
			GitResults results = downloader.download(props);
			assertNotNull(results);
			assertTrue("The repository has been created", results.isCreated());			
		}
		catch(Exception e) {
			logger.error("Failed to create local repository", e);
			fail("Expected to create a local repository");
		}
	}

	@Test
	public void testUpdateNewMasterBranchFromTag() {
		try (DeletableDirectory dir = new DeletableDirectory()) {
			GitProperties props = new GitProperties();
			props.setRemote(FRONTEND_GIT1_URL);
			props.setTagName(TAG_2_4_6);
			props.setLocalRepo(dir.getPath());
			props.setContentPath("");
			
			GitResults results = downloader.download(props);
			assertNotNull(results);
			assertTrue("The repository has been created", results.isCreated());
			
			props.setTagName(null);
			results = downloader.download(props);
			assertNotNull(results);
			assertTrue("The repository has been updated", !results.isCreated());
			assertEquals("Expected number of changes", 10, results.getChanges().size());
		}
		catch(Exception e) {
			logger.error("Failed to create local repository", e);
			fail("Expected to create a local repository");
		}
	}

	@Test
	public void testReadLayoutRules() {
		try (DeletableFile cfgFile = new DeletableFile();
			InputStream is = getClass().getResourceAsStream("/layoutRules.json")) {
			byte[] buffer = IOUtils.toByteArray(is);
			FileUtils.writeByteArrayToFile(cfgFile.getTmpFile(), buffer);
			List<LayoutRule> rules = downloader.getLayoutRules(cfgFile.getTmpFile());
			assertEquals("Expected number of rules", 3, rules.size());
			assertEquals("Assert name", "content", rules.get(0).getName());
			assertEquals("Assert inclusion pattern", "content[\\x2F|\\x5C].*", rules.get(0).getInclusionPattern());
		}
		catch(Exception e) {
			logger.error("Failed to test", e);
			fail("Expected to read the layout rules");			
		}
	}
}
