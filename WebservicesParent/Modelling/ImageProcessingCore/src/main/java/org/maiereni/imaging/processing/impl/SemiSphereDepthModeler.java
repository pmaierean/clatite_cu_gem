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
 * @author Petre Maierean
 *
 */
public class SemiSphereDepthModeler implements DepthModeler {
	private float imageWidth, imageHeigth, radius, depth;
	
	public SemiSphereDepthModeler() {}
	
	public SemiSphereDepthModeler(float radius, float depth) {
		this.radius = radius;
		this.depth = depth;
	}
	
	public float getImageHeigth() {
		return imageHeigth;
	}

	public void setImageHeigth(float imageHeigth) {
		this.imageHeigth = imageHeigth;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getImageWidth() {
		return imageWidth;
	}

	/**
	 * Calculates the depth of the pixel
	 * @param x
	 * @param y
	 * @return
	 */
	public float calculateDepth(int x, int y) {
		return calculateOrigin(x, y) + depth;
	}

	/**
	 * Calculates the model origin point for the pixel
	 * @param x
	 * @param y
	 * @return
	 */
	public float calculateOrigin(int x, int y) {
		double dX = (imageWidth - 2 * x)/imageWidth;
		double dY = (imageHeigth - 2* y)/imageHeigth;		
		double o = Math.sqrt(Math.pow(radius, 2) - Math.pow(dX, 2) - Math.pow(dY, 2));
		return (float)o;
	}

	public void setImageWidth(float width) {
		this.imageWidth = width;
	}
	
	public void setImageHeight(float height) {
		this.imageHeigth = height;
	}

	public float getDepth() {
		return depth;
	}

	public void setDepth(float depth) {
		this.depth = depth;
	}

}
