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

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;

import org.maiereni.imaging.processing.render.Modeler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modeler that is based on colors
 * @author Petre Maierean
 *
 */
public abstract class ColorFilterModeler implements Modeler {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private boolean[][] map;
	private int lx, ly; 
	
	public ColorFilterModeler(final RenderedImage image, final String colorSelectorFile, int densityX, int densityY) throws Exception {
		this(image, colorSelectorFile != null? new ColorFilter(colorSelectorFile) : ColorFilter.AVOID_WHITE, densityX, densityY);
	}
	
	public ColorFilterModeler(final RenderedImage image, final ColorFilter filter, int densityX, int densityY) throws Exception {
		SampleModel model = image.getSampleModel();
		DataBuffer buffer = image.getData().getDataBuffer();
		int height = model.getHeight(); // get height in pixels
		int width = model.getWidth(); // get weigth in pixels
		logger.debug("The original size is " + height + "," + width);
		ly = height / densityY;
		lx = width / densityX;
		logger.debug("The reduced size is " + ly + "," + lx);
		map = new boolean[lx][ly];
		int count = 0;
		for (int y = 0; y < height; y++) {				
			for (int x = 0; x < width; x++) {
				float[] d = model.getPixel(x, y, (float[])null, buffer);
				if (!filter.filterPixel(d)) {
					int xh = x / densityX;
					int yh = y / densityY;
					if (!map[xh][yh]) {
						map[xh][yh] = true;
						count++;
					}
				}
			}
		}
		logger.debug("A number of " + count + " points have been identified in the input file");
	}

	/**
	 * Get the maximum number of pixels on the Y coordonate
	 * @return
	 */
	public int getHeight() {
		return ly;
	}

	/**
	 * Get the maximum number of pixels on the X coordonate
	 * @return
	 */

	public int getWidth() {
		return lx;
	}

	/**
	 * Verify if the point defined by x and y should be visible on the 
	 * output
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isVisible(int x, int y) {
		return map[x][y];
	}

}
