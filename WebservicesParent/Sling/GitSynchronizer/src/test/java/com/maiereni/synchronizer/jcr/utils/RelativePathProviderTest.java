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

import static org.junit.Assert.*;

import org.junit.Test;
import java.io.File;
import com.maiereni.util.DeletableDirectory;

/**
 * @author Petre Maierean
 *
 */
public class RelativePathProviderTest {

	@Test
	public void testNegative1() {
		RelativePathProvider provider = new RelativePathProvider(null);
		assertNull("", provider.getRelativePath(null));
	}
	
	@Test
	public void testNegative2() {
		RelativePathProvider provider = new RelativePathProvider(new File("/"));
		assertNull("Expected to not calculate", provider.getRelativePath(null));		
	}

	@Test
	public void testNegative3() {
		try (DeletableDirectory dir = new DeletableDirectory()) {
			RelativePathProvider provider = new RelativePathProvider(dir.getDir());
			assertNull("Expected to not calculate", provider.getRelativePath(new File(DeletableDirectory.TMP)));
		}
		catch(Exception e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}
	private static final String SUBDIR = "test/me/123";
	@Test
	public void testPositive() {
		try (DeletableDirectory dir = new DeletableDirectory()) {
			File newDir = dir.makeSubdirectory(SUBDIR);
			RelativePathProvider provider = new RelativePathProvider(dir.getDir());
			assertEquals("Expected to not calculate", SUBDIR, provider.getRelativePath(newDir));
		}
		catch(Exception e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}
}
