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
package com.maiereni.utils.archiving.tar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * @author Petre Maierean
 *
 */
public class TarGzArchivingUtilTest {
	public static final String EXTERNAL_TEST_FILE = "com.maiereni.utils.archiving.tar.test.file";
	@Test
	public void testCompressionNegative() {
		try {
			TarGzArchivingUtil tarUtil = new TarGzArchivingUtil();
			tarUtil.compress(null, null);
			fail("Unexpected behavior");
		}
		catch(Exception e) {
		}
	}
	
	@Test
	public void testCompressionNegative2() {
		try {
			TarGzArchivingUtil tarUtil = new TarGzArchivingUtil();
			tarUtil.compress(new File("/random/" + UUID.randomUUID().toString()), "sample.tar.gz");
			fail("Unexpected behavior");
		}
		catch(Exception e) {
		}
	}

	@Test
	public void testCompressionOfFileGood() throws Exception {
		File testCase = null;
		try {
			testCase = File.createTempFile("simple", "txt");
			FileUtils.writeStringToFile(testCase, "This is a very simple content");
			TarGzArchivingUtil tarUtil = new TarGzArchivingUtil();
			File fTarGz = tarUtil.compress(testCase, "sample.tar.gz");
			assertNotNull("Proper result file", fTarGz);
		}
		finally {
			if (testCase != null)
				if (!testCase.delete())
					testCase.deleteOnExit();
		}
	}

	@Test
	public void testCompressionOfDirGood() throws Exception {
		File testCase = null, fTarGz = null;
		try {
			testCase = createTestDir();
			TarGzArchivingUtil tarUtil = new TarGzArchivingUtil();
			fTarGz = tarUtil.compress(testCase, "sample.tar.gz");
			assertNotNull("Proper result file", fTarGz);
		}
		finally {
			if (testCase != null)
				FileUtils.deleteDirectory(testCase);
			if (fTarGz != null)
				if (!fTarGz.delete())
					fTarGz.deleteOnExit();
		}
	}
	
	@Test
	public void testDecompressionNull() {
		try {
			TarGzArchivingUtil tarUtil = new TarGzArchivingUtil();
			tarUtil.decompress(null);
			fail("Unexpected behavior");
		}
		catch(Exception e) {
		}		
	}
	
	@Test
	public void testDecompressionBogus() {
		try {
			TarGzArchivingUtil tarUtil = new TarGzArchivingUtil();
			tarUtil.decompress(new File("/random/" + UUID.randomUUID().toString()));
			fail("Unexpected behavior");
		}
		catch(Exception e) {
		}		
	}

	@Test
	public void testDecompressionNonArchive() {
		File testCase = null;
		try {
			testCase = File.createTempFile("simple", "txt");
			FileUtils.writeStringToFile(testCase, "This is a very simple content");

			TarGzArchivingUtil tarUtil = new TarGzArchivingUtil();
			tarUtil.decompress(testCase);
			fail("Unexpected behavior");
		}
		catch(Exception e) {
		}
		finally {
			if (testCase != null)
				if (!testCase.delete())
					testCase.deleteOnExit();
		}
	}
	
	@Test
	public void testDecompressionArchive() throws Exception {
		File testCase = null, fTarGz = null, res = null;
		try {
			testCase = createTestDir();
			TarGzArchivingUtil tarUtil = new TarGzArchivingUtil();
			fTarGz = tarUtil.compress(testCase, "sample.tar.gz");
			assertNotNull("Proper result file", fTarGz);

			res = tarUtil.decompress(fTarGz);
			assertNotNull("Expected", res);
			assertEquals("Expected content", "This is a very simple text file 2", readFile(res, "two/sample2.txt"));
		}
		finally {
			if (res != null)
				FileUtils.forceDelete(res);
			if (testCase != null)
				FileUtils.forceDelete(testCase);
			if (fTarGz != null)
				if (!fTarGz.delete())
					fTarGz.deleteOnExit();
		}
	}
	
	@Test
	public void testDecompressionActual() throws Exception {
		String testFile = System.getProperty(EXTERNAL_TEST_FILE);
		if (StringUtils.isNotEmpty(testFile)) {
			File testCase = new File(testFile);
			if (testCase.exists()) {
				File res = null;
				try {
					TarGzArchivingUtil tarUtil = new TarGzArchivingUtil();
					res = tarUtil.decompress(testCase);
					assertNotNull("Expected", res);
					assertTrue("Expected content", res.exists());
				}
				finally {
					if (res != null)
						FileUtils.forceDelete(res);
				}
			}
		}
	}
	
	private String readFile(final File res, final String s) throws Exception {
		File f = new File(res, "two/sample2.txt");
		assertTrue("The file exists", f.exists());
		try (FileInputStream is = new FileInputStream(f)) {
			return IOUtils.toString(is);
		}
	}
	
	private File createTestDir() throws Exception {
		File fDir = createTempDir();
		FileUtils.writeStringToFile(new File(fDir, "sample.txt"), "This is a very simple text file");
		File fDir1 = new File(fDir, "one");
		assertTrue("Create subdirectory", fDir1.mkdirs());
		FileUtils.writeStringToFile(new File(fDir1, "sample.txt"), "This is a very simple text file");
		File fDir2 = new File(fDir, "two");
		assertTrue("Create subdirectory", fDir2.mkdirs());
		FileUtils.writeStringToFile(new File(fDir2, "sample1.txt"), "This is a very simple text file 1");		
		FileUtils.writeStringToFile(new File(fDir2, "sample2.txt"), "This is a very simple text file 2");
		return fDir;
	}
	
	File createTempDir() throws Exception {
		File f = File.createTempFile("arch", ".tmp");
		int ix = f.getPath().lastIndexOf(".");
		if (!f.delete())
			f.deleteOnExit();
		File dir = new File(f.getPath().substring(0, ix));
		if (!dir.mkdirs())
			throw new Exception("Could not create temp directory");
		return dir;
	}
}
