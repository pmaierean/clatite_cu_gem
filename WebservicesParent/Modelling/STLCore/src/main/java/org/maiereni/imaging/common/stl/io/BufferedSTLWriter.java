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

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.maiereni.imaging.common.stl.util.STLUtil;
import org.maiereni.imaging.common.stl.vo.Triangle;
import org.maiereni.imaging.common.stl.vo.Vertex;

/**
 * Writer class for an STL model
 * @author Petre Maierean
 *
 */
public class BufferedSTLWriter implements Closeable {
	private OutputStream os;
	
	/**
	 * Constructor of the utility class
	 * @param name the name of the STL model
	 * @param outputFile the output file
	 * @param size the size 
	 * @throws Exception
	 */
	public BufferedSTLWriter(final String name, final String outputFile, int size) throws Exception { 
		this(name, new FileOutputStream(outputFile), size);
	}
	
	public BufferedSTLWriter(final String name, final OutputStream os, int size) throws Exception {
		this.os = os;
		byte[] bname = name.getBytes();
		int sz = bname.length;
		os.write(bname, 0, sz < 80? sz : 80);
		for(;sz<80;sz++)
			os.write(0x20);
		os.write(STLUtil.int2array(sz = size));
	}
	/**
	 * Writes a triangle to the model
	 * @param triangle
	 * @throws Exception
	 */
	public void write(final Triangle triangle) throws Exception {
		Vertex[] vertexes = triangle.getVertexes();
		if (vertexes == null || vertexes.length < 3)
			throw new IOException("Invalid vertex ");
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
	
	public void close()  throws IOException {
		if (os != null)
			os.close();
	}
}
