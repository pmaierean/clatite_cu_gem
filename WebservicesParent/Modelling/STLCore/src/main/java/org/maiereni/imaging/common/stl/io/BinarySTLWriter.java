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

import java.io.IOException;
import java.io.OutputStream;

import org.maiereni.imaging.common.stl.util.STLUtil;
import org.maiereni.imaging.common.stl.vo.Triangle;
import org.maiereni.imaging.common.stl.vo.Vertex;


/**
 * A utility class to writes an STL model to a binary stream
 * @author Petre Maierean
 *
 */
public class BinarySTLWriter extends STLWriter {
	private OutputStream os;
	/**
	 * Constructor of the writer
	 * @param name the name of the STL stream
	 * @param os the output stream to write to
	 */
	public BinarySTLWriter(String name, OutputStream os) throws IOException  {
		super(name);
		if (os == null)
			throw new IOException("No output stream");
		this.os = os;
	}
	/**
	 * Write the STL content to the output stream
	 */
	public void close() throws IOException {
		if (os == null)
			throw new IOException("No output stream to write to");
		if (name == null)
			name = STLWriter.BLANK;
		byte[] bname = name.getBytes();
		int sz = bname.length;
		os.write(bname, 0, sz < 80? sz : 80);
		for(;sz<80;sz++)
			os.write(0x20);
		os.write(STLUtil.int2array(sz = triangles.size()));
		for(int i=0; i<sz; i++) {
			Triangle triangle = triangles.get(i);
			Vertex[] vertexes = triangle.getVertexes();
			if (vertexes == null || vertexes.length < 3)
				throw new IOException("Invalid vertex " + i);
			os.write(STLUtil.double2arr(triangle.getNormalX()));
			os.write(STLUtil.double2arr(triangle.getNormalY()));
			os.write(STLUtil.double2arr(triangle.getNormalZ()));
			for(int j=0; j<3; j++) {
				os.write(STLUtil.double2arr(vertexes[j].getX()));
				os.write(STLUtil.double2arr(vertexes[j].getY()));
				os.write(STLUtil.double2arr(vertexes[j].getZ()));
			}
			os.write(0x20);
			os.write(0x20);
		}
		os.close();
	}

}
