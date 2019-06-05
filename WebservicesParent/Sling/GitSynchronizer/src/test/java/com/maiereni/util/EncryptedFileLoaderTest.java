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
package com.maiereni.util;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * A unit test for Properties file encryption and decryption
 * @author Petre Maierean
 *
 */
public class EncryptedFileLoaderTest {
	private EncryptedFileLoader fileLoader = new EncryptedFileLoader("classpath:/keystore.jks", "simple", "sample", "JKS", "sample");
	
	@Test
	public void testEncryptionDecryption() throws Exception {
		try (DeletableFile certs = new DeletableFile()) {
			Properties props = new Properties();
			props.setProperty("userName", "petre");
			props.setProperty("password", "simple123");
			fileLoader.saveProperties(props, certs.getPath());
			byte[] buffer = FileUtils.readFileToByteArray(certs.getTmpFile());
			assertEquals("The encryption result", 344, buffer.length);
			Properties p = fileLoader.loadProperties(certs.getPath());
			assertEquals("The user name", "petre", p.getProperty("userName"));
			assertEquals("The password", "simple123", p.getProperty("password"));
		}
	}
}
