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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for the Bouncy Castle wraping encryptor
 * @author Petre Maierean
 *
 */
public class BouncyCastleEncryptorImplTest {
	private static final Logger logger = LoggerFactory.getLogger(BouncyCastleEncryptorImplTest.class);
	private BouncyCastleEncryptorImpl encryptor;
	
	@Before
	public void setUp() throws Exception {
		encryptor = TestEncryptorProvider.load();
	}
	
	private static final String TESTING_STRING = "# This is a testing string\r\nadmin.password=testme";
	@Test
	public void testEncryptData() {
		try {
			byte[] encrypted = encryptor.encryptData(TESTING_STRING.getBytes());
			assertNotNull(encrypted);
		}
		catch(Exception e) {
			logger.error("Failed to encrypt the data", e);
			fail("Failure");
		}
	}

	/**
	 * Test method for {@link com.maiereni.host.web.util.impl.BouncyCastleEncryptorImpl#decryptData(byte[])}.
	 */
	@Test
	public void testDecryptData() {
		try {
			byte[] encrypted = encryptor.encryptData(TESTING_STRING.getBytes());
			assertNotNull(encrypted);
			byte[] decrypt = encryptor.decryptData(encrypted);
			String s = new String(decrypt);
			assertEquals("Matching strings", TESTING_STRING, s);
		}
		catch(Exception e) {
			logger.error("Failed to encrypt the data", e);
			fail("Failure");
		}
	}

}
