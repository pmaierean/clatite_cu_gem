/**
 * 
 */
package org.maiereni.imaging.common.stl.io;

import java.io.InputStream;

import junit.framework.TestCase;

import org.maiereni.imaging.common.stl.vo.Triangle;
import org.maiereni.imaging.common.stl.vo.Vertex;

/**
 * Tests the functionality of the AsciiSTLReader
 * @author peter
 *
 */
public class AsciiSTLReaderTest extends TestCase {
	/**
	 * Negative test where the constructor is passed a null
	 * @throws Exception
	 */
	public void testNegative() throws Exception {
		try {
			new AsciiSTLReader(null);
			fail("Unexpected behavior");
		}
		catch(Exception e) {
			assertTrue(true);
		}
	}
	
	/**
	 * Negative test where the constructor is passed a null
	 * @throws Exception
	 */
	public void testRead1() throws Exception {
		InputStream is = null;
		try {
			is = getClass().getResourceAsStream("/model1.stl");
			assertNotNull(is);
			AsciiSTLReader reader = new AsciiSTLReader(is);
			assertEquals("cube_corner", reader.getName());
			Triangle tr = reader.nextTriangle();
			assertNotNull(tr);
			assertTrue(tr.getNormalX() == 0f);
			assertTrue(tr.getNormalY() == -1.0f);
			assertTrue(tr.getNormalZ() == 1.0f);
			Vertex[] vertexes = tr.getVertexes();
			assertEquals("vertex 0.0 0.0 0.0", vertexes[0].toString());
			assertEquals("vertex 2.0 0.0 0.0", vertexes[1].toString());
			assertEquals("vertex 0.0 0.0 2.0", vertexes[2].toString());
			assertNull(reader.nextTriangle());
		}
		finally {
			if (is != null)
				try {
					is.close();
				}
				catch(Exception e){}
		}
	}
	
	/**
	 * Negative test where the constructor is passed a null
	 * @throws Exception
	 */
	public void testRead5() throws Exception {
		InputStream is = null;
		try {
			is = getClass().getResourceAsStream("/model5.stl");
			assertNotNull(is);
			AsciiSTLReader reader = new AsciiSTLReader(is);
			assertEquals("cube_corner", reader.getName());
			assertNotNull(reader.nextTriangle());
			assertNotNull(reader.nextTriangle());
			assertNotNull(reader.nextTriangle());
			assertNotNull(reader.nextTriangle());
			assertNotNull(reader.nextTriangle());
		}
		finally {
			if (is != null)
				try {
					is.close();
				}
				catch(Exception e){}
		}
	}
}
