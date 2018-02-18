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
package com.maiereni.blpapi.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

/**
 * @author Petre Maierean
 *
 */
public class FileDownloaderTest {
	private static final Log logger = new SystemStreamLog();
/*	
	@Test
	public void testDownloading() {
		try {
			FileDownloader fileDownloader = new FileDownloader();
			File f = fileDownloader.getBLApi();
			assertNotNull("Expected file", f);
			logger.debug("The file is available at " + f.getPath());
			if (!f.delete())
				f.deleteOnExit();
		}
		catch(Exception e) {
			logger.error("Failed to parse", e);
			fail("Invalid behavior");
		}		
	}
*/	
	@Test
	public void testParsing() {
		try {
			FileDownloader fileDownloader = new FileDownloader();
			String sample = getSample();
			String name = fileDownloader.extractFromText(sample);
			assertEquals("Expected result", "blpapi_java_3.11.1.1.tar.gz", name);
		}
		catch(Exception e) {
			logger.error("Failed to parse", e);
			fail("Invalid behavior");
		}
	}
	
	@Test
	public void testCompareVersions() {
		try {
			FileDownloader fileDownloader = new FileDownloader();
			assertTrue("Greater", fileDownloader.compareVersions("3.8.7.8.1", "3.7.11.2.1") > 0);
			assertTrue("Less then", fileDownloader.compareVersions("3.8.7.8.1", "3.9.11.2.1") < 0);
			assertTrue("Equals", fileDownloader.compareVersions("3.8.7", "3.8.7.2.1") == 0);
		}
		catch(Exception e) {
			logger.error("Failed to parse", e);
			fail("Invalid behavior");
		}		
	}

	@Test
	public void testVersion() {
		try {
			FileDownloader fileDownloader = new FileDownloader();
			String sample = "blpapi_java_3.11.1.1";
			String version = fileDownloader.getVersion(sample);
			assertEquals("Expected result", "3.11.1.1", version);
		}
		catch(Exception e) {
			logger.error("Failed to parse", e);
			fail("Invalid behavior");
		}		
	}
	
	private String getSample() throws Exception {
		try (InputStream is = FileDownloaderTest.class.getResourceAsStream("/sample.html")) {
			return IOUtils.toString(is);
		}		
	}
}
