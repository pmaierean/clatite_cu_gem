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

import java.util.List;

import org.maiereni.imaging.common.stl.vo.Vertex;

/**
 * Helper class to calculate the points at the intersection of the
 * triangle with the mesh containing space
 * @author Petre Maierean
 *
 */
class PointsCalculator {
	private List<Vertex> incl, excl;

	public List<Vertex> getIncl() {
		return incl;
	}

	public void setIncl(List<Vertex> incl) {
		this.incl = incl;
	}

	public List<Vertex> getExcl() {
		return excl;
	}

	public void setExcl(List<Vertex> excl) {
		this.excl = excl;
	}
	
	
}
