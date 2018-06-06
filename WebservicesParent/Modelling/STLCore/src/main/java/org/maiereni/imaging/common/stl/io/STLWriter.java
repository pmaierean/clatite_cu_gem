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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.maiereni.imaging.common.stl.vo.Model;
import org.maiereni.imaging.common.stl.vo.Triangle;

/**
 * Base class that defines a STL stream writer
 * 
 * @author petre maierean
 *
 */
public abstract class STLWriter implements Closeable {
	protected static final String BLANK = "Stl Writer file";
	protected String name = STLWriter.BLANK;
	protected List<Triangle> triangles;
	
	public STLWriter(String name) {
		this.name = name;
		if (name == null)
			this.name = STLWriter.BLANK;
		this.triangles = new ArrayList<Triangle>();
	}
	
	/**
	 * Write the model
	 * @param model
	 * @throws IOException
	 */
	public void writer(final Model model) throws IOException {
		if (model != null) {
			name = model.getName();
			if (model.getTriangles() != null)
				for(Triangle triangle: model.getTriangles())
					write(triangle);
		}
	}	
	/**
	 * Write a triangle
	 * @param triangle
	 * @throws IOException
	 */
	public void write(final Triangle triangle) throws IOException {
		if (triangle != null)
			if (triangle.getVertexes() == null || triangle.getVertexes().length < 3)
				throw new IOException("Invalid triangle");
			else
				triangles.add(triangle);
		else
			throw new IOException("Null triangle");
	}
}
