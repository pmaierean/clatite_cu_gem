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
package com.maiereni.synchronizer.jcr.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.charset.Charset;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.synchronizer.git.service.bo.FSStatus;
import com.maiereni.util.DeletableDirectory;

/**
 * @author Petre Maierean
 *
 */
public class ContentBuilderTest {
	private static final Logger logger = LoggerFactory.getLogger(ContentBuilderTest.class);
	
	/**
	 * Negative test case, the path to the local branch is null
	 */
	@Test
	public void testNegative1() {
		try {
			ContentBuilder cb = createBuilder(new String[] {});
			cb.buildContentUpdater(null, null, null);
			fail("Unexpected behavior");
		}
		catch(Exception e) {
			assertEquals("The root cannot be blank", e.getMessage());
		}
	}

	/**
	 * Negative test case, the path to the local branch is invalid
	 */
	@Test
	public void testNegative2() {
		try {
			ContentBuilder cb = createBuilder(new String[] {});
			cb.buildContentUpdater(UUID.randomUUID().toString(), null, null);
			fail("Unexpected behavior");
		}
		catch(Exception e) {
			assertEquals("Unexpected request. The rootPath must point ot a directory", e.getMessage());
		}
	}
	
	/**
	 * Negative test case, the path to the local branch is a file
	 */
	@Test
	public void testInvalidPath() {
		try (DeletableDirectory dir = new DeletableDirectory()) {
			ContentBuilder cb = createBuilder(new String[] {});
			File f = new File(dir.getPath(), "some.txt");
			FileUtils.writeStringToFile(f, "This is a simple text", Charset.defaultCharset());
			cb.buildContentUpdater(f.getPath(), null, null);
			fail("Unexpected behavior");
		}
		catch(Exception e) {
			assertEquals("Unexpected request. The rootPath must point ot a directory", e.getMessage());
		}
	}

	/**
	 * Tests the case when there are no new files to be added to the zip
	 */
	@Test
	public void testArchiveNoChange() {
		File arch = null;
		try (DeletableDirectory dir = new DeletableDirectory()) {
			ContentBuilder cb = createBuilder(new String[] {"a/b/c/f1", "a/b/d/f2"});
			String s = createContent(dir, new String[] {"a/b/c/f1", "a/b/d/f2"});
			FSStatus status = cb.buildContentUpdater(s, null, null);
			arch = cb.buildZip(s, status);
			assertNull("Archive is null", arch);
		}
		catch(Exception e) {
			logger.error("Not expected to fail", e);
			fail("Unexpected behavior");
		}
		finally {
			if (arch != null)
				if (!arch.delete())
					arch.deleteOnExit();
		}
	}

	/**
	 * Tests the case when there are two new files to be added to the zip
	 */
	@Test
	public void testArchiveSimple() {
		File arch = null;
		try (DeletableDirectory dir = new DeletableDirectory()) {
			ContentBuilder cb = createBuilder(new String[] {});
			String s = createContent(dir, new String[] {"a/b/c/f1", "a/b/d/f2"});
			FSStatus status = cb.buildContentUpdater(s, null, null);
			arch = cb.buildZip(s, status);
			assertNotNull("Archive is not null", arch);
			assertEquals("Size of the archive", 308L, arch.length());
		}
		catch(Exception e) {
			logger.error("Not expected to fail", e);
			fail("Unexpected behavior");
		}
		finally {
			if (arch != null)
				if (!arch.delete())
					arch.deleteOnExit();
		}
	}

	/**
	 * Tests the case when there are two new files to be added to the zip. There is one matching file that needs to be ignored
	 */
	@Test
	public void testArchiveSomeMatch() {
		File arch = null;
		try (DeletableDirectory dir = new DeletableDirectory()) {
			ContentBuilder cb = createBuilder(new String[] {"a/b/c/f1"});
			String s = createContent(dir, new String[] {"a/b/c/f1", "a/b/d/f2", "a/b/c/f2"});
			FSStatus status = cb.buildContentUpdater(s, null, null);
			arch = cb.buildZip(s, status);
			assertNotNull("Archive is not null", arch);
			assertEquals("Size of the archive", 308L, arch.length());
		}
		catch(Exception e) {
			logger.error("Not expected to fail", e);
			fail("Unexpected behavior");
		}
		finally {
			if (arch != null)
				if (!arch.delete())
					arch.deleteOnExit();
		}
	}
	
	private String createContent(final DeletableDirectory dir, final String[] fs) throws Exception {
		if (fs != null) {
			for(String s: fs) {
				File d = new File(dir.getPath());
				String[] toks = s.split("/");
				if (toks.length > 1) {
					for(int i=0; i<toks.length - 1; i++) {
						d = new File(d, toks[i]);
						if (!d.exists())
							if (!d.mkdir())
								throw new Exception("Could not created dir at " + d.getPath());
					}
				}
				File f = new File(dir.getPath(), s);
				FileUtils.writeStringToFile(f, "This is another very simple content", Charset.defaultCharset());				
			}
		}
		return dir.getPath();
	}

	private ContentBuilder createBuilder(final String[] fs) throws Exception {
		JackrabbitSession session = Mockito.mock(JackrabbitSession.class);
		if (fs != null) {
			for(String f: fs) {
				String[] toks = f.split("/");
				if (toks.length > 1) {
					StringBuffer sb = new StringBuffer();
					for(int i=0; i<toks.length - 1; i++) {
						if (sb.length()>0) {
							sb.append("/");
						}
						sb.append(toks[i]);
						createNode(session, sb.toString(), NodeType.NT_FOLDER);						
					}
				}
				createNode(session, f, NodeType.NT_FILE);											
			}
		}
		return new ContentBuilder(session);
	}

	
	private void createNode(@Nonnull JackrabbitSession session, @Nonnull final String path, @Nonnull final String sNodeType) throws RepositoryException {
		Node ret = Mockito.mock(Node.class);
		NodeType nodeType = Mockito.mock(NodeType.class);
		Mockito.when(nodeType.getName()).thenReturn(sNodeType);
		Mockito.when(ret.getPrimaryNodeType()).thenReturn(nodeType);
		Mockito.when(session.getNode(path)).thenReturn(ret);
	}
}
