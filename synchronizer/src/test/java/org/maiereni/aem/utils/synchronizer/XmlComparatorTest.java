/**
 * Copyright 2017 Maiereni Software and Consulting Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package org.maiereni.aem.utils.synchronizer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for the XML file comparator
 * @author Petre Maierean
 *
 */
public class XmlComparatorTest {
	private static final ConsoleLog consoleLog = new ConsoleLog();
	private File fTempDir, f1, f2;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		fTempDir = getTempDir();
		f1 = createFromResource("/org/maiereni/aem/utils/synchronizer/test1a.xml");
		f2 = createFromResource("/org/maiereni/aem/utils/synchronizer/test1b.xml");
		consoleLog.debug("Test files have been created at " + fTempDir.getPath());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		if (fTempDir != null && fTempDir.exists()) {
			FileUtils.deleteDirectory(fTempDir);
			consoleLog.debug("Test folder have been deleted from the file system " + fTempDir.getPath());
		}
	}

	@Test
	public void testPlainCompare() {
		try {
			boolean same = new XmlComparator() {
				private ConsoleLog consoleLog = new ConsoleLog();
				@Override
				protected Log getLog() {
					return consoleLog;
				}
			}.isSame(f1, f2);
			assertFalse("Files are not the same", same);
		}
		catch(Exception e) {
			fail("Not yet implemented");
		}
	}
	
	@Test
	public void testCompareWithIgnoresAttributes() {
		try {
			boolean same = new XmlComparator() {
				private ConsoleLog consoleLog = new ConsoleLog();
				private List<String> ignoreAttrs = Arrays.asList(new String[] {"timestamp", "ab"});
				@Override
				protected Log getLog() {
					return consoleLog;
				}
				@Override				
				public boolean ignoreAttribute(final String attrName) {
					return ignoreAttrs.contains(attrName);
				}
			}.isSame(f1, f2);
			assertTrue("Files are the same", same);
		}
		catch(Exception e) {
			fail("Not yet implemented");
		}
	}

	@Test
	public void testCompareWithIgnoresElement() {
		try {
			boolean same = new XmlComparator() {
				@Override
				protected Log getLog() {
					return consoleLog;
				}
				@Override				
				public boolean ignoreTag(final String tagName) {
					return tagName.equals("a");
				}
			}.isSame(f1, f2);
			assertTrue("Files are the same", same);
		}
		catch(Exception e) {
			fail("Not yet implemented");
		}
	}

	
	private File createFromResource(final String resource) throws Exception {
		InputStream is = getClass().getResourceAsStream(resource);
		if (is == null)
			throw new Exception("No such resource found " + resource);
		byte[] buffer = IOUtils.toByteArray(is);
		int ix = resource.lastIndexOf("/");
		File f = new File(fTempDir, resource.substring(ix + 1));
		FileUtils.writeByteArrayToFile(f, buffer);
		return f;
	}
	
	private static final String TEMP = ".tmp";
	private File getTempDir() throws Exception {
		File f = File.createTempFile("unitTest", TEMP);
		String path = f.getPath();
		int i = path.indexOf(TEMP);
		File dir = new File(path.substring(0, i));
		if (!dir.mkdirs())
			throw new Exception("Could not create temp dir at " + dir.getPath());
		if (!f.delete())
			f.deleteOnExit();
		return dir;
	}
}
