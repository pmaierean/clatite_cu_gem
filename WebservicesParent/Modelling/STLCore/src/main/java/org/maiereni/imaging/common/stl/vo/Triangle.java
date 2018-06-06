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
package org.maiereni.imaging.common.stl.vo;

import java.io.IOException;
import java.util.Arrays;

/**
 * Bean that represents a triangle in the model
 * @author Petre Maierean
 *
 */
public class Triangle {
	private Vertex[] vertexes;
	private float normalX, normalY, normalZ;
	private boolean used;
	private String id;
	public Triangle(String id) {
		this.id = id;
	}
	
	public Triangle(String id, float normalX, float normalY, float normalZ) throws IOException {
		this(id, normalX, normalY, normalZ, null);
	}

	public Triangle(String id, Vertex[] vertexes) throws IOException {
		this(id, 0f,0f,0f,vertexes);
	}
	
	public Triangle(String id, Triangle triangle) {
		this(id);
		if (triangle != null) {
			this.normalX = triangle.normalX;
			this.normalY = triangle.normalY;
			this.normalZ = triangle.normalZ;
			if (triangle.vertexes != null) {
				vertexes = new Vertex[triangle.vertexes.length];
				for(int i=0; i<vertexes.length; i++) {
					vertexes[i] = new Vertex(this, triangle.vertexes[i]);
				}
			}
		}
	}
	
	public Triangle(String id, float normalX, float normalY, float normalZ, Vertex[] vertexes)  throws IOException{
		this(id);
		this.normalX = normalX;
		this.normalY = normalY;
		this.normalZ = normalZ;
		if (vertexes != null)
			if (vertexes.length != 3)
				throw new IOException("Invalid request. A triangle must have 3 vertexes");
		this.vertexes = vertexes;
		if (vertexes != null)
			for(int i=0; i<vertexes.length; i++)
				vertexes[i].setTriangle(this);
	}

	public float getNormalX() {
		return normalX;
	}

	public void setNormalX(float normalX) {
		this.normalX = normalX;
	}

	public float getNormalY() {
		return normalY;
	}

	public void setNormalY(float normalY) {
		this.normalY = normalY;
	}

	public float getNormalZ() {
		return normalZ;
	}

	public void setNormalZ(float normalZ) {
		this.normalZ = normalZ;
	}

	public Vertex[] getVertexes() {
		return vertexes;
	}

	public void setVertexes(Vertex[] vertexes) {
		this.vertexes = vertexes;
		if (vertexes != null)
			for(Vertex v: vertexes)
				if (v != null)
					v.setTriangle(this);
	}
	
	private static final String FACET = "facet normal ";
	private static final String NEXT = "\r\n   ";
	private static final String OUTER_LOOP = "\r\n outer loop" + NEXT;
	private static final String END_TAG = "\r\n endloop\r\nendfacet\r\n";
	public String toString() {
		if (vertexes == null)
			return null;
		StringBuffer sb = new StringBuffer(Triangle.FACET);
		sb.append(normalX).append(" ").append(normalY).append(" ").append(normalZ).append(Triangle.OUTER_LOOP);
		sb.append(vertexes[0].toString()).append(Triangle.NEXT);
		sb.append(vertexes[1].toString()).append(Triangle.NEXT);
		sb.append(vertexes[2].toString());
		sb.append(Triangle.END_TAG);
		return sb.toString();
	}
	
	public boolean equals(Object o) {
		if (o != null && o instanceof Triangle) {
			Triangle t = (Triangle)o;
			if (t.normalX == normalX && t.normalY == normalY && t.normalZ == normalZ)
				if (t.vertexes != null && vertexes != null)
					return Arrays.deepEquals(t.vertexes, vertexes);
		}
		return false;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public boolean isUsed() {
		return used;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
