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
package com.maiereni.host.web.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Petre Maierean
 *
 */
public class ConfigurationProviderImplTest {
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationProviderImplTest.class);
	private ConfigurationProviderImpl configurationProvider;
	private BouncyCastleEncryptorImpl encryptor;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		encryptor = TestEncryptorProvider.load();
		File f = File.createTempFile("test", ".dta");
		configurationProvider = new ConfigurationProviderImpl(encryptor, f.getPath());
	}
	
	@After
	public void tearDown() throws Exception {
		File f = new File(configurationProvider.getConfigFile());
		if (!f.delete())
			f.deleteOnExit();
	}
	
	@Test
	public void testWritingAndReading() {
		try {
			File f = new File(configurationProvider.getConfigFile());
			configurationProvider.setProperty("prop1", "this is a very simple value");
			configurationProvider.setProperty("prop2", "this is the second simple value");
			configurationProvider.setProperty("prop3", "123");
			assertEquals("Expected value for prop1", "this is a very simple value", configurationProvider.getProperty("prop1", null));
			assertEquals("Expected value for prop2", "this is the second simple value", configurationProvider.getProperty("prop2", null));
			assertEquals("Expected value for prop3", "123", configurationProvider.getProperty("prop3", null));
			assertEquals("Expected default value", "def", configurationProvider.getProperty("prop4", "def"));
			assertNull("Expected value null", configurationProvider.getProperty("prop5", null));
			long l = f.length();
			assertEquals("Expected size of the file", "692", Long.toString(l));
		}
		catch(Exception e) {
			logger.error("Failed to test", e);
			fail("Not yet implemented");
		}
	}

}
