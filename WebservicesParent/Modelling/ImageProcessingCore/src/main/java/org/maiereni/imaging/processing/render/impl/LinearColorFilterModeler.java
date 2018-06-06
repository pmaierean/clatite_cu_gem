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
 * Color based filter that returns a constant for depth
 * @author petre maierean
 *
 */
public class LinearColorFilterModeler extends ColorFilterModeler {
	private float originalPosition;
	/**
	 * 
	 * @param image
	 * @param colorFilterFile
	 * @param densityX
	 * @param densityY
	 * @param originalPosition
	 * @throws Exception
	 */
	public LinearColorFilterModeler(RenderedImage image, String colorFilterFile,
			int densityX, int densityY, float originalPosition) throws Exception {
		super(image, colorFilterFile, densityX, densityY);
		this.originalPosition = originalPosition;
	}

	/**
	 * @param image
	 * @param filter
	 * @param densityX
	 * @param densityY
	 * @throws Exception
	 */
	public LinearColorFilterModeler(RenderedImage image, ColorFilter filter,
			int densityX, int densityY, float originalPosition) throws Exception {
		super(image, filter, densityX, densityY);
		this.originalPosition = originalPosition;
	}

	/**
	 * Verify if the point defined by x and y should be visible on the 
	 * output
	 * @param x
	 * @param y
	 * @return
	 */	public float calculatePosition(int x, int y) {
		return originalPosition;
	}

}
