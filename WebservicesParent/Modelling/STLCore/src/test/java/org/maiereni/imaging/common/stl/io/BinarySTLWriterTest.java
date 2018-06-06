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

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import org.maiereni.imaging.common.stl.vo.Triangle;
import org.maiereni.imaging.common.stl.vo.Vertex;

/**
 * Simple unit test for the BinarySTLWriter
 * @author petre maierean
 *
 */
public class BinarySTLWriterTest extends TestCase {
	/**
	 * Tests the case when the utility is not passed an output stream in the constructor
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public void testNegativeNoStream() throws Exception {
		try {
			new BinarySTLWriter(null, null);
			fail("Unexpected behavior");
		}
		catch(Exception e) {
			assertTrue(true);
		}
	}
	protected static final String MODEL_NAME = "Sample";
	protected static final Vertex V11 =  new Vertex(null, 0.1f, 0f, 0.1f);
	protected static final Vertex V12 =  new Vertex(null, 0.1f, 0.1f, 0.1f);
	protected static final Vertex V13 =  new Vertex(null, 0.1f, 0.1f, 0f);
	protected static final Vertex V21 =  new Vertex(null, 0f, 0f, 0.1f);
	protected static final Vertex V22 =  new Vertex(null, 0f, 0.1f, 0.1f);
	protected static final Vertex V23 =  new Vertex(null, 0.1f, 0.1f, 0.1f);
	protected static final Vertex V31 =  new Vertex(null, 0.1f, 0.1f, 0f);
	protected static final Vertex V32 =  new Vertex(null, 0.1f, 0f, 0f);
	protected static final Vertex V33 =  new Vertex(null, 0f, 0.1f, 0f);
	protected static final Triangle T1, T2, T3;
	static {
		Triangle t1 = null, t2 = null, t3 = null;
		try {
			t1 = new Triangle("test1", new Vertex[] {BinarySTLWriterTest.V11, BinarySTLWriterTest.V12, BinarySTLWriterTest.V13});
			t2 = new Triangle("test2", new Vertex[] {BinarySTLWriterTest.V21, BinarySTLWriterTest.V22, BinarySTLWriterTest.V23});
			t3 = new Triangle("test3", new Vertex[] {BinarySTLWriterTest.V31, BinarySTLWriterTest.V32, BinarySTLWriterTest.V33});
		}
		catch(Exception e) {
			throw new ExceptionInInitializerError(e);
		}
		T1 = t1;
		T2 = t2;
		T3 = t3;
	}
	protected static final byte[] RESULT = new byte[] { (byte)0x53, (byte)0x61, (byte)0x6D, (byte)0x70, (byte)0x6C, (byte)0x65, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x20, (byte)0x20, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0x20, (byte)0x20, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xCD, (byte)0xCC, (byte)0xCC, (byte)0x3D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x20, (byte)0x20};
	
	/**
	 * Test writing a very simple model with the writer
	 * @throws Exception
	 */
	public void testWriteVertex() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		BinarySTLWriter writer = new BinarySTLWriter(BinarySTLWriterTest.MODEL_NAME, os);
		writer.write(T1);
		writer.write(T2);
		writer.write(T3);
		writer.close();
		assertTrue(Arrays.equals(os.toByteArray(), BinarySTLWriterTest.RESULT));
	}
	
}
