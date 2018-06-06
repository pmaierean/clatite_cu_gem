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

import org.maiereni.imaging.processing.render.Modeler;
import org.maiereni.imaging.processing.render.ModelerFactory;

/**
 * Creates color based filter modelers
 * @author Petre Maierean
 *
 */
public class ColorFilterModelerFactory implements ModelerFactory {
	private String colorFilterFile;
	private float originalPosition, radius;
	private int densityX = 1, densityY = 1;
	
	public String getColorFilterFile() {
		return colorFilterFile;
	}

	public void setColorFilterFile(String colorFilterFile) {
		this.colorFilterFile = colorFilterFile;
	}

	public int getDensityX() {
		return densityX;
	}

	public void setDensityX(int densityX) {
		this.densityX = densityX;
	}

	public int getDensityY() {
		return densityY;
	}

	public void setDensityY(int densityY) {
		this.densityY = densityY;
	}

	public float getOriginalPosition() {
		return originalPosition;
	}

	public void setOriginalPosition(float originalPosition) {
		this.originalPosition = originalPosition;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public ColorFilterModelerFactory(String colorFilterFile) {
		this.colorFilterFile = colorFilterFile;
	}
	
	public Modeler buildModeler(RenderedImage image) throws Exception{
		if (radius >= 2)
			return new DemiSphereColorFilterModeler(image, colorFilterFile, densityX, densityY, originalPosition, radius) ;
		return new LinearColorFilterModeler(image, colorFilterFile, densityX, densityY, originalPosition);
	}

}
