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
package org.maiereni.imaging.common.stl.util;

import java.util.ArrayList;
import java.util.List;

import org.maiereni.imaging.common.stl.vo.Hexahedron;
import org.maiereni.imaging.common.stl.vo.Triangle;
import org.maiereni.imaging.common.stl.vo.Vertex;

/**
 * Helper class that calculates the joint point of triangles within a Space
 * @author Petre Maierean
 *
 */
public class TriangleJointUtils {
	private Hexahedron hexahedron;
	
	public TriangleJointUtils(final Hexahedron hexahedron) {
		this.hexahedron = hexahedron;
	}
	
	public TriangleJointUtils(float x0, float y0, float z0, float x1, float y1, float z1) {
		this.hexahedron = new Hexahedron();
		this.hexahedron.setX0(x0);
		this.hexahedron.setX1(x1);
		this.hexahedron.setY0(y0);
		this.hexahedron.setY1(y1);
		this.hexahedron.setZ0(z0);
		this.hexahedron.setZ1(z1);
	}
	/**
	 * Creates an array of triangles by splitting the one provided as an argument at the
	 * intersection at the hexahedron 
	 * 
	 * @param triangle
	 * @return
	 */
	public Triangle[] makeContained(final Triangle triangle) throws Exception {
		if (triangle == null || triangle.getVertexes() == null)
			return null; // Invalid
		List<Vertex> incl = new ArrayList<Vertex>(), excl = new ArrayList<Vertex>();
		for(int i=0; i<triangle.getVertexes().length; i++) {
			Vertex vertex = triangle.getVertexes()[i];
			if (!isContained(vertex)) 
				excl.add(vertex);
			else
				incl.add(vertex);
		}
		if (excl.size() == 0)
			return new Triangle[] {triangle}; // All included
		else if (excl.size() == triangle.getVertexes().length)
			return null; // All excluded
		
		List<Triangle> triangles = new ArrayList<Triangle>();
		PointsCalculator pointCalculator = new PointsCalculator();
		pointCalculator.setExcl(excl);
		pointCalculator.setIncl(incl);
		
		return triangles.toArray(new Triangle[triangles.size()]);
	}
	
	/**
	 * Verifies if the triangle is contained or not within the Hexahedron
	 * @param triangle
	 * @return -2 for invalid compare; -1 not included; 0 - completely included; 
	 *          1 - one vertex not included; 2 - two vertexes not included; 
	 *          3 - three vertexes not included
	 */
	public int isIncluded(final Triangle triangle) {
		if (triangle == null || triangle.getVertexes() == null)
			return -2; // Invalid

		int degree = 0;
		for(int i=0; i<triangle.getVertexes().length; i++) {
			if (!isContained(triangle.getVertexes()[i]))
				degree++;
		}
		if (degree == triangle.getVertexes().length)
			return -1; // Excluded
		
		return degree == 0 ? 0 : degree;
	}
	/**
	 * Verifies if the vertex is contained or not within the Hexahedron
	 * @param triangle
	 * @return
	 */
	public boolean isContained(final Vertex vertex) {
		if (vertex == null)
			return false;
		if (vertex.getZ() >= hexahedron.getZ0() && vertex.getZ() <= hexahedron.getZ1())
			if (vertex.getY() >= hexahedron.getY0() && vertex.getY() <= hexahedron.getY1())
				if (vertex.getX() >= hexahedron.getX0() && vertex.getX() <= hexahedron.getX1())
					return true;

		return false;
	}
	
	
}
