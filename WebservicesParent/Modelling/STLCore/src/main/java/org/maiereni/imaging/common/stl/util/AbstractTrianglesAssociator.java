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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.maiereni.imaging.common.stl.vo.Model;
import org.maiereni.imaging.common.stl.vo.Triangle;
import org.maiereni.imaging.common.stl.vo.TriangleWithAssociations;

/**
 * Associate neighbors to triangles
 * @author Petre Maierean
 *
 */
public abstract class AbstractTrianglesAssociator {
	
	/**
	 * Loads the model with its associations. A triangle is said to be a simple neighbour to an other one
	 * if they have one matching vertex. A triangle is said to be a strong neighbour to an other one if they
	 * have two matching vertexes
	 * 
	 * @param model
	 * @return
	 */
	public Map<String, TriangleWithAssociations> calculateAssociations(final Model model) throws Exception {
		if (model == null)
			throw new Exception("The model cannot be null");
		
		if (model.getTriangles() == null)
			throw new Exception("The model cannot be blank");
		Map<String, TriangleWithAssociations> ret = new HashMap<String, TriangleWithAssociations>();
		Iterator<Triangle> iTriangles = model.getTriangles().iterator();
		for(int i=0;iTriangles.hasNext(); i++) {
			String name = new String("t" + i);
			ret.put(name, new TriangleWithAssociations(iTriangles.next(), name));
		}
		findNeighbours(ret);
		
		return ret;
	}
	
	protected abstract void findNeighbours(final Map<String, TriangleWithAssociations> ret);
}
