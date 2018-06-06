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

/**
 * A bean that represents a vertex in the model
 * 
 * @author Petre Maierean
 *
 */
public class Vertex {
	private float x,y,z;
	private Triangle triangle;
	private String id;
	
	public Vertex(Triangle triangle) {
		this.triangle = triangle;
	}
	
	public Vertex(Triangle triangle, Vertex v) {
		this(triangle);
		if (v != null) {
			this.x = v.x;
			this.y = v.y;
			this.z = v.z;
		}
	}
	
	public Vertex(Triangle triangle, float x, float y, float z){
		this(triangle);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
	
	private static final String VERTEX = "vertex ";
	
	public String toString() {
		StringBuffer sb = new StringBuffer(Vertex.VERTEX);
		sb.append(x).append(" ").append(y).append(" ").append(z);
		return sb.toString();
	}
	
	public boolean equals(Object o) {
		if (o != null && o instanceof Vertex) {
			Vertex v = (Vertex)o;
			return (v.x == this.x) && (v.y == this.y) && (v.z == this.z);
		}
		return false;
	}

	public void setTriangle(Triangle triangle) {
		this.triangle = triangle;
	}

	public Triangle getTriangle() {
		return triangle;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
