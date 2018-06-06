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

import java.util.List;

/**
 * A triangle is said to be a simple neighbour to an other one if they have one matching vertex. A triangle is said to be a 
 * strong neighbour to an other one if they have two matching vertexes
 * 
 * @author Petre Maierean
 *
 */
public class TriangleWithAssociations {
	private String id;
	private Triangle triangle;
	private List<String> simpleNeighbours, strongNeighbours;

	public TriangleWithAssociations(Triangle triangle, String id) {
		this.triangle = triangle;
		this.id = id;
	}
	
	public Triangle getTriangle() {
		return triangle;
	}
	public void setTriangle(Triangle triangle) {
		this.triangle = triangle;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setStrongNeighbours(List<String> strongNeighbours) {
		this.strongNeighbours = strongNeighbours;
	}
	public List<String> getStrongNeighbours() {
		return strongNeighbours;
	}
	public void setSimpleNeighbours(List<String> simpleNeighbours) {
		this.simpleNeighbours = simpleNeighbours;
	}
	public List<String> getSimpleNeighbours() {
		return simpleNeighbours;
	}
}
