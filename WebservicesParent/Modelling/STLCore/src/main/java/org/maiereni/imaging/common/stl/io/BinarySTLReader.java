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
import java.io.InputStream;

import org.maiereni.imaging.common.stl.util.STLUtil;
import org.maiereni.imaging.common.stl.vo.Triangle;
import org.maiereni.imaging.common.stl.vo.Vertex;

/**
 * A binary STL stream reader
 * 
 * @author Petre Maierean
 *
 */
public class BinarySTLReader extends STLReader {
	private long size;
	private int nr;
	/**
	 * Constructor of the reader
	 * @param is
	 */
	public BinarySTLReader(final InputStream is) throws IOException {
		super(is);
		initialize();
	}
	
	/**
	 * Gets the next Triangle from the stream
	 * @return a triangle
	 */
	public Triangle nextTriangle() throws IOException {
		if (is == null) 
			throw new IOException("There's no input stream for this reader");
		byte[] buffer = new byte[50];
		
		int size = is.read(buffer);
		if (size == -1)
			return null;
		if (size != 50)
			throw new IOException("Invalid STL stream. Could not read next triangle");
		Triangle triangle = new Triangle("tr" + nr++);
		triangle.setNormalX(STLUtil.arr2double(buffer, 0));
		triangle.setNormalY(STLUtil.arr2double(buffer, 4));
		triangle.setNormalZ(STLUtil.arr2double(buffer, 8));
		triangle.setVertexes(new Vertex[3]);
		for (int i = 0, ii = 12; i<3; i++) {
			Vertex crt = new Vertex(triangle);
			triangle.getVertexes()[i] = crt;
			for (int j = 0; j < 3; j++, ii += 4) {
				switch(j) {
				case 0:
					crt.setX(STLUtil.arr2double(buffer, ii));
				break;
				case 1:
					crt.setY(STLUtil.arr2double(buffer, ii));
				break;
				case 2:
					crt.setZ(STLUtil.arr2double(buffer, ii));
				break;
				}
			}
		}
		
		return triangle;
	}
	
	/**
	 * Get the number of triangles in the stream
	 * @return
	 */
	public long getSize() {
		return size;
	}
			
	private void initialize() throws IOException {
		if (is == null) 
			throw new IOException("There's no input stream for this reader");
		byte[] buffer = new byte[80];
		if (is.read(buffer) < 80)
			throw new IOException("Could not read name from the STL file");
		int i = 79;
		for(;i>=0;i--) {
			if (!(buffer[i] == 0 || buffer[i] == 0x20))
				break;
		}
		name = new String(buffer, 0, i + 1);
		buffer = new byte[4];
		if (is.read(buffer) < 4)
			throw new IOException("Could not read length from the STL file");
		size = STLUtil.getInt(buffer);
	}
}
