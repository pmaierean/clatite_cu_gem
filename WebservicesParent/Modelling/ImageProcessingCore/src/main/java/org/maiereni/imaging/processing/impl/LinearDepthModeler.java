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
package org.maiereni.imaging.processing.impl;

import org.maiereni.imaging.processing.DepthModeler;

/**
 * 
 * @author Petre Maierean
 *
 */
public class LinearDepthModeler implements DepthModeler {
	private float depth;
	
	public LinearDepthModeler() {}
	
	public LinearDepthModeler(float depth) {
		this.depth = depth;
	}
	
	public float getDepth() {
		return depth;
	}
	public void setDepth(float depth) {
		this.depth = depth;
	}
	/**
	 * Calculates depth for a given pixel in the model
	 * @param x the x dimention of the pixel
	 * @param y the y dimention of the pixel
	 * @return the depth
	 */
	public float calculateDepth(int x, int y) {	
		return depth;
	}
	/**
	 * Calculates the origin for the pixel in the model
	 * @param x
	 * @param y
	 * @return
	 */
	public float calculateOrigin(int x, int y) {
		return 0;
	}
	
	public void setImageWidth(float width) {
		
	}
	
	public void setImageHeight(float height) {
		
	}

}
