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
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.maiereni.imaging.common.stl.util.STLConstants;
import org.maiereni.imaging.common.stl.vo.Triangle;
import org.maiereni.imaging.common.stl.vo.Vertex;

/**
 * A Reader class for ASCII STL streams
 * @author Petre Maierean
 *
 */
public class AsciiSTLReader extends STLReader {
	private LineNumberReader reader;
	private int nr = 0, line = 0;
	
	public AsciiSTLReader(final InputStream is) throws IOException {
		super(is);
		reader = new LineNumberReader(new InputStreamReader(is));
		String fl = readLine();
		if (!(fl.startsWith(STLConstants.HEADER_KEY)))
			throw new IOException("Invalid header: " + fl);
	
		name = fl.substring(STLConstants.HEADER_KEY.length());
	}

	/**
	 * Read the next triangle from the stream
	 */
	@Override
	public Triangle nextTriangle() throws IOException {
		String s = readLine();
		int ix = s.indexOf(STLConstants.FACET_KEY);
		if (ix > 0) {
			String tok = s.substring(ix + STLConstants.FACET_KEY.length());
			String[] arr = tok.split(" ");
			if (arr.length < 3)
				throw new IOException("Invalid string at line " + line);
			Triangle tr = new Triangle(Integer.toString(nr++), Float.parseFloat(arr[0].trim()), Float.parseFloat(arr[1].trim()), Float.parseFloat(arr[2].trim()));
			s = readLine();
			if (!(s.endsWith(STLConstants.OUTER_LOOP_KEY)))
				throw new IOException("Invalid key at line " + line + " expected '" + STLConstants.OUTER_LOOP_KEY + "'");				
			
			Vertex[] vertexes = new Vertex[3];
			vertexes[0] = readNextVertex();
			vertexes[1] = readNextVertex();
			vertexes[2] = readNextVertex();
			tr.setVertexes(vertexes);
			s = readLine();
			if (!(s.endsWith(STLConstants.END_LOOP_KEY)))
				throw new IOException("Invalid key at line " + line + " expected '" + STLConstants.END_LOOP_KEY + "'");
			s = readLine();
			if (!(s.endsWith(STLConstants.END_FACET_KEY)))
				throw new IOException("Invalid key at line " + line + " expected '" + STLConstants.END_FACET_KEY + "'");
			return tr;
		}
		return null;
	}
	
	private Vertex readNextVertex() throws IOException {
		String s = readLine();
		int ix = s.indexOf(STLConstants.VERTEX_KEY);
		if (ix < 0)
			throw new IOException("Expected vertex key at line " + line);				
		String[] arr = s.substring(ix + STLConstants.VERTEX_KEY.length()).split(" ");
		if (arr.length < 3)
			throw new IOException("Invalid vertex string at line " + line);
		return new Vertex(null, Float.parseFloat(arr[0].trim()), Float.parseFloat(arr[1].trim()), Float.parseFloat(arr[2].trim()));
	}
	
	private String readLine() throws IOException {
		String s = reader.readLine();
		line++;
		return s;
	}
}
