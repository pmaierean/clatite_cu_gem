/** ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.maiereni.imaging.common.stl.io;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

/**
 * Tests the functionality of the reader utility
 * @author petre maierean
 *
 */
public class BinarySTLReaderTest extends TestCase {
	/**
	 * Tests the case when the constructor is passed a new stream
	 */
	public void testBadConstructorCall() {
		try {
			new BinarySTLReader(null);
			fail("Unexpected behavior");
		}
		catch(Exception e) {
			assertTrue(true);
		}
	}
	/**
	 * Tests the utility when passed an invalid input
	 */
	public void testInvalidStream() {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(new byte[] {0x01, 0x02, 0x03});
			new BinarySTLReader(is);
			fail("Unexpected behavior");
		}
		catch(Exception e) {
			assertTrue(true);
		}		
	}
	/**
	 * Tests the utility when passed a proper model
	 */
	public void testRightDecode() throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(BinarySTLWriterTest.RESULT);
		BinarySTLReader reader = new BinarySTLReader(is);
		assertEquals(BinarySTLWriterTest.MODEL_NAME, reader.getName());
		assertTrue(reader.getSize() == 3);
		assertEquals(BinarySTLWriterTest.T1,reader.nextTriangle());
		assertEquals(BinarySTLWriterTest.T2,reader.nextTriangle());
		assertEquals(BinarySTLWriterTest.T3,reader.nextTriangle());
	}
}
