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
package org.maiereni.imaging.processing.render.impl;

import java.awt.image.RenderedImage;

/**
 * A modeler that calculates the depth of a model based on a sphere
 * @author Petre Maierean
 *
 */
public class DemiSphereColorFilterModeler extends ColorFilterModeler {
	private float originalPosition, radius;
	
	/**
	 * Constructor
	 * @param image
	 * @param filter
	 * @param densityX
	 * @param densityY
	 * @throws Exception
	 */
	public DemiSphereColorFilterModeler(RenderedImage image, String colorsFile, 
			int densityX, int densityY, float originalPosition, float radius) throws Exception {
		super(image, colorsFile, densityX, densityY);
		this.originalPosition = originalPosition;
		this.radius = radius;		
	}
	
	/**
	 * Constructor
	 * @param image
	 * @param filter
	 * @param densityX
	 * @param densityY
	 * @throws Exception
	 */
	public DemiSphereColorFilterModeler(RenderedImage image,
			ColorFilter filter, int densityX, int densityY, float originalPosition, float radius) throws Exception {
		super(image, filter, densityX, densityY);
		this.originalPosition = originalPosition;
		this.radius = radius;
	}

	/**
	 * Verify if the point defined by x and y should be visible on the 
	 * output
	 * @param x
	 * @param y
	 * @return
	 */
	public float calculatePosition(int x, int y) {
		int imageWidth = getWidth();
		int imageHeigth = this.getHeight();
		double dX = (imageWidth - 2 * x)/imageWidth;
		double dY = (imageHeigth - 2 * y)/imageHeigth;		
		double position = Math.sqrt(Math.pow(radius, 2) - Math.pow(dX, 2) - Math.pow(dY, 2));
		return (float) position + originalPosition; 
	}

}
